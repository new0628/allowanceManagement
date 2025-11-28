//
// Created by 이상원 on 2025. 11. 27..
//

#include <stdio.h>
#include <string.h>
#include <jni.h>
#include <android//log.h>
#include "sqlite3.h"

#define LOG_TAG "NativeDb"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

// 전역 DB
static sqlite3* db = NULL;

// 예외 발생시
static void throwException(JNIEnv* env, const char* message, int errorCode) {
    jclass exclass = (*env) -> FindClass(env, "java/lang/RuntimeException");
    if (exclass == NULL) {
        LOGE("Failed to find RuntimeException class");
        return;
    }
    char buffer[512];
    snprintf(buffer, sizeof(buffer), "%s (sqlite code = %d", message, errorCode);
    (*env) -> ThrowNew(env, exclass, buffer);
}

JNIEXPORT void JNICALL
Java_com_example_allowancemanagement_db_NativeDb_debugPrintAll(
        JNIEnv *env,
        jclass clazz
) {
    if (db == NULL) {
        LOGE("DB가 열려있지 않음");
        return;
    }

    const char* sql = "SELECT id, type, date, description, amount FROM ACTIVITY_DB;";
    sqlite3_stmt* stmt = NULL;

    int rc = sqlite3_prepare_v2(db, sql, -1, &stmt, NULL);
    if (rc != SQLITE_OK) {
        LOGE("SELECT 준비 실패: %d", rc);
        return;
    }

    LOGD("==== DB 내용 출력 시작 ====");

    while ((rc = sqlite3_step(stmt)) == SQLITE_ROW) {
        int id = sqlite3_column_int(stmt, 0);
        int type = sqlite3_column_int(stmt, 1);
        const unsigned char* date = sqlite3_column_text(stmt, 2);
        const unsigned char* desc = sqlite3_column_text(stmt, 3);
        int amount = sqlite3_column_int(stmt, 4);

        LOGD("ROW -> id=%d, type=%d, date=%s, desc=%s, amount=%d",
             id, type, date, desc, amount);
    }

    LOGD("==== DB 내용 출력 끝 ====");

    sqlite3_finalize(stmt);
}

// DB 열기
JNIEXPORT void JNICALL
Java_com_example_allowancemanagement_db_NativeDb_open (
        JNIEnv *env,
        jclass clazz,
        jstring jpath
        ) {
    if (db != NULL) {
        LOGD("DB 열려있음");
        return;
    }

    const char* path = (*env) -> GetStringUTFChars(env, jpath, 0);
    LOGD("DB 열기 : %s", path);

    int rc = sqlite3_open(path, &db);
    (*env) -> ReleaseStringUTFChars(env, jpath, path);

    if (rc != SQLITE_OK) {
        LOGE("sqlite3 열기 실패 : %d", rc);
        throwException(env, "db를 열 수 없음", rc);
        db = NULL;
        return;
    }

    // 테이블 생성
    const char* createSql =
            "CREATE TABLE IF NOT EXISTS ACTIVITY_DB ("
            "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            "type INTEGER NOT NULL, "
            "date TEXT NOT NULL, "
            "description TEXT NOT NULL, "
            "amount INTEGER NOT NULL"
            ");";

    rc = sqlite3_exec(db, createSql, NULL, NULL, NULL);
    if (rc != SQLITE_OK) {
        LOGE("테이블 생성 실패 : %d", rc);
        throwException(env, "ACTIVITY_DB 테이블 생성 실패", rc);
    }
    LOGD("DB 열림 및 테이블 준비 완료");
}

// DB 닫기
JNIEXPORT void JNICALL
Java_com_example_allowancemanagement_db_NativeDb_close (
        JNIEnv *env,
        jclass clazz
        ) {
    if (db != NULL) {
        int rc = sqlite3_close(db);
        if (rc != SQLITE_OK) {
            LOGE("sqlite3_close 실패 : %d", rc);
            throwException(env, "데이터베이스를 닫는 중 오류 발생", rc);
            return;
        }
        db = NULL;
        LOGD("DB 닫힘");
    }
}

// 저장된 금액
JNIEXPORT jint JNICALL
Java_com_example_allowancemanagement_db_NativeDb_getBalance(
        JNIEnv* env,
        jclass clazz
        ) {
    if (db == NULL) {
        throwException(env, "DB가 열려있지않음", -1);
        return 0;
    }

    const char* sql =
            "SELECT "
            " (SELECT IFNULL(SUM(amount), 0) FROM ACTIVITY_DB WHERE type = 1) - "
            " (SELECT IFNULL(SUM(amount), 0) FROM ACTIVITY_DB WHERE type = 0);";

    sqlite3_stmt* stmt = NULL;
    int rc = sqlite3_prepare_v2(db, sql, -1, &stmt, NULL);
    if (rc != SQLITE_OK) {
        throwException(env, "잔액 계산 실패", rc);
        return 0;
    }

    rc = sqlite3_step(stmt);
    if(rc != SQLITE_ROW) {
        sqlite3_finalize(stmt);
        throwException(env, "잔액 조회 실패", rc);
        return 0;
    }

    int balance = sqlite3_column_int(stmt, 0);
    sqlite3_finalize(stmt);
    return balance;
}

// SELECT
JNIEXPORT jobject JNICALL
Java_com_example_allowancemanagement_db_NativeDb_loadByMonthAndQuery(
        JNIEnv *env,
        jclass clazz,
        jint jYear,
        jint jMonth,
        jstring jQuery
        ) {
    if (db == NULL) {
        throwException(env, "DB가 열려있지 않음", -1);
        return NULL;
    }

    int year = (int) jYear;
    int month = (int) jMonth;

    const char* query = (*env) -> GetStringUTFChars(env, jQuery, 0);
    int hasQuery = (query != NULL && query[0] != '\0');

    char ym[8]; //"YYYY-MM" + '\0'
    snprintf(ym, sizeof(ym), "%04d-%02d", year, month);

    const char* sql =
            "SELECT id, type, date, description, amount "
            "FROM ACTIVITY_DB "
            "WHERE strftime('%Y-%m', date) = ? "
            "AND description LIKE ? "
            "ORDER BY date ASC, id ASC;";

    sqlite3_stmt* stmt = NULL;
    int rc = sqlite3_prepare_v2(db, sql, -1, &stmt, NULL);
    if (rc != SQLITE_OK) {
        LOGE("SELECT 준비 실패 : %d", rc);
        (*env) -> ReleaseStringUTFChars(env, jQuery, query);
        throwException(env, "SELECT 준비 중 오류", rc);
        return NULL;
    }

    // 년-월
    sqlite3_bind_text(stmt, 1, ym, -1, SQLITE_TRANSIENT);

    // like 패턴
    char likeBuf[256];
    if (hasQuery) {
        snprintf(likeBuf, sizeof(likeBuf), "%%%s%%", query);
    }
    else {
        snprintf(likeBuf, sizeof(likeBuf), "%%");
    }
    sqlite3_bind_text(stmt, 2, likeBuf, -1, SQLITE_TRANSIENT);

    jclass listClass = (*env) -> FindClass(env, "java/util/ArrayList");
    jmethodID listCtor = (*env) -> GetMethodID(env, listClass, "<init>", "()V");
    jmethodID listAdd = (*env) -> GetMethodID(env, listClass, "add", "(Ljava/lang/Object;)Z");
    jobject listObj = (*env) -> NewObject(env, listClass, listCtor);

    // ActivityUI 클래스 찾기
    jclass uiClass = (*env) -> FindClass(env, "com/example/allowancemanagement/model/ActivityUI");
    if (uiClass == NULL) {
        LOGE("ACTIVITY UI 클래스를 찾을 수 없음");
        sqlite3_finalize(stmt);
        (*env) -> ReleaseStringUTFChars(env, jQuery, query);
        throwException(env, "ACTIVITY UI 클래스를 찾지 못함", -1);
        return NULL;
    }

    // ActivityUI(Int id, Int type, String date, String description, Int amount)
    jmethodID uiCtor = (*env) -> GetMethodID(env, uiClass, "<init>", "(IILjava/lang/String;Ljava/lang/String;I)V");
    if (uiCtor == NULL) {
        LOGE("ACTIVITY UI 생성자를 못찾음");
        sqlite3_finalize(stmt);
        (*env) -> ReleaseStringUTFChars(env, jQuery, query);
        throwException(env, "ACTIVITY UI 생성자를 찾지 못함", -1);
        return NULL;
    }

    while ((rc = sqlite3_step(stmt)) == SQLITE_ROW) {
        int id = sqlite3_column_int(stmt, 0);
        int type = sqlite3_column_int(stmt, 1);
        const unsigned char* cDate = sqlite3_column_text(stmt, 2);
        const unsigned char* cDesc = sqlite3_column_text(stmt, 3);
        int amount = sqlite3_column_int(stmt, 4);

        jstring jDate = (*env)->NewStringUTF(env, (const char*) cDate);
        jstring jDesc = (*env)->NewStringUTF(env, (const char*) cDesc);

        jobject uiObj = (*env) -> NewObject(env, uiClass, uiCtor, id, type, jDate, jDesc, amount);

        (*env) -> CallBooleanMethod(env, listObj, listAdd, uiObj);
        (*env) -> DeleteLocalRef(env, jDate);
        (*env) -> DeleteLocalRef(env, jDesc);
        (*env) -> DeleteLocalRef(env, uiObj);
    }

    if (rc != SQLITE_DONE) {
        LOGE("SELECT 실행중 오류 : %d", rc);
        sqlite3_finalize(stmt);
        (*env) -> ReleaseStringUTFChars(env, jQuery, query);
        throwException(env, "SELECT 실행 실패", rc);
        return NULL;
    }

    sqlite3_finalize(stmt);
    (*env) -> ReleaseStringUTFChars(env, jQuery, query);

    LOGD("SELECT 완료");
    return listObj;
}

// INSERT
JNIEXPORT jlong JNICALL
Java_com_example_allowancemanagement_db_NativeDb_insertActivity(
        JNIEnv *env,
        jclass clazz,
        jint jType,
        jstring jDate,
        jstring jDescription,
        jint jAmount
        ) {
    if (db == NULL) {
        throwException(env, "DB가 열려있지않음", -1);
        return -1;
    }

    const char* date = (*env) -> GetStringUTFChars(env, jDate, 0);
    const char* desc = (*env) -> GetStringUTFChars(env, jDescription, 0);
    int type = (int) jType;
    int amount = (int) jAmount;

    const char* sql =
            "INSERT INTO ACTIVITY_DB (type, date, description, amount) "
            "VALUES (?, ?, ?, ?);";

    sqlite3_stmt* stmt = NULL;
    int rc = sqlite3_prepare_v2(db, sql, -1, &stmt, NULL);

    if (rc != SQLITE_OK) {
        LOGE("삽입 실패 : %d", rc);
        (*env) -> ReleaseStringUTFChars(env, jDate, date);
        (*env) -> ReleaseStringUTFChars(env, jDescription, desc);
        throwException(env, "삽입 중비중 오류", rc);
        return -1;
    }

    sqlite3_bind_int(stmt, 1, type);
    sqlite3_bind_text(stmt, 2, date, -1, SQLITE_TRANSIENT);
    sqlite3_bind_text(stmt, 3, desc, -1, SQLITE_TRANSIENT);
    sqlite3_bind_int(stmt, 4, amount);

    rc = sqlite3_step(stmt);
    if (rc != SQLITE_DONE) {
        LOGE("삽입 실행 실패 : %d", rc);
        sqlite3_finalize(stmt);
        (*env) -> ReleaseStringUTFChars(env, jDate, date);
        (*env) -> ReleaseStringUTFChars(env, jDescription, desc);
        throwException(env, "데이터 저장 실패", rc);
        return -1;
    }

    sqlite3_finalize(stmt);
    (*env) -> ReleaseStringUTFChars(env, jDate, date);
    (*env) -> ReleaseStringUTFChars(env, jDescription, desc);

    sqlite3_int64 lastId = sqlite3_last_insert_rowid(db);
    LOGD("저장 완료 -> type = %d, date = %s, desc = %s, amount = %d", type, date, desc, amount);
    return (jlong) lastId;
}

// UPDATE
JNIEXPORT void JNICALL
Java_com_example_allowancemanagement_db_NativeDb_updateActivity(
        JNIEnv *env,
        jobject clazz,
        jint jId,
        jstring jDate,
        jstring jDescription,
        jint jAmount) {
    if (db == NULL) {
        throwException(env, "DB가 열려있지 않음", -1);
        return;
    }

    int id = (int) jId;
    const char* date = (*env) -> GetStringUTFChars(env, jDate, 0);
    const char* desc = (*env) -> GetStringUTFChars(env, jDescription, 0);
    int amount = (int) jAmount;

    const char* sql =
            "UPDATE ACTIVITY_DB "
            "SET date = ?, description = ?, amount = ? "
            "WHERE id = ?;";

    sqlite3_stmt* stmt = NULL;
    int rc = sqlite3_prepare_v2(db, sql, -1, &stmt, NULL);
    if (rc != SQLITE_OK) {
        LOGE("업데이트 준비 실패 : %d", rc);
        (*env) -> ReleaseStringUTFChars(env, jDate, date);
        (*env) -> ReleaseStringUTFChars(env, jDescription, desc);
        throwException(env, "업데이트 준비 중 오류", rc);
        return;
    }

    sqlite3_bind_text(stmt, 1, date, -1, SQLITE_TRANSIENT);
    sqlite3_bind_text(stmt, 2, desc, -1, SQLITE_TRANSIENT);
    sqlite3_bind_int(stmt, 3, amount);
    sqlite3_bind_int(stmt, 4, id);

    rc = sqlite3_step(stmt);
    if (rc != SQLITE_DONE) {
        LOGE("업데이트 실행 실패 : %d", rc);
        sqlite3_finalize(stmt);
        (*env) -> ReleaseStringUTFChars(env, jDate, date);
        (*env) -> ReleaseStringUTFChars(env, jDescription, desc);
        throwException(env, "데이터 수정 실패", rc);
        return;
    }

    sqlite3_finalize(stmt);
    (*env) -> ReleaseStringUTFChars(env, jDate, date);
    (*env) -> ReleaseStringUTFChars(env, jDescription, desc);

    LOGD("UPDATE 완료 -> id=%d, date=%s, desc=%s, amount=%d", id, date, desc, amount);
}

// Delete
JNIEXPORT void JNICALL
Java_com_example_allowancemanagement_db_NativeDb_deleteActivity (
        JNIEnv *env,
        jclass clazz,
        jint jId
        ) {
    if (db == NULL) {
        throwException(env, "DB가 열려있지 않음", -1);
        return;
    }

    int id = (int) jId;
    const char* sql = "DELETE FROM ACTIVITY_DB WHERE id = ?;";

    sqlite3_stmt* stmt = NULL;
    int rc = sqlite3_prepare_v2(db, sql, -1, &stmt, NULL);
    if (rc != SQLITE_OK) {
        LOGE("DELETE 준비 실패 : %d", rc);
        throwException(env, "DELETE 준비 중 오류", rc);
        return;
    }

    sqlite3_bind_int(stmt, 1, id);
    rc = sqlite3_step(stmt);
    if (rc != SQLITE_DONE) {
        LOGE("DELETE 실행 실패 : %d", rc);
        sqlite3_finalize(stmt);
        throwException(env, "데이터 삭제 실패", rc);
        return;
    }

    sqlite3_finalize(stmt);
    LOGD("삭제 완료 id = %d", id);
}
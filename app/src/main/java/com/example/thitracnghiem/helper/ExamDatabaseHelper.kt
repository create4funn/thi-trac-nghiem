package com.example.thitracnghiem.helper

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import com.example.thitracnghiem.model.ExamItem
import com.example.thitracnghiem.model.QuestionItem

class ExamDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_EXAMS_TABLE)
        db.execSQL(CREATE_QUESTIONS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Code to upgrade the database schema if necessary
    }

    fun saveExamToSQLite(exam: ExamItem, questions: List<QuestionItem>) {
        val db = writableDatabase

        db.beginTransaction()
        try {
            val examValues = ContentValues().apply {
                put(COLUMN_EXAM_ID, exam.id)
                put(COLUMN_EXAM_NAME, exam.name)
                put(COLUMN_DURATION, exam.duration)
                put(COLUMN_NUM_OF_QUES, exam.numOfQues)
            }
            db.insert(TABLE_EXAMS, null, examValues)

            for (question in questions) {
                val questionValues = ContentValues().apply {
                    put(COLUMN_QUESTION_ID, question.id)
                    put(COLUMN_QUESTION_TEXT, question.text)
                    put(COLUMN_OPTIONS, question.listAnswer.joinToString(","))
                    put(COLUMN_ANSWER, question.correct)
                    put(COLUMN_EXAM_ID_FK, exam.id)
                }
                db.insert(TABLE_QUESTIONS, null, questionValues)
            }

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun getExams(): MutableList<ExamItem> {
        val examList = mutableListOf<ExamItem>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_EXAMS,
            null, null, null, null, null, null
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXAM_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXAM_NAME))
                val duration = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DURATION))
                val numOfQues = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_NUM_OF_QUES))
                val questions = getQuestionsForExam(id)
                examList.add(ExamItem(id, name, duration, numOfQues, questions))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return examList
    }

    fun getQuestionsForExam(examId: String): List<String> {
        val questionIds = mutableListOf<String>()
        val db = readableDatabase

        val cursor = db.query(
            TABLE_QUESTIONS,
            arrayOf(COLUMN_QUESTION_ID),
            "$COLUMN_EXAM_ID_FK = ?",
            arrayOf(examId), null, null, null
        )

        if (cursor.moveToFirst()) {
            do {
                val questionId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_ID))
                questionIds.add(questionId)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return questionIds
    }

    fun getQuestionsByIds(questionIds: List<String>): List<QuestionItem> {
        val questionsList = mutableListOf<QuestionItem>()
        val db = readableDatabase
        val columns = arrayOf(
            COLUMN_QUESTION_ID,
            COLUMN_QUESTION_TEXT,
            COLUMN_OPTIONS,
            COLUMN_ANSWER,
            COLUMN_EXAM_ID_FK
        )
        val selection = "$COLUMN_QUESTION_ID IN (${questionIds.joinToString { "'" + it + "'" }})"
        val sortOrder = "$COLUMN_QUESTION_ID ASC"
        val cursor: Cursor = db.query(TABLE_QUESTIONS, columns, selection, null, null, null, sortOrder)
        while (cursor.moveToNext()) {
            val questionId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_ID))
            val text = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_TEXT))
            val optionsString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OPTIONS))
            val listAnswer = optionsString.split(",")
            val correct = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANSWER))

            val questionItem = QuestionItem(questionId, text, listAnswer, correct)
            questionsList.add(questionItem)
        }
        cursor.close()
        return questionsList
    }

    fun deleteExamById(examId: String) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.delete(
                TABLE_QUESTIONS,
                "$COLUMN_EXAM_ID_FK = ?",
                arrayOf(examId)
            )
            db.delete(
                TABLE_EXAMS,
                "$COLUMN_EXAM_ID = ?",
                arrayOf(examId)
            )
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    companion object {
        private const val DATABASE_NAME = "exams.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_EXAMS = "exams"
        const val COLUMN_EXAM_ID = "id"
        const val COLUMN_EXAM_NAME = "name"
        const val COLUMN_DURATION = "duration"
        const val COLUMN_NUM_OF_QUES = "numOfQues"

        const val TABLE_QUESTIONS = "questions"
        const val COLUMN_QUESTION_ID = "id"
        const val COLUMN_QUESTION_TEXT = "text"
        const val COLUMN_OPTIONS = "options"
        const val COLUMN_ANSWER = "answer"
        const val COLUMN_EXAM_ID_FK = "examId"

        private const val CREATE_EXAMS_TABLE = "CREATE TABLE $TABLE_EXAMS (" +
                "$COLUMN_EXAM_ID TEXT PRIMARY KEY," +
                "$COLUMN_EXAM_NAME TEXT," +
                "$COLUMN_DURATION INTEGER," +
                "$COLUMN_NUM_OF_QUES INTEGER)"

        private const val CREATE_QUESTIONS_TABLE = "CREATE TABLE $TABLE_QUESTIONS (" +
                "$COLUMN_QUESTION_ID TEXT PRIMARY KEY," +
                "$COLUMN_QUESTION_TEXT TEXT," +
                "$COLUMN_OPTIONS TEXT," +
                "$COLUMN_ANSWER TEXT," +
                "$COLUMN_EXAM_ID_FK TEXT," +
                "FOREIGN KEY($COLUMN_EXAM_ID_FK) REFERENCES $TABLE_EXAMS($COLUMN_EXAM_ID))"
    }
}

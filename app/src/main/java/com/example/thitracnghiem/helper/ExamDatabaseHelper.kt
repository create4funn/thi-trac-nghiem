package com.example.thitracnghiem.helper

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import com.example.thitracnghiem.model.Answer
import com.example.thitracnghiem.model.ExamItem
import com.example.thitracnghiem.model.QuestionItem

class ExamDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        // Create new tables
        db.execSQL(CREATE_EXAMS_TABLE)
        db.execSQL(CREATE_QUESTIONS_TABLE)
        db.execSQL(CREATE_ANSWERS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop old tables
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EXAMS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_QUESTIONS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ANSWERS")
        // Create new tables
        onCreate(db)
    }


    fun saveExamToSQLite(exam: ExamItem, questions: List<QuestionItem>) {
        val db = writableDatabase
        db.beginTransaction()

        try {
            // Insert into exams table
            val examValues = ContentValues().apply {
                put(COLUMN_EXAM_NAME, exam.exam_name)
                put(COLUMN_DURATION, exam.duration)
                put(COLUMN_NUM_OF_QUES, exam.numOfQues)
            }
            val examId = db.insert(TABLE_EXAMS, null, examValues)

            // Insert each question into questions table
            for (question in questions) {
                val questionValues = ContentValues().apply {
                    put(COLUMN_QUESTION_TEXT, question.question_text)
                    put(COLUMN_EXAM_ID_FK, examId)
                }
                val questionId = db.insert(TABLE_QUESTIONS, null, questionValues)

                // Insert each answer into answers table
                for (answer in question.answers) {
                    val answerValues = ContentValues().apply {
                        put(COLUMN_ANSWER_TEXT, answer.answer_text)
                        put(COLUMN_IS_CORRECT, answer.is_correct)
                        put(COLUMN_QUESTION_ID_FK, questionId)
                    }
                    db.insert(TABLE_ANSWERS, null, answerValues)
                }
            }

            // If all insertions are successful, commit the transaction
            db.setTransactionSuccessful()

        } catch (e: Exception) {
            // Handle exceptions
            e.printStackTrace()
        } finally {
            db.endTransaction()
        }
    }


    fun getExams(): MutableList<ExamItem> {
        val examsList = mutableListOf<ExamItem>()
        val db = readableDatabase

        // Query to get all exams from the exams table
        val cursor = db.query(
            TABLE_EXAMS,  // Table name
            null,         // Select all columns
            null,         // No WHERE clause
            null,         // No selection arguments
            null,         // No group by
            null,         // No having
            null          // No order by
        )

        // Check if the cursor contains data
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Extract data from cursor
                val examId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EXAM_ID))
                val examName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXAM_NAME))
                val duration = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DURATION))
                val numOfQues = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NUM_OF_QUES))

                // Create ExamItem object
                val examItem = ExamItem(exam_id = examId, exam_name = examName, duration = duration, numOfQues = numOfQues, null, null)

                // Add ExamItem to the list
                examsList.add(examItem)
            } while (cursor.moveToNext())
        }

        cursor?.close()
        return examsList
    }


    fun getQuestionsForExam(examId: Int): List<QuestionItem> {
        val questionsList = mutableListOf<QuestionItem>()
        val db = readableDatabase

        // Query to get questions based on examId
        val cursor = db.query(
            TABLE_QUESTIONS,  // Table name
            null,             // Select all columns
            "$COLUMN_EXAM_ID_FK = ?",  // WHERE clause
            arrayOf(examId.toString()),  // Selection arguments
            null,             // No group by
            null,             // No having
            null              // No order by
        )

        // Check if the cursor contains data
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Extract data from cursor
                val questionId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_ID))
                val questionText = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_TEXT))

                // Get answers for this question
                val answers = getAnswersForQuestion(questionId)

                // Create QuestionItem object
                val questionItem = QuestionItem(question_id = questionId, question_text = questionText, answers = answers)

                // Add QuestionItem to the list
                questionsList.add(questionItem)
            } while (cursor.moveToNext())
        }

        cursor?.close()
        return questionsList.toList()
    }

    fun getAnswersForQuestion(questionId: Int): MutableList<Answer> {
        val answersList = mutableListOf<Answer>()
        val db = readableDatabase

        // Query to get answers based on questionId
        val cursor = db.query(
            TABLE_ANSWERS,  // Table name
            null,           // Select all columns
            "$COLUMN_QUESTION_ID_FK = ?",  // WHERE clause
            arrayOf(questionId.toString()),  // Selection arguments
            null,           // No group by
            null,           // No having
            null            // No order by
        )

        // Check if the cursor contains data
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Extract data from cursor
                val answerId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ANSWER_ID))
                val answerText = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANSWER_TEXT))
                val isCorrect = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_CORRECT))

                // Create Answer object
                val answer = Answer(answer_id = answerId, answer_text = answerText, is_correct = isCorrect)

                // Add Answer to the list
                answersList.add(answer)
            } while (cursor.moveToNext())
        }

        cursor?.close()
        return answersList
    }



    fun deleteExamById(examId: Int) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            // Lấy tất cả các question_id liên quan đến examId
            val cursor = db.query(
                TABLE_QUESTIONS,
                arrayOf(COLUMN_QUESTION_ID),
                "$COLUMN_EXAM_ID_FK = ?",
                arrayOf(examId.toString()),
                null,
                null,
                null
            )

            // Xóa các đáp án liên quan đến các câu hỏi
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    val questionId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_ID))
                    db.delete(
                        TABLE_ANSWERS,
                        "$COLUMN_QUESTION_ID_FK = ?",
                        arrayOf(questionId)
                    )
                } while (cursor.moveToNext())
            }
            cursor?.close()

            // Xóa các câu hỏi liên quan đến examId
            db.delete(
                TABLE_QUESTIONS,
                "$COLUMN_EXAM_ID_FK = ?",
                arrayOf(examId.toString())
            )

            // Xóa bài kiểm tra từ bảng exams
            db.delete(
                TABLE_EXAMS,
                "$COLUMN_EXAM_ID = ?",
                arrayOf(examId.toString())
            )

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }


    companion object {
        private const val DATABASE_NAME = "exams_new.db"
        private const val DATABASE_VERSION = 2

        const val TABLE_EXAMS = "exams"
        const val COLUMN_EXAM_ID = "exam_id"
        const val COLUMN_EXAM_NAME = "exam_name"
        const val COLUMN_DURATION = "duration"
        const val COLUMN_NUM_OF_QUES = "numOfQues"

        const val TABLE_QUESTIONS = "questions"
        const val COLUMN_QUESTION_ID = "question_id"
        const val COLUMN_QUESTION_TEXT = "question_text"
        const val COLUMN_EXAM_ID_FK = "exam_id"

        const val TABLE_ANSWERS = "answers"
        const val COLUMN_ANSWER_ID = "answer_id"
        const val COLUMN_ANSWER_TEXT = "answer_text"
        const val COLUMN_IS_CORRECT = "is_correct"
        const val COLUMN_QUESTION_ID_FK = "question_id"

        private const val CREATE_EXAMS_TABLE = """
            CREATE TABLE $TABLE_EXAMS (
                $COLUMN_EXAM_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_EXAM_NAME TEXT NOT NULL,
                $COLUMN_DURATION INTEGER NOT NULL,
                $COLUMN_NUM_OF_QUES INTEGER NOT NULL
            )
        """

        private const val CREATE_QUESTIONS_TABLE = """
            CREATE TABLE $TABLE_QUESTIONS (
                $COLUMN_QUESTION_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_QUESTION_TEXT TEXT NOT NULL,
                $COLUMN_EXAM_ID_FK INTEGER NOT NULL,
                FOREIGN KEY($COLUMN_EXAM_ID_FK) REFERENCES $TABLE_EXAMS($COLUMN_EXAM_ID)
                ON DELETE NO ACTION ON UPDATE NO ACTION
            )
        """

        private const val CREATE_ANSWERS_TABLE = """
            CREATE TABLE $TABLE_ANSWERS (
                $COLUMN_ANSWER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ANSWER_TEXT TEXT NOT NULL,
                $COLUMN_IS_CORRECT BOOLEAN NOT NULL,
                $COLUMN_QUESTION_ID_FK INTEGER NOT NULL,
                FOREIGN KEY($COLUMN_QUESTION_ID_FK) REFERENCES $TABLE_QUESTIONS($COLUMN_QUESTION_ID)
                ON DELETE NO ACTION ON UPDATE NO ACTION
            )
        """
    }
}

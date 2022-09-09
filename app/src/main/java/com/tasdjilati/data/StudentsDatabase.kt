package com.tasdjilati.data

import android.content.Context
import androidx.room.*
import com.tasdjilati.data.converters.Converter
import com.tasdjilati.data.dao.SplashImageDao
import com.tasdjilati.data.dao.StudentDao
import com.tasdjilati.data.dao.StudentEnterAttendanceDao
import com.tasdjilati.data.dao.StudentExitAttendanceDao
import com.tasdjilati.data.entities.SplashImage
import com.tasdjilati.data.entities.Student
import com.tasdjilati.data.entities.StudentEnterAttendance
import com.tasdjilati.data.entities.StudentExitAttendance

@Database(entities = [Student::class , StudentEnterAttendance::class , StudentExitAttendance::class , SplashImage::class], version = 1, exportSchema = false)
@TypeConverters(Converter::class)

abstract class StudentsDatabase : RoomDatabase(){

    abstract fun getStudentDao(): StudentDao
    abstract fun getStudentEnterAttendanceDao(): StudentEnterAttendanceDao
    abstract fun getStudentExitAttendanceDao(): StudentExitAttendanceDao
    abstract fun getImageDao(): SplashImageDao

    companion object {
        @Volatile
        private var INSTANCE: StudentsDatabase? = null

        fun getDatabase(context: Context): StudentsDatabase {
                return INSTANCE ?: synchronized(this) {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        StudentsDatabase::class.java,
                        "database"
                    ).build()
                    INSTANCE = instance
                    instance
                }
            }
        }
    }
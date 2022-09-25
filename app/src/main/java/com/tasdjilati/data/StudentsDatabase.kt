package com.tasdjilati.data

import android.content.Context
import androidx.room.*
import com.tasdjilati.data.converters.Converter
import com.tasdjilati.data.dao.*
import com.tasdjilati.data.entities.*

@Database(entities = [Student::class , StudentEnterAttendance::class , StudentExitAttendance::class , SplashImage::class , StudentTransport::class], version = 1, exportSchema = false)
@TypeConverters(Converter::class)

abstract class StudentsDatabase : RoomDatabase(){

    abstract fun getStudentDao(): StudentDao
    abstract fun getStudentEnterAttendanceDao(): StudentEnterAttendanceDao
    abstract fun getStudentExitAttendanceDao(): StudentExitAttendanceDao
    abstract fun getImageDao(): SplashImageDao
    abstract fun getStudentTransportDao(): StudentTransportDao

    companion object {
        @Volatile
        private var INSTANCE: StudentsDatabase? = null

        fun getDatabase(context: Context): StudentsDatabase {
                return INSTANCE ?: synchronized(this) {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        StudentsDatabase::class.java,
                        "allStudentsDb"
                    ).build()
                    INSTANCE = instance
                    instance
                }
            }
        }
    }
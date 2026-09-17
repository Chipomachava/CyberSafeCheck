package com.example.cybersafecheck.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RiskAnswerEntity::class, AssessmentEntity::class], version = 1, exportSchema = false)
abstract class CyberSafeDatabase : RoomDatabase() {
    abstract fun riskDao(): RiskDao
    abstract fun assessmentDao(): AssessmentDao

    companion object {
        @Volatile
        private var INSTANCE: CyberSafeDatabase? = null

        fun getDatabase(context: Context): CyberSafeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CyberSafeDatabase::class.java,
                    "cybersafe_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
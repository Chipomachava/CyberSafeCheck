package com.example.cybersafecheck.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [RiskAnswerEntity::class, AssessmentEntity::class, ReportEntity::class],
    version = 2,
    exportSchema = false
)
abstract class CyberSafeDatabase : RoomDatabase() {
    abstract fun riskDao(): RiskDao
    abstract fun assessmentDao(): AssessmentDao
    abstract fun reportDao(): ReportDao

    companion object {
        @Volatile
        private var INSTANCE: CyberSafeDatabase? = null

        fun getDatabase(context: Context): CyberSafeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CyberSafeDatabase::class.java,
                    "cybersafe_database"
                ).addMigrations(migration_1_2).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

val migration_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `reports` (`id` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `description` TEXT NOT NULL, `photoUri` TEXT, PRIMARY KEY(`id`))"
        )
    }
}
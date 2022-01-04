package com.bd.deliverytiger.app.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bd.deliverytiger.app.api.model.district.DistrictData
import com.bd.deliverytiger.app.database.dao.DistrictDao
import com.bd.deliverytiger.app.database.dao.NotificationDao
import com.bd.deliverytiger.app.fcm.FCMData

@Database(entities = [FCMData::class, DistrictData::class], version = 2, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract fun notificationDao(): NotificationDao
    abstract fun districtDao(): DistrictDao

    companion object {

        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE district_table ADD COLUMN nextDayAlertMessage TEXT")
            }
        }
        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "delivery_tiger_db"
        )
            .allowMainThreadQueries()
            .addMigrations(MIGRATION_1_2)
            .build()
    }


}
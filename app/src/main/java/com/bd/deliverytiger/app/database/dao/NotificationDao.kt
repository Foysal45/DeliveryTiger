package com.bd.deliverytiger.app.database.dao

import androidx.room.*
import com.bd.deliverytiger.app.fcm.FCMData
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(model: FCMData): Long

    @Query("SELECT * FROM notification_table ORDER BY uid DESC")
    suspend fun getAllNotification(): List<FCMData>

    @Query("SELECT * FROM notification_table ORDER BY uid DESC")
    fun getAllNotificationFlow(): Flow<List<FCMData>>

    @Query("SELECT * FROM notification_table WHERE uid = :id")
    suspend fun getNotificationById(id: Int): FCMData

    @Update
    suspend fun updateNotification(model: FCMData): Int

    @Query("DELETE FROM notification_table WHERE uid = :id")
    suspend fun deleteNotificationById(id: Int): Int

    @Query("DELETE FROM notification_table")
    suspend fun deleteAllNotification(): Int

}
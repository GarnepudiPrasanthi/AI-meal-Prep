package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.UserWeightProfile
import com.example.data.model.WeightEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightDao {
    @Query("SELECT * FROM weight_profile WHERE id = 1 LIMIT 1")
    fun getProfile(): Flow<UserWeightProfile?>

    @Query("SELECT * FROM weight_profile WHERE id = 1 LIMIT 1")
    suspend fun getProfileSync(): UserWeightProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(profile: UserWeightProfile)

    @Query("SELECT * FROM weight_entries ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<WeightEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: WeightEntry): Long

    @Delete
    suspend fun deleteEntry(entry: WeightEntry)
}

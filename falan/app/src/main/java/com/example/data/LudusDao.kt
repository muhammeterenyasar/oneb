package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface GladiatorDao {
    @Query("SELECT * FROM gladiators ORDER BY id ASC")
    fun getAllGladiators(): Flow<List<GladiatorEntity>>

    @Query("SELECT * FROM gladiators WHERE id = :id")
    suspend fun getGladiatorById(id: Long): GladiatorEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGladiator(gladiator: GladiatorEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllGladiators(gladiators: List<GladiatorEntity>)

    @Update
    suspend fun updateGladiator(gladiator: GladiatorEntity)

    @Delete
    suspend fun deleteGladiator(gladiator: GladiatorEntity)

    @Query("DELETE FROM gladiators WHERE id = :id")
    suspend fun deleteGladiatorById(id: Long)
}

@Dao
interface LudusStateDao {
    @Query("SELECT * FROM ludus_state WHERE id = 1")
    fun getLudusState(): Flow<LudusStateEntity?>

    @Query("SELECT * FROM ludus_state WHERE id = 1")
    suspend fun getLudusStateDirect(): LudusStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateState(state: LudusStateEntity)
}

@Dao
interface MatchLogDao {
    @Query("SELECT * FROM match_logs ORDER BY id DESC LIMIT 30")
    fun getRecentMatchLogs(): Flow<List<MatchLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatchLog(log: MatchLogEntity)
}

@Dao
interface TeacherDao {
    @Query("SELECT * FROM teachers ORDER BY id ASC")
    fun getAllTeachers(): Flow<List<TeacherEntity>>

    @Query("SELECT * FROM teachers ORDER BY id ASC")
    suspend fun getAllTeachersDirect(): List<TeacherEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeacher(teacher: TeacherEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTeachers(teachers: List<TeacherEntity>)

    @Delete
    suspend fun deleteTeacher(teacher: TeacherEntity)

    @Query("DELETE FROM teachers WHERE id = :id")
    suspend fun deleteTeacherById(id: Long)
}


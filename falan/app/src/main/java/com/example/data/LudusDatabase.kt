package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        GladiatorEntity::class,
        LudusStateEntity::class,
        MatchLogEntity::class,
        TeacherEntity::class
    ],
    version = 9,
    exportSchema = false
)
abstract class LudusDatabase : RoomDatabase() {
    abstract fun gladiatorDao(): GladiatorDao
    abstract fun ludusStateDao(): LudusStateDao
    abstract fun matchLogDao(): MatchLogDao
    abstract fun teacherDao(): TeacherDao

    companion object {
        @Volatile
        private var INSTANCE: LudusDatabase? = null

        fun getDatabase(context: Context): LudusDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LudusDatabase::class.java,
                    "ludus_magnus_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}

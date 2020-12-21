package asd.android.movieslist.services.database


import androidx.room.Database
import androidx.room.RoomDatabase
import asd.android.movieslist.services.database.*


@Database(entities = [FavoriteMovie::class], version = 1)
abstract class Db : RoomDatabase() {
    abstract fun dao(): DatabaseDTO
}
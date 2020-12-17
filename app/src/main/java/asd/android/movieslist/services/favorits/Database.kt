package asd.android.movieslist.services.favorits

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FavoriteMovie::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun dao(): DatabaseDTO
}
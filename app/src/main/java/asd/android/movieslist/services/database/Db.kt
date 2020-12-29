package asd.android.movieslist.services.database


import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [FavoriteMovie::class], version = 1)
abstract class Db : RoomDatabase() {
    abstract fun dao(): DTO
}
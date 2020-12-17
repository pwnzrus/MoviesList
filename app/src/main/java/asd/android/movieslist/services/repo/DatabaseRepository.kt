package asd.android.movieslist.services.repo

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import asd.android.movieslist.services.favorits.Database
import asd.android.movieslist.services.favorits.FavoriteMovie
import java.util.concurrent.Executors

class DatabaseRepository(val context: Context) {
    private val executor = Executors.newSingleThreadExecutor()

    private var database = Room.databaseBuilder(context, Database::class.java,"database").build()

    fun getFavoriteList(): LiveData<MutableList<FavoriteMovie>> = database.dao().getFavoriteList()

    fun updateFavoriteList(favoriteList:MutableList<FavoriteMovie>){
        executor.execute{
            database.dao().updateFavoriteList(favoriteList)
        }
    }

    fun dalete(favoriteMovie: FavoriteMovie){
        executor.execute {
            database.dao().delete(favoriteMovie)
        }
    }

}

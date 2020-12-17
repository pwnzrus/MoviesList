package asd.android.movieslist.services.repo

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Delete
import androidx.room.Room
import asd.android.movieslist.services.dto.MovieListResponse
import asd.android.movieslist.services.favorits.Database
import asd.android.movieslist.services.favorits.FavoriteMovie
import asd.android.movieslist.services.network.MovieApi
import asd.android.movieslist.services.network.Retrofit
import io.reactivex.Single
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class NetworkRepository() {
    val retrofit by lazy {
        Retrofit.retrofit
    }
    fun getAllMovies():Single<MovieListResponse>{
        return retrofit.create(MovieApi::class.java).getAllMovies()
    }

    fun searchMovies(_query:String):Single<MovieListResponse>{
        return retrofit.create(MovieApi::class.java).searchQuery(query = _query)
    }




}
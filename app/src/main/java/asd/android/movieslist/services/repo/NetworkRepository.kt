package asd.android.movieslist.services.repo

import asd.android.movieslist.services.models.MovieListResponse
import asd.android.movieslist.services.network.MovieApi
import asd.android.movieslist.services.network.Retrofit
import io.reactivex.Single

class NetworkRepository {

    val retrofit by lazy {
        Retrofit.retrofit
    }

    fun getAllMovies(): Single<MovieListResponse> {
        return retrofit.create(MovieApi::class.java).getAllMovies()
    }

    fun searchMovies(_query: String): Single<MovieListResponse> {
        return retrofit.create(MovieApi::class.java).searchQuery(query = _query)
    }
}
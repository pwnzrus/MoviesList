package asd.android.movieslist.services.network


import asd.android.movieslist.services.models.MovieListResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApi {

    @GET("discover/movie")
    fun getAllMovies(): Single<MovieListResponse>

    @GET("search/movie")
    fun searchQuery(
        @Query("query") query: String
    ): Single<MovieListResponse>

}
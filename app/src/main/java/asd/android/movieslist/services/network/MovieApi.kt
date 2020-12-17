package asd.android.movieslist.services.network


import asd.android.movieslist.services.dto.MovieListResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApi {

    @GET("discover/movie")
    fun getAllMovies(@Query("api_key") apiKey: String = "0c88a20f74279d297c32de4d8fbdfbeb"):Single<MovieListResponse>

    @GET("search/movie")
    fun searchQuery(@Query("api_key") apiKey: String = "0c88a20f74279d297c32de4d8fbdfbeb", @Query("query") query: String):Single<MovieListResponse>

}
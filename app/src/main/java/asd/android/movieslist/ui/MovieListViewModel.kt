package asd.android.movieslist.ui

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import asd.android.movieslist.services.LoadStatus
import asd.android.movieslist.services.ScreenStatus
import asd.android.movieslist.services.dto.Movie
import asd.android.movieslist.services.favorits.FavoriteMovie
import asd.android.movieslist.services.repo.DatabaseRepository
import asd.android.movieslist.services.repo.NetworkRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class MovieListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NetworkRepository()
    private val databaseRepository = DatabaseRepository(application.baseContext)

    var movies: MutableLiveData<List<Movie>> = MutableLiveData()
    var status: MutableLiveData<ScreenStatus> = MutableLiveData()
    var favoriteMovieIDs: MutableLiveData<MutableList<Int>> = MutableLiveData<MutableList<Int>>()
    private var favoriteMovies = databaseRepository.getFavoriteList()



    init {
        loadMovies()
        getFavoriteMovies()
    }

    private fun getFavoriteMovies() {
        favoriteMovieIDs = Transformations.map(favoriteMovies) { input ->
            val listOfid = mutableListOf<Int>()
            for (i in input) {
                listOfid.add(i.id)
            }
            listOfid
        } as MutableLiveData<MutableList<Int>>
    }

    fun updateFavoriteList(favoriteList: MutableList<Int>) {
        val favoriteMovieList = mutableListOf<FavoriteMovie>()

        for (i in favoriteList) {
            favoriteMovieList.add(FavoriteMovie(i))
        }

        databaseRepository.updateFavoriteList(favoriteMovieList)




    }


    @SuppressLint("CheckResult")
    fun loadMovies() {
        repository.getAllMovies()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { status.setValue(ScreenStatus(LoadStatus.LOADING)) }
            .subscribe({
                movies.value = it.results
                status.setValue(ScreenStatus(LoadStatus.NORMAL))
            },
                {
                    Log.d("test1", it.toString())
                    status.setValue(ScreenStatus(LoadStatus.ERROR))
                }
            )

    }

    fun delete(id:Int) = databaseRepository.dalete(FavoriteMovie(id))



    fun searchMovies(query:String){
        repository.searchMovies(query)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { status.setValue(ScreenStatus(LoadStatus.LOADING)) }
            .subscribe({
                movies.value = it.results
                status.setValue(ScreenStatus(LoadStatus.NORMAL))
            },
                {
                    Log.d("test1", it.toString())
                    status.setValue(ScreenStatus(LoadStatus.ERROR))
                }
            )

    }



}
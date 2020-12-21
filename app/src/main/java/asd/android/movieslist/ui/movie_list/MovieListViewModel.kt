package asd.android.movieslist.ui.movie_list

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import asd.android.movieslist.services.ScreenStatus
import asd.android.movieslist.services.database.FavoriteMovie
import asd.android.movieslist.services.dto.Movie
import asd.android.movieslist.services.repo.DatabaseRepository
import asd.android.movieslist.services.repo.NetworkRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.*
import java.util.concurrent.TimeUnit


class MovieListViewModel(application: Application,val publishSubject: PublishSubject<String>) : AndroidViewModel(application) {

    private val repository = NetworkRepository()
    private val databaseRepository = DatabaseRepository(application.baseContext)

    var movies: MutableLiveData<List<Movie>> = MutableLiveData()
    var status: MutableLiveData<ScreenStatus> = MutableLiveData()

    var favoriteMovieIDs: MutableLiveData<MutableList<Int>> = MutableLiveData<MutableList<Int>>()

    private var favoriteMovies = databaseRepository.getFavoriteList()



    init {
        loadMovies()
        getFavoriteMovies()
        subscribeOnSubject()
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

    fun updateFavoriteList(id:Int) {
        favoriteMovies.value?.add(FavoriteMovie(id))

        favoriteMovies.value?.let {
            databaseRepository.updateFavoriteList(it)
        }



    }

    fun subscribeOnSubject(){
        publishSubject.debounce(500,TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe {
            if (it.isEmpty()){
                loadMovies()
            }else{
                searchMovies(it)
            }
        }



    }



    @SuppressLint("CheckResult")
    fun loadMovies() {
        repository.getAllMovies()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { status.setValue(ScreenStatus(ScreenStatus.CurrentStatus.LOADING)) }
            .subscribe({
                movies.value = it.results
                status.setValue(ScreenStatus(ScreenStatus.CurrentStatus.NORMAL))
            },
                {
                    Log.d("test1", it.toString())
                    status.setValue(ScreenStatus(ScreenStatus.CurrentStatus.ERROR))
                }
            )

    }

    fun delete(id:Int) = databaseRepository.dalete(FavoriteMovie(id))



    fun searchMovies(query:String){
        repository.searchMovies(query)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { status.setValue(ScreenStatus(ScreenStatus.CurrentStatus.LOADING)) }
            .subscribe({
                movies.value = it.results
                status.setValue(ScreenStatus(ScreenStatus.CurrentStatus.NORMAL))
            },
                {
                    Log.d("test1", it.toString())
                    status.setValue(ScreenStatus(ScreenStatus.CurrentStatus.ERROR))
                }
            )

    }



}
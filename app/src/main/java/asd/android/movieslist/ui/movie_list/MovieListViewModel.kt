package asd.android.movieslist.ui.movie_list

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import asd.android.movieslist.services.ScreenStatus
import asd.android.movieslist.services.database.FavoriteMovie
import asd.android.movieslist.services.models.Movie
import asd.android.movieslist.services.repo.DatabaseRepository
import asd.android.movieslist.services.repo.NetworkRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit


class MovieListViewModel(
    application: Application
) : AndroidViewModel(application) {

    lateinit var publishSubject: PublishSubject<String>
    private val repository = NetworkRepository()
    private val databaseRepository = DatabaseRepository(application.baseContext)
    private var favoriteMovies = databaseRepository.getFavoriteList()
    var searchQuery = ""
    var movies: MutableLiveData<List<Movie>> = MutableLiveData()
    var status: MutableLiveData<ScreenStatus> = MutableLiveData()
    var favoriteMovieIDs: MutableLiveData<MutableList<Int>> = MutableLiveData<MutableList<Int>>()


    init {
        loadMovies()
        getFavoriteMovies()
    }

    private fun getFavoriteMovies() {
        favoriteMovieIDs = Transformations.map(favoriteMovies) { input ->
            val _favoriteMovieIDs = mutableListOf<Int>()
            for (i in input) {
                _favoriteMovieIDs.add(i.id)
            }
            _favoriteMovieIDs
        } as MutableLiveData<MutableList<Int>>
    }

    fun updateFavoriteList(id: Int) {
        favoriteMovies.value?.add(FavoriteMovie(id))
        favoriteMovies.value?.let {
            databaseRepository.updateFavoriteList(it)
        }
    }

    fun subscribeOnSubject(_publishSubject: PublishSubject<String>) {
        publishSubject = _publishSubject
        publishSubject.debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread()).subscribe {
                Log.d("test1", it)
                searchQuery = it
                if (it.isEmpty()) {
                    loadMovies()
                } else {
                    searchMovies(searchQuery)
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

    fun delete(id: Int) = databaseRepository.dalete(FavoriteMovie(id))

    @SuppressLint("CheckResult")
    fun searchMovies(query: String) {
        repository.searchMovies(query)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { status.setValue(ScreenStatus(ScreenStatus.CurrentStatus.LOADING)) }
            .subscribe({

                if (it.results.isEmpty() && searchQuery.isNotEmpty() ?: false) {
                    status.setValue(ScreenStatus(ScreenStatus.CurrentStatus.EMPTY_STATE))
                } else {
                    movies.value = it.results
                    status.setValue(ScreenStatus(ScreenStatus.CurrentStatus.NORMAL))
                }
            },
                {
                    status.setValue(ScreenStatus(ScreenStatus.CurrentStatus.ERROR))
                }
            )
    }

    fun refresh() {
        if (searchQuery.isEmpty() ?: true) {
            loadMovies()
        } else
            searchMovies(searchQuery)
    }
}
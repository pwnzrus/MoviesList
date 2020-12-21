package asd.android.movieslist.ui.movie_list

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.subjects.PublishSubject

class MovieListViewModelFactory(val application: Application,val publishSubject: PublishSubject<String>):ViewModelProvider.Factory

{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MovieListViewModel(application,publishSubject) as T
    }


}
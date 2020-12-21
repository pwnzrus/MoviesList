package asd.android.movieslist.ui.movie_list

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MovieListViewModelFactory(val application: Application):ViewModelProvider.Factory

{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MovieListViewModel(application) as T
    }


}
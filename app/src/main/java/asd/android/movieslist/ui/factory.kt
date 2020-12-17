package asd.android.movieslist.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class factory(val application: Application):ViewModelProvider.Factory

{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MovieListViewModel(application) as T
    }


}
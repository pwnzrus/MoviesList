package asd.android.movieslist.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import asd.android.movieslist.R

class HostFragmentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, MovieListFragment())
            .commit()
    }
}
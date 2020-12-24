package asd.android.movieslist.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import asd.android.movieslist.R
import asd.android.movieslist.services.ScreenStatus
import asd.android.movieslist.ui.movie_list.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import io.reactivex.subjects.PublishSubject

class MoviesListActivity : AppCompatActivity(), RecyclerClickItemListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: MovieListAdapter
    private lateinit var viewModel: MovieListViewModel
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var searchView: SearchView
    private lateinit var refreshButton: FloatingActionButton
    private lateinit var errorTextView: TextView
    private val publishSubject = PublishSubject.create<String>()
    private var favoritesList = mutableListOf<Int>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.movies_list_fragment)
        initView()
        initViewModel()
        initListeners()
    }

    private fun initView() {
        recyclerView = findViewById(R.id.movies_list_rv)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerViewAdapter = MovieListAdapter(emptyList(), favoritesList, this).also {
            recyclerView.adapter = it
        }
        progressBar = findViewById(R.id.movies_list_progress_bar)
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
        searchView = findViewById(R.id.movies_list_searchView)
        refreshButton = findViewById(R.id.movies_list_refresh_fab)
        errorTextView = findViewById(R.id.movies_list_error_text_view)
    }

    private fun initViewModel() {

        viewModel = ViewModelProvider(
            this,
            MovieListViewModelFactory(application)
        ).get(MovieListViewModel::class.java)

        //Подписываем вью модель на паблиш сабджект,испускающий значения из поисковой строки.По ним осуществляется поиск
        viewModel.subscribeOnSubject(publishSubject)

        viewModel.movies.observe(this) {
            recyclerViewAdapter.movies = it
            recyclerViewAdapter.notifyDataSetChanged()
        }

        //Подписываемся на статусы экранов
        viewModel.status.observe(this) {
            when (it.loadStatus) {
                ScreenStatus.CurrentStatus.ERROR -> doOnErrorState("Ошибка")
                ScreenStatus.CurrentStatus.LOADING -> doOnLoadingState()
                ScreenStatus.CurrentStatus.NORMAL -> doOnNormalState()
                ScreenStatus.CurrentStatus.EMPTY_STATE -> doOnEmptySearchResult()
            }

            swipeRefreshLayout.isRefreshing = false
            swipeRefreshLayout.isEnabled = it.loadStatus == ScreenStatus.CurrentStatus.NORMAL
        }

        //Подписываемся на список айди любимых фильмов
        viewModel.favoriteMovieIDs.observe(this) {
            favoritesList = it
            recyclerViewAdapter.favoriteList = favoritesList
            recyclerViewAdapter.notifyDataSetChanged()
        }
    }

    private fun initListeners() {
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
        }

        refreshButton.setOnClickListener { viewModel.loadMovies() }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                publishSubject.onNext(newText ?: "")
                return true
            }
        }
        )
    }

    private fun doOnErrorState(errorText: String) {
        progressBar.visibility = View.GONE

        //Если во вьюМодели даже не инициализировались фильмы, то будет отображен экран ошибки с кнопкой обновить.Например первый запуск без инета
        if (viewModel.movies.value == null) {
            showErrorView(true)
            return
        }
        //Если пришла ошибка, но список фильмов не пустой - отображаем снек
        Snackbar.make(recyclerView, errorText, Snackbar.LENGTH_SHORT).show()
    }

    private fun doOnLoadingState() {
        progressBar.visibility = View.VISIBLE
        showErrorView(false)
    }

    private fun doOnNormalState() {
        progressBar.visibility = View.GONE
        showErrorView(false)
    }

    override fun onItemClick(int: Int) {
        //Реализация добавления/удаления из избранного
        //Если в в списке есть айдишка фильма, то по тапу удаляем ее из этого списка и обновляем значение в бд,
        // затем список обновится через лайвдату и обновит представления
        if (int in favoritesList) {
            viewModel.delete(int)
            
            //Если айди фильма в списке отсутстствует, то его туда добавляем и обновляем данные в бд
        } else if (int !in favoritesList) {
            viewModel.updateFavoriteList(int)
        }
    }

    private fun showErrorView(show: Boolean) {
        //Показ или скрытие заглушки, если список фильмов был пустой на момент ошибки
        if (show) {
            refreshButton.visibility = View.VISIBLE
            errorTextView.visibility = View.VISIBLE
        } else {
            refreshButton.visibility = View.GONE
            errorTextView.visibility = View.GONE
        }
    }

    private fun doOnEmptySearchResult() {
        doOnErrorState("По вашему запросу \"${viewModel.searchQuery}\" ничего не найдено")
    }
}







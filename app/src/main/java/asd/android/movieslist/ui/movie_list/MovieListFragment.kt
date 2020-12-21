package asd.android.movieslist.ui.movie_list

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import asd.android.movieslist.R
import asd.android.movieslist.services.ScreenStatus
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.*
import java.util.concurrent.TimeUnit


class MovieListFragment : Fragment(), RecyclerClickItemListener {
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: MovieListAdapter
    private lateinit var viewModel: MovieListViewModel
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeLayout: SwipeRefreshLayout
    private var favoriteList = mutableListOf<Int>()
    private var screenStatus: ScreenStatus = ScreenStatus()
    private lateinit var searchView: SearchView
    private var querySearch: String = ""
    private lateinit var refreshButton: FloatingActionButton
    private lateinit var errorStateTextView: TextView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.movies_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        initListeners()
        initViewModel()

    }

    private fun initView(view: View) {
        recycler = view.findViewById(R.id.movies_list_rv)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        adapter = MovieListAdapter(emptyList(), favoriteList, this).also {
            recycler.adapter = it
        }
        progressBar = view.findViewById(R.id.progressBar)
        swipeLayout = view.findViewById(R.id.swipe_refresh_layout)
        searchView = view.findViewById(R.id.searchView)
        refreshButton = view.findViewById(R.id.refresh_btn)
        errorStateTextView = view.findViewById(R.id.errorTextView)
    }

    @SuppressLint("CheckResult")
    private fun initListeners() {

        swipeLayout.setOnRefreshListener {
            onLayoutRefresh()
        }

        Observable.create<String> { observableEmitter ->
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    observableEmitter.onNext(newText ?: "")
                    return true
                }
            }
            )
        }.debounce(500, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe {
            if (it.isEmpty()) {
                viewModel.loadMovies()
            } else {
                querySearch = it
                viewModel.searchMovies(it)
            }
        }

        refreshButton.setOnClickListener { viewModel.loadMovies() }
    }


    private fun initViewModel() {

        viewModel = ViewModelProvider(
            this,
            MovieListViewModelFactory(requireActivity().application)
        ).get(MovieListViewModel::class.java)

        viewModel.movies.observe(this) {
            swipeLayout.isRefreshing = false
            adapter.movies = it
            adapter.notifyDataSetChanged()

            if (it.isEmpty() && querySearch.isNotEmpty()) {
                showError("По вашему запроса \"$querySearch\" ничего не найдено")
            }

        }

        viewModel.status.observe(this) {
            when (it.loadStatus) {
                ScreenStatus.CurrentStatus.ERROR -> showError("Ошибка")
                ScreenStatus.CurrentStatus.LOADING -> showProgressBar()
                ScreenStatus.CurrentStatus.NORMAL -> hideProgressBar()
            }
        }

        viewModel.favoriteMovieIDs.observe(this) {
            favoriteList = it
            adapter.favoriteList = favoriteList
            adapter.notifyDataSetChanged()
        }
    }

    private fun showError(errorText: String) {
        screenStatus.loadStatus = ScreenStatus.CurrentStatus.ERROR
        progressBar.visibility = View.GONE
        swipeLayout.isEnabled = screenStatus.loadStatus == (ScreenStatus.CurrentStatus.NORMAL)

        if (viewModel.movies.value == null && screenStatus.loadStatus == ScreenStatus.CurrentStatus.ERROR) {
            showErrorView(true)
            return
        }
        Snackbar.make(recycler, errorText, Snackbar.LENGTH_SHORT).show()
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
        screenStatus.loadStatus = ScreenStatus.CurrentStatus.LOADING
        swipeLayout.isEnabled = screenStatus.loadStatus == (ScreenStatus.CurrentStatus.NORMAL)
        showErrorView(false)
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
        screenStatus.loadStatus = ScreenStatus.CurrentStatus.NORMAL
        swipeLayout.isEnabled = screenStatus.loadStatus == (ScreenStatus.CurrentStatus.NORMAL)
        showErrorView(false)
    }

    override fun onItemClick(int: Int) {
        if (int in favoriteList) {
            viewModel.delete(int)

        } else if (int !in favoriteList) {
            viewModel.updateFavoriteList(int)
        }
    }

    private fun onLayoutRefresh() {
        if (querySearch.isEmpty()) {
            viewModel.loadMovies()
        } else
            viewModel.searchMovies(querySearch)
    }

    private fun showErrorView(show: Boolean) {
        if (show) {
            refreshButton.visibility = View.VISIBLE
            errorStateTextView.visibility = View.VISIBLE

        } else {
            refreshButton.visibility = View.GONE
            errorStateTextView.visibility = View.GONE
        }
    }


}
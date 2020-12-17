package asd.android.movieslist.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import asd.android.movieslist.R
import asd.android.movieslist.services.LoadStatus
import asd.android.movieslist.services.ScreenStatus
import com.google.android.material.snackbar.Snackbar


class MovieListFragment : Fragment(), RecyclerClickItemListener{
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: MovieListAdapter
    private lateinit var viewModel: MovieListViewModel
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeLayout: SwipeRefreshLayout
    private var favoriteList = mutableListOf<Int>()
    private var screenStatus: ScreenStatus = ScreenStatus()
    private lateinit var searchView: SearchView

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

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








    }

    private fun initListeners() {
        swipeLayout.setOnRefreshListener {
            onLayoutRefresh()
        }

        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.searchMovies(query?:"")
                Toast.makeText(requireContext(),query,Toast.LENGTH_SHORT).show()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        } )
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            factory(requireActivity().application)
        ).get(MovieListViewModel::class.java)

        viewModel.movies.observe(this) {
            swipeLayout.isRefreshing = false
            adapter.movies = it
            adapter.notifyDataSetChanged()
        }

        viewModel.status.observe(this) {
            when (it.loadStatus) {
                LoadStatus.ERROR -> showError("Ошибка")
                LoadStatus.LOADING -> showProgressBar()
                LoadStatus.NORMAL -> hideProgressBar()
            }
        }


        viewModel.favoriteMovieIDs.observe(this) {
            favoriteList = it
            adapter.favoriteList = favoriteList
            adapter.notifyDataSetChanged()
            Log.d("test1", "Получение списка айдишников фильмов подписчиком")
        }


    }

    private fun showError(errorText: String) {
        screenStatus.loadStatus = LoadStatus.ERROR
        Snackbar.make(recycler, errorText, Snackbar.LENGTH_SHORT).show()
        swipeLayout.isEnabled = screenStatus.loadStatus == (LoadStatus.NORMAL)
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
        screenStatus.loadStatus = LoadStatus.LOADING
        swipeLayout.isEnabled = screenStatus.loadStatus == (LoadStatus.NORMAL)
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
        screenStatus.loadStatus = LoadStatus.NORMAL
        swipeLayout.isEnabled = screenStatus.loadStatus == (LoadStatus.NORMAL)

    }

    override fun onItemClick(int: Int) {

        if (int in favoriteList) {
            favoriteList.remove(int)
            viewModel.delete(int)

        } else if (int !in favoriteList) {
            favoriteList.add(int)
            viewModel.updateFavoriteList(favoriteList)
        }
        Log.d("test",(screenStatus == ScreenStatus(LoadStatus.NORMAL)).toString())

    }

    fun onLayoutRefresh() {
        viewModel.loadMovies()
    }




}
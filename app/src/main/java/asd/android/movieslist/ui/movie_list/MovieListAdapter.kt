package asd.android.movieslist.ui.movie_list

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import asd.android.movieslist.R
import asd.android.movieslist.services.dto.Movie
import com.squareup.picasso.Picasso

class MovieListAdapter(var movies: List<Movie>, var favoriteList: MutableList<Int>,var recyclerClickItemListener: RecyclerClickItemListener) :
    RecyclerView.Adapter<MovieListAdapter.MovieViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return MovieViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.movie_recycler_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])


    }

    override fun getItemCount(): Int {
        return movies.size
    }


    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private var poster: ImageView = itemView.findViewById(R.id.movie_poster_iv)
        private var title: TextView = itemView.findViewById(R.id.movie_title_tv)
        private var date: TextView = itemView.findViewById(R.id.movie_date_tv)
        private var description: TextView = itemView.findViewById(R.id.movie_description_tv)
        private var favorite: ImageView = itemView.findViewById(R.id.favorites_iv)

        init {
            favorite.setOnClickListener(this)
            itemView.setOnClickListener(this)
        }

        fun bind(movie: Movie) {
            title.text = movie.title
            date.text = movie.releaseDate
            description.text = movie.overview
            Picasso.get().load("https://image.tmdb.org/t/p/w500" + movie.posterPath).into(poster)
            if (movie.id in favoriteList){ favorite.setImageResource(R.drawable.ic_favorite)}
            else favorite.setImageResource(R.drawable.ic_not_favorite)

        }

        override fun onClick(v: View?) {
           Log.d("test1","Нажатие")
            when(v){
                favorite -> recyclerClickItemListener.onItemClick(movies[adapterPosition].id)
                itemView ->Toast.makeText(itemView.context,movies[adapterPosition].title,Toast.LENGTH_SHORT).show()
            }


        }


    }

}
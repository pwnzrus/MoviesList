package asd.android.movieslist.services.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DTO {
    @Query("SELECT * FROM FavoriteMovie")
    fun getFavoriteList(): LiveData<MutableList<FavoriteMovie>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateFavoriteList(favoriteMovie: MutableList<FavoriteMovie>)

    @Delete
    fun delete(favoriteMovie: FavoriteMovie)

}
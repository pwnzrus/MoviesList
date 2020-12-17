package asd.android.movieslist.services.favorits

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DatabaseDTO {
    @Query("SELECT * FROM FavoriteMovie")
    fun getFavoriteList():LiveData<MutableList<FavoriteMovie>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateFavoriteList(favoriteMovie:MutableList<FavoriteMovie>)

    @Delete
    fun delete(favoriteMovie:FavoriteMovie)

}
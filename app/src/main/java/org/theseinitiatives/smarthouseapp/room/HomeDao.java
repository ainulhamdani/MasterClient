package org.theseinitiatives.smarthouseapp.room;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface HomeDao {
    @Query("SELECT * FROM home")
    LiveData<List<Home>> getHomes();

    @Query("SELECT * FROM home WHERE id IN(:homeIds)")
    LiveData<List<Home>> getHomesByIds(String[] homeIds);

    @Query("SELECT * FROM home WHERE id=:homeId")
    Home getHome(String homeId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertHomes(Home... homes);

    @Delete
    void deleteHomes(Home... homes);
}

package org.theseinitiatives.smarthouseapp.room;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface HomeDeviceDao {
    @Query("SELECT * FROM home_device")
    LiveData<List<HomeDevice>> getHomeDevices();

    @Query("SELECT * FROM home_device WHERE home_id=:homeId")
    LiveData<List<HomeDevice>> getHomeDevicesWithHomeId(String homeId);

    @Query("SELECT * FROM home_device WHERE id IN(:homeDeviceIds)")
    LiveData<List<HomeDevice>> getHomeDevicesByIds(String[] homeDeviceIds);

    @Query("SELECT * FROM home_device WHERE id=:homeDeviceId")
    HomeDevice getHomeDevice(String homeDeviceId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertHomeDevices(HomeDevice... homes);

    @Delete
    void deleteHomeDevices(HomeDevice... homes);
}

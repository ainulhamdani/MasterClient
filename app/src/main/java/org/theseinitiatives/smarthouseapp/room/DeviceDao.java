package org.theseinitiatives.smarthouseapp.room;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface DeviceDao {

    @Query("SELECT * FROM device")
    LiveData<List<Device>> getDevices();

    @Query("SELECT * FROM device WHERE id IN(:deviceIds)")
    LiveData<List<Device>> getDevicesByIds(String[] deviceIds);

    @Query("SELECT * FROM device WHERE id=:deviceId")
    Device getDevice(String deviceId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDevices(Device... devices);

    @Delete
    void deletedevices(Device... devices);
}

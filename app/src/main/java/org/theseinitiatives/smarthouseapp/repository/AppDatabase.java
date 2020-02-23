package org.theseinitiatives.smarthouseapp.repository;

import android.content.Context;

import org.theseinitiatives.smarthouseapp.room.Device;
import org.theseinitiatives.smarthouseapp.room.DeviceDao;
import org.theseinitiatives.smarthouseapp.room.Home;
import org.theseinitiatives.smarthouseapp.room.HomeDao;
import org.theseinitiatives.smarthouseapp.room.HomeDevice;
import org.theseinitiatives.smarthouseapp.room.HomeDeviceDao;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Home.class, HomeDevice.class, Device.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DeviceDao deviceDao();
    public abstract HomeDao homeDao();
    public abstract HomeDeviceDao homeDeviceDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (AppDatabase.class){
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class,"smarthouse_db").build();
                }
            }
        }
        return INSTANCE;
    }
}

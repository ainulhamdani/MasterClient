package org.theseinitiatives.smarthouseapp.repository;

import android.app.Application;
import android.os.AsyncTask;

import org.theseinitiatives.smarthouseapp.room.HomeDevice;
import org.theseinitiatives.smarthouseapp.room.HomeDeviceDao;

import java.util.List;

import androidx.lifecycle.LiveData;

public class HomeDeviceRepository {

    private HomeDeviceDao homeDeviceDao;
    private LiveData<List<HomeDevice>> listHomeDevices;

    public HomeDeviceRepository(Application application){
        AppDatabase appDatabase = AppDatabase.getDatabase(application);
        homeDeviceDao = appDatabase.homeDeviceDao();
        listHomeDevices = homeDeviceDao.getHomeDevices();
    }

    public void setListHomeDevices(){
        listHomeDevices = homeDeviceDao.getHomeDevices();
    }

    public void setListHomeDevices(String homeId){
        listHomeDevices = homeDeviceDao.getHomeDevicesWithHomeId(homeId);
    }

    public HomeDevice getHomeDevice(String id){
        return homeDeviceDao.getHomeDevice(id);
    }

    public LiveData<List<HomeDevice>> getListHomeDevices() {
        return listHomeDevices;
    }

    public void setListHomeDevices(LiveData<List<HomeDevice>> listHomeDevices) {
        this.listHomeDevices = listHomeDevices;
    }

    public void insertHomeDevice(HomeDevice... homeDevice){
        new insertHomeDeviceAsyncTask(homeDeviceDao).execute(homeDevice);
    }

    public void deleteHomeDevice(HomeDevice homeDevice){
        new deleteHomeDeviceAsyncTask(homeDeviceDao).execute(homeDevice);
    }

    private static class insertHomeDeviceAsyncTask extends AsyncTask<HomeDevice, Void, Void> {

        private HomeDeviceDao homeDeviceDao;

        insertHomeDeviceAsyncTask(HomeDeviceDao homeDeviceDao) {
            this.homeDeviceDao = homeDeviceDao;
        }

        @Override
        protected Void doInBackground(HomeDevice... homeDevices) {
            homeDeviceDao.insertHomeDevices(homeDevices);
            return null;
        }
    }

    private static class deleteHomeDeviceAsyncTask extends AsyncTask<HomeDevice, Void, Void>{
        private HomeDeviceDao homeDeviceDao;

        deleteHomeDeviceAsyncTask(HomeDeviceDao homeDeviceDao){
            this.homeDeviceDao = homeDeviceDao;
        }

        @Override
        protected Void doInBackground(HomeDevice... homeDevices) {
            homeDeviceDao.deleteHomeDevices(homeDevices);
            return null;
        }
    }
}

package org.theseinitiatives.smarthouseapp.repository;

import android.app.Application;
import android.os.AsyncTask;

import org.theseinitiatives.smarthouseapp.room.Home;
import org.theseinitiatives.smarthouseapp.room.HomeDao;

import java.util.List;

import androidx.lifecycle.LiveData;

public class HomeRepository {

    private HomeDao homeDao;
    private LiveData<List<Home>> listHomes;

    public HomeRepository(Application application){
        AppDatabase appDatabase = AppDatabase.getDatabase(application);
        homeDao = appDatabase.homeDao();
        listHomes = homeDao.getHomes();
    }

    public Home getHome(String id){
        return homeDao.getHome(id);
    }

    public LiveData<List<Home>> getListHomes() {
        return listHomes;
    }

    public void setListHomes(LiveData<List<Home>> listHomes) {
        this.listHomes = listHomes;
    }

    public void insertHome(Home home){
        new insertHomeAsyncTask(homeDao).execute(home);
    }

    public void deleteHome(Home home){
        new deleteHomeAsyncTask(homeDao).execute(home);
    }

    private static class insertHomeAsyncTask extends AsyncTask<Home, Void, Void> {

        private HomeDao homeDao;

        insertHomeAsyncTask(HomeDao homeDao) {
            this.homeDao = homeDao;
        }

        @Override
        protected Void doInBackground(Home... homes) {
            homeDao.insertHomes(homes);
            return null;
        }
    }

    private static class deleteHomeAsyncTask extends AsyncTask<Home, Void, Void>{
        private HomeDao homeDao;

        deleteHomeAsyncTask(HomeDao homeDao){
            this.homeDao = homeDao;
        }

        @Override
        protected Void doInBackground(Home... homes) {
            homeDao.deleteHomes(homes);
            return null;
        }
    }
}

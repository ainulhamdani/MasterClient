package org.theseinitiatives.smarthouseapp.repository;

import android.app.Application;
import android.os.AsyncTask;

import org.theseinitiatives.smarthouseapp.room.Device;
import org.theseinitiatives.smarthouseapp.room.DeviceDao;

import java.util.List;

import androidx.lifecycle.LiveData;

public class DeviceRepository {

    private DeviceDao deviceDao;
    private LiveData<List<Device>> listDevices;

    public DeviceRepository(Application application){
        AppDatabase appDatabase = AppDatabase.getDatabase(application);
        deviceDao = appDatabase.deviceDao();
        listDevices = deviceDao.getDevices();
    }

    public Device getDevice(String id){
//        Device device = null;
//        try {
//            device = new getDeviceByIdAsyncTask(deviceDao).execute(id).get();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return deviceDao.getDevice(id);
    }

    public LiveData<List<Device>> getListDevices() {
        return listDevices;
    }

    public void setListDevices(LiveData<List<Device>> listDevices) {
        this.listDevices = listDevices;
    }

    public void insertDevice(Device device){
        new insertDeviceAsyncTask(deviceDao).execute(device);
    }

    public void deleteDevice(Device device){
        new deleteDeviceAsyncTask(deviceDao).execute(device);
    }



    private static class getDeviceByIdAsyncTask extends AsyncTask<String, Void, Device> {

        private DeviceDao deviceDao;

        getDeviceByIdAsyncTask(DeviceDao deviceDao) {
            this.deviceDao = deviceDao;
        }

        @Override
        protected Device doInBackground(String... id) {
            return deviceDao.getDevice(id[0]);
        }

        @Override
        protected void onPostExecute(Device device){

        }
    }

    private static class insertDeviceAsyncTask extends AsyncTask<Device, Void, Void> {

        private DeviceDao deviceDao;

        insertDeviceAsyncTask(DeviceDao deviceDao) {
            this.deviceDao = deviceDao;
        }

        @Override
        protected Void doInBackground(Device... devices) {
            deviceDao.insertDevices(devices);
            return null;
        }
    }

    private static class deleteDeviceAsyncTask extends AsyncTask<Device, Void, Void>{
        private DeviceDao deviceDao;

        deleteDeviceAsyncTask(DeviceDao deviceDao){
            this.deviceDao = deviceDao;
        }

        @Override
        protected Void doInBackground(Device... devices) {
            deviceDao.deletedevices(devices);
            return null;
        }
    }
}

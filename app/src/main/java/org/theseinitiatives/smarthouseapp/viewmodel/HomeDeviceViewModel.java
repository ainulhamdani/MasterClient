package org.theseinitiatives.smarthouseapp.viewmodel;

import android.app.Application;

import org.theseinitiatives.smarthouseapp.room.HomeDevice;
import org.theseinitiatives.smarthouseapp.repository.HomeDeviceRepository;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class HomeDeviceViewModel extends AndroidViewModel {

    private LiveData<List<HomeDevice>> listDevices;

    private HomeDeviceRepository deviceRepository;

    public HomeDeviceViewModel(@NonNull Application application) {
        super(application);
        deviceRepository = new HomeDeviceRepository(application);
    }

    public LiveData<List<HomeDevice>> getListHomeDevices() {
        if (listDevices == null){
            listDevices = deviceRepository.getListHomeDevices();
        }
        return listDevices;
    }
}

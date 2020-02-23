package org.theseinitiatives.smarthouseapp.viewmodel;

import android.app.Application;

import org.theseinitiatives.smarthouseapp.room.Device;
import org.theseinitiatives.smarthouseapp.repository.DeviceRepository;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class DeviceViewModel extends AndroidViewModel {

    private LiveData<List<Device>> listDevices;

    private DeviceRepository deviceRepository;

    public DeviceViewModel(@NonNull Application application) {
        super(application);
        deviceRepository = new DeviceRepository(application);
    }

    public LiveData<List<Device>> getListDevices() {
        if (listDevices == null){
            listDevices = deviceRepository.getListDevices();
        }
        return listDevices;
    }
}

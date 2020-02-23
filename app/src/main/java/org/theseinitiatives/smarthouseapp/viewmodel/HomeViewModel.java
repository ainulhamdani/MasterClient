package org.theseinitiatives.smarthouseapp.viewmodel;

import android.app.Application;

import org.theseinitiatives.smarthouseapp.room.Home;
import org.theseinitiatives.smarthouseapp.repository.HomeRepository;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class HomeViewModel extends AndroidViewModel {
    private LiveData<List<Home>> listHomes;

    private HomeRepository homeRepository;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        homeRepository = new HomeRepository(application);
    }

    public LiveData<List<Home>> getListHomes() {
        if (listHomes == null){
            listHomes = homeRepository.getListHomes();
        }
        return listHomes;
    }
}

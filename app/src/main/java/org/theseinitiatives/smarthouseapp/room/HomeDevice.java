package org.theseinitiatives.smarthouseapp.room;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "home_device")
public class HomeDevice implements Parcelable {

    public HomeDevice(){}

    @NonNull
    @PrimaryKey
    public String id;

    @ColumnInfo(name = "home_id")
    public String homeId;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "ServiceType_name")
    public String serviceTypeName;

    @ColumnInfo(name = "type")
    public String type;

    @ColumnInfo(name = "status")
    public String status;

    protected HomeDevice(Parcel in) {
        id = in.readString();
        homeId = in.readString();
        name = in.readString();
        serviceTypeName = in.readString();
        type = in.readString();
        status = in.readString();
    }

    public static final Creator<HomeDevice> CREATOR = new Creator<HomeDevice>() {
        @Override
        public HomeDevice createFromParcel(Parcel in) {
            return new HomeDevice(in);
        }

        @Override
        public HomeDevice[] newArray(int size) {
            return new HomeDevice[size];
        }
    };

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getHomeId() {
        return homeId;
    }

    public void setHomeId(String homeId) {
        this.homeId = homeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServiceTypeName() {
        return serviceTypeName;
    }

    public void setServiceTypeName(String serviceTypeName) {
        this.serviceTypeName = serviceTypeName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.homeId);
        dest.writeString(this.name);
        dest.writeString(this.type);
        dest.writeString(this.status);
    }
}

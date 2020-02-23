package org.theseinitiatives.smarthouseapp.room;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "device")
public class Device implements Parcelable {

    public Device() {
    }

    @NonNull
    @PrimaryKey
    public String id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "url")
    public String url;

    @ColumnInfo(name = "type")
    public String type;

    @ColumnInfo(name = "group")
    public String group;

    @ColumnInfo(name = "status")
    public String status;

    public boolean isOnline = false;

    protected Device(Parcel in) {
        id = in.readString();
        name = in.readString();
        url = in.readString();
        type = in.readString();
        group = in.readString();
        status = in.readString();
    }

    public static final Creator<Device> CREATOR = new Creator<Device>() {
        @Override
        public Device createFromParcel(Parcel in) {
            return new Device(in);
        }

        @Override
        public Device[] newArray(int size) {
            return new Device[size];
        }
    };

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.url);
        dest.writeString(this.type);
        dest.writeString(this.group);
        dest.writeString(this.status);
    }
}

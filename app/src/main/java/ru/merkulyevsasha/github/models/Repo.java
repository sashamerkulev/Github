
package ru.merkulyevsasha.github.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Repo implements Parcelable {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("owner")
    @Expose
    private Owner owner;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("forks_count")
    @Expose
    private int forksCount;
    @SerializedName("watchers_count")
    @Expose
    private int watchersCount;

    public Repo(){}

    protected Repo(Parcel in) {
        owner = in.readParcelable(Owner.class.getClassLoader());
        name = in.readString();
        fullName = in.readString();
        description = in.readString();
        forksCount = in.readInt();
        watchersCount = in.readInt();
        id = in.readInt();
    }

    public static final Creator<Repo> CREATOR = new Creator<Repo>() {
        @Override
        public Repo createFromParcel(Parcel in) {
            return new Repo(in);
        }

        @Override
        public Repo[] newArray(int size) {
            return new Repo[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getForksCount() {
        return forksCount;
    }

    public void setForksCount(Integer forksCount) {
        this.forksCount = forksCount;
    }

    public Integer getWatchersCount() {
        return watchersCount;
    }

    public void setWatchersCount(Integer watchersCount) {
        this.watchersCount = watchersCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(owner, flags);
        dest.writeString(name);
        dest.writeString(fullName);
        dest.writeString(description);
        dest.writeInt(forksCount);
        dest.writeInt(watchersCount);
        dest.writeInt(id);
    }
}

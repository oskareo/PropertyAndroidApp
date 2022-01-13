package com.example.madpropertypal.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Property implements Serializable, Parcelable {
    private int number;
    private String name;
    private String type;
    private String leaseType;
    private String location;
    private long bedrooms;
    private long bathrooms;
    private long size;
    private long askingPrice;
    private String localAmenities;
    private String description;
    private long floors;
    private byte[] image;
    private int userId;
    private long creationDate;
    private String imageLink;
    private int isFavourite; // 1 = Favourite and 0 = Not Favourite
    private int isUploadedToServer; //1 = Uploaded To Server and 0 = Not Uploaded

    public Property(int number, String name, String type, String leaseType, String location, long bedrooms, long bathrooms, long size, long askingPrice, String localAmenities, String description, long floors, byte[] image, int userId, long creationDate, String imageLink, int isFavourite,int isUploadedToServer) {
        this.number = number;
        this.name = name;
        this.type = type;
        this.leaseType = leaseType;
        this.location = location;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.size = size;
        this.askingPrice = askingPrice;
        this.localAmenities = localAmenities;
        this.description = description;
        this.floors = floors;
        this.image = image;
        this.userId = userId;
        this.creationDate = creationDate;
        this.imageLink = imageLink;
        this.isFavourite = isFavourite;
        this.isUploadedToServer = isUploadedToServer;
    }

    protected Property(Parcel in) {
        number = in.readInt();
        name = in.readString();
        type = in.readString();
        leaseType = in.readString();
        location = in.readString();
        bedrooms = in.readLong();
        bathrooms = in.readLong();
        size = in.readLong();
        askingPrice = in.readLong();
        localAmenities = in.readString();
        description = in.readString();
        floors = in.readLong();
        image = in.createByteArray();
        userId = in.readInt();
        creationDate = in.readLong();
        imageLink = in.readString();
        isFavourite = in.readInt();
        isUploadedToServer = in.readInt();
    }

    public static final Creator<Property> CREATOR = new Creator<Property>() {
        @Override
        public Property createFromParcel(Parcel in) {
            return new Property(in);
        }

        @Override
        public Property[] newArray(int size) {
            return new Property[size];
        }
    };

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLeaseType() {
        return leaseType;
    }

    public void setLeaseType(String leaseType) {
        this.leaseType = leaseType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(long bedrooms) {
        this.bedrooms = bedrooms;
    }

    public long getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(long bathrooms) {
        this.bathrooms = bathrooms;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getAskingPrice() {
        return askingPrice;
    }

    public void setAskingPrice(long askingPrice) {
        this.askingPrice = askingPrice;
    }

    public String getLocalAmenities() {
        return localAmenities;
    }

    public void setLocalAmenities(String localAmenities) {
        this.localAmenities = localAmenities;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getFloors() {
        return floors;
    }

    public void setFloors(long floors) {
        this.floors = floors;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public int getIsFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(int isFavourite) {
        this.isFavourite = isFavourite;
    }

    public int getIsUploadedToServer() {
        return isUploadedToServer;
    }

    public void setIsUploadedToServer(int isUploadedToServer) {
        this.isUploadedToServer = isUploadedToServer;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(number);
        parcel.writeString(name);
        parcel.writeString(type);
        parcel.writeString(leaseType);
        parcel.writeString(location);
        parcel.writeLong(bedrooms);
        parcel.writeLong(bathrooms);
        parcel.writeLong(size);
        parcel.writeLong(askingPrice);
        parcel.writeString(localAmenities);
        parcel.writeString(description);
        parcel.writeLong(floors);
        parcel.writeByteArray(image);
        parcel.writeInt(userId);
        parcel.writeLong(creationDate);
        parcel.writeString(imageLink);
        parcel.writeInt(isFavourite);
        parcel.writeInt(isUploadedToServer);
    }
}

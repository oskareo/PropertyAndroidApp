package com.example.madpropertypal.model.api_response;

import java.util.List;

public class PropertyResponse {

    private List<Data> data;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ClassPojo [data = " + data + "]";
    }

    public class Data {

        private String leastype;

        private String image;

        private String description;

        private String local_amenities;

        private String creation_date;

        private String is_favourite;

        private String type;

        private String bathrooms;

        private String bedrooms;

        private String floors;

        private String size;

        private String user_id;

        private String name;

        private String location;

        private String id;

        private String asking_price;

        public String getLeastype() {
            return leastype;
        }

        public void setLeastype(String leastype) {
            this.leastype = leastype;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLocal_amenities() {
            return local_amenities;
        }

        public void setLocal_amenities(String local_amenities) {
            this.local_amenities = local_amenities;
        }

        public String getCreation_date() {
            return creation_date;
        }

        public void setCreation_date(String creation_date) {
            this.creation_date = creation_date;
        }

        public String getIs_favourite() {
            return is_favourite;
        }

        public void setIs_favourite(String is_favourite) {
            this.is_favourite = is_favourite;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getBathrooms() {
            return bathrooms;
        }

        public void setBathrooms(String bathrooms) {
            this.bathrooms = bathrooms;
        }

        public String getBedrooms() {
            return bedrooms;
        }

        public void setBedrooms(String bedrooms) {
            this.bedrooms = bedrooms;
        }

        public String getFloors() {
            return floors;
        }

        public void setFloors(String floors) {
            this.floors = floors;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAsking_price() {
            return asking_price;
        }

        public void setAsking_price(String asking_price) {
            this.asking_price = asking_price;
        }

        @Override
        public String toString() {
            return "ClassPojo [leastype = " + leastype + ", image = " + image + ", description = " + description + ", local_amenities = " + local_amenities + ", creation_date = " + creation_date + ", is_favourite = " + is_favourite + ", type = " + type + ", bathrooms = " + bathrooms + ", bedrooms = " + bedrooms + ", floors = " + floors + ", size = " + size + ", user_id = " + user_id + ", name = " + name + ", location = " + location + ", id = " + id + ", asking_price = " + asking_price + "]";
        }
    }
}

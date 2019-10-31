package com.travelplanner;

public class Poi {
    private String poi_rank;
    private String poi_name;
    private String poi_address;
    private String poi_longitude;
    private String poi_latitude;
    private String poi_image;

    public Poi() {

    }

    public Poi(String poi_name, String poi_address, String poi_longitude, String poi_latitude, String poi_image) {
        this.poi_name = poi_name;
        this.poi_address = poi_address;
        this.poi_longitude = poi_longitude;
        this.poi_latitude = poi_latitude;
        this.poi_image = poi_image;
    }

    public String getPoi_rank() {

        return poi_rank;
    }

    public void setPoi_rank(String poi_rank) {

        this.poi_rank = poi_rank;
    }

    public String getPoi_name() {

        return poi_name;
    }

    public void setPoi_name(String poi_name) {

        this.poi_name = poi_name;
    }

    public String getPoi_address() {

        return poi_address;
    }

    public void setPoi_address(String poi_address) {

        this.poi_address = poi_address;
    }

    public String getPoi_longitude() {

        return poi_longitude;
    }

    public void setPoi_longitude(String poi_longitude) {

        this.poi_longitude = poi_longitude;
    }

    public String getPoi_latitude() {

        return poi_latitude;
    }

    public void setPoi_latitude(String poi_latitude) {

        this.poi_latitude = poi_latitude;
    }

    public String getPoi_image() {

        return poi_image;
    }

    public void setPoi_image(String poi_image) {

        this.poi_image = poi_image;
    }
}

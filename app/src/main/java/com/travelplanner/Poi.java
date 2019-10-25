package com.travelplanner;

public class Poi {
    public String poi_rank;
    public String poi_name;
    public String poi_address;
    public String poi_image;

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

    public String getPoi_image() {

        return poi_image;
    }

    public void setPoi_image(String poi_image) {

        this.poi_image = poi_image;
    }

    public Poi(String poi_name, String poi_address, String poi_image) {
        this.poi_name = poi_name;
        this.poi_address = poi_address;
        this.poi_image = poi_image;
    }

}




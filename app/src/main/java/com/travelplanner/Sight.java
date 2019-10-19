package com.travelplanner;

public class Sight {

    private String mImage;
    private String mImageName;

    public Sight(String mImage, String mImageName) {
        this.mImage = mImage;
        this.mImageName = mImageName;
    }

    public String getmImage() {
        return mImage;
    }

    public void setmImage(String mImage) {
        this.mImage = mImage;
    }

    public String getmImageName() {
        return mImageName;
    }

    public void setmImageName(String mImageName) {
        this.mImageName = mImageName;
    }
}

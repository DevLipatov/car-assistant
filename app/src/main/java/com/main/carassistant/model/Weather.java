package com.main.carassistant.model;

public class Weather {

    private Integer mTemp;
    private String mIconCode;
    public byte[] iconData;

    public Integer getTemp() {
        return mTemp;
    }

    public void setTemp(Integer temp) {
        this.mTemp = temp;
    }

    public String getIconCode() {
        return mIconCode;
    }

    public void setIconCode(String icon) {
        this.mIconCode = icon;
    }
}
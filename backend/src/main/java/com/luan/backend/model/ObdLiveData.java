package com.luan.backend.model;

import java.util.ArrayList;
import java.util.List;

public class ObdLiveData {

    private int rpm;
    private int speed;
    private int coolantTemp;
    private int throttle;
    private int fuelPressure;
    private double batteryVoltage;
    private boolean hasActiveDtc;

    //  新增：故障码列表
    private List<String> dtcList = new ArrayList<>();


    public int getRpm() {
        return rpm;
    }

    public void setRpm(int rpm) {
        this.rpm = rpm;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getCoolantTemp() {
        return coolantTemp;
    }

    public void setCoolantTemp(int coolantTemp) {
        this.coolantTemp = coolantTemp;
    }

    public int getThrottle() {
        return throttle;
    }

    public void setThrottle(int throttle) {
        this.throttle = throttle;
    }

    public int getFuelPressure() {
        return fuelPressure;
    }

    public void setFuelPressure(int fuelPressure) {
        this.fuelPressure = fuelPressure;
    }

    public double getBatteryVoltage() {
        return batteryVoltage;
    }

    public void setBatteryVoltage(double batteryVoltage) {
        this.batteryVoltage = batteryVoltage;
    }

    public boolean isHasActiveDtc() {
        return hasActiveDtc;
    }

    public void setHasActiveDtc(boolean hasActiveDtc) {
        this.hasActiveDtc = hasActiveDtc;
    }

    // ⭐ 新增 getter/setter
    public List<String> getDtcList() {
        return dtcList;
    }

    public void setDtcList(List<String> dtcList) {
        this.dtcList = dtcList;
    }
}

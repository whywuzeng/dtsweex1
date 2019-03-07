package com.weex.plugins.baiduAMP;

import java.io.Serializable;

/**
 * Created by mac on 2018/12/18.
 */

public class MapBean implements Serializable {
    private String AccStatus;
    private String  lon;

    public String getAccStatus() {
        return AccStatus;
    }

    public void setAccStatus(String accStatus) {
        AccStatus = accStatus;
    }

    private String  lat;
    private String  DriverName;
    private String  TruckNo;
    private String  phoneNo;
    private String GpsTimeStamp;
    private String Velocity;
    private String SumMilesNum;
    private String  reasonname;
    private String   AbnormalMessage;
    private String AbnormalSTime;
    private String Smuite;
    private String IsRecover;
    private String warpoint;
    /**
     * 订单单号
     */
    private String Bill_Id;
    /**
     * 方向
     */
    private String Orientation;

    public String getReasonname() {
        return reasonname;
    }

    public void setReasonname(String reasonname) {
        this.reasonname = reasonname;
    }

    public String getAbnormalMessage() {
        return AbnormalMessage;
    }

    public void setAbnormalMessage(String abnormalMessage) {
        AbnormalMessage = abnormalMessage;
    }

    public String getAbnormalSTime() {
        return AbnormalSTime;
    }

    public void setAbnormalSTime(String abnormalSTime) {
        AbnormalSTime = abnormalSTime;
    }

    public String getSmuite() {
        return Smuite;
    }

    public void setSmuite(String smuite) {
        Smuite = smuite;
    }

    public String getIsRecover() {
        return IsRecover;
    }

    public void setIsRecover(String isRecover) {
        IsRecover = isRecover;
    }

    public String getWarpoint() {
        return warpoint;
    }

    public void setWarpoint(String warpoint) {
        this.warpoint = warpoint;
    }

    public String getSumMilesNum() {
        return SumMilesNum;
    }

    public void setSumMilesNum(String sumMilesNum) {
        SumMilesNum = sumMilesNum;
    }

    public String getVelocity() {
        return Velocity;
    }

    public void setVelocity(String velocity) {
        Velocity = velocity;
    }

    private String billstatusname;
    public String getLon() {
        return lon;
    }

    public String getBillstatusname() {
        return billstatusname;
    }

    public void setBillstatusname(String billstatusname) {
        this.billstatusname = billstatusname;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getDriverName() {
        return DriverName;
    }

    public void setDriverName(String driverName) {
        DriverName = driverName;
    }

    public String getTruckNo() {
        return TruckNo;
    }

    public void setTruckNo(String truckNo) {
        TruckNo = truckNo;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getGpsTimeStamp() {
        return GpsTimeStamp;
    }

    public void setGpsTimeStamp(String gpsTimeStamp) {
        GpsTimeStamp = gpsTimeStamp;
    }

    public String getBill_Id() {
        return Bill_Id;
    }

    public void setBill_Id(String bill_Id) {
        Bill_Id = bill_Id;
    }

    public String getOrientation() {
        return Orientation;
    }

    public void setOrientation(String orientation) {
        Orientation = orientation;
    }
}

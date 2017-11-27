package org.testtown.donggukeas_rd;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by restr on 2017-11-21.
 */

public class stuModel {
    String studentId, deviceToken,wifiInfo,pw,name;
    int attendanceStatus;
    public stuModel() {
    }

    public stuModel(String studentId, String deviceToken, String wifiInfo, int attendanceStatus, String pw, String name) {
        this.studentId = studentId;
        this.deviceToken = deviceToken;
        this.wifiInfo = wifiInfo;
        this.attendanceStatus = attendanceStatus;
        this.pw = pw;
        this.name = name;
    }

    public Map<String,Object> stu_dev_Map(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("studentId",studentId);
        result.put("deviceToken",deviceToken);
        return result;
    }
    public Map<String,Object>  stu_auth_Map(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("id",studentId);
        result.put("pw",pw);
        return result;
    }
    public Map<String,Object>  stu_wifi_Map(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("studentId",studentId);
        result.put("wifiInfo",wifiInfo);
        return result;
    }
    public Map<String,Object>  stu_Map(){
        HashMap<String,Object> result = new HashMap<>();
        HashMap<String,Object> result1 = new HashMap<>();
        result1.put("0","CSE4058-02");
        result1.put("1","CSE2017-01");

        result.put("studentId",studentId);
        result.put("studentName",name);
        result.put("listenSubject",result1);
        return result;
    }
    public Map<String,Object>  stu_atd_Map(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("studentId",studentId);
        result.put("attendanceStatus",attendanceStatus);
        return result;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getWifiInfo() {
        return wifiInfo;
    }

    public void setWifiInfo(String wifiInfo) {
        this.wifiInfo = wifiInfo;
    }

    public int getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(int attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

package dgu.donggukeas_client.model;

/**
 * Created by hanseungbeom on 2017. 11. 5..
 */

public class Student {
    private String studentId;
    private String deviceToken;
    private String[] classes;
    private String wifiInfo;

    public Student(String studentId, String deviceToken){
        this.studentId = studentId;
        this.deviceToken = deviceToken;
    }
    public Student(){

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

    public String[] getClasses() {
        return classes;
    }

    public void setClasses(String[] classes) {
        this.classes = classes;
    }

    public String getWifiInfo(){
        return wifiInfo;
    }

    public void setWifiInfo(String wifiInfo){ this.wifiInfo = wifiInfo;}

}

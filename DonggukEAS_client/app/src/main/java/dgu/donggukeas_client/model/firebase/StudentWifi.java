package dgu.donggukeas_client.model.firebase;

/**
 * Created by hanseungbeom on 2017. 11. 19..
 */

public class StudentWifi {
    private String studentId;

    private String wifiInfo;

    public StudentWifi(String studentId, String wifiInfo) {
        this.studentId = studentId;
        this.wifiInfo = wifiInfo;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getWifiInfo() {
        return wifiInfo;
    }

    public void setWifiInfo(String wifiInfo) {
        this.wifiInfo = wifiInfo;
    }

}

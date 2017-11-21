package dgu.donggukeas_client.model.firebase;

/**
 * Created by hanseungbeom on 2017. 11. 20..
 */

public class StudentDevice {
    private String studentId;
    private String deviceToken;

    public StudentDevice(){

    }
    public StudentDevice(String studentId, String deviceToken) {
        this.deviceToken = deviceToken;
        this.studentId = studentId;
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

}
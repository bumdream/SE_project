package dgu.donggukeas_admin.model.firebase;

/**
 * Created by hanseungbeom on 2017. 11. 15..
 */

public class StudentDevice {
    private String studentId;
    private String deviceToken;

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

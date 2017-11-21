package dgu.donggukeas_prof.model;

/**
 * Created by hanseungbeom on 2017. 11. 14..
 */

public class StudentInfo {
    private String studentId;
    private String studentName;
    private String deviceToken;
    private int attendanceStatus;

    public StudentInfo(String studentId){this.studentId = studentId;}
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public int getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(int attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

}
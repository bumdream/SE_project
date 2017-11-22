package dgu.donggukeas_client.model;

import java.util.List;

/**
 * Created by hanseungbeom on 2017. 11. 7..
 */

public class SubjectInfo {
    private String subjectCode;
    private String subjectName;
    private boolean isAttendanceChecking;

    public SubjectInfo(String subjectCode){
        this.subjectCode = subjectCode;
        isAttendanceChecking = false;
    }
    public SubjectInfo(){

    }

    public boolean isAttendanceChecking() {
        return isAttendanceChecking;
    }

    public void setAttendanceChecking(boolean attendanceChecking) {
        isAttendanceChecking = attendanceChecking;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }
}

package dgu.donggukeas_admin.model.firebase;

/**
 * Created by hanseungbeom on 2017. 11. 23..
 */

public class RunAwayStudent {
    private String studentId;
    private String updatedTime;

    public RunAwayStudent() {
    }

    public RunAwayStudent(String studentId, String updatedTime) {
        this.studentId = studentId;
        this.updatedTime = updatedTime;
    }

    public RunAwayStudent(String studentId) {
        this.studentId = studentId;
        updatedTime = "-1";
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }
}

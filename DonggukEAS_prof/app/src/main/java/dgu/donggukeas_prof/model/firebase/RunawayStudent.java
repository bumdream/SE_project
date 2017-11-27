package dgu.donggukeas_prof.model.firebase;

/**
 * Created by francisbae on 2017-11-23.
 *
 * public class RunawayStudent
 * 파이어베이스로부터 동기화할 출튀 예상 학생 정보

 */

public class RunawayStudent {
    private String studentId;
    private String updatedTime;

    public RunawayStudent() {
        //Log.d("#####","Runaway");
    }

    public RunawayStudent(String studentId) {
        this.studentId = studentId;
        updatedTime = "-1";
    }

    public RunawayStudent(String studentId, String updatedTime) {
        this.studentId = studentId;
        this.updatedTime = updatedTime;
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

package dgu.donggukeas_client.model.firebase;

/**
 * Created by hanseungbeom on 2017. 11. 14..
 */

//파이어베이스로부터 동기화할 학생 출석 정보
public class AttendanceStatus {
    private String studentId;
    private int attendanceStatus;

    public AttendanceStatus(){

    }
    public AttendanceStatus(String studentId, int attendanceStatus){
        this.studentId = studentId;
        this.attendanceStatus = attendanceStatus;
    }
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public int getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(int attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }
}

package dgu.donggukeas_prof.model.firebase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hanseungbeom on 2017. 11. 14..
 */

//파이어베이스로부터 동기화할 학생 출석 정보
public class AttendanceStatus {
    private String studentId;
    private int attendanceStatus;//0:미처리, 1:출석, 2:결석, 3:출튀

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

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("studentId",studentId);
        result.put("attendanceStatus",attendanceStatus);
        return result;
    }
}

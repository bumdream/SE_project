package dgu.donggukeas_prof.model;

/**
 * Created by francisbae on 2017-11-21.
 *
 *  public class RunawayInfo
 * 출튀 학생의 정보와 그 학생이 가진 와이파이 신호 정보를 멤버로 가지는 클래스
 * RUNAWAY_STUDENT 테이블과 STUDENT_ATTENDANCE 테이블을 조인하여 검색 후 값을 저장
 */

public class RunawayInfo {
    private String studentId;
    private String studentName;
    private String studentWifi;

    public RunawayInfo(String studentId, String studentWifi) {
        this.studentId = studentId;
        //this.studentName = studentName;
        this.studentWifi = studentWifi;
    }
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

    public String getStudentWifi(){ return studentWifi; }

    public void setStudentWifi(String studentWifi) { this.studentWifi = studentWifi; }

}

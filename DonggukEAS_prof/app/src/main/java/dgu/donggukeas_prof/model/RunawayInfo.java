package dgu.donggukeas_prof.model;

/**
 * Created by qowhd on 2017-11-21.
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

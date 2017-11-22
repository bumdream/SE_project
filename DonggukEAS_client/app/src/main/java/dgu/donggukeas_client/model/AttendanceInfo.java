package dgu.donggukeas_client.model;

/**
 * Created by hanseungbeom on 2017. 11. 22..
 */

public class AttendanceInfo {
    private int week;
    private int attendanceStatus;

    public AttendanceInfo() {
    }

    public AttendanceInfo(int attendanceStatus, int week) {
        this.attendanceStatus = attendanceStatus;
        this.week = week;
    }

    public int getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(int attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }
}

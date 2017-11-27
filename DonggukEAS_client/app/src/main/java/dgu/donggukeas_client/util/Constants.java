package dgu.donggukeas_client.util;

/**
 * Created by hanseungbeom on 2017. 11. 19..
 */

public class Constants {
    public static final int ATTENDANCE_NONE = 0; //미처리
    public static final int ATTENDANCE_OK = 1;  //출석
    public static final int ATTENDANCE_ABSENCE = 2;  //결석
    public static final int ATTENDANCE_LATE =3;
    public static final int ATTENDANCE_RUN = 4; //출튀
    public static final int studentNotFound = -1;
    public static final int subjectNotFound = -1;
    public static final int attendanceNotFound = -1;
    public static final String deviceNotRegisterd = "-1";
    ///
    public static final int ATTENDANCE_REQUEST = 0;
    public static final int RUNAWAY_REQUEST = 1;
    public static final int ATTENDANCE_RESULT = 2;

    ///
    public static final int SUBJECT_ATTENDANCE_ACTIVE=0;
    public static final int SUBJECT_ATTENDANCE_END=1;
    public static final int SUBJECT_ATTENDANCE_RUNAWAY_ACTIVE=2;
    public static final int SUBJECT_ATTENDANCE_RUNAWAY_END=3;
    public static final int SUBJECT_END=4;

}

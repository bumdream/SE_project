package dgu.donggukeas_admin.util;

/**
 * Created by hanseungbeom on 2017. 11. 10..
 */

public class Constants {
    //firebase push 메세지 전송을 위한 serverKey
    public static final String ServerKey = "AAAAfaRBKQA:APA91bF6EuyaA-z6PRwZT0hkQdWzIMp11RuC6XDTg7JuQYTxClQM0Aq2kzk_2D20GI8cRB8x-xIVNAHYoNs4Dyl-p_kXKkPdvGYnZD3-e2XGSTsKul12Hk_Jy7lztPl7uvRN5U6IXzct";
    public static final int ATTENDANCE_NONE = 0; //미처리
    public static final int ATTENDANCE_OK = 1;  //출석
    public static final int ATTENDANCE_ABSENCE = 2;  //결석
    public static final int ATTENDANCE_LATE =3;
    public static final int ATTENDANCE_RUN = 4; //출튀

    public static final int studentNotFound = -1;
    public static final String deviceNotRegisterd = "-1";


}

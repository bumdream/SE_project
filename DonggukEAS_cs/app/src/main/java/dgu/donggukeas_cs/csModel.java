package dgu.donggukeas_cs;

import java.util.HashMap;
import java.util.Map;

public class csModel {
    String studentId, deviceToken;

    public csModel() {
    }
    public csModel(String studentid, String deviceToken) {
        this.studentId=studentid;
        this.deviceToken=deviceToken;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("studentId",studentId);
        result.put("deviceToken",deviceToken);

        return result;
    }

 /*   public Map<String, Object> clearMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("studentId",studentId);
        result.put("deviceToken","-1");

        return result;
    }
*/
    public String getStudentid() {
        return studentId;
    }

    public void setStudentid(String studentid) {
        this.studentId = studentid;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}

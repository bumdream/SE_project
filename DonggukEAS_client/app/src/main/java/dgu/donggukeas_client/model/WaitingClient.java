package dgu.donggukeas_client.model;

/**
 * Created by hansb on 2017-09-06.
 */

public class WaitingClient {
    private String studentId;
    private String phoneId;

    public WaitingClient(){

    }

    public WaitingClient(String studentId, String phoneId) {
        this.studentId = studentId;
        this.phoneId = phoneId;
    }
    public String getStudentId() {
        return studentId;
    }

    public String getPhoneId() {
        return phoneId;
    }
}

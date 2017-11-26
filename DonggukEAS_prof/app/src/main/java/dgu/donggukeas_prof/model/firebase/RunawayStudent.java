package dgu.donggukeas_prof.model.firebase;

import android.util.Log;

/**
 * Created by qowhd on 2017-11-23.
 */

public class RunawayStudent {
    private String studentId;
    private String updatedTime;

    public RunawayStudent()
    {
        Log.d("#####","Runaway");
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

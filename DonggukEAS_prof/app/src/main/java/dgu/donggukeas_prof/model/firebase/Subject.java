package dgu.donggukeas_prof.model.firebase;

import android.util.Log;

import java.util.List;

/**
 * Created by hanseungbeom on 2017. 11. 7..
 */

public class Subject {
    String subjectCode;
    String subjectName;
    List<String> listenStudent;
    String days;

    public List<String> getListenStudent() {
        return listenStudent;
    }

    public void setListenStudent(List<String> listenStudent) {
        this.listenStudent = listenStudent;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public Subject()
    {
        Log.d("#####","subject1");
    }

    public Subject(String subjectName, String subjectCode) {
        this.subjectName = subjectName;
        this.subjectCode = subjectCode;
        Log.d("#####","subject생성");

    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }
}

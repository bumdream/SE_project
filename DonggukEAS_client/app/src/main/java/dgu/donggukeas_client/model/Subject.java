package dgu.donggukeas_client.model;

import java.util.List;

/**
 * Created by hanseungbeom on 2017. 11. 7..
 */

public class Subject {
    String subjectCode;
    String subjectName;
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

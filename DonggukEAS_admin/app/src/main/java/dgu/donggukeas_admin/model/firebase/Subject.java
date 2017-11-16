package dgu.donggukeas_admin.model.firebase;

import java.util.List;

/**
 * Created by hanseungbeom on 2017. 11. 14..
 */

public class Subject {
    private String subjectCode;
    private String subjectName;
    private List<String> listenStudent;

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }
    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public List<String> getListenStudent() {
        return listenStudent;
    }

    public void setListenStudent(List<String> listenStudent) {
        this.listenStudent = listenStudent;
    }
}

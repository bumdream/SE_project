package dgu.donggukeas_client.model.firebase;

import java.util.List;

/**
 * Created by hanseungbeom on 2017. 11. 5..
 */

public class Student {
    private String studentId;
    private String studentName;
    private List<String> listenSubject;

    public List<String> getListenSubject() {
        return listenSubject;
    }

    public void setListenSubject(List<String> listenSubject) {
        this.listenSubject = listenSubject;
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
}

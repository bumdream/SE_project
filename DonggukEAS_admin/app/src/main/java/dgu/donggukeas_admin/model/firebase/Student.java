package dgu.donggukeas_admin.model.firebase;

import java.util.List;

/**
 * Created by hanseungbeom on 2017. 11. 14..
 */

//디비로 부터 동기화할 학생 디바이스 정보 및 wifi정보
public class Student {
    String studentId;
    String studentName;
    List<String> listenSubject;

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

    public List<String> getListenSubject() {
        return listenSubject;
    }

    public void setListenSubject(List<String> listenSubject) {
        this.listenSubject = listenSubject;
    }


}

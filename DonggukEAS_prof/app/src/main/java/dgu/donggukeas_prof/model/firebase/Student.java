package dgu.donggukeas_prof.model.firebase;

import java.util.List;

/**
 * Created by hanseungbeom on 2017. 11. 5..
 *
 * public class Student
 * 파이어베이스로부터 동기화할 학생 정보
 */

public class Student {
    String studentId;
    String studentName;
    List<String> listenStudent;

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

    public List<String> getListenStudent() {
        return listenStudent;
    }

    public void setListenStudent(List<String> listenStudent) {
        this.listenStudent = listenStudent;
    }
}

package dgu.donggukeas_prof.model.firebase;

import java.util.List;

/**
 * Created by qowhd on 2017. 11. 16..
 */

public class Professor {
    private String professorId;
    private String professorName;
    private List<String> lectureSubject;

    //private String wifiInfo;

    public Professor(String professorId) {
        this.professorId = professorId;
    }

    public Professor() {
    }

    public String getProfessorId() {
        return professorId;
    }

    public void setProfessorId(String professorId) {
        this.professorId = professorId;
    }

    public String getProfessorName() {
        return professorName;
    }

    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }

    public List<String> getLectureSubject() {
        return lectureSubject;
    }

    public void setLectureSubject(List<String> lectureSubject) {
        this.lectureSubject = lectureSubject;
    }
}
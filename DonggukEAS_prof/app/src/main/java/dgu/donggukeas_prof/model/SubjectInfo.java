package dgu.donggukeas_prof.model;

/**
 * Created by francisbae on 2017-11-17.

 * public class SubjectInfo
 * 교수가 가르치는 강좌와 그 학생수를 멤버로 가지는 클래스
 * SUBJECT 테이블과 STUDENT 테이블을 조인하여 검색 후 값을 저장
 */

public class SubjectInfo {
    private String subjectCode;
    private String subjectName;
    private int numOfStudents;

    public SubjectInfo(String subjectCode) {
        this.subjectCode = subjectCode;
    }

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

    public int getNumOfStudents() {
        return numOfStudents;
    }

    public void setNumOfStudents(int numOfStudents) {
        this.numOfStudents = numOfStudents;
    }
}

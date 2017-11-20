package dgu.donggukeas_prof.model;

/**
 * Created by francisbae on 2017-11-17.
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

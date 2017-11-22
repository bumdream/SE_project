package dgu.donggukeas_client.model.firebase;

/**
 * Created by hanseungbeom on 2017. 11. 6..
 */

public class StudentAuth {
    private String id;
    private String pw;

    public StudentAuth(String id, String pw){
        this.id = id;
        this.pw = pw;
    }
    public StudentAuth(){

    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

}

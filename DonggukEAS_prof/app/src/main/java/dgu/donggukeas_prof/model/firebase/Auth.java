package dgu.donggukeas_prof.model.firebase;

/**
 * Created by hanseungbeom on 2017. 11. 6..
 *
 * public class Auth
 * 파이어베이스로부터 동기화할 교수 권한 정보
 */

public class Auth {
    private String id;
    private String pw;

    public Auth(){

    }

    public Auth(String id,String pw){
        this.id = id;
        this.pw = pw;
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

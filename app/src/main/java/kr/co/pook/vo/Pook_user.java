package kr.co.pook.vo;

public class Pook_user {
    private String user_id;
    private String nickname;
    private String phone;
    private String result;

    public Pook_user(String user_id,String nickname, String phone){
        this.user_id = user_id;
        this.nickname = nickname;
        this.phone = phone;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPhone() {
        return phone;
    }

    public String getResult() {
        return result;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setResult(String result) {
        this.result = result;
    }
}

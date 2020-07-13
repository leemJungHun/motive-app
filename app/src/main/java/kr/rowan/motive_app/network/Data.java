package kr.rowan.motive_app.network;

public class Data {
    private String code;
    private String name;
    private String addr;
    private String tel;
    private String admin;
    private String id;

    public Data(String code, String name, String addr, String tel, String admin, String id, String registrationDate){
        this.code = code;
        this.name = name;
        this.addr = addr;
        this.tel = tel;
        this.admin = admin;
        this.id = id;
        this.registrationDate = registrationDate;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getAddr() {
        return addr;
    }

    public String getTel() {
        return tel;
    }

    public String getAdmin() {
        return admin;
    }

    public String getId() {
        return id;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    private String registrationDate;
}

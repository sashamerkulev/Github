package ru.merkulyevsasha.github.models;


public class Credentials {

    private String login;
    private String password;

    public Credentials(String login, String password){
        this.setLogin(login);
        this.setPassword(password);
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

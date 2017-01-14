
package ru.merkulyevsasha.github.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Auth {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("app")
    @Expose
    private App app;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("hashed_token")
    @Expose
    private String hashedToken;
    @SerializedName("token_last_eight")
    @Expose
    private String tokenLastEight;
    @SerializedName("note")
    @Expose
    private Object note;
    @SerializedName("note_url")
    @Expose
    private Object noteUrl;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("scopes")
    @Expose
    private List<String> scopes = null;
    @SerializedName("fingerprint")
    @Expose
    private Object fingerprint;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getHashedToken() {
        return hashedToken;
    }

    public void setHashedToken(String hashedToken) {
        this.hashedToken = hashedToken;
    }

    public String getTokenLastEight() {
        return tokenLastEight;
    }

    public void setTokenLastEight(String tokenLastEight) {
        this.tokenLastEight = tokenLastEight;
    }

    public Object getNote() {
        return note;
    }

    public void setNote(Object note) {
        this.note = note;
    }

    public Object getNoteUrl() {
        return noteUrl;
    }

    public void setNoteUrl(Object noteUrl) {
        this.noteUrl = noteUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }

    public Object getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(Object fingerprint) {
        this.fingerprint = fingerprint;
    }

}

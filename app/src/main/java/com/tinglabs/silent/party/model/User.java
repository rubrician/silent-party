package com.tinglabs.silent.party.model;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.io.Serializable;
import java.util.Random;

/**
 * Created by Talal on 12/26/2016.
 */

public class User extends SugarRecord implements Serializable {

    // @Formatter:off
    @Ignore
    public static final String PROP_NAME                 = "name";
    @Ignore
    public static final String PROP_USER_NAME            = "user_name";
    @Ignore
    public static final String PROP_PASSWORD             = "password";
    // @Formatter:on

    private String name;
    private String userName;
    private String password;
    private String email;
    private String description;
    private String picUrl;
    private String role = Role.NONE;
    private String userIp;
    private String url;

    public class Role {
        public static final String ORGANIZER = "organizer";
        public static final String MEMBER = "member";
        public static final String NONE = "none";
    }

    public User() {
    }

    public User(String name, String userName, String email) {
        this.name = name;
        this.userName = userName;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isOrganizer() {
        return Role.ORGANIZER.equals(role);
    }

    public boolean isMember() {
        return Role.MEMBER.equals(role);
    }

    public static User find(String userName, String password) {
        return Select.from(User.class).where(Condition.prop(PROP_USER_NAME).eq(userName), Condition.prop(PROP_PASSWORD).eq(password)).first();
    }

    public static User createAnonymous() {
        int i = new Random().nextInt(1000);
        return new User("Anonymous User-" + i, "Nick-" + i, "Anonymous-" + i);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        if (!((User) o).getUserIp().equals(userIp)) return false;
        if (!((User) o).getUserName().equals(userName)) return false;

        return true;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", description='" + description + '\'' +
                ", picUrl='" + picUrl + '\'' +
                ", role='" + role + '\'' +
                ", userIp='" + userIp + '\'' +
                ", url='" + url + '\'' +
                "} " + super.toString();
    }
}

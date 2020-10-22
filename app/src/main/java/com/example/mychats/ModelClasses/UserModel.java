package com.example.mychats.ModelClasses;

public class UserModel {

    private String Name, ProfileLink, Status;

    UserModel(){

    }

    public UserModel(String name, String profileLink, String status) {
        Name = name;
        ProfileLink = profileLink;
        Status = status;
    }

    public String getName() {
        return Name;
    }

    public String getProfileLink() {
        return ProfileLink;
    }

    public String getStatus() {
        return Status;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setProfileLink(String profileLink) {
        ProfileLink = profileLink;
    }

    public void setStatus(String status) {
        Status = status;
    }
}

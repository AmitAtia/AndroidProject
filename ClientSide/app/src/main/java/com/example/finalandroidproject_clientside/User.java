package com.example.finalandroidproject_clientside;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.regex.Pattern;

public class User {
    public static final String SEPERATOR = "-";
    private static final String USERNAME_PATTERN = "^[a-z0-9_-]{3,15}$";
    private static final String PASSWORD_PATTERN = "^[a-z0-9]{3,15}$";
    private String username, password;
    private String photoPath;

    public User(String userAsString) {
        if (userAsString.isEmpty())
            throw new InvalidParameterException();
        String[] parts = userAsString.split(SEPERATOR);
        if (parts.length != 3)
            throw new InvalidParameterException("must have three parts");
        this.username = parts[0];
        this.password = parts[1];
        this.photoPath = parts[2];
    }

    public User(String username, String password, String photoPath) {
        this.username = username;
        this.password = password;
        this.photoPath = photoPath;
    }

    public String isValid() {
        if(!Pattern.compile(USERNAME_PATTERN).matcher(username).matches())
            return "שם משתמש לא תקין";
        if(!Pattern.compile(PASSWORD_PATTERN).matcher(password).matches())
            return "סיסמה לא תקינה";
        File image = new File(photoPath);
        if (!image.exists())
            return "תמונה לא נמצאה";
        return "valid";
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    @Override
    public String toString() {
        return username + SEPERATOR + password + SEPERATOR + photoPath;
    }

}

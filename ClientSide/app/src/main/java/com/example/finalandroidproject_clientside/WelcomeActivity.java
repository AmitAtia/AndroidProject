package com.example.finalandroidproject_clientside;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class WelcomeActivity extends Activity {

    TextView usernameLabel;
    ImageView userImageLabel;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        usernameLabel = findViewById(R.id.usernameLabel);
        userImageLabel = findViewById(R.id.userImageLabel);
        SharedPreferences prefs = getSharedPreferences(MainActivity.PREFS, MODE_PRIVATE);
        if(prefs.contains(MainActivity.USER)) {
            String userAsString = prefs.getString(MainActivity.USER, null);
            user = new User(userAsString);
            usernameLabel.setText(user.getUsername());
            setPic();
        }
    }

    public void btnLogoutClicked(View view) {
        SharedPreferences prefs = getSharedPreferences(MainActivity.PREFS, MODE_PRIVATE);
        prefs.edit().remove(MainActivity.USER).commit();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void setPic(){
        int imageWidth = 70;
        int imageHeight = 70;
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(user.getPhotoPath(), bitmapOptions);
        int photoWidth = bitmapOptions.outWidth;
        int photoHeight = bitmapOptions.outHeight;
        int scaleFactor = Math.min(photoWidth / imageWidth, photoHeight / imageHeight);
        bitmapOptions.inJustDecodeBounds = false;
        bitmapOptions.inSampleSize = scaleFactor;
        Bitmap bitmap = BitmapFactory.decodeFile(user.getPhotoPath(), bitmapOptions);
        userImageLabel.setImageBitmap(bitmap);
    }
}

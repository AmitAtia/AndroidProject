package com.example.finalandroidproject_clientside;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static final String BASE_URL = "http://192.168.1.28:8080/MainServlet";
    public static final String WRONG_PARAMETER = "WP";
    public static final String OK = "OK";
    public static final int REQUEST_CODE = 123;
    public static final String PREFS = "prefs";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String PHOTO_PATH = "photoPath";
    public static final String USER = "user";
    EditText usernameLbl, passwordLbl;
    TextView signUpAlert;
    ImageView imageView;
    String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usernameLbl = findViewById(R.id.username);
        passwordLbl = findViewById(R.id.password);
        imageView = findViewById(R.id.photoView);
        signUpAlert = findViewById(R.id.signUpAlert);

        String userString = getSharedPreferences(PREFS, MODE_PRIVATE).getString(USER, null);
        if(userString != null)
            goToWelcomeActivity();
    }

    public void userSignUp(View view) {
        User user = getUserFromUI();
        if(user == null)
            return;
        String isValid = user.isValid();
        if(user != null && isValid.equals("valid")) {
            getSharedPreferences(PREFS, MODE_PRIVATE).edit().putString(USER, user.toString()).commit();
            signup(user.toString());
        } else {
            signUpAlert.setText(isValid);
        }
    }

    public void btnLoginFragment(View view) {
        LoginFragment loginFragment = new LoginFragment();
        loginFragment.setListener(new LoginFragment.OnLoginFragmentListener() {
            @Override
            public void onLogin(String username, String password, TextView alert) {
                login(username, password, alert);
            }
        });
        loginFragment.show(getFragmentManager(), "");
    }

    public User getUserFromUI() {
        String usern = usernameLbl.getText().toString();
        String passw = passwordLbl.getText().toString();
        if(usern.isEmpty() || passw.isEmpty() || photoPath == null) {
            signUpAlert.setText("מלא את כל השדות");
            return null;
        }
        return new User(usern, passw, photoPath);
    }

    public void goToWelcomeActivity() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String username = usernameLbl.getText().toString();
        if(!username.isEmpty())
            outState.putString(USERNAME, username);
        String password = passwordLbl.getText().toString();
        if(!password.isEmpty())
            outState.putString(PASSWORD, password);
        if(photoPath != null)
            outState.putString(PHOTO_PATH, photoPath);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.containsKey("username"))
            usernameLbl.setText(savedInstanceState.getString("username"));
        if(savedInstanceState.containsKey("password"))
            passwordLbl.setText(savedInstanceState.getString("password"));
        if(savedInstanceState.containsKey("photoPath")) {
            photoPath = savedInstanceState.getString("photoPath");
            imageView.setVisibility(imageView.VISIBLE);
            setPic();
        }
    }

    /*
     * Image Section
     */


    public void btnTakePhoto(View view) {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePhotoIntent.resolveActivity(getPackageManager()) != null) {
            File file = createImageFile();
            Uri photoUri = FileProvider.getUriForFile(this, "com.example.finalandroidproject_clientside.fileprovider", file);
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(takePhotoIntent, REQUEST_CODE);
        }
    }

    private File createImageFile(){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(imageFileName,".jpg",storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        photoPath = image.getAbsolutePath();
        return image;
    }

    private void setPic(){
        int imageWidth = 200;
        int imageHeight = 200;
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bitmapOptions);
        int photoWidth = bitmapOptions.outWidth;
        int photoHeight = bitmapOptions.outHeight;
        int scaleFactor = Math.min(photoWidth / imageWidth, photoHeight / imageHeight);
        bitmapOptions.inJustDecodeBounds = false;
        bitmapOptions.inSampleSize = scaleFactor;
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bitmapOptions);
        imageView.setImageBitmap(bitmap);
    }

    private void savePictureToMobile() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(photoPath);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            imageView.setVisibility(imageView.VISIBLE);
            setPic();
            savePictureToMobile();
        }
    }


    /*
     * Connect to database
     * Sign up or Login
     */

    // Sign up
    @SuppressLint("StaticFieldLeak")
    public void signup(String userString) {
        new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(String... strings) {
                URL url = null;
                InputStream inputStream = null;
                HttpURLConnection connection = null;
                try {
                    String urlString = BASE_URL + "?action=signup&userAsString=" + strings[0];
                    url = new URL(urlString);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setUseCaches(false);
                    connection.setDoOutput(false);
                    connection.setRequestMethod("GET");
                    connection.connect();
                    inputStream = connection.getInputStream();
                    byte[] buffer = new byte[1024];
                    int actuallyRead = inputStream.read(buffer);
                    String responseFromServer = null;
                    if(actuallyRead != -1)
                        responseFromServer = new String(buffer, 0, actuallyRead);
                    if(responseFromServer.equals(OK))
                        return true;
                    return false;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if(aBoolean) {
                    goToWelcomeActivity();
                    finish();
                } else {
                    signUpAlert.setText("שם משתמש תפוס");
                }
            }
        }.execute(userString);
    }

    // Login
    @SuppressLint("StaticFieldLeak")
    public void login(String username, String password, final TextView alert) {
        new AsyncTask<Object, Integer, String>() {
            @Override
            protected String doInBackground(Object... objects) {
                URL url = null;
                InputStream inputStream = null;
                HttpURLConnection connection = null;
                try {
                    String urlString = BASE_URL + "?action=login&username=" + String.valueOf(objects[0]) + "&password=" + String.valueOf(objects[1]);
                    url = new URL(urlString);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setUseCaches(false);
                    connection.setDoOutput(false);
                    connection.setRequestMethod("GET");
                    connection.connect();
                    inputStream = connection.getInputStream();
                    byte[] buffer = new byte[1024];
                    StringBuilder stringBuilder = new StringBuilder();
                    int actuallyRead;
                    while ((actuallyRead = inputStream.read(buffer)) != -1){
                        stringBuilder.append(new String(buffer, 0, actuallyRead));
                    }
                    String response = stringBuilder.toString();
                    Log.d("Amit",response);
                    return response;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
                return WRONG_PARAMETER;
            }

            @Override
            protected void onPostExecute(String response) {
                if(response.equals(WRONG_PARAMETER))
                    alert.setText("פרטי המשתמש שגויים");
                else {
                    getSharedPreferences(PREFS, MODE_PRIVATE).edit().putString(USER, new User(response).toString()).commit();
                    goToWelcomeActivity();
                }

            }
        }.execute(username, password, alert);
    }
}

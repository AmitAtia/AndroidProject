package com.example.finalandroidproject_clientside;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LoginFragment extends DialogFragment {

    private RelativeLayout fragmentTitleLayout;
    private Button closeBtn;
    private String userName;
    private EditText txtUserName;
    private EditText txtPassword;
    private TextView alert;
    private Button btnLogin;
    private OnLoginFragmentListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        fragmentTitleLayout = view.findViewById(R.id.fragmentTitleLayout);
        closeBtn = fragmentTitleLayout.findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        txtUserName = view.findViewById(R.id.txtUserName);
        txtPassword = view.findViewById(R.id.txtPassword);
        alert = view.findViewById(R.id.loginAlert);
        btnLogin = view.findViewById(R.id.btnLogin);
        if(userName != null) {
            txtUserName.setText(userName);
            txtPassword.requestFocus();
        }else
            txtUserName.requestFocus();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = txtUserName.getText().toString();
                String password = txtPassword.getText().toString();
                if(userName.isEmpty() || password.isEmpty()){
                    alert.setText("שם משתמש וסיסמה נדרשים");
                    return;
                }
                if(listener != null)
                    listener.onLogin(userName, password, alert);
            }
        });

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return view;
    }

    public void setListener(OnLoginFragmentListener listener) {
        this.listener = listener;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public interface OnLoginFragmentListener{
        void onLogin(String userName, String password, TextView alert);
    }
}
package com.tinglabs.silent.party.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.tinglabs.silent.party.R;
import com.tinglabs.silent.party.model.User;
import com.tinglabs.silent.party.util.PreferenceUtils;
import shem.com.materiallogin.DefaultLoginView;
import shem.com.materiallogin.DefaultRegisterView;
import shem.com.materiallogin.MaterialLoginView;

public class AuthActivity extends AppCompatActivity {

    public static final String TAG = "AuthActivity";

    @BindView(R.id.login)
    MaterialLoginView mLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);


        if (PreferenceUtils.isRemembered(this)) {
            startActivity(new Intent(this, ControllerActivity.class));
            finish();
        }

        ((DefaultLoginView) mLogin.getLoginView()).setListener(new DefaultLoginView.DefaultLoginViewListener() {
            @Override
            public void onLogin(TextInputLayout loginUser, TextInputLayout loginPass) {
                String userName = loginUser.getEditText().getText().toString();
                if (userName.isEmpty()) {
                    loginUser.setError("User name can't be empty");
                    return;
                }
                loginUser.setError("");

                String password = loginPass.getEditText().getText().toString();
                User user = User.find(userName, password);
                if (user == null) {
                    loginPass.setError("Wrong username or password");
                    return;
                }
                loginPass.setError("");
                Snackbar.make(mLogin, "Login success!", Snackbar.LENGTH_LONG).show();
                startActivity(new Intent(AuthActivity.this, ControllerActivity.class));
                finish();
            }
        });

        ((DefaultRegisterView) mLogin.getRegisterView()).setListener(new DefaultRegisterView.DefaultRegisterViewListener() {
            @Override
            public void onRegister(TextInputLayout registerUser, TextInputLayout registerPass, TextInputLayout registerPassRep) {
                User user = new User();
                String userName = registerUser.getEditText().getText().toString();
                if (userName.isEmpty()) {
                    registerUser.setError("User name can't be empty");
                    return;
                }
                registerUser.setError("");

                String password = registerPass.getEditText().getText().toString();
                if (password.isEmpty()) {
                    registerPass.setError("Password can't be empty");
                    return;
                }
                registerPass.setError("");

                String passRep = registerPassRep.getEditText().getText().toString();
                if (!password.equals(passRep)) {
                    registerPassRep.setError("Passwords are different");
                    return;
                }
                registerPassRep.setError("");
                user.setName(userName);
                user.setUserName(userName);
                user.setPassword(password);
                user.save();
                startActivity(new Intent(AuthActivity.this, ControllerActivity.class));
                Snackbar.make(mLogin, "Register success!", Snackbar.LENGTH_LONG).show();
                finish();
            }
        });
    }
}
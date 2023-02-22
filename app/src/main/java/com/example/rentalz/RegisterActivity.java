package com.example.rentalz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rentalz.Backend.DatabaseHelper;

public class RegisterActivity extends AppCompatActivity {
    DatabaseHelper db;
    EditText txtPhone, txtPassword, txtConfPassword;
    TextView lblLogin;
    Button btnCreate;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        db = new DatabaseHelper(this);
        txtPhone = findViewById(R.id.txt_reg_phone_number);
        txtPassword = findViewById(R.id.txt_reg_password);
        txtConfPassword = findViewById(R.id.txt_conf_password);
        lblLogin = findViewById(R.id.link_login);
        btnCreate = findViewById(R.id.btn_register);
        loadingBar = new ProgressDialog(this);

        lblLogin.setOnClickListener((View view)-> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        btnCreate.setOnClickListener((View view)-> verifyUser());

    }

    // function that verifies a user

    private void verifyUser() {

        String phone = txtPhone.getText().toString();
        String password1 = txtPassword.getText().toString();
        String password2 = txtConfPassword.getText().toString();

        if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(this, "Please enter phone number", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password1))
        {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password2))
        {
            Toast.makeText(this, "Please enter confirmation password", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please wait, while we check your credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            createUser();
        }
    }


// function that creates a user

    private void createUser() {

        String phone = txtPhone.getText().toString().trim();
        String password1 = txtPassword.getText().toString().trim();
        String password2 = txtConfPassword.getText().toString().trim();

        if (password1.equals(password2))
        {
            long val = db.addUser(phone, password1);
            if(val > 0)
            {
                Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(this, "Registration Error", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
        else {
            Toast.makeText(this, "Password does not much", Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();
        }

    }



}
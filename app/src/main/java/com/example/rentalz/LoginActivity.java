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
import com.example.rentalz.Backend.SQLiteHelper;


public class LoginActivity extends AppCompatActivity {

    EditText txtPhone, txtPassword;
    Button btnLogin;
    TextView lblRegister;
    DatabaseHelper db;
    public static SQLiteHelper mSQLiteHelper;

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //creating database
        mSQLiteHelper = new SQLiteHelper(this, "RECORDDB.sqlite", null, 1);


        //creating table in database
        mSQLiteHelper.queryData("CREATE TABLE IF NOT EXISTS RECORD(id INTEGER PRIMARY KEY AUTOINCREMENT, propType VARCHAR, bedrooms VARCHAR, date VARCHAR, time VARCHAR, rentPrice VARCHAR, furnType VARCHAR, notes VARCHAR, reporter VARCHAR, image BLOB)");

        db = new DatabaseHelper(this);
        txtPhone = findViewById(R.id.txt_phone_number);
        txtPassword = findViewById(R.id.txt_password);
        lblRegister = findViewById(R.id.link_register);
        btnLogin = findViewById(R.id.btn_login);
        loadingBar = new ProgressDialog(this);

        lblRegister.setOnClickListener((View view)->{
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener((View view) -> validateUser());



    }

    private void validateUser() {
        String phone = txtPhone.getText().toString();
        String password = txtPassword.getText().toString();

        if (TextUtils.isEmpty(phone))
        {
            Toast.makeText(this, "Please enter phone number", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait, while we check your credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            loginUser();
        }
    }

    private void loginUser()
    {
        String phone = txtPhone.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        Boolean res = db.checkUser(phone, password);

        if (res == true)
        {
            Toast.makeText(this, "Logged in successfully", Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();
            Intent intent = new Intent(LoginActivity.this, PropertyListActivity.class);
            startActivity(intent);
            fileList();
        }
        else
        {
            Toast.makeText(this, "Account Doesn't Exist, Error 404", Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();
        }
    }
}
package com.example.app_ee3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

//todo
// logo on app with name (ResponseSync/FireSync/FireLink)
public class Register extends AppCompatActivity {
    private Button btnReg;
    private EditText txtBadge;
    private EditText txtFirstName;
    private EditText txtSurName;
    private EditText txtPassword;
    private EditText txtConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        btnReg = (Button) findViewById(R.id.btnReg);
        txtBadge = (EditText) findViewById(R.id.txtBadge_reg);
        txtBadge = (EditText) findViewById(R.id.txtFirstName);
        txtBadge = (EditText) findViewById(R.id.txtSurName);
        txtPassword = (EditText) findViewById(R.id.txtPassword_reg);
        txtConfirmPassword = (EditText) findViewById(R.id.txtConfirmPassword);

        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.txtPassword_reg), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void onBtnRegister_Clicked(View Caller) {
        Intent intent = new Intent(this, BottomNavigation.class);
        startActivity(intent);
    }
}
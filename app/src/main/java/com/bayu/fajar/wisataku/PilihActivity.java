package com.bayu.fajar.wisataku;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.bayu.fajar.wisataku.Admin.HomeAdmin;
import com.bayu.fajar.wisataku.Admin.LoginAdmin;
import com.bayu.fajar.wisataku.Wisatawan.Login;

public class PilihActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilih);
    }

    public void user (View view) {
        Intent user = new Intent(getApplicationContext(), Login.class);
        startActivity(user);
    }

    public void admin (View view) {
        Intent admin = new Intent(getApplicationContext(), LoginAdmin.class);
        startActivity(admin);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}

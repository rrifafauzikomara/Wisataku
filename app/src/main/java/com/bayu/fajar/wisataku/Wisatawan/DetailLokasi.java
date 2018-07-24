package com.bayu.fajar.wisataku.Wisatawan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.bayu.fajar.wisataku.R;

public class DetailLokasi extends AppCompatActivity {

    TextView txt_nama, txt_tgl, txt_time, txt_desk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_lokasi);

        txt_nama = findViewById(R.id.nama);
        txt_tgl = findViewById(R.id.tgl);
        txt_time = findViewById(R.id.time);
        txt_desk = findViewById(R.id.deskripsi);

        txt_nama.setText(getIntent().getStringExtra("nama"));
        txt_tgl.setText(getIntent().getStringExtra("tgl"));
        txt_time.setText(getIntent().getStringExtra("time"));
        txt_desk.setText(getIntent().getStringExtra("deskripsi"));

        //membuat back button toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Detail Wisata");
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    //membuat fungsi back dengan mengirim data session
    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
    }
}

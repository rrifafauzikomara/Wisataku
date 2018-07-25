package com.bayu.fajar.wisataku.Admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bayu.fajar.wisataku.R;
import com.bayu.fajar.wisataku.Server.RequestHandler;
import com.bayu.fajar.wisataku.Server.Server;
import com.bayu.fajar.wisataku.Login;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeAdmin extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private EndDrawerToggle drawerToggle;
    String idx, level;

    //untuk login session
    SharedPreferences sharedpreferences;
    public static final String TAG_ID = "id_user";
    public final static String TAG_NAMA = "nama";
    public final static String TAG_EMAIL = "email";
    public final static String TAG_FOTO = "foto";
    public final static String TAG_LEVEL = "level";

    //untuk show profile
    private String urlp = Server.showProfilA;
    TextView tv_nama, tv_email;
    CircleImageView user_picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_admin);
        toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        sharedpreferences = getSharedPreferences(Login.my_shared_preferences, Context.MODE_PRIVATE);
        idx = sharedpreferences.getString(TAG_ID, null);
        level = sharedpreferences.getString(TAG_LEVEL, null);

        initNavigationDrawer();

    }

    public void initNavigationDrawer() {
        NavigationView navigationView = (NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id){
                    case R.id.nav_admin_profile:
                        Intent intent = new Intent(HomeAdmin.this, AdminProfile.class);
                        intent.putExtra(TAG_ID, idx);
                        startActivity(intent);
                        break;
                    case R.id.nav_admin_maps:
                        Intent maps = new Intent(HomeAdmin.this, MapsActivity.class);
                        maps.putExtra(TAG_ID, idx);
                        startActivity(maps);
                        break;
                    case R.id.nav_admin_list:

                        break;
                    case R.id.nav_admin_logout:
                        logout();
                }
                drawerLayout.closeDrawer(GravityCompat.END);
                return true;
            }
        });

        View header = navigationView.getHeaderView(0);
        tv_nama = (TextView)header.findViewById(R.id.nama);
        tv_email = (TextView)header.findViewById(R.id.email);
        user_picture = header.findViewById(R.id.profile_image);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);
        drawerToggle = new EndDrawerToggle(this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(drawerToggle);

        getUserProfile();

    }

    private void getUserProfile(){
        class GetUser extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(HomeAdmin.this,"Tunggu",".....",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                showUser(s);
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(urlp,idx);
                return s;
            }
        }
        GetUser gu = new GetUser();
        gu.execute();
    }

    private void showUser(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray result = jsonObject.getJSONArray("result");
            JSONObject c = result.getJSONObject(0);
            String nama = c.getString(TAG_NAMA);
            String email = c.getString(TAG_EMAIL);
            String foto = c.getString(TAG_FOTO);

            tv_nama.setText(nama);
            tv_email.setText(email);
            Picasso.with(this).load(foto).placeholder(R.drawable.user).error(R.drawable.user).into(user_picture);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void logout(){
        //Creating an alert dialog to confirm logout
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Anda yakin ingin logout ?");
        alertDialogBuilder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(Login.session_status, false);
                editor.putString(TAG_ID, null);
                editor.putString(TAG_NAMA, null);
                editor.putString(TAG_EMAIL, null);
                editor.putString(TAG_FOTO, null);
                editor.commit();
                Intent intent = new Intent(HomeAdmin.this, Login.class);
                finish();
                startActivity(intent);
            }
        });

        alertDialogBuilder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });

        //Showing the alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    @Override
    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.END)){
            drawerLayout.closeDrawer(GravityCompat.END);
        }else{
            moveTaskToBack(true);
        }
    }

}
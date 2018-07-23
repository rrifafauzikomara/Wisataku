package com.bayu.fajar.wisataku.Admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bayu.fajar.wisataku.PilihActivity;
import com.bayu.fajar.wisataku.R;
import com.bayu.fajar.wisataku.Server.AppController;
import com.bayu.fajar.wisataku.Server.Server;
import com.bayu.fajar.wisataku.Wisatawan.Login;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginAdmin extends AppCompatActivity {

    //untuk login session
    ProgressDialog pDialog;
    Button btn_login;
    EditText txt_email, txt_password;
    int success;
    ConnectivityManager conMgr;
    private String url = Server.URLA + "loginAdmin.php";
    private static final String TAG = LoginAdmin.class.getSimpleName();
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    public final static String TAG_ID = "id_admin";
    public final static String TAG_EMAIL = "email";

    String tag_json_obj = "json_obj_req";
    SharedPreferences sharedpreferencesA;
    Boolean session = false;
    String id, email;
    public static final String my_shared_preferences = "my_shared_preferences";
    public static final String session_status = "session_status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_admin);

        btn_login = findViewById(R.id.btn_login);
        txt_email = findViewById(R.id.input_email);
        txt_password = findViewById(R.id.input_password);

        // Cek session login jika TRUE maka langsung buka MainActivity
        sharedpreferencesA = getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        session = sharedpreferencesA.getBoolean(session_status, false);
        id = sharedpreferencesA.getString(TAG_ID, null);
        email = sharedpreferencesA.getString(TAG_EMAIL, null);

        if (session) {
            Intent intent = new Intent(LoginAdmin.this, HomeAdmin.class);
            intent.putExtra(TAG_ID, id);
            intent.putExtra(TAG_EMAIL, email);
            finish();
            startActivity(intent);
        }
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String email = txt_email.getText().toString();
                String password = txt_password.getText().toString();
                // mengecek kolom yang kosong
                if (email.trim().length() > 0 && password.trim().length() > 0) {
                    conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    {
                        if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {
                            checkLogin(email, password);
                        } else {
                            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext() ,"Kolom tidak boleh kosong", Toast.LENGTH_LONG).show();
                }
            }
        });

        //membuat back button toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    public void register (View v) {
//        Intent intent = new Intent(LoginAdmin.this, RegisterAdmin.class);
//        startActivity(intent);
    }

    private void checkLogin(final String email, final String password) {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Logging in ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Login Response: " + response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);
                    // Check for error node in json
                    if (success == 1) {
                        String id = jObj.getString(TAG_ID);
                        String email = jObj.getString(TAG_EMAIL);
                        Log.e("Successfully Login!", jObj.toString());
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                        // menyimpan login ke session
                        SharedPreferences.Editor editor = sharedpreferencesA.edit();
                        editor.putBoolean(session_status, true);
                        editor.putString(TAG_ID, id);
                        editor.putString(TAG_EMAIL, email);
                        editor.commit();
                        // Memanggil main activity
                        Intent intent = new Intent(LoginAdmin.this, HomeAdmin.class);
                        intent.putExtra(TAG_ID, id);
                        intent.putExtra(TAG_EMAIL, email);
                        finish();
                        startActivity(intent);
                    } else if (success == 2) {
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        Intent pilih = new Intent(getApplicationContext(), PilihActivity.class);
        startActivity(pilih);
    }

    //membuat fungsi back dengan mengirim data session
    @Override
    public boolean onSupportNavigateUp() {
        Intent pilih = new Intent(getApplicationContext(), PilihActivity.class);
        startActivity(pilih);
        return true;
    }

}

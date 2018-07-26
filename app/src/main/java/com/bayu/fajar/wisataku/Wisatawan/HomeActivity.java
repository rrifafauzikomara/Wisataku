package com.bayu.fajar.wisataku.Wisatawan;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.bayu.fajar.wisataku.Admin.MapsActivity;
import com.bayu.fajar.wisataku.Login;
import com.bayu.fajar.wisataku.R;
import com.bayu.fajar.wisataku.Server.AdapterData;
import com.bayu.fajar.wisataku.Server.AppController;
import com.bayu.fajar.wisataku.Server.ModelData;
import com.bayu.fajar.wisataku.Server.RequestHandler;
import com.bayu.fajar.wisataku.Server.Server;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {

    View header;
    TextView txt_nama, txt_email;
    CircleImageView user_picture;
    String idx, level;

    //untuk login session
    SharedPreferences sharedpreferences;
    public static final String TAG_ID = "id_user";
    public final static String TAG_NAMA = "nama";
    public final static String TAG_EMAIL = "email";
    public final static String TAG_FOTO = "foto";
    private String TAG_LEVEL = "level";

    //untuk show profile
    private String urlp = Server.showProfil;

    //untuk menampilkan data pada recyclerview
    ProgressDialog pd;
    List<ModelData> mItems = new ArrayList<>();
    RecyclerView.Adapter mAdapter;
    SwipeRefreshLayout swipe;
    RecyclerView mRecyclerview;
    RecyclerView.LayoutManager mManager;

    private String urld = Server.URLU + "detail_lokasi.php";
    public static final String TAG_IDL       = "id_lokasi";
    public static final String TAG_NAMAL     = "nama";
//    public static final String TAG_LNG      = "lng";
//    public static final String TAG_LAT      = "lat";
    public static final String TAG_TGL      = "tgl";
    public static final String TAG_TIME      = "time";
    public static final String TAG_DESKRIPSI = "deskripsi";
    public static final String TAG_HARGA = "harga";

    //untuk mencari data lokasi
    private String urlc = Server.URLU + "cari_lokasi.php";
    public static final String TAG_VALUE = "value";
    private static final String TAG_MESSAGE = "message";
    public static final String TAG_RESULTS = "results";
    private static final String TAG = HomeActivity.class.getSimpleName();
    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //untuk mengambil data login session
        sharedpreferences = getSharedPreferences(Login.my_shared_preferences, Context.MODE_PRIVATE);
        idx = sharedpreferences.getString(TAG_ID, null);
        level = sharedpreferences.getString(TAG_LEVEL, null);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        header = navigationView.getHeaderView(0);
        txt_nama = header.findViewById(R.id.nama);
        txt_email = header.findViewById(R.id.email);
        user_picture = header.findViewById(R.id.profile_image);

        getUserProfile();

        //menampilkan data lokasi dari database ke recycleview
        swipe = findViewById(R.id.swipe_refresh);
        mRecyclerview = findViewById(R.id.recyclerviewTemp);

        mManager = new LinearLayoutManager(HomeActivity.this,LinearLayoutManager.VERTICAL,false);
        mRecyclerview.setLayoutManager(mManager);

        mAdapter = new AdapterData(HomeActivity.this,mItems);
        mRecyclerview.setAdapter(mAdapter);

        swipe.setOnRefreshListener(this);

        swipe.post(new Runnable() {
                       @Override
                       public void run() {
                           swipe.setRefreshing(true);
                           loadJson();
                       }
                   }
        );

    }

    private void getUserProfile(){
        class GetUser extends AsyncTask<Void,Void,String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
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

            txt_nama.setText(nama);
            txt_email.setText(email);
            Picasso.with(this).load(foto).placeholder(R.drawable.user).error(R.drawable.user).into(user_picture);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //melakukan pengambilan data dari database
    private void loadJson() {
        mItems.clear();
        mAdapter.notifyDataSetChanged();
        swipe.setRefreshing(true);

        JsonArrayRequest reqData = new JsonArrayRequest(Request.Method.POST, urld,null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString());
                for(int i = 0 ; i < response.length(); i++)
                {
                    try {
                        JSONObject data = response.getJSONObject(i);
                        ModelData md = new ModelData();
                        md.setId(data.getString(TAG_IDL));
                        md.setNama(data.getString(TAG_NAMAL));
//                        md.setLng(data.getString(TAG_LNG));
//                        md.setLat(data.getString(TAG_LAT));
                        md.setDate(data.getString(TAG_TGL));
                        md.setTime(data.getString(TAG_TIME));
                        md.setDeskripsi(data.getString(TAG_DESKRIPSI));
                        md.setHarga(data.getString(TAG_HARGA));
                        mItems.add(md);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mAdapter.notifyDataSetChanged();
                swipe.setRefreshing(false);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e(TAG, "Error: " + error.getMessage());
                        Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        swipe.setRefreshing(false);
                    }
                });
        AppController.getInstance().addToRequestQueue(reqData);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        cariData(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private void cariData(final String keyword) {
        pd = new ProgressDialog(HomeActivity.this);
        pd.setCancelable(false);
        pd.setMessage("Loading...");
        pd.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, urlc, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response: ", response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);

                    int value = jObj.getInt(TAG_VALUE);

                    if (value == 1) {
                        mItems.clear();
                        mAdapter.notifyDataSetChanged();

                        String getObject = jObj.getString(TAG_RESULTS);
                        JSONArray jsonArray = new JSONArray(getObject);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);

                            ModelData data = new ModelData();

                            data.setId(obj.getString(TAG_IDL));
                            data.setNama(obj.getString(TAG_NAMAL));
                            data.setDate(obj.getString(TAG_TGL));
                            data.setTime(obj.getString(TAG_TIME));
                            data.setDeskripsi(obj.getString(TAG_DESKRIPSI));
                            data.setHarga(obj.getString(TAG_HARGA));

                            mItems.add(data);
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

                mAdapter.notifyDataSetChanged();
                pd.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("nama", keyword);

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }


    @Override
    public void onRefresh() {
        loadJson();
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
                Intent intent = new Intent(HomeActivity.this, Login.class);
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setQueryHint(getString(R.string.type_name));
        searchView.setIconified(true);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Intent intent = new Intent(HomeActivity.this, Profile.class);
            intent.putExtra(TAG_ID, idx);
            startActivity(intent);
        } else if (id == R.id.nav_maps) {
            Intent maps = new Intent(HomeActivity.this, WisataActivity.class);
            startActivity(maps);
        } else if (id == R.id.nav_logout) {
            logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}

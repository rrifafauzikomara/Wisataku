package com.bayu.fajar.wisataku.Admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bayu.fajar.wisataku.R;
import com.bayu.fajar.wisataku.Server.AdapterData;
import com.bayu.fajar.wisataku.Server.AppController;
import com.bayu.fajar.wisataku.Server.ModelData;
import com.bayu.fajar.wisataku.Server.RequestHandler;
import com.bayu.fajar.wisataku.Server.Server;
import com.bayu.fajar.wisataku.Wisatawan.DetailLokasi;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListWisata extends AppCompatActivity {

    //untuk menampilkan data pada recyclerview
    RecyclerView mRecyclerview;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mManager;
    List<ModelData> mItems;
    ProgressDialog pd;
    private String urld = Server.URLA + "detail_lokasi.php?id_user=";
    public static final String TAG_IDL       = "id_lokasi";
    public static final String TAG_NAMA1     = "nama";
    public static final String TAG_LNG      = "lng";
    public static final String TAG_LAT      = "lat";
    public static final String TAG_TGL      = "tgl";
    public static final String TAG_TIME      = "time";
    public static final String TAG_DESKRIPSI = "deskripsi";
    public static final String TAG_HARGA = "harga";
    public static final String TAG_ID = "id_user";
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_wisata);

        Intent intent = getIntent();
        id = intent.getStringExtra(TAG_ID);

        //menampilkan data lokasi dari database ke recycleview
        mRecyclerview = findViewById(R.id.recyclerviewTemp);
        pd = new ProgressDialog(ListWisata.this);
        mItems = new ArrayList<>();
        loadJson();
        mManager = new LinearLayoutManager(ListWisata.this,LinearLayoutManager.VERTICAL,false);
        mRecyclerview.setLayoutManager(mManager);
        mAdapter = new AdapterData(ListWisata.this,mItems);
        mRecyclerview.setAdapter(mAdapter);

        //membuat back button toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    //membuat fungsi back dengan mengirim data session
    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(getApplicationContext(), HomeAdmin.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), HomeAdmin.class);
        startActivity(intent);
    }

    //melakukan pengambilan data dari database
    private void loadJson() {
        pd.setMessage("Mengambil Data");
        pd.setCancelable(false);
        pd.show();
        JsonArrayRequest reqData = new JsonArrayRequest(Request.Method.POST, urld + id,null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                pd.cancel();
                Log.d("volley","response : " + response.toString());
                for(int i = 0 ; i < response.length(); i++)
                {
                    try {
                        JSONObject data = response.getJSONObject(i);
                        ModelData md = new ModelData();
                        md.setId(data.getString(TAG_IDL));
                        md.setNama(data.getString(TAG_NAMA1));
                        md.setLng(data.getString(TAG_LNG));
                        md.setLat(data.getString(TAG_LAT));
                        md.setDate(data.getString(TAG_TGL));
                        md.setTime(data.getString(TAG_TIME));
                        md.setDeskripsi(data.getString(TAG_DESKRIPSI));
                        md.setHarga(data.getString(TAG_HARGA));
                        md.setId_user(data.getString(TAG_ID));
                        mItems.add(md);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.cancel();
                        Log.d("volley", "error : " + error.getMessage());
                    }
                });
        AppController.getInstance().addToRequestQueue(reqData);
    }

}

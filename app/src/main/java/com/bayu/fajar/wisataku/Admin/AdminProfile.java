package com.bayu.fajar.wisataku.Admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bayu.fajar.wisataku.R;
import com.bayu.fajar.wisataku.Server.AppController;
import com.bayu.fajar.wisataku.Server.RequestHandler;
import com.bayu.fajar.wisataku.Server.Server;
import com.bayu.fajar.wisataku.Wisatawan.HomeActivity;
import com.bayu.fajar.wisataku.Wisatawan.Profile;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminProfile extends AppCompatActivity {

    private String urlp = Server.showProfilA;

    Button simpan;
    ImageButton pilih;
    CircleImageView imageView;
    EditText txt_id, txt_nama, txt_email;
    Bitmap bitmap, decoded;
    ConnectivityManager conMgr;

    //untuk mengambil data session saat login
    String id, nama, email, foto;
    private String TAG_NAMA = "nama";
    private String TAG_EMAIL = "email";
    private String TAG_FOTO = "foto";

    //untuk upload foto serta edit profile
    int success;
    int PICK_IMAGE_REQUEST = 1;
    int bitmap_size = 60; // range 1 - 100
    private static final String TAG = HomeAdmin.class.getSimpleName();
    private String UPLOAD_URL = Server.URLA + "upload.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private String TAG_ID = "id_admin";

    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        Intent intent = getIntent();
        id = intent.getStringExtra(TAG_ID);

        //merelasikan variable dengan design UI
        pilih = findViewById(R.id.img);
        simpan = findViewById(R.id.btnUpload);
        txt_nama = findViewById(R.id.txt_nama);
        txt_email = findViewById(R.id.txt_email);
        txt_id = findViewById(R.id.txt_id);
        imageView = findViewById(R.id.profile_image1);

        //untuk mengambil gambar dari galery
        pilih.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        //untuk melakukan update data serta upload foto/gambar
        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);{
                    if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {
                        uploadImage();
                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        //membuat back button toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        txt_id.setText(id);
        getUserProfile();

    }

    private void getUserProfile(){
        class GetUser extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(AdminProfile.this,"Tunggu",".....",false,false);
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
                String s = rh.sendGetRequestParam(urlp,id);
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
            Picasso.with(this).load(foto).placeholder(R.drawable.user).error(R.drawable.user).into(imageView);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //membuat fungsi back dengan mengirim data session
    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(AdminProfile.this, HomeAdmin.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AdminProfile.this, HomeAdmin.class);
        startActivity(intent);
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void uploadImage() {
        //menampilkan progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        Intent intent = new Intent(AdminProfile.this, HomeActivity.class);
                        startActivity(intent);
                        Log.e("v Add", jObj.toString());
                        Toast.makeText(AdminProfile.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(AdminProfile.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //menghilangkan progress dialog
                loading.dismiss();
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //menghilangkan progress dialog
                        loading.dismiss();

                        //menampilkan toast
                        Toast.makeText(AdminProfile.this, "Silahkan pilih foto profil terlebih dahulu", Toast.LENGTH_LONG).show();
//                        Log.e(TAG, error.getMessage().toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                //membuat parameters
                Map<String, String> params = new HashMap<String, String>();
                //menambah parameter yang di kirim ke web servis
                params.put(TAG_ID, txt_id.getText().toString().trim());
                params.put(TAG_FOTO, getStringImage(decoded));
                params.put(TAG_NAMA, txt_nama.getText().toString().trim());
                params.put(TAG_EMAIL, txt_email.getText().toString().trim());
                //kembali ke parameters
                Log.e(TAG, "" + params);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //mengambil Gambar dari Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                // 512 adalah resolusi tertinggi setelah image di resize, bisa di ganti.
                setToImageView(getResizedBitmap(bitmap, 512));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setToImageView(Bitmap bmp) {
        //compress image
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, bytes);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));
        //menampilkan gambar yang dipilih dari camera/gallery ke ImageView
        imageView.setImageBitmap(decoded);
    }

    // fungsi resize image
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

}

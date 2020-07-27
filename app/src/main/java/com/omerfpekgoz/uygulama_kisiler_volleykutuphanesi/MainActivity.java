package com.omerfpekgoz.uygulama_kisiler_volleykutuphanesi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.omerfpekgoz.uygulama_kisiler_volleykutuphanesi.adapter.KisilerAdapter;
import com.omerfpekgoz.uygulama_kisiler_volleykutuphanesi.model.Kisiler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


//Searc için arama menüsüne ekle
// app:actionViewClass="androidx.appcompat.widget.SearchView"
public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private Toolbar toolbarMain;
    private RecyclerView recyclerViewKisiler;
    private FloatingActionButton fabKisi;

    private ArrayList<Kisiler> kisilerArrayList;
    private KisilerAdapter kisilerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbarMain = findViewById(R.id.toolbarMain);
        recyclerViewKisiler = findViewById(R.id.recyclerKisiler);
        fabKisi = findViewById(R.id.fabKisi);


        toolbarMain.setTitle("KİŞİLER");
        setSupportActionBar(toolbarMain);

        kisilerAdapter = new KisilerAdapter();

        recyclerViewKisiler.setHasFixedSize(true);
        recyclerViewKisiler.setLayoutManager(new LinearLayoutManager(this));


        tumKisiler();


        fabKisi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertView();

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.menuArama);

        SearchView searchView = (SearchView)menuItem.getActionView();
        searchView.setOnQueryTextListener(MainActivity.this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        arama(newText);
        return false;
    }


    public void alertView() {


        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View alertView = layoutInflater.inflate(R.layout.alertview_tasarim, null);


        final EditText txtKisiAd = alertView.findViewById(R.id.txtKisiAd);
        final EditText txtKisiTel = alertView.findViewById(R.id.txtKisiTel);

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Kişi Ekle");
        alertDialog.setView(alertView);
        alertDialog.setPositiveButton("Ekle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String kisiAd = txtKisiAd.getText().toString().trim();
                String kisiTel = txtKisiTel.getText().toString().trim();

                kisiEkle(kisiAd,kisiTel);


                if (TextUtils.isEmpty(kisiAd)){
                    Snackbar.make(alertView.getRootView(),"Kişi Adı Giriniz",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(kisiTel)){
                    Snackbar.make(alertView.getRootView(),"Kişi Tel Giriniz",Snackbar.LENGTH_SHORT).show();
                    return;
                }


            }
        });
        alertDialog.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.create().show();

    }

    public void tumKisiler() {

        String url = "http://www.omerfpekgoz.cf/kisiler/tum_kisiler.php";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                kisilerArrayList = new ArrayList<>();           //Sürekli yenilememesi için bunu burda çalıştırdık

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray kisiler = jsonObject.getJSONArray("kisiler");

                    for (int i = 0; i < kisiler.length(); i++) {


                        JSONObject k = kisiler.getJSONObject(i);

                        int kisi_id = k.getInt("kisi_id");
                        String kisi_ad = k.getString("kisi_ad");
                        String kisi_tel = k.getString("kisi_tel");

                        Kisiler kisi = new Kisiler(kisi_id, kisi_ad, kisi_tel);
                        kisilerArrayList.add(kisi);

                    }

                    kisilerAdapter = new KisilerAdapter(MainActivity.this, kisilerArrayList);
                    recyclerViewKisiler.setAdapter(kisilerAdapter);


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        Volley.newRequestQueue(MainActivity.this).add(stringRequest);

    }

    public void kisiEkle(final String kisi_ad, final String kisi_tel) {

        String url = "http://www.omerfpekgoz.cf/kisiler/insert_kisiler.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                tumKisiler();                          //Cevap kısmında tekrar çalıştırıp sayfayı yeniledik  ÖNEMLİ

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                Map<String, String> params = new HashMap<>();
                params.put("kisi_ad", kisi_ad);
                params.put("kisi_tel", kisi_tel);


                return params;
            }
        };

        Volley.newRequestQueue(MainActivity.this).add(stringRequest);


    }

    public void arama(final String arananKisi){
        String url="http://www.omerfpekgoz.cf/kisiler/tum_kisiler_arama.php";

        StringRequest stringRequest1=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                kisilerArrayList = new ArrayList<>();             //Arraylisti aktif etmemiz gerekli yeniden
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray kisiler = jsonObject.getJSONArray("kisiler");

                    for (int i = 0; i < kisiler.length(); i++) {


                        JSONObject k = kisiler.getJSONObject(i);

                        int kisi_id = k.getInt("kisi_id");
                        String kisi_ad = k.getString("kisi_ad");
                        String kisi_tel = k.getString("kisi_tel");

                        Kisiler kisi = new Kisiler(kisi_id, kisi_ad, kisi_tel);
                        kisilerArrayList.add(kisi);

                    }

                    kisilerAdapter = new KisilerAdapter(MainActivity.this, kisilerArrayList);
                    recyclerViewKisiler.setAdapter(kisilerAdapter);


                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("kisi_ad",arananKisi);


                return params;
            }
        };
        Volley.newRequestQueue(MainActivity.this).add(stringRequest1);
    }
}

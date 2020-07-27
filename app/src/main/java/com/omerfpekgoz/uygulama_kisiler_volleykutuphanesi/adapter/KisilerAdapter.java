package com.omerfpekgoz.uygulama_kisiler_volleykutuphanesi.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.omerfpekgoz.uygulama_kisiler_volleykutuphanesi.R;
import com.omerfpekgoz.uygulama_kisiler_volleykutuphanesi.model.Kisiler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KisilerAdapter extends RecyclerView.Adapter<KisilerAdapter.cardViewHolder> {

    private Context mContext;
    private List<Kisiler> kisilerList;



    public KisilerAdapter() {
    }

    public KisilerAdapter(Context mContext, List<Kisiler> kisilerList) {
        this.mContext = mContext;
        this.kisilerList = kisilerList;
    }

    @NonNull
    @Override
    public cardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.kisi_card_tasarim, parent, false);

        return new cardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final cardViewHolder holder, int position) {

        final Kisiler kisi = kisilerList.get(position);


        holder.txtKisiBilgi.setText(kisi.getKisi_ad() + " - " + kisi.getKisi_tel());

        holder.imageSecenek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(mContext, holder.imageSecenek);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {


                        switch (item.getItemId()) {

                            case R.id.menuSil:
                                Snackbar.make(holder.imageSecenek, "Kisi Silinsin Mi?", Snackbar.LENGTH_LONG)
                                        .setAction("Evet", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                kisiSil(kisi);

                                            }
                                        }).show();

                                return true;
                            case R.id.menuDuzenle:
                                alertView(kisi);

                                return true;
                            default:
                                return false;


                        }
                    }
                });
                popupMenu.show();


            }
        });


    }

    @Override
    public int getItemCount() {
        return kisilerList.size();
    }


    public class cardViewHolder extends RecyclerView.ViewHolder {

        private CardView cardViewKisiler;
        private TextView txtKisiBilgi;
        private ImageView imageSecenek;


        public cardViewHolder(@NonNull View itemView) {
            super(itemView);

            cardViewKisiler = itemView.findViewById(R.id.cardViewKisiler);
            txtKisiBilgi = itemView.findViewById(R.id.txtKisiBilgi);
            imageSecenek = itemView.findViewById(R.id.imageSecenek);

        }
    }

    public void kisiSil(final Kisiler kisi) {

        String url = "http://www.omerfpekgoz.cf/kisiler/delete_kisiler.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Sayfa yenilemesi için tumKisileri burada da çalıştırdık
                tumKisiler();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("kisi_id", String.valueOf(kisi.getKisi_id()));


                return params;
            }
        };

        Volley.newRequestQueue(mContext).add(stringRequest);

    }

    public void tumKisiler() {

        String url = "http://www.omerfpekgoz.cf/kisiler/tum_kisiler.php";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Sürekli yenilememesi için bunu burda çalıştırdık
                //Ancak Adapter daki listeyi çalıştırmamız gerekir
                kisilerList = new ArrayList<>();


                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray kisiler = jsonObject.getJSONArray("kisiler");

                    for (int i = 0; i < kisiler.length(); i++) {


                        JSONObject k = kisiler.getJSONObject(i);

                        int kisi_id = k.getInt("kisi_id");
                        String kisi_ad = k.getString("kisi_ad");
                        String kisi_tel = k.getString("kisi_tel");

                        Kisiler kisi = new Kisiler(kisi_id, kisi_ad, kisi_tel);
                        kisilerList.add(kisi);

                    }

                    //Bunu kullanmak yanileme yapmaya yeterli
                    notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        Volley.newRequestQueue(mContext).add(stringRequest);

    }
    public void kisiDüzenle(final int kisiId, final String kisiAd, final String kisiTel){

        String url="http://www.omerfpekgoz.cf/kisiler/update_kisiler.php";

        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                tumKisiler();                 //Düzenledikten sonra sayfa yenilemek için
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params=new HashMap<>();
                params.put("kisi_id",String.valueOf(kisiId));
                params.put("kisi_ad",kisiAd);
                params.put("kisi_tel",kisiTel);


                return params;
            }
        };

        Volley.newRequestQueue(mContext).add(stringRequest);

    }
    public void alertView(Kisiler kisi) {


        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        final View alertView = layoutInflater.inflate(R.layout.alertview_tasarim, null);


        final EditText txtKisiAd = alertView.findViewById(R.id.txtKisiAd);
        final EditText txtKisiTel = alertView.findViewById(R.id.txtKisiTel);

        txtKisiAd.setText(kisi.getKisi_ad());
        txtKisiTel.setText(kisi.getKisi_tel());
        final int yenikisiId=kisi.getKisi_id();

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Kişi Düzenle");
        alertDialog.setView(alertView);
        alertDialog.setPositiveButton("Düzenle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {



                String yenikisiAd = txtKisiAd.getText().toString().trim();
                String yenikisiTel = txtKisiTel.getText().toString().trim();

                kisiDüzenle(yenikisiId,yenikisiAd,yenikisiTel);


                if (TextUtils.isEmpty(yenikisiAd)){
                    Snackbar.make(alertView.getRootView(),"Kişi Adı Giriniz",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(yenikisiTel)){
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

}

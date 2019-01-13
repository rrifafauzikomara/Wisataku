package com.bayu.fajar.wisataku.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bayu.fajar.wisataku.R;
import com.bayu.fajar.wisataku.model.ModelData;
import com.bayu.fajar.wisataku.view.wisatawan.DetailLokasi;

import java.util.List;

public class AdapterData extends RecyclerView.Adapter<AdapterData.HolderData> {

    private List<ModelData> mItems ;
    private Context context;
    public AdapterData (Context context, List<ModelData> items) {
        this.mItems = items;
        this.context = context;
    }

    @Override
    public HolderData onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row,parent,false);
        HolderData holderData = new HolderData(layout);
        return holderData;
    }

    @Override
    public void onBindViewHolder(HolderData holder, final int position) {
        ModelData md  = mItems.get(position);
        holder.tvnama.setText(md.getNama());
//        holder.tvlng.setText(md.getLng());
//        holder.tvlat.setText(md.getLat());
        holder.tvdate.setText(md.getDate());
//        holder.tvtime.setText(md.getTime());
        holder.tvdesk.setText(md.getDeskripsi());
        holder.tvharga.setText(md.getHarga());

        holder.cd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), DetailLokasi.class);
                i.putExtra("nama", mItems.get(position).getNama());
                i.putExtra("lng", mItems.get(position).getLng());
                i.putExtra("lat", mItems.get(position).getLat());
                i.putExtra("tgl", mItems.get(position).getDate());
                i.putExtra("time", mItems.get(position).getTime());
                i.putExtra("deskripsi", mItems.get(position).getDeskripsi());
                i.putExtra("harga", mItems.get(position).getHarga());
                i.putExtra("id_user", mItems.get(position).getId_user());
                v.getContext().startActivity(i);
            }
        });

        holder.md = md;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class HolderData extends RecyclerView.ViewHolder {
        TextView tvnama,tvlng, tvlat, tvdate, tvtime, tvdesk, tvharga, txt_id_user;
        ImageView foto;
        ModelData md;
        CardView cd;
        public  HolderData (View view) {
            super(view);
            tvnama = view.findViewById(R.id.nama);
//            tvlng = view.findViewById(R.id.lngValue);
//            tvlat = view.findViewById(R.id.latValue);
            tvdate = view.findViewById(R.id.tgl);
//            tvtime = view.findViewById(R.id.date);
            tvdesk = view.findViewById(R.id.deskripsi);
            tvharga = view.findViewById(R.id.harga);
            foto = view.findViewById(R.id.foto);
//            txt_id_user = view.findViewById(R.id.idUser);
            cd = view.findViewById(R.id.cardview);
        }
    }
}

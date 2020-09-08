package com.mkpolo.appgfv.modelo.alerta;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AlertaAdapter extends RecyclerView.Adapter<AlertaAdapter.MyViewAlerta> {
    @NonNull
    @Override
    public AlertaAdapter.MyViewAlerta onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull AlertaAdapter.MyViewAlerta holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class MyViewAlerta extends RecyclerView.ViewHolder {
        public MyViewAlerta(@NonNull View itemView) {
            super(itemView);
        }
    }
}

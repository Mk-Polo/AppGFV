package com.mkpolo.appgfv.modelo.marca;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mkpolo.appgfv.R;

import java.util.ArrayList;

public class MarcaAdapter extends RecyclerView.Adapter<MarcaAdapter.MyViewHolder> implements View.OnClickListener{

    private Context context;
    private ArrayList<Marca> marca;

    private View.OnClickListener listenerMar;

    public MarcaAdapter(Context context, ArrayList<Marca> marca) {
        this.context = context;
        this.marca = marca;
    }

    @NonNull
    @Override
    public MarcaAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        LayoutInflater layoutInflater= LayoutInflater.from(context);
        view =layoutInflater.inflate(R.layout.marca_lista, parent, false);

        view.setOnClickListener(this);

        return new MyViewHolder(view);
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listenerMar = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull MarcaAdapter.MyViewHolder holder, final int position) {
        holder.nombreMarca.setText(marca.get(position).getMarca());
        holder.noMarca.setText("#" + String.valueOf(position + 1));
    }

    @Override
    public int getItemCount() {
            return marca.size();
    }

    @Override
    public void onClick(View v) {
        if(listenerMar!= null){
            listenerMar.onClick(v);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView nombreMarca, noMarca;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            noMarca = (TextView) itemView.findViewById(R.id.noMarca);
            nombreMarca = (TextView) itemView.findViewById(R.id.nombreMarca);
        }
    }
}

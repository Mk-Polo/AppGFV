package com.mkpolo.appgfv.modelo.categoria;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mkpolo.appgfv.R;

import java.util.ArrayList;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.ViewMyHolderCategoria> implements View.OnClickListener{

    private Context context;
    private ArrayList<Categoria> categoria;
    private String url="http://192.168.1.60:9001/api/categorias/";

    //Enviar click position
    private View.OnClickListener listenerCat;

    public CategoriaAdapter(Context context, ArrayList<Categoria> categoria) {
        this.context = context;
        this.categoria = categoria;
    }

    @NonNull
    @Override
    public CategoriaAdapter.ViewMyHolderCategoria onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.categoria_lista, parent, false);

        //Enviar click position
        view.setOnClickListener(this);

        return new ViewMyHolderCategoria(view);
    }

    //Enviar click position
    public void setOnClickListener(View.OnClickListener listener){
        this.listenerCat = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriaAdapter.ViewMyHolderCategoria holder, final int position) {
        holder.noCategoria.setText("#" + String.valueOf(position + 1));
        holder.nombreCategoria.setText(categoria.get(position).getCategoria());
    }

    @Override
    public int getItemCount() {
        return categoria.size();
    }

    //Enviar click position
    @Override
    public void onClick(View v) {
        if(listenerCat!= null){
            listenerCat.onClick(v);
        }
    }

    public class ViewMyHolderCategoria extends RecyclerView.ViewHolder {
        private TextView nombreCategoria, noCategoria;

        public ViewMyHolderCategoria(@NonNull View itemView) {
            super(itemView);

            noCategoria = (TextView) itemView.findViewById(R.id.noCategoria);
            nombreCategoria = (TextView) itemView.findViewById(R.id.nombreCategoria);
        }
    }
}
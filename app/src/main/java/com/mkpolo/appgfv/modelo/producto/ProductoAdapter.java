package com.mkpolo.appgfv.modelo.producto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mkpolo.appgfv.R;

import java.util.ArrayList;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.MyViewProducto> {

    private Context context;
    private ArrayList<Producto> producto;
    private String url = "http://192.168.1.60:9001/api/productos/";

    public ProductoAdapter(Context context, ArrayList<Producto> producto) {
        this.context = context;
        this.producto = producto;
    }

    @NonNull
    @Override
    public ProductoAdapter.MyViewProducto onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.producto_lista, parent, false);

        return new MyViewProducto(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoAdapter.MyViewProducto holder, int position) {
        holder.noProducto.setText("#" + String.valueOf(position + 1));
        holder.marcaProducto.setText(producto.get(position).marca.getMarca());
        holder.nombreProducto.setText(producto.get(position).getNombreProducto());
        holder.pesoProducto.setText(producto.get(position).getPesoProducto());
        holder.diasProducto.setText(String.valueOf(producto.get(position).getDiasProducto()));
    }

    @Override
    public int getItemCount() {
        return producto.size();
    }

    public class MyViewProducto extends RecyclerView.ViewHolder {
        private TextView nombreProducto, noProducto, marcaProducto, catProducto, diasProducto, pesoProducto;
        private ImageView editProducto, deleteProducto;

        public MyViewProducto(@NonNull View itemView) {
            super(itemView);
            noProducto = (TextView) itemView.findViewById(R.id.noProducto);
            marcaProducto = (TextView) itemView.findViewById(R.id.productoMarca);
            nombreProducto = (TextView) itemView.findViewById(R.id.nombreProducto);
            pesoProducto = (TextView) itemView.findViewById(R.id.pesoProducto);
            diasProducto = (TextView) itemView.findViewById(R.id.diasProducto);
            editProducto = (ImageView) itemView.findViewById(R.id.editProducto);
            deleteProducto = (ImageView) itemView.findViewById(R.id.deleteProducto);
        }
    }
}

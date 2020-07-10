package com.mkpolo.appgfv.modelo.categoria;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mkpolo.appgfv.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.ViewMyHolderCategoria> {

    private Context context;
    private ArrayList<Categoria> categoria;
    private String url="http://192.168.1.60:9001/api/categorias/";

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
        return new ViewMyHolderCategoria(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriaAdapter.ViewMyHolderCategoria holder, final int position) {
        holder.noCategoria.setText("#" + String.valueOf(position + 1));
        holder.nombreCategoria.setText(categoria.get(position).getCategoria());
        holder.editCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = categoria.get(position).getId();
                String valueCat = categoria.get(position).getCategoria();
                editarCategoria(id, valueCat);
            }
        });

        holder.deleteCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = categoria.get(position).getId();
                eliminarCategoria(id);
            }
        });
    }

    private void eliminarCategoria(final int id) {
        TextView closeMarca,tittleMarca;
        final EditText edtxtMarca;
        Button submitMarca;
        final Dialog dialog;

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.delete_marca);
        closeMarca = (TextView) dialog.findViewById(R.id.txtCerrarProducto);
        tittleMarca = (TextView) dialog.findViewById(R.id.tituloMarca);
        tittleMarca.setText("Eliminar Categoria");

        closeMarca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        submitMarca = (Button) dialog.findViewById(R.id.submitMarca);
        submitMarca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubmitCategoria("DELETE","",dialog, id);
            }
        });
        dialog.show();
    }

    private void editarCategoria(final int id, String valueCat) {
        TextView closeCategoria,tittleCategoria;
        final EditText edtxtCategoria;
        Button submitCategoria;
        final Dialog dialog;

        dialog = new Dialog(context);

        dialog.setContentView(R.layout.fragment_modcategoria);

        closeCategoria = (TextView) dialog.findViewById(R.id.txtCerrarCategoria);
        tittleCategoria = (TextView) dialog.findViewById(R.id.tituloCategoria);
        tittleCategoria.setText("Editar Categoria");

        closeCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        edtxtCategoria = (EditText)dialog.findViewById(R.id.edtxtMarca);
        submitCategoria = (Button) dialog.findViewById(R.id.submitMarca);

        edtxtCategoria.setText(valueCat);

        submitCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dataCategoria = "{"+"\"categoria\"" +":"+ "\"" + edtxtCategoria.getText().toString() + "\","+
                        "\"idCategoria\"" +":"+ "\"" + id + "\""+
                        "}";
                SubmitCategoria("PUT",dataCategoria,dialog,id);
            }
        });
        dialog.show();
    }

    private void SubmitCategoria(String method, String dataCategoria, final Dialog dialog, int id) {
        if(method == "PUT"){
            final  String saveCategoria = dataCategoria;
            StringRequest request =new StringRequest(Request.Method.POST, url + "save", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject objmar = new JSONObject(response);
                        dialog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "No se pudieron actualizar datos", Toast.LENGTH_SHORT).show();
                }
            }){
                public String getBodyContentType(){
                    return "application/json; charset=utf-8";
                }

                public byte[] getBody() throws AuthFailureError {
                    try {
                        return saveCategoria == null ? null : saveCategoria.getBytes("utf-8");
                    }catch (UnsupportedEncodingException uee){
                        return null;
                    }
                }
            };
            Volley.newRequestQueue(context).add(request);
        }else if(method == "DELETE"){
            StringRequest request = new StringRequest(Request.Method.DELETE, url + "delete/" + id, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    dialog.dismiss();
                    Toast.makeText(context, "Datos eliminados exitosamente", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "No se pudieron eliminar datos", Toast.LENGTH_SHORT).show();
                }
            });
            Volley.newRequestQueue(context).add(request);
        }
    }

    @Override
    public int getItemCount() {
        return categoria.size();
    }

    public class ViewMyHolderCategoria extends RecyclerView.ViewHolder {
        private TextView nombreCategoria, noCategoria;
        private ImageView editCategoria, deleteCategoria;

        public ViewMyHolderCategoria(@NonNull View itemView) {
            super(itemView);

            noCategoria = (TextView) itemView.findViewById(R.id.noCategoria);
            nombreCategoria = (TextView) itemView.findViewById(R.id.nombreCategoria);
            editCategoria = (ImageView) itemView.findViewById(R.id.editCategoria);
            deleteCategoria = (ImageView) itemView.findViewById(R.id.deleteCategoria);
        }
    }
}

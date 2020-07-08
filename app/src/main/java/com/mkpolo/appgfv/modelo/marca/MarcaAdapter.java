package com.mkpolo.appgfv.modelo.marca;

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

public class MarcaAdapter extends RecyclerView.Adapter<MarcaAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Marca> marca;
    private String url = "http://192.168.1.60:9001/api/marcas/";

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

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MarcaAdapter.MyViewHolder holder, final int position) {
        holder.nombreMarca.setText(marca.get(position).getMarca());
        holder.noMarca.setText("#" + String.valueOf(position + 1));
        holder.editMarca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = marca.get(position).getId();
                String valueMarca =marca.get(position).getMarca();
                editarMarca(id, valueMarca);
            }
        });
        holder.deleteMarca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = marca.get(position).getId();
                eliminarMarca(id);
            }
        });
    }

    private void eliminarMarca(final int id) {
        TextView closeMarca,tittleMarca;
        final EditText edtxtMarca;
        Button submitMarca;
        final Dialog dialog;

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.delete_marca);
        closeMarca = (TextView) dialog.findViewById(R.id.txtCerrar);
        tittleMarca = (TextView) dialog.findViewById(R.id.tituloMarca);
        tittleMarca.setText("Eliminar Marca");

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
                SubmitMarca("DELETE","",dialog, id);
            }
        });
        dialog.show();
    }

    private void editarMarca(final int id, String valueMarca) {
        TextView closeMarca,tittleMarca;
        final EditText edtxtMarca;
        Button submitMarca;
        final Dialog dialog;

        dialog = new Dialog(context);

        dialog.setContentView(R.layout.fragment_modmarca);

        closeMarca = (TextView) dialog.findViewById(R.id.txtCerrar);
        tittleMarca = (TextView) dialog.findViewById(R.id.tituloMarca);
        tittleMarca.setText("Editar Marca");

        closeMarca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        edtxtMarca = (EditText)dialog.findViewById(R.id.edtxtMarca);
        submitMarca = (Button) dialog.findViewById(R.id.submitMarca);

        edtxtMarca.setText(valueMarca);

        submitMarca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dataMarca = "{"+"\"marca\"" +":"+ "\"" + edtxtMarca.getText().toString() + "\","+
                        "\"idMarca\"" +":"+ "\"" + id + "\""+
                        "}";
                SubmitMarca("PUT",dataMarca,dialog,id);
            }
        });
        dialog.show();
    }

    private void SubmitMarca(String method, String dataMarca, final Dialog dialog, int id) {
        if(method == "PUT"){
            final  String saveMarca = dataMarca;
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
                        return saveMarca == null ? null : saveMarca.getBytes("utf-8");
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

            return marca.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView nombreMarca, noMarca;
        private ImageView editMarca, deleteMarca;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            noMarca = (TextView) itemView.findViewById(R.id.noMarca);
            nombreMarca = (TextView) itemView.findViewById(R.id.nombreMarca);
            editMarca = (ImageView) itemView.findViewById(R.id.editMarca);
            deleteMarca = (ImageView) itemView.findViewById(R.id.deleteMarca);
        }
    }
}

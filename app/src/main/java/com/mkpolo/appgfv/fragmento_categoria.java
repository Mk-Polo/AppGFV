package com.mkpolo.appgfv;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mkpolo.appgfv.modelo.categoria.Categoria;
import com.mkpolo.appgfv.modelo.categoria.CategoriaAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragmento_categoria#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragmento_categoria extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private RequestQueue requestQueue;
    private SwipeRefreshLayout refresh;
    private ArrayList<Categoria> categoria = new ArrayList<>();
    private JsonArrayRequest arrayRequest;
    private RecyclerView recyclerView;
    private Dialog dialog;
    private CategoriaAdapter categoriaAdapter;
    private FloatingActionButton clickAgregarCategoria;
    private String url="http://192.168.1.60:9001/api/categorias/";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragmento_categoria() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragmento_categoria.
     */
    // TODO: Rename and change types and number of parameters
    public static fragmento_categoria newInstance(String param1, String param2) {
        fragmento_categoria fragment = new fragmento_categoria();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragmento_categoria, container, false);
        refresh = view.findViewById(R.id.swipedownCategoria);
        recyclerView = view.findViewById(R.id.recyclerCategoria);
        clickAgregarCategoria = view.findViewById(R.id.agregarCategoria);

        dialog = new Dialog(getContext());

        clickAgregarCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCategoria();
            }
        });

        refresh.setOnRefreshListener(this);

        refresh.post(new Runnable() {
            @Override
            public void run() {
                categoria.clear();
                getDataCategoria();
            }
        });
        return view;
    }

    private void addCategoria() {
        TextView closeCategoria,tittleCategoria;
        final EditText edtxtCategoria;
        Button submitCategoria;
        dialog.setContentView(R.layout.fragment_modcategoria);

        closeCategoria = (TextView) dialog.findViewById(R.id.txtCerrarCategoria);
        tittleCategoria = (TextView) dialog.findViewById(R.id.tituloCategoria);
        tittleCategoria.setText("Agregar Categoria");

        closeCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        edtxtCategoria = (EditText)dialog.findViewById(R.id.edtxtCategoria);
        submitCategoria = (Button) dialog.findViewById(R.id.submitCategoria);

        submitCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dataCategoria = "{"+"\"categoria\"" +":"+ "\"" + edtxtCategoria.getText().toString() + "\""+"}";
                SubmitCategoria(dataCategoria);
            }
        });
        dialog.show();
    }

    private void SubmitCategoria(String dataCategoria) {
        final  String saveCategoria = dataCategoria;
        StringRequest request =new StringRequest(Request.Method.POST, url + "save", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject objcat= new JSONObject(response);
                    dialog.dismiss();
                    refresh.post(new Runnable() {
                        @Override
                        public void run() {
                            categoria.clear();
                            getDataCategoria();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "No se pudieron agregar datos", Toast.LENGTH_SHORT).show();
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
        Volley.newRequestQueue(getContext()).add(request);
    }

    private void getDataCategoria() {
        refresh.setRefreshing(true);
        arrayRequest = new JsonArrayRequest(url + "/all", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);

                        Categoria cat = new Categoria();
                        cat.setId(jsonObject.getInt("idCategoria"));
                        cat.setCategoria(jsonObject.getString("categoria"));
                        categoria.add(cat);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapterPush(categoria);
                refresh.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        );
        requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(arrayRequest);
    }

    private void adapterPush(final ArrayList<Categoria> categoria) {
        categoriaAdapter = new CategoriaAdapter(getContext(), categoria);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(categoriaAdapter);

        categoriaAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre= categoria.get(recyclerView.getChildAdapterPosition(v)).getCategoria();
                int id = categoria.get(recyclerView.getChildAdapterPosition(v)).getId();

                dialog = new Dialog(getContext());

                editarCategoria(nombre, id);
            }
        });

    }

    private void editarCategoria(String valueCat, final int id) {
        TextView closeCategoria,tittleCategoria;
        final EditText edtxtCategoria;
        Button submitCategoria;

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

        edtxtCategoria = (EditText)dialog.findViewById(R.id.edtxtCategoria);
        submitCategoria = (Button) dialog.findViewById(R.id.submitCategoria);

        edtxtCategoria.setText(valueCat);

        submitCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dataCategoria = "{"+"\"categoria\"" +":"+ "\"" + edtxtCategoria.getText().toString() + "\","+
                        "\"idCategoria\"" +":"+ "\"" + id + "\""+
                        "}";
                SubmitCategoria(dataCategoria);
            }
        });
        dialog.show();
    }

    @Override
    public void onRefresh() {
        categoria.clear();
        getDataCategoria();
    }
}
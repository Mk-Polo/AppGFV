package com.mkpolo.appgfv;

import android.app.Dialog;
import android.nfc.Tag;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.mkpolo.appgfv.modelo.categoria.Categoria;
import com.mkpolo.appgfv.modelo.marca.Marca;
import com.mkpolo.appgfv.modelo.producto.Producto;
import com.mkpolo.appgfv.modelo.producto.ProductoAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragmento_producto#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragmento_producto extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private RequestQueue requestQueue;
    private SwipeRefreshLayout refresh;
    private ArrayList<Producto> producto = new ArrayList<>();

    private ArrayList<Marca> listaMarcas;
    private ArrayList<Categoria> listcategoria;

    private JsonArrayRequest arrayRequest;
    private RecyclerView recyclerView;
    private Dialog dialog;
    private ProductoAdapter productoAdapter;
    private ImageView clickAgregarProducto;

    private ImageView editProducto;

    private String url="http://192.168.1.60:9001/api/productos/";
    private String url2 = "http://192.168.1.60:9001/api/categorias/";
    private String url3 = "http://192.168.1.60:9001/api/marcas/";

    private Categoria categoriaspinner = new Categoria();
    private Marca marcaspinner = new Marca();

    ArrayList<String> agregarComboMarca = new ArrayList<String>();
    ArrayList<String> agregarComboCat = new ArrayList<String>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragmento_producto() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragmento_producto.
     */
    // TODO: Rename and change types and number of parameters
    public static fragmento_producto newInstance(String param1, String param2) {
        fragmento_producto fragment = new fragmento_producto();
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
        View view = inflater.inflate(R.layout.fragment_fragmento_producto, container, false);

        refresh = view.findViewById(R.id.swipedownProducto);
        recyclerView = view.findViewById(R.id.recyclerProducto);
        clickAgregarProducto = view.findViewById(R.id.clickAgregarProducto);


        consultarListaCategorias();
        consultarListaMarcas();

        dialog = new Dialog(getContext());

        clickAgregarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProducto();
            }
        });

        refresh.setOnRefreshListener(this);

        refresh.post(new Runnable() {
            @Override
            public void run() {
                producto.clear();
                getDataProducto();
            }
        });

        return view;
    }


    private void addProducto() {
        TextView closeProducto,tittleProducto;
        final EditText txtnombreProducto, txtpesoProducto, txtdiasProducto;
        final Spinner spnMarcas, spnCategorias;
        Button submitProducto;

        dialog.setContentView(R.layout.fragment_modproducto);

        closeProducto = (TextView) dialog.findViewById(R.id.txtCerrarProducto);
        tittleProducto = (TextView) dialog.findViewById(R.id.tituloProducto);
        tittleProducto.setText("Agregar Producto");

        closeProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        spnCategorias = (Spinner) dialog.findViewById(R.id.spnCategoriaProducto);
        spnMarcas = (Spinner) dialog.findViewById(R.id.spnMarcaProducto);
        txtnombreProducto = (EditText)dialog.findViewById(R.id.txtnombreProducto);
        txtpesoProducto = (EditText) dialog.findViewById(R.id.txtpesoProducto);
        txtdiasProducto = (EditText) dialog.findViewById(R.id.txtdiasProducto);
        submitProducto = (Button) dialog.findViewById(R.id.submitProducto);

        ArrayAdapter<CharSequence> adaptadorMarca = new ArrayAdapter(getContext(),R.layout.support_simple_spinner_dropdown_item, agregarComboMarca);
        spnMarcas.setAdapter(adaptadorMarca);

        spnMarcas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                marcaspinner.setId(listaMarcas.get(position).getId());
                marcaspinner.setMarca(listaMarcas.get(position).getMarca());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> adaptadorCat = new ArrayAdapter(getContext(),R.layout.support_simple_spinner_dropdown_item, agregarComboCat);
        spnCategorias.setAdapter(adaptadorCat);

        spnCategorias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoriaspinner.setId(listcategoria.get(position).getId());
                categoriaspinner.setCategoria(listcategoria.get(position).getCategoria());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

       submitProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dataProducto = "{\"categoria\":{\"idCategoria\":"+ categoriaspinner.getId() +",\"categoria\":\""
                        + categoriaspinner.getCategoria() + "\"},\"marca\":{\"idMarca\":" + marcaspinner.getId() + ",\"marca\":\""
                        + marcaspinner.getMarca() +"\"},\"producto\":\"" + txtnombreProducto.getText().toString() + "\",\"peso\":\""
                        + txtpesoProducto.getText().toString() + "\",\"acciones\":null,\"dias\":" + txtdiasProducto.getText().toString() + "}";
                SubmitProducto(dataProducto);
            }
        });
        dialog.show();
    }

    private void SubmitProducto(String dataProducto) {
        final  String saveProducto = dataProducto;
        StringRequest request =new StringRequest(Request.Method.POST, url + "save", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject objcat= new JSONObject(response);
                    dialog.dismiss();
                    refresh.post(new Runnable() {
                        @Override
                        public void run() {
                            producto.clear();
                            getDataProducto();
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
                    return saveProducto == null ? null : saveProducto.getBytes("utf-8");
                }catch (UnsupportedEncodingException uee){
                    return null;
                }
            }
        };
        Volley.newRequestQueue(getContext()).add(request);
    }

    private void consultarListaCategorias() {
        listcategoria = new ArrayList<>();
        arrayRequest = new JsonArrayRequest(url2 + "all", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;

                    try {
                        for (int i = 0; i < response.length(); i++) {
                        jsonObject = response.getJSONObject(i);

                        Categoria liscat = new Categoria();
                        liscat.setId(jsonObject.getInt("idCategoria"));
                        liscat.setCategoria(jsonObject.getString("categoria"));
                        listcategoria.add(liscat);

                        } } catch (JSONException e) {
                        e.printStackTrace();
                    }

                mostrarListaCategoria();
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

    private void mostrarListaCategoria() {
        for(int i = 0; i<listcategoria.size(); i++){
            agregarComboCat.add(listcategoria.get(i).getCategoria());
        }
    }

    private void consultarListaMarcas() {
        listaMarcas = new ArrayList<>();
        arrayRequest = new JsonArrayRequest(url3 + "all", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;

                try {
                    for (int i = 0; i < response.length(); i++) {
                        jsonObject = response.getJSONObject(i);

                        Marca lismarc = new Marca();
                        lismarc.setId(jsonObject.getInt("idMarca"));
                        lismarc.setMarca(jsonObject.getString("marca"));
                        listaMarcas.add(lismarc);

                    } } catch (JSONException e) {
                    e.printStackTrace();
                }

                mostrarListaMarca();
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

    private void mostrarListaMarca() {
        for(int i = 0; i<listaMarcas.size(); i++){
            agregarComboMarca.add(listaMarcas.get(i).getMarca());
        }
    }

    private void getDataProducto() {
        refresh.setRefreshing(true);
        arrayRequest = new JsonArrayRequest(url + "all", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                JSONObject categor = null;
                JSONObject marca = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);

                        Producto produc = new Producto();
                        produc.setId(jsonObject.getInt("idProducto"));
                        Categoria cat = new Categoria();
                        categor = jsonObject.getJSONObject("categoria");
                        cat.setId(categor.getInt("idCategoria"));
                        cat.setCategoria(categor.getString("categoria"));
                        produc.setCategoria(cat);
                        Marca mar = new Marca();
                        marca = jsonObject.getJSONObject("marca");
                        mar.setId(marca.getInt("idMarca"));
                        mar.setMarca(marca.getString("marca"));
                        produc.setMarca(mar);
                        produc.setNombreProducto(jsonObject.getString("producto"));
                        produc.setPesoProducto(jsonObject.getString("peso"));
                        produc.setDiasProducto(jsonObject.getInt("dias"));
                        producto.add(produc);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapterPush(producto);
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

    private void adapterPush(final ArrayList<Producto> producto) {
        productoAdapter = new ProductoAdapter(getContext(), producto);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(productoAdapter);

        productoAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String categoria = producto.get(recyclerView.getChildAdapterPosition(v)).getCategoria().getCategoria();
                String marca = producto.get(recyclerView.getChildAdapterPosition(v)).getMarca().getMarca();
                String produc = producto.get(recyclerView.getChildAdapterPosition(v)).getNombreProducto();
                String peso = producto.get(recyclerView.getChildAdapterPosition(v)).getPesoProducto();
                int dias = producto.get(recyclerView.getChildAdapterPosition(v)).getDiasProducto();
                int id = producto.get(recyclerView.getChildAdapterPosition(v)).getId();

                editarProducto(categoria,marca,produc,peso,dias,id);

            }
        });
    }

    private void editarProducto(String categoria, String marca, String produc, String peso, int dias, final int id) {
        TextView closeProducto,tittleProducto;
        final EditText txtnombreProducto, txtpesoProducto, txtdiasProducto;
        final Spinner spnMarcas, spnCategorias;
        Button submitProducto;

        dialog.setContentView(R.layout.fragment_modproducto);

        closeProducto = (TextView) dialog.findViewById(R.id.txtCerrarProducto);
        tittleProducto = (TextView) dialog.findViewById(R.id.tituloProducto);
        tittleProducto.setText("Editar Producto");

        closeProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        spnCategorias = (Spinner) dialog.findViewById(R.id.spnCategoriaProducto);
        spnMarcas = (Spinner) dialog.findViewById(R.id.spnMarcaProducto);
        txtnombreProducto = (EditText)dialog.findViewById(R.id.txtnombreProducto);
        txtpesoProducto = (EditText) dialog.findViewById(R.id.txtpesoProducto);
        txtdiasProducto = (EditText) dialog.findViewById(R.id.txtdiasProducto);
        submitProducto = (Button) dialog.findViewById(R.id.submitProducto);

        ArrayAdapter<CharSequence> adaptadorMarca = new ArrayAdapter(getContext(),R.layout.support_simple_spinner_dropdown_item, agregarComboMarca);
        spnMarcas.setAdapter(adaptadorMarca);

        spnMarcas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                marcaspinner.setId(listaMarcas.get(position).getId());
                marcaspinner.setMarca(listaMarcas.get(position).getMarca());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> adaptadorCat = new ArrayAdapter(getContext(),R.layout.support_simple_spinner_dropdown_item, agregarComboCat);
        spnCategorias.setAdapter(adaptadorCat);

        spnCategorias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoriaspinner.setId(listcategoria.get(position).getId());
                categoriaspinner.setCategoria(listcategoria.get(position).getCategoria());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        txtnombreProducto.setText(produc);
        txtpesoProducto.setText(peso);
        txtdiasProducto.setText(String.valueOf(dias));

        submitProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dataProducto = "{\"categoria\":{\"idCategoria\":"+ categoriaspinner.getId() +",\"categoria\":\""
                        + categoriaspinner.getCategoria() + "\"},\"marca\":{\"idMarca\":" + marcaspinner.getId() + ",\"marca\":\""
                        + marcaspinner.getMarca() +"\"},\"producto\":\"" + txtnombreProducto.getText().toString() + "\",\"peso\":\""
                        + txtpesoProducto.getText().toString() + "\",\"acciones\":null,\"dias\":" + txtdiasProducto.getText().toString()
                        + ",\"idProducto\":" + id + "}";
                SubmitProducto(dataProducto);
            }
        });
        dialog.show();
    }

    @Override
    public void onRefresh() {
        producto.clear();
        getDataProducto();
    }
}
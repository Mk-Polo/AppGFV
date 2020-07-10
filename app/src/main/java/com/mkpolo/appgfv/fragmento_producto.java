package com.mkpolo.appgfv;

import android.app.Dialog;
import android.nfc.Tag;
import android.os.Bundle;

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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.mkpolo.appgfv.modelo.categoria.Categoria;
import com.mkpolo.appgfv.modelo.marca.Marca;
import com.mkpolo.appgfv.modelo.producto.Producto;
import com.mkpolo.appgfv.modelo.producto.ProductoAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private ArrayList<Marca> marcas = new ArrayList<>();
    private ArrayList<Categoria> listcategoria = new ArrayList<>();
    private ArrayList<String> listaCategoria;
    private JsonArrayRequest arrayRequest;
    private RecyclerView recyclerView;
    private Dialog dialog;
    private ProductoAdapter productoAdapter;
    private ImageView clickAgregarProducto;
    private String url="http://192.168.1.60:9001/api/productos/";
    private String url2 = "http://192.168.1.60:9001/api/categorias/";

    ArrayList<String> arrastrig = new ArrayList<String>();

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

        final String idCategoria, nombreCategoria;

        spnCategorias = (Spinner) dialog.findViewById(R.id.spnCategoriaProducto);
        spnMarcas = (Spinner) dialog.findViewById(R.id.spnMarcaProducto);
        txtnombreProducto = (EditText)dialog.findViewById(R.id.txtnombreProducto);
        txtpesoProducto = (EditText) dialog.findViewById(R.id.txtpesoProducto);
        txtdiasProducto = (EditText) dialog.findViewById(R.id.txtdiasProducto);
        submitProducto = (Button) dialog.findViewById(R.id.submitMarca);

        consultarListaCategorias();

        ArrayAdapter<CharSequence> adaptadorCat = new ArrayAdapter(getContext(),R.layout.support_simple_spinner_dropdown_item,arrastrig);
        spnCategorias.setAdapter(adaptadorCat);

        spnCategorias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

       /* submitProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dataProducto = "{"+"\"marca\"" +":"+ "\"" + txtnombreProducto.getText().toString() + "\""+"}";
               // SubmitMarca(dataProducto);
            }
        });*/
        dialog.show();
    }

    private void consultarListaCategorias() {
        mostrarListaCategoria();



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
        arrastrig.add("Seleccione: ");


        for(int i = 0; i<listcategoria.size(); i++){
            arrastrig.add(listcategoria.get(i).getCategoria());
       }
    }

    private void getDataProducto() {
        refresh.setRefreshing(true);
        arrayRequest = new JsonArrayRequest(url + "all", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                JSONObject marca = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);

                        Producto produc = new Producto();
                        produc.setId(jsonObject.getInt("idProducto"));
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

    private void adapterPush(ArrayList<Producto> producto) {
        productoAdapter = new ProductoAdapter(getContext(), producto);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(productoAdapter);
    }

    @Override
    public void onRefresh() {
        producto.clear();
        getDataProducto();
    }
}
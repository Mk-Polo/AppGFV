package com.mkpolo.appgfv;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.zxing.integration.android.IntentIntegrator;

import android.provider.MediaStore;
import android.util.Base64;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentResult;
import com.mkpolo.appgfv.modelo.categoria.Categoria;
import com.mkpolo.appgfv.modelo.marca.Marca;
import com.mkpolo.appgfv.modelo.producto.Producto;
import com.mkpolo.appgfv.modelo.producto.ProductoAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragmento_producto#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragmento_producto extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    //opcines de la foto
    private static final int COD_SELECCIONA = 10;
    private static final int COD_FOTO = 20;
    private static final int REQUEST_PERMISSION_CAMERA = 101;

    private static final String CARPETA_PRINCIPAL = "misImagenesApp/";
    private static final String CARPETA_IMAGEN = "imagenes";
    private static final String DIRECTORIO_IMAGEN = CARPETA_PRINCIPAL + CARPETA_IMAGEN;

    private RequestQueue requestQueue;
    private SwipeRefreshLayout refresh;
    private ArrayList<Producto> producto = new ArrayList<>();

    private ArrayList<Marca> listaMarcas;
    private ArrayList<Categoria> listcategoria;

    private JsonArrayRequest arrayRequest;
    private RecyclerView recyclerView;
    private Dialog dialog;
    private ProductoAdapter productoAdapter;
    private FloatingActionButton clickAgregarProducto;

    private String url="http://192.168.1.60:9001/api/productos/";
    private String url2 = "http://192.168.1.60:9001/api/categorias/";
    private String url3 = "http://192.168.1.60:9001/api/marcas/";

    private Categoria categoriaspinner = new Categoria();
    private Marca marcaspinner = new Marca();

    //opcines de tomar foto
    private ImageView imgFoto;
    private String path;
    File fileImage;
    Bitmap bitmap;

    //Scanner
    private EditText txtCodBarras;

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
        final Spinner spnMarcas, spnCategorias;;
        Button submitProducto, agregarFoto, btnBarra;;

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
        imgFoto =(ImageView) dialog.findViewById(R.id.fotoProducto);
        agregarFoto =(Button) dialog.findViewById(R.id.btnAgregarFoto);
        submitProducto = (Button) dialog.findViewById(R.id.submitProducto);

        btnBarra = (Button) dialog.findViewById(R.id.btnBarra);
        txtCodBarras = (EditText) dialog.findViewById(R.id.txtBarra);

        agregarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarOpcionesFoto();
            }
        });

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
                Toast.makeText(getContext(), "Seleccionar una Marca", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), "Seleccionar una Categoría", Toast.LENGTH_SHORT).show();
            }
        });

        btnBarra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escanear();
            }
        });

        submitProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String imagen;
                if(bitmap != null) {
                    imagen = convertirImgString(bitmap);
                }else{
                    imagen = null;
                }
                String dataProducto = "{\"categoria\":{\"idCategoria\":"+ categoriaspinner.getId() +",\"categoria\":\""
                        + categoriaspinner.getCategoria() + "\"},\"marca\":{\"idMarca\":" + marcaspinner.getId() + ",\"marca\":\""
                        + marcaspinner.getMarca() +"\"},\"producto\":\"" + txtnombreProducto.getText().toString() + "\",\"peso\":\""
                        + txtpesoProducto.getText().toString() + "\",\"acciones\":null,\"dias\":" + txtdiasProducto.getText().toString()
                        + ",\"imagen\":\"" + imagen + "\",\"barra\":"+ String.valueOf(txtCodBarras.getText().toString()) +"}";
                SubmitProducto(dataProducto);

            }
        });
        dialog.show();

    }

    private void escanear() {
        IntentIntegrator.forSupportFragment(fragmento_producto.this).initiateScan();
    }

    private String convertirImgString(Bitmap bitmap) {

        ByteArrayOutputStream array = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,array);
        byte[] imagenByte = array.toByteArray();
        String imagenString = Base64.encodeToString(imagenByte,Base64.NO_WRAP);

        return imagenString;
    }

    private void mostrarOpcionesFoto() {
        final CharSequence[] opciones = {"Elegir Galeria","Tomar Foto","Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Elige una opcion");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(opciones[i].equals("Elegir Galeria")){
                    Intent intent= new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/");
                    startActivityForResult(intent.createChooser(intent,"Seleccione"),COD_SELECCIONA);
                }else if(opciones[i].equals("Tomar Foto"))
                {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                            abrirCamara();
                        }else{
                            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
                        }
                    }else {
                        abrirCamara();
                    }
                }
                else
                {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    //Mostramos el cuadro de dialogo para otorgar permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_PERMISSION_CAMERA){
            if(permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                abrirCamara();
            }else{
                Toast.makeText(getContext(), "Se necesita habilitar la cámara", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void abrirCamara() {

        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(intent.resolveActivity(getContext().getPackageManager()) != null){
            startActivityForResult(intent,COD_FOTO);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case COD_SELECCIONA:
                if(resultCode == Activity.RESULT_OK) { //Validamos la respuesta de la galeria
                    Uri miPath = data.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), miPath);
                        imgFoto.setImageBitmap(bitmap);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
                break;
            case COD_FOTO:
                if(resultCode == Activity.RESULT_OK) { //validamos la respuesta de la camara
                    bitmap = (Bitmap) data.getExtras().get("data");
                    imgFoto.setImageBitmap(bitmap);
                }
                break;
            case 49374:
                if(resultCode == Activity.RESULT_OK){
                    txtCodBarras.setText(result.getContents());
                }else {
                    Toast.makeText(getContext(), "CANCELASTE EL ESCANER", Toast.LENGTH_SHORT).show();
                }
                break;
        }

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
                        produc.setDato(jsonObject.getString("imagen"));
                        produc.setBarra(jsonObject.getLong("barra"));
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
                Long codigoBarra = producto.get(recyclerView.getChildAdapterPosition(v)).getBarra();
                Bitmap enviarImagen = producto.get(recyclerView.getChildAdapterPosition(v)).getImagen();
                //String imagen = convertirImgString(producto.get(recyclerView.getChildAdapterPosition(v)).getImagen());
                editarProducto(categoria,marca,produc,peso,dias,enviarImagen,id,codigoBarra);

            }
        });
    }

    private void editarProducto(String categoria, String marca, String produc, String peso, int dias, Bitmap enviarImagen, final int id, Long codigoBarra) {
        TextView closeProducto,tittleProducto;
        final EditText txtnombreProducto, txtpesoProducto, txtdiasProducto;
        final Spinner spnMarcas, spnCategorias;
        Button submitProducto, agregarFoto, btnBarra;

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
        imgFoto =(ImageView) dialog.findViewById(R.id.fotoProducto);
        agregarFoto =(Button) dialog.findViewById(R.id.btnAgregarFoto);
        submitProducto = (Button) dialog.findViewById(R.id.submitProducto);

        btnBarra = (Button) dialog.findViewById(R.id.btnBarra);
        txtCodBarras = (EditText) dialog.findViewById(R.id.txtBarra);

        bitmap = enviarImagen;

        agregarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarOpcionesFoto();
            }
        });

        btnBarra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escanear();
            }
        });

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
        imgFoto.setImageBitmap(enviarImagen);
        txtCodBarras.setText(String.valueOf(codigoBarra));

        submitProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String imagen;
                if(bitmap != null) {
                    imagen = convertirImgString(bitmap);
                }else{
                    imagen = null;
                }

                String dataProducto = "{\"categoria\":{\"idCategoria\":"+ categoriaspinner.getId() +",\"categoria\":\""
                        + categoriaspinner.getCategoria() + "\"},\"marca\":{\"idMarca\":" + marcaspinner.getId() + ",\"marca\":\""
                        + marcaspinner.getMarca() +"\"},\"producto\":\"" + txtnombreProducto.getText().toString() + "\",\"peso\":\""
                        + txtpesoProducto.getText().toString() + "\",\"acciones\":null,\"dias\":" + txtdiasProducto.getText().toString()
                        + ",\"imagen\":\"" + imagen + "\",\"barra\":" + String.valueOf(txtCodBarras.getText().toString()) + ",\"idProducto\":" + id + "}";
                SubmitProducto(dataProducto);
                Log.i("pATH", "" + dataProducto);
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
package com.mkpolo.appgfv;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.mkpolo.appgfv.modelo.marca.Marca;
import com.mkpolo.appgfv.modelo.marca.MarcaAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragmento_marca#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragmento_marca extends Fragment implements  SwipeRefreshLayout.OnRefreshListener{

    private RequestQueue requestQueue;
    private SwipeRefreshLayout refresh;
    private ArrayList<Marca> marca = new ArrayList<>();
    private JsonArrayRequest arrayRequest;
    private RecyclerView recyclerView;
    private Dialog dialog;
    private MarcaAdapter marcaAdapter;
    private ImageView clickAgregar;
    private String url="http://192.168.1.60:9001/api/marcas/";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragmento_marca() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragmento_marca.
     */
    // TODO: Rename and change types and number of parameters
    public static fragmento_marca newInstance(String param1, String param2) {
        fragmento_marca fragment = new fragmento_marca();
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
        View view = inflater.inflate(R.layout.fragment_fragmento_marca, container, false);

        refresh = view.findViewById(R.id.swipedownMarca);
        recyclerView = view.findViewById(R.id.recyclerMarca);
        clickAgregar = view.findViewById(R.id.clickAgregar);

         dialog = new Dialog(getContext());

        clickAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMarca();
            }
        });

        refresh.setOnRefreshListener(this);

        refresh.post(new Runnable() {
            @Override
            public void run() {
                marca.clear();
                getData();
            }
        });

        return view;
    }

    private void getData() {
        refresh.setRefreshing(true);
        arrayRequest = new JsonArrayRequest(url + "all", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);

                           Marca marc = new Marca();
                           marc.setId(jsonObject.getInt("idMarca"));
                           marc.setMarca(jsonObject.getString("marca"));
                           marca.add(marc);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapterPush(marca);
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

    private void adapterPush(ArrayList<Marca> marca) {
        marcaAdapter = new MarcaAdapter(getContext(), marca);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(marcaAdapter);
    }

    private void addMarca(){
        TextView closeMarca,tittleMarca;
        final EditText edtxtMarca;
        Button submitMarca;
        dialog.setContentView(R.layout.fragment_modmarca);

        closeMarca = (TextView) dialog.findViewById(R.id.txtCerrar);
        tittleMarca = (TextView) dialog.findViewById(R.id.tituloMarca);
        tittleMarca.setText("Agregar Marca");

        closeMarca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        edtxtMarca = (EditText)dialog.findViewById(R.id.edtxtMarca);
        submitMarca = (Button) dialog.findViewById(R.id.submitMarca);

        submitMarca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dataMarca = "{"+"\"marca\"" +":"+ "\"" + edtxtMarca.getText().toString() + "\""+"}";
                SubmitMarca(dataMarca);
            }
        });
        dialog.show();
    }

    private void SubmitMarca(String dataMarca) {
        final  String saveMarca = dataMarca;
        StringRequest request =new StringRequest(Request.Method.POST, url + "save", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject objmar = new JSONObject(response);
                    dialog.dismiss();
                    refresh.post(new Runnable() {
                        @Override
                        public void run() {
                            marca.clear();
                            getData();
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
                    return saveMarca == null ? null : saveMarca.getBytes("utf-8");
                }catch (UnsupportedEncodingException uee){
                    return null;
                }
            }
        };
        Volley.newRequestQueue(getContext()).add(request);
    }

    @Override
    public void onRefresh() {
        marca.clear();
        getData();
    }
}
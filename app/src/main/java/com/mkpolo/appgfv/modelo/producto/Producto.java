package com.mkpolo.appgfv.modelo.producto;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.mkpolo.appgfv.modelo.categoria.Categoria;
import com.mkpolo.appgfv.modelo.marca.Marca;

public class Producto {

    int id;

    Categoria categoria;

    Marca marca;

    String nombreProducto;

    String pesoProducto;

    int diasProducto;

    Long barra;

    private String dato;

    private Bitmap imagen;

    private String rutaImagen;

    public Producto() {
    }

    public Producto(int id, Categoria categoria, Marca marca, String nombreProducto, String pesoProducto, int diasProducto, Long barra, String dato, Bitmap imagen, String rutaImagen) {
        this.id = id;
        this.categoria = categoria;
        this.marca = marca;
        this.nombreProducto = nombreProducto;
        this.pesoProducto = pesoProducto;
        this.diasProducto = diasProducto;
        this.barra = barra;
        this.dato = dato;
        this.imagen = imagen;
        this.rutaImagen = rutaImagen;
    }

    public String getDato() {
        return dato;
    }

    public void setDato(String dato) {
        this.dato = dato;

        try{
            byte[] byteCode= Base64.decode(dato,Base64.DEFAULT);
            //this.imagen= BitmapFactory.decodeByteArray(byteCode,0,byteCode.length);

            int alto=100;//alto en pixeles
            int ancho=100;//ancho en pixeles

            Bitmap foto= BitmapFactory.decodeByteArray(byteCode,0,byteCode.length);
            this.imagen=Bitmap.createScaledBitmap(foto,alto,ancho,true);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public Long getBarra() {
        return barra;
    }

    public void setBarra(Long barra) {
        this.barra = barra;
    }

    public Bitmap getImagen() {
        return imagen;
    }

    public void setImagen(Bitmap imagen) {
        this.imagen = imagen;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getPesoProducto() {
        return pesoProducto;
    }

    public void setPesoProducto(String pesoProducto) {
        this.pesoProducto = pesoProducto;
    }

    public int getDiasProducto() {
        return diasProducto;
    }

    public void setDiasProducto(int diasProducto) {
        this.diasProducto = diasProducto;
    }
}

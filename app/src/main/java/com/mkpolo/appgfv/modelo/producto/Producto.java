package com.mkpolo.appgfv.modelo.producto;

import com.mkpolo.appgfv.modelo.categoria.Categoria;
import com.mkpolo.appgfv.modelo.marca.Marca;

public class Producto {

    int id;

    Categoria categoria;

    Marca marca;

    String nombreProducto;

    String pesoProducto;

    int diasProducto;

    public Producto() {
    }

    public Producto(int id, Categoria categoria, Marca marca, String nombreProducto, String pesoProducto, int diasProducto) {
        this.id = id;
        this.categoria = categoria;
        this.marca = marca;
        this.nombreProducto = nombreProducto;
        this.pesoProducto = pesoProducto;
        this.diasProducto = diasProducto;
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

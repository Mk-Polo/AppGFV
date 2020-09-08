package com.mkpolo.appgfv.modelo.alerta;

import com.mkpolo.appgfv.modelo.producto.Producto;

import java.util.Date;

public class Alerta {

    private int id;

    private Producto producto;

    private String estado;

    private Date vencimiento;

    public Alerta() {
    }

    public Alerta(int id, Producto producto, String estado, Date vencimiento) {
        this.id = id;
        this.producto = producto;
        this.estado = estado;
        this.vencimiento = vencimiento;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getVencimiento() {
        return vencimiento;
    }

    public void setVencimiento(Date vencimiento) {
        this.vencimiento = vencimiento;
    }
}

package com.sistema.productos.model.dto;

import java.util.List;
import java.util.Set;

public class TipoProductoDTO {

    private Long id;
    private String nombre;
    private int estado;
    private List<String> nombresCategorias;
    private Set<Long> idsCategorias;
    
    public TipoProductoDTO() {
    }

    public TipoProductoDTO(Long id, String nombre, int estado, List<String> nombresCategorias, Set<Long> idsCategorias) {
        this.id = id;
        this.nombre = nombre;
        this.estado = estado;
        this.nombresCategorias = nombresCategorias;
        this.idsCategorias = idsCategorias;
    }

    public Set<Long> getIdsCategorias() {
        return idsCategorias;
    }

    public void setIdsCategorias(Set<Long> idsCategorias) {
        this.idsCategorias = idsCategorias;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public List<String> getNombresCategorias() {
        return nombresCategorias;
    }

    public void setNombresCategorias(List<String> nombresCategorias) {
        this.nombresCategorias = nombresCategorias;
    }
}
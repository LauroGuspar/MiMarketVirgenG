package com.sistema.productos.service;

import com.sistema.productos.model.Unidad;

import java.util.List;
import java.util.Optional;

public interface UnidadService {
    List<Unidad> listarTodasLasUnidades();
    Optional<Unidad> obtenerUnidadPorId(Long id);
    Unidad guardarUnidad(Unidad unidad);
    void eliminarUnidad(Long id);
    boolean cambiarEstadoUnidad(Long id);
}
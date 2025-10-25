package com.sistema.productos.service;

import java.util.List;
import java.util.Optional;

import com.sistema.productos.model.Marca;

public interface MarcaService {
    
    List<Marca> listarTodasLasMarcas();
    Optional<Marca> obtenerMarcaPorId(Long id);
    Marca guardarMarca(Marca marca);
    void eliminarMarca(Long id);
    boolean cambiarEstadoMarca(Long id);
}
package com.sistema.productos.service;

import java.util.List;
import java.util.Optional;
import com.sistema.productos.model.TipoProducto;
import com.sistema.productos.model.dto.TipoProductoDTO;

public interface TipoProductoService {
    List<TipoProductoDTO> listarTodosLosTiposProducto(Long idCategoria);
    Optional<TipoProductoDTO> obtenerTipoProductoDTOPorId(Long id);
    TipoProducto guardarTipoProducto(TipoProducto tipoProducto);
    void eliminarTipoProducto(Long id);
    boolean cambiarEstadoTipoProducto(Long id);
    long contarTiposProducto();
}
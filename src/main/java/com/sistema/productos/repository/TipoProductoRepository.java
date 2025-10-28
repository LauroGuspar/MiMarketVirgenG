package com.sistema.productos.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.sistema.productos.model.TipoProducto;

@Repository
public interface TipoProductoRepository extends JpaRepository<TipoProducto, Long> {
    @Query("SELECT DISTINCT t FROM TipoProducto t LEFT JOIN FETCH t.categorias WHERE t.estado != :estado")
    List<TipoProducto> findAllWithCategoriasByEstadoNot(int estado);

    long countByEstadoNot(Integer estado);

    @Modifying
    @Query("UPDATE TipoProducto t SET t.estado = :nuevoEstado WHERE t.id = :id")
    void actualizarEstado(Long id, int nuevoEstado);

    @Query("SELECT t FROM TipoProducto t LEFT JOIN FETCH t.categorias WHERE t.id = :id")
    Optional<TipoProducto> findByIdWithCategorias(Long id);

    List<TipoProducto> findByCategorias_IdAndEstadoNot(Long categoriaId, int estado);

    @Query("SELECT DISTINCT t FROM TipoProducto t LEFT JOIN FETCH t.categorias c WHERE t.estado != :estado AND c.id = :idCategoria")
    List<TipoProducto> findAllWithCategoriasByCategoriaAndEstadoNot(Long idCategoria, int estado);
}
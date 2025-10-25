package com.sistema.productos.repository;

import com.sistema.productos.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByCorreo(String correo);
    Optional<Cliente> findByCorreoIgnoreCase(String correo);
    Optional<Cliente> findByTipodocumento_IdAndNdocumento(Long tipodocumentoId, String ndocumento);
    boolean existsByCorreo(String correo);
    List<Cliente> findAllByEstadoNot(Integer estado);
    long countByEstadoNot(Integer estado);
    @Modifying
    @Query("UPDATE Cliente c SET c.estado = :nuevoEstado WHERE c.id = :id")
    void actualizarEstado(Long id, Integer nuevoEstado);
}

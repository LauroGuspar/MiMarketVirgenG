package com.sistema.productos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sistema.productos.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsuario(String usuario);
    Optional<Usuario> findByCorreo(String correo);
    Optional<Usuario> findByUsuarioIgnoreCase(String usuario);
    Optional<Usuario> findByCorreoIgnoreCase(String correo);
    Optional<Usuario> findByTipodocumento_IdAndNdocumento(Long tipodocumentoId, String ndocumento);
    Optional<Usuario> findByTelefonoIgnoreCase(String telefono);
    boolean existsByUsuarioIgnoreCase(String usuario);
    boolean existsByCorreoIgnoreCase(String correo);
    boolean existsByTelefonoIgnoreCase(String telefono);
    boolean existsByTipodocumento_IdAndNdocumento(Long tipodocumentoId, String ndocumento);
    List<Usuario> findAllByEstadoNot(Integer estado);
    long countByEstadoNot(Integer estado);
    @Modifying
    @Query("UPDATE Usuario u SET u.estado = :nuevoEstado WHERE u.id = :id")
    void actualizarEstado(Long id, Integer nuevoEstado);
}
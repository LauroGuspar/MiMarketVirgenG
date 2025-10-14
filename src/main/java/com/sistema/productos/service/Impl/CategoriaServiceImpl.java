package com.sistema.productos.service.Impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.sistema.productos.model.Categoria;
import com.sistema.productos.repository.CategoriaRepository;
import com.sistema.productos.service.CategoriaService;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Value("${file.upload-dir.categorias}")
    private String uploadDir;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> listarTodasLasCategorias() {
        return categoriaRepository.findAllByEstadoNot(2);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Categoria> obtenerCategoriaPorId(Long id) {
        return categoriaRepository.findById(id);
    }

    @Override
    @Transactional
    public Categoria guardarCategoria(Categoria categoria, MultipartFile imagenFile) {
        if (imagenFile != null && !imagenFile.isEmpty()) {
            if (categoria.getId() != null && categoria.getImg() != null) {
                eliminarImagenFisica(categoria.getImg());
            }

            String nombreUnico = UUID.randomUUID().toString() + "_" + imagenFile.getOriginalFilename();
            Path rutaAbsoluta = Paths.get(uploadDir).resolve(nombreUnico).toAbsolutePath();

            try {
                Files.createDirectories(rutaAbsoluta.getParent());
                Files.write(rutaAbsoluta, imagenFile.getBytes());
                categoria.setImg(nombreUnico);
            } catch (IOException e) {
                throw new RuntimeException("Error al guardar la imagen", e);
            }
        }

        if (categoria.getId() == null) {
            Optional<Categoria> existenteOpt = categoriaRepository.findByNombreIgnoreCase(categoria.getNombre());
            if (existenteOpt.isPresent()) {
                Categoria existente = existenteOpt.get();
                if (existente.getEstado() == 2) {
                    existente.setEstado(1);
                    existente.setNombre(categoria.getNombre());
                    if (categoria.getImg() != null) {
                        existente.setImg(categoria.getImg());
                    }
                    return categoriaRepository.save(existente);
                } else {
                    throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + categoria.getNombre());
                }
            }
        }

        return categoriaRepository.save(categoria);
    }

    private void eliminarImagenFisica(String nombreImagen) {
        if (nombreImagen == null || nombreImagen.isEmpty()) return;
        try {
            Path rutaArchivo = Paths.get(uploadDir).resolve(nombreImagen).toAbsolutePath();
            Files.deleteIfExists(rutaArchivo);
        } catch (IOException e) {
            System.err.println("Error al eliminar la imagen: " + e.getMessage());
        }
    }

     @Override
    @Transactional
    public void eliminarCategoria(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + id));
        eliminarImagenFisica(categoria.getImg());

        categoria.setEstado(2);
        categoria.setImg(null);
        categoriaRepository.save(categoria);
    }

    @Override
    @Transactional
    public boolean cambiarEstadoCategoria(Long id) {
        Optional<Categoria> categoriaOpt = categoriaRepository.findById(id);
        if (categoriaOpt.isEmpty()) {
            return false;
        }
        Categoria categoria = categoriaOpt.get();
        if (categoria.getEstado() == 1) {
            categoriaRepository.actualizarEstado(id, 0);
        } else if (categoria.getEstado() == 0) {
            categoriaRepository.actualizarEstado(id, 1);
        }
        return true;
    }
}
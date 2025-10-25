package com.sistema.productos.service.Impl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sistema.productos.model.Categoria;
import com.sistema.productos.model.TipoProducto;
import com.sistema.productos.model.dto.TipoProductoDTO;
import com.sistema.productos.repository.CategoriaRepository;
import com.sistema.productos.repository.TipoProductoRepository;
import com.sistema.productos.service.TipoProductoService;

@Service
public class TipoProductoServiceImpl implements TipoProductoService {

    private final TipoProductoRepository tipoProductoRepository;
    private final CategoriaRepository categoriaRepository;

    public TipoProductoServiceImpl(TipoProductoRepository tipoProductoRepository,  CategoriaRepository categoriaRepository) {
        this.tipoProductoRepository = tipoProductoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoProductoDTO> listarTodosLosTiposProducto(Long idCategoria) {
        List<TipoProducto> tipos;
        if (idCategoria != null) {
            tipos = tipoProductoRepository.findAllWithCategoriasByCategoriaAndEstadoNot(idCategoria, 2);
        } else {
            tipos = tipoProductoRepository.findAllWithCategoriasByEstadoNot(2);
        }
        return tipos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TipoProductoDTO> obtenerTipoProductoDTOPorId(Long id) {
        return tipoProductoRepository.findByIdWithCategorias(id).map(this::convertirADTO);
    }

    private TipoProductoDTO convertirADTO(TipoProducto tipo) {
        List<String> nombresCategorias = tipo.getCategorias().stream().map(Categoria::getNombre).collect(Collectors.toList());
        Set<Long> idsCategorias = tipo.getCategorias().stream().map(Categoria::getId).collect(Collectors.toSet());
        
        return new TipoProductoDTO(
            tipo.getId(),
            tipo.getNombre(),
            tipo.getEstado(),
            nombresCategorias,
            idsCategorias
        );
    }

    @Override
    @Transactional
    public TipoProducto guardarTipoProducto(TipoProducto tipoProducto) {
        Set<Categoria> categoriasManejadas = procesarCategorias(tipoProducto.getCategorias());

        if (tipoProducto.getId() != null) {
            TipoProducto existente = tipoProductoRepository.findById(tipoProducto.getId()).orElseThrow(() -> new RuntimeException("TipoProducto no encontrado"));
            existente.setNombre(tipoProducto.getNombre());
            existente.setCategorias(categoriasManejadas);
            return tipoProductoRepository.save(existente);
        } else {
            tipoProducto.setCategorias(categoriasManejadas);
            tipoProducto.setEstado(1);
            return tipoProductoRepository.save(tipoProducto);
        }
    }

    @Override
    @Transactional
    public void eliminarTipoProducto(Long id) {
        TipoProducto tipoProducto = tipoProductoRepository.findById(id).orElseThrow(() -> new RuntimeException("TipoProducto no encontrado"));
        tipoProducto.setEstado(2);
        tipoProductoRepository.save(tipoProducto);
    }

    @Override
    @Transactional
    public boolean cambiarEstadoTipoProducto(Long id) {
        Optional<TipoProducto> tipoProductoOpt = tipoProductoRepository.findById(id);
        if (tipoProductoOpt.isEmpty() || tipoProductoOpt.get().getEstado() == 2) {
            return false;
        }
        int nuevoEstado = tipoProductoOpt.get().getEstado() == 1 ? 0 : 1;
        tipoProductoRepository.actualizarEstado(id, nuevoEstado);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public long contarTiposProducto() {
        return tipoProductoRepository.countByEstadoNot(2);
    }
    private Set<Categoria> procesarCategorias(Set<Categoria> categorias) {
        if (categorias == null || categorias.isEmpty()) {
            return new HashSet<>();
        }
        Set<Long> idsCategorias = categorias.stream().map(Categoria::getId).collect(Collectors.toSet());
        List<Categoria> categoriasReales = categoriaRepository.findAllById(idsCategorias);
        return new HashSet<>(categoriasReales);
    }
}
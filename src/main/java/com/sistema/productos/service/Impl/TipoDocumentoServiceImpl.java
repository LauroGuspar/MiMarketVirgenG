package com.sistema.productos.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sistema.productos.model.TipoDocumento;
import com.sistema.productos.repository.TipoDocumentoRepository;
import com.sistema.productos.service.TipoDocumentoService;

@Service
public class TipoDocumentoServiceImpl implements TipoDocumentoService {

    private final TipoDocumentoRepository tipoDocumentoRepository;

    public TipoDocumentoServiceImpl(TipoDocumentoRepository tipoDocumentoRepository) {
        this.tipoDocumentoRepository = tipoDocumentoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoDocumento> listarTiposDocumentoActivos() {
        return tipoDocumentoRepository.findAllByEstado(1);
    }
}
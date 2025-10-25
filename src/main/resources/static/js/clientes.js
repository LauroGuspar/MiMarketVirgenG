$(document).ready(function () {
    let dataTable;
    let isEditing = false;
    let clienteModal;
    const formId = '#formCliente';

    const ValidacionTDocumento = {
        'DNI': { length: 8, message: 'El DNI debe tener 8 dígitos.' },
        'RUC': { length: 11, message: 'El RUC debe tener 11 dígitos.' },
        'Carné de Extranjería': { length: 20, message: 'El Carné de Extranjería debe tener como máximo 20 caracteres.' }
    };

    const API_BASE = '/clientes/api';
    const ENDPOINTS = {
        list: `${API_BASE}/listar`,
        save: `${API_BASE}/guardar`,
        get: (id) => `${API_BASE}/${id}`,
        delete: (id) => `${API_BASE}/eliminar/${id}`,
        tiposDocumento: `${API_BASE}/tipodocumento`,
        toggleStatus: (id) => `${API_BASE}/cambiar-estado/${id}`,
        buscarReniec: (dni) => `/reniec/api/buscar-dni/${dni}`,
    };

    initializeDataTable();
    clienteModal = new bootstrap.Modal(document.getElementById('clienteModal'));
    loadTiposDocumento();
    setupEventListeners();

    function initializeDataTable() {
        dataTable = $('#tablaClientes').DataTable({
            responsive: true,
            processing: true,
            ajax: { url: ENDPOINTS.list, dataSrc: 'data' },
            columns: [
                { data: 'id' }, { data: 'nombre' },
                { data: 'correo' },
                { data: 'tipodocumento.nombre' }, { data: 'ndocumento' },
                {
                    data: 'estado',
                    render: (data) => data === 1 ? '<span class="badge text-bg-success">Activo</span>' : '<span class="badge text-bg-danger">Inactivo</span>'
                },
                {
                    data: null, orderable: false, searchable: false,
                    render: (data, type, row) => AppUtils.createActionButtons(row)
                }
            ],
            language: { url: "//cdn.datatables.net/plug-ins/1.13.6/i18n/es-ES.json" }
        });
    }

    function setupEventListeners() {
        $('#btnNuevoRegistro').on('click', openModalForNew);
        $(formId).on('submit', (e) => { e.preventDefault(); saveCliente(); });
        $('#tablaClientes tbody').on('click', '.action-edit', handleEdit);
        $('#tablaClientes tbody').on('click', '.action-status', handleToggleStatus);
        $('#tablaClientes tbody').on('click', '.action-delete', handleDelete);

        $('#id_tipodocumento').on('change', function () {
            const selectedText = $(this).find('option:selected').text();
            const ndocumentoInput = $('#ndocumento');
            const rule = ValidacionTDocumento[selectedText];

            if (rule) {
                ndocumentoInput.attr('maxlength', rule.length);
                ndocumentoInput.attr('placeholder', `${rule.length} dígitos`);
                ndocumentoInput.data('expected-length', rule.length);
                ndocumentoInput.data('validation-message', rule.message);
            } else {
                ndocumentoInput.removeAttr('maxlength');
                ndocumentoInput.attr('placeholder', 'N° de Documento');
                ndocumentoInput.removeData('expected-length');
                ndocumentoInput.removeData('validation-message');
            }
            ndocumentoInput.val('');
            validarNumeroDocumento();
        });

        $('#ndocumento').on('input', function () {
            this.value = this.value.replace(/[^0-9]/g, '');
            validarNumeroDocumento();
        });

        $('#btnBuscarReniec').on('click', async function () {
            const dni = $('#ndocumento').val().trim();

            // if (dni.length !== 8 || isNaN(dni)) {
            //     Swal.fire('Advertencia', 'Ingrese un DNI válido de 8 dígitos', 'warning');
            //     return;
            // }

            try {
                AppUtils.showLoading(true);

                const response = await fetch(`/reniec/api/buscar-dni/${dni}`);
                const result = await response.json();
                // console.log('Respuesta RENIEC:', result);

                if (result.success && result.data && result.data.datos) {
                    const datos = result.data.datos;

                    $('#nombre').val(datos.nombres || '');
                    $('#apellidoPaterno').val(datos.ape_paterno || '');
                    $('#apellidoMaterno').val(datos.ape_materno || '');

                    if (datos.domiciliado) {
                        const d = datos.domiciliado;
                        const direccionCompleta = [d.direccion, d.distrito, d.provincia, d.departamento]
                            .filter(Boolean)
                            .join(', ');
                        $('#direccion').val(direccionCompleta);
                    }

                    // Swal.fire('Éxito', 'Datos obtenidos correctamente desde RENIEC', 'success');
                } else {
                    Swal.fire('No encontrado', result.message || 'No se encontraron datos para este DNI', 'info');
                }

            } catch (error) {
                console.error('Error al buscar en RENIEC:', error);
                Swal.fire('Error', 'No se pudo conectar al servicio RENIEC', 'error');
            } finally {
                AppUtils.showLoading(false);
            }
        });

        $('#telefono').on('input', function () {
            this.value = this.value.replace(/[^0-9]/g, '');
            validarTelefono();
        });
    }

    function validarNumeroDocumento() {
        const ndocumentoInput = $('#ndocumento');
        const expectedLength = ndocumentoInput.data('expected-length');
        const currentLength = ndocumentoInput.val().length;
        const errorDiv = $('#ndocumento-error');

        if (expectedLength && currentLength > 0 && currentLength < expectedLength) {
            errorDiv.text(`Faltan ${expectedLength - currentLength} dígitos.`);
            ndocumentoInput.addClass('is-invalid');
            return false;
        } else if (expectedLength && currentLength > expectedLength) {
            errorDiv.text(ndocumentoInput.data('validation-message'));
            ndocumentoInput.addClass('is-invalid');
            return false;
        } else {
            errorDiv.text('');
            ndocumentoInput.removeClass('is-invalid');
            return true;
        }
    }

    function validarTelefono() {
        const telefonoInput = $('#telefono');
        const currentLength = telefonoInput.val().length;
        const errorDiv = $('#telefono-error');

        if (currentLength > 0 && currentLength < 9) {
            errorDiv.text('El teléfono debe tener 9 dígitos.');
            telefonoInput.addClass('is-invalid');
            return false;
        } else {
            errorDiv.text('');
            telefonoInput.removeClass('is-invalid');
            return true;
        }
    }

    function reloadTable() { dataTable.ajax.reload(); }

    function loadTiposDocumento() {
        fetch(ENDPOINTS.tiposDocumento)
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    const select = $('#id_tipodocumento');
                    select.empty().append('<option value="" disabled selected>Selecciona un Tipo</option>');
                    data.data.forEach(doc => select.append(`<option value="${doc.id}">${doc.nombre}</option>`));
                } else { AppUtils.showNotification('Error al cargar tipos de documento', 'error'); }
            }).catch(error => console.error('Error cargando tipos de documento:', error));
    }

    function saveCliente() {
        const isDocumentoValid = validarNumeroDocumento();
        const isTelefonoValid = validarTelefono();

        if (!isDocumentoValid || !isTelefonoValid) {
            AppUtils.showNotification('Por favor, corrige los errores en el formulario.', 'error');
            return;
        }

        const formData = {
            id: $('#id').val() || null, nombre: $('#nombre').val().trim(),
            apellidoPaterno: $('#apellidoPaterno').val().trim(),
            apellidoMaterno: $('#apellidoMaterno').val().trim(), correo: $('#correo').val().trim(),
            telefono: $('#telefono').val().trim(), direccion: $('#direccion').val().trim(),
            ndocumento: $('#ndocumento').val().trim(),
            tipodocumento: { id: $('#id_tipodocumento').val() },
        };

        AppUtils.showLoading(true);
        fetch(ENDPOINTS.save, {
            method: 'POST', headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(formData)
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    clienteModal.hide();
                    AppUtils.showNotification(data.message, 'success');
                    reloadTable();
                } else {
                    if (data.errors) {
                        Object.keys(data.errors).forEach(field => $(`#${field}-error`).text(data.errors[field]));
                    } else { AppUtils.showNotification(data.message, 'error'); }
                }
            })
            .catch(error => AppUtils.showNotification('Error de conexión', 'error'))
            .finally(() => AppUtils.showLoading(false));
    }

    function handleEdit() {
        const id = $(this).data('id');
        AppUtils.showLoading(true);
        fetch(ENDPOINTS.get(id))
            .then(response => response.json())
            .then(data => {
                if (data.success) { openModalForEdit(data.data); }
                else { AppUtils.showNotification('Error al cargar cliente', 'error'); }
            })
            .catch(error => AppUtils.showNotification('Error de conexión', 'error'))
            .finally(() => AppUtils.showLoading(false));
    }

    function handleToggleStatus() {
        const id = $(this).data('id');
        AppUtils.showLoading(true);
        fetch(ENDPOINTS.toggleStatus(id), { method: 'POST' })
            .then(response => response.json())
            .then(data => {
                if (data.success) { AppUtils.showNotification(data.message, 'success'); reloadTable(); }
                else { AppUtils.showNotification(data.message, 'error'); }
            })
            .catch(error => AppUtils.showNotification('Error de conexión', 'error'))
            .finally(() => AppUtils.showLoading(false));
    }

    function handleDelete() {
        const id = $(this).data('id');
        Swal.fire({
            title: '¿Estás seguro?', text: "¡El cliente será marcado como eliminado!",
            icon: 'warning', showCancelButton: true, confirmButtonColor: '#dc3545',
            cancelButtonColor: '#6c757d', confirmButtonText: 'Sí, ¡eliminar!', cancelButtonText: 'Cancelar'
        }).then((result) => {
            if (result.isConfirmed) {
                AppUtils.showLoading(true);
                fetch(ENDPOINTS.delete(id), { method: 'DELETE' })
                    .then(response => response.json())
                    .then(data => {
                        if (data.success) { AppUtils.showNotification(data.message, 'success'); reloadTable(); }
                        else { AppUtils.showNotification(data.message, 'error'); }
                    })
                    .catch(error => AppUtils.showNotification('Error de conexión', 'error'))
                    .finally(() => AppUtils.showLoading(false));
            }
        });
    }

    function openModalForNew() {
        isEditing = false;
        AppUtils.clearForm(formId);
        const ndocumentoInput = $('#ndocumento');
        ndocumentoInput.removeAttr('maxlength');
        ndocumentoInput.attr('placeholder', 'N° de Documento');
        ndocumentoInput.removeData('expected-length');
        ndocumentoInput.removeData('validation-message');

        $('#modalTitle').text('Agregar Cliente');
        clienteModal.show();
    }

    function openModalForEdit(cliente) {
        isEditing = true;
        AppUtils.clearForm(formId);
        $('#modalTitle').text('Editar Cliente');

        $('#id').val(cliente.id);
        $('#nombre').val(cliente.nombre);
        $('#apellidoPaterno').val(cliente.apellidoPaterno);
        $('#apellidoMaterno').val(cliente.apellidoMaterno);
        $('#correo').val(cliente.correo);
        $('#telefono').val(cliente.telefono);
        $('#direccion').val(cliente.direccion);
        $('#ndocumento').val(cliente.ndocumento);

        if (cliente.tipodocumento) {
            $('#id_tipodocumento').val(cliente.tipodocumento.id).trigger('change');
            $('#ndocumento').val(cliente.ndocumento);
        }
        clienteModal.show();
    }
});
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
        buscarDni: (dni) => `/reniec/api/buscar-dni/${dni}`,
        buscarRuc: (ruc) => `/reniec/api/buscar-ruc/${ruc}`,
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
            language: {
                "processing": "Procesando...",
                "lengthMenu": "Mostrar _MENU_ registros",
                "zeroRecords": "No se encontraron resultados",
                "emptyTable": "Ningún dato disponible en esta tabla",
                "info": "Mostrando registros del _START_ al _END_ de un total de _TOTAL_ registros",
                "infoEmpty": "Mostrando registros del 0 al 0 de un total de 0 registros",
                "infoFiltered": "(filtrado de un total de _MAX_ registros)",
                "search": "Buscar:",
                "loadingRecords": "Cargando...",
                "paginate": {
                    "first": "Primero",
                    "last": "Último",
                    "next": "Siguiente",
                    "previous": "Anterior"
                },
                "aria": {
                    "sortAscending": ": Activar para ordenar la columna de manera ascendente",
                    "sortDescending": ": Activar para ordenar la columna de manera descendente"
                }
            }
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
            const btnBuscar = $('#btnBuscarApi');
            $('#nombre, #apellidoPaterno, #apellidoMaterno, #direccion').val('');
            $('#nombreEmpresa, #direccionEmpresa').val('');
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

            if (selectedText === 'DNI') {
                $('#camposPersonaNatural').show();
                $('#camposEmpresa').hide();
                btnBuscar.prop('disabled', false);
                $('#nombre, #apellidoPaterno, #apellidoMaterno, #direccion').prop('disabled', true);
                $('#nombreEmpresa, #direccionEmpresa').prop('disabled', true);
            } else if (selectedText === 'RUC') {
                $('#camposPersonaNatural').hide();
                $('#camposEmpresa').show();
                btnBuscar.prop('disabled', false);
                $('#nombreEmpresa, #direccionEmpresa').prop('disabled', true);
                $('#nombre, #apellidoPaterno, #apellidoMaterno, #direccion').prop('disabled', true);
            } else {
                $('#camposPersonaNatural').show();
                $('#camposEmpresa').hide();
                btnBuscar.prop('disabled', true);
                $('#nombre, #apellidoPaterno, #apellidoMaterno, #direccion').prop('disabled', true);
                $('#nombreEmpresa, #direccionEmpresa').prop('disabled', true);
            }

            ndocumentoInput.val('');
            validarNumeroDocumento();
        });

        $('#ndocumento').on('input', function () {
            this.value = this.value.replace(/[^0-9]/g, '');
            validarNumeroDocumento();
        });

        $('#btnBuscarApi').on('click', async function () {
            const numeroDoc = $('#ndocumento').val().trim();
            const tipoDocSeleccionado = $('#id_tipodocumento').find('option:selected').text();
            const rule = ValidacionTDocumento[tipoDocSeleccionado];
            if (!rule || numeroDoc.length !== rule.length) {
                Swal.fire('Advertencia', rule ? rule.message : 'Seleccione un tipo de documento válido.', 'warning');
                return;
            }

            let urlApi = '';
            let tipoBusqueda = '';

            if (tipoDocSeleccionado === 'DNI') {
                urlApi = ENDPOINTS.buscarDni(numeroDoc);
                tipoBusqueda = 'DNI';
            } else if (tipoDocSeleccionado === 'RUC') {
                urlApi = ENDPOINTS.buscarRuc(numeroDoc);
                tipoBusqueda = 'RUC';
            } else {
                return;
            }

            try {
                AppUtils.showLoading(true);
                const response = await fetch(urlApi);
                const result = await response.json();

                console.log(`Respuesta API ${tipoBusqueda}:`, JSON.stringify(result, null, 2));

                let datos = null;
                if (result.success) {
                    if (tipoBusqueda === 'DNI' && result.data && result.data.datos) {
                        datos = result.data.datos;
                    } else if (result.datos) {
                        datos = result.datos;
                    }
                }

                if (datos) {
                    if (tipoBusqueda === 'DNI') {
                        console.log("Filling DNI fields:", datos.nombres, datos.ape_paterno, datos.ape_materno);
                        $('#nombre').val(datos.nombres || '');
                        $('#apellidoPaterno').val(datos.ape_paterno || '');
                        $('#apellidoMaterno').val(datos.ape_materno || '');
                        if (datos.domiciliado && datos.domiciliado.direccion) {
                            const d = datos.domiciliado;
                            const direccionCompleta = [d.direccion, d.distrito, d.provincia, d.departamento]
                                .filter(Boolean).join(', ');
                            console.log("Filling DNI address:", direccionCompleta);
                            $('#direccion').val(direccionCompleta);
                        } else {
                            console.log("DNI domicilio or direccion missing.");
                            $('#direccion').val('');
                        }
                    } else if (tipoBusqueda === 'RUC') {
                        console.log("Filling RUC fields:", datos.razon_social);
                        $('#nombreEmpresa').val(datos.razon_social || '');
                        if (datos.domiciliado && datos.domiciliado.direccion) {
                            console.log("Filling RUC address:", datos.domiciliado.direccion);
                            $('#direccionEmpresa').val(datos.domiciliado.direccion || '');
                        } else {
                            console.log("RUC domicilio or direccion missing.");
                            $('#direccionEmpresa').val('');
                        }
                        $('#nombre, #apellidoPaterno, #apellidoMaterno, #direccion').val('');
                    }

                } else {
                    console.log("API call failed or data missing. Result:", result);
                    Swal.fire('No encontrado', result.message || `No se encontraron datos para este ${tipoBusqueda}`, 'info');
                    if (tipoBusqueda === 'DNI') {
                        $('#nombre, #apellidoPaterno, #apellidoMaterno, #direccion').val('');
                    } else if (tipoBusqueda === 'RUC') {
                        $('#nombreEmpresa, #direccionEmpresa').val('');
                    }
                }

            } catch (error) {
                console.error(`Error al buscar ${tipoBusqueda}:`, error);
                if (error instanceof SyntaxError && error.message.includes("Unexpected token '<'")) {
                    Swal.fire('Error de Acceso', 'No se pudo obtener la respuesta de la API. Verifique los permisos o la sesión.', 'error');
                } else {
                    Swal.fire('Error', `No se pudo conectar al servicio de ${tipoBusqueda}`, 'error');
                }
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
        const currentValue = telefonoInput.val();
        const errorDiv = $('#telefono-error');
        if (currentValue && currentValue.length !== 9) {
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
        if (!isDocumentoValid) {
            if (!isTelefonoValid && $('#telefono').val()) {
                AppUtils.showNotification('El teléfono debe tener 9 dígitos si se ingresa.', 'error');
            } else if (!isDocumentoValid) {
                AppUtils.showNotification('Por favor, corrige el número de documento.', 'error');
            } else {
                AppUtils.showNotification('Por favor, corrige los errores en el formulario.', 'error');
            }

            return;
        }

        const tipoDocSeleccionado = $('#id_tipodocumento').find('option:selected').text();
        const formData = {
            id: $('#id').val() || null,
            tipodocumento: { id: $('#id_tipodocumento').val() },
            ndocumento: $('#ndocumento').val().trim(),
            correo: $('#correo').val().trim() || null,
            telefono: $('#telefono').val().trim() || null,
            nombre: $('#nombre').val().trim(),
            apellidoPaterno: $('#apellidoPaterno').val().trim(),
            apellidoMaterno: $('#apellidoMaterno').val().trim(),
            direccion: $('#direccion').val().trim(),
            nombreEmpresa: $('#nombreEmpresa').val().trim(),
            direccionEmpresa: $('#direccionEmpresa').val().trim(),
        };
        if (tipoDocSeleccionado === 'RUC') {
            formData.nombre = null;
            formData.apellidoPaterno = null;
            formData.apellidoMaterno = null;
            formData.direccion = null;
        } else {
            formData.nombreEmpresa = null;
            formData.direccionEmpresa = null;
        }


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
                        Object.keys(data.errors).forEach(field => {
                            $(`#${field}-error`).text(data.errors[field]);
                            $(`#${field}`).addClass('is-invalid');
                        });
                        AppUtils.showNotification('Datos inválidos, por favor revise el formulario.', 'error');
                    } else { AppUtils.showNotification(data.message || 'Error al guardar el cliente.', 'error'); }
                }
            })
            .catch(error => AppUtils.showNotification('Error de conexión al guardar.', 'error'))
            .finally(() => AppUtils.showLoading(false));
    }

    function handleEdit() {
        const id = $(this).data('id');
        AppUtils.showLoading(true);
        fetch(ENDPOINTS.get(id))
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    openModalForEdit(data.data);
                } else { AppUtils.showNotification('Error al cargar cliente', 'error'); }
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
        $('#modalTitle').text('Agregar Cliente');
        $('#camposPersonaNatural').hide();
        $('#camposEmpresa').hide();
        $('#btnBuscarApi').prop('disabled', true);
        $('#id_tipodocumento').prop('disabled', false);
        $('#ndocumento').prop('disabled', false);
        $('#correo').prop('disabled', false);
        $('#telefono').prop('disabled', false);
        $('#nombre').prop('disabled', true);
        $('#apellidoPaterno').prop('disabled', true);
        $('#apellidoMaterno').prop('disabled', true);
        $('#direccion').prop('disabled', true);
        $('#nombreEmpresa').prop('disabled', true);
        $('#direccionEmpresa').prop('disabled', true);
        $('#id_tipodocumento').val('1').trigger('change');
        const ndocumentoInput = $('#ndocumento');
        ndocumentoInput.attr('placeholder', '8 dígitos');

        clienteModal.show();
    }

    function openModalForEdit(cliente) {
        isEditing = true;
        $('#modalTitle').text('Editar Cliente');
        $('#id').val(cliente.id);
        $('#correo').val(cliente.correo);
        $('#telefono').val(cliente.telefono);

        if (cliente.tipodocumento) {
            $('#id_tipodocumento').val(cliente.tipodocumento.id);
            $('#ndocumento').val(cliente.ndocumento);
            if (cliente.tipodocumento.nombre === 'RUC') {
                $('#camposPersonaNatural').hide();
                $('#camposEmpresa').show();
                $('#nombreEmpresa').val(cliente.nombreEmpresa);
                $('#direccionEmpresa').val(cliente.direccionEmpresa);
                $('#nombre, #apellidoPaterno, #apellidoMaterno, #direccion').val('');
            } else {
                $('#camposPersonaNatural').show();
                $('#camposEmpresa').hide();
                $('#nombre').val(cliente.nombre);
                $('#apellidoPaterno').val(cliente.apellidoPaterno);
                $('#apellidoMaterno').val(cliente.apellidoMaterno);
                $('#direccion').val(cliente.direccion);
                $('#nombreEmpresa, #direccionEmpresa').val('');
            }
        } else {
            $('#id_tipodocumento').val('');
            $('#ndocumento').val('');
            $('#camposPersonaNatural').hide();
            $('#camposEmpresa').hide();
        }
        $('#id_tipodocumento').prop('disabled', true);
        $('#ndocumento').prop('disabled', true);
        $('#btnBuscarApi').prop('disabled', true);
        $('#nombre').prop('disabled', true);
        $('#apellidoPaterno').prop('disabled', true);
        $('#apellidoMaterno').prop('disabled', true);
        $('#direccion').prop('disabled', true);
        $('#nombreEmpresa').prop('disabled', true);
        $('#direccionEmpresa').prop('disabled', true);
        $('#correo').prop('disabled', false);
        $('#telefono').prop('disabled', false);

        clienteModal.show();
    }
});
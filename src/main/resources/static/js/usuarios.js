$(document).ready(function () {
    let dataTable;
    let isEditing = false;
    let usuarioModal;
    const formId = '#formUsuario';

    const ValidacionTDocumento = {
        'DNI': { length: 8, message: 'El DNI debe tener 8 dígitos.' }
    };

    const API_BASE = '/empleados/api';
    const ENDPOINTS = {
        list: `${API_BASE}/listar`,
        save: `${API_BASE}/guardar`,
        get: (id) => `${API_BASE}/${id}`,
        delete: (id) => `${API_BASE}/eliminar/${id}`,
        roles: `${API_BASE}/roles`,
        tiposDocumento: `${API_BASE}/tipodocumento`,
        toggleStatus: (id) => `${API_BASE}/cambiar-estado/${id}`,
        buscarReniec: (dni) => `/reniec/api/buscar-dni/${dni}`,
    };

    initializeDataTable();
    usuarioModal = new bootstrap.Modal(document.getElementById('usuarioModal'));
    loadRoles();
    loadTiposDocumento();
    setupEventListeners();

    function initializeDataTable() {
        dataTable = $('#tablaUsuarios').DataTable({
            responsive: true,
            processing: true,
            ajax: { url: ENDPOINTS.list, dataSrc: 'data' },
            columns: [
                { data: 'id' }, { data: 'nombre' }, { data: 'usuario' },
                { data: 'rol.nombre' }, { data: 'correo' },
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
        $(formId).on('submit', (e) => { e.preventDefault(); saveUsuario(); });
        $('#tablaUsuarios tbody').on('click', '.action-edit', handleEdit);
        $('#tablaUsuarios tbody').on('click', '.action-status', handleToggleStatus);
        $('#tablaUsuarios tbody').on('click', '.action-delete', handleDelete);

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
        $('#telefono').on('input', function () {
            this.value = this.value.replace(/[^0-9]/g, '');
            validarTelefono();
        });
        $('#btnBuscarReniec').on('click', async function () {
            const dni = $('#ndocumento').val().trim();
            const selectedText = $('#id_tipodocumento').find('option:selected').text();

            if (selectedText !== 'DNI' || dni.length !== 8) {
                Swal.fire('Advertencia', 'Ingrese un DNI válido de 8 dígitos', 'warning');
                return;
            }

            try {
                AppUtils.showLoading(true);
                const response = await fetch(ENDPOINTS.buscarReniec(dni));
                const result = await response.json();

                if (result.success && result.data && result.data.datos) {
                    const datos = result.data.datos;

                    // Autocompletar campos del formulario de Usuarios
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

    function loadRoles() {
        fetch(ENDPOINTS.roles)
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    const select = $('#id_rol');
                    select.empty().append('<option value="" disabled selected>Selecciona un Rol</option>');
                    data.data.forEach(rol => select.append(`<option value="${rol.id}">${rol.nombre}</option>`));
                } else { AppUtils.showNotification('Error al cargar Roles', 'error'); }
            }).catch(error => console.error('Error cargando roles:', error));
    }

    function loadTiposDocumento() {
        const select = $('#id_tipodocumento');
        select.empty();
        select.append('<option value="1" selected>DNI</option>');
        select.trigger('change');
    }

    function saveUsuario() {
        const isDocumentoValid = validarNumeroDocumento();
        const isTelefonoValid = validarTelefono();

        if (!isDocumentoValid || !isTelefonoValid) {
            AppUtils.showNotification('Por favor, corrige los errores en el formulario.', 'error');
            return;
        }

        const formData = {
            id: $('#id').val() || null, nombre: $('#nombre').val().trim(),
            usuario: $('#usuario').val().trim(), apellidoPaterno: $('#apellidoPaterno').val().trim(),
            apellidoMaterno: $('#apellidoMaterno').val().trim(), correo: $('#correo').val().trim(),
            telefono: $('#telefono').val().trim(), direccion: $('#direccion').val().trim(),
            ndocumento: $('#ndocumento').val().trim(), clave: $('#clave').val(),
            tipodocumento: { id: $('#id_tipodocumento').val() },
            rol: { id: $('#id_rol').val() }
        };

        if (isEditing && !formData.clave) { delete formData.clave; }

        AppUtils.showLoading(true);
        fetch(ENDPOINTS.save, {
            method: 'POST', headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(formData)
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    usuarioModal.hide();
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
                else { AppUtils.showNotification('Error al cargar usuario', 'error'); }
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
            title: '¿Estás seguro?', text: "¡El usuario será marcado como eliminado!",
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
        $('#id_tipodocumento').val('1').trigger('change');

        const ndocumentoInput = $('#ndocumento');
        ndocumentoInput.attr('placeholder', '8 dígitos');
        ndocumentoInput.removeData('validation-message');

        $('#modalTitle').text('Agregar Usuario');
        usuarioModal.show();
    }

    function openModalForEdit(usuario) {
        isEditing = true;
        clearForm(formId); // Usando la función local
        $('#modalTitle').text('Editar Usuario');

        $('#id').val(usuario.id);
        $('#nombre').val(usuario.nombre);
        $('#usuario').val(usuario.usuario);
        $('#apellidoPaterno').val(usuario.apellidoPaterno);
        $('#apellidoMaterno').val(usuario.apellidoMaterno);
        $('#correo').val(usuario.correo);
        $('#telefono').val(usuario.telefono);
        $('#direccion').val(usuario.direccion);
        $('#ndocumento').val(usuario.ndocumento);
        $('#id_rol').val(usuario.rol ? usuario.rol.id : '');
        $('#clave').attr('placeholder', 'Dejar en blanco para no cambiar');
        $('#id_tipodocumento').val('1').trigger('change');
        $('#ndocumento').val(usuario.ndocumento);

        usuarioModal.show();
    }
});
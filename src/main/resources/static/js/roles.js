$(document).ready(function () {
    let dataTable;
    let isEditing = false;
    let rolModal;
    let permisosModal;
    const formid = '#formRol';

    const API_BASE = '/empleados/roles/api';
    const ENDPOINTS = {
        list: `${API_BASE}/listar`,
        save: `${API_BASE}/guardar`,
        get: (id) => `${API_BASE}/${id}`,
        toggleStatus: (id) => `${API_BASE}/cambiar-estado/${id}`,
        delete: (id) => `${API_BASE}/eliminar/${id}`,
        options: `${API_BASE}/opciones`
    };

    // Inicializar Componentes
    initializeDataTable();
    rolModal = new bootstrap.Modal(document.getElementById('rolModal'));
    permisosModal = new bootstrap.Modal(document.getElementById('permisosModal'));

    setupEventListeners();

    function initializeDataTable() {
        dataTable = $('#tablaRoles').DataTable({
            responsive: true,
            processing: true,
            ajax: {
                url: ENDPOINTS.list,
                dataSrc: 'data'
            },
            columns: [
                { data: 'id' },
                { data: 'nombre' },
                { data: 'descripcion' },
                {
                    data: 'estado',
                    render: (data) => data === 1 ? '<span class="badge text-bg-success">Activo</span>' : '<span class="badge text-bg-danger">Inactivo</span>'
                },
                {
                    data: null, orderable: false, searchable: false,
                    render: (data, type, row) => createActionButtons(row)
                }
            ],
            columnDefs: [
                { responsivePriority: 1, targets: 1 },
                { responsivePriority: 2, targets: 4 },
            ],
            language: { url: "//cdn.datatables.net/plug-ins/1.13.6/i18n/es-ES.json" },
            pageLength: 10
        });
    }

    function createActionButtons(row) {
        const statusIcon = row.estado === 1
            ? '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-eye-slash-fill" viewBox="0 0 16 16"><path d="m10.79 12.912-1.614-1.615a3.5 3.5 0 0 1-4.474-4.474l-2.06-2.06C.938 6.278 0 8 0 8s3 5.5 8 5.5a7.029 7.029 0 0 0 2.79-.588M5.21 3.088A7.028 7.028 0 0 1 8 2.5c5 0 8 5.5 8 5.5s-.939 1.721-2.641 3.238l-2.062-2.062a3.5 3.5 0 0 0-4.474-4.474L5.21 3.089z"/><path d="M5.525 7.646a2.5 2.5 0 0 0 2.829 2.829l-2.83-2.829zm4.95.708-2.829-2.83a2.5 2.5 0 0 1 2.829 2.829zm3.171 6-12-12 .708-.708 12 12z"/></svg>'
            : '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-eye-fill" viewBox="0 0 16 16"><path d="M10.5 8a2.5 2.5 0 1 1-5 0 2.5 2.5 0 0 1 5 0"/><path d="M0 8s3-5.5 8-5.5S16 8 16 8s-3 5.5-8 5.5S0 8 0 8m8 3.5a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7"/></svg>';
        const statusClass = row.estado === 1 ? 'action-btn-status-deactivate' : 'action-btn-status-activate';
        const statusTitle = row.estado === 1 ? 'Desactivar' : 'Activar';

        return `
            <div class="d-flex gap-1">
                <button data-id="${row.id}" class="btn btn-sm btn-info action-permissions" title="Permisos">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-shield-lock-fill" viewBox="0 0 16 16"><path d="M5.338 1.59a61.44 61.44 0 0 0-2.837.856.481.481 0 0 0-.328.39c-.554 4.157.726 7.19 2.253 9.188a10.725 10.725 0 0 0 2.287 2.233c.346.244.652.42.893.533.12.056.255.115.385.17.117.05.238.097.36.133.124.037.25.06.377.06s.253-.023.377-.06a2.1 2.1 0 0 0 .745-.265c.16-.085.312-.18.456-.282.14-.1.274-.213.396-.333a10.726 10.726 0 0 0 2.287-2.233c1.527-1.997 2.807-5.031 2.253-9.188a.48.48 0 0 0-.328-.39c-.651-.213-1.75-.56-2.837-.855C9.552 1.29 8.5 1 8 1s-1.552.29-2.662.59zM10 8.5a1.5 1.5 0 0 1-1.5 1.5h-1a1.5 1.5 0 1 1 0-3h1A1.5 1.5 0 0 1 10 8.5z"/></svg>
                    Permisos
                </button>
                <button data-id="${row.id}" class="action-btn action-btn-edit action-edit" title="Editar"><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-pencil-square" viewBox="0 0 16 16"><path d="M15.502 1.94a.5.5 0 0 1 0 .706L14.459 3.69l-2-2L13.502.646a.5.5 0 0 1 .707 0l1.293 1.293zm-1.75 2.456-2-2L4.939 9.21a.5.5 0 0 0-.121.196l-.805 2.414a.25.25 0 0 0 .316.316l2.414-.805a.5.5 0 0 0 .196-.12l6.813-6.814z"/><path fill-rule="evenodd" d="M1 13.5A1.5 1.5 0 0 0 2.5 15h11a1.5 1.5 0 0 0 1.5-1.5v-6a.5.5 0 0 0-1 0v6a.5.5 0 0 1-.5.5h-11a.5.5 0 0 1-.5-.5v-11a.5.5 0 0 1 .5-.5H9a.5.5 0 0 0 0-1H2.5A1.5 1.5 0 0 0 1 2.5z"/></svg></button>
                <button data-id="${row.id}" class="action-btn action-status ${statusClass}" title="${statusTitle}">${statusIcon}</button>
                <button data-id="${row.id}" class="action-btn action-btn-delete action-delete" title="Eliminar"><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-trash3-fill" viewBox="0 0 16 16"><path d="M11 1.5v1h3.5a.5.5 0 0 1 0 1h-.538l-.853 10.66A2 2 0 0 1 11.115 16h-6.23a2 2 0 0 1-1.994-1.84L2.038 3.5H1.5a.5.5 0 0 1 0-1H5v-1A1.5 1.5 0 0 1 6.5 0h3A1.5 1.5 0 0 1 11 1.5m-5 0v1h4v-1a.5.5 0 0 0-.5-.5h-3a.5.5 0 0 0-.5.5M4.5 5.029l.5 8.5a.5.5 0 1 0 .998-.06l-.5-8.5a.5.5 0 1 0-.998.06m6.53-.528a.5.5 0 0 0-.528.47l-.5 8.5a.5.5 0 0 0 .998.058l.5-8.5a.5.5 0 0 0-.47-.528M8 4.5a.5.5 0 0 0-.5.5v8.5a.5.5 0 0 0 1 0V5a.5.5 0 0 0-.5-.5"/></svg></button>
            </div>
        `;
    }
    function setupEventListeners() {
        $('#btnNuevoRegistro').on('click', openModalForNew);
        $(formid).on('submit', (e) => { e.preventDefault(); saveRol(); });
        $('#tablaRoles tbody').on('click', '.action-edit', handleEdit);
        $('#tablaRoles tbody').on('click', '.action-status', handleToggleStatus);
        $('#tablaRoles tbody').on('click', '.action-permissions', handlePermissions);
        $('#btnGuardarPermisos').on('click', savePermissions);
        $('#tablaRoles tbody').on('click', '.action-delete', handleDelete);
    }

    function reloadTable() {
        dataTable.ajax.reload();
    }
    function saveRol() {
        const rolData = {
            id: $('#id').val() || null,
            nombre: $('#nombre').val().trim(),
            descripcion: $('#descripcion').val().trim(),
        };

        if (!rolData.nombre) {
            showFieldError('nombre', 'El nombre es obligatorio');
            return;
        }

        AppUtils.showLoading(true);
        fetch(ENDPOINTS.save, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(rolData)
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    rolModal.hide();
                    AppUtils.showNotification(data.message, 'success');
                    reloadTable();
                } else {
                    AppUtils.showNotification(data.message, 'error');
                }
            })
            .catch(error => AppUtils.showNotification('Error de conexión', 'error'))
            .finally(() => AppUtils.showLoading(false));
    }

    function handleEdit(e) {
        const id = $(this).data('id');
        AppUtils.showLoading(true);
        fetch(ENDPOINTS.get(id))
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    openModalForEdit(data.data);
                } else {
                    AppUtils.showNotification('Error al cargar rol', 'error');
                }
            })
            .catch(error => AppUtils.showNotification('Error de conexión', 'error'))
            .finally(() => AppUtils.showLoading(false));
    }

    function handleToggleStatus(e) {
        const id = $(this).data('id');
        AppUtils.showLoading(true);
        fetch(ENDPOINTS.toggleStatus(id), { method: 'POST' })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    AppUtils.showNotification(data.message, 'success');
                    reloadTable();
                } else {
                    AppUtils.showNotification(data.message, 'error');
                }
            })
            .catch(error => AppUtils.showNotification('Error de conexión', 'error'))
            .finally(() => AppUtils.showLoading(false));
    }

    function handleDelete(e) {
        const id = $(this).data('id');

        Swal.fire({
            title: '¿Estás seguro?',
            text: "¡No podrás revertir esta acción! Se eliminará el rol permanentemente.",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#dc3545',
            cancelButtonColor: '#6c757d',
            confirmButtonText: 'Sí, ¡eliminar!',
            cancelButtonText: 'Cancelar'
        }).then((result) => {
            if (result.isConfirmed) {
                AppUtils.showLoading(true);
                fetch(ENDPOINTS.delete(id), {
                    method: 'DELETE'
                })
                    .then(response => response.json())
                    .then(data => {
                        if (data.success) {
                            AppUtils.showNotification(data.message, 'success');
                            reloadTable();
                        } else {
                            AppUtils.showNotification(data.message, 'error');
                        }
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        AppUtils.showNotification('Error de conexión al eliminar el rol.', 'error');
                    })
                    .finally(() => {
                        AppUtils.showLoading(false);
                    });
            }
        });
    }

    async function handlePermissions(e) {
        const id = $(this).data('id');
        AppUtils.showLoading(true);
        $('#permisoRolId').val(id);

        try {
            const [rolRes, opcionesRes] = await Promise.all([
                fetch(ENDPOINTS.get(id)),
                fetch(ENDPOINTS.options)
            ]);

            const rolData = await rolRes.json();
            const opcionesData = await opcionesRes.json();

            if (rolData.success && opcionesData.success) {
                $('#permisoRolNombre').text(rolData.data.nombre);
                const listaOpciones = $('#listaOpciones');
                listaOpciones.empty();

                opcionesData.data.forEach(opcion => {
                    const isChecked = rolData.data.opciones.includes(opcion.id);
                    const item = `
                        <label class="list-group-item">
                            <input class="form-check-input me-1" type="checkbox" value="${opcion.id}" ${isChecked ? 'checked' : ''}>
                            ${opcion.nombre}
                        </label>
                    `;
                    listaOpciones.append(item);
                });
                permisosModal.show();
            } else {
                AppUtils.showNotification('Error al cargar datos de permisos', 'error');
            }
        } catch (error) {
            AppUtils.showNotification('Error de conexión al cargar permisos', 'error');
        } finally {
            AppUtils.showLoading(false);
        }
    }

    async function savePermissions() {
        const rolId = $('#permisoRolId').val();
        const selectedOpciones = $('#listaOpciones input:checked').map(function () {
            return { id: $(this).val() };
        }).get();

        AppUtils.showLoading(true);
        try {
            const rolRes = await fetch(ENDPOINTS.get(rolId));
            const rolData = await rolRes.json();

            if (!rolData.success) {
                AppUtils.showNotification('No se pudo obtener el rol para actualizar', 'error');
                return;
            }

            const rolToUpdate = rolData.data;
            rolToUpdate.opciones = selectedOpciones;

            const saveRes = await fetch(ENDPOINTS.save, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(rolToUpdate)
            });
            const saveData = await saveRes.json();

            if (saveData.success) {
                permisosModal.hide();
                AppUtils.showNotification('Permisos actualizados correctamente', 'success');
            } else {
                AppUtils.showNotification(saveData.message || 'Error al guardar permisos', 'error');
            }
        } catch (error) {
            AppUtils.showNotification('Error de conexión al guardar permisos', 'error');
        } finally {
            AppUtils.showLoading(false);
        }
    }

    function openModalForNew() {
        isEditing = false;
        AppUtils.clearForm(formid);
        $('#modalTitle').text('Agregar Rol');
        rolModal.show();
    }

    function openModalForEdit(rol) {
        isEditing = true;
        AppUtils.clearForm(formid);
        $('#modalTitle').text('Editar Rol');
        $('#id').val(rol.id);
        $('#nombre').val(rol.nombre);
        $('#descripcion').val(rol.descripcion);
        rolModal.show();
    }
});
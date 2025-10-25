$(document).ready(function () {
    let dataTable;
    let isEditing = false;
    let tipoProductoModal;
    const formid = '#formTipoProducto';
    const API_BASE = '/productos/tipos-producto/api';
    const CATEGORIA_API_LIST = '/productos/categorias/api/listar';
    const ENDPOINTS = {
        list: `${API_BASE}/listar`,
        save: `${API_BASE}/guardar`,
        get: (id) => `${API_BASE}/${id}`,
        toggleStatus: (id) => `${API_BASE}/cambiar-estado/${id}`,
        delete: (id) => `${API_BASE}/eliminar/${id}`
    };

    $('#categoriasSelect').select2({
        theme: 'bootstrap-5',
        dropdownParent: $('#tipoProductoModal'),
        placeholder: 'Seleccione una o más categorías',
        allowClear: true
    });

    initializeDataTable();
    cargarFiltroCategorias();
    tipoProductoModal = new bootstrap.Modal(document.getElementById('tipoProductoModal'));
    setupEventListeners();

    function initializeDataTable() {
        dataTable = $('#tablaTiposProducto').DataTable({
            responsive: true,
            processing: true,
            ajax: {
                url: ENDPOINTS.list,
                data: function (d) {
                    const idCategoria = $('#filtroCategoria').val();
                    if (idCategoria) {
                        d.idCategoria = idCategoria;
                    }
                },
                dataSrc: 'data'
            },
            columns: [
                { data: 'id' },
                { data: 'nombre' },
                {
                    data: 'nombresCategorias',
                    orderable: false,
                    render: function (data) {
                        if (!data || data.length === 0) {
                            return '<span class="badge text-bg-light">Ninguna</span>';
                        }
                        return data.map(nombre =>
                            `<span class="badge bg-secondary me-1">${nombre}</span>`
                        ).join(' ');
                    }
                },
                {
                    data: 'estado',
                    render: (data) => data === 1 ? '<span class="badge text-bg-success">Activo</span>' : '<span class="badge text-bg-danger">Inactivo</span>'
                },
                {
                    data: null,
                    orderable: false,
                    searchable: false,
                    render: (data, type, row) => AppUtils.createActionButtons(row)
                }
            ],
            language: { url: "//cdn.datatables.net/plug-ins/1.13.6/i18n/es-ES.json" }
        });
    }

    function cargarFiltroCategorias() {
        fetch(CATEGORIA_API_LIST)
            .then(response => response.json())
            .then(data => {
                if (data.success && data.data) {
                    const $filtro = $('#filtroCategoria');
                    const $selectModal = $('#categoriasSelect'); // Selector del modal

                    // Limpia solo el select del modal (el filtro ya tiene "Todas")
                    $selectModal.empty();

                    data.data.forEach(categoria => {
                        // Añade al filtro de la tabla
                        $filtro.append(new Option(categoria.nombre, categoria.id));

                        // Añade al select del modal
                        $selectModal.append(new Option(categoria.nombre, categoria.id));
                    });

                    // Limpia la selección de Select2 (por si acaso)
                    $selectModal.val(null).trigger('change');
                }
            })
            .catch(error => console.error("Error al cargar categorías:", error));
    }

    function setupEventListeners() {
        $('#btnNuevoRegistro').on('click', openModalForNew);
        $(formid).on('submit', (e) => { e.preventDefault(); saveTipoProducto(); });
        $('#tablaTiposProducto tbody').on('click', '.action-edit', handleEdit);
        $('#tablaTiposProducto tbody').on('click', '.action-status', handleToggleStatus);
        $('#tablaTiposProducto tbody').on('click', '.action-delete', handleDelete);
        $('#filtroCategoria').on('change', function () {
            reloadTable();
        });
    }

    function reloadTable() {
        dataTable.ajax.reload();
    }

    function saveTipoProducto() {
        // 1. Obtener IDs del Select2
        const idsCategorias = $('#categoriasSelect').val();

        // 2. Mapearlos al formato JSON que espera el backend
        const categoriasParaEnviar = idsCategorias.map(id => ({ "id": parseInt(id) }));

        // 3. Construir el objeto principal
        const tipoProductoData = {
            id: $('#id').val() || null,
            nombre: $('#nombre').val().trim(),
            categorias: categoriasParaEnviar // --- Añadir las categorías ---
        };

        if (!tipoProductoData.nombre) {
            AppUtils.showNotification('El nombre es obligatorio', 'error');
            return;
        }

        AppUtils.showLoading(true);
        fetch(ENDPOINTS.save, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(tipoProductoData) // Enviar el objeto completo
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    tipoProductoModal.hide();
                    AppUtils.showNotification(data.message, 'success');
                    reloadTable();
                } else {
                    AppUtils.showNotification(data.message, 'error');
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
                if (data.success) {
                    openModalForEdit(data.data);
                } else {
                    AppUtils.showNotification('Error al cargar tipo de producto', 'error');
                }
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

    function handleDelete() {
        const id = $(this).data('id');
        Swal.fire({
            title: '¿Estás seguro?',
            text: "¡El tipo de producto será marcado como eliminado!",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#dc3545',
            cancelButtonColor: '#6c757d',
            confirmButtonText: 'Sí, ¡eliminar!',
            cancelButtonText: 'Cancelar'
        }).then((result) => {
            if (result.isConfirmed) {
                AppUtils.showLoading(true);
                fetch(ENDPOINTS.delete(id), { method: 'DELETE' })
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
        });
    }

    function openModalForNew() {
        isEditing = false;
        AppUtils.clearForm(formid);
        $('#modalTitle').text('Agregar Tipo de Producto');

        // Limpiar el select2
        $('#categoriasSelect').val(null).trigger('change');

        tipoProductoModal.show();
    }

    function openModalForEdit(tipoProducto) { // tipoProducto es el DTO
        isEditing = true;
        AppUtils.clearForm(formid);
        $('#modalTitle').text('Editar Tipo de Producto');
        $('#id').val(tipoProducto.id);
        $('#nombre').val(tipoProducto.nombre);

        // Usar el nuevo campo 'idsCategorias' del DTO para poblar el select2
        $('#categoriasSelect').val(tipoProducto.idsCategorias).trigger('change');

        tipoProductoModal.show();
    }
});
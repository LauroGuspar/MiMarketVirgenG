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

    function cargarFiltroCategorias() {
        fetch(CATEGORIA_API_LIST)
            .then(response => response.json())
            .then(data => {
                if (data.success && data.data) {
                    const $filtro = $('#filtroCategoria');
                    const $selectModal = $('#categoriasSelect');
                    $selectModal.empty();

                    data.data.forEach(categoria => {
                        $filtro.append(new Option(categoria.nombre, categoria.id));
                        $selectModal.append(new Option(categoria.nombre, categoria.id));
                    });
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
        const idsCategorias = $('#categoriasSelect').val();
        const categoriasParaEnviar = idsCategorias.map(id => ({ "id": parseInt(id) }));
        const tipoProductoData = {
            id: $('#id').val() || null,
            nombre: $('#nombre').val().trim(),
            categorias: categoriasParaEnviar
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
            body: JSON.stringify(tipoProductoData)
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
        $('#categoriasSelect').val(null).trigger('change');
        tipoProductoModal.show();
    }

    function openModalForEdit(tipoProducto) {
        isEditing = true;
        AppUtils.clearForm(formid);
        $('#modalTitle').text('Editar Tipo de Producto');
        $('#id').val(tipoProducto.id);
        $('#nombre').val(tipoProducto.nombre);
        $('#categoriasSelect').val(tipoProducto.idsCategorias).trigger('change');
        tipoProductoModal.show();
    }
});
$(document).ready(function () {
    const formId = '#formProducto';
    let productoModal = new bootstrap.Modal(document.getElementById('productoModal'));
    let dataTable;
    let isEditing = false;

    const API_BASE = '/productos/api';
    const ENDPOINTS = {
        list: `${API_BASE}/listar`,
        save: `${API_BASE}/guardar`,
        get: (id) => `${API_BASE}/${id}`,
        delete: (id) => `${API_BASE}/eliminar/${id}`,
        toggleStatus: (id) => `${API_BASE}/cambiar-estado/${id}`,
        categorias: `${API_BASE}/categorias`,
        marcas: `${API_BASE}/marcas`,
        unidades: `${API_BASE}/unidades`,
        tiposPorCategoria: (id) => `${API_BASE}/tipos-producto/por-categoria/${id}`
    };

    function initializeDataTable() {
        dataTable = $('#tablaProductos').DataTable({
            responsive: true,
            processing: true,
            ajax: { url: ENDPOINTS.list, dataSrc: 'data' },
            columns: [
                {
                    data: 'imagen',
                    render: function (data) {
                        const imageUrl = data ? `/productos/${data}` : '/images/placeholder.png';
                        return `<img src="${imageUrl}" alt="Producto" class="img-thumbnail" width="50">`;
                    }
                },
                { data: 'nombre', title: 'Nombre' },
                { data: 'codigo', title: 'Código' },
                { data: 'categoria.nombre', title: 'Categoría' },
                { data: 'tipoProducto.nombre', title: 'Tipo Prod.' },
                { data: 'marca.nombre', title: 'Marca' },
                { data: 'descripcion', title: 'Descripción' },
                { data: 'precio', title: 'Precio', render: data => `S/ ${parseFloat(data || 0).toFixed(2)}` },
                { data: 'stock', title: 'Stock' },
                { data: 'stockMinimo', title: 'Stock Mín.' },
                { data: 'unidad.nombre', title: 'Unidad' },
                { data: 'fechaCreacion', title: 'Fec. Creación' },
                { data: 'fechaVencimiento', title: 'Fec. Venc.' },
                {
                    data: 'estado', title: 'Estado',
                    render: (data) => data === 1 ? '<span class="badge text-bg-success">Activo</span>' : '<span class="badge text-bg-danger">Inactivo</span>'
                },
                {
                    data: null, title: 'Acciones',
                    orderable: false, searchable: false,
                    render: (data, type, row) => AppUtils.createActionButtons(row)
                }
            ],

            columnDefs: [
                {
                    targets: [6, 8, 11, 12, 13],
                    visible: false
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
            },
            dom: "<'row'<'col-sm-12 col-md-6'l><'col-sm-12 col-md-6'f>>" +
                "<'row'<'col-sm-12'tr>>" +
                "<'row'<'col-sm-12 col-md-5'i><'col-sm-12 col-md-7'p>>" +
                "<'row'<'col-sm-12'B>>",
            buttons: [
                {
                    extend: 'colvis',
                    text: 'Mostrar / Ocultar Columnas',
                    className: 'btn btn-outline-secondary'
                }
            ]
        });
    }

    function loadSelectOptions(endpoint, selector, placeholder) {
        fetch(endpoint)
            .then(response => response.json())
            .then(data => {
                const select = $(selector);
                select.empty().append(`<option value="" disabled selected>${placeholder}</option>`);
                data.forEach(item => {
                    select.append(`<option value="${item.id}">${item.nombre}</option>`);
                });
            });
    }

    function loadTiposProducto(idCategoria, tipoProductoSeleccionadoId = null) {
        const $selectTipoProducto = $('#tipoProducto');

        if (!idCategoria) {
            $selectTipoProducto.empty().append('<option value="">Seleccione una categoría primero</option>').prop('disabled', true);
            return;
        }

        AppUtils.showLoading(true);
        fetch(ENDPOINTS.tiposPorCategoria(idCategoria))
            .then(response => response.json())
            .then(data => {
                if (data.success && data.data) {
                    $selectTipoProducto.empty().append('<option value="" disabled selected>Seleccione un Tipo</option>').prop('disabled', false);
                    data.data.forEach(tipo => {
                        $selectTipoProducto.append(new Option(tipo.nombre, tipo.id));
                    });
                    if (tipoProductoSeleccionadoId) {
                        $selectTipoProducto.val(tipoProductoSeleccionadoId);
                    }
                } else {
                    $selectTipoProducto.empty().append('<option value="">No hay tipos para esta categoría</option>').prop('disabled', true);
                }
            })
            .catch(() => {
                $selectTipoProducto.empty().append('<option value="">Error al cargar</option>').prop('disabled', true);
            })
            .finally(() => AppUtils.showLoading(false));
    }

    function setupEventListeners() {
        $('#btnNuevoRegistro').on('click', openModalForNew);
        $(formId).on('submit', (e) => { e.preventDefault(); saveProducto(); });

        $('#tablaProductos tbody').on('click', '.action-edit', handleEdit);
        $('#tablaProductos tbody').on('click', '.action-status', handleToggleStatus);
        $('#tablaProductos tbody').on('click', '.action-delete', handleDelete);

        $('#imagenFile').on('change', function () {
            if (this.files && this.files[0]) {
                const reader = new FileReader();
                reader.onload = (e) => {
                    $('#imagenPreview').attr('src', e.target.result).show();
                };
                reader.readAsDataURL(this.files[0]);
            }
        });

        $('#categoria').on('change', function () {
            const idCategoria = $(this).val();
            loadTiposProducto(idCategoria);
        });
    }

    function saveProducto() {
        const producto = {
            id: $('#id').val() || null,
            nombre: $('#nombre').val(),
            codigo: $('#codigo').val(),
            descripcion: $('#descripcion').val(),
            precio: $('#precio').val(),
            stockMinimo: $('#stockMinimo').val(),
            fechaVencimiento: $('#fechaVencimiento').val() || null,
            categoria: { id: $('#categoria').val() },
            tipoProducto: { id: $('#tipoProducto').val() },
            marca: { id: $('#marca').val() },
            unidad: { id: $('#unidad').val() }
        };

        const formData = new FormData();
        const imagenFile = $('#imagenFile')[0].files[0];
        formData.append('producto', JSON.stringify(producto));
        if (imagenFile) {
            formData.append('imagenFile', imagenFile);
        }
        AppUtils.showLoading(true);
        fetch(ENDPOINTS.save, {
            method: 'POST',
            body: formData
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    productoModal.hide();
                    AppUtils.showNotification('Producto guardado correctamente', 'success');
                    dataTable.ajax.reload();
                } else {
                    AppUtils.showNotification(data.message, 'error');
                }
            })
            .catch(error => AppUtils.showNotification('Error de conexión.', 'error'))
            .finally(() => AppUtils.showLoading(false));
    }

    function handleEdit() {
        const id = $(this).data('id');
        AppUtils.showLoading(true);
        fetch(ENDPOINTS.get(id))
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    openModalForEdit(data.data);
                } else {
                    AppUtils.showNotification('No se pudieron cargar los datos del producto', 'error');
                }
            })
            .catch(() => AppUtils.showNotification('Error de conexión', 'error'))
            .finally(() => AppUtils.showLoading(false));
    }

    function handleToggleStatus() {
        const id = $(this).data('id');
        AppUtils.showLoading(true);
        fetch(ENDPOINTS.toggleStatus(id), { method: 'POST' })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    AppUtils.showNotification(data.message, 'success');
                    dataTable.ajax.reload();
                } else {
                    AppUtils.showNotification(data.message, 'error');
                }
            })
            .catch(() => AppUtils.showNotification('Error de conexión', 'error'))
            .finally(() => AppUtils.showLoading(false));
    }

    function handleDelete() {
        const id = $(this).data('id');
        Swal.fire({
            title: '¿Estás seguro?',
            text: "El producto será marcado como eliminado.",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#dc3545',
            cancelButtonText: 'Cancelar',
            confirmButtonText: 'Sí, eliminar'
        }).then(result => {
            if (result.isConfirmed) {
                AppUtils.showLoading(true);
                fetch(ENDPOINTS.delete(id), { method: 'DELETE' })
                    .then(res => res.json())
                    .then(data => {
                        if (data.success) {
                            AppUtils.showNotification(data.message, 'success');
                            dataTable.ajax.reload();
                        } else {
                            AppUtils.showNotification(data.message, 'error');
                        }
                    })
                    .catch(() => AppUtils.showNotification('Error de conexión', 'error'))
                    .finally(() => AppUtils.showLoading(false));
            }
        });
    }

    function openModalForNew() {
        isEditing = false;
        AppUtils.clearForm(formId);
        $('#modalTitle').text('Nuevo Producto');
        $('#imagenPreview').attr('src', '/images/placeholder.png').show();
        $('#imagenFile').val('');
        $('#tipoProducto').empty().append('<option value="">Seleccione una categoría primero</option>').prop('disabled', true);
        productoModal.show();
    }

    function openModalForEdit(producto) {
        isEditing = true;
        AppUtils.clearForm(formId);
        $('#modalTitle').text('Editar Producto');

        $('#id').val(producto.id);
        $('#nombre').val(producto.nombre);
        $('#codigo').val(producto.codigo);
        $('#descripcion').val(producto.descripcion);
        $('#precio').val(producto.precio);
        $('#stockMinimo').val(producto.stockMinimo);
        $('#fechaVencimiento').val(producto.fechaVencimiento);

        $('#categoria').val(producto.categoria.id);
        $('#marca').val(producto.marca.id);
        $('#unidad').val(producto.unidad.id);

        const tipoProductoId = producto.tipoProducto ? producto.tipoProducto.id : null;
        loadTiposProducto(producto.categoria.id, tipoProductoId);

        const imageUrl = producto.imagen ? `/productos/${producto.imagen}` : '/images/placeholder.png';
        $('#imagenPreview').attr('src', imageUrl).show();
        $('#imagenFile').val('');

        productoModal.show();
    }

    initializeDataTable();
    setupEventListeners();
    loadSelectOptions(`${API_BASE}/categorias`, '#categoria', 'Seleccione una Categoría');
    loadSelectOptions(`${API_BASE}/marcas`, '#marca', 'Seleccione una Marca');
    loadSelectOptions(`${API_BASE}/unidades`, '#unidad', 'Seleccione una Unidad');
});
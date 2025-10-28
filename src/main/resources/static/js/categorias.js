$(document).ready(function () {
    let dataTable;
    let isEditing = false;
    let categoriaModal;
    const formid = '#formCategoria';

    const API_BASE = '/productos/categorias/api';
    const ENDPOINTS = {
        list: `${API_BASE}/listar`,
        save: `${API_BASE}/guardar`,
        get: (id) => `${API_BASE}/${id}`,
        toggleStatus: (id) => `${API_BASE}/cambiar-estado/${id}`,
        delete: (id) => `${API_BASE}/eliminar/${id}`
    };

    initializeDataTable();
    categoriaModal = new bootstrap.Modal(document.getElementById('categoriaModal'));
    setupEventListeners();

    function initializeDataTable() {
        dataTable = $('#tablaCategorias').DataTable({
            responsive: true,
            processing: true,
            ajax: {
                url: ENDPOINTS.list,
                dataSrc: 'data'
            },
            columns: [
                { data: 'id' },
                {
                    data: 'img',
                    orderable: false,
                    searchable: false,
                    render: function (data) {
                        const imageUrl = data ? `/categorias/${data}` : '/images/placeholder.png';
                        return `<img src="${imageUrl}" alt="Categoría" class="img-thumbnail" width="50">`;
                    }
                },
                { data: 'nombre' },
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


    function setupEventListeners() {
        $('#btnNuevoRegistro').on('click', openModalForNew);
        $(formid).on('submit', (e) => { e.preventDefault(); saveCategoria(); });
        $('#tablaCategorias tbody').on('click', '.action-edit', handleEdit);
        $('#tablaCategorias tbody').on('click', '.action-status', handleToggleStatus);
        $('#tablaCategorias tbody').on('click', '.action-delete', handleDelete);
        $('#imagenFile').on('change', function () {
            if (this.files && this.files[0]) {
                const reader = new FileReader();
                reader.onload = (e) => {
                    $('#imagenPreview').attr('src', e.target.result).show();
                };
                reader.readAsDataURL(this.files[0]);
            }
        });
    }

    function reloadTable() {
        dataTable.ajax.reload();
    }

    function saveCategoria() {
        const categoriaData = {
            id: $('#id').val() || null,
            nombre: $('#nombre').val().trim()
        };

        if (!categoriaData.nombre) {
            AppUtils.showNotification('El nombre es obligatorio', 'error');
            return;
        }

        const formData = new FormData();
        const imagenFile = $('#imagenFile')[0].files[0];

        formData.append('categoria', JSON.stringify(categoriaData));
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
                    categoriaModal.hide();
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
                    AppUtils.showNotification('Error al cargar categoría', 'error');
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
            text: "¡La categoría será marcada como eliminada!",
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
        $('#modalTitle').text('Agregar Categoría');
        $('#imagenPreview').attr('src', '/images/placeholder.png').show();
        $('#imagenFile').val('');
        categoriaModal.show();
    }

    function openModalForEdit(categoria) {
        isEditing = true;
        AppUtils.clearForm(formid);
        $('#modalTitle').text('Editar Categoría');
        $('#id').val(categoria.id);
        $('#nombre').val(categoria.nombre);
        const imageUrl = categoria.img ? `/categorias/${categoria.img}` : '/images/placeholder.png';
        $('#imagenPreview').attr('src', imageUrl).show();
        $('#imagenFile').val('');

        categoriaModal.show();
    }
});
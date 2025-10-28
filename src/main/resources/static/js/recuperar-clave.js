$(document).ready(function () {
    const formId = '#recuperarForm';
    const feedbackDiv = $('#feedbackMessage');

    $(formId).on('submit', async function (event) {
        event.preventDefault();
        feedbackDiv.hide().removeClass('alert-success alert-danger');
        $('#ndocumento-error, #correo-error').text('');
        $('#ndocumento, #correo').removeClass('is-invalid');

        const dni = $('#ndocumento').val().trim();
        const correo = $('#correo').val().trim();
        let isValid = true;

        if (!dni || !/^[0-9]{8}$/.test(dni)) {
            $('#ndocumento-error').text('Ingrese un DNI válido de 8 dígitos.');
            $('#ndocumento').addClass('is-invalid');
            isValid = false;
        }

        if (!correo || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(correo)) {
            $('#correo-error').text('Ingrese un correo electrónico válido.');
            $('#correo').addClass('is-invalid');
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        AppUtils.showLoading(true);

        try {
            const response = await fetch('/recuperar-clave/solicitar', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    ndocumento: dni,
                    correo: correo
                }),
            });

            const result = await response.json();

            if (response.ok && result.success) {
                feedbackDiv.text(result.message || 'Se han enviado las instrucciones a tu correo electrónico.')
                    .removeClass('alert-danger')
                    .addClass('alert-success')
                    .show();
                $(formId)[0].reset();
            } else {
                feedbackDiv.text(result.message || 'Ocurrió un error. Verifica tus datos e inténtalo de nuevo.')
                    .removeClass('alert-success')
                    .addClass('alert-danger')
                    .show();
            }

        } catch (error) {
            console.error('Error en solicitud de recuperación:', error);
            feedbackDiv.text('Error de conexión. Inténtalo más tarde.')
                .removeClass('alert-success')
                .addClass('alert-danger')
                .show();
        } finally {
            AppUtils.showLoading(false);
        }
    });

    $('#ndocumento, #correo').on('input', function () {
        $(this).removeClass('is-invalid');
        $(`#${this.id}-error`).text('');
        feedbackDiv.hide();
    });
});
$(document).ready(function () {
    const formId = '#restablecerForm';
    const nuevaClaveInput = $('#nuevaClave');
    const confirmarClaveInput = $('#confirmarClave');
    const requirementsWrapper = $('#passwordRequirementsWrapper');
    const reqItems = requirementsWrapper.find('.req-item');

    const MIN_LENGTH = 8;
    const UPPERCASE_REGEX = /[A-Z]/;
    const LOWERCASE_REGEX = /[a-z]/;
    const DIGIT_REGEX = /\d/;
    const NO_SPACE_REGEX = /^\S*$/;

    function validatePasswordRealtime() {
        const password = nuevaClaveInput.val();
        let isOverallValid = true;

        $('#nuevaClave-error').text('');
        nuevaClaveInput.removeClass('is-invalid');
        reqItems.removeClass('req-valid req-invalid');

        if (!password) {
            return false;
        }

        if (password.length >= MIN_LENGTH) {
            $('#req-length').addClass('req-valid');
        } else {
            $('#req-length').addClass('req-invalid');
            isOverallValid = false;
        }

        if (UPPERCASE_REGEX.test(password)) {
            $('#req-uppercase').addClass('req-valid');
        } else {
            $('#req-uppercase').addClass('req-invalid');
            isOverallValid = false;
        }

        if (LOWERCASE_REGEX.test(password)) {
            $('#req-lowercase').addClass('req-valid');
        } else {
            $('#req-lowercase').addClass('req-invalid');
            isOverallValid = false;
        }

        if (DIGIT_REGEX.test(password)) {
            $('#req-digit').addClass('req-valid');
        } else {
            $('#req-digit').addClass('req-invalid');
            isOverallValid = false;
        }

        if (NO_SPACE_REGEX.test(password)) {
            $('#req-nospace').addClass('req-valid');
        } else {
            $('#req-nospace').addClass('req-invalid');
            isOverallValid = false;
        }

        if (!isOverallValid) {
            $('#nuevaClave-error').text('La contraseña no cumple todos los requisitos.');
            nuevaClaveInput.addClass('is-invalid');
        }

        validateConfirmation();

        return isOverallValid;
    }

    function validateConfirmation() {
        const nuevaClave = nuevaClaveInput.val();
        const confirmarClave = confirmarClaveInput.val();
        const errorDiv = $('#confirmarClave-error');
        let isMatchValid = true;

        if (confirmarClave) {
            if (nuevaClave !== confirmarClave) {
                errorDiv.text('Las contraseñas no coinciden.');
                confirmarClaveInput.addClass('is-invalid');
                isMatchValid = false;
            } else {
                errorDiv.text('');
                confirmarClaveInput.removeClass('is-invalid');
            }
        } else {
            errorDiv.text('');
            confirmarClaveInput.removeClass('is-invalid');
            if (nuevaClave) {
                isMatchValid = false;
            }
        }
        return isMatchValid;
    }


    $(formId).on('submit', function (event) {
        const isPasswordValid = validatePasswordRealtime();
        const doPasswordsMatch = validateConfirmation();

        let isFormValid = isPasswordValid && doPasswordsMatch;

        if (!isFormValid) {
            event.preventDefault();
            if (isPasswordValid && !doPasswordsMatch && !confirmarClaveInput.val()) {
                $('#confirmarClave-error').text('Confirme la contraseña.');
                confirmarClaveInput.addClass('is-invalid');
            } else if (isPasswordValid && !doPasswordsMatch) {
                $('#confirmarClave-error').text('Las contraseñas no coinciden.');
                confirmarClaveInput.addClass('is-invalid');
            }
        } else {
            $('.alert-danger').hide();
            AppUtils.showLoading(true);
        }
    });

    nuevaClaveInput.on('input', validatePasswordRealtime);
    confirmarClaveInput.on('input', validateConfirmation);

    $('#nuevaClave, #confirmarClave').on('input', function () {
        $('.alert-danger').hide();
    });

});
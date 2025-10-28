package com.sistema.productos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.from:#{null}}")
    private String fromAddress;

    @Value("${spring.mail.username}")
    private String usernameAddress;

    @Value("${app.base.url}")
    private String appBaseUrl;

    public void EnviarCredencialesEmail(String to, String username, String plainPassword) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress != null ? fromAddress : usernameAddress);
            message.setTo(to);
            message.setSubject("Credenciales de Acceso MiMarketVirgenDeGuadalupe");

            String text = String.format(
                "Se ha creado una cuenta para ti en nuestro sistema.\n\n" +
                "Usuario: %s\n" +
                "Contraseña: %s\n\n" +
                "Por favor, inicia sesión y cambia tu contraseña lo antes posible.\n\n",
                username,
                plainPassword
            );

            message.setText(text);

            mailSender.send(message);
            System.out.println("Correo de credenciales enviado exitosamente a: " + to);

        } catch (MailException e) {
            System.err.println("Error al enviar correo de credenciales a " + to + ": " + e.getMessage());
        }
    }

    public void EnviarPasswordRecoveryEmail(String to, String recoveryToken) {
         try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress != null ? fromAddress : usernameAddress);
            message.setTo(to);
            message.setSubject("Recuperación de Contraseña");

            String resetUrl = appBaseUrl + "/restablecer-clave?token=" + recoveryToken;

            String text = String.format(
                "Has solicitado restablecer tu contraseña.\n\n" +
                "Haz clic en el siguiente enlace para continuar:\n" +
                "%s\n\n" +
                "Si no solicitaste esto, ignora este mensaje.\n" +
                "El enlace expirará pronto (generalmente en %d minutos).\n\n",
                resetUrl,
                15
            );
            message.setText(text);

            mailSender.send(message);
            System.out.println("Correo de recuperación (con URL) enviado exitosamente a: " + to);

         } catch (MailException e) {
             System.err.println("Error al enviar correo de recuperación a " + to + ": " + e.getMessage());
             throw new RuntimeException("Error al enviar el correo de recuperación.", e);
         }
    }
}
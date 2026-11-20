package co.edu.unbosque.rackFindrBack.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailSender {

	@Autowired
	private JavaMailSender mailSender;

	@Value("${spring.mail.username:tu_correo@gmail.com}")
	private String fromEmail;

	public EmailSender() {
	}

	public boolean sendEmail(String to, String subject, String body) {
		if (to == null || to.isEmpty()) {
			return false;
		}
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(fromEmail);
			message.setTo(to);
			message.setSubject(subject);
			message.setText(body);
			mailSender.send(message);
			return true;
		} catch (Exception e) {
			System.out.println("Error enviando correo: " + e.getMessage());
			return false;
		}
	}

	public boolean enviarConfirmacionRegistro(String to, String username, String codigoVerificacion) {
		String enlace = "http://localhost:8080/auth/confirmar-cuenta?username=" + username + "&codigo="
				+ codigoVerificacion;
		String subject = "Confirma tu cuenta en RackFindr";
		String body = "Hola " + username + ",\n\n"
				+ "Gracias por registrarte en RackFindr.\n"
				+ "Antes de iniciar sesion, confirma tu cuenta haciendo clic en el siguiente enlace:\n"
				+ enlace + "\n\n"
				+ "O ingresando tu codigo de activacion: " + codigoVerificacion + "\n";
		return sendEmail(to, subject, body);
	}

	public boolean enviarRecuperacionContrasena(String to, String username, String codigoRecuperacion) {
		String enlaceFront = "http://localhost:4200/restablecer-contrasena?username=" + username + "&codigo="
				+ codigoRecuperacion;
		String subject = "Recuperacion de contrasena - RackFindr";
		String body = "Hola " + username + ",\n\n"
				+ "Solicitaste recuperar tu contrasena.\n"
				+ "Haz clic en el siguiente enlace para restablecer tu contrasena:\n"
				+ enlaceFront + "\n\n"
				+ "Codigo de recuperacion: " + codigoRecuperacion + "\n";
		return sendEmail(to, subject, body);
	}

}

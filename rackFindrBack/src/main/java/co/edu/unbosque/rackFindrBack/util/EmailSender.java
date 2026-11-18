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

	public boolean enviarConfirmacionInicioSesion(String to, String username, String fecha, String ip,
			String coordenadas, String ciudad, String pais, String codigoVerificacion) {
		String subject = "Confirmacion de inicio de sesion - RackFindr";
		String body = "Hola " + username + ",\n\n" + "Se registro un inicio de sesion en tu cuenta.\n" + "Fecha: "
				+ fecha + "\n" + "IP: " + ip + "\n" + "Coordenadas: " + coordenadas + " (" + ciudad + ", " + pais
				+ ")\n" + "Codigo de confirmacion: " + codigoVerificacion + "\n";
		return sendEmail(to, subject, body);
	}

	public boolean enviarRecuperacionContrasena(String to, String username, String codigoRecuperacion) {
		String subject = "Recuperacion de contrasena - RackFindr";
		String body = "Hola " + username + ",\n\n" + "Solicitaste recuperar tu contrasena.\n"
				+ "Tu codigo de recuperacion es: " + codigoRecuperacion + "\n";
		return sendEmail(to, subject, body);
	}

	public boolean enviarBienvenidaRegistro(String to, String username) {
		String subject = "Bienvenido a RackFindr";
		String body = "Hola " + username + ", tu cuenta fue creada con exito.";
		return sendEmail(to, subject, body);
	}

}

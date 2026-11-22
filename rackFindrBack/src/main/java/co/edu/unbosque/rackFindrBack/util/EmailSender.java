package co.edu.unbosque.rackFindrBack.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Componente utilitario encargado del envío de correos electrónicos transaccionales mediante SMTP
 * utilizando {@link JavaMailSender}. Gestiona las notificaciones de confirmación de registro
 * y recuperación de contraseña detectando dinámicamente las URLs de despliegue.
 *
 * @author Equipo RackFindr - Universidad El Bosque
 * @version 1.0
 */
@Component
public class EmailSender {

	@Autowired
	private JavaMailSender mailSender;

	@Value("${spring.mail.username:tu_correo@gmail.com}")
	private String fromEmail;

	@Value("${app.backend.url:https://gpcueb.org/rackFindrBack-0.0.2}")
	private String backendUrl;

	@Value("${app.frontend.url:http://localhost:4200}")
	private String frontendUrl;

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

	private String resolverBackendUrl() {
		try {
			ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			if (attrs != null) {
				HttpServletRequest request = attrs.getRequest();
				String contextPath = request.getContextPath() != null ? request.getContextPath() : "";
				String currentUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
				if (!contextPath.isEmpty() && (currentUrl.contains("localhost") || currentUrl.contains("127.0.0.1"))) {
					return "https://gpcueb.org" + contextPath;
				}
				return currentUrl;
			}
		} catch (Exception e) {
			// Usar fallback configurado
		}
		return backendUrl;
	}

	private String resolverFrontendUrl() {
		try {
			ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			if (attrs != null) {
				HttpServletRequest request = attrs.getRequest();
				String origin = request.getHeader("Origin");
				if (origin != null && !origin.isEmpty() && !"null".equalsIgnoreCase(origin)) {
					return origin.endsWith("/") ? origin.substring(0, origin.length() - 1) : origin;
				}
			}
		} catch (Exception e) {
			// Ignorar y usar fallback configurado
		}
		return frontendUrl;
	}

	public boolean enviarConfirmacionRegistro(String to, String username, String codigoVerificacion) {
		String baseBackUrl = resolverBackendUrl();
		String enlace = baseBackUrl + "/auth/confirmar-cuenta?username=" + username + "&codigo="
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
		String baseFrontUrl = resolverFrontendUrl();
		String enlaceFront = baseFrontUrl + "/restablecer-contrasena?username=" + username + "&codigo="
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

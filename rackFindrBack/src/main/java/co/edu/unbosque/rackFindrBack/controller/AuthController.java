package co.edu.unbosque.rackFindrBack.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.rackFindrBack.dto.UserDTO;
import co.edu.unbosque.rackFindrBack.model.User;
import co.edu.unbosque.rackFindrBack.security.JwtUtil;
import co.edu.unbosque.rackFindrBack.service.AuditoriaSesionService;
import co.edu.unbosque.rackFindrBack.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Controlador REST responsable de los flujos públicos de autenticación y gestión de credenciales.
 * Incluye inicio de sesión con generación de JWT, registro con confirmación por correo,
 * activación de cuenta y recuperación/restablecimiento de contraseña.
 *
 * @author Equipo RackFindr - Universidad El Bosque
 * @version 1.0
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(originPatterns = "*")
@Tag(name = "Autenticación", description = "API para autenticación de usuarios (login, registro, confirmación de cuenta y recuperación)")
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	private final UserService userService;
	private final AuditoriaSesionService auditoriaService;

	public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService,
			AuditoriaSesionService auditoriaService) {
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
		this.userService = userService;
		this.auditoriaService = auditoriaService;
	}

	@Operation(summary = "Iniciar sesión de usuario")
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody UserDTO loginRequest, HttpServletRequest request) {
		String ipCliente = request != null ? request.getRemoteAddr() : "127.0.0.1";
		if (loginRequest.getIp() != null && !loginRequest.getIp().isEmpty()) {
			ipCliente = loginRequest.getIp();
		}

		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String jwt = jwtUtil.generateToken(userDetails);

			String role = null;
			if (userDetails instanceof User) {
				User user = (User) userDetails;
				role = user.getRole().name();
			}

			auditoriaService.registrarAuditoriaLogin(
					userDetails.getUsername(), role != null ? role : "USER", ipCliente, loginRequest.getCoordenadas(),
					true, "Inicio de sesion exitoso");

			return ResponseEntity.ok(new AuthResponse(jwt, role));
		} catch (DisabledException e) {
			auditoriaService.registrarAccion(
					loginRequest.getUsername() != null ? loginRequest.getUsername() : "desconocido", "USER",
					"LOGIN_CUENTA_NO_CONFIRMADA", "Intento de login sin confirmar correo", ipCliente,
					loginRequest.getCoordenadas(), false);
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body("Debes confirmar tu cuenta por correo antes de iniciar sesion");
		} catch (AuthenticationException e) {
			e.printStackTrace();
			auditoriaService.registrarAuditoriaLogin(
					loginRequest.getUsername() != null ? loginRequest.getUsername() : "desconocido", "USER", ipCliente,
					loginRequest.getCoordenadas(), false, "Intento fallido de inicio de sesion");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body("Nombre de usuario o contraseña inválidos o usuario no encontrado");
		}
	}

	@Operation(summary = "Registrar un nuevo usuario y enviar enlace de confirmación al correo")
	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody UserDTO registerRequest, HttpServletRequest request) {
		if (userService.findUsernameAlreadyTaken(registerRequest.getUsername())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("El nombre de usuario ya existe");
		}

		int result = userService.registrarUsuarioConConfirmacion(registerRequest);
		if (result == 0) {
			String ip = request != null ? request.getRemoteAddr() : "127.0.0.1";
			auditoriaService.registrarAccion(registerRequest.getUsername(), "USER", "REGISTRO_USUARIO",
					"Usuario registrado pendiente de confirmacion por correo", ip, registerRequest.getCoordenadas(),
					true);
			return ResponseEntity.status(HttpStatus.CREATED)
					.body("Usuario registrado exitosamente. Revisa tu correo para confirmar y activar tu cuenta.");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al registrar el usuario");
		}
	}

	@Operation(summary = "Confirmar y activar cuenta mediante enlace enviado al correo")
	@GetMapping("/confirmar-cuenta")
	public ResponseEntity<String> confirmarCuentaEnlace(@RequestParam String username, @RequestParam String codigo,
			HttpServletRequest request) {
		return procesarConfirmacionCuenta(username, codigo, request);
	}

	@Operation(summary = "Confirmar y activar cuenta mediante código")
	@PostMapping("/confirmar-cuenta")
	public ResponseEntity<String> confirmarCuentaPost(@RequestParam String username, @RequestParam String codigo,
			HttpServletRequest request) {
		return procesarConfirmacionCuenta(username, codigo, request);
	}

	private ResponseEntity<String> procesarConfirmacionCuenta(String username, String codigo,
			HttpServletRequest request) {
		boolean confirmado = userService.confirmarCuentaPorCorreo(username, codigo);
		String ip = request != null ? request.getRemoteAddr() : "127.0.0.1";
		if (confirmado) {
			auditoriaService.registrarAccion(username, "USER", "CONFIRMACION_CUENTA",
					"Cuenta confirmada y activada por correo", ip, null, true);
			return new ResponseEntity<>("Cuenta confirmada y activada exitosamente. Ya puedes iniciar sesion.",
					HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Codigo o enlace de confirmacion invalido", HttpStatus.BAD_REQUEST);
		}
	}

	@Operation(summary = "Solicitar enlace de recuperación de contraseña al correo")
	@PostMapping("/recuperar-contrasena")
	public ResponseEntity<String> recuperarContrasena(@RequestParam String usernameOrEmail,
			HttpServletRequest request) {
		int status = userService.solicitarRecuperacionContrasena(usernameOrEmail);
		String ip = request != null ? request.getRemoteAddr() : "127.0.0.1";
		if (status == 0) {
			auditoriaService.registrarAccion(usernameOrEmail, "USER", "SOLICITUD_RECUPERACION_CONTRASENA",
					"Enlace de recuperacion enviado al correo", ip, null, true);
			return new ResponseEntity<>("Enlace de recuperacion enviado al correo", HttpStatus.OK);
		} else if (status == 1) {
			return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>("El usuario no tiene correo registrado", HttpStatus.BAD_REQUEST);
		}
	}

	@Operation(summary = "Restablecer contraseña mediante enlace recibido por correo")
	@PostMapping("/restablecer-contrasena")
	public ResponseEntity<String> restablecerContrasena(@RequestParam String usernameOrEmail,
			@RequestParam String codigo, @RequestParam String nuevaPassword, HttpServletRequest request) {
		int status = userService.restablecerContrasenaConCodigo(usernameOrEmail, codigo, nuevaPassword);
		String ip = request != null ? request.getRemoteAddr() : "127.0.0.1";
		if (status == 0) {
			auditoriaService.registrarAccion(usernameOrEmail, "USER", "RESTABLECER_CONTRASENA",
					"Contrasena restablecida exitosamente", ip, null, true);
			return new ResponseEntity<>("Contrasena actualizada exitosamente", HttpStatus.OK);
		} else if (status == 1) {
			return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>("Codigo o enlace de recuperacion invalido", HttpStatus.BAD_REQUEST);
		}
	}

	private static class AuthResponse {
		private final String token;
		private final String role;

		public AuthResponse(String token) {
			this.token = token;
			this.role = null;
		}

		public AuthResponse(String token, String role) {
			this.token = token;
			this.role = role;
		}

		public String getToken() {
			return token;
		}

		public String getRole() {
			return role;
		}
	}

}

package co.edu.unbosque.rackFindrBack.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.rackFindrBack.dto.AuditoriaSesionDTO;
import co.edu.unbosque.rackFindrBack.dto.UserDTO;
import co.edu.unbosque.rackFindrBack.model.User;
import co.edu.unbosque.rackFindrBack.security.JwtUtil;
import co.edu.unbosque.rackFindrBack.service.AuditoriaSesionService;
import co.edu.unbosque.rackFindrBack.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = { "http://localhost:4200", "http://localhost:8080", "http://localhost:8081", "*" })
@Tag(name = "Autenticación", description = "API para autenticación de usuarios (login y registro)")
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

			AuditoriaSesionDTO auditoria = auditoriaService.registrarAuditoriaLogin(
					userDetails.getUsername(), role != null ? role : "USER", ipCliente, loginRequest.getCoordenadas(),
					true, "Inicio de sesion exitoso");

			String fechaStr = auditoria.getFecha() != null ? auditoria.getFecha().toString() : "";
			userService.generarYEnviarConfirmacionLogin(userDetails.getUsername(), fechaStr, auditoria.getIp(),
					auditoria.getCoordenadas(), auditoria.getCiudad(), auditoria.getPais());

			return ResponseEntity.ok(new AuthResponse(jwt, role));
		} catch (AuthenticationException e) {
			e.printStackTrace();
			auditoriaService.registrarAuditoriaLogin(
					loginRequest.getUsername() != null ? loginRequest.getUsername() : "desconocido", "USER", ipCliente,
					loginRequest.getCoordenadas(), false, "Intento fallido de inicio de sesion");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body("Nombre de usuario o contraseña inválidos o usuario no encontrado");
		}
	}

	@Operation(summary = "Registrar un nuevo usuario")
	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody UserDTO registerRequest) {
		if (userService.findUsernameAlreadyTaken(registerRequest.getUsername())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("El nombre de usuario ya existe");
		}

		int result = userService.create(registerRequest);
		if (result == 0) {
			return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado exitosamente");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al registrar el usuario");
		}
	}

	@Operation(summary = "Confirmar inicio de sesión con código enviado al correo")
	@PostMapping("/confirmar-login")
	public ResponseEntity<String> confirmarLogin(@RequestParam String username, @RequestParam String codigo) {
		boolean confirmado = userService.confirmarCodigoInicioSesion(username, codigo);
		if (confirmado) {
			auditoriaService.marcarUltimaSesionConfirmadaPorCorreo(username);
			return new ResponseEntity<>("Inicio de sesion confirmado exitosamente", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Codigo invalido o expirado", HttpStatus.BAD_REQUEST);
		}
	}

	@Operation(summary = "Solicitar código de recuperación de contraseña al correo")
	@PostMapping("/recuperar-contrasena")
	public ResponseEntity<String> recuperarContrasena(@RequestParam String usernameOrEmail) {
		int status = userService.solicitarRecuperacionContrasena(usernameOrEmail);
		if (status == 0) {
			return new ResponseEntity<>("Codigo de recuperacion enviado al correo", HttpStatus.OK);
		} else if (status == 1) {
			return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>("El usuario no tiene correo registrado", HttpStatus.BAD_REQUEST);
		}
	}

	@Operation(summary = "Restablecer contraseña con código de recuperación")
	@PostMapping("/restablecer-contrasena")
	public ResponseEntity<String> restablecerContrasena(@RequestParam String usernameOrEmail,
			@RequestParam String codigo, @RequestParam String nuevaPassword) {
		int status = userService.restablecerContrasenaConCodigo(usernameOrEmail, codigo, nuevaPassword);
		if (status == 0) {
			return new ResponseEntity<>("Contrasena actualizada exitosamente", HttpStatus.OK);
		} else if (status == 1) {
			return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>("Codigo de recuperacion invalido", HttpStatus.BAD_REQUEST);
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

package co.edu.unbosque.rackFindrBack.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.rackFindrBack.dto.UserDTO;
import co.edu.unbosque.rackFindrBack.model.User.Role;
import co.edu.unbosque.rackFindrBack.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = { "http://localhost:4200", "http://localhost:8080", "http://localhost:8081", "*" })
@Transactional
@Tag(name = "Gestión de Usuarios", description = "Endpoints para administrar usuarios")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

	@Autowired
	private UserService userServ;

	public UserController() {
	}

	@Operation(summary = "Crear usuario (JSON)")
	@PostMapping(path = "/createjson", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> createNewWithJSON(@RequestBody UserDTO newUser) {
		if (newUser.getUsername().contains("<") || newUser.getUsername().contains(">")) {
			return new ResponseEntity<>("Solicitud con caracteres invalidos", HttpStatus.BAD_REQUEST);
		}
		int status = userServ.create(newUser);

		if (status == 0) {
			return new ResponseEntity<>("Usuario creado exitosamente", HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(
					"Error al crear el usuario, posiblemente el nombre de usuario ya está en uso",
					HttpStatus.NOT_ACCEPTABLE);
		}
	}

	@Operation(summary = "Crear usuario (parámetros)")
	@PostMapping(path = "/create")
	ResponseEntity<String> createNew(@RequestParam String username, @RequestParam String password,
			@RequestParam(required = true) Role role, @RequestParam(required = false) String email) {
		UserDTO newUser = new UserDTO(username, password, email, role);
		int status = userServ.create(newUser);

		if (status == 0) {
			return new ResponseEntity<>("Usuario creado exitosamente", HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(
					"Error al crear el usuario, posiblemente el nombre de usuario ya está en uso",
					HttpStatus.NOT_ACCEPTABLE);
		}
	}

	@Operation(summary = "Obtener todos los usuarios")
	@GetMapping("/getall")
	ResponseEntity<List<UserDTO>> getAll() {
		List<UserDTO> users = userServ.getAll();
		if (users.isEmpty()) {
			return new ResponseEntity<>(users, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(users, HttpStatus.ACCEPTED);
		}
	}

	@Operation(summary = "Contar usuarios")
	@GetMapping("/count")
	ResponseEntity<Long> countAll() {
		Long count = userServ.count();
		if (count == 0) {
			return new ResponseEntity<>(count, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(count, HttpStatus.ACCEPTED);
		}
	}

	@Operation(summary = "Verificar existencia de usuario")
	@GetMapping("/exists/{id}")
	ResponseEntity<Boolean> exists(@PathVariable Long id) {
		boolean found = userServ.exist(id);
		if (found) {
			return new ResponseEntity<>(true, HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>(false, HttpStatus.NO_CONTENT);
		}
	}

	@Operation(summary = "Obtener usuario por ID")
	@GetMapping("/getbyid/{id}")
	ResponseEntity<UserDTO> getById(@PathVariable Long id) {
		UserDTO found = userServ.getById(id);
		if (found != null) {
			return new ResponseEntity<>(found, HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>(new UserDTO(), HttpStatus.NOT_FOUND);
		}
	}

	@Operation(summary = "Actualizar usuario (JSON)")
	@PutMapping(path = "/updatejson", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> updateNewWithJSON(@RequestParam Long id, @RequestBody UserDTO newUser) {
		int status = userServ.updateById(id, newUser);

		if (status == 0) {
			return new ResponseEntity<>("Usuario actualizado exitosamente", HttpStatus.ACCEPTED);
		} else if (status == 1) {
			return new ResponseEntity<>("El nuevo nombre de usuario ya está en uso", HttpStatus.IM_USED);
		} else if (status == 2) {
			return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>("Error al actualizar", HttpStatus.BAD_REQUEST);
		}
	}

	@Operation(summary = "Actualizar usuario (parámetros)")
	@PutMapping(path = "/update")
	ResponseEntity<String> updateNew(@RequestParam long id, @RequestParam String newUsername,
			@RequestParam String newPassword, @RequestParam(required = false) Role role,
			@RequestParam(required = false) String email) {
		UserDTO newUser = new UserDTO(newUsername, newPassword, email, role);

		int status = userServ.updateById(id, newUser);

		if (status == 0) {
			return new ResponseEntity<>("Usuario actualizado exitosamente", HttpStatus.ACCEPTED);
		} else if (status == 1) {
			return new ResponseEntity<>("El nuevo nombre de usuario ya está en uso", HttpStatus.IM_USED);
		} else if (status == 2) {
			return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>("Error al actualizar", HttpStatus.BAD_REQUEST);
		}
	}

	@Operation(summary = "Eliminar usuario por ID")
	@DeleteMapping("/deletebyid/{id}")
	ResponseEntity<String> deleteById(@PathVariable Long id) {
		int status = userServ.deleteById(id);

		if (status == 0) {
			return new ResponseEntity<>("Usuario eliminado exitosamente", HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>("Error al eliminar", HttpStatus.NOT_FOUND);
		}
	}

	@Operation(summary = "Eliminar usuario por nombre")
	@DeleteMapping("/deletebyname")
	ResponseEntity<String> deleteByName(@RequestParam String name) {
		int status = userServ.deleteByUsername(name);
		if (status == 0) {
			return new ResponseEntity<>("Usuario eliminado exitosamente", HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>("Error al eliminar", HttpStatus.NOT_FOUND);
		}
	}

}

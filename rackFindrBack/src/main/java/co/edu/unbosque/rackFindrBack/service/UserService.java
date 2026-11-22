package co.edu.unbosque.rackFindrBack.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import co.edu.unbosque.rackFindrBack.dto.UserDTO;
import co.edu.unbosque.rackFindrBack.model.User;
import co.edu.unbosque.rackFindrBack.repository.SolicitudRepository;
import co.edu.unbosque.rackFindrBack.repository.UserRepository;
import co.edu.unbosque.rackFindrBack.util.AESUtil;
import co.edu.unbosque.rackFindrBack.util.EmailSender;

@Service
public class UserService implements CRUDOperation<UserDTO> {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private SolicitudRepository solicitudRepo;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private EmailSender emailSender;

	public UserService() {
	}

	@Override
	public long count() {
		return userRepo.count();
	}

	@Override
	public boolean exist(Long id) {
		return userRepo.existsById(id) ? true : false;
	}

	@Override
	public int create(UserDTO data) {
		User entity = modelMapper.map(data, User.class);
		if (findUsernameAlreadyTaken(entity)) {
			return 1;
		} else {
			entity.setPassword(passwordEncoder.encode(entity.getPassword()));
			if (data.getRole() != null) {
				entity.setRole(data.getRole());
			}
			String plainEmail = data.getEmail();
			if (plainEmail != null && !plainEmail.isEmpty()) {
				entity.setEmail(AESUtil.encrypt(plainEmail));
			}
			userRepo.save(entity);
			return 0;
		}
	}

	private static final Random REGISTRATION_RANDOM = new Random();
	public int registrarUsuarioConConfirmacion(UserDTO data) {
		User entity = modelMapper.map(data, User.class);
		if (findUsernameAlreadyTaken(entity)) {
			return 1;
		}
		entity.setPassword(passwordEncoder.encode(entity.getPassword()));
		if (data.getRole() != null) {
			entity.setRole(data.getRole());
		} else {
			entity.setRole(User.Role.USER);
		}

		entity.setEnabled(false);

		int numero = 100000 + REGISTRATION_RANDOM.nextInt(900000);
		String codigo = String.valueOf(numero);
		entity.setCodigoVerificacion(AESUtil.encrypt(codigo));
		entity.setFechaExpiracionCodigo(LocalDateTime.now().plusMinutes(30));

		String plainEmail = data.getEmail();
		if (plainEmail != null && !plainEmail.isEmpty()) {
			entity.setEmail(AESUtil.encrypt(plainEmail));
		}

		userRepo.save(entity);

		if (plainEmail != null && !plainEmail.isEmpty()) {
			emailSender.enviarConfirmacionRegistro(plainEmail, entity.getUsername(), codigo);
		}
		return 0;
	}

	public boolean confirmarCuentaPorCorreo(String username, String codigo) {
		if (username == null || codigo == null) {
			return false;
		}
		Optional<User> userOpt = userRepo.findByUsername(username);
		if (!userOpt.isPresent()) {
			return false;
		}
		User user = userOpt.get();
		if (user.getCodigoVerificacion() == null) {
			return user.isEnabled();
		}
		String storedCode = AESUtil.decrypt(user.getCodigoVerificacion());
		if (codigo.trim().equals(storedCode)) {
			user.setEnabled(true);
			user.setCodigoVerificacion(null);
			userRepo.save(user);
			return true;
		}
		return false;
	}

	public int cambiarEstadoUsuario(Long id, boolean activo) {
		Optional<User> found = userRepo.findById(id);
		if (found.isPresent()) {
			User user = found.get();
			user.setEnabled(activo);
			userRepo.save(user);
			return 0;
		}
		return 1;
	}

	@Override
	public List<UserDTO> getAll() {
		List<User> entityList = userRepo.findAll();
		List<UserDTO> dtoList = new ArrayList<>();
		entityList.forEach((entity) -> {
			UserDTO dto = modelMapper.map(entity, UserDTO.class);
			if (entity.getEmail() != null) {
				dto.setEmail(AESUtil.decrypt(entity.getEmail()));
			}
			dto.setEnabled(entity.isEnabled());
			dtoList.add(dto);
		});

		return dtoList;
	}

	@Override
	public int deleteById(Long id) {
		Optional<User> found = userRepo.findById(id);
		if (found.isPresent()) {
			solicitudRepo.deleteByUsername(found.get().getUsername());
			userRepo.delete(found.get());
			return 0;
		} else {
			return 1;
		}
	}

	public int deleteByUsername(String username) {
		Optional<User> found = userRepo.findByUsername(username);
		if (found.isPresent()) {
			solicitudRepo.deleteByUsername(username);
			userRepo.delete(found.get());
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	public int updateById(Long id, UserDTO newData) {
		Optional<User> found = userRepo.findById(id);
		if (!found.isPresent()) {
			return 2;
		}

		Optional<User> newFound = userRepo.findByUsername(newData.getUsername());
		if (newFound.isPresent() && !newFound.get().getId().equals(id)) {
			return 1;
		}

		User temp = found.get();
		if (newData.getUsername() != null && !newData.getUsername().isEmpty()) {
			temp.setUsername(newData.getUsername());
		}
		if (newData.getPassword() != null && !newData.getPassword().isEmpty()) {
			temp.setPassword(passwordEncoder.encode(newData.getPassword()));
		}
		if (newData.getRole() != null) {
			temp.setRole(newData.getRole());
		}
		if (newData.getEmail() != null && !newData.getEmail().isEmpty()) {
			temp.setEmail(AESUtil.encrypt(newData.getEmail()));
		}
		temp.setEnabled(newData.isEnabled());
		userRepo.save(temp);
		return 0;
	}

	public UserDTO getById(Long id) {
		Optional<User> found = userRepo.findById(id);
		if (found.isPresent()) {
			UserDTO dto = modelMapper.map(found.get(), UserDTO.class);
			if (found.get().getEmail() != null) {
				dto.setEmail(AESUtil.decrypt(found.get().getEmail()));
			}
			dto.setEnabled(found.get().isEnabled());
			return dto;
		} else {
			return null;
		}
	}

	public boolean findUsernameAlreadyTaken(User newUser) {
		Optional<User> found = userRepo.findByUsername(newUser.getUsername());
		if (found.isPresent()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean findUsernameAlreadyTaken(String username) {
		Optional<User> found = userRepo.findByUsername(username);
		return found.isPresent();
	}

	public int solicitarRecuperacionContrasena(String usernameOrEmail) {
		Optional<User> userOpt = buscarPorUsernameOCorreo(usernameOrEmail);
		if (!userOpt.isPresent()) {
			return 1;
		}
		User user = userOpt.get();
		String plainEmail = user.getEmail() != null ? AESUtil.decrypt(user.getEmail()) : null;
		if (plainEmail == null || plainEmail.isEmpty()) {
			return 2;
		}

		Random random = java.util.concurrent.ThreadLocalRandom.current();
		int numero = 100000 + random.nextInt(900000);
		String codigoRecuperacion = String.valueOf(numero);
		user.setCodigoRecuperacion(AESUtil.encrypt(codigoRecuperacion));
		user.setFechaExpiracionCodigo(LocalDateTime.now().plusMinutes(15));
		userRepo.save(user);

		emailSender.enviarRecuperacionContrasena(plainEmail, user.getUsername(), codigoRecuperacion);
		return 0;
	}

	public int restablecerContrasenaConCodigo(String usernameOrEmail, String codigo, String nuevaPassword) {
		if (nuevaPassword == null || nuevaPassword.isEmpty()) {
			return 3;
		}
		Optional<User> userOpt = buscarPorUsernameOCorreo(usernameOrEmail);
		if (!userOpt.isPresent()) {
			return 1;
		}
		User user = userOpt.get();
		if (user.getCodigoRecuperacion() == null) {
			return 2;
		}

		String codigoGuardado = AESUtil.decrypt(user.getCodigoRecuperacion());
		if (codigo == null || !codigo.trim().equals(codigoGuardado)) {
			return 2;
		}

		user.setPassword(passwordEncoder.encode(nuevaPassword));
		user.setCodigoRecuperacion(null);
		user.setFechaExpiracionCodigo(null);
		userRepo.save(user);
		return 0;
	}

	public Optional<User> buscarPorUsernameOCorreo(String usernameOrEmail) {
		if (usernameOrEmail == null || usernameOrEmail.isEmpty()) {
			return Optional.empty();
		}
		Optional<User> byUsername = userRepo.findByUsername(usernameOrEmail);
		if (byUsername.isPresent()) {
			return byUsername;
		}
		Optional<User> byEncEmail = userRepo.findByEmail(AESUtil.encrypt(usernameOrEmail));
		if (byEncEmail.isPresent()) {
			return byEncEmail;
		}
		return userRepo.findByEmail(usernameOrEmail);
	}

}

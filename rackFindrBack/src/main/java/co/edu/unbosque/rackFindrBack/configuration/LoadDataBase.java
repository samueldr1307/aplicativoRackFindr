package co.edu.unbosque.rackFindrBack.configuration;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import co.edu.unbosque.rackFindrBack.model.User;
import co.edu.unbosque.rackFindrBack.repository.UserRepository;
import co.edu.unbosque.rackFindrBack.util.AESUtil;

@Configuration
public class LoadDataBase {

	private static final Logger log = LoggerFactory.getLogger(LoadDataBase.class);

	@Bean
	CommandLineRunner initDatabase(UserRepository userRepo, PasswordEncoder passwordEncoder) {
		return args -> {
			Optional<User> found = userRepo.findByUsername("admin");
			if (found.isPresent()) {
				log.info("El administrador ya existe, omitiendo la creación del administrador...");
			} else {
				User adminUser = new User("admin", passwordEncoder.encode("1234567890"), User.Role.ADMIN);
				adminUser.setEmail(AESUtil.encrypt("admin@rackfindr.com"));
				userRepo.save(adminUser);
				log.info("Precargando usuario administrador");
			}

			Optional<User> found2 = userRepo.findByUsername("normaluser");
			if (found2.isPresent()) {
				log.info("El usuario normal ya existe, omitiendo la creación del usuario normal...");
			} else {
				User normalUser = new User("normaluser", passwordEncoder.encode("1234567890"), User.Role.USER);
				normalUser.setEmail(AESUtil.encrypt("normaluser@rackfindr.com"));
				userRepo.save(normalUser);
				log.info("Precargando usuario normal");
			}
		};
	}

}

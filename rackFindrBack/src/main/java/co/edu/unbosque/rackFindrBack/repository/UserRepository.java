package co.edu.unbosque.rackFindrBack.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.rackFindrBack.model.User;

public interface UserRepository extends JpaRepository<User, Long>{
	
	public Optional<User> findByUsername(String username);

	public Optional<User> findByEmail(String email);

	public void deleteByUsername(String username);


}

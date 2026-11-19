package co.edu.unbosque.rackFindrBack.model;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "useraccount")
public class User implements UserDetails {

	private static final long serialVersionUID = 1L;

	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;

	@Column(unique = true)
	private String username;

	private String password;

	private String email;

	@Enumerated(EnumType.STRING)
	private Role role;

	private boolean accountNonExpired;
	private boolean accountNonLocked;
	private boolean credentialsNonExpired;
	private boolean enabled;

	private String codigoVerificacion;
	private String codigoRecuperacion;
	private LocalDateTime fechaExpiracionCodigo;

	public User() {
		this.accountNonExpired = true;
		this.accountNonLocked = true;
		this.credentialsNonExpired = true;
		this.enabled = true;
		this.role = Role.USER;
	}

	public User(String username, String password) {
		this();
		this.username = username;
		this.password = password;
	}

	public User(String username, String password, Role rol) {
		this();
		this.username = username;
		this.password = password;
		this.role = rol != null ? rol : Role.USER;
	}

	public User(String username, String password, String email, Role rol) {
		this(username, password, rol);
		this.email = email;
	}

	public enum Role {
		USER, ADMIN
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
	}

	@Override
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCodigoVerificacion() {
		return codigoVerificacion;
	}

	public void setCodigoVerificacion(String codigoVerificacion) {
		this.codigoVerificacion = codigoVerificacion;
	}

	public String getCodigoRecuperacion() {
		return codigoRecuperacion;
	}

	public void setCodigoRecuperacion(String codigoRecuperacion) {
		this.codigoRecuperacion = codigoRecuperacion;
	}

	public LocalDateTime getFechaExpiracionCodigo() {
		return fechaExpiracionCodigo;
	}

	public void setFechaExpiracionCodigo(LocalDateTime fechaExpiracionCodigo) {
		this.fechaExpiracionCodigo = fechaExpiracionCodigo;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, password, username);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(id, other.id) && Objects.equals(password, other.password)
				&& Objects.equals(username, other.username);
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", password=" + password + "]";
	}

}

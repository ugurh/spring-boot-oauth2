package io.ugurh.oauth2.user.models;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.ugurh.oauth2.glee.models.Glee;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table( name="TB_USERS", uniqueConstraints = @UniqueConstraint(columnNames={"EMAIL"}))
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotEmpty
	@Email
	private String email;

	@JsonIgnore
	@ToString.Exclude
	private String password;

	@NotNull
	@Enumerated(EnumType.STRING)
	private UserRole role;

	private Double minGleePerDay;

	@JsonIgnore
	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
	@ToString.Exclude
	private Collection<Glee> glee;

}
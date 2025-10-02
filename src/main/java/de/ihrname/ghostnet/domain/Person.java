package de.ihrname.ghostnet.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Person {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  private String name;

  @Pattern(regexp="^$|[+0-9 /()-]{5,}$", message="Telefon optional, aber wenn angegeben, dann gültig.")
  private String phone;

  @Enumerated(EnumType.STRING) @NotNull
  private Role role;
}

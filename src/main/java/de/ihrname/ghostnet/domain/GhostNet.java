package de.ihrname.ghostnet.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.Instant;

@Entity @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class GhostNet {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull @DecimalMin(value="-90.0", message="Lat min -90") @DecimalMax(value="90.0", message="Lat max 90")
  private Double gpsLat;

  @NotNull @DecimalMin(value="-180.0", message="Lon min -180") @DecimalMax(value="180.0", message="Lon max 180")
  private Double gpsLon;

  @NotBlank
  private String estimatedSize; // "klein/mittel/groß" oder "20 m²"

  @Enumerated(EnumType.STRING)
  private NetStatus status; // im Service gesetzt

  private Instant createdAt;

  @ManyToOne(fetch = FetchType.LAZY)
  private Person reportedBy;

  @ManyToOne(fetch = FetchType.LAZY)
  private Person assignee;

  @PrePersist void onCreate() { createdAt = Instant.now(); }
}

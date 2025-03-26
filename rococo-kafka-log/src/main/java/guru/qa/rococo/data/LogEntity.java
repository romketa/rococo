package guru.qa.rococo.data;

import guru.qa.rococo.model.EventType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

@Getter
@Setter
@Entity
@Table(name = "logs")
public class LogEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false, columnDefinition = "BINARY(16) DEFAULT (UUID_TO_BIN(UUID()))")
  private UUID id;

  @Enumerated(EnumType.STRING)
  @Column(name = "event", nullable = false)
  private EventType event;

  @Column(name = "entity_id", nullable = false, columnDefinition = "BINARY(16)")
  private UUID entityId;

  @Column(name = "entity_type", nullable = false, columnDefinition = "text")
  private String entityType;

  @Column(name = "description", columnDefinition = "text")
  private String description;

  @Column(name = "event_date", nullable = false)
  private LocalDateTime eventDate;

  @Column(name = "event_created_date", nullable = false)
  private LocalDateTime eventCreatedDate;
  
  @PrePersist
  protected void onCreate() {
    this.eventCreatedDate = LocalDateTime.now();
  }
}

package guru.qa.rococo.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record LogJson(

    String entityType,
    UUID entityId,
    String description,
    EventType eventType,
    LocalDateTime eventDate
) {

}

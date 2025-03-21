package guru.qa.rococo.model;

import java.util.UUID;
import jdk.jfr.Event;

public record ArtistEvent(

    String name,

    EventType eventType
) {

}

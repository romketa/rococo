package guru.qa.rococo.service;

import guru.qa.rococo.model.ArtistEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class EventConsumerService {

  private static final Logger LOGGER = LoggerFactory.getLogger(EventConsumerService.class);


  @KafkaListener(topics = "events", groupId = "rococo-consumer")
  public void listener(@Payload ArtistEvent artist, Acknowledgment acknowledgment) {
    LOGGER.info("### Event received: {} {}", artist.name(), artist.eventType());
    //TODO добавить возможность создавать записи в базе данных logs при чтении из кафки
    acknowledgment.acknowledge();
  }
}

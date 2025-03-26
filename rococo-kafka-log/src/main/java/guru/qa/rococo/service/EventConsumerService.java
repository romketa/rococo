package guru.qa.rococo.service;

import guru.qa.rococo.data.LogEntity;
import guru.qa.rococo.data.repository.LogRepository;
import guru.qa.rococo.model.LogJson;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class EventConsumerService {

  private static final Logger LOGGER = LoggerFactory.getLogger(EventConsumerService.class);
  private final LogRepository logRepository;

  public EventConsumerService(LogRepository logRepository) {
    this.logRepository = logRepository;
  }

  @Transactional
  @KafkaListener(topics = {"artists", "museums", "paintings", "userdata"}, groupId = "rococo-consumer")
  public void listener(@Payload LogJson log, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
    LOGGER.info("### Event received: {} {}", log.eventType(), topic);
    //TODO добавить возможность создавать записи в базе данных logs при чтении из кафки
    LogEntity logEntity = new LogEntity();
    logEntity.setEvent(log.eventType());
    logEntity.setEntityType(log.entityType());
    logEntity.setEntityId(log.entityId());
    logEntity.setDescription(log.description());
    logEntity.setEventDate(log.eventDate());
    LogEntity savedEntity = logRepository.save(logEntity);
    LOGGER.info("### Event {} saved in database", savedEntity.getEvent());
  }
}

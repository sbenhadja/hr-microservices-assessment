package com.hr.employee.e2e;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;
import static org.awaitility.Awaitility.await;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public abstract class AbstractE2ETest {

  // ─────────────────────────────────────────────
  // Containers
  // ─────────────────────────────────────────────
  static final Network NETWORK = Network.newNetwork();

  @Container
  static final KafkaContainer KAFKA =
      new KafkaContainer(
              DockerImageName.parse("confluentinc/cp-kafka:7.6.0")
                  .asCompatibleSubstituteFor("confluentinc/cp-kafka"))
          .withNetwork(NETWORK)
          .withNetworkAliases("kafka");

  @Container
  static final PostgreSQLContainer<?> POSTGRES =
      new PostgreSQLContainer<>("postgres:16")
          .withNetwork(NETWORK)
          .withNetworkAliases("hr-db")
          .withDatabaseName("hrdb")
          .withUsername("postgres")
          .withPassword("admin");

  @Container
  static final GenericContainer<?> EMPLOYEE_SERVICE =
      new GenericContainer<>(
              new ImageFromDockerfile()
                  .withDockerfile(Path.of("").toAbsolutePath().resolve("Dockerfile").normalize()))
          .withNetwork(NETWORK)
          .withNetworkAliases("employee-service")
          .withExposedPorts(8081)
          .withEnv("SPRING_DATASOURCE_URL", "jdbc:postgresql://hr-db:5432/hrdb")
          .withEnv("SPRING_DATASOURCE_USERNAME", "postgres")
          .withEnv("SPRING_DATASOURCE_PASSWORD", "admin")
          .withEnv("SPRING_KAFKA_BOOTSTRAP_SERVERS", "kafka:9092")
          .dependsOn(KAFKA, POSTGRES)
          .waitingFor(Wait.forHttp("/actuator/health").forPort(8081).forStatusCode(200));

  // Test state
  protected KafkaConsumer<String, String> kafkaConsumer;
  protected ObjectMapper objectMapper = new ObjectMapper();
  protected String baseUrl;

  @BeforeEach
  void setup() {

    baseUrl = "http://" + EMPLOYEE_SERVICE.getHost() + ":" + EMPLOYEE_SERVICE.getMappedPort(8081);

    RestAssured.baseURI = baseUrl;

    kafkaConsumer = createConsumer();
    kafkaConsumer.subscribe(List.of("employee.events"));
  }

  @AfterEach
  void cleanup() {
    if (kafkaConsumer != null) {
      kafkaConsumer.close();
    }
  }

  // Kafka Consumer Factory
  private KafkaConsumer<String, String> createConsumer() {

    Properties props = new Properties();
    props.put(BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
    props.put(GROUP_ID_CONFIG, "e2e-" + UUID.randomUUID());
    props.put(AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ENABLE_AUTO_COMMIT_CONFIG, "true");

    props.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    props.put(VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

    return new KafkaConsumer<>(props);
  }

  // ─────────────────────────────────────────────
  // Stable Kafka polling (NO IllegalState, NO flaky await)
  // ─────────────────────────────────────────────
  protected ConsumerRecord<String, String> pollUntil(String topic, Predicate<String> matcher) {

    AtomicReference<ConsumerRecord<String, String>> result = new AtomicReference<>();
    await()
        .atMost(Duration.ofSeconds(40))
        .pollInterval(Duration.ofMillis(500))
        .until(
            () -> {
              ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(500));

              for (ConsumerRecord<String, String> r : records.records(topic)) {
                System.out.println("KAFKA EVENT => " + r.value());
                if (matcher.test(r.value())) {
                  result.set(r);
                  return true;
                }
              }
              return false;
            });

    return result.get();
  }
}

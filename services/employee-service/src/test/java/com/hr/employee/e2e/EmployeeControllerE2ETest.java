package com.hr.employee.e2e;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.fasterxml.jackson.databind.JsonNode;
import io.restassured.http.ContentType;
import java.util.UUID;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Employee Controller — E2E Tests (Real Containers)")
class EmployeeControllerE2ETest extends AbstractE2ETest {

  private static final UUID IT_DEPARTMENT_ID =
      UUID.fromString("633513ee-65bc-4e08-96c4-88c5b4a74c21");

  private static final UUID HR_DEPARTMENT_ID =
      UUID.fromString("11111111-1111-1111-1111-111111111111");

  private static final UUID FINANCE_DEPARTMENT_ID =
      UUID.fromString("22222222-2222-2222-2222-222222222222");

  private static final UUID MARKETING_DEPARTMENT_ID =
      UUID.fromString("33333333-3333-3333-3333-333333333333");

  private static final UUID SALES_DEPARTMENT_ID =
      UUID.fromString("44444444-4444-4444-4444-444444444444");

  private static final UUID LEGAL_DEPARTMENT_ID =
      UUID.fromString("55555555-5555-5555-5555-555555555555");

  // POST /api/v1/employees
  @Test
  @DisplayName("POST /employees → 201 Created + persisted + Kafka event fired")
  void createEmployee_shouldReturn201_andPublishKafkaEvent() throws Exception {

    String body =
        """
            {
              "firstName":  "Alice",
              "lastName":   "Martin",
              "email":      "alice.martin@company.com",
              "departmentId": "%s",
              "recrutementDate": "2024-01-15"
            }
            """
            .formatted(IT_DEPARTMENT_ID);

    // 1. Call real REST endpoint
    String responseBody =
        given()
            .contentType(ContentType.JSON)
            .body(body)
            .when()
            .post("/api/v1/employees")
            .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("firstName", equalTo("Alice"))
            .body("lastName", equalTo("Martin"))
            .body("email", equalTo("alice.martin@company.com"))
            .body("department.id", equalTo(IT_DEPARTMENT_ID.toString()))
            .body("department.name", equalTo("IT"))
            .body("recrutementDate", equalTo("2024-01-15"))
            .body("status", equalTo("ACTIVE"))
            .extract()
            .asString();

    String createdId = objectMapper.readTree(responseBody).get("id").asText();

    // 2. Assert Kafka event was published
    ConsumerRecord<String, String> record =
        pollUntil(
            "employee.events",
            v -> {
              try {
                JsonNode json = objectMapper.readTree(v);
                return "CREATED".equals(json.get("eventType").asText())
                    && createdId.equals(json.get("employeeId").asText());
              } catch (Exception e) {
                return false;
              }
            });
    JsonNode event = objectMapper.readTree(record.value());

    assertThat(event.get("employeeId").asText()).isEqualTo(createdId);
    assertThat(event.get("eventType").asText()).isEqualTo("CREATED");
    assertThat(event.get("status").asText()).isEqualTo("ACTIVE");
  }

  @Test
  @DisplayName("POST /employees → 409 Conflict when email already exists")
  void createEmployee_shouldReturn409_whenEmailDuplicate() {

    String body =
        """
            {
              "firstName":  "Bob",
              "lastName":   "Dupont",
              "email":      "bob.dupont@company.com",
              "departmentId": "%s",
              "recrutementDate": "2024-01-15"
            }
            """
            .formatted(HR_DEPARTMENT_ID);

    // First creation — must succeed
    given()
        .contentType(ContentType.JSON)
        .body(body)
        .when()
        .post("/api/v1/employees")
        .then()
        .statusCode(201);

    // Second creation with same email — must conflict
    given()
        .contentType(ContentType.JSON)
        .body(body)
        .when()
        .post("/api/v1/employees")
        .then()
        .statusCode(409)
        .body("message", containsString("Email already exists"));
  }

  // GET /api/v1/employees
  @Test
  @DisplayName("GET /employees → 200 with list of all employees")
  void getAllEmployees_shouldReturn200_withNonEmptyList() {

    // Ensure at least one employee exists
    given()
        .contentType(ContentType.JSON)
        .body(
            """
                {
                  "firstName":  "Carol",
                  "lastName":   "Smith",
                  "email":      "carol.smith@company.com",
                  "departmentId": "%s",
                   "recrutementDate": "2024-01-15"
                }
                """
                .formatted(FINANCE_DEPARTMENT_ID))
        .when()
        .post("/api/v1/employees")
        .then()
        .statusCode(201);

    given()
        .when()
        .get("/api/v1/employees")
        .then()
        .statusCode(200)
        .body("$", not(empty()))
        .body("[0].id", notNullValue())
        .body("[0].status", notNullValue());
  }

  // GET /api/v1/employees/{id}
  @Test
  @DisplayName("GET /employees/{id} → 200 with correct employee data")
  void getEmployeeById_shouldReturn200_withCorrectData() throws Exception {

    // Create employee first
    String created =
        given()
            .contentType(ContentType.JSON)
            .body(
                """
                    {
                      "firstName":  "David",
                      "lastName":   "Lee",
                      "email":      "david.lee@company.com",
                      "departmentId": "%s",
                      "recrutementDate": "2024-01-15"
                    }
                 """
                    .formatted(IT_DEPARTMENT_ID))
            .when()
            .post("/api/v1/employees")
            .then()
            .statusCode(201)
            .extract()
            .asString();

    String id = objectMapper.readTree(created).get("id").asText();

    // Fetch by ID
    given()
        .pathParam("id", id)
        .when()
        .get("/api/v1/employees/{id}")
        .then()
        .statusCode(200)
        .body("id", equalTo(id))
        .body("firstName", equalTo("David"))
        .body("lastName", equalTo("Lee"))
        .body("email", equalTo("david.lee@company.com"))
        .body("department.id", equalTo(IT_DEPARTMENT_ID.toString()))
        .body("department.name", equalTo("IT"))
        .body("recrutementDate", equalTo("2024-01-15"))
        .body("status", equalTo("ACTIVE"));
  }

  @Test
  @DisplayName("GET /employees/{id} → 404 when not found")
  void getEmployeeById_shouldReturn404_whenNotFound() {

    given()
        .pathParam("id", UUID.randomUUID())
        .when()
        .get("/api/v1/employees/{id}")
        .then()
        .statusCode(404)
        .body("message", containsString("Employee not found"));
  }

  // PUT /api/v1/employees/{id}
  @Test
  @DisplayName("PUT /employees/{id} → 200 Updated + Kafka event fired")
  void updateEmployee_shouldReturn200_andPublishKafkaEvent() throws Exception {

    // 1. Create
    String created =
        given()
            .contentType(ContentType.JSON)
            .body(
                """
                    {
                      "firstName":  "Eve",
                      "lastName":   "Brown",
                      "email":      "eve.brown@company.com",
                      "departmentId": "%s",
                       "recrutementDate": "2024-01-15"
                    }
                    """
                    .formatted(MARKETING_DEPARTMENT_ID))
            .when()
            .post("/api/v1/employees")
            .then()
            .statusCode(201)
            .extract()
            .asString();

    String id = objectMapper.readTree(created).get("id").asText();

    // 2. Update
    given()
        .contentType(ContentType.JSON)
        .pathParam("id", id)
        .body(
            """
                {
                  "firstName":  "Eve",
                  "lastName":   "Brown-Updated",
                  "email":      "eve.brown@company.com",
                  "departmentId": "%s",
                  "recrutementDate": "2024-01-15",
                  "status": "ACTIVE"
                }
                """
                .formatted(SALES_DEPARTMENT_ID))
        .when()
        .put("/api/v1/employees/{id}")
        .then()
        .statusCode(200)
        .body("lastName", equalTo("Brown-Updated"))
        .body("department.id", equalTo(SALES_DEPARTMENT_ID.toString()))
        .body("department.name", equalTo("Sales"))
        .body("recrutementDate", equalTo("2024-01-15"));

    // 3. Assert update Kafka event
    ConsumerRecord<String, String> record =
        pollUntil(
            "employee.events",
            v -> {
              try {
                JsonNode json = objectMapper.readTree(v);
                return "UPDATED".equals(json.get("eventType").asText())
                    && id.equals(json.get("employeeId").asText());
              } catch (Exception e) {
                return false;
              }
            });

    JsonNode event = objectMapper.readTree(record.value());

    assertThat(event.get("employeeId").asText()).isEqualTo(id);
    assertThat(event.get("eventType").asText()).isEqualTo("UPDATED");
    assertThat(event.get("status").asText()).isEqualTo("ACTIVE");
  }

  @Test
  @DisplayName("PUT /employees/{id} → 404 when updating non-existing employee")
  void updateEmployee_shouldReturn404_whenNotFound() {

    given()
        .contentType(ContentType.JSON)
        .pathParam("id", UUID.randomUUID())
        .body(
            """
                {
                  "firstName":  "Ghost",
                  "lastName":   "User",
                  "email":      "ghost@company.com",
                  "departmentId": "%s",
                  "recrutementDate": "2024-01-15",
                  "status": "ACTIVE"
                }
                """
                .formatted(IT_DEPARTMENT_ID))
        .when()
        .put("/api/v1/employees/{id}")
        .then()
        .statusCode(404);
  }

  // PATCH /api/v1/employees/{id}/deactivate
  @Test
  @DisplayName("PATCH /employees/{id}/deactivate → 204 + status INACTIVE + Kafka event")
  void deactivateEmployee_shouldReturn204_andPublishKafkaEvent() throws Exception {

    // 1. Create
    String created =
        given()
            .contentType(ContentType.JSON)
            .body(
                """
                    {
                      "firstName":  "Frank",
                      "lastName":   "White",
                      "email":      "frank.white@company.com",
                      "departmentId": "%s",
                       "recrutementDate": "2024-01-15"
                    }
                """
                    .formatted(LEGAL_DEPARTMENT_ID))
            .when()
            .post("/api/v1/employees")
            .then()
            .statusCode(201)
            .extract()
            .asString();

    String id = objectMapper.readTree(created).get("id").asText();

    // 2. Deactivate
    given()
        .pathParam("id", id)
        .when()
        .patch("/api/v1/employees/{id}/deactivate")
        .then()
        .statusCode(200);

    // 3. Verify status via GET
    given()
        .pathParam("id", id)
        .when()
        .get("/api/v1/employees/{id}")
        .then()
        .statusCode(200)
        .body("status", equalTo("INACTIVE"));

    // 4. Assert Kafka deactivation event
    ConsumerRecord<String, String> record =
        pollUntil(
            "employee.events",
            v -> {
              try {
                JsonNode json = objectMapper.readTree(v);
                return "DESACTIVATED".equals(json.get("eventType").asText())
                    && "INACTIVE".equals(json.get("status").asText())
                    && id.equals(json.get("employeeId").asText());
              } catch (Exception e) {
                return false;
              }
            });

    JsonNode event = objectMapper.readTree(record.value());

    assertThat(event.get("employeeId").asText()).isEqualTo(id);
    assertThat(event.get("eventType").asText()).isEqualTo("DESACTIVATED");
    assertThat(event.get("status").asText()).isEqualTo("INACTIVE");
  }

  @Test
  @DisplayName("PATCH /employees/{id}/deactivate → 404 when not found")
  void deactivateEmployee_shouldReturn404_whenNotFound() {

    given()
        .pathParam("id", UUID.randomUUID())
        .when()
        .patch("/api/v1/employees/{id}/deactivate")
        .then()
        .statusCode(404);
  }
}

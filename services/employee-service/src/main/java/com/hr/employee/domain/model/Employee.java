package com.hr.employee.domain.model;

import com.hr.employee.domain.enums.Status;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
public class Employee {

  @Id @GeneratedValue @UuidGenerator private UUID id;
  private String firstName;
  private String lastName;
  private String email;

  @ManyToOne(
      fetch = FetchType.LAZY,
      cascade = {})
  @JoinColumn(name = "department_id")
  private Department department;

  private LocalDate recrutementDate;

  @Enumerated(EnumType.STRING)
  private Status status = Status.ACTIVE;

  public Employee() {}

  //  public Employee(String firstName, String lastName, String email, Department departement) {
  //    this.firstName = firstName;
  //    this.lastName = lastName;
  //    this.email = email;
  //    this.department = departement;
  //  }

  public Employee(
      String firstName,
      String lastName,
      String email,
      Department departement,
      String status,
      LocalDate recrutementDate) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.department = departement;
    this.status = Status.valueOf(status);
    this.recrutementDate = recrutementDate;
  }

  public Employee(
      String firstName,
      String lastName,
      String email,
      Department departement,
      LocalDate recrutementDate) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.department = departement;
    this.recrutementDate = recrutementDate;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Department getDepartment() {
    return department;
  }

  public void setDepartment(Department department) {
    this.department = department;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public LocalDate getRecrutementDate() {
    return recrutementDate;
  }

  public void setRecrutementDate(LocalDate recrutementDate) {
    this.recrutementDate = recrutementDate;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Employee other = (Employee) obj;
    return id != null && id.equals(other.id);
  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "employee{"
        + "id="
        + id
        + ", firstName='"
        + firstName
        + '\''
        + ", lastName='"
        + lastName
        + '\''
        + ", email='"
        + email
        + '\''
        + ", departmentId="
        + department
        + '\''
        + ", status="
        + status
        + '\''
        + ", dateRecrutement="
        + recrutementDate
        + '}';
  }
}

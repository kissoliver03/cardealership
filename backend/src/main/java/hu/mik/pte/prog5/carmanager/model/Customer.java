package hu.mik.pte.prog5.carmanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long customerId;

    @NotBlank(message = "A keresztnév nem lehet üres!")
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @NotBlank(message = "A vezetéknév nem lehet üres!")
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @NotBlank(message = "Az e-mail cím nem lehet üres!")
    @Email(message = "Érvényes e-mail címet adjon meg!")
    @Column(name = "e_mail", nullable = false, unique = true, length = 100)
    private String email;

    @NotNull
    @Column(name = "is_regular_customer", nullable = false)
    private Boolean isRegularCustomer = false;

    public Customer() {
    }

    public Customer(String firstName, String lastName, String email, Boolean isRegularCustomer) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.isRegularCustomer = isRegularCustomer;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
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

    public Boolean getIsRegularCustomer() {
        return isRegularCustomer;
    }

    public void setIsRegularCustomer(Boolean isRegularCustomer) {
        this.isRegularCustomer = isRegularCustomer;
    }

    public String getFullName() {
        return lastName + " " + firstName;
    }

    @Override
    public String toString() {
        return getFullName() + " (" + email + ")";
    }
}

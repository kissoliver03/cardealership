package hu.mik.pte.prog5.carmanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "car")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_id")
    private Long carId;

    @NotBlank(message = "A rendszám nem lehet üres!")
    @Column(name = "license_plate", unique = true, nullable = false, length = 40)
    private String licensePlate;

    @NotNull(message = "Kategória kiválasztása kötelező!")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @NotBlank(message = "A gyártó nem lehet üres!")
    @Column(name = "car_manufacturer", nullable = false, length = 100)
    private String carManufacturer;

    @NotBlank(message = "A modell nem lehet üres!")
    @Column(name = "car_model", nullable = false, length = 100)
    private String carModel;

    @NotNull(message = "A km óra állás kötelező!")
    @Min(value = 0, message = "A futásteljesítmény nem lehet negatív!")
    @Column(name = "mileage")
    private Integer mileage = 0;

    @NotNull(message = "A státusz kötelező!")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 40)
    private CarStatus status = CarStatus.AVAILABLE;

    public Car() {
    }

    public Car(String licensePlate, Category category, String carManufacturer, String carModel, Integer mileage, CarStatus status) {
        this.licensePlate = licensePlate;
        this.category = category;
        this.carManufacturer = carManufacturer;
        this.carModel = carModel;
        this.mileage = mileage;
        this.status = status;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getCarManufacturer() {
        return carManufacturer;
    }

    public void setCarManufacturer(String carManufacturer) {
        this.carManufacturer = carManufacturer;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }

    public CarStatus getStatus() {
        return status;
    }

    public void setStatus(CarStatus status) {
        this.status = status;
    }

    public String getCategoryName() {
        return category != null ? category.getCategoryName() : "";
    }

    public BigDecimal getDailyFee() {
        return category != null ? category.getDailyFee() : BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return carManufacturer + " " + carModel + " (" + licensePlate + ")";
    }
}

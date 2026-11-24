package hu.mik.pte.prog5.carmanager.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class Car {

    private Long carId;

    @NotBlank(message = "A rendszám nem lehet üres!")
    private String licensePlate;

    @NotNull(message = "Kategória kiválasztása kötelező!")
    private Long categoryId;

    @NotBlank(message = "A gyártó nem lehet üres!")
    private String carManufacturer;

    @NotBlank(message = "A modell nem lehet üres!")
    private String carModel;

    @NotNull(message = "A km óra állás kötelező!")
    @Min(value = 0, message = "A futásteljesítmény nem lehet negatív!")
    private Integer mileage = 0;

    @NotNull(message = "A státusz kötelező!")
    private CarStatus status = CarStatus.AVAILABLE;

    private String categoryName;
    private BigDecimal dailyFee;

    public Car() {
    }

    public Car(String licensePlate, Long categoryId, String carManufacturer, String carModel, Integer mileage, CarStatus status) {
        this.licensePlate = licensePlate;
        this.categoryId = categoryId;
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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigDecimal getDailyFee() {
        return dailyFee;
    }

    public void setDailyFee(BigDecimal dailyFee) {
        this.dailyFee = dailyFee;
    }

    @Override
    public String toString() {
        return carManufacturer + " " + carModel + " (" + licensePlate + ")";
    }
}

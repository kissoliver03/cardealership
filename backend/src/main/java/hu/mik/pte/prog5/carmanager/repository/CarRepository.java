package hu.mik.pte.prog5.carmanager.repository;

import hu.mik.pte.prog5.carmanager.model.Car;
import hu.mik.pte.prog5.carmanager.model.CarStatus;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CarRepository {

    private final JdbcClient jdbcClient;

    private final RowMapper<Car> carRowMapper = (rs, rowNum) -> {
        Car car = new Car();
        car.setCarId(rs.getLong("car_id"));
        car.setLicensePlate(rs.getString("license_plate"));
        car.setCategoryId(rs.getLong("category_id"));
        car.setCarManufacturer(rs.getString("car_manufacturer"));
        car.setCarModel(rs.getString("car_model"));
        car.setMileage(rs.getInt("mileage"));
        car.setStatus(CarStatus.valueOf(rs.getString("status")));

        car.setCategoryName(rs.getString("category_name"));
        car.setDailyFee(rs.getBigDecimal("daily_fee"));
        return car;
    };

    public CarRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<Car> findAll() {
        return jdbcClient.sql("""
                SELECT c.car_id, c.license_plate, c.category_id, c.car_manufacturer,
                       c.car_model, c.mileage, c.status,
                       cat.category_name, cat.daily_fee
                FROM car c
                JOIN category cat ON c.category_id = cat.category_id
                ORDER BY c.car_id
                """)
                .query(carRowMapper)
                .list();
    }

    public List<Car> findAvailable() {
        return jdbcClient.sql("""
                SELECT c.car_id, c.license_plate, c.category_id, c.car_manufacturer,
                       c.car_model, c.mileage, c.status,
                       cat.category_name, cat.daily_fee
                FROM car c
                JOIN category cat ON c.category_id = cat.category_id
                WHERE c.status = 'AVAILABLE'
                ORDER BY c.car_manufacturer, c.car_model
                """)
                .query(carRowMapper)
                .list();
    }

    public List<Car> findByCategory(Long categoryId) {
        return jdbcClient.sql("""
                SELECT c.car_id, c.license_plate, c.category_id, c.car_manufacturer,
                       c.car_model, c.mileage, c.status,
                       cat.category_name, cat.daily_fee
                FROM car c
                JOIN category cat ON c.category_id = cat.category_id
                WHERE c.category_id = :categoryId
                ORDER BY c.car_id
                """)
                .param("categoryId", categoryId)
                .query(carRowMapper)
                .list();
    }

    public Optional<Car> findById(Long carId) {
        return jdbcClient.sql("""
                SELECT c.car_id, c.license_plate, c.category_id, c.car_manufacturer,
                       c.car_model, c.mileage, c.status,
                       cat.category_name, cat.daily_fee
                FROM car c
                JOIN category cat ON c.category_id = cat.category_id
                WHERE c.car_id = :carId
                """)
                .param("carId", carId)
                .query(carRowMapper)
                .optional();
    }

    public Car save(Car car) {
        if (car.getCarId() == null) {
            Long generatedId = jdbcClient.sql("""
                    INSERT INTO car (license_plate, category_id, car_manufacturer, car_model, mileage, status)
                    VALUES (:licensePlate, :categoryId, :manufacturer, :model, :mileage, :status)
                    RETURNING car_id
                    """)
                    .param("licensePlate", car.getLicensePlate())
                    .param("categoryId", car.getCategoryId())
                    .param("manufacturer", car.getCarManufacturer())
                    .param("model", car.getCarModel())
                    .param("mileage", car.getMileage())
                    .param("status", car.getStatus().name())
                    .query(Long.class)
                    .single();
            car.setCarId(generatedId);
        } else {
            jdbcClient.sql("""
                    UPDATE car
                    SET license_plate = :licensePlate,
                        category_id = :categoryId,
                        car_manufacturer = :manufacturer,
                        car_model = :model,
                        mileage = :mileage,
                        status = :status
                    WHERE car_id = :carId
                    """)
                    .param("licensePlate", car.getLicensePlate())
                    .param("categoryId", car.getCategoryId())
                    .param("manufacturer", car.getCarManufacturer())
                    .param("model", car.getCarModel())
                    .param("mileage", car.getMileage())
                    .param("status", car.getStatus().name())
                    .param("carId", car.getCarId())
                    .update();
        }
        return car;
    }

    public void updateStatus(Long carId, CarStatus status) {
        jdbcClient.sql("UPDATE car SET status = :status WHERE car_id = :carId")
                .param("status", status.name())
                .param("carId", carId)
                .update();
    }

    public void deleteById(Long carId) {
        jdbcClient.sql("DELETE FROM car WHERE car_id = :carId")
                .param("carId", carId)
                .update();
    }
}

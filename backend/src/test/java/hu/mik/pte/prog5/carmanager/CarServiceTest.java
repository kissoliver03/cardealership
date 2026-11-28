package hu.mik.pte.prog5.carmanager;

import hu.mik.pte.prog5.carmanager.model.Car;
import hu.mik.pte.prog5.carmanager.model.CarStatus;
import hu.mik.pte.prog5.carmanager.service.CarService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class CarServiceTest {

    @Autowired
    private CarService carService;

    @Test
    void testListCarsFromDatabaseWithJpa() {
        List<Car> cars = carService.list();
        assertNotNull(cars);
        assertFalse(cars.isEmpty());
    }

    @Test
    void testListCarsByStatus() {
        List<Car> availableCars = carService.listByStatus(CarStatus.AVAILABLE);
        assertNotNull(availableCars);
        assertFalse(availableCars.isEmpty());
        assertTrue(availableCars.stream().allMatch(c -> c.getStatus() == CarStatus.AVAILABLE));

        List<Car> allCars = carService.listByStatus(null);
        assertNotNull(allCars);
        assertTrue(allCars.size() >= availableCars.size());
    }
}

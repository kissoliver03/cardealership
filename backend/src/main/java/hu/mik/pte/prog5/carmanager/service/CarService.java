package hu.mik.pte.prog5.carmanager.service;

import hu.mik.pte.prog5.carmanager.model.Car;
import hu.mik.pte.prog5.carmanager.model.CarStatus;
import hu.mik.pte.prog5.carmanager.repository.CarRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarService {

    private final CarRepository carRepository;

    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public List<Car> list() {
        return carRepository.findAll();
    }

    public List<Car> listAvailable() {
        return carRepository.findAvailable();
    }

    public List<Car> listByCategory(Long categoryId) {
        return carRepository.findByCategory(categoryId);
    }

    public Car findById(Long id) {
        return carRepository.findById(id).orElse(null);
    }

    public Car save(Car car) {
        return carRepository.save(car);
    }

    public void updateStatus(Long id, CarStatus status) {
        carRepository.updateStatus(id, status);
    }

    public void deleteById(Long id) {
        carRepository.deleteById(id);
    }
}

package hu.mik.pte.prog5.carmanager.service;

import hu.mik.pte.prog5.carmanager.model.Car;
import hu.mik.pte.prog5.carmanager.model.CarStatus;
import hu.mik.pte.prog5.carmanager.repository.CarRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CarService {

    private final CarRepository carRepository;

    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public List<Car> list() {
        return carRepository.findAll();
    }

    public List<Car> listAvailable() {
        return carRepository.findByStatus(CarStatus.AVAILABLE);
    }

    public List<Car> listByStatus(CarStatus status) {
        if (status == null) {
            return carRepository.findAll();
        }
        return carRepository.findByStatus(status);
    }

    public List<Car> listByCategory(Long categoryId) {
        return carRepository.findByCategoryCategoryId(categoryId);
    }

    public Car findById(Long id) {
        return carRepository.findById(id).orElse(null);
    }

    @Transactional
    public Car save(Car car) {
        return carRepository.save(car);
    }

    @Transactional
    public void updateStatus(Long id, CarStatus status) {
        carRepository.findById(id).ifPresent(car -> car.setStatus(status));
    }

    @Transactional
    public void deleteById(Long id) {
        carRepository.deleteById(id);
    }
}

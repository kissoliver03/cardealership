package hu.mik.pte.prog5.carmanager.service;

import hu.mik.pte.prog5.carmanager.model.Car;
import hu.mik.pte.prog5.carmanager.model.CarStatus;
import hu.mik.pte.prog5.carmanager.model.Customer;
import hu.mik.pte.prog5.carmanager.model.Rental;
import hu.mik.pte.prog5.carmanager.repository.CarRepository;
import hu.mik.pte.prog5.carmanager.repository.CustomerRepository;
import hu.mik.pte.prog5.carmanager.repository.RentalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class RentalService {

    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;
    private final CustomerRepository customerRepository;

    public RentalService(RentalRepository rentalRepository,
                         CarRepository carRepository,
                         CustomerRepository customerRepository) {
        this.rentalRepository = rentalRepository;
        this.carRepository = carRepository;
        this.customerRepository = customerRepository;
    }

    public List<Rental> listAll() {
        return rentalRepository.findAllByOrderByFromDateDesc();
    }

    public List<Rental> list() {
        return listAll();
    }

    public List<Rental> listActive() {
        return rentalRepository.findByReturnDateIsNullOrderByFromDateDesc();
    }

    public List<Rental> getCustomerHistory(Long customerId) {
        return rentalRepository.findByCustomerCustomerIdOrderByFromDateDesc(customerId);
    }

    public boolean isCarFree(Long carId, LocalDate fromDate, LocalDate toDate) {
        if (fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("A kezdő dátum nem lehet későbbi a záró dátumnál.");
        }
        return !rentalRepository.hasConflictingRental(carId, fromDate, toDate);
    }

    public BigDecimal calculateEstimatedFee(Long carId, Long customerId, LocalDate fromDate, LocalDate toDate) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Nem található autó az adott azonosítóval."));
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Nem található ügyfél az adott azonosítóval."));

        long days = ChronoUnit.DAYS.between(fromDate, toDate);
        if (days <= 0) {
            days = 1;
        }

        BigDecimal dailyFee = car.getCategory().getDailyFee();
        BigDecimal total = dailyFee.multiply(BigDecimal.valueOf(days));

        if (Boolean.TRUE.equals(customer.getIsRegularCustomer())) {
            total = total.multiply(BigDecimal.valueOf(0.80));
        }

        return total;
    }

    @Transactional
    public Rental startRental(Long carId, Long customerId, LocalDate fromDate, LocalDate toDate) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Nem található autó az adott azonosítóval."));
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Nem található ügyfél az adott azonosítóval."));

        if (fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("A kezdő dátum nem lehet későbbi a záró dátumnál.");
        }

        if (rentalRepository.hasConflictingRental(carId, fromDate, toDate)) {
            throw new IllegalStateException("Az autó már foglalt erre az időszakra.");
        }

        Rental rental = new Rental(car, customer, fromDate, toDate);

        LocalDate today = LocalDate.now();
        if (fromDate.isAfter(today)) {
            car.setStatus(CarStatus.RESERVED);
        } else {
            car.setStatus(CarStatus.RENTED);
        }

        carRepository.save(car);
        return rentalRepository.save(rental);
    }

    @Transactional
    public Rental returnCar(Long carId, LocalDate actualReturnDate) {
        Rental rental = rentalRepository.findFirstByCarCarIdAndReturnDateIsNull(carId)
                .orElseThrow(() -> new IllegalStateException("Nincs aktív bérlés ehhez az autóhoz."));

        if (actualReturnDate == null) {
            actualReturnDate = LocalDate.now();
        }

        rental.setReturnDate(actualReturnDate);

        long days = ChronoUnit.DAYS.between(rental.getFromDate(), actualReturnDate);
        if (days <= 0) {
            days = 1;
        }

        BigDecimal dailyFee = rental.getCar().getCategory().getDailyFee();
        BigDecimal finalFee = dailyFee.multiply(BigDecimal.valueOf(days));

        if (Boolean.TRUE.equals(rental.getCustomer().getIsRegularCustomer())) {
            finalFee = finalFee.multiply(BigDecimal.valueOf(0.80));
        }

        rental.setRentalFee(finalFee);

        Car car = rental.getCar();
        car.setStatus(CarStatus.AVAILABLE);
        carRepository.save(car);

        return rentalRepository.save(rental);
    }
}

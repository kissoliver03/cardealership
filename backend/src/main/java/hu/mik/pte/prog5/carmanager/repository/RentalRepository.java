package hu.mik.pte.prog5.carmanager.repository;

import hu.mik.pte.prog5.carmanager.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    List<Rental> findAllByOrderByFromDateDesc();

    List<Rental> findByReturnDateIsNullOrderByFromDateDesc();

    Optional<Rental> findFirstByCarCarIdAndReturnDateIsNull(Long carId);

    List<Rental> findByCustomerCustomerIdOrderByFromDateDesc(Long customerId);

    @Query("""
        SELECT COUNT(r) > 0
        FROM Rental r
        WHERE r.car.carId = :carId
          AND r.returnDate IS NULL
          AND :fromDate <= r.toDate
          AND :toDate >= r.fromDate
    """)
    boolean hasConflictingRental(@Param("carId") Long carId,
                                 @Param("fromDate") LocalDate fromDate,
                                 @Param("toDate") LocalDate toDate);
}

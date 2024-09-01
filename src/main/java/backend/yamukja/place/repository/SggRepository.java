package backend.yamukja.place.repository;

import backend.yamukja.place.model.Sgg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SggRepository extends JpaRepository<Sgg, Long> {
    // Custom query methods can be added here if needed
}
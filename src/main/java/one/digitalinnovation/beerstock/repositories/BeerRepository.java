package one.digitalinnovation.beerstock.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import one.digitalinnovation.beerstock.entities.Beer;

public interface BeerRepository extends JpaRepository<Beer, Long>{

	Optional<Beer> findByName(String name);
}

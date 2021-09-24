package one.digitalinnovation.beerstock.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import one.digitalinnovation.beerstock.dto.BeerDTO;
import one.digitalinnovation.beerstock.entities.Beer;
import one.digitalinnovation.beerstock.exception.BeerAlreadyRegisteredException;
import one.digitalinnovation.beerstock.exception.BeerNotFoundException;
import one.digitalinnovation.beerstock.mapper.BeerMapper;
import one.digitalinnovation.beerstock.repositories.BeerRepository;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class BeerService {

	private final BeerRepository beerRepository;
	private final BeerMapper beerMapper = BeerMapper.INSTANCE;
	
	public BeerDTO createBeer(BeerDTO beerDTO) throws BeerAlreadyRegisteredException {
		verifyIfIsAlereadyRegistered(beerDTO.getName());
		Beer beerToSave = beerMapper.toModel(beerDTO);
		Beer savedBeer = beerRepository.save(beerToSave);
		return beerMapper.toDTO(savedBeer);
	}
	
	public BeerDTO findByName(String name) throws BeerNotFoundException {
		Beer foundBeer = beerRepository.findByName(name)
				.orElseThrow(() -> new BeerNotFoundException(name));
		return beerMapper.toDTO(foundBeer);
	}

	public List<BeerDTO> listAll() {
		List<Beer> allBeers = beerRepository.findAll();
		
		return allBeers.stream()
				.map(beerMapper::toDTO)
				.collect(Collectors.toList());
	}

	public void delete(Long id) throws BeerNotFoundException {
		verifyIfExists(id);
		beerRepository.deleteById(id);
	}
	
	private void verifyIfIsAlereadyRegistered(String name) throws BeerAlreadyRegisteredException {
		Optional<Beer> optSaveBeer = beerRepository.findByName(name);
		if (optSaveBeer.isPresent()) {
			throw new BeerAlreadyRegisteredException(name);
		}	
	}
	
	private Beer verifyIfExists(Long id) throws BeerNotFoundException {
		return beerRepository.findById(id)
				.orElseThrow(() -> new BeerNotFoundException(id));
	}
}

package one.digitalinnovation.beerstock.tests.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import one.digitalinnovation.beerstock.dto.BeerDTO;
import one.digitalinnovation.beerstock.entities.Beer;
import one.digitalinnovation.beerstock.exception.BeerAlreadyRegisteredException;
import one.digitalinnovation.beerstock.exception.BeerNotFoundException;
import one.digitalinnovation.beerstock.mapper.BeerMapper;
import one.digitalinnovation.beerstock.repositories.BeerRepository;
import one.digitalinnovation.beerstock.services.BeerService;
import one.digitalinnovation.beerstock.tests.builder.BeerDTOBuilder;

@ExtendWith(MockitoExtension.class)
public class BeerServiceTest {

	@Mock
	private BeerRepository beerRepository;
	
	private BeerMapper beerMapper = BeerMapper.INSTANCE;
	
	@InjectMocks
	private BeerService beerService;
	
	@Test
	void whenBeerInformedThenItShouldBeCreated() throws BeerAlreadyRegisteredException{
		 //given
	     BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
	     Beer expectedSavedBeer = beerMapper.toModel(expectedBeerDTO);
	     
	     //when
	     Mockito.when(beerRepository.findByName(expectedBeerDTO.getName())).thenReturn(Optional.empty());
	     Mockito.when(beerRepository.save(expectedSavedBeer)).thenReturn(expectedSavedBeer);
	     
	     //then
	     BeerDTO createdBeerDTO = beerService.createBeer(expectedBeerDTO);
	     
	     MatcherAssert.assertThat(createdBeerDTO.getId(), Matchers.is(Matchers.equalTo(expectedBeerDTO.getId())));
	     MatcherAssert.assertThat(createdBeerDTO.getName(), Matchers.is(Matchers.equalTo(expectedBeerDTO.getName())));
	     MatcherAssert.assertThat(createdBeerDTO.getQuantity(), Matchers.is(Matchers.equalTo(expectedBeerDTO.getQuantity())));
	     
	     //assertEquals(expectedBeerDTO.getId(), createdBeerDTO.getId());
	     //assertEquals(expectedBeerDTO.getName(), createdBeerDTO.getName());    
	}
	
	void whenAlreadyRegisteredBeerInformedThenAnExceptionShouldBeThrown() throws BeerAlreadyRegisteredException {
		 //given
	     BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
	     Beer duplicatedBeer = beerMapper.toModel(expectedBeerDTO);
	     
	     //when
	     when(beerRepository.findByName(expectedBeerDTO.getName())).thenReturn(Optional.of(duplicatedBeer));
	     
	     //then
	     assertThrows(BeerAlreadyRegisteredException.class, () -> beerService.createBeer(expectedBeerDTO));
	}
	
	@Test
	void whenValidBeerNameIsGivenThenReturnBeer() throws BeerNotFoundException{
		// given
	    BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
	    Beer expectedFoundBeer = beerMapper.toModel(expectedFoundBeerDTO);
		
	    // when
	    when(beerRepository.findByName(expectedFoundBeer.getName())).thenReturn(Optional.of(expectedFoundBeer));
	    
	    // then
	    BeerDTO foundBeerDTO = beerService.findByName(expectedFoundBeerDTO.getName());
	    
	    MatcherAssert.assertThat(foundBeerDTO, is(equalTo(expectedFoundBeerDTO)));
	}
	
	@Test
	void whenNoRegisteredBeerNameIsGivenThenThrowAnException() throws BeerNotFoundException{
		// given
	    BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		
	    // when
	    when(beerRepository.findByName(expectedFoundBeerDTO.getName())).thenReturn(Optional.empty());
	    
	    // then
	    assertThrows(BeerNotFoundException.class, () ->beerService.findByName(expectedFoundBeerDTO.getName())); 
	}
}

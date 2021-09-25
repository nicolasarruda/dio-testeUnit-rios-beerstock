package one.digitalinnovation.beerstock.tests.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.hamcrest.MatcherAssert;
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
import one.digitalinnovation.beerstock.exception.BeerStockExceededException;
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
	void whenBeerInformedThenItShouldBeCreated() throws BeerAlreadyRegisteredException {
		// given
		BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		Beer expectedSavedBeer = beerMapper.toModel(expectedBeerDTO);

		// when
		Mockito.when(beerRepository.findByName(expectedBeerDTO.getName())).thenReturn(Optional.empty());
		Mockito.when(beerRepository.save(expectedSavedBeer)).thenReturn(expectedSavedBeer);

		// then
		BeerDTO createdBeerDTO = beerService.createBeer(expectedBeerDTO);

		assertThat(createdBeerDTO.getId(), is(equalTo(expectedBeerDTO.getId())));
		assertThat(createdBeerDTO.getName(), is(equalTo(expectedBeerDTO.getName())));
		assertThat(createdBeerDTO.getQuantity(), is(equalTo(expectedBeerDTO.getQuantity())));

		// assertEquals(expectedBeerDTO.getId(), createdBeerDTO.getId());
		// assertEquals(expectedBeerDTO.getName(), createdBeerDTO.getName());
	}

	void whenAlreadyRegisteredBeerInformedThenAnExceptionShouldBeThrown() throws BeerAlreadyRegisteredException {
		// given
		BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		Beer duplicatedBeer = beerMapper.toModel(expectedBeerDTO);

		// when
		when(beerRepository.findByName(expectedBeerDTO.getName())).thenReturn(Optional.of(duplicatedBeer));

		// then
		assertThrows(BeerAlreadyRegisteredException.class, () -> beerService.createBeer(expectedBeerDTO));
	}

	@Test
	void whenValidBeerNameIsGivenThenReturnBeer() throws BeerNotFoundException {
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
	void whenNoRegisteredBeerNameIsGivenThenThrowAnException() throws BeerNotFoundException {
		// given
		BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

		// when
		when(beerRepository.findByName(expectedFoundBeerDTO.getName())).thenReturn(Optional.empty());

		// then
		assertThrows(BeerNotFoundException.class, () -> beerService.findByName(expectedFoundBeerDTO.getName()));
	}

	@Test
	void whenListBeerIsCalledThenReturnAListOfBeers() {
		// given
		BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		Beer expectedFoundBeer = beerMapper.toModel(expectedFoundBeerDTO);

		// when
		when(beerRepository.findAll()).thenReturn(Collections.singletonList(expectedFoundBeer));

		// then
		List<BeerDTO> foundListBeersDTO = beerService.listAll();

		MatcherAssert.assertThat(foundListBeersDTO, is(not(empty())));
		MatcherAssert.assertThat(foundListBeersDTO.get(0), is(equalTo(expectedFoundBeerDTO)));
	}

	@Test
	void whenListBeerIsCalledThenReturnAnEmptyListOfBeers() {
		// when
		when(beerRepository.findAll()).thenReturn(Collections.EMPTY_LIST);

		// then
		List<BeerDTO> foundListBeersDTO = beerService.listAll();

		MatcherAssert.assertThat(foundListBeersDTO, is(not(empty())));
	}

	@Test
	void whenExclusionIsCalledWithValidIdThenABeerShouldBeDeleted() throws BeerNotFoundException {
		// given
		BeerDTO expectedDeletedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		Beer expectedDeletedBeer = beerMapper.toModel(expectedDeletedBeerDTO);

		// when
		when(beerRepository.findById(expectedDeletedBeerDTO.getId())).thenReturn(Optional.of(expectedDeletedBeer));
		doNothing().when(beerRepository).deleteById(expectedDeletedBeerDTO.getId());

		// then
		beerService.deleteById(expectedDeletedBeerDTO.getId());

		verify(beerRepository, times(1)).findById(expectedDeletedBeerDTO.getId());
		verify(beerRepository, times(1)).deleteById(expectedDeletedBeerDTO.getId());
	}

	@Test
	void whenIncrementIsCalledThenIncrementBeerStock() throws BeerNotFoundException, BeerStockExceededException {
		// given
		BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);

		// when
		when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));
		when(beerRepository.save(expectedBeer)).thenReturn(expectedBeer);

		int quantityToIncrement = 45;
		int expectedQuantityAfterIncrement = expectedBeerDTO.getQuantity() + quantityToIncrement;

		// then
		BeerDTO incrementedBeerDTO = beerService.increment(expectedBeerDTO.getId(), quantityToIncrement);

		assertThat(expectedQuantityAfterIncrement, equalTo(incrementedBeerDTO.getQuantity()));
		assertThat(expectedQuantityAfterIncrement, lessThan(expectedBeerDTO.getMax()));
	}
	
	@Test
    void whenIncrementIsGreatherThanMaxThenThrowException() {
        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);

        when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));

        int quantityToIncrement = 80;
        assertThrows(BeerStockExceededException.class, () -> beerService.increment(expectedBeerDTO.getId(), quantityToIncrement));
    }
	
	@Test
    void whenIncrementAfterSumIsGreatherThanMaxThenThrowException() {
        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);

        when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));

        int quantityToIncrement = 45;
        assertThrows(BeerStockExceededException.class, () -> beerService.increment(expectedBeerDTO.getId(), quantityToIncrement));
    }

}

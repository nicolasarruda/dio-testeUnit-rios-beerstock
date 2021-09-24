package one.digitalinnovation.beerstock.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import one.digitalinnovation.beerstock.dto.BeerDTO;
import one.digitalinnovation.beerstock.entities.Beer;

@Mapper
public interface BeerMapper {

	BeerMapper INSTANCE = Mappers.getMapper(BeerMapper.class);
	
	Beer toModel(BeerDTO beerDTO);
	
	BeerDTO toDTO(Beer beer);
}

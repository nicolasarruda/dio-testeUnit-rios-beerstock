package one.digitalinnovation.beerstock.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import one.digitalinnovation.beerstock.dto.BeerDTO;
import one.digitalinnovation.beerstock.dto.response.MessageResponseDTO;
import one.digitalinnovation.beerstock.exception.BeerNotFoundException;
import one.digitalinnovation.beerstock.services.BeerService;

@RestController
@RequestMapping(value = "/api/v1/beers")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class BeerController {

	private BeerService beerService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public MessageResponseDTO createBeer(@RequestBody @Valid BeerDTO beerDTO) {
		return  beerService.createBeer(beerDTO);
	}
	
	@GetMapping
	public List<BeerDTO> listAll(){
		return beerService.listAll();
	}
	
	@GetMapping("/{id}")
	public BeerDTO findById(@PathVariable Long id) throws BeerNotFoundException {
		return beerService.findById(id);
	}
	
	@GetMapping("/{name}")
	public BeerDTO findByName(@PathVariable String name) throws BeerNotFoundException {
		return beerService.findByName(name);
	}
	
	@PutMapping("/{id}")
	public MessageResponseDTO updateById(@PathVariable Long id, @RequestBody @Valid BeerDTO beerDTO) throws BeerNotFoundException {
		return beerService.updateById(id, beerDTO);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteById(@PathVariable Long id) throws BeerNotFoundException {
		beerService.delete(id);
	}
}

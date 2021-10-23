package com.devsuperior.dscatalog.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class ProductService {
	
	@Autowired
	private ProductRepository repository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Transactional(readOnly = true)
	public Page<ProductDTO> findAllPaged(Pageable pageable){
		Page<Product> list = repository.findAll(pageable);
		return list.map(x -> new ProductDTO(x));
		/*
		 * A função stream() transforma uma lista em um recurso que permite a execução de funções
		 * como o .map() para fazer conversão (transformação) de objetos de um tipo em outro.
		 * list.stream().map() . A função map() é quem executa em seu parâmetro uma expressão lambda
		 * para fazer a conversão de tipos de objetos.
		 * Expressão lambda: x -> new ProductDTO(x) irá percorrer cada elemento da lista original
		 * aplicando a conversão.
		 * Para cada elemento x da lista original, transforma-se ele em um outro elemento atravez
		 * de uma função lambda.
		 * Está sendo pego cada elemento da lista original e esta aplicando-se uma função a cada
		 * elemento. Essa função basicamente transforma o elemento x que era do tipo Product
		 * em um novo ProductDTO recebendo esse x.
		 * Com isso se está transformando a lista que era do tipo Product em uma nova lista
		 * do tipo ProductDTO.
		 * Mas esse resultado, até então, será um stream, precisa-se converter de volta
		 * esse resultado para uma lista. Para isso coloca-se em seguida a função .collect() que
		 * irá transformar de volta a strem em uma lista, utilizando como parametro o comando
		 * Collectors.toList(). 
		 */
		
		/*
		 * O que está sendo feito abaixo, é a mesma coisa do que está sendo feito acima 
		 * atraves de uma expressao lambda.
		 * 
		List<ProductDTO> listDto = new ArrayList<>();
		for(Product cat : list) {
			listDto.add(new ProductDTO(cat));
		}
	
		return listDto;
		
		*/
	}

	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {
		Optional<Product> obj = repository.findById(id);
		Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entidade Não Encontrada!"));
		return new ProductDTO(entity, entity.getCategories());
	}

	@Transactional
	public ProductDTO insert(ProductDTO dto) {
		Product entity = new Product();
		copyDtoToEntity(dto, entity);
		//entity.setName(dto.getName());
		entity = repository.save(entity);
		return new ProductDTO(entity);
	}

	@Transactional
	public ProductDTO update(Long id, ProductDTO dto) {
		try {
		Product entity = repository.getOne(id);
		copyDtoToEntity(dto, entity);
		//entity.setName(dto.getName());
		entity = repository.save(entity);
		return new ProductDTO(entity);
		}
		catch(ResourceNotFoundException e) {
			throw new ResourceNotFoundException("Id not found "+id);
		}
	}

	public void delete(Long id) {
		try {
		repository.deleteById(id);
		}
		catch(EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id not found! "+id);
		}
		catch(DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity Violation");
			
		}
		
	}
	
	private void copyDtoToEntity(ProductDTO dto, Product entity) {
		entity.setName(dto.getName());
		entity.setDescription(dto.getDescription());
		entity.setDate(dto.getDate());
		entity.setImgUrl(dto.getImgUrl());
		entity.setPrice(dto.getPrice());
		
		entity.getCategories().clear();
		for (CategoryDTO catDto : dto.getCategories()) {
			Category category = categoryRepository.getOne(catDto.getId());
			entity.getCategories().add(category);
		}
		
	}
}

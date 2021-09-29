package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository repository;
	
	@Transactional(readOnly = true)
	public List<CategoryDTO> findAll(){
		List<Category> list = repository.findAll();
		
		return list.stream().map(x -> new CategoryDTO(x)).collect(Collectors.toList());
		/*
		 * A função stream() transforma uma lista em um recurso que permite a execução de funções
		 * como o .map() para fazer conversão (transformação) de objetos de um tipo em outro.
		 * list.stream().map() . A função map() é quem executa em seu parâmetro uma expressão lambda
		 * para fazer a conversão de tipos de objetos.
		 * Expressão lambda: x -> new CategoryDTO(x) irá percorrer cada elemento da lista original
		 * aplicando a conversão.
		 * Para cada elemento x da lista original, transforma-se ele em um outro elemento atravez
		 * de uma função lambda.
		 * Está sendo pego cada elemento da lista original e esta aplicando-se uma função a cada
		 * elemento. Essa função basicamente transforma o elemento x que era do tipo Category
		 * em um novo CategoryDTO recebendo esse x.
		 * Com isso se está transformando a lista que era do tipo Category em uma nova lista
		 * do tipo CategoryDTO.
		 * Mas esse resultado, até então, será um stream, precisa-se converter de volta
		 * esse resultado para uma lista. Para isso coloca-se em seguida a função .collect() que
		 * irá transformar de volta a strem em uma lista, utilizando como parametro o comando
		 * Collectors.toList(). 
		 */
		
		/*
		 * O que está sendo feito abaixo, é a mesma coisa do que está sendo feito acima 
		 * atraves de uma expressao lambda.
		 * 
		List<CategoryDTO> listDto = new ArrayList<>();
		for(Category cat : list) {
			listDto.add(new CategoryDTO(cat));
		}
	
		return listDto;
		
		*/
	}

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {
		Optional<Category> obj = repository.findById(id);
		Category entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entidade Não Encontrada!"));
		return new CategoryDTO(entity);
	}

	@Transactional
	public CategoryDTO insert(CategoryDTO dto) {
		Category entity = new Category();
		entity.setName(dto.getName());
		entity = repository.save(entity);
		return new CategoryDTO(entity);
	}

	@Transactional
	public CategoryDTO update(Long id, CategoryDTO dto) {
		try {
		Category entity = repository.getOne(id);
		entity.setName(dto.getName());
		entity = repository.save(entity);
		return new CategoryDTO(entity);
		}
		catch(ResourceNotFoundException e) {
			throw new ResourceNotFoundException("Id not found "+id);
		}
	}
}

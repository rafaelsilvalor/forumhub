package com.rafaellor.forumhub.controller;

import com.rafaellor.forumhub.dto.TopicCreateDto;
import com.rafaellor.forumhub.dto.TopicResponseDto;
import com.rafaellor.forumhub.model.Curso;
import com.rafaellor.forumhub.model.Topic;
import com.rafaellor.forumhub.model.User;
import com.rafaellor.forumhub.repository.CursoRepository;
import com.rafaellor.forumhub.repository.TopicRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/topics")
public class TopicController {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private CursoRepository cursoRepository; // Injetar o novo repositório

    @PostMapping
    @Transactional
    public ResponseEntity<TopicResponseDto> createTopic(@RequestBody @Valid TopicCreateDto topicCreateDto,
                                                        UriComponentsBuilder uriComponentsBuilder) {

        // Pega o usuário autenticado do contexto de segurança
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authenticatedUser = (User) authentication.getPrincipal();

        // Verifica se já existe um tópico com o mesmo título ou mensagem
        if (topicRepository.existsByTitle(topicCreateDto.getTitle())) {
            // Lança uma exceção que será capturada pelo ErrorHandler para retornar um erro 409 Conflict
            throw new DataIntegrityViolationException("Title already exists");
        }
        if (topicRepository.existsByMessage(topicCreateDto.getMessage())) {
            throw new DataIntegrityViolationException("Message already exists");
        }

        // Busca a entidade completa do Curso usando o ID fornecido no DTO
        Curso curso = cursoRepository.findById(topicCreateDto.getCursoId())
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + topicCreateDto.getCursoId()));

        // Cria o Tópico, passando o objeto User autenticado e o objeto Curso encontrado
        Topic topic = new Topic(
                topicCreateDto.getTitle(),
                topicCreateDto.getMessage(),
                authenticatedUser,
                curso // Passa a entidade Curso completa
        );
        topic = topicRepository.save(topic);

        // Constrói a URI para o novo recurso criado
        URI uri = uriComponentsBuilder.path("/topics/{id}").buildAndExpand(topic.getId()).toUri();

        // Retorna a resposta 201 Created com a localização do novo recurso e o DTO do tópico criado
        return ResponseEntity.created(uri).body(new TopicResponseDto(topic));
    }

    @GetMapping
    public ResponseEntity<List<TopicResponseDto>> getAllTopics() {
        List<TopicResponseDto> topics = topicRepository.findAll().stream()
                .map(TopicResponseDto::new) // Converte cada Topic em um TopicResponseDto
                .collect(Collectors.toList());
        return ResponseEntity.ok(topics);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TopicResponseDto> getTopicById(@PathVariable Long id) {
        return topicRepository.findById(id)
                .map(topic -> ResponseEntity.ok(new TopicResponseDto(topic)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<TopicResponseDto> updateTopic(@PathVariable Long id,
                                                        @RequestBody @Valid TopicCreateDto topicUpdateDto) {
        return topicRepository.findById(id)
                .map(topic -> {
                    // Verifica duplicidade apenas se o título ou mensagem foi alterado
                    if (!topic.getTitle().equals(topicUpdateDto.getTitle()) && topicRepository.existsByTitle(topicUpdateDto.getTitle())) {
                        throw new DataIntegrityViolationException("Title already exists");
                    }
                    if (!topic.getMessage().equals(topicUpdateDto.getMessage()) && topicRepository.existsByMessage(topicUpdateDto.getMessage())) {
                        throw new DataIntegrityViolationException("Message already exists");
                    }

                    // Busca o novo curso se um ID for fornecido
                    Curso curso = cursoRepository.findById(topicUpdateDto.getCursoId())
                            .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + topicUpdateDto.getCursoId()));

                    // Atualiza o tópico com os novos dados
                    topic.update(topicUpdateDto.getTitle(), topicUpdateDto.getMessage(), curso);

                    return ResponseEntity.ok(new TopicResponseDto(topic));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id) {
        if (topicRepository.existsById(id)) {
            topicRepository.deleteById(id);
            return ResponseEntity.noContent().build(); // Retorna 204 No Content
        }
        return ResponseEntity.notFound().build(); // Retorna 404 Not Found
    }
}

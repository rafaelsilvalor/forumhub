package com.rafaellor.forumhub.repository;

import com.rafaellor.forumhub.model.Course;
import com.rafaellor.forumhub.model.Topic;
import com.rafaellor.forumhub.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
// O profile 'dev' usa o banco de dados H2 em memória, ideal para testes.
@ActiveProfiles("dev")
class TopicRepositoryTest {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private TestEntityManager em; // Gerenciador de entidades para testes de persistência

    @Test
    @DisplayName("Deve retornar true quando um tópico com o título informado existir")
    void existsByTitleScenario1() {
        // Arrange: Preparação
        User author = new User(null, "author.test", "123456");
        em.persist(author);
        Course course = new Course(null, "JPA", "Backend");
        em.persist(course);
        Topic topic = new Topic("Título Único", "Mensagem de teste", author, course);
        em.persist(topic);

        // Act: Ação
        boolean exists = topicRepository.existsByTitle("Título Único");

        // Assert: Verificação
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false quando um tópico com o título informado não existir")
    void existsByTitleScenario2() {
        // Act
        boolean exists = topicRepository.existsByTitle("Título Inexistente");

        // Assert
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Deve retornar true quando um tópico com a mensagem informada existir")
    void existsByMessageScenario1() {
        // Arrange
        User author = new User(null, "author.test2", "123456");
        em.persist(author);
        Course course = new Course(null, "Spring Test", "Testes");
        em.persist(course);
        Topic topic = new Topic("Outro Título", "Mensagem Única", author, course);
        em.persist(topic);

        // Act
        boolean exists = topicRepository.existsByMessage("Mensagem Única");

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false quando um tópico com a mensagem informada não existir")
    void existsByMessageScenario2() {
        // Act
        boolean exists = topicRepository.existsByMessage("Mensagem Inexistente");

        // Assert
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Deve salvar um novo tópico com sucesso")
    void saveTopicTest() {
        // Arrange
        User author = new User(null, "author.save", "123456");
        em.persist(author);
        Course course = new Course(null, "Hibernate", "Backend");
        em.persist(course);
        Topic newTopic = new Topic("Tópico para Salvar", "Mensagem para salvar", author, course);

        // Act
        Topic savedTopic = topicRepository.save(newTopic);

        // Assert
        assertThat(savedTopic).isNotNull();
        assertThat(savedTopic.getId()).isNotNull();
        assertThat(savedTopic.getTitle()).isEqualTo("Tópico para Salvar");
        assertThat(savedTopic.getAuthor().getUsername()).isEqualTo("author.save");
        assertThat(savedTopic.getCourse().getNome()).isEqualTo("Hibernate");
    }

    @Test
    @DisplayName("Deve deletar um tópico por ID")
    void deleteTopicByIdTest() {
        // Arrange
        User author = new User(null, "author.delete", "123456");
        em.persist(author);
        Course course = new Course(null, "Mockito", "Testes");
        em.persist(course);
        Topic topic = new Topic("Tópico para Deletar", "Mensagem a ser deletada", author, course);
        Topic persistedTopic = em.persistFlushFind(topic);
        Long topicId = persistedTopic.getId();

        // Act
        topicRepository.deleteById(topicId);

        // Assert
        Topic deletedTopic = em.find(Topic.class, topicId);
        assertThat(deletedTopic).isNull();
    }
}

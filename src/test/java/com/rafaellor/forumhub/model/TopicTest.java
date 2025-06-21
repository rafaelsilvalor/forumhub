package com.rafaellor.forumhub.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TopicTest {

    private User author;
    private Curso course;

    @BeforeEach
    void setUp() {
        // Criar objetos mock para serem usados nos testes
        author = new User(1L, "testuser", "password");
        course = new Curso(1L, "Test Course", "Testing");
    }

    @Test
    @DisplayName("Deve criar um tópico com status padrão 'true' e data de criação atual")
    void constructorTest() {
        String title = "Test Title";
        String message = "Test Message";

        // Usar o construtor atualizado com objetos User и Curso
        Topic topic = new Topic(title, message, author, course);

        // O ID será nulo até ser persistido, então não testamos aqui.
        assertNull(topic.getId());
        assertEquals(title, topic.getTitle());
        assertEquals(message, topic.getMessage());
        assertEquals(author, topic.getAuthor(), "O autor deve ser o objeto User fornecido");
        assertEquals(course, topic.getCurso(), "O curso deve ser o objeto Curso fornecido");
        assertTrue(topic.getStatus(), "O status de um novo tópico deve ser 'true' (ativo)");
        assertNotNull(topic.getCreationDate(), "A data de criação não deve ser nula");
        assertTrue(topic.getCreationDate().isBefore(LocalDateTime.now().plusSeconds(1)), "A data de criação deve ser a atual");
    }

    @Test
    @DisplayName("Deve atualizar o título, a mensagem e o curso do tópico corretamente")
    void updateTopicTest() {
        Topic topic = new Topic("Old Title", "Old Message", author, course);

        String newTitle = "New Title";
        String newMessage = "New Message";
        Curso newCourse = new Curso(2L, "New Course", "New Category");

        // Chamar o método de atualização com o novo objeto Curso
        topic.update(newTitle, newMessage, newCourse);

        assertEquals(newTitle, topic.getTitle());
        assertEquals(newMessage, topic.getMessage());
        assertEquals(newCourse, topic.getCurso(), "O curso deve ser atualizado para o novo objeto Curso");
    }

    @Test
    @DisplayName("Não deve atualizar campos do tópico se os novos valores forem nulos ou em branco")
    void updateTopicWithNullOrBlankTest() {
        String originalTitle = "Original Title";
        String originalMessage = "Original Message";

        Topic topic = new Topic(originalTitle, originalMessage, author, course);

        Curso originalCourse = topic.getCurso(); // Guardar o curso original

        // Tentar atualizar com valores nulos e em branco.
        // O último parâmetro (curso) é nulo.
        topic.update(null, " ", null);

        assertEquals(originalTitle, topic.getTitle(), "O título não deve mudar se o novo valor for nulo");
        assertEquals(originalMessage, topic.getMessage(), "A mensagem não deve mudar se a nova for em branco");
        assertEquals(originalCourse, topic.getCurso(), "O curso não deve mudar se o novo valor for nulo");
    }

    @Test
    @DisplayName("Deve definir o status do tópico como 'false' ao ser fechado")
    void closeTopicTest() {
        Topic topic = new Topic("Title", "Message", author, course);

        // Ação
        topic.close();

        // Verificação
        assertFalse(topic.getStatus(), "O status do tópico deve ser 'false' após ser fechado");
    }
}

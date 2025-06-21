-- Criar a tabela de cursos
CREATE TABLE cursos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL UNIQUE,
    categoria VARCHAR(255)
);

-- Adicionar a coluna de chave estrangeira na tabela de tópicos
ALTER TABLE topics ADD COLUMN curso_id BIGINT;

-- Adicionar a restrição de chave estrangeira
ALTER TABLE topics
ADD CONSTRAINT fk_topics_curso_id
FOREIGN KEY (curso_id) REFERENCES cursos(id);

-- (Opcional) Popular com alguns cursos para teste
INSERT INTO cursos(nome, categoria) VALUES('Spring Boot 3', 'Programação');
INSERT INTO cursos(nome, categoria) VALUES('Java Básico', 'Programação');

-- Remover a coluna antiga 'course' que era apenas texto
-- ATENÇÃO: Se você já tiver tópicos criados, precisaria antes migrar os dados
-- da coluna antiga para a nova antes de executar este comando.
ALTER TABLE topics DROP COLUMN course;
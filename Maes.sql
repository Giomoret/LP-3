-- Criação do banco de dados
CREATE DATABASE IF NOT EXISTS maesdb;
USE maesdb;
show Tables;
-- =========================================================
-- TABELA DE MÃES
-- =========================================================
CREATE TABLE mae (
    id_mae INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    telefone VARCHAR(20),
    endereco VARCHAR(150),
    data_aniversario DATE
);

-- =========================================================
-- TABELA DE ENCONTROS
-- =========================================================
CREATE TABLE encontro (
    id_encontro INT AUTO_INCREMENT PRIMARY KEY,
    data_encontro DATE NOT NULL,
    cancelado BOOLEAN DEFAULT FALSE
);

-- =========================================================
-- TABELA DE SERVIÇOS
-- Cada serviço pertence a um encontro e tem uma mãe responsável
-- =========================================================
CREATE TABLE servico (
    id_servico INT AUTO_INCREMENT PRIMARY KEY,
    nome_servico VARCHAR(100) NOT NULL,
    descricao VARCHAR(255),
    id_mae INT,
    id_encontro INT,
    FOREIGN KEY (id_mae) REFERENCES mae(id_mae) ON DELETE SET NULL,
    FOREIGN KEY (id_encontro) REFERENCES encontro(id_encontro) ON DELETE CASCADE
);

-- =========================================================
-- INSERÇÃO INICIAL: serviços fixos pré-definidos
-- =========================================================
INSERT INTO servico (nome_servico, descricao, id_mae, id_encontro)
VALUES 
('MÚSICA', NULL, NULL, NULL),
('RECEPÇÃO DE MÃES', NULL, NULL, NULL),
('ACOLHIDA', NULL, NULL, NULL),
('TERÇO', NULL, NULL, NULL),
('FORMAÇÃO', NULL, NULL, NULL),
('MOMENTO ORACIONAL', NULL, NULL, NULL),
('PROCLAMAÇÃO DA VITÓRIA', NULL, NULL, NULL),
('SORTEIO DAS FLORES', NULL, NULL, NULL),
('ENCERRAMENTO', NULL, NULL, NULL),
('ARRUMAÇÃO CAPELA', NULL, NULL, NULL),
('QUEIMA DOS PEDIDOS', NULL, NULL, NULL),
('COMPRA DAS FLORES', NULL, NULL, NULL);

-- =========================================================
-- VIEW opcional para listar aniversariantes do mês atual
-- =========================================================
CREATE OR REPLACE VIEW aniversariantes_mes AS
SELECT id_mae, nome, telefone, endereco, data_aniversario
FROM mae
WHERE MONTH(data_aniversario) = MONTH(CURDATE());

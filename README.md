
# Sistema de Cadastro dos Encontros das **Mães Que Oram pelos Filhos**

📘 **Trabalho Acadêmico — 3ª Avaliação de LP**
👨‍💻 **Turma:** 2º ADS
👩‍🏫 **Professora:** Juliana Pasquini
📅 **Data:** 03/11/2025

👤 **Autores:**

* Giovanni Moretto
* Gustavo Bueno

---

## 🧩 Descrição do Projeto

Este sistema **desktop**, desenvolvido em **Java**, tem como objetivo auxiliar na **organização dos encontros das *Mães Que Oram pelos Filhos*** de uma igreja.

A aplicação permite o **cadastro de mães participantes**, o **gerenciamento dos encontros e dos serviços** realizados, além da **geração automática de relatórios** em formato `.txt`.

---

## ⚙️ Funcionalidades Principais

### Cadastro de Mães

* Nome
* Telefone
* Endereço
* Data de aniversário

---

### Cadastro de Encontros

* Data do encontro
* Serviços associados: nome da mãe responsável e descrição da atividade

---

### Serviços Fixos do Encontro

* MÚSICA
* RECEPÇÃO DE MÃES
* ACOLHIDA
* TERÇO
* FORMAÇÃO
* MOMENTO ORACIONAL
* PROCLAMAÇÃO DA VITÓRIA
* SORTEIO DAS FLORES
* ENCERRAMENTO
* ARRUMAÇÃO DA CAPELA
* QUEIMA DOS PEDIDOS
* COMPRAS DAS FLORES

---

### Edição e Exclusão de Encontros

* **Permitido apenas para encontros futuros**
* **Exclusão lógica** para encontros passados (marcados como *cancelado* ou *não realizado*)

---

### Lista de Aniversariantes do Mês

* Exibe automaticamente todas as mães que fazem aniversário no mês atual.

---

### Geração de Relatório (.txt)

* Gera um resumo de um encontro específico com os responsáveis por cada serviço.

**Exemplo de saída:**

```
Data do Encontro: 04/11
Serviços:
MÚSICA: Fernanda
RECEPÇÃO DE MÃES: Joana
ACOLHIDA: Laura
TERÇO: Maria
FORMAÇÃO: Regina
MOMENTO ORACIONAL: Fernanda
PROCLAMAÇÃO DA VITÓRIA: Julia
SORTEIO DAS FLORES: Adriana
ENCERRAMENTO: Laura
ARRUMAÇÃO CAPELA: Maria Cláudia
QUEIMA DOS PEDIDOS: Maria Fernanda
COMPRA DAS FLORES: Adriana
```

---

## 🧠 Requisitos Técnicos

| Requisito                  | Descrição                                                            |
| -------------------------- | -------------------------------------------------------------------- |
| **Linguagem**              | Java                                                                 |
| **Arquitetura de Pacotes** | `factory`, `modelo`, `dao`, `gui`                                    |
| **Interface**              | Aplicação Desktop (Swing)                                            |
| **Banco de Dados**         | MySQL                                                                |
| **Paradigma**              | Programação Orientada a Objetos (POO)                                |
| **Persistência**           | Todas as informações são armazenadas e consultadas no banco de dados |

---

## 🧾 Diagramas Obrigatórios

1. **Diagrama de Classes UML**
2. **Diagrama Conceitual do Banco de Dados**
3. **Diagrama Lógico do Banco de Dados**

---

## 📦 Entregas Esperadas

* Código-fonte completo
* Script SQL (criação e dados iniciais)
* Arquivo executável (`.jar`, `.exe` ou `.app`)
* Diagramas UML e de banco de dados
* Relatório `.txt` gerado pelo sistema

---

## 🚀 Instalação e Execução

### 🔧 Configuração do Projeto (Java + MySQL)

1. **Clonar o repositório:**

   ```bash
   git clone https://github.com/Giomoret/LP-3
   ```

2. **Importar o projeto no IntelliJ.**

3. **Configurar o banco de dados MySQL:**

   * Criar um banco com o nome `maes_encontros`
   * Executar o script SQL fornecido no repositório

4. **Executar o sistema:**

   * Rodar a classe `TelaPrincipal.java`

---

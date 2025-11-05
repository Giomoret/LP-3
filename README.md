Sistema de Cadastro dos Encontros das Mães Que Oram pelos Filhos

Nome: Giovanni Moretto
Nome: Gustavo Bueno
Turma: 2ADS 
Professor: Juliana Pasquini
Data: 03/11/2025
3ª Avaliação de LP

---

Descrição do Projeto:
Este sistema desktop, desenvolvido em java, auxilia na organização dos encontros das Mães Que Oram pelos Filhos de uma igreja. Permite cadastro de mães participantes, gerenciamento de encontros e serviços, e geração de relatórios.

Funcionalidades:
1. Cadastro de Mães
   - Nome
   - Telefone
   - Endereço
   - Data de aniversário

2. Cadastro de Encontros
   - Data do encontro
   - Serviços do encontro: nome da mãe responsável e descrição da atividade

3. Serviços Fixos do Encontro:
   - MÚSICA
   - RECEPÇÃO DE MÃES
   - ACOLHIDA
   - TERÇO
   - FORMAÇÃO
   - MOMENTO ORACIONAL
   - PROCLAMAÇÃO DA VITÓRIA
   - SORTEIO DAS FLORES
   - ENCERRAMENTO
   - ARRUMAÇÃO CAPELA
   - QUEIMA DOS PEDIDOS
   - COMPRAS FLORES

4. Edição e Exclusão de Encontros
   - Permitido apenas para encontros futuros
   - Exclusão lógica para encontros passados (marcados como "não realizado" ou "cancelado")

5. Lista de Aniversariantes do Mês
   - Exibe mães aniversariantes do mês atual

6. Geração de Relatório (.txt)
   - Resumo de um encontro específico
   - Exemplo de formato:

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

Requisitos Técnicos:
- Linguagem: Java
- Estrutura de pacotes: factory, modelo, dao, gui
- Interface: Desktop (Electron ou similar)
- Banco de dados: MySQL (com conexão via TypeORM ou mysql2)
- Paradigma: Programação Orientada a Objetos (POO)
- Todas as informações armazenadas e consultadas no banco de dados

Diagramas Obrigatórios:
1. Diagrama de Classes UML *
2. Diagrama Conceitual do Banco de Dados*
3. Diagrama Lógico do Banco de Dados*

Entregas Esperadas:
- Código-fonte completo
- Script SQL para criação e inserção de dados iniciais
- Arquivo executável (.exe, .app ou .jar)
- Diagramas UML e de banco de dados*
- Relatório .txt gerado pelo sistema

Instalação e Uso (TypeScript/Electron):
1. Clonar repositório: git clone <link>
2. Instalar dependências: npm install
3. Configurar banco MySQL no arquivo config/db.ts
4. Executar projeto: npm start
5. Utilizar interface desktop para acessar funcionalidades

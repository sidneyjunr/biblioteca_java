<p align="center">
  <img src="biblioteca.png" width="200"/>
</p>

<h1 align="center">
 Sistema de Gerenciamento de Biblioteca
</h1>

Este é um sistema simples de gerenciamento de biblioteca desenvolvido em **Java**, com conexão a um banco de dados **MySQL**, utilizando o padrão de projeto DAO (Data Access Object).

## ✨ Funcionalidades

- ✅ Cadastro de livros com título, autor, ano e quantidade em estoque
- ✅ Cadastro de alunos com nome e data de nascimento
- ✅ Registro de empréstimos com controle de estoque e data de devolução prevista
- ✅ Registro de devoluções
- ✅ Relatórios e consultas de dados

## 📂 Estrutura do Projeto

```
biblioteca/
├── src/
│   ├── dao/
│   │   ├── AlunoDAO.java
│   │   ├── EmprestimoDAO.java
│   │   └── LivroDAO.java
│   ├── model/
│   │   ├── Aluno.java
│   │   ├── Emprestimo.java
│   │   └── Livro.java
│   ├── util/
│   │   └── Conexao.java
│   └── Main.java
```

## 🧠 Tecnologias Utilizadas

- Java 11+
- JDBC (Java Database Connectivity)
- MySQL
- IntelliJ IDEA (ou qualquer outra IDE Java)
- Git

## ⚙️ Requisitos

- Java JDK instalado (recomendado: versão 11 ou superior)
- MySQL Server em execução
- Driver JDBC MySQL adicionado ao projeto (geralmente via `.idea/libraries`)
- Conexão com o banco de dados corretamente configurada no arquivo `Conexao.java`

## 🛠️ Configuração do Banco de Dados

Execute os comandos SQL abaixo no seu MySQL para criar as tabelas necessárias:

```sql
CREATE DATABASE biblioteca;

USE biblioteca;

CREATE TABLE Alunos (
    id_aluno INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100),
    data_nascimento DATE
);

CREATE TABLE Livros (
    id_livro INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(100),
    autor VARCHAR(100),
    ano_publicacao INT,
    quantidade_estoque INT
);

CREATE TABLE Emprestimos (
    id_emprestimo INT AUTO_INCREMENT PRIMARY KEY,
    id_aluno INT,
    id_livro INT,
    data_emprestimo DATE,
    data_devolucao DATE,
    FOREIGN KEY (id_aluno) REFERENCES Alunos(id_aluno),
    FOREIGN KEY (id_livro) REFERENCES Livros(id_livro)
);
```

> ⚠️ **Importante:** configure corretamente sua conexão no arquivo `Conexao.java`:

```java
String url = "jdbc:mysql://localhost:3306/biblioteca";
String usuario = "seu_usuario";
String senha = "sua_senha";
```

## ▶️ Como Executar

1. Clone este repositório ou extraia o `.zip` com o código-fonte.
2. Abra o projeto em uma IDE Java, como o IntelliJ IDEA.
3. Certifique-se de que o MySQL está rodando e que o banco já foi criado.
4. Edite o arquivo `Conexao.java` com suas credenciais de acesso ao banco de dados.
5. Compile e execute a classe `Main.java`.
6. Utilize o menu que aparecerá no terminal para interagir com o sistema.

## 🔍 Pontos a Melhorar

- [ ] Validar melhor as entradas do usuário
- [ ] Adicionar testes automatizados
- [ ] Criar uma interface gráfica (Swing, JavaFX) ou versão web (Spring Boot)

## 👨‍💻 Autor

Projeto desenvolvido por **Sidney Junior** para fins acadêmicos🥹.

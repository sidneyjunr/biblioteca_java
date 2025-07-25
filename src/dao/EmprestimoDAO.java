package dao;

import util.Conexao;
import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class EmprestimoDAO {

    public boolean registrarEmprestimo(int idAluno, int idLivro) {
        String verificarEstoque = "SELECT quantidade_estoque FROM Livros WHERE id_livro = ?";
        String atualizarEstoque = "UPDATE Livros SET quantidade_estoque = quantidade_estoque - 1 WHERE id_livro = ?";
        String inserirEmprestimo = "INSERT INTO Emprestimos (id_aluno, id_livro, data_emprestimo, data_devolucao) VALUES (?, ?, ?, ?)";

        try (Connection conn = Conexao.conectar()) {
            try (PreparedStatement stmtEstoque = conn.prepareStatement(verificarEstoque)) {
                stmtEstoque.setInt(1, idLivro);
                ResultSet rs = stmtEstoque.executeQuery();

                if (rs.next()) {
                    int estoque = rs.getInt("quantidade_estoque");
                    if (estoque <= 0) {
                        System.out.println("❌ Livro sem estoque disponível.");
                        return false;
                    }
                } else {
                    System.out.println("❌ Livro não encontrado.");
                    return false;
                }
            }

            try (PreparedStatement stmtAtualiza = conn.prepareStatement(atualizarEstoque)) {
                stmtAtualiza.setInt(1, idLivro);
                stmtAtualiza.executeUpdate();
            }

            try (PreparedStatement stmtInsert = conn.prepareStatement(inserirEmprestimo)) {
                stmtInsert.setInt(1, idAluno);
                stmtInsert.setInt(2, idLivro);
                stmtInsert.setDate(3, Date.valueOf(LocalDate.now()));
                stmtInsert.setDate(4, Date.valueOf(LocalDate.now().plusDays(7)));
                stmtInsert.executeUpdate();
            }

            System.out.println("✅ Empréstimo registrado com sucesso!");
            return true;

        } catch (SQLException e) {
            System.out.println("❌ Erro ao registrar empréstimo: " + e.getMessage());
            return false;
        }
    }

    public boolean registrarDevolucao(int idEmprestimo) {
        String buscarEmprestimo = "SELECT id_livro, data_devolucao FROM Emprestimos WHERE id_emprestimo = ?";
        String atualizarEmprestimo = "UPDATE Emprestimos SET data_devolucao = ? WHERE id_emprestimo = ?";
        String atualizarEstoque = "UPDATE Livros SET quantidade_estoque = quantidade_estoque + 1 WHERE id_livro = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmtBusca = conn.prepareStatement(buscarEmprestimo)) {

            stmtBusca.setInt(1, idEmprestimo);
            ResultSet rs = stmtBusca.executeQuery();

            if (rs.next()) {
                Date dataDevolucao = rs.getDate("data_devolucao");
                int idLivro = rs.getInt("id_livro");

                if (dataDevolucao != null && dataDevolucao.toLocalDate().isBefore(LocalDate.now().plusDays(1))) {
                    System.out.println("⚠️ Esse empréstimo já foi devolvido.");
                    return false;
                }


                try (PreparedStatement stmtAtualiza = conn.prepareStatement(atualizarEmprestimo)) {
                    stmtAtualiza.setDate(1, Date.valueOf(LocalDate.now()));
                    stmtAtualiza.setInt(2, idEmprestimo);
                    stmtAtualiza.executeUpdate();
                }


                try (PreparedStatement stmtEstoque = conn.prepareStatement(atualizarEstoque)) {
                    stmtEstoque.setInt(1, idLivro);
                    stmtEstoque.executeUpdate();
                }

                System.out.println("✅ Devolução registrada com sucesso!");
                return true;

            } else {
                System.out.println("❌ Empréstimo não encontrado.");
                return false;
            }

        } catch (SQLException e) {
            System.out.println("❌ Erro ao registrar devolução: " + e.getMessage());
            return false;
        }
    }

    public void listarEmprestimosAtivos() {
        String sql = """
            SELECT e.id_emprestimo, a.nome_aluno, l.titulo, e.data_emprestimo, e.data_devolucao
            FROM Emprestimos e
            JOIN Alunos a ON e.id_aluno = a.id_aluno
            JOIN Livros l ON e.id_livro = l.id_livro
            WHERE e.data_devolucao >= CURRENT_DATE
            ORDER BY e.data_devolucao
        """;

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n📋 Empréstimos Ativos:");
            while (rs.next()) {
                System.out.println("- Empréstimo ID: " + rs.getInt("id_emprestimo"));
                System.out.println("  Aluno: " + rs.getString("nome_aluno"));
                System.out.println("  Livro: " + rs.getString("titulo"));
                System.out.println("  Empréstimo: " + rs.getDate("data_emprestimo"));
                System.out.println("  Devolução prevista: " + rs.getDate("data_devolucao"));
                System.out.println("---------------------------");
            }

        } catch (SQLException e) {
            System.out.println("❌ Erro ao listar empréstimos: " + e.getMessage());
        }
    }

    public void listarAlunosComEmprestimos() {
        String sql = """
            SELECT DISTINCT a.nome_aluno, a.matricula
            FROM Emprestimos e
            JOIN Alunos a ON e.id_aluno = a.id_aluno
            WHERE e.data_devolucao >= CURRENT_DATE
        """;

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n👨‍🎓 Alunos com Empréstimos Ativos:");
            while (rs.next()) {
                System.out.println("- " + rs.getString("nome_aluno") + " (Matrícula: " + rs.getString("matricula") + ")");
            }

        } catch (SQLException e) {
            System.out.println("❌ Erro ao listar alunos: " + e.getMessage());
        }
    }
    public void registrarDevolucaoPorMatricula(String matricula) {
        dao.AlunoDAO alunoDAO = new dao.AlunoDAO();
        model.Aluno aluno = alunoDAO.buscarPorMatricula(matricula);

        if (aluno == null) {
            System.out.println("❌ Matrícula não encontrada.");
            return;
        }

        String sql = """
        SELECT e.id_emprestimo, l.titulo, e.data_emprestimo, e.data_devolucao
        FROM Emprestimos e
        JOIN Livros l ON e.id_livro = l.id_livro
        WHERE e.id_aluno = ? AND e.data_devolucao >= CURRENT_DATE
    """;

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, aluno.getId());
            ResultSet rs = stmt.executeQuery();

            int count = 0;
            int[] idsEmprestimos = new int[10];
            Scanner sc = new Scanner(System.in);

            System.out.println("\n📚 Empréstimos ativos do aluno:");
            while (rs.next()) {
                idsEmprestimos[count] = rs.getInt("id_emprestimo");
                System.out.println((count + 1) + ". " + rs.getString("titulo") +
                        " | Empréstimo: " + rs.getDate("data_emprestimo"));
                count++;
            }

            if (count == 0) {
                System.out.println("⚠️ Nenhum empréstimo ativo encontrado.");
                return;
            }

            System.out.print("Digite o número do empréstimo para registrar devolução: ");
            int escolha = sc.nextInt();
            sc.nextLine();

            if (escolha < 1 || escolha > count) {
                System.out.println("❌ Opção inválida.");
                return;
            }

            int idEmprestimoSelecionado = idsEmprestimos[escolha - 1];
            registrarDevolucao(idEmprestimoSelecionado);

        } catch (SQLException e) {
            System.out.println("❌ Erro ao buscar empréstimos: " + e.getMessage());
        }
    }



}


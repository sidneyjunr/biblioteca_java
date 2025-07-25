package dao;

import model.Aluno;
import util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AlunoDAO {

    public void cadastrar(Aluno aluno) {
        String sql = "INSERT INTO Alunos (nome_aluno, matricula, data_nascimento) VALUES (?, ?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, aluno.getNome());
            stmt.setString(2, aluno.getMatricula());
            stmt.setDate(3, java.sql.Date.valueOf(aluno.getDataNascimento()));

            stmt.executeUpdate();
            System.out.println("✅ Aluno cadastrado com sucesso!");

        } catch (SQLException e) {
            System.out.println("❌ Erro ao cadastrar aluno: " + e.getMessage());
        }
    }

    public Aluno buscarPorMatricula(String matricula) {
        String sql = "SELECT * FROM Alunos WHERE matricula = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, matricula);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Aluno aluno = new Aluno();
                aluno.setId(rs.getInt("id_aluno"));
                aluno.setNome(rs.getString("nome_aluno"));
                aluno.setMatricula(rs.getString("matricula"));
                aluno.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
                return aluno;
            }

        } catch (SQLException e) {
            System.out.println("❌ Erro ao buscar aluno: " + e.getMessage());
        }

        return null;
    }

    public void buscarPorNome(String nome) {
        String sql = "SELECT * FROM Alunos WHERE nome_aluno LIKE ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + nome + "%");
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n🔍 Alunos encontrados:");
            boolean encontrou = false;

            while (rs.next()) {
                encontrou = true;
                System.out.println("ID: " + rs.getInt("id_aluno"));
                System.out.println("Nome: " + rs.getString("nome_aluno"));
                System.out.println("Matrícula: " + rs.getString("matricula"));
                System.out.println("Data de nascimento: " + rs.getDate("data_nascimento"));
                System.out.println("------------------------");
            }

            if (!encontrou) {
                System.out.println("Nenhum aluno encontrado com esse nome.");
            }

        } catch (SQLException e) {
            System.out.println("❌ Erro ao buscar aluno: " + e.getMessage());
        }
    }

    public void excluirPorMatricula(String matricula) {
        String verificarSql = "SELECT COUNT(*) AS total FROM Emprestimos e JOIN Alunos a ON e.id_aluno = a.id_aluno WHERE a.matricula = ?";
        String excluirSql = "DELETE FROM Alunos WHERE matricula = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement verificarStmt = conn.prepareStatement(verificarSql)) {

            verificarStmt.setString(1, matricula);
            ResultSet rs = verificarStmt.executeQuery();

            if (rs.next() && rs.getInt("total") > 0) {

                Aluno aluno = buscarPorMatricula(matricula);
                String nome = (aluno != null) ? aluno.getNome() : "Aluno";

                System.out.println("❌ " + nome + " NÃO PODE SER EXCLUÍDO PORQUE TEM EMPRÉSTIMO ATIVO.");
                return;
            }


            try (PreparedStatement excluirStmt = conn.prepareStatement(excluirSql)) {
                excluirStmt.setString(1, matricula);
                int linhasAfetadas = excluirStmt.executeUpdate();

                if (linhasAfetadas > 0) {
                    System.out.println("✅ Aluno excluído com sucesso.");
                } else {
                    System.out.println("⚠️ Matrícula não encontrada.");
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Erro ao excluir aluno: " + e.getMessage());
        }
    }

}

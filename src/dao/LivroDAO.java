package dao;

import model.Livro;
import util.Conexao;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LivroDAO {

    public void cadastrar(Livro livro) {
        String sql = "INSERT INTO Livros (titulo, autor, ano_publicacao, quantidade_estoque) VALUES (?, ?, ?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, livro.getTitulo());
            stmt.setString(2, livro.getAutor());
            stmt.setInt(3, livro.getAnoPublicacao());
            stmt.setInt(4, livro.getQuantidadeEstoque());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int idGerado = rs.getInt(1);
                System.out.println("✅ Livro cadastrado com sucesso! ID: " + idGerado);
            }

        } catch (SQLException e) {
            System.out.println("❌ Erro ao cadastrar livro: " + e.getMessage());
        }
    }
    public void buscarPorTitulo(String titulo) {
        String sql = "SELECT * FROM Livros WHERE titulo LIKE ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + titulo + "%");
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n🔍 Livros encontrados:");
            boolean encontrou = false;

            while (rs.next()) {
                encontrou = true;
                System.out.println("ID: " + rs.getInt("id_livro"));
                System.out.println("Título: " + rs.getString("titulo"));
                System.out.println("Autor: " + rs.getString("autor"));
                System.out.println("Ano: " + rs.getInt("ano_publicacao"));
                System.out.println("Estoque: " + rs.getInt("quantidade_estoque"));
                System.out.println("------------------------");
            }

            if (!encontrou) {
                System.out.println("Nenhum livro encontrado com esse título.");
            }

        } catch (SQLException e) {
            System.out.println("❌ Erro ao buscar livro: " + e.getMessage());
        }
    }
    public void excluirPorId(int idLivro) {
        String verificarSql = "SELECT COUNT(*) AS total FROM Emprestimos WHERE id_livro = ?";
        String excluirSql = "DELETE FROM Livros WHERE id_livro = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement verificarStmt = conn.prepareStatement(verificarSql)) {

            verificarStmt.setInt(1, idLivro);
            ResultSet rs = verificarStmt.executeQuery();

            if (rs.next() && rs.getInt("total") > 0) {

                String titulo = buscarTituloPorId(idLivro);
                System.out.println("❌ O livro \"" + titulo + "\" NÃO PODE SER EXCLUÍDO PORQUE ESTÁ COM EMPRÉSTIMO ATIVO.");
                return;
            }

            try (PreparedStatement excluirStmt = conn.prepareStatement(excluirSql)) {
                excluirStmt.setInt(1, idLivro);
                int linhasAfetadas = excluirStmt.executeUpdate();

                if (linhasAfetadas > 0) {
                    System.out.println("✅ Livro excluído com sucesso.");
                } else {
                    System.out.println("⚠️ ID não encontrado.");
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Erro ao excluir livro: " + e.getMessage());
        }
    }

    private String buscarTituloPorId(int idLivro) {
        String sql = "SELECT titulo FROM Livros WHERE id_livro = ?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idLivro);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("titulo");
            }
        } catch (SQLException e) {
            System.out.println("❌ Erro ao buscar título do livro: " + e.getMessage());
        }
        return "Livro";
    }


}

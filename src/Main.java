import dao.LivroDAO;
import dao.AlunoDAO;
import dao.EmprestimoDAO;
import model.Livro;
import model.Aluno;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        LivroDAO livroDAO = new LivroDAO();
        AlunoDAO alunoDAO = new AlunoDAO();
        EmprestimoDAO emprestimoDAO = new EmprestimoDAO();

        int opcao;

        do {
            System.out.println("\n📚 MENU BIBLIOTECA");
            System.out.println("1 - Cadastrar livro");
            System.out.println("2 - Cadastrar aluno");
            System.out.println("3 - Realizar empréstimo");
            System.out.println("4 - Registrar devolução");
            System.out.println("5 - Relatórios e Consultas");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");
            opcao = sc.nextInt();
            sc.nextLine();

            switch (opcao) {
                case 1:
                    System.out.print("Título: ");
                    String titulo = sc.nextLine();

                    System.out.print("Autor: ");
                    String autor = sc.nextLine();

                    System.out.print("Ano de publicação: ");
                    int ano = sc.nextInt();

                    System.out.print("Quantidade em estoque: ");
                    int estoque = sc.nextInt();
                    sc.nextLine();

                    Livro livro = new Livro(titulo, autor, ano, estoque);

                    System.out.println("\n📋 Confirme os dados do livro:");
                    System.out.println("Título: " + livro.getTitulo());
                    System.out.println("Autor: " + livro.getAutor());
                    System.out.println("Ano: " + livro.getAnoPublicacao());
                    System.out.println("Estoque: " + livro.getQuantidadeEstoque());

                    if (confirmar(sc, "Deseja confirmar o cadastro?")) {
                        livroDAO.cadastrar(livro);
                    } else {
                        System.out.println("❌ Cadastro cancelado.");
                    }
                    break;

                case 2:
                    System.out.print("Nome do aluno: ");
                    String nomeAluno = sc.nextLine();

                    System.out.print("Matrícula: ");
                    String matricula = sc.nextLine();

                    System.out.print("Data de nascimento (DD/MM/AAAA): ");
                    String data = sc.nextLine();

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    Aluno aluno = new Aluno(nomeAluno, matricula, LocalDate.parse(data, formatter));

                    System.out.println("\n📋 Confirme os dados do aluno:");
                    System.out.println("Nome: " + aluno.getNome());
                    System.out.println("Matrícula: " + aluno.getMatricula());
                    System.out.println("Data de Nascimento: " + aluno.getDataNascimento());

                    if (confirmar(sc, "Deseja confirmar o cadastro?")) {
                        alunoDAO.cadastrar(aluno);
                    } else {
                        System.out.println("❌ Cadastro cancelado.");
                    }
                    break;

                case 3:
                    System.out.print("Matrícula do aluno: ");
                    String matriculaBusca = sc.nextLine();
                    Aluno alunoBuscado = alunoDAO.buscarPorMatricula(matriculaBusca);

                    if (alunoBuscado == null) break;

                    System.out.print("ID do livro: ");
                    int idLivroEmprestimo = sc.nextInt();
                    sc.nextLine();

                    System.out.println("\n📋 Confirme os dados do empréstimo:");
                    System.out.println("Aluno: " + alunoBuscado.getNome() + " (Matrícula: " + alunoBuscado.getMatricula() + ")");
                    System.out.println("ID do livro: " + idLivroEmprestimo);

                    if (confirmar(sc, "Deseja confirmar o empréstimo?")) {
                        emprestimoDAO.registrarEmprestimo(alunoBuscado.getId(), idLivroEmprestimo);
                    } else {
                        System.out.println("❌ Empréstimo cancelado.");
                    }
                    break;

                case 4:
                    System.out.print("Matrícula do aluno: ");
                    String matriculaDev = sc.nextLine();

                    System.out.println("\n⚠️ Será exibida a lista de empréstimos ativos do aluno.");
                    if (confirmar(sc, "Deseja continuar?")) {
                        emprestimoDAO.registrarDevolucaoPorMatricula(matriculaDev);
                    } else {
                        System.out.println("❌ Ação cancelada.");
                    }
                    break;

                case 5:
                    int opcaoRelatorio;
                    do {
                        System.out.println("\n📊 RELATÓRIOS E CONSULTAS");
                        System.out.println("1 - Ver empréstimos ativos");
                        System.out.println("2 - Ver alunos com empréstimos");
                        System.out.println("3 - Buscar aluno por nome");
                        System.out.println("4 - Buscar livro por título");
                        System.out.println("5 - Excluir aluno por matrícula");
                        System.out.println("6 - Excluir livro por ID");
                        System.out.println("0 - Voltar ao menu principal");
                        System.out.print("Escolha uma opção: ");
                        opcaoRelatorio = sc.nextInt();
                        sc.nextLine();

                        switch (opcaoRelatorio) {
                            case 1:
                                emprestimoDAO.listarEmprestimosAtivos();
                                break;
                            case 2:
                                emprestimoDAO.listarAlunosComEmprestimos();
                                break;
                            case 3:
                                System.out.print("Digite o nome do aluno: ");
                                String nomeBusca = sc.nextLine();
                                alunoDAO.buscarPorNome(nomeBusca);
                                break;
                            case 4:
                                System.out.print("Digite o título do livro: ");
                                String tituloBusca = sc.nextLine();
                                livroDAO.buscarPorTitulo(tituloBusca);
                                break;
                            case 5:
                                System.out.print("Digite a matrícula do aluno para excluir: ");
                                String matriculaExcluir = sc.nextLine();

                                System.out.println("⚠️ Isso excluirá permanentemente o aluno com matrícula " + matriculaExcluir);
                                if (confirmar(sc, "Tem certeza que deseja excluir?")) {
                                    alunoDAO.excluirPorMatricula(matriculaExcluir);
                                } else {
                                    System.out.println("❌ Exclusão cancelada.");
                                }
                                break;
                            case 6:
                                System.out.print("Digite o ID do livro para excluir: ");
                                int idLivroExcluir = sc.nextInt();
                                sc.nextLine();

                                System.out.println("⚠️ Isso excluirá permanentemente o livro com ID " + idLivroExcluir);
                                if (confirmar(sc, "Tem certeza que deseja excluir?")) {
                                    livroDAO.excluirPorId(idLivroExcluir);
                                } else {
                                    System.out.println("❌ Exclusão cancelada.");
                                }
                                break;
                            case 0:
                                System.out.println("Voltando ao menu principal...");
                                break;
                            default:
                                System.out.println("Opção inválida.");
                        }
                    } while (opcaoRelatorio != 0);
                    break;

                case 0:
                    System.out.println("Encerrando...");
                    break;

                default:
                    System.out.println("Opção inválida.");
            }

        } while (opcao != 0);
    }

    public static boolean confirmar(Scanner sc, String mensagem) {
        System.out.print(mensagem + " (s/n): ");
        String resp = sc.nextLine().trim().toLowerCase();
        return resp.equals("s");
    }
}

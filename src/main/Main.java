/**
 * Main - Trabalho Pratico 01 de Algoritmos e Estruturas de Dados III
 * @author Gabriel Vargas Bento de Souza e Nilson Deon Cordeiro Filho
 * @version 1.0 03/2023
 */

// Bibliotecas
import java.util.InputMismatchException;

// Bibliotecas proprias
import app.IO;
import crud.CRUD;
import sort.OrdenacaoExterna;

class Main {

    /**
     * Metodo principal do sistema, capaz de unir todas as classes instansiadas
     * e inicializar um banco de dados capaz de realizar CRUD e Ordenacao
     * Externa.
     */
    public static void main (String args[]) {

        OrdenacaoExterna sort = new OrdenacaoExterna();
        IO io = new IO();
        CRUD crud = new CRUD();

       String intro = "\n        Trabalho Pratico 02 - TP02         " +
                      "\n    Algoritmos e Estruturas de Dados III   " + 
                      "\n       Gabriel Vargas e Nilson Deon        " +
                      "\n     Base de dados: Musicas do Spotify     " +
                      "\n                  04/2023                  ";

       String menu = "\n+------------------------------------------+" +
                     "\n|              MENU PRINCIPAL              |" +
                     "\n|------------------------------------------|" +
                     "\n| 1 - Sair                                 |" +
                     "\n| 2 - Carregar dados iniciais              |" +
                     "\n| 3 - Cadastrar                            |" +
                     "\n| 4 - Pesquisar                            |" +
                     "\n| 5 - Atualizar                            |" +
                     "\n| 6 - Deletar                              |" +
                     "\n| 7 - Ordenar                              |" +
                     "\n| 8 - Salvar registros em txt              |" +
                     "\n| 9 - Abrir musica no Spotify              |" +
                     "\n+------------------------------------------+";
       int opcao = -1;

       System.out.println(intro);

       do {
           try {
               System.out.println(menu);
               opcao = io.readInt("\nDigite uma opcao: ");

               switch (opcao) {
                   case 1 :                        break;
                   case 2 : crud.carregarCSV();    break;
                   case 3 : crud.create();         break;
                   case 4 : crud.read();           break;
                   case 5 : crud.update();         break;
                   case 6 : crud.delete();         break;
                   case 7 : sort.ordenarArquivo(); break;
                   case 8 : crud.saveTXT();        break;
                   case 9 : crud.abrirMusica();    break;
                   default: mostrarErro();         break;
               }
           } catch (InputMismatchException e) {
               mostrarErro();
               io.readLine();
           } finally {
               
               // Fazer um break para ser possivel ler os resultados obtidos
               if(opcao >= 2 && opcao <= 9) {
                   System.out.println("\nPressione ENTER para continuar");
                   io.readLine();
               }

           }
       } while (opcao != 1);

   }

    /**
     * Mostra uma mensagem de erro informando que a opcao digitada nao e'
     * valida.
     */
    public static void mostrarErro() {
        System.out.println("\nERRO: Por favor, digite uma opcao valida de " + 
                           "1 a 8.");
    }
}

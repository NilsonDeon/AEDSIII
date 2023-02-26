/**
 * Main - Trabalho Pratico 01 de Algoritmos e Estruturas de Dados III
 * @author Gabriel Vargas Bento de Souza e Nilson Deon Cordeiro Filho
 * @version 1.0 02/2023
 */

// bibliotecas
import java.util.Scanner;
import java.util.InputMismatchException;

class Main {

    /**
     * Metodo principal do sistema, capaz de unir todas as classes instansiadas
     * e inicializar um banco de dados capaz de realizar CRUD e Ordenacao
     * Externa.
     */
    public static void main (String args[]) throws Exception {

       Scanner sc = new Scanner(System.in);
       CRUD crud = new CRUD();
       OrdenacaoExterna sort = new OrdenacaoExterna();

       String introducao = "\nTrabalho Pratico 01 - TP01" +
                           "\nAlgoritmos e Estruturas de Dados III" + 
                           "\nGabriel Vargas e Nilson Deon" +
                           "\nBase de dados: Musicas do Spotify" +
                           "\n02 / 2023";

       String menu = "\n+------------------------------------------+" +
                     "\n|                   MENU                   |" +
                     "\n|------------------------------------------|" +
                     "\n| 0 - Sair                                 |" +
                     "\n| 1 - Realizar cargas inicial dos dados    |" +
                     "\n| 2 - Cadastrar                            |" +
                     "\n| 3 - Pesquisar                            |" +
                     "\n| 4 - Atualizar                            |" +
                     "\n| 5 - Deletar                              |" +
                     "\n| 6 - Ordenar                              |" +
                     "\n| 7 - Abrir música no Spotify              |" +
                     "\n+------------------------------------------+";
       int opcao = -1;

       System.out.println(introducao);

       do {
           try {
               System.out.println(menu);
               System.out.print("\nDigite uma opcao: ");
               String input = sc.next();
               opcao = Integer.parseInt(input);

               switch (opcao) {
                   case 0 :                      break;
                   case 1 : crud.carregarCSV();  break;
                   case 2 : crud.create();       break;
                   case 3 : crud.read();         break;
                   case 4 : crud.update();       break;
                   case 5 : crud.delete();       break;
                   case 6 : /*sort.orderBy(); */ break;
                   case 7 : crud.abrirMusica(); 
                   default: mostrarErro();       break;
               }
           } catch (InputMismatchException e) {
               mostrarErro();
               sc.nextLine();
           }
       } while (opcao != 0);

       sc.close(); 
   }

    /**
     * Mostra uma mensagem de erro informando que a opcao digitada nao e'
     * valida.
     */
    public static void mostrarErro() {
        System.out.println("\nERRO: Por favor, digite uma opcao valida de " + 
                           "0 a 7.");
    }
}
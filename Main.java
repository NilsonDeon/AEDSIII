/**
 * Main - Trabalho Pratico 01 de Algoritmos e Estruturas de Dados III
 * @author Gabriel Vargas Bento de Souza e Nilson Deon Cordeiro Filho
 * @version 1.0 02/2023
 */

import java.util.InputMismatchException;
// import src.*;
// import src.ordenacoes.*;

class Main {

    /**
     * Metodo principal do sistema, capaz de unir todas as classes instansiadas
     * e inicializar um banco de dados capaz de realizar CRUD e Ordenacao
     * Externa.
     */
    public static void main (String args[]) throws Exception {

       IO io = new IO();
       CRUD crud = new CRUD();

       String intro = "\n        Trabalho Pratico 01 - TP01         " +
                      "\n    Algoritmos e Estruturas de Dados III   " + 
                      "\n       Gabriel Vargas e Nilson Deon        " +
                      "\n     Base de dados: Músicas do Spotify     " +
                      "\n                  02/2023                  ";

       String menu = "\n+------------------------------------------+" +
                     "\n|                   MENU                   |" +
                     "\n|------------------------------------------|" +
                     "\n| 1 - Sair                                 |" +
                     "\n| 2 - Carregar dados iniciais              |" +
                     "\n| 3 - Cadastrar                            |" +
                     "\n| 4 - Pesquisar                            |" +
                     "\n| 5 - Atualizar                            |" +
                     "\n| 6 - Deletar                              |" +
                     "\n| 7 - Ordenar                              |" +
                     "\n| 8 - Abrir música no Spotify              |" +
                     "\n+------------------------------------------+";
       int opcao = -1;

       System.out.println(intro);

       do {
           try {
               System.out.println(menu);
               opcao = io.readInt("\nDigite uma opção: ");

               switch (opcao) {
                   case 1 :                      break;
                   case 2 : crud.carregarCSV();  break;
                   case 3 : crud.create();       break;
                   case 4 : crud.read();         break;
                   case 5 : crud.update();       break;
                   case 6 : crud.delete();       break;
                   case 7 : /*sort.orderBy(); */ break;
                   case 8 : crud.abrirMusica();  break;
                   default: mostrarErro();       break;
               }
           } catch (InputMismatchException e) {
               mostrarErro();
               io.readLine();
           }
       } while (opcao != 1);
   }

    /**
     * Mostra uma mensagem de erro informando que a opcao digitada nao e'
     * valida.
     */
    public static void mostrarErro() {
        System.out.println("\nERRO: Por favor, digite uma opção válida de " + 
                           "1 a 8.");
    }
}
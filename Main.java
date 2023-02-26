

/**
 * Main - Trabalho Pratico 01 de Algoritmos e Estruturas de Dados III
 * @author Gabriel Vargas Bento de Souza e Nilson Deon Cordeiro Filho
 * @version 1.0 02/2023
 */

import java.util.InputMismatchException;

import src.*;
import src.ordenacoes.*;

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
                     "\n| 0 - Sair                                 |" +
                     "\n| 1 - Carregar dados iniciais              |" +
                     "\n| 2 - Cadastrar                            |" +
                     "\n| 3 - Pesquisar                            |" +
                     "\n| 4 - Atualizar                            |" +
                     "\n| 5 - Deletar                              |" +
                     "\n| 6 - Ordenar                              |" +
                     "\n| 7 - Abrir música no Spotify              |" +
                     "\n+------------------------------------------+";
       int opcao = -1;

       System.out.println(intro);

       do {
           try {
               System.out.println(menu);
               opcao = io.readInt("\nDigite uma opcao: ");

               switch (opcao) {
                   case 0 :                      break;
                   case 1 : crud.carregarCSV();  break;
                   case 2 : crud.create();       break;
                   case 3 : crud.read();         break;
                   case 4 : crud.update();       break;
                   case 5 : crud.delete();       break;
                   case 6 : /*sort.orderBy(); */ break;
                   case 7 : crud.abrirMusica();  break;
                   default: mostrarErro();       break;
               }
           } catch (InputMismatchException e) {
               mostrarErro();
               io.readLine();
           }
       } while (opcao != 0);
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
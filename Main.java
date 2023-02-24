/**
 * Main - Trabalho Pratico 01 de Algoritmos e Estruturas de Dados III
 * @author Gabriel Vargas Bento de Souza e Nilson Deon Cordeiro Filho
 * @version 1.0 02/2023
 */

// bibliotecas
import java.util.Scanner;
import java.io.IOException;

class Main {

    public static void main (String args[]) throws Exception {

       Scanner sc = new Scanner(System.in);
       CRUD crud = new CRUD();

       String menu = "\n0 - Sair" +
                     "\n1 - Realizar cargas inicial dos dados" +
                     "\n2 - Ler" +
                     "\n3 - Atualizar" +
                     "\n4 - Deletar";
       int opcao = -1;

       do {
           try {
               System.out.println(menu);
               System.out.print("\nDigite uma opção: ");
               opcao = sc.nextInt();

               switch (opcao) {
                   case 0 :                           break;
                   case 1 : crud.carregarCSV();       break;
                   case 2 : /*lerRegistro();       */ break;
                   case 3 : /*atualizarRegistro(); */ break;
                   case 4 : /*deletarRegistro();   */ break;
                   default: crud.mostrarErro();       break;
               }
           } catch (IOException e) {
               crud.mostrarErro();
               sc.nextLine();
           }
       } while (opcao != 0);

       sc.close();
       
   }

}
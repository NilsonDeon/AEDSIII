package sort;


// bibliotecas
import java.util.InputMismatchException;

import app.IO;

public class OrdenacaoExterna {

    public void ordenarArquivo () throws Exception {

        IO io = new IO();

        ComumSort sort1 = new ComumSort();
        TamanhoVariavelSort sort2 = new TamanhoVariavelSort();
        SelecaoPorSubstituicaoSort sort3 = new SelecaoPorSubstituicaoSort();

        int atributo = -1;
        int opcao = -1;

        String menu1 = "\n+------------------------------------------+" +
                       "\n|    Escolha o atributo para ordenacao:    |" +
                       "\n|------------------------------------------|" +
                       "\n| 1 - Id                                   |" +
                       "\n| 2 - Nome                                 |" +
                       "\n| 3 - Data de lancamento                   |" +
                       "\n+------------------------------------------+";    

       do {
           try {
               System.out.println(menu1);
               atributo = io.readInt("\nDigite o atributo desejado: ");

               if (atributo < 1 || atributo > 3) {
                   System.out.println("\nERRO: Por favor, digite uma opcao valida de 1 a 3.");
                }

           } catch (InputMismatchException e) {
               System.out.println("\nERRO: Por favor, digite uma opcao valida de 1 a 3.");
               io.readLine();
           }
       } while (atributo < 1 || atributo > 3);


        String menu2 = "\n+------------------------------------------+" +
                       "\n|        MENU INTECALACAO BALANCEADA       |" +
                       "\n|------------------------------------------|" +
                       "\n| 1 - Comum                                |" +
                       "\n| 2 - Blocos de Tamanho Variavel           |" +
                       "\n| 3 - Selecao por Substituicao             |" +
                       "\n| 4 - Voltar                               |" +
                       "\n+------------------------------------------+";        

       do {
           try {
               System.out.println(menu2);
               opcao = io.readInt("\nDigite a ordenacao desejada: ");

               switch (opcao) {
                   case 1 : sort1.ordenar(atributo); break;
                   case 2 : sort2.ordenar(atributo); break;
                   case 3 : sort3.ordenar(atributo); break;
                   case 4 : break;
                   default: System.out.println("\nERRO: Por favor, digite uma opcao valida de 1 a 4."); break;
               }
           } catch (InputMismatchException e) {
               System.out.println("\nERRO: Por favor, digite uma opcao valida de 1 a 4.");
               io.readLine();
           }
       } while (opcao < 1 || opcao > 4);

    }
}
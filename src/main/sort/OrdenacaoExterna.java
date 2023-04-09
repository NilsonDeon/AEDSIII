package sort;

// bibliotecas
import java.util.InputMismatchException;

import app.IO;
import hashing.HashingExtensivel;

public class OrdenacaoExterna {

    public void ordenarArquivo () throws Exception {

        IO io = new IO();
        HashingExtensivel hash = new HashingExtensivel();

        int opcao = 0;
        int atributo = 0;
        int numCaminhos = 0;
        int numRegistros = 0;

        String menu1 = "\n+------------------------------------------+" +
                       "\n|    Escolha o atributo para ordenacao:    |" +
                       "\n|------------------------------------------|" +
                       "\n| 1 - Id                                   |" +
                       "\n| 2 - Nome                                 |" +
                       "\n| 3 - Data de lancamento                   |" +
                       "\n+------------------------------------------+";    

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

       try {
            // Caso valor lido for invalido, construtor trata execao
            numCaminhos  = io.readInt("\nDigite o numero de caminhos : ");
            numRegistros = io.readInt("\nDigite o numero de registros: ");

            if (numCaminhos < 2 || numRegistros < 1) {
                System.out.println("\nERRO: valores invalidos!");
                System.out.println("Redefinido para 4 caminhos e 1500 registros!");
            }

        } catch (InputMismatchException e) {
            io.readLine();
        }

       do {
           try {

                System.out.println(menu2);
                opcao = io.readInt("\nDigite a ordenacao desejada: ");

                switch (opcao) {
                   case 1 :
                        ComumSort sort1 = new ComumSort(numRegistros, numCaminhos);
                        sort1.ordenar(atributo);
                        hash.refazerHashing();
                        break;
                   case 2 :
                        TamanhoVariavelSort sort2 = new TamanhoVariavelSort(numRegistros, numCaminhos);
                        sort2.ordenar(atributo);
                        hash.refazerHashing();
                        break;                   
                   case 3 :
                        SelecaoPorSubstituicaoSort sort3 = new SelecaoPorSubstituicaoSort(numRegistros, numCaminhos);
                        sort3.ordenar(atributo);
                        hash.refazerHashing();
                        break;
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
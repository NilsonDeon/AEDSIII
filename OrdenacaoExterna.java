// bibliotecas
import java.util.InputMismatchException;

public class OrdenacaoExterna {

    public void ordenarArquivo () throws Exception {

        IO io = new IO();

        ComumSort sort1 = new ComumSort();
        TamanhoVariavelSort sort2 = new TamanhoVariavelSort();
        SelecaoPorSubstituicaoSort sort3 = new SelecaoPorSubstituicaoSort();

        int opcao = -1;

       String menu = "\n+------------------------------------------+" +
                     "\n|        MENU INTECALACAO BALANCEADA       |" +
                     "\n|------------------------------------------|" +
                     "\n| 1 - Comum                                |" +
                     "\n| 2 - Blocos de Tamanho Variavel           |" +
                     "\n| 3 - Selecao por Substituicao             |" +
                     "\n| 4 - Voltar                               |" +
                     "\n+------------------------------------------+";        

       do {
           try {
               System.out.println(menu);
               opcao = io.readInt("\nDigite a ordenacao desejada: ");

               switch (opcao) {
                   case 1 : sort1.ordenar(); break;
                   case 2 : sort2.ordenar(); break;
                   case 3 : sort3.ordenar(); break;
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
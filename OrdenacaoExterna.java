// bibliotecas
import java.util.InputMismatchException;

public class OrdenacaoExterna {

    public void ordenarArquivo () throws Exception {

        IO io = new IO();

        ComumSort sort1 = new ComumSort();
        TamanhoVariavelSort sort2 = new TamanhoVariavelSort();
        SelecaoPorSubstituicaoSort sort3 = new SelecaoPorSubstituicaoSort();

        int opcao = -1;

        String menu = "\n Intercalação balanceada:" +
                      "\n 1 - Comum" +
                      "\n 2 - Blocos de Tamanho Variável" +
                      "\n 3 - Seleção por substituição";

       do {
           try {
               System.out.println(menu);
               opcao = io.readInt("\nDigite uma ordenação desejada: ");

               switch (opcao) {
                   case 1 : sort1.ordenar(); break;
                   case 2 : sort2.ordenar(); break;
                   case 3 : sort3.ordenar(); break;
                   default: System.out.println("\nERRO: Por favor, digite uma opção valida de 1 a 3."); break;
               }
           } catch (InputMismatchException e) {
               System.out.println("\nERRO: Por favor, digite uma opção valida de 1 a 3.");
               io.readLine();
           }
       } while (opcao != 1);

    }
}
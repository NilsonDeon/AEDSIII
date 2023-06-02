/**
 * Main - Trabalho Pratico 04 de Algoritmos e Estruturas de Dados IV
 * @author Gabriel Vargas Bento de Souza e Nilson Deon Cordeiro Filho
 * @version 4.0 06/2023
*/

// Bibliotecas
import java.util.InputMismatchException;

// Bibliotecas proprias
import app.IO;
import casamentoPadroes.CasamentoPadroes;
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
        CasamentoPadroes casamento = new CasamentoPadroes();

       String intro = "\n        Trabalho Pratico 04 - TP04         " +
                      "\n    Algoritmos e Estruturas de Dados III   " + 
                      "\n       Gabriel Vargas e Nilson Deon        " +
                      "\n     Base de dados: Musicas do Spotify     " +
                      "\n                  06/2023                  ";

       String menu = "\n+------------------------------------------+" +
                     "\n|              MENU PRINCIPAL              |" +
                     "\n|------------------------------------------|" +
                     "\n|  1 - Sair                                |" +
                     "\n|  2 - Carregar dados iniciais             |" +
                     "\n|  3 - Cadastrar                           |" +
                     "\n|  4 - Pesquisar                           |" +
                     "\n|  5 - Atualizar                           |" +
                     "\n|  6 - Deletar                             |" +
                     "\n|  7 - Ordenar                             |" +
                     "\n|  8 - Salvar registros em txt             |" +
                     "\n|  9 - Abrir musica no Spotify             |" +
                     "\n| 10 - Comprimir registro                  |" +
                     "\n| 11 - Descomprimir registro               |" +
                     "\n| 12 - Casamento de padrao                 |" +
                     "\n+------------------------------------------+";
       int opcao = -1;

       System.out.println(intro);

       do {
           try {
               System.out.println(menu);
               opcao = io.readInt("\nDigite uma opcao: ");

               switch (opcao) {
                   case  1:                         break;
                   case  2: crud.carregarCSV();     break;
                   case  3: crud.create();          break;
                   case  4: crud.read();            break;
                   case  5: crud.update();          break;
                   case  6: crud.delete();          break;
                   case  7: sort.ordenarArquivo();  break;
                   case  8: crud.saveTXT();         break;
                   case  9: crud.abrirMusica();     break;
                   case 10: crud.comprimir();       break;
                   case 11: crud.descomprimir();    break;
                   case 12: String padrao = io.readLine("str: "); casamento.kmp.procurarPadrao(padrao);    break;
                   default: mostrarErro();          break;
               }
           } catch (InputMismatchException e) {
               mostrarErro();
               io.readLine();
           } finally {
               
               // Fazer um break para ser possivel ler os resultados obtidos
               if(opcao >= 2 && opcao <= 12) {
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
                           "1 a 11.");
    }
}

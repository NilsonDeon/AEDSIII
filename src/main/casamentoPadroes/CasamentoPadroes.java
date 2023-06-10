// Package
package casamentoPadroes;

import java.io.BufferedWriter;
// Bibliotecas
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Bibliotecas proprias
import app.IO;
import app.Musica;
import casamentoPadroes.auxiliar.Contador;

public class CasamentoPadroes {

    private static final String registroDB = "./src/resources/Registro.db";
    private static final IO io = new IO();

    private KMP KMP;
    private ForcaBruta FB;
    private BoyerMoore BM;
    private RabinKarp RK;
    private ShiftAnd SA;

    /**
     * Construtor padrao da classe casamentoPadroes.
     */
    public CasamentoPadroes() {
        KMP = new KMP();
        FB = new ForcaBruta();
        BM = new BoyerMoore();
        RK = new RabinKarp();
        SA = new ShiftAnd();
    }

    /**
     * Metodo para procurar um padrao (string) dentro do arquivo "Registro.db"
     */
    public void procurarPadrao() {

        // Testar se registro existe
        File arquivoRegistro = new File(registroDB);

        if (arquivoRegistro.length() > 0) {

            // Ler padrao a ser procurado
            String padrao = io.readLine("\nDigite o padrao a se procurar: ");

            // Testar se valido
            while (padrao.length() <= 0) {
                System.out.println("\nERRO: Digite um padrao valido!");
                padrao = io.readLine("\nDigite o padrao a se procurar: ");
            }

            // Forca Bruta
            Contador comparacoesFB = new Contador();
            Contador ocorrenciasFB = new Contador();
            List<Musica> listMusicFB = new ArrayList<>();
            long FBInicio = io.now();
            FB.procurarPadrao(padrao, comparacoesFB, ocorrenciasFB, listMusicFB);
            long FBFim = io.now();
            String tempoFB = io.getTempo(FBInicio, FBFim);

            // KMP
            Contador comparacoesKMP = new Contador();
            Contador ocorrenciasKMP = new Contador();
            List<Musica> listMusicKMP = new ArrayList<>();
            long KMPInicio = io.now();
            KMP.procurarPadrao(padrao, comparacoesKMP, ocorrenciasKMP, listMusicKMP);
            long KMPFim = io.now();
            String tempoKMP = io.getTempo(KMPInicio, KMPFim);

            // Boyer Moore
            Contador comparacoesBM = new Contador();
            Contador ocorrenciasBM = new Contador();
            List<Musica> listMusicBM = new ArrayList<>();
            long BMInicio = io.now();
            BM.procurarPadrao(padrao, comparacoesBM, ocorrenciasBM, listMusicBM);
            long BMFim = io.now();
            String tempoBM = io.getTempo(BMInicio, BMFim);

            // Rabin Karp
            Contador comparacoesRK = new Contador();
            Contador ocorrenciasRK = new Contador();
            List<Musica> listMusicRK = new ArrayList<>();
            long RKInicio = io.now();
            RK.procurarPadrao(padrao, comparacoesRK, ocorrenciasRK, listMusicRK);
            long RKFim = io.now();
            String tempoRK = io.getTempo(RKInicio, RKFim);

            // Shift And
            Contador comparacoesSA = new Contador();
            Contador ocorrenciasSA = new Contador();
            List<Musica> listMusicSA = new ArrayList<>();
            long SAInicio = io.now();
            SA.procurarPadrao(padrao, comparacoesSA, ocorrenciasSA, listMusicSA);
            long SAFim = io.now();
            String tempoSA = io.getTempo(SAInicio, SAFim);


            // Exibir resultados
            String resultado = "\n _________________________________________________________"  +
                               "\n|  Algoritmo  | Comparacoes | Ocorrencias |  Tempo Busca  |" +
                               "\n|-------------|-------------|-------------|---------------|" +
                               "\n| Forca Bruta |" + mostrar(comparacoesFB, ocorrenciasFB, tempoFB) +
                               "\n| Boyer-Moore |" + mostrar(comparacoesBM, ocorrenciasBM, tempoBM) +
                               "\n| Rabin-Karp  |" + mostrar(comparacoesRK, ocorrenciasRK, tempoRK) +
                               "\n| Shift And   |" + mostrar(comparacoesSA, ocorrenciasSA, tempoSA) +
                               "\n| KMP         |" + mostrar(comparacoesKMP, ocorrenciasKMP, tempoKMP) +
                               "\n|_____________|_____________|_____________|_______________|";
            
            System.out.println(resultado);

            // Salvar busca em TXT
            salvarPadraoTXT(padrao, listMusicFB);

        } else {
            arquivoRegistro.delete();
            System.out.println("\nERRO: Registro vazio!" +
                               "\n      Tente carregar os dados iniciais primeiro!\n");
        }    
    }

    /**
     * Metodo privado para mostrar os resultados da busca por padrao.
     * @param comp - Contador do numero de comparacoes.
     * @param ocorrencias - Contador do numero de ocorrencias.
     * @param tempo - String com o tempo gasto na busca.
     * @return - String formatada com as informacoes do algoritmo.
     */
    private String mostrar(Contador comp, Contador ocorrencias, String tempo) {
        String formattedComp = String.format("%12d", comp.cont);
        String formattedOcorrencias = String.format("%12d", ocorrencias.cont);
        String formattedTempo = String.format("%-13s", tempo);
        
        return(formattedComp + " |" + formattedOcorrencias + " |" + formattedTempo) + "|";
    }

    /**
     * Metodo para salvar os resultados para o padrao procurado em txt.
     * @param padrao - padrao procurado.
     * @param listMusic - lista de musicas salvas.
     */
    private void salvarPadraoTXT(String padrao, List<Musica> listMusic) {

        // Testar se houve match
        if (listMusic.size() > 0) {

            String menu = "\nForam encontradas " + listMusic.size() + " musica(s)\n" +
                        "\n+------------------------------------------+" +
                        "\n|  Deseja salvar os resultados em arquivo? |" +
                        "\n|------------------------------------------|" +
                        "\n|         1 - SIM          2 - NAO         |" +
                        "\n+------------------------------------------+";  

            System.out.println(menu);

            // Perguntar se deseja salvar
            int opcao = io.readInt("\nDigite uma opcao: ");
            while (opcao != 1 && opcao != 2) {
                System.out.println("\nERRO: Por favor, digite uma opcao valida.");
                opcao = io.readInt("\nDigite uma opcao: ");
            }

            // Salvar se desejar
            if (opcao == 1) {

                BufferedWriter arqTXT = null;

                try{
                    String pathPasta = "./src/resources/casamentoPadroes/";
                    String pathArquivo = "./src/resources/casamentoPadroes/" + padrao + ".txt";

                    // Abrir pasta se nao existir
                    File pasta = new File(pathPasta);
                    if (!pasta.exists()) {
                        pasta.mkdirs();
                    }

                    // Apagar arquivo anterior caso exista
                    File arqPadrao = new File(pathArquivo);
                    arqPadrao.delete();

                    // Escrever no arquivo
                    arqTXT = new BufferedWriter (new FileWriter (pathArquivo));
                    for(Musica musica : listMusic) {
                        arqTXT.write(musica.getId() + " - " + musica.getNome() + "\n");
                    }
                    arqTXT.close();
                
                } catch (IOException e) {
                    System.out.println("\nERRO: " + e.getMessage() + " ao escrever o arquivo \"" + arqTXT + "\"\n");
                }
            }
        }

    }
    
}

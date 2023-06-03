// Package
package casamentoPadroes;

// Bibliotecas
import java.util.ArrayList;
import java.util.List;

// Bibliotecas proprias
import app.IO;
import casamentoPadroes.auxiliar.Contador;

public class CasamentoPadroes {

    private static final IO io = new IO();

    private KMP KMP;
    private ForcaBruta FB;
    private BoyerMoore BM;
    private RabinKarp RK;
    private ShiftAnd SA;

    public CasamentoPadroes() {
        KMP = new KMP();
        FB = new ForcaBruta();
        BM = new BoyerMoore();
        RK = new RabinKarp();
        SA = new ShiftAnd();
    }

    public void procurarPadrao(String padrao) {

        // Forca Bruta
        Contador comparacoesFB = new Contador();
        Contador ocorrenciasFB = new Contador();
        List<Integer> listIdFB = new ArrayList<>();
        long FBInicio = io.now();
        FB.procurarPadrao(padrao, comparacoesFB, ocorrenciasFB, listIdFB);
        long FBFim = io.now();
        String tempoFB = io.getTempo(FBInicio, FBFim);

        // KMP
        Contador comparacoesKMP = new Contador();
        Contador ocorrenciasKMP = new Contador();
        List<Integer> listIdKMP = new ArrayList<>();
        long KMPInicio = io.now();
        KMP.procurarPadrao(padrao, comparacoesKMP, ocorrenciasKMP, listIdKMP);
        long KMPFim = io.now();
        String tempoKMP = io.getTempo(KMPInicio, KMPFim);

        // Boyer Moore
        Contador comparacoesBM = new Contador();
        Contador ocorrenciasBM = new Contador();
        List<Integer> listIdBM = new ArrayList<>();
        long BMInicio = io.now();
        BM.procurarPadrao(padrao, comparacoesBM, ocorrenciasBM, listIdBM);
        long BMFim = io.now();
        String tempoBM = io.getTempo(BMInicio, BMFim);

        // Rabin Karp
        Contador comparacoesRK = new Contador();
        Contador ocorrenciasRK = new Contador();
        List<Integer> listIdRK = new ArrayList<>();
        long RKInicio = io.now();
        RK.procurarPadrao(padrao, comparacoesRK, ocorrenciasRK, listIdRK);
        long RKFim = io.now();
        String tempoRK = io.getTempo(RKInicio, RKFim);

        // Shift And
        Contador comparacoesSA = new Contador();
        Contador ocorrenciasSA = new Contador();
        List<Integer> listIdSA = new ArrayList<>();
        long SAInicio = io.now();
        SA.procurarPadrao(padrao, comparacoesSA, ocorrenciasSA, listIdSA);
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
    }

    private String mostrar(Contador comp, Contador ocorrencias, String tempo) {
        String formattedComp = String.format("%12d", comp.cont);
        String formattedOcorrencias = String.format("%12d", ocorrencias.cont);
        String formattedTempo = String.format("%-13s", tempo);
        
        return(formattedComp + " |" + formattedOcorrencias + " |" + formattedTempo) + "|";
    }
    
}

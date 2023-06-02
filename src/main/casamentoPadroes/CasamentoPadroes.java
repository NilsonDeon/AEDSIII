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

    public CasamentoPadroes() {
        KMP = new KMP();
        FB = new ForcaBruta();
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

        // Exibir resultados
        String resultado = "\n _________________________________________________________"  +
                           "\n|  Algoritmo  | Comparacoes | Ocorrencias |  Tempo Busca  |" +
                           "\n|-------------|-------------|-------------|---------------|" +
                           "\n| Forca Bruta |" + mostrar(comparacoesFB, ocorrenciasFB, tempoFB) +
                           "\n| Boyer-Moore |" + mostrar(comparacoesKMP, ocorrenciasKMP, tempoKMP) +
                           "\n| Rabin-Karp  |" + mostrar(comparacoesKMP, ocorrenciasKMP, tempoKMP) +
                           "\n| Shift And   |" + mostrar(comparacoesKMP, ocorrenciasKMP, tempoKMP) +
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

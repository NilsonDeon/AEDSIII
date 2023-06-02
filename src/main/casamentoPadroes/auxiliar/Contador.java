// Package
package casamentoPadroes.auxiliar;

/**
 * Classe para servir como um contador auxiliar. Por exemplo, contar o numero
 * de comparacoes e o de ocorrencias durante um casamento de padroes.
 */
public class Contador {
    
    public int cont;

    /**
     * Construtor padrao da classe Contador.
     */
    public Contador() {
        cont = 0;
    }

    /**
     * Construtor da classe Contador, com passagem de parametros.
     * @param numInicial - valor inicial para comecar o contador.
     */
    public Contador(int numInicial) {
        cont = numInicial;
    }
}
// Package
package compressao;

/**
 * Classe para representar um No na arvore de Huffman.
 */
public class No implements Comparable<No> {
    byte valor;
    int frequencia;
    No esquerda, direita;

    /**
     * Construtor da classe No.
     * @param valor - byte que o No representa.
     * @param frequencia - de ocorrencia daquele No.
     */
    public No(byte valor, int frequencia) {
        this.valor = valor;
        this.frequencia = frequencia;
        this.esquerda = null;
        this.direita = null;
    }

    /**
     * Construtor da classe No.
     * @param valor - byte que o No representa.
     */
    public No(byte valor) {
        this.valor = valor;
        this.frequencia = 0;
        this.esquerda = null;
        this.direita = null;
    }

    /**
     * Construtor da classe No.
     * @param esquerda - No da esquerda.
     * @param direita - No da direita.
     */
    public No(No esquerda, No direita) {
        this.valor = (byte)0;
        this.frequencia = 0;
        this.esquerda = esquerda;
        this.direita = direita;
    }

    /**
     * Construtor da classe No.
     * @param frequencia - de ocorrencia daquele No.
     * @param esquerda - No da esquerda.
     * @param direita - No da direita.
     */
    public No(int frequencia, No esquerda, No direita) {
        this.valor = (byte)0;
        this.frequencia = frequencia;
        this.esquerda = esquerda;
        this.direita = direita;
    }

    /**
     * Metodo para comparar os Nos com base em sua frequencia, para a 
     * construcao da fila de prioridade
     */
    public int compareTo(No other) {
        return this.frequencia - other.frequencia;
    }
}
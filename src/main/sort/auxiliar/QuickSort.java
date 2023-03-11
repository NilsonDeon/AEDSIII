package sort.auxiliar;

import app.Musica;

/**
 * Classe QuickSort responsavel por realizar a ordenacao em memoria principal
 * de um array de Musicas.
 */
public class QuickSort {

    private static int MAX_TAM;

    /**
     * Construtor padrao da Classe QuickSort.
     */
    public QuickSort (int tam) {
        MAX_TAM = tam;
    }

   /**
    * Metodo de ordenacao por divisao dos registros Musica pelo atributo
    * ID.
    * @param musicas - array de musicas a ser ordenado.
    * @param esq - posicao da lista dividido mais 'a esquerda
    * @param dir - posicao da lista dividido mais 'a direita
    */
    public void quicksort (Musica[] musicas, int esq, int dir) {
        int i = esq, j = dir;
        Musica pivo = musicas[(dir+esq)/2].clone();
 
        while (i <= j) {
            while (pivo.id > musicas[i].id){ i++; }
            while (musicas[j].id > pivo.id){ j--; }
            if (i <= j) {
                swap(musicas, i, j);
                i++;
                j--;
            }
        }
        if (esq < j) quicksort(musicas, esq, j);
        if (i < dir) quicksort(musicas, i, dir);
    }
 
    /**
     * Metodo para chamar de forma mais elegante o metodo de ordenacao 
     * quicksort.
     * @param musicas - array de musicas a ser ordenado.
     */
    public void quicksort(Musica[] musicas) {
        quicksort(musicas, 0, MAX_TAM-1);
    }
 
    /**
     * Metodo para trocar duas posicoes em um array de objetos do tipo Musica.
     * @param i - posicao de um elemento a ser trocado.
     * @param j - posicao do outro elemento que sera' trocado com o anterior.
     */
     public void swap (Musica[] musicas, int i, int j){
       Musica tmp = new Musica();
       tmp = musicas[j].clone();
       musicas[j] = musicas[i].clone();
       musicas[i] = tmp.clone();
    }
}
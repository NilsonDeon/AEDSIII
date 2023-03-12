package sort.auxiliar;

import app.Musica;

/**
 * Classe QuickSort responsavel por realizar a ordenacao em memoria principal
 * de um array de Musicas.
 */
public class QuickSort {

    private static int MAX_TAM;
    private int atributo;

    /**
     * Construtor padrao da Classe QuickSort.
     */
    public QuickSort (int tam, int atributo) {
        MAX_TAM = tam;

        // Se valor valido, atribuir
        if (atributo >= 1 && atributo <=3) {
            this.atributo = atributo;
        
        // Se nao, ordenar pelo ID (padrao)
        } else {
            this.atributo = 1;
        }
    }

   /**
    * Metodo de ordenacao por divisao dos registros Musica pelo atributo
    * ID ou pelo atributo nome.
    * @param musicas - array de musicas a ser ordenado.
    * @param esq - posicao da lista dividido mais 'a esquerda
    * @param dir - posicao da lista dividido mais 'a direita
    */
    public void quicksort (Musica[] musicas, int esq, int dir) {
        int i = esq, j = dir;
        Musica pivo = musicas[(dir+esq)/2].clone();
 
        while (i <= j) {

            switch (atributo) {

                // Order by ID
                case 1: 
                    while (pivo.getId() > musicas[i].getId()){ i++; }
                    while (musicas[j].getId() > pivo.getId()){ j--; }
                    break;
                
                // Order by nome
                case 2: 
                    while (pivo.getNome().compareTo(musicas[i].getNome()) > 0){ i++; }
                    while (musicas[j].getNome().compareTo(pivo.getNome()) > 0){ j--; }
                    break;

                // Order by data lancamento
                case 3: 
                    while (pivo.getDataLancamento().compareTo(musicas[i].getDataLancamento()) > 0){ i++; }
                    while (musicas[j].getDataLancamento().compareTo(pivo.getDataLancamento()) > 0){ j--; }
                    break;
            }
            
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
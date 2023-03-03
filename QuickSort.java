public class QuickSort {

    private static int MAX_TAM;
 
    public QuickSort (int tam) {
        MAX_TAM = tam;
    }

   /**
    * quicksort - metodo de ordenacao por divisao da Lista de objetos Musica
    * pelo atributo nome.
    * @param musicas - array de musicas a ser ordenado.
    * @param esq - posicao da lista dividido mais 'a esquerda
    * @param dir - posicao da lista dividido mais 'a direita
    */
    public void quicksort (Musica[] musicas, int esq, int dir) {
        int i = esq, j = dir;
        Musica pivo = musicas[(dir+esq)/2].clone();
 
        while (i <= j) {
            while (pivo.id < musicas[i].id){ i++; }    // SINAL TROCADO
            while (musicas[j].id < pivo.id){ j--; }    // < COM >
            if (i <= j) {
                swap(musicas, i, j);
                i++;
                j--;
            }
        }
        if (esq < j) quicksort(musicas, esq, j);
        if (i < dir) quicksort(musicas, i, dir);
    }
 
    /* 
     * quicksort - metodo para chamar de forma mais elegante o metodo de
     * de ordenacao quicksort.
     */
     public void quicksort(Musica[] musicas) {
         quicksort(musicas, 0, MAX_TAM-1);
     }
 
    /**
     * swap - metodo para trocar duas posicoes em um array de objetos do tipo 
     * Musica.
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
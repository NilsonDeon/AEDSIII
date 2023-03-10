public class MinHeap {

    protected Musica[] heap;
    protected int prioridade[];
    protected int size;
    protected int capacity;

    /**
     * Construtor padrao da classe MinHeap.
     */
    public MinHeap() {
        this(1500);
    }

    /**
     * Construtor da classe MinHeap com passagem de parametros.
     * @param capacity - capacidade maxima do heap.
     */
    public MinHeap(int capacity) {

        if (capacity > 3) {
            this.capacity = capacity;
        } else {
            this.capacity = 1500;
        }

        this.size = 0;
        this.heap = new Musica[this.capacity];
        this.prioridade = new int[this.capacity];
        
    }

    /**
     * Metodo para inserir nova Musica no heap.
     * @param musica - a ser inserida.
     * @param cargaInicial - booleano para indicar se e' a carga do heap
     * @throws Exception - Caso o heap estiver cheio.
     */
    public void inserir (Musica musica, boolean cargaInicial) throws Exception {
        if (isFull()) {
            throw new Exception ("\nERRO: MinHeap.inserir() -> heap cheio!\n");
        }

        heap[size] = musica;
        int posAtual = size;

        if (cargaInicial == false) {
            // Atualizar a prioridade da nova musica
            if (size > 0) {

                // Se musica for ordenavel ainda, manter igual
                if (heap[0].id <= musica.id) {
                    prioridade[size] = prioridade[0];

                // Caso contrario, atualizar para proximo valor
                } else {
                    prioridade[size] = prioridade[0] + 1;
                }
            } else {

                // Primeira insercao
                prioridade[0] = 0;
            }
        } else {

            // Os primeiros devem sempre receber prioridade 0
            prioridade[size] = 0;
        }
        size++;

        // Reorganiza o heap apos a insercao para pai ser menor que filhos
        while (posAtual > 0 && comparePrioridade(posAtual, parent(posAtual))) {
            swap(posAtual, parent(posAtual));
            posAtual = parent(posAtual);
        }

    }

    /**
     * Metodo para remover Musica do heap.
     * @return removed - musica de menor ID que foi removida.
     * @throws Exception - Caso o heap estiver vazio.
     */
    public Musica remover () throws Exception {
        if (isEmpty()) {
            throw new Exception ("\nERRO: MinHeap.remover() -> heap vazio!\n");
        }

        Musica removed = heap[0];
        heap[0] = heap[size - 1];
        prioridade[0] = prioridade[size - 1];
        size--;
        heapify(0);

        return removed;
    }

    /**
     * Metodo para obter a prioridade da primeira Musica do heap; 
     * consequentemente, a menor.
     * @return - numero desta prioridade.
     */
    public int getMenorPrioridade() {
        return prioridade[0];
    }

    /**
     * Metodo para comparar a prioridade de duas musicas a partir de seu id e a
     * prioridade obtida na hora da insercao.
     * @param i - posicao da musica 1.
     * @param j - posicao da musica 2.
     * @return true se 1 < 2; false, caso contrario.
     */
    private boolean comparePrioridade(int i, int j) {
        boolean resp;

        if (prioridade[i] != prioridade[j]) {
            resp = (prioridade[i] < prioridade[j]);
        } else {
            resp = (heap[i].id <= heap[j].id);
        }

        return resp;
    }

    /**
     * Metodo para trocar duas musicas do heap de lugar. E, tambem, atualizar
     * suas respectivas ordens de prioridade.
     * @param i - 1 posicao a ser trocada.
     * @param j - 2 posicao a ser trocada.
     */
    private void swap(int i, int j) {
        Musica heapTmp = heap[i];
        heap[i] = heap[j];
        heap[j] = heapTmp;

        int prioridadeTmp = prioridade[i];
        prioridade[i] = prioridade[j];
        prioridade[j] = prioridadeTmp;
    }

    /**
     * Metodo para comparar o pai com seus filhos e realizar a troca caso seja
     * necessario, visando manter a caracteristica de heap minimo.
     * @param i - posicao de onde comecao a verificacao.
     */
    private void heapify(int i) {
        int smallest = i;
        int left = leftChild(i);
        int right = rightChild(i);

        // Obter a menor musica filha, se existir
        if (left < size && comparePrioridade(left, smallest)) {
            smallest = left;
        }

        if (left < size && comparePrioridade(right, smallest)) {
            smallest = right;
        }

        // Se pai for maior que filho, trocar
        if (smallest != i) {
            swap(i, smallest);
            heapify(smallest);
        }
    }

    /**
     * Metodo para obter a posicao do pai de um elemento do heap.
     * @param i - elemento que se deseja obter o pai.
     * @return posicao do pai de i.
     */
    private int parent(int i) {
        return (i - 1) / 2;
    }

    /**
     * Metodo para obter a posicao do filho esquerdo de um elemento do heap.
     * @param i - elemento que se deseja obter o filho.
     * @return posicao do filho esquerdo de i.
     */
    private int leftChild(int i) {
        return 2 * i + 1;
    }

    /**
     * Metodo para obter a posicao do filho direito de um elemento do heap.
     * @param i - elemento que se deseja obter o filho.
     * @return posicao do filho direito de i.
     */
    private int rightChild(int i) {
        return 2 * i + 2;
    }

    /**
     * Metodo para verificar se heap esta' vazio.
     * @return true, se tiver; false, caso contrario.
     */
    private boolean isEmpty() {
        return size == 0;
    }

    /**
     * Metodo para verificar se heap esta' cheio.
     * @return true, se tiver; false, caso contrario.
     */
    private boolean isFull() {
        return size == capacity;
    }

    /**
     * Metodo para verificar se heap nao esta' vazio.
     * @return true, se tiver; false, caso contrario.
     */
    public boolean hasElement() {
        return !isEmpty();
    }
}

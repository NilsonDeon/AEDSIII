package arvoreB;

public class NoB {

    protected static final int ordemArvore = 8;
    protected int chave[];
    protected long endereco[];
    protected NoB noFilho[];
    protected NoB noPai;
    protected int numElementos;

    public NoB() {
        chave = new int[ordemArvore-1];
        endereco = new long[ordemArvore-1];
        noFilho = new NoB[ordemArvore];
        noPai = null;

        for(int i = 0; i < ordemArvore-1; i++) {
            chave[i] = -1;
            endereco[i] = -1;
        }

        for(int i = 0; i < ordemArvore; i++) {
            noFilho[i] = null;
        }

        numElementos = 0;
    }

    public NoB(int newChave, long newEndereco) {
        chave = new int[ordemArvore-1];
        endereco = new long[ordemArvore-1];
        noFilho = new NoB[ordemArvore];
        noPai = null;

        for(int i = 0; i < ordemArvore-1; i++) {
            chave[i] = -1;
            endereco[i] = -1;
        }

        for(int i = 0; i < ordemArvore; i++) {
            noFilho[i] = null;
        }

        chave[0] = newChave;
        endereco[0] = newEndereco;
        numElementos = 1;
    }

    private void inserir(int newChave, long newEndereco) {
        if (temEspacoLivre()) {

            // Localizar local de insercao no array
            int i;
            for(i = 0; (i < numElementos) && (chave[i] < newChave); i++);

            // Shift dos elementos para a direita
            int pos = i;
            i = numElementos-1;
            while(i >= pos) {
                chave[i+1] = chave[i];
                endereco[i+1] = endereco[i];
                noFilho[i+1] = noFilho[i];
                i--;
            }

            // Inserir efetivamente na posicao ordenada
            chave[pos] = newChave;
            endereco[pos] = newEndereco;
            noFilho[pos+1] = null;
            numElementos++;

        } else {
            System.out.println("\nERRO: No cheio!!");
        }
    }

    public void remontarPonteiros(int newChave, NoB noEsq, NoB noDir) {

        // Localizar chave recem inserida
        int i;
        for(i = 0; (i < (ordemArvore-1)/2) && (chave[i] != newChave); i++);

        // Adicionar filhos do novo NoB
        noFilho[i] = noEsq;
        noFilho[i+1] = noDir;

    }

    public boolean isFull() {
        return numElementos == ordemArvore-1;
    }

    public boolean temEspacoLivre() {
        return !isFull();
    }

    public NoB getFilhoEsq() {
        int i = 0;
        int tamNo = (ordemArvore-1)/2;
        NoB noEsq = new NoB();

        // Copiar os primeiros elementos do No
        int pos = i;
        while (pos < tamNo) {
            noEsq.inserir(this.chave[pos], this.endereco[pos]);
            pos++;
        }

        // Settar ponteiros
        pos = i;
        int k = 0;
        while (pos <= tamNo) {
            this.noFilho[pos] = noEsq.noFilho[k++];
            pos++;
        }

        // Settar pai deste novo No
        noEsq.noPai = this;

        return noEsq;
    }

    public NoB getFilhoDir() {
        int i = (ordemArvore-1)-1;
        int tamNo = (ordemArvore-1)/2;
        NoB noDir = new NoB();

        // Copiar os ultimos elementos do No
        int pos = i;
        while (pos > tamNo) {
            noDir.inserir(this.chave[pos], this.endereco[pos]);
            pos--;
        }

        // Settar ponteiros
        pos = i;
        int k = 0;
        while (pos >= tamNo) {
            this.noFilho[pos] = noDir.noFilho[k++];
            pos--;
        }

        // Settar pai deste novo No
        noDir.noPai = this;

        return noDir;
    }

    public NoB getMeio() {
        int posMeio = ((ordemArvore-1)/2);
        NoB newRaiz = new NoB(this.chave[posMeio], this.endereco[posMeio]);

        return newRaiz;
    }

    public NoB encontrarInsercao (int chave) {
        return encontrarInsercao(this, chave);
    }

    public NoB encontrarInsercao (NoB no, int chave) {

        NoB procurado;
        
        // Procurar o filho que a chave poderia ficar
        int i;
        for(i = 0; (i < no.numElementos) && (chave > no.chave[i]); i++);

        // Se o No nao for folha, continuar recursao
        if (no.noFilho[i] != null) {
            procurado = no.noFilho[i];
            procurado = encontrarInsercao(procurado, chave);
        
        // Se o No for folha, retornar ele mesmo
        } else {
            procurado = no;
        }

        return procurado;
    }

    public boolean isFolha() {
        boolean resp = true;
        for(int i = 0; i <= ordemArvore-1; i++) {
            resp = resp && this.noFilho[i] == null;
        }

        return resp;
    }

    public int getChave(int pos) {
        int chaveProcurada = -1;
        if (pos < numElementos) {
            chaveProcurada = this.chave[pos];
        }

        return chaveProcurada;
    }

    public long getEndereco(int pos) {
        long enderecoProcurado = -1;
        if (pos < numElementos) {
            enderecoProcurado = this.endereco[pos];
        }

        return enderecoProcurado;
    }

    public boolean isRaiz() {
        return noPai == null;
    }

/*
    private void ordenar() {
        for(int i = 1; i  < numElementos; i++) {
            int chaveTmp = chave[i];
            int enderecoTmp = endereco[i];

            int j = i-1;

            while ((j >= 0) && (chave[j] > chaveTmp)) {
                chave[j+1] = chave[j];
                endereco[j+1] = endereco[j];
                j--;
            }
            chave[j+1] = chaveTmp;
            endereco[j+1] = enderecoTmp;
      }
	}
*/
}
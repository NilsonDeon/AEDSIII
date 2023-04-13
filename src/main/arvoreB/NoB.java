package arvoreB;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class NoB {

    protected static final int ordemArvore = 8;

    protected short numElementos;
    protected int chave[];
    protected long endereco[];
    protected long noFilho[];

    private static final String arvoreBDB = "./src/resources/ArvoreB.db";

    /**
     * Construtor padrao da classe NoB.
     */
    public NoB() {

        numElementos = 0;
        chave = new int[ordemArvore-1];
        endereco = new long[ordemArvore-1];
        noFilho = new long[ordemArvore];

        for(int i = 0; i < ordemArvore-1; i++) {
            chave[i] = -1;
            endereco[i] = -1;
        }

        for(int i = 0; i < ordemArvore; i++) {
            noFilho[i] = -1;
        }
    }

    /**
     * Costrutor da classe NoB, utilizando passagem de parametros.
     * @param newChave - id da chave a ser inserida no No.
     * @param newEndereco - posicao do id no arquivo "Registro.db".
     */
    public NoB(int newChave, long newEndereco) {

        chave = new int[ordemArvore-1];
        endereco = new long[ordemArvore-1];
        noFilho = new long[ordemArvore];

        for(int i = 0; i < ordemArvore-1; i++) {
            chave[i] = -1;
            endereco[i] = -1;
        }

        for(int i = 0; i < ordemArvore; i++) {
            noFilho[i] = -1;
        }

        numElementos = 1;
        chave[0] = newChave;
        endereco[0] = newEndereco;
    }

    /**
     * Metodo para criar um NoB em arquivo, como fluxo de bytes.
     * @throws Exception Se ocorrer algum erro ao manipular os arquivos.
     */
    public void escreverNoB() throws Exception {
        RandomAccessFile arvoreBFile = null;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBDB, "rw");

            // Posicionar ponteiro no fim do arquivo
            long fimArquivo = arvoreBFile.length();
            arvoreBFile.seek(fimArquivo);

            // Escrever numero de elementos no No
            byte[] numElementosBytes = ByteBuffer.allocate(2).putShort(numElementos).array();
            arvoreBFile.write(numElementosBytes);

            // Escrever informacoes do No
            for (int i = 0; i < ordemArvore-1; i++) {
                
                // Escrever ponteiro para filho da esquerda da posicao i
                byte[] noFilhoBytes = ByteBuffer.allocate(8).putLong(noFilho[i]).array();
                arvoreBFile.write(noFilhoBytes);

                // Escrever chave na posicao i
                byte[] chaveBytes = ByteBuffer.allocate(4).putInt(chave[i]).array();
                arvoreBFile.write(chaveBytes);

                // Escrever endereco na posicao i para "Registro.db"
                byte[] enderecoBytes = ByteBuffer.allocate(8).putLong(endereco[i]).array();
                arvoreBFile.write(enderecoBytes);
            }

            // Escrever ultimo ponteiro 'a direita
            byte[] noFilhoBytes = ByteBuffer.allocate(8).putLong(noFilho[ordemArvore-1]).array();
            arvoreBFile.write(noFilhoBytes);

        } catch (IOException e) {
            System.out.println("\nERRO: Ocorreu um erro de escrita no " +
                               "arquivo \"" + arvoreBDB + "\"\n");
        } finally {
            if (arvoreBFile != null) arvoreBFile.close();
        }
    }

    /**
     * Metodo para criar um NoB em arquivo, como fluxo de bytes.
     * @param posicaoInserir - posicao de inicio para escrita do NoB.
     * @throws Exception Se ocorrer algum erro ao manipular os arquivos.
     */
    public void escreverNoB(long posicaoInserir) throws Exception {
        RandomAccessFile arvoreBFile = null;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBDB, "rw");

            // Posicionar ponteiro no local de inicio do NoB.
            arvoreBFile.seek(posicaoInserir);

            // Escrever numero de elementos no No
            byte[] numElementosBytes = ByteBuffer.allocate(2).putShort(numElementos).array();
            arvoreBFile.write(numElementosBytes);

            // Escrever informacoes do No
            for (int i = 0; i < ordemArvore-1; i++) {
                
                // Escrever ponteiro para filho da esquerda da posicao i
                byte[] noFilhoBytes = ByteBuffer.allocate(8).putLong(noFilho[i]).array();
                arvoreBFile.write(noFilhoBytes);

                // Escrever chave na posicao i
                byte[] chaveBytes = ByteBuffer.allocate(4).putInt(chave[i]).array();
                arvoreBFile.write(chaveBytes);

                // Escrever endereco na posicao i para  "Registro.db"
                byte[] enderecoBytes = ByteBuffer.allocate(8).putLong(endereco[i]).array();
                arvoreBFile.write(enderecoBytes);
            }

            // Escrever ultimo ponteiro 'a direita
            byte[] noFilhoBytes = ByteBuffer.allocate(8).putLong(noFilho[ordemArvore-1]).array();
            arvoreBFile.write(noFilhoBytes);

        } catch (IOException e) {
            System.out.println("\nERRO: Ocorreu um erro de escrita no " +
                               "arquivo \"" + arvoreBDB + "\"\n");
        } finally {
            if (arvoreBFile != null) arvoreBFile.close();
        }
    }

    /**
     * Metodo para inserir um NoB em arquivo, como fluxo de bytes.
     * @param posicaoInserir - posicao de inicio para escrita no arquivo.
     * @param newChave - nova chave a se inserir.
     * @param newEndereco - novo endereco a se inserir.
     * @throws Exception Se ocorrer algum erro ao manipular os arquivos.
     */
    public void inserir(long posicaoInserir, int newChave, long newEndereco) throws Exception {
        RandomAccessFile arvoreBFile = null;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBDB, "rw");

            // Inserir o elemento de forma ordenada
            inserir(newChave, newEndereco);

            // Escrever numero de elementos no No
            arvoreBFile.seek(posicaoInserir);
            escreverNoB(posicaoInserir);

        } catch (IOException e) {
            System.out.println("\nERRO: Ocorreu um erro de escrita no " +
                               "arquivo \"" + arvoreBDB + "\"\n");
        } finally {
            if (arvoreBFile != null) arvoreBFile.close();
        }
    }

    /**
     * Metodo para inserir uma chave no NoB de forma ordenada, mantendo os 
     * devidos ponteiros.
     * @param newChave - nova chave a se inserir.
     * @param newEndereco - novo endereco a se inserir.
     * @throws Exception Se ocorrer algum erro ao manipular os arquivos.
     */
    private void inserir(int newChave, long newEndereco) {
        if (temEspacoLivre()) {

            // Localizar local de insercao no No
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
            noFilho[pos+1] = -1;
            numElementos++;

        } else {
            System.out.println("\nERRO: No cheio!!");
        }
    }

    /**
     * Metodo para ler NoB em arquivo, a apartir de sua posicao de inicio.
     * @param posInicio - posicao de inicio daquele NoB.
     * @throws Exception Se ocorrer algum erro ao manipular os arquivos.
     */
    public void lerNoB (long posInicio) throws Exception {
        RandomAccessFile arvoreBFile = null;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBDB, "rw");

            // Posicionar ponteiro na posicao de inicio do No
            arvoreBFile.seek(posInicio);

            // Ler numero de elementos no No
            numElementos = arvoreBFile.readShort();

            // Ler informacoes do No
            for (int i = 0; i < ordemArvore-1; i++) {
                
                // Ler ponteiro para filho da esquerda da posicao i
                noFilho[i] = arvoreBFile.readLong();

                // Ler chave na posicao i
                chave[i] = arvoreBFile.readInt();

                // Ler endereco na posicao i para  "Registro.db"
                endereco[i] = arvoreBFile.readLong();
            }

            // Ler ultimo ponteiro 'a direita
            noFilho[ordemArvore-1] = arvoreBFile.readLong();

        } catch (IOException e) {
            System.out.println("\nERRO: Ocorreu um erro de escrita no " +
                               "arquivo \"" + arvoreBDB + "\"\n");
        } finally {
            if (arvoreBFile != null) arvoreBFile.close();
        }
    }

    /**
     * Metodo para encontrar NoB de insersao de uma chave.
     * @param chave - que sera' inserida.
     * @return noB - no qual deve-se inserir a chave.
     * @throws Exception Se ocorrer algum erro ao manipular os arquivos.
     */
    public long encontrarInsercao (int chave) throws Exception {
        return encontrarInsercao(this, chave);
    }

    /**
     * Metodo privado para encontrar o local de insersao de uma chave em um
     * determinado NoB.
     * @param no - NoB em analise.
     * @param chave - chave que sera' inserida.
     * @return posInserir - endereco de insercao no arquivo.
     * @throws Exception Se ocorrer algum erro ao manipular os arquivos.
     */
    private long encontrarInsercao (NoB no, int chave) throws Exception {

        long posInserir;
       
        // Procurar o filho, no qual a chave poderia ficar
        int i;
        for(i = 0; (i < no.numElementos) && (chave > no.chave[i]); i++);

        // Se o No nao for folha, continuar recursao
        posInserir = no.noFilho[i];
        if (no.noFilho[i] != -1) {
            no.lerNoB(posInserir);
            posInserir = encontrarInsercao(no, chave);
        }

        return posInserir;
    }

    /**
     * Metodo para obter o No resultante com as chaves 'a esquerda do No em
     * analise.
     * @return NoEsq - com os elementos menores que a chave do meio.
     */
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

        return noEsq;
    }

    /**
     * Metodo para obter o No resultante com as chaves 'a direita do No em
     * analise.
     * @return NoDir - com os elementos maiores que a chave do meio.
     */
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

        return noDir;
    }

    /**
     * Metodo para indicar se o NoB esta' cheio ou nao.
     * @return true, se estiver cheio; false, caso contrario.
     */
    public boolean isFull() {
        return numElementos == ordemArvore-1;
    }

    /**
     * Metodo para indicar se o NoB tem espaco livre para insercao.
     * @return true, se tiver espaco; false, caso contrario.
     */
    public boolean temEspacoLivre() {
        return !isFull();
    }

    /**
     * Metodo para indicar se o NoB e' uma folha da 'arvore.
     * @return true, se for folha; false, caso contrario.
     */
    public boolean isFolha() {
        boolean resp = true;
        for(int i = 0; i <= ordemArvore-1; i++) {
            resp = resp && this.noFilho[i] == -1;
        }

        return resp;
    }
}
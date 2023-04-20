// Package
package arvores.arvoreBestrela;

// Bibliotecas
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class NoBestrela {

    protected static final int ordemArvore = 8;
    protected static final int tamNoB = 150;

    protected short numElementos;
    protected int chave[];
    protected long endereco[];
    protected long noFilho[];

    private static final String arvoreBestrelaDB = "./src/resources/ArvoreBestrela.db";

    /**
     * Construtor padrao da classe NoBestrela.
     */
    public NoBestrela() {

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
     * Costrutor da classe NoBestrela, utilizando passagem de parametros.
     * @param newChave - id da chave a ser inserida no No.
     * @param newEndereco - posicao do id no arquivo "Registro.db".
     */
    public NoBestrela(int newChave, long newEndereco) {

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
     * Costrutor da classe NoBestrela, utilizando passagem de parametros.
     * @param newChave - id da chave a ser inserida no No.
     * @param newEndereco - posicao do id no arquivo "Registro.db".
     * @param filhoEsq - posicao filho 'a esquerda.
     * @param filhoDir - posicao filho 'a direita.
     */
    public NoBestrela(int newChave, long newEndereco, long filhoEsq, long filhoDir) {

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
        noFilho[0] = filhoEsq;
        noFilho[1] = filhoDir;

    }

    /**
     * Metodo para clonar um NoBestrela.
     * @return NoBestrela clonado.
     */
    public NoBestrela clone() {

        NoBestrela cloneNoB = new NoBestrela();

        cloneNoB.numElementos = this.numElementos;
        cloneNoB.chave = this.chave;
        cloneNoB.endereco = this.endereco;
        cloneNoB.noFilho = this.noFilho;

        return cloneNoB;
    }


    /**
     * Metodo para criar um NoBestrela em arquivo, como fluxo de bytes.
     * @return fimArquivo - posicao do arquivo que o NoBestrela foi escrito.
     */
    public long escreverNoB() {
        RandomAccessFile arvoreBFile = null;
        long fimArquivo = -1;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBestrelaDB, "rw");

            // Posicionar ponteiro no fim do arquivo
            fimArquivo = arvoreBFile.length();
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

            // Fechar arquivo
            arvoreBFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao escrever o arquivo \"" + arvoreBestrelaDB + "\"\n");
        } finally {
            return fimArquivo;
        }
    }

    /**
     * Metodo para criar um NoBestrela em arquivo, como fluxo de bytes.
     * @param posicaoInserir - posicao de inicio para escrita do NoBestrela.
     */
    public void escreverNoB(long posicaoInserir) {
        RandomAccessFile arvoreBFile = null;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBestrelaDB, "rw");

            // Posicionar ponteiro no local de inicio do NoBestrela.
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

            // Fechar arquivo
            arvoreBFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao escrever o arquivo \"" + arvoreBestrelaDB + "\"\n");
        }
    }

    /**
     * Metodo para inserir um NoBestrela em arquivo, como fluxo de bytes.
     * @param posicaoInserir - posicao de inicio para escrita no arquivo.
     * @param newChave - nova chave a se inserir.
     * @param newEndereco - novo endereco a se inserir.
     */
    public void inserir(long posicaoInserir, int newChave, long newEndereco) {
        RandomAccessFile arvoreBFile = null;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBestrelaDB, "rw");

            // Inserir o elemento de forma ordenada
            inserir(newChave, newEndereco);

            // Escrever numero de elementos no No
            arvoreBFile.seek(posicaoInserir);
            escreverNoB(posicaoInserir);

            // Fechar arquivo
            arvoreBFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao escrever o arquivo \"" + arvoreBestrelaDB + "\"\n");
        }
    }

    /**
     * Metodo para inserir um NoBestrela em arquivo, como fluxo de bytes.
     * @param posicaoInserir - posicao de inicio para escrita no arquivo.
     * @param newChave - nova chave a se inserir.
     * @param newEndereco - novo endereco a se inserir.
     * @param filhoDir - endereco do filho da direita.
     */
    public void inserir(long posicaoInserir, int newChave, long newEndereco, long filhoDir) {
        RandomAccessFile arvoreBFile = null;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBestrelaDB, "rw");

            // Inserir o elemento de forma ordenada
            inserir(newChave, newEndereco, filhoDir);

            // Escrever numero de elementos no No
            arvoreBFile.seek(posicaoInserir);
            escreverNoB(posicaoInserir);

            // Fechar arquivo
            arvoreBFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao escrever o arquivo \"" + arvoreBestrelaDB + "\"\n");
        }
    }

    /**
     * Metodo para inserir um NoBestrela em arquivo, como fluxo de bytes.
     * @param posicaoInserir - posicao de inicio para escrita no arquivo.
     * @param newChave - nova chave a se inserir.
     * @param newEndereco - novo endereco a se inserir.
     * @param filhoDir - endereco do filho da direita.
     */
    public void inserir(long posicaoInserir, int newChave, long newEndereco, long filhoDir, long filhoEsq) {
        RandomAccessFile arvoreBFile = null;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBestrelaDB, "rw");

            // Inserir o elemento de forma ordenada
            inserir(newChave, newEndereco, filhoDir, filhoEsq);

            // Escrever numero de elementos no No
            arvoreBFile.seek(posicaoInserir);
            escreverNoB(posicaoInserir);

            // Fechar arquivo
            arvoreBFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao escrever o arquivo \"" + arvoreBestrelaDB + "\"\n");
        }
    }

    /**
     * Metodo para inserir uma chave no NoBestrela de forma ordenada, mantendo os 
     * devidos ponteiros.
     * @param newChave - nova chave a se inserir.
     * @param newEndereco - novo endereco a se inserir.
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
     * Metodo para inserir uma chave no NoBestrela de forma ordenada, mantendo os 
     * devidos ponteiros.
     * @param newChave - nova chave a se inserir.
     * @param newEndereco - novo endereco a se inserir.
     * @param filhoDir - endereco do filho 'a direita.
     */
    private void inserir(int newChave, long newEndereco, long filhoDir) {
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
            noFilho[pos+1] = filhoDir;
            numElementos++;

        } else {
            System.out.println("\nERRO: No cheio!!");
        }
    }

   /**
     * Metodo para inserir uma chave no NoBestrela de forma ordenada, mantendo os 
     * devidos ponteiros.
     * @param newChave - nova chave a se inserir.
     * @param newEndereco - novo endereco a se inserir.
     * @param filhoEsq - endereco do filho 'a esquerda.
     * @param filhoDir - endereco do filho 'a direita.
     */
    private void inserir(int newChave, long newEndereco, long filhoEsq, long filhoDir) {
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
            noFilho[pos] = filhoEsq;
            noFilho[pos+1] = filhoDir;
            numElementos++;

        } else {
            System.out.println("\nERRO: No cheio!!");
        }
    }

    /**
     * Metodo para ler NoBestrela em arquivo, a apartir de sua posicao de inicio.
     * @param posInicio - posicao de inicio daquele NoBestrela.
     */
    public void lerNoB (long posInicio) {
        RandomAccessFile arvoreBFile = null;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBestrelaDB, "rw");

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

            // Fechar arquivo
            arvoreBFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler o arquivo \"" + arvoreBestrelaDB + "\"\n");
        }
    }

    /**
     * Metodo para encontrar NoBestrela de insersao de uma chave.
     * @param chave - que sera' inserida.
     * @return noBestrela - no qual deve-se inserir a chave.
     */
    public long encontrarInsercao (int chave) {
        return encontrarInsercao(this, chave, 8);
    }

    /**
     * Metodo privado para encontrar o local de insersao de uma chave em um
     * determinado NoBestrela.
     * @param no - NoBestrela em analise.
     * @param chave - chave que sera' inserida.
     * @param posInserir - posicao para inserir a chave.
     * @return posInserir - endereco de insercao no arquivo.
     */
    private long encontrarInsercao (NoBestrela no, int chave, long posInserir) {
     
        // Procurar o filho, no qual a chave poderia ficar
        int i;
        for(i = 0; (i < no.numElementos) && (chave > no.chave[i]); i++);

        // Se o No nao for folha, continuar recursao
        if (no.noFilho[i] != -1) {
            posInserir = no.noFilho[i];
            no.lerNoB(posInserir);
            posInserir = encontrarInsercao(no, chave, posInserir);
        }

        return posInserir;
    }

    /**
     * Metodo para encontrar a posicao do pai de um NoBestrela.
     * @param posFilho - posicao do filho em arquivo
     * @return posInserir - endereco de insercao no arquivo.
     */
    public long encontrarPai(long posFilho) {

        RandomAccessFile arvoreBFile = null;
        long posicaoPai = -1;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBestrelaDB, "rw");

            // Posicionar ponteiro no inicio do arquivo
            arvoreBFile.seek(0);
            arvoreBFile.readLong();

            long posicaoAtual = arvoreBFile.getFilePointer();
            boolean find = false;

            // Percorrer todo o arquivo
            while(posicaoAtual != arvoreBFile.length() && find == false) {
                NoBestrela tmp = new NoBestrela();
                posicaoPai = posicaoAtual;
                tmp.lerNoB(posicaoAtual);

                for (int i = 0; i < tmp.ordemArvore; i++) {
                    
                    // Encontrar pai procurando a partir do filho
                    if (tmp.noFilho[i] == posFilho) {
                        find = true;
                        i = ordemArvore;
                    }

                }

                // Atualizar ponteiro
                posicaoAtual += tamNoB;
            }

            if (find == false) posicaoPai = -1;

            // Fechar arquivo
            arvoreBFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler o arquivo \"" + arvoreBestrelaDB + "\"\n");
        } finally {
            return posicaoPai;
        }
    }

    /**
     * Metodo para obter o No resultante com as chaves 'a esquerda do No em
     * analise.
     * @return NoEsq - com os elementos menores que a chave do meio.
     */
    public NoBestrela getFilhoEsq() {
        int i = 0;
        int tamNo = (ordemArvore-1)/2;
        NoBestrela noEsq = new NoBestrela();

        // Copiar os primeiros elementos do No
        int pos = i;
        while (pos < tamNo) {
            noEsq.inserir(this.chave[pos], this.endereco[pos], this.noFilho[pos], this.noFilho[pos+1]);
            pos++;
        }

        return noEsq;
    }

    /**
     * Metodo para obter o No resultante com as chaves 'a direita do No em
     * analise.
     * @return NoDir - com os elementos maiores que a chave do meio.
     */
    public NoBestrela getFilhoDir() {
        int i = ordemArvore/2;
        NoBestrela noDir = new NoBestrela();

        // Copiar os ultimos elementos do No
        int pos = i;
        while (pos < ordemArvore-1) {
            noDir.inserir(this.chave[pos], this.endereco[pos], this.noFilho[pos], this.noFilho[pos+1]);
            pos++;
        }

        return noDir;
    }

    /**
     * Metodo para obter o No com a chave do meio do NoBestrela analisado.
     * @return noMeio - com os elementos maiores que a chave do meio.
     */
    public NoBestrela getMeio() {
        int posMeio = ((ordemArvore-1)/2);
        NoBestrela noMeio = new NoBestrela(this.chave[posMeio], this.endereco[posMeio]);

        return noMeio;
    }

    /**
     * Metodo para obter a chave em uma determinada posicao do NoBestrela.
     * @param pos - posicao da chave no NoBestrela.
     * @return chave procurada.
     */
    public int getChave(int pos) {
        int chaveProcurada = -1;
        if (pos < numElementos) {
            chaveProcurada = this.chave[pos];
        }

        return chaveProcurada;
    }

    /**
     * Metodo para obter o endereco em uma determinada posicao do NoBestrela.
     * @param pos - posicao do endereco no NoBestrela.
     * @return endereco procurado.
     */
    public long getEndereco(int pos) {
        long enderecoProcurado = -1;
        if (pos < numElementos) {
            enderecoProcurado = this.endereco[pos];
        }

        return enderecoProcurado;
    }

    /**
     * Metodo para reestruturar os ponteiros para os filhos do NoBestrela alterado.
     * @param newChave - nova chave no NoBestrela adicionada.
     * @param posEsq - posicao NoBestrela filho da esquerda no arquivo.
     * @param posDir - posicao NoBestrela filho da direita no arquivo.
     */
    public void remontarPonteiros(int newChave, long posEsq, long posDir) {

        // Localizar chave recem inserida
        int i;
        for(i = 0; (i < (ordemArvore-1)/2) && (chave[i] != newChave); i++);

        // Adicionar filhos do novo NoBestrela
        noFilho[i] = posEsq;
        noFilho[i+1] = posDir;
    }

    /**
     * Metodo para indicar se o NoBestrela esta' cheio ou nao.
     * @return true, se estiver cheio; false, caso contrario.
     */
    public boolean isFull() {
        return numElementos == ordemArvore-1;
    }

    /**
     * Metodo para indicar se o NoBestrela tem espaco livre para insercao.
     * @return true, se tiver espaco; false, caso contrario.
     */
    public boolean temEspacoLivre() {
        return !isFull();
    }

    /**
     * Metodo para indicar se o NoBestrela e' uma folha da 'arvore.
     * @return true, se for folha; false, caso contrario.
     */
    public boolean isFolha() {
        boolean resp = true;
        for(int i = 0; i <= ordemArvore-1; i++) {
            resp = resp && this.noFilho[i] == -1;
        }

        return resp;
    }

    /**
     * Metodo para sobrescrever o toString do Objeto e converter os atributos
     * da classe NoBestrela em uma string.
     */
    public String toString() {

        String noBestrela = "";

        noBestrela += "(" + String.format("%2d", numElementos) + "): ";

        for(int i = 0; i < ordemArvore-1; i++) {
            noBestrela += "|" + String.format("%8d", noFilho[i]);
            noBestrela += "|" + String.format("%5d", chave[i]);
            noBestrela += "|" + String.format("%8d", endereco[i]);
        }

        noBestrela += "|" + String.format("%8d", noFilho[ordemArvore-1]) + "|";

        return noBestrela;
    }
}
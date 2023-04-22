// Package
package arvores.arvoreB;

// Bibliotecas
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class NoB {

    protected static final int ordemArvore = 8;
    protected static final int tamNoB = 150;

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
     * Costrutor da classe NoB, utilizando passagem de parametros.
     * @param newChave - id da chave a ser inserida no No.
     * @param newEndereco - posicao do id no arquivo "Registro.db".
     * @param filhoEsq - posicao filho 'a esquerda.
     * @param filhoDir - posicao filho 'a direita.
     */
    public NoB(int newChave, long newEndereco, long filhoEsq, long filhoDir) {

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
     * Metodo para clonar um NoB.
     * @return NoB clonado.
     */
    public NoB clone() {

        NoB cloneNoB = new NoB();

        cloneNoB.numElementos = this.numElementos;
        cloneNoB.chave = this.chave;
        cloneNoB.endereco = this.endereco;
        cloneNoB.noFilho = this.noFilho;

        return cloneNoB;
    }


    /**
     * Metodo para criar um NoB em arquivo, como fluxo de bytes.
     * @return fimArquivo - posicao do arquivo que o NoB foi escrito.
     */
    public long escreverNoB() {
        RandomAccessFile arvoreBFile = null;
        long fimArquivo = -1;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBDB, "rw");

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
            System.out.println("\nERRO: " + e.getMessage() + " ao escrever o arquivo \"" + arvoreBDB + "\"\n");
        } finally {
            return fimArquivo;
        }
    }

    /**
     * Metodo para criar um NoB em arquivo, como fluxo de bytes.
     * @param posicaoInserir - posicao de inicio para escrita do NoB.
     */
    public void escreverNoB(long posicaoInserir) {
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

            // Fechar arquivo
            arvoreBFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao escrever o arquivo \"" + arvoreBDB + "\"\n");
        }
    }

    /**
     * Metodo para inserir um NoB em arquivo, como fluxo de bytes.
     * @param posicaoInserir - posicao de inicio para escrita no arquivo.
     * @param newChave - nova chave a se inserir.
     * @param newEndereco - novo endereco a se inserir.
     */
    public void inserir(long posicaoInserir, int newChave, long newEndereco) {
        RandomAccessFile arvoreBFile = null;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBDB, "rw");

            // Inserir o elemento de forma ordenada
            inserir(newChave, newEndereco);

            // Escrever numero de elementos no No
            arvoreBFile.seek(posicaoInserir);
            escreverNoB(posicaoInserir);

            // Fechar arquivo
            arvoreBFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao escrever o arquivo \"" + arvoreBDB + "\"\n");
        }
    }

    /**
     * Metodo para inserir um NoB em arquivo, como fluxo de bytes.
     * @param posicaoInserir - posicao de inicio para escrita no arquivo.
     * @param newChave - nova chave a se inserir.
     * @param newEndereco - novo endereco a se inserir.
     * @param filhoDir - endereco do filho da direita.
     */
    public void inserir(long posicaoInserir, int newChave, long newEndereco, long filhoDir) {
        RandomAccessFile arvoreBFile = null;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBDB, "rw");

            // Inserir o elemento de forma ordenada
            inserir(newChave, newEndereco, filhoDir);

            // Escrever numero de elementos no No
            arvoreBFile.seek(posicaoInserir);
            escreverNoB(posicaoInserir);

            // Fechar arquivo
            arvoreBFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao escrever o arquivo \"" + arvoreBDB + "\"\n");
        }
    }

    /**
     * Metodo para inserir um NoB em arquivo, como fluxo de bytes.
     * @param posicaoInserir - posicao de inicio para escrita no arquivo.
     * @param newChave - nova chave a se inserir.
     * @param newEndereco - novo endereco a se inserir.
     * @param filhoEsq - endereco do filho da esquerda.
     * @param filhoDir - endereco do filho da direita.
     */
    public void inserir(long posicaoInserir, int newChave, long newEndereco, long filhoEsq, long filhoDir) {
        RandomAccessFile arvoreBFile = null;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBDB, "rw");

            // Inserir o elemento de forma ordenada
            inserir(newChave, newEndereco, filhoEsq, filhoDir);

            // Escrever numero de elementos no No
            arvoreBFile.seek(posicaoInserir);
            escreverNoB(posicaoInserir);

            // Fechar arquivo
            arvoreBFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao escrever o arquivo \"" + arvoreBDB + "\"\n");
        }
    }

    /**
     * Metodo para inserir um NoB em arquivo, como fluxo de bytes.
     * @param posicaoInserir - posicao de inicio para escrita no arquivo.
     * @param noInserir - noB com 1 elemento a ser inserido.
     */
    public void inserir(long posicaoInserir, NoB noInserir) {
        RandomAccessFile arvoreBFile = null;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBDB, "rw");

            // Obter informacoes do NoB
            int newChave = noInserir.chave[0];
            long newEndereco = noInserir.endereco[0];
            long filhoEsq = noInserir.noFilho[0];
            long filhoDir = noInserir.noFilho[1];

            // Inserir o elemento de forma ordenada
            if (filhoEsq == -1) {
                inserir(newChave, newEndereco, filhoDir);
            } else {
                inserir(newChave, newEndereco, filhoEsq, filhoDir);
            }
            

            // Escrever numero de elementos no No
            arvoreBFile.seek(posicaoInserir);
            escreverNoB(posicaoInserir);

            // Fechar arquivo
            arvoreBFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao escrever o arquivo \"" + arvoreBDB + "\"\n");
        }
    }

    /**
     * Metodo para inserir uma chave no NoB de forma ordenada, mantendo os 
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
     * Metodo para inserir uma chave no NoB de forma ordenada, mantendo os 
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
     * Metodo para inserir uma chave no NoB de forma ordenada, mantendo os 
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
     * Metodo para ler NoB em arquivo, a apartir de sua posicao de inicio.
     * @param posInicio - posicao de inicio daquele NoB.
     */
    public void lerNoB (long posInicio) {
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

            // Fechar arquivo
            arvoreBFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler o arquivo \"" + arvoreBDB + "\"\n");
        }
    }

    /**
     * Metodo para encontrar NoB de insersao de uma chave.
     * @param chave - que sera' inserida.
     * @param posRaiz - posicao da raiz da arvore
     * @return noB - no qual deve-se inserir a chave.
     */
    public long encontrarInsercao (int chave, long posRaiz) {
        return encontrarInsercao(this, chave, posRaiz);
    }

    /**
     * Metodo privado para encontrar o local de insersao de uma chave em um
     * determinado NoB.
     * @param noB - NoB em analise.
     * @param chaveProcurada - chave que sera' inserida.
     * @param posInserir - posicao para inserir a chave.
     * @return posInserir - endereco de insercao no arquivo.
     */
    private long encontrarInsercao (NoB noB, int chaveProcurada, long posInserir) {

        // Ler NoB desejado
        noB.lerNoB(posInserir);
    
        // Se chave procurada for menor que a primeira
        if ((noB.numElementos > 0) && (chaveProcurada < noB.chave[0])) {

            // Se o No nao for folha, continuar recursao
            if (noB.noFilho[0] != -1) {
                posInserir = noB.noFilho[0];
                noB.lerNoB(posInserir);
                posInserir = encontrarInsercao(noB, chaveProcurada, posInserir);
            }
        
        // Senao, testar se e' maior que a ultima
        } else if ((noB.numElementos > 0) && (chaveProcurada > noB.chave[noB.numElementos-1])) {
            
            // Se o No nao for folha, continuar recursao
            if (noB.noFilho[noB.numElementos] != -1) {
                posInserir = noB.noFilho[numElementos];
                noB.lerNoB(posInserir);
                posInserir = encontrarInsercao(noB, chaveProcurada, posInserir);
            }

        // Senao, procurar valores intermediarios
        } else {

            // Procurar o filho, no qual a chave poderia ficar
            int i;
            for(i = 0; (i < noB.numElementos) && (chaveProcurada > noB.chave[i]); i++);

            // Se o No nao for folha, continuar recursao
            if (noB.noFilho[i] != -1) {
                posInserir = noB.noFilho[i];
                noB.lerNoB(posInserir);
                posInserir = encontrarInsercao(noB, chaveProcurada, posInserir);
            }
        }

        return posInserir;
    }

    /**
     * Metodo para encontrar a posicao do pai de um NoB.
     * @param posFilho - posicao do filho em arquivo
     * @return posInserir - endereco do pai no arquivo.
     */
    public long encontrarPai(long posFilho) {

        RandomAccessFile arvoreBFile = null;
        long posicaoPai = -1;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBDB, "rw");

            // Posicionar ponteiro no inicio do arquivo
            arvoreBFile.seek(0);
            arvoreBFile.readLong();

            long posicaoAtual = arvoreBFile.getFilePointer();
            boolean find = false;

            // Percorrer todo o arquivo
            while(posicaoAtual != arvoreBFile.length() && find == false) {
                NoB tmp = new NoB();
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
            System.out.println("\nERRO: " + e.getMessage() + " ao ler o arquivo \"" + arvoreBDB + "\"\n");
        } finally {
            return posicaoPai;
        }
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
    public NoB getFilhoDir() {
        int i = ordemArvore/2;
        NoB noDir = new NoB();

        // Copiar os ultimos elementos do No
        int pos = i;
        while (pos < ordemArvore-1) {
            noDir.inserir(this.chave[pos], this.endereco[pos], this.noFilho[pos], this.noFilho[pos+1]);
            pos++;
        }

        return noDir;
    }

    /**
     * Metodo para obter o No com a chave do meio do NoB analisado.
     * @return noMeio - com os elementos maiores que a chave do meio.
     */
    public NoB getMeio() {
        int posMeio = ((ordemArvore-1)/2);
        NoB noMeio = new NoB(this.chave[posMeio], this.endereco[posMeio]);

        return noMeio;
    }

    /**
     * Metodo para obter a chave em uma determinada posicao do NoB.
     * @param pos - posicao da chave no NoB.
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
     * Metodo para obter o endereco em uma determinada posicao do NoB.
     * @param pos - posicao do endereco no NoB.
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
     * Metodo para reestruturar os ponteiros para os filhos do NoB alterado.
     * @param newChave - nova chave no NoB adicionada.
     * @param posEsq - posicao NoB filho da esquerda no arquivo.
     * @param posDir - posicao NoB filho da direita no arquivo.
     */
    public void remontarPonteiros(int newChave, long posEsq, long posDir) {

        // Localizar chave recem inserida
        int i;
        for(i = 0; (i < (ordemArvore-1)/2) && (chave[i] != newChave); i++);

        // Adicionar filhos do novo NoB
        noFilho[i] = posEsq;
        noFilho[i+1] = posDir;
    }

    /**
     * Metodo para reestruturar os ponteiros para os filhos do NoB alterado.
     * @param newChave - nova chave no NoB adicionada.
     * @param posDir - posicao NoB filho da direita no arquivo.
     */
    public void remontarPonteiros(int newChave, long posDir) {

        // Localizar chave recem inserida
        int i;
        for(i = 0; (i < (ordemArvore-1)/2) && (chave[i] != newChave); i++);

        // Adicionar filho direita do novo NoB
        noFilho[i] = -1;
        noFilho[i+1] = posDir;
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

    /**
     * Metodo para sobrescrever o toString do Objeto e converter os atributos
     * da classe NoB em uma string.
     */
    public String toString() {

        String noB = "";

        noB += "(" + String.format("%2d", numElementos) + "): ";

        for(int i = 0; i < ordemArvore-1; i++) {
            noB += "|" + String.format("%8d", noFilho[i]);
            noB += "|" + String.format("%5d", chave[i]);
            noB += "|" + String.format("%8d", endereco[i]);
        }

        noB += "|" + String.format("%8d", noFilho[ordemArvore-1]) + "|";

        return noB;
    }

    /**
     * Metodo para determinar se, ao retirar um elemento, o NoB permanecera'
     * com 50% de ocupacao, e, assim, pode-se remover um elemento.
     * @return true, se puder remover um elemento; false, caso contrario.
     */
    public boolean isMaisMetade() {
        int novoNumElementos = numElementos-1;
        int metadeNoB = (ordemArvore-1)/2;

        return novoNumElementos >= metadeNoB;
    }

    /**
     * Metodo para realizar um deslocamento para a esquerda dos elementos.
     * @param pos - posicao de inicio do deslocamento.
     * @param posArquivo - posicao do NoB na arvore
     */
    public void remanejarRegistros(int pos, long posArquivo) {

        // Shift para esquerda dos elementos, incluindo os null
        for(int i = pos; i < (ordemArvore-1)-1; i++) {
            chave[i] = chave[i+1];
            endereco[i] = endereco[i+1];
            noFilho[i+1] = noFilho[i+2];
        }

        chave[(ordemArvore-1)-1] = -1;
        endereco[(ordemArvore-1)-1] = -1;
        noFilho[ordemArvore-1] = -1;

        // Atualizar numero de elementos
        numElementos--;

        // Salvar no arquivo
        escreverNoB(posArquivo);
    }

    public long encontrarIrmaoDir(long posPai, int chaveProcurada) {
        RandomAccessFile arvoreBFile = null;

        long posIrmao = -1;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBDB, "rw");

            // Encontrar pai
            lerNoB(posPai);

            // Testar se nao tem irmao 'a direita
            if (chave[numElementos-1] < chaveProcurada) {
                this.numElementos = 0;

            // Se tiver, pode-se procurar
            } else {

                // Encontrar posicao do irmao
                int i;
                for(i = 0; (i < numElementos) && (chave[i] < chaveProcurada); i++);
                posIrmao = noFilho[i+1];

                // Encontrar irmao da direita
                lerNoB(posIrmao);
            }

            // Fechar arquivo
            arvoreBFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler o arquivo \"" + arvoreBDB + "\"\n");
        } finally {
            return posIrmao;
        } 
    }

    public long encontrarIrmaoEsq(long posPai, int chaveProcurada) {
        RandomAccessFile arvoreBFile = null;

        long posIrmao = -1;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBDB, "rw");

            // Encontrar pai
            lerNoB(posPai);

            // Testar se nao tem irmao 'a esquerda
            if (chave[0] > chaveProcurada) {
                this.numElementos = 0;

            // Se tiver, pode-se procurar
            } else {

                // Encontrar posicao do irmao
                int i;
                for(i = 0; (i < numElementos) && (chave[i] < chaveProcurada); i++);
                posIrmao = noFilho[i];

                // Encontrar irmao da esquerda
                lerNoB(posIrmao);
            }

            // Fechar arquivo
            arvoreBFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler o arquivo \"" + arvoreBDB + "\"\n");
        } finally {
            return posIrmao;
        }   
    }

    public void deletarNo(long posicaoDeletar) {
        RandomAccessFile arvoreBFile = null;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBDB, "rw");

            // Settar valores como null
            NoB noNull = new NoB();

            // Escrever noB nulo
            noNull.escreverNoB(posicaoDeletar);

            // Fechar arquivo
            arvoreBFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler o arquivo \"" + arvoreBDB + "\"\n");
        }
    }

    /**
     * Metodo para trocar dois elementos em um No, sem alterar os ponteiros.
     * @param posAntiga - posicao de inicio para escrita no arquivo.
     * @param chaveAntiga - chave a se apagar.
     * @param enderecoAntigo - endereco a se apagar.
     * @param posNova - posicao de inicio para escrita no arquivo novo.
     * @param chaveNova - nova chave a se inserir.
     * @param enderecoNovo - novo endereco a se inserir.
     */
    public void swap(long posAntiga, int chaveAntiga, long enderecoAntigo, 
                     long posNova,   int chaveNova,   long enderecoNovo) {
        RandomAccessFile arvoreBFile = null;

        NoB noAux = new NoB();
            
        // Ler primeiro No
        noAux.lerNoB(posAntiga);

        // Localizar local de insercao no No
        int i;
        for(i = 0; (i < noAux.numElementos) && (noAux.chave[i] < chaveAntiga); i++);

        // Trocar elementos
        noAux.chave[i] = chaveNova;
        noAux.endereco[i] = enderecoNovo;

        // Salvar em arquivo
        noAux.escreverNoB(posAntiga);

        // Ler segundo No
        noAux.lerNoB(posNova);

        // Localizar local de insercao no No
        for(i = 0; (i < noAux.numElementos) && (noAux.chave[i] < chaveNova); i++);

        // Trocar elementos
        noAux.chave[i] = chaveAntiga;
        noAux.endereco[i] = enderecoAntigo;

        // Salvar em arquivo
        noAux.escreverNoB(posNova);
    }
}
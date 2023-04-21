// Package
package arvores.arvoreBmais;

// Bibliotecas
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class NoBmais {

    protected static final int ordemArvore = 8;
    protected static final int tamNoBmais = 150;

    protected short numElementos;
    protected int chave[];
    protected long endereco[];
    protected long noFilho[];
    protected long folhaDir;

    private static final String arvoreBmaisDB = "./src/resources/ArvoreBmais.db";

    /**
     * Construtor padrao da classe NoBmais+.
     */
    public NoBmais() {

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
        folhaDir = -1;
    }

    /**
     * Costrutor da classe NoBmais, utilizando passagem de parametros.
     * @param newChave - id da chave a ser inserida no No.
     * @param newEndereco - posicao do id no arquivo "Registro.db".
     */
    public NoBmais(int newChave, long newEndereco) {

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
        folhaDir = -1;

        numElementos = 1;
        chave[0] = newChave;
        endereco[0] = newEndereco;
    }

    /**
     * Costrutor da classe NoBmais, utilizando passagem de parametros.
     * @param newChave - id da chave a ser inserida no No.
     * @param newEndereco - posicao do id no arquivo "Registro.db".
     * @param filhoEsq - posicao filho 'a esquerda.
     * @param filhoDir - posicao filho 'a direita.
     */
    public NoBmais(int newChave, long newEndereco, long filhoEsq, long filhoDir) {

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
        folhaDir = -1;

        numElementos = 1;
        chave[0] = newChave;
        endereco[0] = newEndereco;
        noFilho[0] = filhoEsq;
        noFilho[1] = filhoDir;

    }

    /**
     * Metodo para clonar um NoBmais.
     * @return NoBmais clonado.
     */
    public NoBmais clone() {

        NoBmais cloneNoBmais = new NoBmais();

        cloneNoBmais.numElementos = this.numElementos;
        cloneNoBmais.chave = this.chave;
        cloneNoBmais.endereco = this.endereco;
        cloneNoBmais.noFilho = this.noFilho;
        cloneNoBmais.folhaDir = this.folhaDir;

        return cloneNoBmais;
    }


    /**
     * Metodo para criar um NoBmais em arquivo, como fluxo de bytes.
     * @return fimArquivo - posicao do arquivo que o NoBmais foi escrito.
     */
    public long escreverNoB() {
        RandomAccessFile arvoreBFile = null;
        long fimArquivo = -1;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBmaisDB, "rw");

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

            // Escrever ponteiro para folha 'a direita
            byte[] folhaDirBytes = ByteBuffer.allocate(8).putLong(folhaDir).array();
            arvoreBFile.write(folhaDirBytes);

            // Fechar arquivo
            arvoreBFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao escrever o arquivo \"" + arvoreBmaisDB + "\"\n");
        } finally {
            return fimArquivo;
        }
    }

    /**
     * Metodo para criar um NoBmais em arquivo, como fluxo de bytes.
     * @param posicaoInserir - posicao de inicio para escrita do NoBmais.
     */
    public void escreverNoB(long posicaoInserir) {
        RandomAccessFile arvoreBFile = null;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBmaisDB, "rw");

            // Posicionar ponteiro no local de inicio do NoBmais.
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

            // Escrever ponteiro para folha 'a direita
            byte[] folhaDirBytes = ByteBuffer.allocate(8).putLong(folhaDir).array();
            arvoreBFile.write(folhaDirBytes);

            // Fechar arquivo
            arvoreBFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao escrever o arquivo \"" + arvoreBmaisDB + "\"\n");
        }
    }

    /**
     * Metodo para inserir um NoBmais em arquivo, como fluxo de bytes.
     * @param posicaoInserir - posicao de inicio para escrita no arquivo.
     * @param newChave - nova chave a se inserir.
     * @param newEndereco - novo endereco a se inserir.
     */
    public void inserir(long posicaoInserir, int newChave, long newEndereco) {
        RandomAccessFile arvoreBFile = null;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBmaisDB, "rw");

            // Inserir o elemento de forma ordenada
            inserir(newChave, newEndereco);

            // Escrever numero de elementos no No
            arvoreBFile.seek(posicaoInserir);
            escreverNoB(posicaoInserir);

            // Fechar arquivo
            arvoreBFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao escrever o arquivo \"" + arvoreBmaisDB + "\"\n");
        }
    }

    /**
     * Metodo para inserir um NoBmais em arquivo, como fluxo de bytes.
     * @param posicaoInserir - posicao de inicio para escrita no arquivo.
     * @param newChave - nova chave a se inserir.
     * @param newEndereco - novo endereco a se inserir.
     * @param filhoDir - endereco do filho da direita.
     */
    public void inserir(long posicaoInserir, int newChave, long newEndereco, long filhoDir) {
        RandomAccessFile arvoreBFile = null;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBmaisDB, "rw");

            // Inserir o elemento de forma ordenada
            inserir(newChave, newEndereco, filhoDir);

            // Escrever numero de elementos no No
            arvoreBFile.seek(posicaoInserir);
            escreverNoB(posicaoInserir);

            // Fechar arquivo
            arvoreBFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao escrever o arquivo \"" + arvoreBmaisDB + "\"\n");
        }
    }

    /**
     * Metodo para inserir um NoBmais em arquivo, como fluxo de bytes.
     * @param posicaoInserir - posicao de inicio para escrita no arquivo.
     * @param newChave - nova chave a se inserir.
     * @param newEndereco - novo endereco a se inserir.
     * @param filhoEsq - endereco do filho da esquerda.
     * @param filhoDir - endereco do filho da direita.
     */
    public void inserir(long posicaoInserir, int newChave, long newEndereco, long filhoEsq, long filhoDir) {
        RandomAccessFile arvoreBFile = null;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBmaisDB, "rw");

            // Inserir o elemento de forma ordenada
            inserir(newChave, newEndereco, filhoEsq, filhoDir);

            // Escrever numero de elementos no No
            arvoreBFile.seek(posicaoInserir);
            escreverNoB(posicaoInserir);

            // Fechar arquivo
            arvoreBFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao escrever o arquivo \"" + arvoreBmaisDB + "\"\n");
        }
    }

    /**
     * Metodo para inserir um NoBmais em arquivo, como fluxo de bytes.
     * @param posicaoInserir - posicao de inicio para escrita no arquivo.
     * @param noInserir - noBmais com 1 elemento a ser inserido.
     */
    public void inserir(long posicaoInserir, NoBmais noInserir) {
        RandomAccessFile arvoreBFile = null;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBmaisDB, "rw");

            // Obter informacoes do NoBmais
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
            System.out.println("\nERRO: " + e.getMessage() + " ao escrever o arquivo \"" + arvoreBmaisDB + "\"\n");
        }
    }

    /**
     * Metodo para inserir uma chave no NoBmais de forma ordenada, mantendo os 
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
     * Metodo para inserir uma chave no NoBmais de forma ordenada, mantendo os 
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
     * Metodo para inserir uma chave no NoBmais de forma ordenada, mantendo os 
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
     * Metodo para ler NoBmais em arquivo, a apartir de sua posicao de inicio.
     * @param posInicio - posicao de inicio daquele NoBmais.
     */
    public void lerNoB (long posInicio) {
        RandomAccessFile arvoreBFile = null;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBmaisDB, "rw");

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

            // Ler ponteiro para folha 'a direita
            folhaDir = arvoreBFile.readLong();

            // Fechar arquivo
            arvoreBFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler o arquivo \"" + arvoreBmaisDB + "\"\n");
        }
    }

    /**
     * Metodo para encontrar NoBmais de insersao de uma chave.
     * @param chave - que sera' inserida.
     * @param posRaiz - posicao da raiz da arvore
     * @return noBmais - no qual deve-se inserir a chave.
     */
    public long encontrarInsercao (int chave, long posRaiz) {
        return encontrarInsercao(this, chave, posRaiz);
    }

    /**
     * Metodo privado para encontrar o local de insersao de uma chave em um
     * determinado NoBmais.
     * @param noBmais - NoBmais em analise.
     * @param chaveProcurada - chave que sera' inserida.
     * @param posInserir - posicao para inserir a chave.
     * @return posInserir - endereco de insercao no arquivo.
     */
    private long encontrarInsercao (NoBmais noBmais, int chaveProcurada, long posInserir) {

        // Ler NoBmais desejado
        noBmais.lerNoB(posInserir);
     
        // Se chave procurada for menor que a primeira
        if ((noBmais.numElementos > 0) && (chaveProcurada < noBmais.chave[0])) {

            // Se o No nao for folha, continuar recursao
            if (noBmais.noFilho[0] != -1) {
                posInserir = noBmais.noFilho[0];
                noBmais.lerNoB(posInserir);
                posInserir = encontrarInsercao(noBmais, chaveProcurada, posInserir);
            }
        
        // Senao, testar se e' maior que a ultima
        } else if ((noBmais.numElementos > 0) && (chaveProcurada > noBmais.chave[noBmais.numElementos-1])) {
            
            // Se o No nao for folha, continuar recursao
            if (noBmais.noFilho[noBmais.numElementos] != -1) {
                posInserir = noBmais.noFilho[numElementos];
                noBmais.lerNoB(posInserir);
                posInserir = encontrarInsercao(noBmais, chaveProcurada, posInserir);
            }

        // Senao, procurar valores intermediarios
        } else {

            // Procurar o filho, no qual a chave poderia ficar
            int i;
            for(i = 0; (i < noBmais.numElementos) && (chaveProcurada > noBmais.chave[i]); i++);

            // Se o No nao for folha, continuar recursao
            if (noBmais.noFilho[i] != -1) {
                posInserir = noBmais.noFilho[i];
                noBmais.lerNoB(posInserir);
                posInserir = encontrarInsercao(noBmais, chaveProcurada, posInserir);
            }
        }

        return posInserir;
    }

    /**
     * Metodo para encontrar a posicao do pai de um NoBmais.
     * @param posFilho - posicao do filho em arquivo
     * @return posInserir - endereco de insercao no arquivo.
     */
    public long encontrarPai(long posFilho) {

        RandomAccessFile arvoreBFile = null;
        long posicaoPai = -1;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBmaisDB, "rw");

            // Posicionar ponteiro no inicio do arquivo
            arvoreBFile.seek(0);
            arvoreBFile.readLong();

            long posicaoAtual = arvoreBFile.getFilePointer();
            boolean find = false;

            // Percorrer todo o arquivo
            while(posicaoAtual != arvoreBFile.length() && find == false) {
                NoBmais tmp = new NoBmais();
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
                posicaoAtual += tamNoBmais;
            }

            if (find == false) posicaoPai = -1;

            // Fechar arquivo
            arvoreBFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler o arquivo \"" + arvoreBmaisDB + "\"\n");
        } finally {
            return posicaoPai;
        }
    }

    /**
     * Metodo para obter o No resultante com as chaves 'a esquerda do No em
     * analise.
     * @return NoEsq - com os elementos menores que a chave do meio.
     */
    public NoBmais getFilhoEsq() {
        int i = 0;
        int tamNo = (ordemArvore-1)/2;
        NoBmais noEsq = new NoBmais();

        // Copiar os primeiros elementos do No
        int pos = i;
        while (pos < tamNo) {
            noEsq.inserir(this.chave[pos], this.endereco[pos], this.noFilho[pos], this.noFilho[pos+1]);
            pos++;
        }

        return noEsq;
    }

    /**
     * Metodo para obter o No resultante com as chaves 'a esquerda do No em
     * analise.
     * @param folhaDireita - ponteiro para a folha 'a direita
     * @return NoEsq - com os elementos menores que a chave do meio.
     */
    public NoBmais getFilhoEsq(long folhaDireita) {
        int i = 0;
        int tamNo = (ordemArvore-1)/2;
        NoBmais noEsq = new NoBmais();

        // Copiar os primeiros elementos do No
        int pos = i;
        while (pos < tamNo) {
            noEsq.inserir(this.chave[pos], this.endereco[pos], this.noFilho[pos], this.noFilho[pos+1]);
            pos++;
        }

        // Copiar ponteiro para a proxima folha irma
        folhaDir = folhaDireita;

        return noEsq;
    }

    /**
     * Metodo para obter o No resultante com as chaves 'a direita do No em
     * analise.
     * @return NoDir - com os elementos maiores que a chave do meio.
     */
    public NoBmais getFilhoDir() {
        int i = ordemArvore/2;
        NoBmais noDir = new NoBmais();

        // Copiar os ultimos elementos do No
        int pos = i;
        while (pos < ordemArvore-1) {
            noDir.inserir(this.chave[pos], this.endereco[pos], this.noFilho[pos], this.noFilho[pos+1]);
            pos++;
        }

        return noDir;
    }

    /**
     * Metodo para obter o No com a chave do meio do NoBmais analisado.
     * @return noMeio - com os elementos maiores que a chave do meio.
     */
    public NoBmais getMeio() {
        int posMeio = ((ordemArvore-1)/2);
        NoBmais noMeio = new NoBmais(this.chave[posMeio], this.endereco[posMeio]);

        return noMeio;
    }

    /**
     * Metodo para obter a chave em uma determinada posicao do NoBmais.
     * @param pos - posicao da chave no NoBmais.
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
     * Metodo para obter o endereco em uma determinada posicao do NoBmais.
     * @param pos - posicao do endereco no NoBmais.
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
     * Metodo para reestruturar os ponteiros para os filhos do NoBmais alterado.
     * @param newChave - nova chave no NoBmais adicionada.
     * @param posEsq - posicao NoBmais filho da esquerda no arquivo.
     * @param posDir - posicao NoBmais filho da direita no arquivo.
     */
    public void remontarPonteiros(int newChave, long posEsq, long posDir) {

        // Localizar chave recem inserida
        int i;
        for(i = 0; (i < (ordemArvore-1)/2) && (chave[i] != newChave); i++);

        // Adicionar filhos do novo NoBmais
        noFilho[i] = posEsq;
        noFilho[i+1] = posDir;
    }

    /**
     * Metodo para reestruturar os ponteiros para os filhos do NoBmais alterado.
     * @param newChave - nova chave no NoBmais adicionada.
     * @param posDir - posicao NoBmais filho da direita no arquivo.
     */
    public void remontarPonteiros(int newChave, long posDir) {

        // Localizar chave recem inserida
        int i;
        for(i = 0; (i < (ordemArvore-1)/2) && (chave[i] != newChave); i++);

        // Adicionar filho direita do novo NoBmais
        noFilho[i] = -1;
        noFilho[i+1] = posDir;
    }

    /**
     * Metodo para indicar se o NoBmais esta' cheio ou nao.
     * @return true, se estiver cheio; false, caso contrario.
     */
    public boolean isFull() {
        return numElementos == ordemArvore-1;
    }

    /**
     * Metodo para indicar se o NoBmais tem espaco livre para insercao.
     * @return true, se tiver espaco; false, caso contrario.
     */
    public boolean temEspacoLivre() {
        return !isFull();
    }

    /**
     * Metodo para indicar se o NoBmais e' uma folha da 'arvore.
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
     * da classe NoBmais em uma string.
     */
    public String toString() {

        String noBmais = "";

        noBmais += "(" + String.format("%2d", numElementos) + "): ";

        for(int i = 0; i < ordemArvore-1; i++) {
            noBmais += "|" + String.format("%8d", noFilho[i]);
            noBmais += "|" + String.format("%5d", chave[i]);
            noBmais += "|" + String.format("%8d", endereco[i]);
        }

        noBmais += "|" + String.format("%8d", noFilho[ordemArvore-1]);
        noBmais += "|" + String.format("%8d", folhaDir) + "|";

        return noBmais;
    }
}
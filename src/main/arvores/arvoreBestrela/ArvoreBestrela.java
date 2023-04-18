// Package
package arvores.arvoreBestrela;

// Bibliotecas
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

// Bibliotecas proprias
import app.*;

public class ArvoreBestrela {

    protected NoBestrela raiz;
    private static final String arvoreBDB = "./src/resources/ArvoreBestrela.db";

    /**
     * Construtor padrao da classe ArvoreBestrela
     */
    public ArvoreBestrela() {
        raiz = new NoBestrela();
    }

    /**
     * Metodo para inicializar o arquivo "Arvore.db", inicializando a raiz.
     */
    public void inicializarArvoreB() {
        RandomAccessFile arvoreBFile = null;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBDB, "rw");

            // Posicionar ponteiro no inicio do arquivo
            arvoreBFile.seek(0);

            // Escrever posicao da raiz (proximos 8 bytes)
            long posRaiz = 8;
            byte[] posRaizBytes = ByteBuffer.allocate(8).putLong(posRaiz).array();
            arvoreBFile.write(posRaizBytes);

            // Escrever raiz no arquivo
            raiz.escreverNoB();

            // Fechar arquivo
            arvoreBFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao escrever o arquivo \"" + arvoreBDB + "\"\n");
        }
    }

    /**
     * Metodo para obter posicao da raiz no arquivo de 'arvore
     */
    private long getPosRaiz() {
        RandomAccessFile arvoreBFile = null;
        long posRaiz = -1;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBDB, "rw");

            // Ler posicao da raiz
            posRaiz = arvoreBFile.readLong();

            // Fechar arquivo
            arvoreBFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler o arquivo \"" + arvoreBDB + "\"\n");
        } finally {
            return posRaiz;
        }
    }

    /**
     * Metodo para inserir uma chave de pesquisa na arvore B.
     * @param musica - musica a se inserir.
     * @param newEndereco - endereco da musica no arquivo "Registro.db".
     */
    public void inserir(Musica musica, long newEndereco) {
        raiz = inserir(raiz, getPosRaiz(), musica, newEndereco, -1, -1);
    }

    /**
     * Metodo para inserir uma chave de pesquisa na arvore B.
     * @param noBestrela - no em analise.
     * @param posArvore - posicao do No na 'arvoreB
     * @param musica - musica a se inserir.
     * @param newEndereco - endereco da musica no arquivo "Registro.db".
     * @param filhoEsq - posicao filho 'a esquerda que esta' sendo inserido.
     * @param filhoDir - posicao filho 'a direita que esta' sendo inserido.
     * @return novo No.
     */
    private NoBestrela inserir(NoBestrela noBestrela, long posArvore, Musica musica, long newEndereco, long filhoEsq, long filhoDir) {
        RandomAccessFile arvoreBFile = null;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBDB, "rw");

            // Obter chave de insercao
            int newChave = musica.getId();

            // Obter raiz
            arvoreBFile.seek(0);
            long posRaiz = arvoreBFile.readLong();

            // Localizar e ler NoBestrela
            arvoreBFile.seek(posArvore);
            noBestrela.lerNoB(posArvore);

            // Testar se No tem espaco livre para insercao e e' folha
            if (noBestrela.temEspacoLivre() && noBestrela.isFolha()) {
                noBestrela.inserir(posArvore, newChave, newEndereco);
            
            // Se nao for folha, testar se chave ja pertencia 'a arvore anteriormente
            } else if (noBestrela.temEspacoLivre() && (filhoEsq != -1 || filhoDir != -1)) {
                noBestrela.inserir(posArvore, newChave, newEndereco, filhoEsq, filhoDir);

            // Se nao couber na folha, deve-se procurar No de insercao
            } else {
                long posInserir = posArvore;

                // Atualizar NoBestrela caso nao esteja fazendo o split recursivo
                if ((filhoEsq == -1 && filhoDir == -1)) {
                    posInserir = noBestrela.encontrarInsercao(newChave);
                    noBestrela.lerNoB(posInserir);
                }

                // Se couber no NoBestrela, basta inserir
                if (noBestrela.temEspacoLivre()) {
                    noBestrela.inserir(posInserir, newChave, newEndereco, filhoEsq, filhoDir);
                
                // Se for raiz, basta criar novos dois filhos (split na raiz)
                } else if(posInserir == posRaiz) {

                    // Separar filhos direita e esquerda do No
                    NoBestrela noEsq = noBestrela.getFilhoEsq();
                    NoBestrela noDir = noBestrela.getFilhoDir();

                    // Escrever novos NoBestrela em arquivo
                    long posEsq = noEsq.escreverNoB();
                    long posDir = noDir.escreverNoB();

                    // Separar elemento do meio para "subir"
                    noBestrela = noBestrela.getMeio();
                    int chave = noBestrela.getChave(0);

                    // Alterar NoBestrela pai
                    noBestrela.remontarPonteiros(chave, posEsq, posDir);

                    // Reescrever NoBestrela que sofreu o split
                    noBestrela.escreverNoB(posInserir);

                    // Se estiver inserindo chave completamente nova
                    if ((filhoEsq == -1 && filhoDir == -1)) {
                       posInserir = noBestrela.encontrarInsercao(newChave);
                    
                    // Senao, significa que nova posicao e' no final de arquivo
                    } else {
                        posInserir = posDir;
                    }

                    // Inserir nova chave
                    inserir(noBestrela, posInserir, musica, newEndereco, filhoEsq, filhoDir);
                
                // Senao, deve-se dividir o No
                } else {

                    // Copiar NoBestrela atual
                    NoBestrela tmp = noBestrela.clone();

                    // Como sera' inserido sequencialmente, o nextID > thisID
                    // Alterar NoBestrela atual para filho da esquerda
                    noBestrela = tmp.getFilhoEsq();

                    // Separar filho da direita
                    NoBestrela noDir = tmp.getFilhoDir();

                    // Obter NoBestrela que devera' "subir"
                    NoBestrela noMeio = tmp.getMeio();
                    int chave = noMeio.getChave(0);
                    long endereco = noMeio.getEndereco(0);

                    // Testar se No pai esta' com espaco livre
                    NoBestrela noPai = new NoBestrela();
                    long newPos = noPai.encontrarPai(posInserir);
                    noPai.lerNoB(newPos);
                    
                    // Se tiver espaco, basta inserir
                    if (noPai.temEspacoLivre()) {

                        // Alterar filho da esquerda em arquivo
                        noBestrela.escreverNoB(posInserir);

                        // Inserir novo filho direita
                        long posDir = noDir.escreverNoB();

                        // Incluir chave que subiu no arquivo
                        noPai.inserir(newPos, chave, endereco, posDir);

                        // Inserir nova chave
                        inserir(musica, newEndereco);
                    
                    // Se No pai estiver cheio, deve-se fazer o split no pai
                    } else {

                        // Gerar musica fake com id para insercao
                        int chaveSplit = noPai.getChave((noPai.ordemArvore-1)/2);
                        Musica musicaSplit = new Musica();
                        musicaSplit.setId(chaveSplit);
                        long enderecoSplit = noPai.getEndereco((noPai.ordemArvore-1)/2);

                        // Testar se NoBestrela gerado e' raiz
                        // Se for, deve-se altera-la em arquivo
                        if(newPos == posRaiz) {

                            // Inserir novo filho 'a direita da pagina cheia
                            NoBestrela newNoDir = noPai.getFilhoDir();
                            long posDir = newNoDir.escreverNoB();

                            // Sobrescrever filho esquerdo na raiz antiga
                            NoBestrela newNoEsq = noPai.getFilhoEsq();
                            long posEsq = posRaiz;
                            newNoEsq.escreverNoB(posEsq);

                            // Escrever nova raiz
                            NoBestrela newRaiz = new NoBestrela(chaveSplit, enderecoSplit, posEsq, posDir);
                            long posNewRaiz = newRaiz.escreverNoB();

                            // Alterar ponteiro para nova raiz
                            arvoreBFile.seek(0);
                            byte[] posRaizBytes = ByteBuffer.allocate(8).putLong(posNewRaiz).array();
                            arvoreBFile.write(posRaizBytes);

                            // Inserir nova chave
                            inserir(musica, newEndereco);
                        
                        } else {

                            // Inserir novo filho 'a direita da pagina cheia
                            NoBestrela newNoDir = noPai.getFilhoDir();
                            long posDir = newNoDir.escreverNoB();

                            // Sobrescrever filho esquerdo no NoBestrela antigo
                            NoBestrela newNoEsq = noPai.getFilhoEsq();
                            long posEsq = newPos;
                            newNoEsq.escreverNoB(posEsq);

                            // Obter posicao do pai
                            posInserir = noPai.encontrarPai(newPos);

                            // Inserir no pai do pai a chave que sofreu split
                            inserir(tmp, posInserir, musicaSplit, enderecoSplit, posEsq, posDir);

                            // Inserir nova chave
                            inserir(musica, newEndereco);
                        }

                                  
                    }
                }
            }

            // Fechar arquivo
            arvoreBFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler/escrever o arquivo \"" + arvoreBDB + "\"\n");
        } finally {
            return noBestrela;
        }
    }

    /**
     * Metodo para procurar uma chave na arvore.
     * @param chaveProcurada - id da chave que se deseja encontrar.
     * @return posicao da chave no arquivo "Registro.db".
     */
    public long read(int chaveProcurada) {
        return read(getPosRaiz(), chaveProcurada);
    }

    /**
     * Metodo para procurar uma chave na arvore.
     * @param pos - posicao a ser analisada se a chava esta' inclusa ou nao.
     * @param chaveProcurada - id da chave que se deseja encontrar.
     * @return posicao da chave no arquivo "Registro.db".
     */
    private long read(long pos, int chaveProcurada) {

        long endereco = -1;

        // Ler NoBestrela desejado
        NoBestrela noBestrela = new NoBestrela();
        noBestrela.lerNoB(pos);

        // Procurar possivel local da chave
        int i;

        // Se chave procurada for menor que a primeira
        if ((noBestrela.numElementos > 0) && (chaveProcurada < noBestrela.chave[0])) {
            endereco = read(noBestrela.noFilho[0], chaveProcurada);
        
        // Senao, testar o ultimo
        } else if ((noBestrela.numElementos > 0) && (chaveProcurada > noBestrela.chave[noBestrela.numElementos-1])) {
            endereco = read(noBestrela.noFilho[noBestrela.numElementos], chaveProcurada);

        // Senao, procurar valores intermediarios
        } else {
            for(i = 0; (i < noBestrela.numElementos) && (noBestrela.chave[i] < chaveProcurada); i++);

            // Testar se chave foi encontrada
            if(noBestrela.chave[i] == chaveProcurada) {
                endereco = noBestrela.endereco[i];

            } else {
                endereco = read(noBestrela.noFilho[i+1], chaveProcurada);
            }
        }

        return endereco;
    }

    public void mostrarArquivo() {
        RandomAccessFile arvoreBFile = null;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBDB, "rw");

            System.out.print("Pos [" + String.format("%8d", 0) + "]: raiz: ");
            long posRaiz = arvoreBFile.readLong();
            long posAtual = arvoreBFile.getFilePointer();
            System.out.println("|" + String.format("%8d", posRaiz) + "|");

            // Percorrer todo arquivo
            while(posAtual != arvoreBFile.length()) {
                
                // Mostrar posicao atual do arquivo
                System.out.print("Pos [" + String.format("%8d", posAtual) + "]: ");

                // Ler NoBestrela e mostrar
                NoBestrela aux = new NoBestrela();

                // Posicionar ponteiro na posicao de inicio do No
                arvoreBFile.seek(posAtual);

                // Ler numero de elementos no No
                aux.numElementos = arvoreBFile.readShort();

                // Ler informacoes do No
                for (int i = 0; i < aux.ordemArvore-1; i++) {
                    
                    // Ler ponteiro para filho da esquerda da posicao i
                    aux.noFilho[i] = arvoreBFile.readLong();

                    // Ler chave na posicao i
                    aux.chave[i] = arvoreBFile.readInt();

                    // Ler endereco na posicao i para  "Registro.db"
                    aux.endereco[i] = arvoreBFile.readLong();
                }

                // Ler ultimo ponteiro 'a direita
                aux.noFilho[aux.ordemArvore-1] = arvoreBFile.readLong();
                posAtual = arvoreBFile.getFilePointer();

                //Printar
                System.out.println(aux);
            }
            System.out.println();
            
            // Fechar arquivo
            arvoreBFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler o arquivo \"" + arvoreBDB + "\"\n");
        }
    }

    /**
     * Metodo para obter o total de chaves que estao, de fato, inseridas na
     * arvore.
     * @return total de chaves inseridas.
     */
    public int contarChaves() {
        RandomAccessFile arvoreBFile = null;
        int total = 0;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBDB, "rw");

            long posRaiz = arvoreBFile.readLong();
            long posAtual = arvoreBFile.getFilePointer();

            // Percorrer todo arquivo
            while(posAtual != arvoreBFile.length()) {
                

                // Ler NoBestrela e mostrar
                NoBestrela aux = new NoBestrela();

                // Posicionar ponteiro na posicao de inicio do No
                arvoreBFile.seek(posAtual);

                // Ler numero de elementos no No
                aux.numElementos = arvoreBFile.readShort();
                total += aux.numElementos;

                // Ler informacoes do No
                for (int i = 0; i < aux.ordemArvore-1; i++) {
                    
                    // Ler ponteiro para filho da esquerda da posicao i
                    aux.noFilho[i] = arvoreBFile.readLong();

                    // Ler chave na posicao i
                    aux.chave[i] = arvoreBFile.readInt();

                    // Ler endereco na posicao i para  "Registro.db"
                    aux.endereco[i] = arvoreBFile.readLong();
                }

                // Ler ultimo ponteiro 'a direita
                aux.noFilho[aux.ordemArvore-1] = arvoreBFile.readLong();

                posAtual = arvoreBFile.getFilePointer();
            }
            
            // Fechar arquivo
            arvoreBFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler o arquivo \"" + arvoreBDB + "\"\n");
        } finally {
            return total;
        }
    }

    private void caminhar(long pos) {
        if (pos != -1) {
            NoBestrela no = new NoBestrela();
            no.lerNoB(pos);

            int i;
            for(i = 0; i < no.numElementos; i++) {
                if(no.noFilho[i] != -1) {
                    caminhar(no.noFilho[i]);
                }
                System.out.println(no.chave[i]);
            }

            if (no.noFilho[i] != -1) {
                caminhar(no.noFilho[i]);
            }
        }
    }

    public void caminhar() {
        System.out.println("In order:");
        caminhar(getPosRaiz());

    }

}
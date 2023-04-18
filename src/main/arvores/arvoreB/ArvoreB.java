// Package
package arvores.arvoreB;

// Bibliotecas
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

// Bibliotecas proprias
import app.*;

public class ArvoreB {

    protected NoB raiz;
    private static final String arvoreBDB = "./src/resources/ArvoreB.db";

    /**
     * Construtor padrao da classe ArvoreB
     */
    public ArvoreB() {
        raiz = new NoB();
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
     * @param noB - no em analise.
     * @param posArvore - posicao do No na 'arvoreB
     * @param musica - musica a se inserir.
     * @param newEndereco - endereco da musica no arquivo "Registro.db".
     * @param filhoEsq - posicao filho 'a esquerda que esta' sendo inserido.
     * @param filhoDir - posicao filho 'a direita que esta' sendo inserido.
     * @return novo No.
     */
    private NoB inserir(NoB noB, long posArvore, Musica musica, long newEndereco, long filhoEsq, long filhoDir) {
        RandomAccessFile arvoreBFile = null;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBDB, "rw");

            // Obter chave de insercao
            int newChave = musica.getId();

            // Obter raiz
            arvoreBFile.seek(0);
            long posRaiz = arvoreBFile.readLong();

            // Localizar e ler NoB
            arvoreBFile.seek(posArvore);
            noB.lerNoB(posArvore);

            // Testar se No tem espaco livre para insercao e e' folha
            if (noB.temEspacoLivre() && noB.isFolha()) {
                noB.inserir(posArvore, newChave, newEndereco);
            
            // Se nao for folha, testar se chave ja pertencia 'a arvore anteriormente
            } else if (noB.temEspacoLivre() && (filhoEsq != -1 || filhoDir != -1)) {
                noB.inserir(posArvore, newChave, newEndereco, filhoEsq, filhoDir);

            // Se nao couber na folha, deve-se procurar No de insercao
            } else {
                long posInserir = posArvore;

                // Atualizar NoB caso nao esteja fazendo o split recursivo
                if ((filhoEsq == -1 && filhoDir == -1)) {
                    posInserir = noB.encontrarInsercao(newChave);
                    noB.lerNoB(posInserir);
                }

                // Se couber no NoB, basta inserir
                if (noB.temEspacoLivre()) {
                    noB.inserir(posInserir, newChave, newEndereco, filhoEsq, filhoDir);
                
                // Se for raiz, basta criar novos dois filhos (split na raiz)
                } else if(posInserir == posRaiz) {

                    // Separar filhos direita e esquerda do No
                    NoB noEsq = noB.getFilhoEsq();
                    NoB noDir = noB.getFilhoDir();

                    // Escrever novos NoB em arquivo
                    long posEsq = noEsq.escreverNoB();
                    long posDir = noDir.escreverNoB();

                    // Separar elemento do meio para "subir"
                    noB = noB.getMeio();
                    int chave = noB.getChave(0);

                    // Alterar NoB pai
                    noB.remontarPonteiros(chave, posEsq, posDir);

                    // Reescrever NoB que sofreu o split
                    noB.escreverNoB(posInserir);

                    // Se estiver inserindo chave completamente nova
                    if ((filhoEsq == -1 && filhoDir == -1)) {
                       posInserir = noB.encontrarInsercao(newChave);
                    
                    // Senao, significa que nova posicao e' no final de arquivo
                    } else {
                        posInserir = posDir;
                    }

                    // Inserir nova chave
                    inserir(noB, posInserir, musica, newEndereco, filhoEsq, filhoDir);
                
                // Senao, deve-se dividir o No
                } else {

                    // Copiar NoB atual
                    NoB tmp = noB.clone();

                    // Como sera' inserido sequencialmente, o nextID > thisID
                    // Alterar NoB atual para filho da esquerda
                    noB = tmp.getFilhoEsq();

                    // Separar filho da direita
                    NoB noDir = tmp.getFilhoDir();

                    // Obter NoB que devera' "subir"
                    NoB noMeio = tmp.getMeio();
                    int chave = noMeio.getChave(0);
                    long endereco = noMeio.getEndereco(0);

                    // Testar se No pai esta' com espaco livre
                    NoB noPai = new NoB();
                    long newPos = noPai.encontrarPai(posInserir);
                    noPai.lerNoB(newPos);
                    
                    // Se tiver espaco, basta inserir
                    if (noPai.temEspacoLivre()) {

                        // Alterar filho da esquerda em arquivo
                        noB.escreverNoB(posInserir);

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

                        // Testar se NoB gerado e' raiz
                        // Se for, deve-se altera-la em arquivo
                        if(newPos == posRaiz) {

                            // Inserir novo filho 'a direita da pagina cheia
                            NoB newNoDir = noPai.getFilhoDir();
                            long posDir = newNoDir.escreverNoB();

                            // Sobrescrever filho esquerdo na raiz antiga
                            NoB newNoEsq = noPai.getFilhoEsq();
                            long posEsq = posRaiz;
                            newNoEsq.escreverNoB(posEsq);

                            // Escrever nova raiz
                            NoB newRaiz = new NoB(chaveSplit, enderecoSplit, posEsq, posDir);
                            long posNewRaiz = newRaiz.escreverNoB();

                            // Alterar ponteiro para nova raiz
                            arvoreBFile.seek(0);
                            byte[] posRaizBytes = ByteBuffer.allocate(8).putLong(posNewRaiz).array();
                            arvoreBFile.write(posRaizBytes);

                            // Inserir nova chave
                            inserir(musica, newEndereco);
                        
                        } else {

                            // Inserir novo filho 'a direita da pagina cheia
                            NoB newNoDir = noPai.getFilhoDir();
                            long posDir = newNoDir.escreverNoB();

                            // Sobrescrever filho esquerdo no NoB antigo
                            NoB newNoEsq = noPai.getFilhoEsq();
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
            return noB;
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

        // Ler NoB desejado
        NoB noB = new NoB();
        noB.lerNoB(pos);

        // Procurar possivel local da chave
        int i;

        // Se chave procurada for menor que a primeira
        if ((noB.numElementos > 0) && (chaveProcurada < noB.chave[0])) {
            endereco = read(noB.noFilho[0], chaveProcurada);
        
        // Senao, testar o ultimo
        } else if ((noB.numElementos > 0) && (chaveProcurada > noB.chave[noB.numElementos-1])) {
            endereco = read(noB.noFilho[noB.numElementos], chaveProcurada);

        // Senao, procurar valores intermediarios
        } else {
            for(i = 0; (i < noB.numElementos) && (noB.chave[i] < chaveProcurada); i++);

            // Testar se chave foi encontrada
            if(noB.chave[i] == chaveProcurada) {
                endereco = noB.endereco[i];

            } else {
                endereco = read(noB.noFilho[i+1], chaveProcurada);
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

                // Ler NoB e mostrar
                NoB aux = new NoB();

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
                

                // Ler NoB e mostrar
                NoB aux = new NoB();

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
            NoB no = new NoB();
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
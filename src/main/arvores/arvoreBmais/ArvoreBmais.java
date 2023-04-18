// Package
package arvores.arvoreBmais;

// Bibliotecas
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

// Bibliotecas proprias
import app.*;

public class ArvoreBmais {

    protected NoBmais raiz;
    private static final String arvoreBmaisDB = "./src/resources/ArvoreBmais.db";

    /**
     * Construtor padrao da classe ArvoreBmais
     */
    public ArvoreBmais() {
        raiz = new NoBmais();
    }

    /**
     * Metodo para inicializar o arquivo "Arvore.db", inicializando a raiz.
     */
    public void inicializarArvoreB() {
        RandomAccessFile arvoreBmaisFile = null;

        try {
            arvoreBmaisFile = new RandomAccessFile (arvoreBmaisDB, "rw");

            // Posicionar ponteiro no inicio do arquivo
            arvoreBmaisFile.seek(0);

            // Escrever posicao da raiz (proximos 8 bytes)
            long posRaiz = 8;
            byte[] posRaizBytes = ByteBuffer.allocate(8).putLong(posRaiz).array();
            arvoreBmaisFile.write(posRaizBytes);

            // Escrever raiz no arquivo
            raiz.escreverNoB();

            // Fechar arquivo
            arvoreBmaisFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao escrever o arquivo \"" + arvoreBmaisDB + "\"\n");
        }
    }

    /**
     * Metodo para obter posicao da raiz no arquivo de 'arvore
     */
    private long getPosRaiz() {
        RandomAccessFile arvoreBmaisFile = null;
        long posRaiz = -1;

        try {
            arvoreBmaisFile = new RandomAccessFile (arvoreBmaisDB, "rw");

            // Ler posicao da raiz
            posRaiz = arvoreBmaisFile.readLong();

            // Fechar arquivo
            arvoreBmaisFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler o arquivo \"" + arvoreBmaisDB + "\"\n");
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

// ==================================================================================================================================

    /**
     * Metodo para inserir uma chave de pesquisa na arvore B.
     * @param noBmais - no em analise.
     * @param posArvore - posicao do No na 'ArvoreBmais
     * @param musica - musica a se inserir.
     * @param newEndereco - endereco da musica no arquivo "Registro.db".
     * @param filhoEsq - posicao filho 'a esquerda que esta' sendo inserido.
     * @param filhoDir - posicao filho 'a direita que esta' sendo inserido.
     * @return novo No.
     */
    private NoBmais inserir(NoBmais noBmais, long posArvore, Musica musica, long newEndereco, long filhoEsq, long filhoDir) {
        RandomAccessFile arvoreBmaisFile = null;

        try {
            arvoreBmaisFile = new RandomAccessFile (arvoreBmaisDB, "rw");

            // Obter chave de insercao
            int newChave = musica.getId();

            // Obter raiz
            arvoreBmaisFile.seek(0);
            long posRaiz = arvoreBmaisFile.readLong();

            // Localizar e ler NoBmais
            arvoreBmaisFile.seek(posArvore);
            noBmais.lerNoBmais(posArvore);

            // Testar se No tem espaco livre para insercao e e' folha
            if (noBmais.temEspacoLivre() && noBmais.isFolha()) {
                noBmais.inserir(posArvore, newChave, newEndereco);
            
            // Se nao for folha, testar se chave ja pertencia 'a arvore anteriormente
            } else if (noBmais.temEspacoLivre() && (filhoEsq != -1 || filhoDir != -1)) {
                noBmais.inserir(posArvore, newChave, newEndereco, filhoEsq, filhoDir);

            // Se nao couber na folha, deve-se procurar No de insercao
            } else {
                long posInserir = posArvore;

                // Atualizar NoBmais caso nao esteja fazendo o split recursivo
                if ((filhoEsq == -1 && filhoDir == -1)) {
                    posInserir = noBmais.encontrarInsercao(newChave);
                    noBmais.lerNoBmais(posInserir);
                }

                // Se couber no NoBmais, basta inserir
                if (noBmais.temEspacoLivre()) {
                    noBmais.inserir(posInserir, newChave, newEndereco, filhoEsq, filhoDir);
                
                // Se for raiz, basta criar novos dois filhos (split na raiz)
                } else if(posInserir == posRaiz) {

                    // Separar filhos direita e esquerda do No
                    NoBmais noEsq = noBmais.getFilhoEsq();
                    NoBmais noDir = noBmais.getFilhoDir();

                    // Escrever novos NoBmais em arquivo
                    long posEsq = noEsq.escreverNoB();
                    long posDir = noDir.escreverNoB();

                    // Atualizar ponteiro para folha da direita
                    noEsq.inserirFolhaDir(posDir);
                    noEsq.escreverNoB(posEsq);

                    // Separar elemento do meio para "subir"
                    noBmais = noBmais.getMeio();
                    int chave = noBmais.getChave(0);

                    // Alterar NoBmais pai
                    noBmais.remontarPonteiros(chave, posEsq, posDir);

                    // Reescrever NoBmais que sofreu o split
                    noBmais.escreverNoB(posInserir);

                    // Se estiver inserindo chave completamente nova
                    if ((filhoEsq == -1 && filhoDir == -1)) {
                       posInserir = noBmais.encontrarInsercao(newChave);
                    
                    // Senao, significa que nova posicao e' no final de arquivo
                    } else {
                        posInserir = posDir;
                    }

                    // Inserir nova chave
                    inserir(noBmais, posInserir, musica, newEndereco, filhoEsq, filhoDir);
                
                // Senao, deve-se dividir o No
                } else {

                    // Copiar NoBmais atual
                    NoBmais tmp = noBmais.clone();

                    // Como sera' inserido sequencialmente, o nextID > thisID
                    // Alterar NoBmais atual para filho da esquerda
                    noBmais = tmp.getFilhoEsq();

                    // Separar filho da direita
                    NoBmais noDir = tmp.getFilhoDir();

                    // Obter NoBmais que devera' "subir"
                    NoBmais noMeio = tmp.getMeio();
                    int chave = noMeio.getChave(0);
                    long endereco = noMeio.getEndereco(0);

                    // Testar se No pai esta' com espaco livre
                    NoBmais noPai = new NoBmais();
                    long newPos = noPai.encontrarPai(posInserir);
                    noPai.lerNoBmais(newPos);
                    
                    // Se tiver espaco, basta inserir
                    if (noPai.temEspacoLivre()) {

                        // Alterar filho da esquerda em arquivo
                        noBmais.escreverNoB(posInserir);

                        // Inserir novo filho direita
                        long posDir = noDir.escreverNoB();

                        // Adicionar ponteiro para filho da esquerda
                        noBmais.inserirFolhaDir(posDir);
                        noBmais.escreverNoB(posInserir);

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

                        // Testar se NoBmais gerado e' raiz
                        // Se for, deve-se altera-la em arquivo
                        if(newPos == posRaiz) {

                            // Inserir novo filho 'a direita da pagina cheia
                            NoBmais newNoDir = noPai.getFilhoDir();
                            long posDir = newNoDir.escreverNoB();

                            // Sobrescrever filho esquerdo na raiz antiga
                            NoBmais newNoEsq = noPai.getFilhoEsq();
                            long posEsq = posRaiz;
                            newNoEsq.escreverNoB(posEsq);

                            // Escrever nova raiz
                            NoBmais newRaiz = new NoBmais(chaveSplit, enderecoSplit, posEsq, posDir);
                            long posNewRaiz = newRaiz.escreverNoB();

                            // Alterar ponteiro para nova raiz
                            arvoreBmaisFile.seek(0);
                            byte[] posRaizBytes = ByteBuffer.allocate(8).putLong(posNewRaiz).array();
                            arvoreBmaisFile.write(posRaizBytes);

                            // Inserir nova chave
                            inserir(musica, newEndereco);
                        
                        } else {

                            // Inserir novo filho 'a direita da pagina cheia
                            NoBmais newNoDir = noPai.getFilhoDir();
                            long posDir = newNoDir.escreverNoB();

                            // Sobrescrever filho esquerdo no NoBmais antigo
                            NoBmais newNoEsq = noPai.getFilhoEsq();
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
            arvoreBmaisFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler/escrever o arquivo \"" + arvoreBmaisDB + "\"\n");
        } finally {
            return noBmais;
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

        // Ler NoBmais desejado
        NoBmais noBmais = new NoBmais();
        noBmais.lerNoBmais(pos);

        // Procurar possivel local da chave
        int i;

        // Se chave procurada for menor que a primeira
        if ((noBmais.numElementos > 0) && (chaveProcurada < noBmais.chave[0])) {
            endereco = read(noBmais.noFilho[0], chaveProcurada);
        
        // Senao, testar o ultimo
        } else if ((noBmais.numElementos > 0) && (chaveProcurada > noBmais.chave[noBmais.numElementos-1])) {
            endereco = read(noBmais.noFilho[noBmais.numElementos], chaveProcurada);

        // Senao, procurar valores intermediarios
        } else {
            for(i = 0; (i < noBmais.numElementos) && (noBmais.chave[i] < chaveProcurada); i++);

            // Testar se chave foi encontrada
            if(noBmais.chave[i] == chaveProcurada) {
                endereco = noBmais.endereco[i];

            } else {
                endereco = read(noBmais.noFilho[i+1], chaveProcurada);
            }
        }

        return endereco;
    }

    public void mostrarArquivo() {
        RandomAccessFile arvoreBmaisFile = null;

        try {
            arvoreBmaisFile = new RandomAccessFile (arvoreBmaisDB, "rw");

            System.out.print("Pos [" + String.format("%8d", 0) + "]: raiz: ");
            long posRaiz = arvoreBmaisFile.readLong();
            long posAtual = arvoreBmaisFile.getFilePointer();
            System.out.println("|" + String.format("%8d", posRaiz) + "|");

            // Percorrer todo arquivo
            while(posAtual != arvoreBmaisFile.length()) {
                
                // Mostrar posicao atual do arquivo
                System.out.print("Pos [" + String.format("%8d", posAtual) + "]: ");

                // Ler NoBmais e mostrar
                NoBmais aux = new NoBmais();

                // Posicionar ponteiro na posicao de inicio do No
                arvoreBmaisFile.seek(posAtual);

                // Ler numero de elementos no No
                aux.numElementos = arvoreBmaisFile.readShort();

                // Ler informacoes do No
                for (int i = 0; i < aux.ordemArvore-1; i++) {
                    
                    // Ler ponteiro para filho da esquerda da posicao i
                    aux.noFilho[i] = arvoreBmaisFile.readLong();

                    // Ler chave na posicao i
                    aux.chave[i] = arvoreBmaisFile.readInt();

                    // Ler endereco na posicao i para  "Registro.db"
                    aux.endereco[i] = arvoreBmaisFile.readLong();
                }

                // Ler ultimo ponteiro 'a direita
                aux.noFilho[aux.ordemArvore-1] = arvoreBmaisFile.readLong();

                // Ler ponteiro para folha da direita
                aux.folhaDir = arvoreBmaisFile.readLong();
                posAtual = arvoreBmaisFile.getFilePointer();

                //Printar
                System.out.println(aux);
            }
            System.out.println();
            
            // Fechar arquivo
            arvoreBmaisFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler o arquivo \"" + arvoreBmaisDB + "\"\n");
        }
    }

    /**
     * Metodo para obter o total de chaves que estao, de fato, inseridas na
     * arvore.
     * @return total de chaves inseridas.
     */
    public int contarChaves() {
        RandomAccessFile arvoreBmaisFile = null;
        int total = 0;

        try {
            arvoreBmaisFile = new RandomAccessFile (arvoreBmaisDB, "rw");

            long posRaiz = arvoreBmaisFile.readLong();
            long posAtual = arvoreBmaisFile.getFilePointer();

            // Percorrer todo arquivo
            while(posAtual != arvoreBmaisFile.length()) {
                

                // Ler NoBmais e mostrar
                NoBmais aux = new NoBmais();

                // Posicionar ponteiro na posicao de inicio do No
                arvoreBmaisFile.seek(posAtual);

                // Ler numero de elementos no No
                aux.numElementos = arvoreBmaisFile.readShort();
                total += aux.numElementos;

                // Ler informacoes do No
                for (int i = 0; i < aux.ordemArvore-1; i++) {
                    
                    // Ler ponteiro para filho da esquerda da posicao i
                    aux.noFilho[i] = arvoreBmaisFile.readLong();

                    // Ler chave na posicao i
                    aux.chave[i] = arvoreBmaisFile.readInt();

                    // Ler endereco na posicao i para  "Registro.db"
                    aux.endereco[i] = arvoreBmaisFile.readLong();
                }

                // Ler ultimo ponteiro 'a direita
                aux.noFilho[aux.ordemArvore-1] = arvoreBmaisFile.readLong();

                posAtual = arvoreBmaisFile.getFilePointer();
            }
            
            // Fechar arquivo
            arvoreBmaisFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler o arquivo \"" + arvoreBmaisDB + "\"\n");
        } finally {
            return total;
        }
    }

    private void caminhar(long pos) {
        if (pos != -1) {
            NoBmais no = new NoBmais();
            no.lerNoBmais(pos);

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
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
    private long getRaiz() {
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
     * Metodo para inserir uma chave de pesquisa na arvore B, procurando No de
     * insercao.
     * @param newMusica - musica a se inserir.
     * @param newEndereco - endereco da musica no arquivo "Registro.db".
     */
    public void inserir(Musica newMusica, long newEndereco) {
        NoB noB = new NoB();
        long posInserir = noB.encontrarInsercao(newMusica.getId(), getRaiz());
        inserir(posInserir, newMusica, newEndereco, -1, -1);
    }

    /**
     * Metodo para inserir uma chave de pesquisa na arvore B.
     * @param posInserir - posicao do No na 'arvoreB
     * @param newMusica - musica a se inserir.
     * @param newEndereco - endereco da musica no arquivo "Registro.db".
     * @param filhoEsq - posicao filho 'a esquerda que esta' sendo inserido.
     * @param filhoDir - posicao filho 'a direita que esta' sendo inserido.
     * contrario.
     */
    private void inserir(long posInserir, Musica newMusica, long newEndereco, long filhoEsq, long filhoDir) {
        RandomAccessFile arvoreBFile = null;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBDB, "rw");

            // Obter chave a ser inserida
            int newChave = newMusica.getId();

            // Obter raiz
            long posRaiz = getRaiz();

            // Ler NoB indicado pela posicao
            NoB noB = new NoB();
            noB.lerNoB(posInserir);

            // Testar se NoB esta' livre e e' folha
            if (noB.temEspacoLivre() && noB.isFolha()) {
                noB.inserir(posInserir, newChave, newEndereco);
            
            // Inserir fora da folha, caso esteja realizandoo split
            } else if((noB.temEspacoLivre()) && (filhoEsq != -1 || filhoDir != -1)) {
                noB.inserir(posInserir, newChave, newEndereco, filhoEsq, filhoDir);
            
            // Senao, deve-se considerar o split
            } else {

                // Realizar o split
                split(posInserir);

                // Inserir na posicao desejada se for split
                if((filhoEsq != -1 || filhoDir != -1)) {
                   inserir(posInserir, newMusica, newEndereco, filhoEsq, filhoDir);
                
                // Senao, procurar local de insercao
                } else {
                    inserir(newMusica, newEndereco);
                }
                
            } 

            // Fechar arquivo
            arvoreBFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler/escrever o arquivo \"" + arvoreBDB + "\"\n");
        }
    }

    /**
     * Metodo para realizar o split da arvore durante a insercao.
     * @param posSplit - posicao do arquivo, no qual deve-se realizar o split.
     */
    private void split (long posSplit) {
        RandomAccessFile arvoreBFile = null;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBDB, "rw");

            // Ler NoB que sofrera' split
            NoB noSplit = new NoB();
            noSplit.lerNoB(posSplit);

            // Obter NoB filhos
            NoB noEsq = noSplit.getFilhoEsq();
            NoB noDir = noSplit.getFilhoDir();

            // Escrever filhos direito em arquivo
            noDir.escreverNoB(posSplit);
            long filhoEsq = noEsq.escreverNoB();

            // Separar NoB pra split
            noSplit = noSplit.getMeio();
            int chaveSplit = noSplit.getChave(0);
            long enderecoSplit = noSplit.getEndereco(0);
            noSplit.remontarPonteiros(chaveSplit, posSplit);

            // Obter pai
            NoB noBpai = new NoB();
            long posPai = noBpai.encontrarPai(posSplit);
            if (posPai != -1) {
                noBpai.lerNoB(posPai);

                // Testar se tem espaco livre no pai
                if (noBpai.temEspacoLivre()) {

                    // Remanejar ponteiros e reinserir chave que sofreu split
                    noSplit.remontarPonteiros(chaveSplit, filhoEsq, posSplit);
                    noBpai.inserir(posPai, noSplit);

                // Se nao tiver, deve-se escrever outro No (Split recursivo)
                } else {

                    // Reinserir a chaveSplit na arvore
                    Musica musicaSplit = new Musica();
                    musicaSplit.setId(chaveSplit);
                    inserir(posPai, musicaSplit, enderecoSplit, filhoEsq, posSplit);
                }

            // Se estiver na raiz, nao tera' pai
            } else {

                // Remanejar ponteiros e reinserir chave que sofreu split
                noSplit.remontarPonteiros(chaveSplit, filhoEsq, posSplit);
                long newRaiz = noSplit.escreverNoB();;

                // Alterar ponteiro para nova raiz
                arvoreBFile.seek(0);
                byte[] posRaizBytes = ByteBuffer.allocate(8).putLong(newRaiz).array();
                arvoreBFile.write(posRaizBytes);
            }

            // Fechar arquivo
            arvoreBFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler/escrever o arquivo \"" + arvoreBDB + "\"\n");
        }
    }

    /**
     * Metodo para procurar uma chave na arvore.
     * @param chaveProcurada - id da chave que se deseja encontrar.
     * @return posicao da chave no arquivo "Registro.db".
     */
    public long read(int chaveProcurada) {
        return read(getRaiz(), chaveProcurada);
    }

    /**
     * Metodo para procurar uma chave na arvore.
     * @param pos - posicao a ser analisada se a chava esta' inclusa ou nao.
     * @param chaveProcurada - id da chave que se deseja encontrar.
     * @return posicao da chave no arquivo "Registro.db".
     */
    private long read(long pos, int chaveProcurada) {

        long endereco = -1;

        // Ler NoB desejado se nao for null
        // Se chegou a -1, significa que nao encontrou
        if (pos != -1) {

            // Obter NoB para analise
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
                    endereco = read(noB.noFilho[i], chaveProcurada);
                }
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

}
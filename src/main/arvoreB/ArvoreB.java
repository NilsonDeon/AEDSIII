package arvoreB;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import app.Musica;

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
     * @throws Exception Se ocorrer algum erro ao manipular os arquivos.
     */
    public void inicializarArvoreB() throws Exception {
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

        } catch (IOException e) {
            System.out.println("\nERRO: Ocorreu um erro de escrita no " +
                               "arquivo \"" + arvoreBDB + "\"\n");
        } finally {
            if (arvoreBFile != null) arvoreBFile.close();
        }
    }

    /**
     * Metodo para obter posicao da raiz no arquivo de 'arvore
     * @throws Exception Se ocorrer algum erro ao manipular os arquivos.
     */
    private long getPosRaiz() throws Exception {
        RandomAccessFile arvoreBFile = null;
        long posRaiz = -1;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBDB, "rw");

            // Ler posicao da raiz
            posRaiz = arvoreBFile.readLong();

        } catch (IOException e) {
            System.out.println("\nERRO: Ocorreu um erro de escrita no " +
                               "arquivo \"" + arvoreBDB + "\"\n");
        } finally {
            if (arvoreBFile != null) arvoreBFile.close();
            return posRaiz;
        }
    }

    /**
     * Metodo para inserir uma chave de pesquisa na arvore B.
     * @param musica - musica a se inserir.
     * @param newEndereco - endereco da musica no arquivo "Registro.db".
     * @throws Exception Se ocorrer algum erro ao manipular os arquivos.
     */
    public void inserir(Musica musica, long newEndereco) throws Exception {
        raiz = inserir(raiz, getPosRaiz(), musica, newEndereco);
    }

    /**
     * Metodo para inserir uma chave de pesquisa na arvore B.
     * @param noB - no em analise.
     * @param posArvore - posicao do No na 'arvoreB
     * @param musica - musica a se inserir.
     * @param newEndereco - endereco da musica no arquivo "Registro.db".
     * @return novo No.
     * @throws Exception Se ocorrer algum erro ao manipular os arquivos.
     */
    private NoB inserir(NoB noB, long posArvore, Musica musica, long newEndereco) throws Exception {
        RandomAccessFile arvoreBFile = null;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBDB, "rw");

            // Obter chave de insercao
            int newChave = musica.getId();

            // Localizar e ler NoB
            arvoreBFile.seek(posArvore);
            noB.lerNoB(posArvore);

            // Testar se No tem espaco livre para insercao e e' folha
            if (noB.temEspacoLivre() && noB.isFolha()) {
                noB.inserir(posArvore, newChave, newEndereco);
            
            // Se nao couber na folha, deve-se procurar No de insercao
            } else {
                long posInserir;
                posInserir = noB.encontrarInsercao(newChave);
                noB.lerNoB(posInserir);

                // Se couber no NoB, basta inserir
                if (noB.temEspacoLivre()) {
                    noB.inserir(posInserir, newChave, newEndereco);
                
                // Se for raiz, basta criar novos dois filhos
                } else if(posInserir == posArvore) {

                    System.out.println("\nIF");

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

                    // Inserir nova chave
                    posInserir = noB.encontrarInsercao(newChave);
                    inserir(noB, posInserir, musica, newEndereco);
                
                // Senao, deve-se dividir o No
                } else {

                    System.out.println("\nELSE");

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

                    // Alterar filho da esquerda em arquivo
                    noB.escreverNoB(posArvore);

                    // Inserir novo filho direita
                    long posDir = noDir.escreverNoB();

                    // Incluir chave que subiu no arquivo
                    NoB aux = new NoB();
                    long newPos = aux.encontrarPai(posInserir);
                    aux.lerNoB(newPos);
                    aux.inserir(posInserir, chave, endereco, posDir);

                    // Inserir nova chave
                    posInserir = noB.encontrarInsercao(newChave);
                    inserir(noB, posInserir, musica, newEndereco);                    

                }
            }

        } catch (IOException e) {
            System.out.println("\nERRO: Ocorreu um erro de escrita no " +
                               "arquivo \"" + arvoreBDB + "\"\n");
        } finally {
            if (arvoreBFile != null) arvoreBFile.close();
            return noB;
        }
    }

    public void mostrarArquivo() throws Exception {
        RandomAccessFile arvoreBFile = null;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBDB, "rw");

            arvoreBFile.readLong();
            long posAtual = arvoreBFile.getFilePointer();

            // Percorrer todo arquivo
            while(posAtual != arvoreBFile.length()) {
                
                // Mostrar posicao atual do arquivo
                System.out.print("Pos [" + String.format("%6d", posAtual) + "]: ");

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
            
        } catch (IOException e) {
            System.out.println("\nERRO: Ocorreu um erro de escrita no " +
                               "arquivo \"" + arvoreBDB + "\"\n");
        } finally {
            if (arvoreBFile != null) arvoreBFile.close();
        }
    }
}
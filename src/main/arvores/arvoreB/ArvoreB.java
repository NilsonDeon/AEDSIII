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
     * Metodo para inicializar o arquivo "ArvoreB.db", inicializando a raiz.
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
        }

        return posRaiz;
        
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

    /**
     * Metodo para procurar uma chave na arvore.
     * @param chaveProcurada - id da chave que se deseja encontrar.
     * @return posicao da chave no arquivo "AroreB.db".
    */
    public long getPosicao(int chaveProcurada) {
        System.out.println("getRaiz();" + getRaiz());
        return getPosicao(getRaiz(), chaveProcurada);
    }

    /**
     * Metodo para procurar uma chave na arvore.
     * @param pos - posicao a ser analisada se a chava esta' inclusa ou nao.
     * @param chaveProcurada - id da chave que se deseja encontrar.
     * @return posicao da chave no arquivo "AroreB.db".
    */
    private long getPosicao(long pos, int chaveProcurada) {

        long endereco = -1;

        System.out.println("pos: " + pos);

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
                endereco = getPosicao(noB.noFilho[0], chaveProcurada);
            
            // Senao, testar o ultimo
            } else if ((noB.numElementos > 0) && (chaveProcurada > noB.chave[noB.numElementos-1])) {
                endereco = getPosicao(noB.noFilho[noB.numElementos], chaveProcurada);

            // Senao, procurar valores intermediarios
            } else {
                for(i = 0; (i < noB.numElementos) && (noB.chave[i] < chaveProcurada); i++);

                // Testar se chave foi encontrada
                System.out.println("noB.chave[i] == chaveProcurada: " + noB.chave[i] + " == " + chaveProcurada);
                if(noB.chave[i] == chaveProcurada) {
                    endereco = pos;

                    System.out.println("endereco: " + endereco);

                } else {
                    endereco = getPosicao(noB.noFilho[i], chaveProcurada);
                }
            }
        
        }

        return endereco;
    }

    /**
     * Metodo para exibir a estrutura da arvore em arquivos, sendo cada linha
     * representando um NoB da arvore.
     * Estrutura:
     * Pos[ &arqArvore] : | &filho | chave | &arqRegistro | ... 
    */
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
                for (int i = 0; i < NoB.ordemArvore-1; i++) {
                    
                    // Ler ponteiro para filho da esquerda da posicao i
                    aux.noFilho[i] = arvoreBFile.readLong();

                    // Ler chave na posicao i
                    aux.chave[i] = arvoreBFile.readInt();

                    // Ler endereco na posicao i para  "Registro.db"
                    aux.endereco[i] = arvoreBFile.readLong();
                }

                // Ler ultimo ponteiro 'a direita
                aux.noFilho[NoB.ordemArvore-1] = arvoreBFile.readLong();
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
                for (int i = 0; i < NoB.ordemArvore-1; i++) {
                    
                    // Ler ponteiro para filho da esquerda da posicao i
                    aux.noFilho[i] = arvoreBFile.readLong();

                    // Ler chave na posicao i
                    aux.chave[i] = arvoreBFile.readInt();

                    // Ler endereco na posicao i para  "Registro.db"
                    aux.endereco[i] = arvoreBFile.readLong();
                }

                // Ler ultimo ponteiro 'a direita
                aux.noFilho[NoB.ordemArvore-1] = arvoreBFile.readLong();

                posAtual = arvoreBFile.getFilePointer();
            }
            
            // Fechar arquivo
            arvoreBFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler o arquivo \"" + arvoreBDB + "\"\n");
        }

        return total;
    }

    /**
     * Metodo para procurar e alterar endereco uma musica a partir do seu ID.
     * @param idProcurado - id da musica para atualizar endereco.
     * @param newEndereco - novo endereco da musica.
     * @return true, se a música foi altualizada; false, caso contrario.
    */
    public boolean update(int idProcurado, long newEndereco) {
        boolean find = false;

        // Procurar id desejado
        long posArvore = getPosicao(idProcurado);
        NoB noB = new NoB();
        noB.lerNoB(posArvore);

        // Localizar id na posicao encontrada
        for(int i = 0; i < noB.numElementos; i++) {

            // Alterar novo endereco
            if(idProcurado == noB.chave[i]) {
                noB.endereco[i] = newEndereco;
                i = noB.numElementos;
                find = true;
            }
        }
        
        // Alterar em arquivo
        noB.escreverNoB(posArvore);

        return find;
    }

    public void delete(int chaveProcurada) {
        delete(chaveProcurada, false);
    }

    /**
     * Metodo para deletar uma musica da arvore, a partir de seu id.
     * @param chaveProcurada - id da musica a ser deletada.
     * @param isDuplicada - boolean para indicar se a chave que e sera' 
     * removida foi duplicada.
    */
    private void delete (int chaveProcurada, boolean isDuplicada) {
        RandomAccessFile arvoreBFile = null;
                    IO io = new IO();

        try {
            arvoreBFile = new RandomAccessFile (arvoreBDB, "rw");

            NoB noB = new NoB();
            long posNo;

            // Obter posicao da chave no arquivo
            if (isDuplicada) {
                System.out.println("if");
                posNo = noB.encontrarInsercao(chaveProcurada, getRaiz());
            } else {
                posNo = getPosicao(chaveProcurada);
                System.out.println("else");

            }

            // Ler No em que a chave esta'
            noB.lerNoB(posNo);
        

            // Se estiver em uma folha e ela mantiver 50% de ocupacao
            if(noB.isFolha() && noB.isMaisMetade()) {
                
                // Deletar da folha
                delete(posNo, chaveProcurada, false);

            // Se ele nao estiver na folha, trocar pelo antecessor
            } else if(! noB.isFolha()){

                // Obter antecessor
                io.readLine("getPrint");
                long posAnt = noB.encontrarInsercao(chaveProcurada, posNo);

                io.readLine("posAnt: " + posAnt);
                io.readLine("chaveProcurada: " + chaveProcurada);
                io.readLine("posNo: " + posNo);

                NoB noAnt = new NoB();
                noAnt.lerNoB(posAnt);

                // Obter elementos do antecessor
                int chaveAnt = noAnt.chave[noAnt.numElementos-1];
                long enderecoAnt = noAnt.endereco[noAnt.numElementos-1];

                io.readLine("chaveAnt: " + chaveAnt);
                io.readLine("enderecoAnt: " + enderecoAnt);

                // Trocar chave procurada pelo antecessor
                noB.trocarChave(posNo, chaveProcurada, chaveAnt, enderecoAnt);

                // Apagar antecessor duplicado
                delete(chaveAnt, true);
            
            // Se ele estiver na folha e nao ceder
            } else {

                // Encontrar NoB pai do NoB analisado
                NoB noPai = new NoB();
                long posPai = noPai.encontrarPai(posNo);
                noPai.lerNoB(posPai);

                System.out.println("posPai: " + posPai);

                // Encontrar irmao da esquerda
                NoB noIrmaoEsq = new NoB();
                long posIrmaoEsq = noIrmaoEsq.encontrarIrmaoEsq(posPai, chaveProcurada);

                // Encontrar irmao da direita
                NoB noIrmaoDir = new NoB();
                long posIrmaoDir = noIrmaoDir.encontrarIrmaoDir(posPai, chaveProcurada);

                System.out.println("noPai: " + noPai);
                System.out.println("posIrmaoEsq: " + noIrmaoEsq);
                System.out.println("posIrmaoDir: " + noIrmaoDir);

                // Informacoes sobre o irmao a ser utilizado
                NoB noIrmao;
                long posIrmao;
                int chaveIrmao;
                long enderecoIrmao;

                // Informacoes do pai
                int chavePai;
                long enderecoPai;
        

                // Selecionar o NoB com mais elementos
                if(noIrmaoDir.numElementos >= noIrmaoEsq.numElementos) {
                    noIrmao = noIrmaoDir;
                    posIrmao = posIrmaoDir;

                    // Primeiro registro do NoB
                    chaveIrmao = noIrmaoDir.chave[0];
                    enderecoIrmao = noIrmaoDir.endereco[0];

                    // Obter chave pai correspondente
                    int i;
                    for(i = 0; (i < noPai.numElementos) && (noPai.chave[i] < chaveProcurada); i++);
                    chavePai = noPai.chave[i];
                    enderecoPai = noPai.endereco[i];

                // Da esquerda e' o maior
                } else {
                    noIrmao = noIrmaoEsq;
                    posIrmao = posIrmaoEsq;

                    // Ultimo registro do NoB
                    chaveIrmao = noIrmaoEsq.chave[noIrmaoEsq.numElementos-1];
                    enderecoIrmao = noIrmaoEsq.endereco[noIrmaoEsq.numElementos-1];   

                    // Obter chave pai correspondente
                    int i;
                    for(i = 0; (i < noPai.numElementos) && (noPai.chave[i] < chaveProcurada); i++);
                    chavePai = noPai.chave[i-1];
                    enderecoPai = noPai.endereco[i-1];
                }

                // Se pagina irma puder ceder um registro
                if(noIrmao.isMaisMetade()) {

                    // Apagar chave desejada
                    delete(posNo, chaveProcurada, true);
                    System.out.println("posNo: " + posNo);
                    System.out.println("chaveProcurada: " + chaveProcurada);

                    mostrarArquivo();
                    io.readLine();

                    // Inserir chave do pai no noB
                    noB.lerNoB(posNo);
                    noB.inserir(posNo, chavePai, enderecoPai);

                    mostrarArquivo();
                    io.readLine();

                    // Irmao cede a chave
                    noB.swap(posPai, chavePai, enderecoPai, posIrmao, chaveIrmao, enderecoIrmao);
                    mostrarArquivo();
                    io.readLine();

                    // Deletar antiga posicao irmao
                    delete(posIrmao, chavePai, true);               
                    mostrarArquivo();
                    io.readLine();

                // Se pagina irma nao ceder e ficar com menos de 50% de ocupacao
                } else {

                    // Selecionar irmao existente
                    noIrmao = noIrmaoDir;
                    posIrmao = posIrmaoDir;
                    System.out.println("noIrmao: " + noIrmao);
                    System.out.println("noIrmao.numElementos: " + noIrmao.numElementos); 

                    // Testar se irmao da direita e' valido
                    if (noIrmao.numElementos != 0) {

                        System.out.println("if noIrmao");

                        // Obter chave pai correspondente
                        int i;
                        for(i = 0; (i < noPai.numElementos) && (noPai.chave[i] < chaveProcurada); i++);
                        chavePai = noPai.chave[i];
                        enderecoPai = noPai.endereco[i];


                    } else {
                        System.out.println("else noIrmao");

                        // Ler irmao da esquerda
                        noIrmao = noIrmaoEsq;
                        posIrmao = posIrmaoEsq;
                        
                        // Obter chave pai correspondente
                        int i;
                        for(i = 0; (i < noPai.numElementos) && (noPai.chave[i] < chaveProcurada); i++);
                        chavePai = noPai.chave[i-1];
                        enderecoPai = noPai.endereco[i-1];

                    }

                    System.out.println("noIrmao: " + noIrmao);

                    mostrarArquivo();
                    io.readLine("ANTES:\n");

                    System.out.println("\nchavePai: " + chavePai + "\nenderecoPai: " + enderecoPai);

                    // Juntar, no atual, o pai
                    noB.inserir(posNo, chavePai, enderecoPai);

                    mostrarArquivo();
                    io.readLine("noB.inserir(posNo, chavePai, enderecoPai)\n");
                    

                    // Juntar elementos do irmao
                    for(int i = 0; i < noIrmao.numElementos; i++) {
                        noB.inserir(posNo, noIrmao.chave[i], noIrmao.endereco[i]);
                    }
                    mostrarArquivo();
                    io.readLine("noB.inserir(posNo, noIrmao.chave[i], noIrmao.endereco[i])\n");

                    // Apagar chave procurada
                    delete(posNo, chaveProcurada, true);
                    mostrarArquivo();
                    io.readLine("delete(posicao, chaveProcurada)\n");

                    // Apagar no irmao
                    noIrmao.deletarNo(posIrmao);
                    mostrarArquivo();
                    io.readLine("noIrmao.deletarNo(posIrmao)\n");

                    // Apagar o pai do noPai
                    if(isDuplicada) {
                        delete(posPai, chavePai, false);
                    } else {
                        delete(posPai, chavePai, true);
                    }

                    mostrarArquivo();
                    io.readLine("delete(posPai, chavePai)\n");

                    // Testar se posicao pai esta' com 50%
                    noPai.lerNoB(posPai);
                    System.out.println("posPai  : " + posPai);
                    System.out.println("chavePai: " + chavePai);
                    if(! noPai.isMaisMetade() ) {
                        noPai = corrigirNoB(chavePai, posPai);
                        noPai.lerNoB(posPai);
                    }

                }

            }

            // Fechar arquivo
            arvoreBFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler/escrever o arquivo \"" + arvoreBDB + "\"\n");
        }
    }


    public NoB corrigirNoB (int chaveErro, long posErro) {
        RandomAccessFile arvoreBFile = null;
        NoB noIrmao = null;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBDB, "rw");

            // Ler No com erro
            NoB noErro = new NoB();
            noErro.lerNoB(posErro);                                               // 20 24 32

            // Encontrar NoB pai do NoB analisado
            NoB noPai = new NoB();
            long posPai = noPai.encontrarPai(posErro);                            // 16 36
            noPai.lerNoB(posPai);

            // Procurar irmaos
            // Encontrar irmao da esquerda
            NoB noIrmaoEsq = new NoB();
            long posIrmaoEsq = noIrmaoEsq.encontrarIrmaoEsq(posPai, chaveErro);

            // Encontrar irmao da direita
            NoB noIrmaoDir = new NoB();
            long posIrmaoDir = noIrmaoDir.encontrarIrmaoDir(posPai, chaveErro);  //[40 48 53]

            // Informacoes sobre o irmao a ser utilizado
            long posIrmao;
            int chaveIrmao;
            long enderecoIrmao;
            long noFilhoIrmao;

            // Informacoes do pai
            int chavePai;
            long enderecoPai;

            // Selecionar o NoB com mais elementos (preferencialmente 'a direita)
            if(noIrmaoDir.numElementos >= noIrmaoEsq.numElementos) {
                noIrmao = noIrmaoDir;
                posIrmao = posIrmaoDir;

                // Primeiro registro do NoB
                chaveIrmao = noIrmaoDir.chave[0];
                enderecoIrmao = noIrmaoDir.endereco[0];
                noFilhoIrmao = noIrmao.noFilho[0];

                // Obter chave pai correspondente
                int i;
                for(i = 0; (i < noPai.numElementos) && (noPai.chave[i] < chaveErro); i++);
                chavePai = noPai.chave[i];
                enderecoPai = noPai.endereco[i];

            // Da esquerda e' o maior
            } else {
                noIrmao = noIrmaoEsq;
                posIrmao = posIrmaoEsq;

                // Ultimo registro do NoB
                chaveIrmao = noIrmaoEsq.chave[noIrmaoEsq.numElementos-1];
                enderecoIrmao = noIrmaoEsq.endereco[noIrmaoEsq.numElementos-1];   
                noFilhoIrmao = noIrmao.noFilho[noIrmao.numElementos-1];

                // Obter chave pai correspondente
                int i;
                for(i = 0; (i < noPai.numElementos) && (noPai.chave[i] < chaveErro); i++);
                chavePai = noPai.chave[i-1];
                enderecoPai = noPai.endereco[i-1];
            }

            // Descer o pai
            noErro.inserir(posErro, chavePai, enderecoPai, noFilhoIrmao);

            // Trocar pai pelo irmao do filho
            noErro.swap(posPai, chavePai, enderecoPai, posIrmao, chaveIrmao, enderecoIrmao);

            // Apagar id duplicado
            delete(posIrmao, chavePai, true);

            // Fechar arquivo
            arvoreBFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler/escrever o arquivo \"" + arvoreBDB + "\"\n");
        }
            
        return noIrmao;
    }

    /**
     * Metodo privado para deletar uma musica na posicao desejada na arvore.
     * @param posArvore - posicao da musica na arvore.
     * @param chaveProcurada -  id da musica para se deletar.
     * @param ultimoFilho - determinar se e' necessario salvar ultimo ponteiro de filho.
    */
    private void delete(long posArvore, int chaveProcurada, boolean ultimoFilho) {
        RandomAccessFile arvoreBFile = null;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBDB, "rw");

            // Ler No em que a chave esta'
            NoB noB = new NoB();
            noB.lerNoB(posArvore);

            // Localizar id e deletar
            for(int i = 0; i < noB.numElementos; i++) {

                // Alterar endereco e chave para -1
                System.out.println("noB.chave[i]: " + noB.chave[i]);
                if(chaveProcurada == noB.chave[i]) {
                                       
                    // Remanejar elementos para a esquerda
                    noB.remanejarRegistros(i, posArvore, ultimoFilho);
                    System.out.println("noB.remanejarRegistros(i, posArvore): i = " + i);

                    // Quebrar loop
                    i = noB.numElementos;
                }
            }

            // Fechar arquivo
            arvoreBFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler/escrever o arquivo \"" + arvoreBDB + "\"\n");
        }
    }
}
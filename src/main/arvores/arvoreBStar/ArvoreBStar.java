// Package
package arvores.arvoreBStar;

// Bibliotecas
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

// Bibliotecas proprias
import app.*;

public class ArvoreBStar {

    protected NoBStar raiz;
    private static final String arvoreBDB = "./src/resources/ArvoreBStar.db";

    /**
     * Construtor padrao da classe ArvoreBStar
    */
    public ArvoreBStar() {
        raiz = new NoBStar();
    }

    /**
     * Metodo para inicializar o arquivo "ArvoreBStar.db", inicializando a raiz.
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
        NoBStar noB = new NoBStar();
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

            // Ler NoBStar indicado pela posicao
            NoBStar noB = new NoBStar();
            noB.lerNoB(posInserir);

            // Testar se NoBStar esta' livre e e' folha
            if (noB.temEspacoLivre() && noB.isFolha()) {
                noB.inserir(posInserir, newChave, newEndereco);
            
            // Inserir fora da folha, caso esteja realizandoo split
            } else if((noB.temEspacoLivre()) && (filhoEsq != -1 || filhoDir != -1)) {
                noB.inserir(posInserir, newChave, newEndereco, filhoEsq, filhoDir);
            
            // Senao, deve-se tentar adiar o slip (irmao ceder chave)
            } else {

                // Encontrar NoB pai do NoB analisado
                NoBStar noPai = new NoBStar();
                long posPai = noPai.encontrarPai(posInserir);

                // Testar se nao e' raiz
                if (posPai != -1) {

                    // Ler noPai
                    noPai.lerNoB(posPai);

                    // Encontrar irmao da esquerda
                    NoBStar noIrmaoEsq = new NoBStar();
                    long posIrmaoEsq = noIrmaoEsq.encontrarIrmaoEsq(posPai, newChave);

                    // Encontrar irmao da direita
                    NoBStar noIrmaoDir = new NoBStar();
                    long posIrmaoDir = noIrmaoDir.encontrarIrmaoDir(posPai, newChave);

                    // Informacoes sobre o irmao a ser utilizado
                    NoBStar noIrmao;
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
                        for(i = 0; (i < noPai.numElementos) && (noPai.chave[i] < newChave); i++);
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
                        for(i = 0; (i < noPai.numElementos) && (noPai.chave[i] < newChave); i++);
                        chavePai = noPai.chave[i-1];
                        enderecoPai = noPai.endereco[i-1];
                    }

                    // Se pagina irma puder ceder um registro
                    if(noIrmao.temEspacoLivre()) {

                        // Inserir no irmao o pai
                        noIrmao.inserir(posIrmao, chavePai, enderecoPai);

                        // NoB cede a chave para trocar com pai
                        int chaveNoB = noB.chave[0];
                        long enderecoNoB = noB.endereco[0];
                        noB.swap(posPai, chavePai, enderecoPai, posInserir, chaveNoB, enderecoNoB);

                        // Deletar antiga posicao noB
                        delete(posInserir, chavePai, true);

                        // Inserir chave desejada
                        noB.lerNoB(posInserir);
                        noB.inserir(posInserir, newChave, newEndereco);
                    
                    // Realizar o split
                    } else {
                        split(posInserir);

                        // Inserir na posicao desejada se for split
                        if((filhoEsq != -1 || filhoDir != -1)) {
                        inserir(posInserir, newMusica, newEndereco, filhoEsq, filhoDir);
                        
                        // Senao, procurar local de insercao
                        } else {
                            inserir(newMusica, newEndereco);
                        }
                    }
                
                // Se for raiz, split obrigatorio
                } else {
                    split(posInserir);

                    // Inserir na posicao desejada se for split
                    if((filhoEsq != -1 || filhoDir != -1)) {
                    inserir(posInserir, newMusica, newEndereco, filhoEsq, filhoDir);
                    
                    // Senao, procurar local de insercao
                    } else {
                        inserir(newMusica, newEndereco);
                    }
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

            // Ler NoBStar que sofrera' split
            NoBStar noSplit = new NoBStar();
            noSplit.lerNoB(posSplit);

            // Obter NoBStar filhos
            NoBStar noEsq = noSplit.getFilhoEsq();
            NoBStar noDir = noSplit.getFilhoDir();

            // Escrever filhos direito em arquivo
            noDir.escreverNoB(posSplit);
            long filhoEsq = noEsq.escreverNoB();

            // Separar NoBStar pra split
            noSplit = noSplit.getMeio();
            int chaveSplit = noSplit.getChave(0);
            long enderecoSplit = noSplit.getEndereco(0);
            noSplit.remontarPonteiros(chaveSplit, posSplit);

            // Obter pai
            NoBStar noBpai = new NoBStar();
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

        // Ler NoBStar desejado se nao for null
        // Se chegou a -1, significa que nao encontrou
        if (pos != -1) {

            // Obter NoBStar para analise
            NoBStar noB = new NoBStar();
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

        // Ler NoBStar desejado se nao for null
        // Se chegou a -1, significa que nao encontrou
        if (pos != -1) {

            // Obter NoBStar para analise
            NoBStar noB = new NoBStar();
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
                if(noB.chave[i] == chaveProcurada) {
                    endereco = pos;
                } else {
                    endereco = getPosicao(noB.noFilho[i], chaveProcurada);
                }
            }
        
        }

        return endereco;
    }

    /**
     * Metodo para exibir a estrutura da arvore em arquivos, sendo cada linha
     * representando um NoBStar da arvore.
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

                // Ler NoBStar e mostrar
                NoBStar aux = new NoBStar();

                // Posicionar ponteiro na posicao de inicio do No
                arvoreBFile.seek(posAtual);

                // Ler numero de elementos no No
                aux.numElementos = arvoreBFile.readShort();

                // Ler informacoes do No
                for (int i = 0; i < NoBStar.ordemArvore-1; i++) {
                    
                    // Ler ponteiro para filho da esquerda da posicao i
                    aux.noFilho[i] = arvoreBFile.readLong();

                    // Ler chave na posicao i
                    aux.chave[i] = arvoreBFile.readInt();

                    // Ler endereco na posicao i para  "Registro.db"
                    aux.endereco[i] = arvoreBFile.readLong();
                }

                // Ler ultimo ponteiro 'a direita
                aux.noFilho[NoBStar.ordemArvore-1] = arvoreBFile.readLong();
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
                

                // Ler NoBStar e mostrar
                NoBStar aux = new NoBStar();

                // Posicionar ponteiro na posicao de inicio do No
                arvoreBFile.seek(posAtual);

                // Ler numero de elementos no No
                aux.numElementos = arvoreBFile.readShort();
                total += aux.numElementos;

                // Ler informacoes do No
                for (int i = 0; i < NoBStar.ordemArvore-1; i++) {
                    
                    // Ler ponteiro para filho da esquerda da posicao i
                    aux.noFilho[i] = arvoreBFile.readLong();

                    // Ler chave na posicao i
                    aux.chave[i] = arvoreBFile.readInt();

                    // Ler endereco na posicao i para  "Registro.db"
                    aux.endereco[i] = arvoreBFile.readLong();
                }

                // Ler ultimo ponteiro 'a direita
                aux.noFilho[NoBStar.ordemArvore-1] = arvoreBFile.readLong();

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

        // Se nao encontrar, id nao esta' cadastrado
        if(posArvore != -1) {
            NoBStar noB = new NoBStar();
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
        }

        return find;
    }

    /**
     * Metodo para deletar uma musica da arvore, a apartir de seu id.
     * @param chaveProcurada - id da chave pra se deletar.
     */
    public void delete(int chaveProcurada) {

        // Deletar somente se a chave existr
        if (read(chaveProcurada) != -1){
            delete(chaveProcurada, false);
        }       
    }

    /**
     * Metodo para deletar uma musica da arvore, a partir de seu id.
     * @param chaveProcurada - id da musica a ser deletada.
     * @param isDuplicada - boolean para indicar se a chave que e sera' 
     * removida foi duplicada.
    */
    private void delete (int chaveProcurada, boolean isDuplicada) {
        RandomAccessFile arvoreBFile = null;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBDB, "rw");

            NoBStar noB = new NoBStar();
            long posNo;

            // Obter posicao da chave no arquivo
            if (isDuplicada) {
                posNo = noB.encontrarInsercao(chaveProcurada, getRaiz());
            } else {
                posNo = getPosicao(chaveProcurada);

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
                long posAnt = noB.encontrarInsercao(chaveProcurada, posNo);

                NoBStar noAnt = new NoBStar();
                noAnt.lerNoB(posAnt);

                // Obter elementos do antecessor
                int chaveAnt = noAnt.chave[noAnt.numElementos-1];
                long enderecoAnt = noAnt.endereco[noAnt.numElementos-1];

                // Trocar chave procurada pelo antecessor
                noB.trocarChave(posNo, chaveProcurada, chaveAnt, enderecoAnt);

                // Apagar antecessor duplicado
                delete(chaveAnt, true);
            
            // Se ele estiver na folha e nao ceder
            } else {

                // Encontrar NoBStar pai do NoBStar analisado
                NoBStar noPai = new NoBStar();
                long posPai = noPai.encontrarPai(posNo);
                noPai.lerNoB(posPai);

                // Encontrar irmao da esquerda
                NoBStar noIrmaoEsq = new NoBStar();
                long posIrmaoEsq = noIrmaoEsq.encontrarIrmaoEsq(posPai, chaveProcurada);

                // Encontrar irmao da direita
                NoBStar noIrmaoDir = new NoBStar();
                long posIrmaoDir = noIrmaoDir.encontrarIrmaoDir(posPai, chaveProcurada);

                // Informacoes sobre o irmao a ser utilizado
                NoBStar noIrmao;
                long posIrmao;
                int chaveIrmao;
                long enderecoIrmao;

                // Informacoes do pai
                int chavePai;
                long enderecoPai;
        

                // Selecionar o NoBStar com mais elementos
                if(noIrmaoDir.numElementos >= noIrmaoEsq.numElementos) {
                    noIrmao = noIrmaoDir;
                    posIrmao = posIrmaoDir;

                    // Primeiro registro do NoBStar
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

                    // Ultimo registro do NoBStar
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

                    // Inserir chave do pai no noB
                    noB.lerNoB(posNo);
                    noB.inserir(posNo, chavePai, enderecoPai);

                    // Irmao cede a chave
                    noB.swap(posPai, chavePai, enderecoPai, posIrmao, chaveIrmao, enderecoIrmao);

                    // Deletar antiga posicao irmao
                    delete(posIrmao, chavePai, true);               

                // Se pagina irma nao ceder e ficar com menos de 50% de ocupacao
                } else {

                    // Selecionar irmao existente
                    noIrmao = noIrmaoDir;
                    posIrmao = posIrmaoDir;

                    // Testar se irmao da direita e' valido
                    if (noIrmao.numElementos != 0) {

                        // Obter chave pai correspondente
                        int i;
                        for(i = 0; (i < noPai.numElementos) && (noPai.chave[i] < chaveProcurada); i++);
                        chavePai = noPai.chave[i];
                        enderecoPai = noPai.endereco[i];


                    } else {
                        // Ler irmao da esquerda
                        noIrmao = noIrmaoEsq;
                        posIrmao = posIrmaoEsq;
                        
                        // Obter chave pai correspondente
                        int i;
                        for(i = 0; (i < noPai.numElementos) && (noPai.chave[i] < chaveProcurada); i++);
                        chavePai = noPai.chave[i-1];
                        enderecoPai = noPai.endereco[i-1];

                    }

                    // Juntar, no atual, o pai
                    noB.inserir(posNo, chavePai, enderecoPai);
                    posPai = posNo;               

                    // Juntar elementos do irmao
                    for(int i = 0; i < noIrmao.numElementos; i++) {
                        noB.inserir(posNo, noIrmao.chave[i], noIrmao.endereco[i]);
                    }

                    // Apagar chave procurada
                    delete(posNo, chaveProcurada, true);

                    // Apagar no irmao
                    noIrmao.deletarNo(posIrmao);

                    // Apagar o pai do noPai
                    if(isDuplicada) {
                        delete(posPai, chavePai, false);
                    } else {
                        delete(posPai, chavePai, true);
                    }

                    // Repetir busca tantos quantos irmaos existirem
                    int count = 0;
                    while (!isRaiz(posPai) && !noPai.isMaisMetade() && count < NoBStar.ordemArvore) {
                        noPai = corrigirNoB(chavePai, posPai);
                        noPai.lerNoB(posPai);
                        count++;
                    }

                }

            }

            // Fechar arquivo
            arvoreBFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler/escrever o arquivo \"" + arvoreBDB + "\"\n");
        }
    }

    /**
     * Metodo para determinar se uma certa posicao do arquivo pertence a uma raiz.
     * @param posicaoNo - posicao do No para se analizar.
     * @return true, se for raiz; false, caso contrario.
     */
    private boolean isRaiz(long posicaoNo) {
        return posicaoNo == getRaiz();
    }

    /**
     * Metodo para corrigir um No caso ele possua a quantidade de chavez menor
     * que 50% de ocupacao.
     * @param chaveErro - chave no No que esta' errado.
     * @param posErro - posicao do No em arquivo que esta' errado.
     * @return NoB atualizado.
     */
    public NoBStar corrigirNoB (int chaveErro, long posErro) {
        RandomAccessFile arvoreBFile = null;
        NoBStar noIrmao = null;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBDB, "rw");

            // Ler No com erro
            NoBStar noErro = new NoBStar();
            noErro.lerNoB(posErro);                                            

            // Encontrar NoBStar pai do NoBStar analisado
            NoBStar noPai = new NoBStar();
            long posPai = noPai.encontrarPai(posErro);                
            noPai.lerNoB(posPai);

            // Procurar irmaos
            // Encontrar irmao da esquerda
            NoBStar noIrmaoEsq = new NoBStar();
            long posIrmaoEsq = noIrmaoEsq.encontrarIrmaoEsq(posPai, chaveErro);

            // Encontrar irmao da direita
            NoBStar noIrmaoDir = new NoBStar();
            long posIrmaoDir = noIrmaoDir.encontrarIrmaoDir(posPai, chaveErro);  //[40 48 53]

            // Informacoes sobre o irmao a ser utilizado
            long posIrmao;
            int chaveIrmao;
            long enderecoIrmao;
            long noFilhoIrmao;

            // Informacoes do pai
            int chavePai;
            long enderecoPai;

            // Selecionar o NoBStar com mais elementos (preferencialmente 'a direita)
            if(noIrmaoDir.numElementos >= noIrmaoEsq.numElementos) {
                noIrmao = noIrmaoDir;
                posIrmao = posIrmaoDir;

                // Primeiro registro do NoBStar
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

                // Ultimo registro do NoBStar
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
            NoBStar noB = new NoBStar();
            noB.lerNoB(posArvore);

            // Localizar id e deletar
            for(int i = 0; i < noB.numElementos; i++) {

                // Alterar endereco e chave para -1
                if(chaveProcurada == noB.chave[i]) {
                                       
                    // Remanejar elementos para a esquerda
                    noB.remanejarRegistros(i, posArvore, ultimoFilho);

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
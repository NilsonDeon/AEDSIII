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

    /**
     * Metodo para deletar uma musica da arvore, a partir de seu id.
     * @param chaveProcurada - id da musica a ser deletada.
     */
    public void delete (int chaveProcurada) {
        RandomAccessFile arvoreBFile = null;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBDB, "rw");

            // Obter posicao da chave no arquivo
            long posNo = getPosicao(chaveProcurada);

            // Ler No em que a chave esta'
            NoB noB = new NoB();
            noB.lerNoB(posNo);

            // Encontrar NoB pai do NoB analisado
            NoB noPai = new NoB();
            long posPai = noPai.encontrarPai(posNo);
            noPai.lerNoB(posPai);

            // Encontrar irmao da esquerda
            NoB noIrmaoEsq = new NoB();
            long posIrmaoEsq = noIrmaoEsq.encontrarIrmaoEsq(posPai, chaveProcurada);

            // Encontrar irmao da direita
            NoB noIrmaoDir = new NoB();
            long posIrmaoDir = noIrmaoDir.encontrarIrmaoDir(posPai, chaveProcurada);

            // Informacoes sobre o irmao a ser utilizado
            NoB noIrmao;
            long posIrmao;
            int chaveIrmao;
            long enderecoIrmao;

            // Informacoes do pai
            int chavePai;
            long enderecoPai;            


            // Se estiver em uma folha e ela mantiver 50% de ocupacao
            if(noB.isFolha() && noB.isMaisMetade()) {
                
                // Deletar da folha
                delete(posNo, chaveProcurada);
  /*          
            // Se ele nao estiver na folha, trocar pelo anterior
            } else {

                System.out.println("\n Caso2");
*/

            } else {

                // Selecionar o NoB com mais elementos
                if(noIrmaoDir.numElementos >= noIrmaoEsq.numElementos) {
                    noIrmao = noIrmaoDir;
                    posIrmao = posIrmaoDir;

                    // Ultimo registro do NoB
                    chaveIrmao = noIrmaoDir.chave[noIrmaoDir.numElementos-1];
                    enderecoIrmao = noIrmaoDir.endereco[noIrmaoDir.numElementos-1];

                    // Obter chave pai correspondente
                    int i;
                    for(i = 0; (i < noPai.numElementos) && (noPai.chave[i] < chaveProcurada); i++);
                    chavePai = noPai.chave[i];
                    enderecoPai = noPai.endereco[i];

                // Da esquerda e' o maior
                } else {
                    noIrmao = noIrmaoEsq;
                    posIrmao = posIrmaoEsq;

                    // Primeiro registro do NoB
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

                    if (noB.isFolha()) {

                        // Apagar chave desejada
                        delete(posNo, chaveProcurada);

                        // Inserir chave do pai no noB
                        noB.inserir(posNo, chavePai, enderecoPai);

                        // Irmao cede a chave
                        noB.swap(posPai, chavePai, enderecoPai, posIrmao, chaveIrmao, enderecoIrmao);

                        // Deletar antiga posicao irmao
                        delete(posIrmao, chaveIrmao);
                    }
                    

                // Se pagina irma nao ceder e ficar com menos de 50% de ocupacao
                } else {

                    if (noB.isFolha()) {

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
                        IO io = new IO();

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
                        delete(posNo, chaveProcurada);
                        mostrarArquivo();
                        io.readLine("delete(posicao, chaveProcurada)\n");

                        // Apagar no irmao
                        noIrmao.deletarNo(posIrmao);
                        mostrarArquivo();
                        io.readLine("noIrmao.deletarNo(posIrmao)\n");

                        // Apagar o pai do noPai
                        delete(posPai, chavePai);
                        mostrarArquivo();
                        io.readLine("delete(posPai, chavePai)\n");

                    }

                }
            }

            // Fechar arquivo
            arvoreBFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler/escrever o arquivo \"" + arvoreBDB + "\"\n");
        }
    }

/*



              [40 45 60 --]
            /     \
[41 42 -- --]     [51 52 -- --]              REMOVER 151

// Pegar nó atual
noAtual = pegarNoAtual()  = [51 52 -- --]

// Pegar pai
pai = noAtual.pai [40 51 60 -- ]

noIrmaoEsq = [41 42 45 --] 

paiValorCentral = 45
noAual.Valor = 51

// Descer o valor do meio do pai para o irmão à esquerda
if pai.valorCentral < noAtual.valor
    irmaoEsquerda = pai.filhoEsquerda
    irmaoEsquerda.valorCentral = pai.valorCentral
    pai.valorCentral = noAtual.valor

// Juntar o nó atual com o irmão da esquerda
irmaoEsquerda = pai.filhoEsquerda
irmaoEsquerda.filhoDireita = noAtual.filhoEsquerda

if noAtual.filhoEsquerda != null
    noAtual.filhoEsquerda.pai = irmaoEsquerda
irmaoEsquerda.valorDireita = noAtual.valorEsquerda
irmaoEsquerda.filhoDireita = noAtual.filhoDireita
if noAtual.filhoDireita != null
    noAtual.filhoDireita.pai = irmaoEsquerda
pai.filhoEsquerda = irmaoEsquerda


 */

    /**
     * Metodo privado para deletar uma musica na posicao desejada na arvore.
     * @param posArvore - posicao da musica na arvore.
     * @param chaveProcurada -  id da musica para se deletar.
     */
    private void delete(long posArvore, int chaveProcurada) {
        RandomAccessFile arvoreBFile = null;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBDB, "rw");

            // Ler No em que a chave esta'
            NoB noB = new NoB();
            noB.lerNoB(posArvore);

            // Localizar id e deletar
            for(int i = 0; i < noB.numElementos; i++) {

                // Alterar endereco e chave para -1
                if(chaveProcurada == noB.chave[i]) {
                                       
                    // Remanejar elementos para a esquerda
                    noB.remanejarRegistros(i, posArvore);

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



    public void remontarArvoreB() {}

}



/*

 100 : 40 60 
 250 : 30 37
 400: 41 45 51 52
 550: 
 700: 70 77 83 
 


                    // Juntar, no atual, o pai
                    noB.inserir(posicao, chavePai, enderecoPai);

                    // Juntar elementos do irmao
                    for(int i = 0; i < noIrmao.numElementos; i++) {
                        noB.inserir(posicao, noIrmao.chave[i], noIrmao.endereco[i]);
                    }

                    // Apagar chave procurada
                    delete(posicao, chaveProcurada);

                    // Apagar no irmao
                    noIrmao.deletarNo(posIrmao);

                    // Apagar o pai do noPai
                    delete(posPai, chavePai);


Pos [       0]: raiz: |     308|
Pos [       8]: ( 6): |      -1|   25|    8313|      -1|   26|    8708|      -1|   27|    9082|      -1|   28|    9456|      -1|   29|    9846|      -1|   30|   10210|      -1|   -1|      -1|      -1|
Pos [     158]: ( 3): |      -1|    1|       4|      -1|    2|     346|      -1|    3|     745|      -1|   -1|      -1|      -1|   -1|      -1|      -1|   -1|      -1|      -1|   -1|      -1|      -1|
Pos [     308]: ( 6): |     158|    4|    1053|     458|    8|    2372|     608|   12|    3691|     758|   16|    5134|     908|   20|    6602|    1058|   24|    7956|       8|   -1|      -1|      -1|
Pos [     458]: ( 3): |      -1|    5|    1371|      -1|    6|    1714|      -1|    7|    2053|      -1|   -1|      -1|      -1|   -1|      -1|      -1|   -1|      -1|      -1|   -1|      -1|      -1|
Pos [     608]: ( 3): |      -1|    9|    2692|      -1|   10|    3018|      -1|   11|    3331|      -1|   -1|      -1|      -1|   -1|      -1|      -1|   -1|      -1|      -1|   -1|      -1|      -1|
Pos [     758]: ( 3): |      -1|   13|    4097|      -1|   14|    4420|      -1|   15|    4767|      -1|   -1|      -1|      -1|   -1|      -1|      -1|   -1|      -1|      -1|   -1|      -1|      -1|
Pos [     908]: ( 3): |      -1|   17|    5487|      -1|   18|    5854|      -1|   19|    6244|      -1|   -1|      -1|      -1|   -1|      -1|      -1|   -1|      -1|      -1|   -1|      -1|      -1|
Pos [    1058]: ( 3): |      -1|   21|    6941|      -1|   22|    7282|      -1|   23|    7615|      -1|   -1|      -1|      -1|   -1|      -1|      -1|   -1|      -1|      -1|   -1|      -1|      -1|



Pos [       0]: raiz: |     308|
Pos [       8]: ( 6): |      -1|   25|    8313|      -1|   26|    8708|      -1|   27|    9082|      -1|   28|    9456|      -1|   29|    9846|      -1|   30|   10210|      -1|   -1|      -1|      -1|
Pos [     158]: ( 3): |      -1|    1|       4|      -1|    2|     346|      -1|    3|     745|      -1|   -1|      -1|      -1|   -1|      -1|      -1|   -1|      -1|      -1|   -1|      -1|      -1|
Pos [     308]: ( 6): |     158|    4|    1053|     458|    8|    2372|     608|   12|    3691|     758|   16|    5134|     908|   20|    6602|    1058|   24|    7956|       8|   -1|      -1|      -1|
Pos [     458]: ( 3): |      -1|    5|    1371|      -1|    6|    1714|      -1|    7|    2053|      -1|   -1|      -1|      -1|   -1|      -1|      -1|   -1|      -1|      -1|   -1|      -1|      -1|
Pos [     608]: ( 6): |      -1|   -1|      -1|      -1|    9|    2692|      -1|   10|    3018|      -1|   13|    4097|      -1|   14|    4420|      -1|   15|    4767|      -1|   -1|      -1|      -1|
Pos [     758]: ( 3): |      -1|   13|    4097|      -1|   14|    4420|      -1|   15|    4767|      -1|   -1|      -1|      -1|   -1|      -1|      -1|   -1|      -1|      -1|   -1|      -1|      -1|
Pos [     908]: ( 3): |      -1|   17|    5487|      -1|   18|    5854|      -1|   19|    6244|      -1|   -1|      -1|      -1|   -1|      -1|      -1|   -1|      -1|      -1|   -1|      -1|      -1|
Pos [    1058]: ( 3): |      -1|   21|    6941|      -1|   22|    7282|      -1|   23|    7615|      -1|   -1|      -1|      -1|   -1|      -1|      -1|   -1|      -1|      -1|   -1|      -1|      -1|

 */
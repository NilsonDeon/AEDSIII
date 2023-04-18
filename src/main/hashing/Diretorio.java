// Package
package hashing;

// Bibliotecas
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * Classe Bucket responsavel por criar e manipular o diretorio de armazenamento
 * para o Hashing Extensivel.
 */
public class Diretorio {

    private static final String diretorioDB = "./src/resources/Diretorio.db";
    private static final String bucketDB = "./src/resources/Bucket.db";

    // Tamanho ocupado pelo bucket
    /* 
       p'  + numElementos + 1136 * (chave  + endereco)
       int + short        + 1136 * (int    + long)
       4   + 2            + 1136 * (4 + 8)   = 13638
    */
    private static final long tamTotalBucket = 13638;

    protected int profundidadeGlobal;
    protected int tamDiretorio;
    protected long posBucket[];

    /**
     * Construtor padrao da classe Diretorio.
     */
    public Diretorio () {
        this(1);
    }

    /**
     * Construtor da classe Diretorio com passagem de parametros.
     * @param profundidadeGlobal - profundidade para se inicializar o diretorio.
     * @throws Exception Se ocorrer algum erro ao manipular o arquivo.
     */
    public Diretorio (int profundidadeGlobal) {

        // Gerar excecao caso a pronfundidade global seja menor que 1
        if (profundidadeGlobal < 1) {
            throw new IllegalArgumentException("ERRO: profundidade global invalida: " + profundidadeGlobal);
        }

        // Instanciar os arrays e inicializar as posicoes dos buckets 
        this.profundidadeGlobal = profundidadeGlobal;
        tamDiretorio = (int)Math.pow(2.0, this.profundidadeGlobal);
        posBucket = new long[tamDiretorio];
        long pos = 0;
        for(int i = 0; i < tamDiretorio; i++) {
            posBucket[i] = pos;
            pos += tamTotalBucket;
        }
    }

    /**
     * Metodo para escrever o diretorio em arquivo, como fluxo de bytes.
     */
    public void criarDiretorio() {
        RandomAccessFile diretorioFile = null;

        try {
            diretorioFile = new RandomAccessFile (diretorioDB, "rw");

            // Escrever, no cabecalho, a profundidade global
            diretorioFile.seek(0);
            byte[] profundidadeBytes = ByteBuffer.allocate(4).putInt(profundidadeGlobal).array();
            diretorioFile.write(profundidadeBytes);

            // Escrever as posicoes dos buckets, inicializando com zero
            for (int i = 0; i < tamDiretorio; i++) {
                long posLong = posBucket[i];
                byte[] posBytes = ByteBuffer.allocate(8).putLong(posLong).array();
                diretorioFile.write(posBytes);
            }

            // Fechar arquivo
            diretorioFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao escrever o arquivo \"" + diretorioFile + "\"\n");
        }
    }

    /**
     * Metodo para ler o diretorio de arquivo, como fluxo de bytes.
     */
    public void lerDiretorio() {
        RandomAccessFile diretorioFile = null;

        try {
            diretorioFile = new RandomAccessFile (diretorioDB, "rw");

            // Ler profundidade atual
            diretorioFile.seek(0);
            profundidadeGlobal = diretorioFile.readInt();
            tamDiretorio = (int)Math.pow(2.0, profundidadeGlobal);
            
            // Ler os buckets em arquivo
            posBucket = new long[tamDiretorio];
            for (int i = 0; i < tamDiretorio; i++) {
                posBucket[i] = diretorioFile.readLong();
            }

            // Fechar arquivo
            diretorioFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler o arquivo \"" + diretorioFile + "\"\n");
        }
    }

    /**
     * Metodo para aumentar a profundidade do diretorio em arquivo.
     * @param finalArquivoBucket - posicao final do arquivo de bucket, na qual
     * deve-se inserir o novo bucket.
     */
    public void aumentarProfundidade(long finalArquivoBucket) {
        RandomAccessFile diretorioFile = null;

        try {
            diretorioFile = new RandomAccessFile (diretorioDB, "rw");

            // Ler profundidade atual
            diretorioFile.seek(0);
            profundidadeGlobal = diretorioFile.readInt();
            profundidadeGlobal++;

            // Atualizar profundidade
            diretorioFile.seek(0);
            byte[] profundidadeBytes = ByteBuffer.allocate(4).putInt(profundidadeGlobal).array();
            diretorioFile.write(profundidadeBytes);

            // Atualizar tamanho diretorio
            int tamDiretorioAntigo = tamDiretorio;
            tamDiretorio = (int)Math.pow(2.0, profundidadeGlobal);
            long newPosBucket[] = new long[tamDiretorio];
            
            // Reposicionar ponteiro no fim do arquivo
            long fimArquivoAntigo = diretorioFile.length();
            diretorioFile.seek(fimArquivoAntigo);

            // Atualizar novos buckets
            for (int i = 0; i < tamDiretorioAntigo; i++) {
                
                // Apontar para as antigas posicoes
                newPosBucket[i] = newPosBucket[tamDiretorioAntigo+i] = posBucket[i];

                // Salvar novas posicoes
                long posLong = newPosBucket[tamDiretorioAntigo+i];
                byte[] posBytes = ByteBuffer.allocate(8).putLong(posLong).array();
                diretorioFile.write(posBytes);   
            }
            posBucket = newPosBucket;

            // Fechar arquivo
            diretorioFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler/escrever o arquivo \"" + diretorioFile + "\"\n");
        }
    }

    /**
     * Metodo para redistribuir os arquivos em de acordo com o novo hash ao
     * aumentar o diretorio.
     * @param posicao - posicao do arquivo de hash, na qual o bucket colidido
     * se encontra.
     */
    public void redistribuir(long posicao) {

        RandomAccessFile bucketFile = null;
        Bucket bucket = new Bucket();

        try {
            bucketFile = new RandomAccessFile (bucketDB, "rw");

            // Obter profundidade do bucket
            bucketFile.seek(posicao);
            bucket.profundidadeLocal = bucketFile.readInt();

            // Obter numero de elementos
            bucket.numElementos = bucketFile.readShort();

            // Testar se valores do bucket estao de acordo com nova profundidade
            int cont = 0;
            while(cont < bucket.tamBucket) {

                long posicaoAtual = bucketFile.getFilePointer();

                int chave = bucketFile.readInt();
                long endereco = bucketFile.readLong();

                int posHash = chave % (int)Math.pow(2.0, bucket.profundidadeLocal);
                int newPosHash = chave % (int)Math.pow(2.0, profundidadeGlobal);
                
                if (newPosHash != posHash) {
                    long newPosicao = posBucket[newPosHash];

                    Bucket aux = new Bucket();

                    // Trocar de bucket a chave
                    aux.inserir(newPosicao, chave, endereco);
                    bucket.remover(posicao, posicaoAtual);
                }

                cont++;
            }

            // Fechar arquivo
            bucketFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler o arquivo \"" + bucketFile + "\"\n");
        }

    }

    /**
     * Metodo para reposicionar o ponteiro do bucket que colidiu.
     * @param id - codigo da Musica que, ao tentar ser inserida, deu erro.
     * @param posicaoBucket - posicao do novo bucket inserido no arquivo "Bucket.db".
     * @param profundidade - profundidade antiga do bucket que colidiu.
     */
    public void atualizarPonteiro(int id, long posicaoBucket, int profundidade) {
        RandomAccessFile diretorioFile = null;

        try {
            diretorioFile = new RandomAccessFile (diretorioDB, "rw");

            // Obter tamanho antigo
            int tamAntigo = (int)Math.pow(2.0, profundidade);
            
            // Calcular hash, com profundidade local
            int posHash = id % tamAntigo;

            // Trocar ponteiro para nova posicao do arquivo de
            int newPos = posHash+tamAntigo;
            posBucket[newPos] = posicaoBucket;

            // Posicionar ponteiro
            long ponteiro = 4 + 8*(newPos);  // int + long*(newPos)
            diretorioFile.seek(ponteiro);

            // Salvar nova posicao no arquivo
            long posLong = posBucket[posHash];
            byte[] posBytes = ByteBuffer.allocate(8).putLong(posLong).array();
            diretorioFile.write(posBytes); 

            // Fechar arquivo
            diretorioFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao escrever o arquivo \"" + diretorioFile + "\"\n");
        }
    }

    /**
     * Metodo para sobrescrever o toString do Objeto e converter os atributos
     * da classe Diretorio em uma string.
     */
    public String toString() {
        String diretorio = "Diretorio = [";
        for(int i = 0; i < tamDiretorio; i++) {
            diretorio += posBucket[i] + ", ";
        }

        diretorio += "\b\b].\t\t pGlobal: " + profundidadeGlobal;

        return diretorio;
    }

}
package hashing;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import app.IO;

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
    public Diretorio () throws Exception {
        this(1);
    }

    /**
     * Construtor da classe Diretorio com passagem de parametros.
     * @param profundidadeGlobal - profundidade para se inicializar o diretorio.
     * @throws Exception Se ocorrer algum erro ao manipular o arquivo.
     */
    public Diretorio (int profundidadeGlobal) throws Exception {
        if (profundidadeGlobal <= 0) {
            throw new Exception ("\nERRO: Diretorio(" + profundidadeGlobal + ") -> profundidade global invalida!\n");
        }

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
     * Metodo para escrever um diretorio em arquivo, como fluxo de bytes.
     * @throws Exception Se ocorrer algum erro ao manipular o arquivo.
     */
    public void criarDiretorio() throws Exception {
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
        } catch (IOException e) {
            System.out.println("\nERRO: Ocorreu um erro de escrita no " +
                               "arquivo \"" + diretorioDB + "\"\n");
        } finally {
            if (diretorioFile != null) diretorioFile.close();
        }
    }

    /**
     * Metodo para ler um diretorio de arquivo, como fluxo de bytes.
     * @throws Exception Se ocorrer algum erro ao manipular o arquivo.
     */
    public void lerDiretorio() throws Exception {
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

        } catch (IOException e) {
            System.out.println("\nERRO: Ocorreu um erro de leitura do " +
                               "arquivo \"" + diretorioDB + "\"\n");
        } finally {
            if (diretorioFile != null) diretorioFile.close();
        }
    }

    /**
     * Metodo para aumentar a profundidade do diretorio em arquivo.
     * @param finalArquivoBucket - posicao final do arquivo de bucket, na qual
     * deve-se inserir o novo bucket.
     * @throws Exception Se ocorrer algum erro ao manipular o arquivo.
     */
    public void aumentarProfundidade(long finalArquivoBucket) throws Exception {
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

        } catch (IOException e) {
            System.out.println("\nERRO: Ocorreu um erro de escrita no " +
                               "arquivo \"" + diretorioDB + "\"\n");
        } finally {
            if (diretorioFile != null) diretorioFile.close();
        }
    }

    /**
     * Metodo para redistribuir os arquivos em de acordo com o novo hash ao
     * aumentar o diretorio.
     * @param posHash - posicao de hash, na qual houve colisao.
     * @param posicao - posicao do arquivo de hash, na qual o bucket colidido
     * se encontra.
     * @throws Exception Se ocorrer algum erro ao manipular o arquivo.
     */
    public void redistribuir(int posHash, long posicao) throws Exception {

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
            for (int i = 0; i < bucket.numElementos; i++) {

                int chave = bucketFile.readInt();
                long endereco = bucketFile.readLong();

                //System.out.println("\nposicao: " + posicao);
                //System.out.println("\nbucket.chave[i]: " + bucket.chave[i] + "\t(int)Math.pow(2.0, bucket.profundidadeLocal: " + (int)Math.pow(2.0, bucket.profundidadeLocal));

                int newPosHash = chave % (int)Math.pow(2.0, bucket.profundidadeLocal);
                
                // Se posicao mudar com nova profundidade
                //System.out.println("\nnewPosHash: " + newPosHash + "\tposHash: " + posHash);
                if (newPosHash != posHash) {
                    long newPosicao = posBucket[newPosHash];

                    //System.out.println("\newPosicao: " + newPosicao);
                    Bucket aux = new Bucket();

                    // Trocar de bucket a chave
                    aux.inserir(newPosicao, chave, endereco);
                    bucket.remover(i, posicao, chave);
                    i--;
                }
            }

        } catch (IOException e) {
            System.out.println("\nERRO: Ocorreu um erro de escrita no " +
                               "arquivo \"" + bucketDB + "\"\n");
        } finally {
            if (bucketFile != null) bucketFile.close();
        }

    }

    /**
     * Metodo para reposicionar o ponteiro do bucket que colidiu.
     * @param id - codigo da Musica que, ao tentar ser inserida, deu erro.
     * @param posicaoBucket - posicao do novo bucket inserido no arquivo "Bucket.db".
     * @param profundidade - profundidade antiga do bucket que colidiu.
     * @throws Exception Se ocorrer algum erro ao manipular o arquivo.
     */
    public void atualizarPonteiro(int id, long posicaoBucket, int profundidade) throws Exception {
        RandomAccessFile diretorioFile = null;

        try {
            diretorioFile = new RandomAccessFile (diretorioDB, "rw");
            boolean ok = false;

            // Obter tamanho antigo
            int tamAntigo = (int)Math.pow(2.0, profundidade);
            
            // Calcular hash, com profundidade local
            int posHash = id % tamAntigo;
           
            for (int i = 0; i < tamAntigo && ok == false; i++) {

                // Encontrar nova posicao do diretorio para trocar ponteiro
                if (i == posHash) {

                    // Trocar ponteiro para nova posicao do arquivo de
                    int newPos = i+tamAntigo;
                    posBucket[newPos] = posicaoBucket;

                    //System.out.print("\ni: " + i);
                   // System.out.print("\nnewPos: " + newPos);
                   // System.out.print("\ntamAntigo: " + tamAntigo);
                    
                    // Posicionar ponteiro
                    long ponteiro = 4 + 8*(newPos);  // int + long*(newPos)
                    //System.out.print("\nponteiro: " + ponteiro);
                    diretorioFile.seek(ponteiro);

                  //  IO io = new IO();io.readLine("\nENTER");

                    // Salvar nova posicao no arquivo
                    long posLong = posBucket[newPos];
                    byte[] posBytes = ByteBuffer.allocate(8).putLong(posLong).array();
                    diretorioFile.write(posBytes); 

                    ok = true;
                }
            }

        } catch (IOException e) {
            System.out.println("\nERRO: Ocorreu um erro de escrita no " +
                               "arquivo \"" + diretorioDB + "\"\n");
        } finally {
            if (diretorioFile != null) diretorioFile.close();
        }
    }

}
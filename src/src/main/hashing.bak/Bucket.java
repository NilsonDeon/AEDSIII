package hashing;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class Bucket {
    protected int profundidadeLocal;
    protected short numElementos;
    protected int chave[];
    protected long endereco[];

    private static final int tamBucket = 1136;  // 5% * 22725 registros
    private static final String bucketDB = "./src/resources/Bucket.db";

    /**
     * Construtor padrao da classe Bucket.
     */
    public Bucket() throws Exception {
        this(1);
    }

    /**
     * Construtor da classe Bucket com passagem de parametros.
     * @throws Exception Se ocorrer algum erro ao manipular os arquivos.
     */
    public Bucket(int profundidadeLocal) throws Exception {
        if (profundidadeLocal <= 0) {
            throw new Exception ("\nERRO: Bucket(" + profundidadeLocal + ") -> profundidade local invalida!\n");
        }

        this.profundidadeLocal = profundidadeLocal;
        numElementos = 0;
        chave = new int[tamBucket];
        endereco = new long[tamBucket];

        for(int i = 0; i < tamBucket; i++) {
            chave[i] = -1;
            endereco[i] = -1;
        }
    }

    /**
     * Metodo para criar os buckets iniciais, de acordo com a profundidade.
     * @throws Exception Se ocorrer algum erro ao manipular os arquivos.
     */
    public void inicializarBuckets() throws Exception {

        // Inicializar os primeiros buckets vazios
        int numBuckets = (int)Math.pow (2.0, profundidadeLocal);
        for (int i = 0; i < numBuckets; i++) {
            criarBucket();
        }
    }

    /**
     * Metodo para escrever um bucket em um arquivo binario como fluxo de bytes.
     * @throws Exception Se ocorrer algum erro ao manipular os arquivos.
     */
    public void criarBucket() throws Exception {
        RandomAccessFile bucketFile = null;

        try {
            bucketFile = new RandomAccessFile (bucketDB, "rw");

            // Posicionar ponteiro no final do arquivo
            long fimArquivo = bucketFile.length();
            bucketFile.seek(fimArquivo);

            // Escrever profundidade bucket
            byte[] profundidadeBytes = ByteBuffer.allocate(4).putInt(profundidadeLocal).array();
            bucketFile.write(profundidadeBytes);

            // Escrever numero de elemento no bucket
            byte[] numElementosBytes = ByteBuffer.allocate(2).putShort(numElementos).array();
            bucketFile.write(numElementosBytes);
            
            for (int i = 0; i < tamBucket; i++) {

                // Escrever chave (ID)
                byte[] chaveBytes = ByteBuffer.allocate(4).putInt(chave[i]).array();
                bucketFile.write(chaveBytes);

                // Escrever endereco no arquivo "Registros.db"
                byte[] enderecoBytes = ByteBuffer.allocate(8).putLong(endereco[i]).array();
                bucketFile.write(enderecoBytes);
            }
            
        } catch (IOException e) {
            System.out.println("\nERRO: Ocorreu um erro de escrita no " +
                               "arquivo \"" + bucketDB + "\"\n");
        } finally {
            if (bucketFile != null) bucketFile.close();
        }
    }

    /**
     * Metodo para aumentar a profundidade de um bucket em arquivo.
     * @param posBucket - posicao do bucket em arquivo que tera' a profundidade
     * aumentada.
     * @throws Exception Se ocorrer algum erro ao manipular os arquivos.
     */
    public void aumentarProfundidade (long posBucket) throws Exception {
        RandomAccessFile bucketFile = null;

        try {
            bucketFile = new RandomAccessFile (bucketDB, "rw");

            // Posicionar ponteiro no bucket desejado
            bucketFile.seek(posBucket);

            // Aumentar profundidade
            profundidadeLocal = bucketFile.readInt();
           // System.out.println("\nprofundidadeLocal: " + profundidadeLocal);
            profundidadeLocal++;

            // Reescrever novo valor
            bucketFile.seek(posBucket);
            byte[] profundidadeBytes = ByteBuffer.allocate(4).putInt(profundidadeLocal).array();
            bucketFile.write(profundidadeBytes);
            
        } catch (IOException e) {
            System.out.println("\nERRO: Ocorreu um erro de leitura do " +
                               "arquivo \"" + bucketDB + "\"\n");
        } finally {
            if (bucketFile != null) bucketFile.close();
        }
    }

    /**
     * Metodo privado para, a partir da posicao de inicio de um bucket, obter
     * a primeira posicao vazia.
     * @param posBucket - posicao do bucket que se deseja obter a posicao vazia.
     * @return posicaoVazia - desejada.
     */
    private long getPosicaoVazia (long posBucket) {
        long bytesShort = 2;          // profundidadeLocal
        long bytesInt = 4;            // numElementos
        long bytesChaveEndereco = 12;

        return posBucket + bytesShort + bytesInt + (numElementos-1)*(bytesChaveEndereco);
    }

    /**
     * Metodo para inserir um registro em um bucket no arquivo.
     * @param posBucket - posicao do bucket, no qual se deseja inserir.
     * @param chave - id da Musica a ser inserida.
     * @param endereco - posicao da Musica no arquivo "Registros.db".
     * @return true, se inserido corretamente; false, caso contrario.
     * @throws Exception Se ocorrer algum erro ao manipular os arquivos.
     */
    public boolean inserir (long posBucket, int chave, long endereco) throws Exception {

        boolean inserido = false;
        RandomAccessFile bucketFile = null;

        try {
            bucketFile = new RandomAccessFile (bucketDB, "rw");
           
            // Obter profundidade do bucket
            bucketFile.seek(posBucket);
            profundidadeLocal = bucketFile.readInt();

            // Obter numero de elementos
            numElementos = bucketFile.readShort();
            numElementos++;

            // Posicionar o ponteiro no inicio do primeiro bucket vazio
            long proximaPosicao = getPosicaoVazia(posBucket);
            bucketFile.seek(proximaPosicao);

            // Escrever nova chave (ID)
            byte[] chaveBytes = ByteBuffer.allocate(4).putInt(chave).array();
            bucketFile.write(chaveBytes);

            // Escrever novo endereco do arquivo "Registros.db"
            byte[] enderecoBytes = ByteBuffer.allocate(8).putLong(endereco).array();
            bucketFile.write(enderecoBytes);

            // Incrementar numero de elementos
            long posNumElementos = posBucket + 4;
            bucketFile.seek(posNumElementos);
            byte[] numElementosBytes = ByteBuffer.allocate(2).putShort(numElementos).array();
            bucketFile.write(numElementosBytes);

            inserido = true;
            
        } catch (IOException e) {
            System.out.println("\nERRO: Ocorreu um erro de escrita no " +
                               "arquivo \"" + bucketDB + "\"\n");
        } finally {
            if (bucketFile != null) bucketFile.close();
        }

        return inserido;
    }

    /**
     * Metodo privado para, a partir da posicao de inicio de um bucket, obter
     * a posicao desejada.
     * @param posBucket - posicao do bucket no arquivo.
     * @param posicaoElemento - posicao do elemento que se deseja encontrar.
     * @return posicaoDesejada.
     */
    private long getPosicao (long posBucket, int posicaoElemento) {
        long bytesShort = 2;          // profundidadeLocal
        long bytesInt = 4;            // numElementos
        long bytesChaveEndereco = 12;

        return posBucket + bytesShort + bytesInt + ((long)posicaoElemento)*(bytesChaveEndereco);
    }

    /**
     * Metodo para remover um registro em um bucket no arquivo.
     * @param posicaoElemento - posicao do elemento que se deseja remover.
     * @param posBucket - posicao do bucket no arquivo.
     * @param chaveProcurada - id da Musica a ser removida.
     * @return true, se removido corretamente; false, caso contrario.
     * @throws Exception Se ocorrer algum erro ao manipular os arquivos.
     */
    public boolean remover (int posicaoElemento, long posBucket, int chaveProcurada) throws Exception {

        boolean removido = false;

        RandomAccessFile bucketFile = null;
        try {
            bucketFile = new RandomAccessFile (bucketDB, "rw");

            // Obter profundidade do bucket
            bucketFile.seek(posBucket);
            profundidadeLocal = bucketFile.readInt();

            // Obter numero de elementos
            numElementos = bucketFile.readShort();
            numElementos--;

            int chave = -1;
            long endereco = -1;

            // Encontrar elemento a ser removido
            long posicaoRemover = getPosicao(posBucket, posicaoElemento);
            bucketFile.readInt();
            bucketFile.readLong();

            long posicaoAtual = bucketFile.getFilePointer();
            
            // Shift elementos para esquerda
            int i = posicaoElemento;
            while(i < numElementos) {

                // Ler proximos valores
                if(posicaoAtual != bucketFile.length()){

                   // Ler registro
                   chave = bucketFile.readInt();
                   endereco = bucketFile.readLong();

                   // Salvar posicao atual
                   posicaoAtual = bucketFile.getFilePointer();

                   // Voltar uma posicao
                   bucketFile.seek(posicaoRemover);

                   // Escrever nova chave (ID)
                   byte[] chaveBytes = ByteBuffer.allocate(4).putInt(chave).array();
                   bucketFile.write(chaveBytes);
 
                   // Escrever novo endereco do arquivo "Registros.db"
                   byte[] enderecoBytes = ByteBuffer.allocate(8).putLong(endereco).array();
                   bucketFile.write(enderecoBytes);
   
                   // Salvar posicao para remocao
                   posicaoRemover = bucketFile.getFilePointer();
                }

                i++;
            }

            // Decrementar numero de elementos
            long posNumElementos = posBucket + 4;
            bucketFile.seek(posNumElementos);
            byte[] numElementosBytes = ByteBuffer.allocate(2).putShort(numElementos).array();
            bucketFile.write(numElementosBytes);

        } catch (IOException e) {
            System.out.println("\nERRO: Ocorreu um erro de escrita no " +
                               "arquivo \"" + bucketDB + "\"\n");
        } finally {
            if (bucketFile != null) bucketFile.close();
        }

        return removido; 
    }

    /**
     * Metodo para se obter a posicao final do arquivo de buckets.
     * @return finalArq - posicao final do arquivo.
     * @throws Exception Se ocorrer algum erro ao manipular os arquivos.
     */
    public long getFinalArquivo () throws Exception {

        RandomAccessFile bucketFile = null;
        long finalArq = 0;
        try {
            bucketFile = new RandomAccessFile (bucketDB, "rw");
            finalArq = bucketFile.length();

        } catch (IOException e) {
            System.out.println("\nERRO: Ocorreu um erro de escrita no " +
                               "arquivo \"" + bucketDB + "\"\n");
        } finally {
            if (bucketFile != null) bucketFile.close();
        }

        return finalArq; 
    }

    /**
     * Metodo para determinar se um bucket esta' cheio.
     * @param posBucket - posicao do bucket no arquivo.
     * @return true, se estiver cheio; false, caso contrario.
     * @throws Exception Se ocorrer algum erro ao manipular os arquivos.
     */
    public boolean isFull(long posBucket) throws Exception {

        RandomAccessFile bucketFile = null;

        try {
            bucketFile = new RandomAccessFile (bucketDB, "rw");

            // Obter profundidade do bucket
            bucketFile.seek(posBucket);
            profundidadeLocal = bucketFile.readInt();

            // Obter numero de elementos
            numElementos = bucketFile.readShort();

        } catch (IOException e) {
            System.out.println("\nERRO: Ocorreu um erro de escrita no " +
                               "arquivo \"" + bucketDB + "\"\n");
        } finally {
            if (bucketFile != null) bucketFile.close();
        }

        return numElementos == tamBucket;     
    }

}
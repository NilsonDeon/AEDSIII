package hashing;

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

    public Bucket() throws Exception {
        this(1);
    }

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

        // Inicializar os primeiros buckets vazios
        int numBuckets = (int)Math.pow (2.0, profundidadeLocal);
        for (int i = 0; i < numBuckets; i++) {
            criarBucket();
        }
    }

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
            System.out.println("\nERRO: Ocorreu um erro de escrita no" +
                               "arquivo \"" + bucketDB + "\"\n");
        } finally {
            if (bucketFile != null) bucketFile.close();
        }
    }

    public void buscarBucket (long posBucket) throws Exception {
        RandomAccessFile bucketFile = null;

        try {
            bucketFile = new RandomAccessFile (bucketDB, "rw");

            // Posicionar ponteiro no bucket desejado
            bucketFile.seek(posBucket);

            // Ler bucket apontado
            profundidadeLocal = bucketFile.readInt();
            numElementos = bucketFile.readShort();

            for (int i = 0; i < numElementos; i++) {
                chave[i] = bucketFile.readInt();
                endereco[i] = bucketFile.readLong();
            }
            
        } catch (IOException e) {
            System.out.println("\nERRO: Ocorreu um erro de leitura do" +
                               "arquivo \"" + bucketDB + "\"\n");
        } finally {
            if (bucketFile != null) bucketFile.close();
        }
    }

    public void aumentarProfundidade (long posBucket) throws Exception {
        RandomAccessFile bucketFile = null;

        try {
            bucketFile = new RandomAccessFile (bucketDB, "rw");

            // Posicionar ponteiro no bucket desejado
            bucketFile.seek(posBucket);

            // Aumentar profundidade
            profundidadeLocal = bucketFile.readInt();
            profundidadeLocal++;

            // Reescrever novo valor
            byte[] profundidadeBytes = ByteBuffer.allocate(4).putInt(profundidadeLocal).array();
            bucketFile.write(profundidadeBytes);
            
        } catch (IOException e) {
            System.out.println("\nERRO: Ocorreu um erro de leitura do" +
                               "arquivo \"" + bucketDB + "\"\n");
        } finally {
            if (bucketFile != null) bucketFile.close();
        }
    }

    private long bytesBucket (long posBucket) {
        long bytesShort = 2;          // profundidadeLocal
        long bytesInt = 4;            // numElementos
        long bytesChaveEndereco = 12;

        return posBucket + bytesShort + bytesInt + (numElementos-1)*(bytesChaveEndereco);
    }

    public boolean inserir (long posBucket, int chave, long endereco) throws Exception {

        boolean inserido = false;

        // Carregar bucket para memoria primaria
        buscarBucket(posBucket);

        if (isFull()) {
            throw new Exception ("\nERRO: Bucket.inserir() -> Bucket esta cheio!");
        }

        // Inserir no arquivo novo registro
        RandomAccessFile bucketFile = null;
        try {
            bucketFile = new RandomAccessFile (bucketDB, "rw");

            // Posicionar o ponteiro no inicio do primeiro bucket vazio
            long proximaPosicao = bytesBucket(posBucket);

            // Escrever nova chave (ID)
            byte[] chaveBytes = ByteBuffer.allocate(4).putInt(chave).array();
            bucketFile.write(chaveBytes);

            // Escrever novo endereco no arquivo "Registros.db"
            byte[] enderecoBytes = ByteBuffer.allocate(8).putLong(endereco).array();
            bucketFile.write(enderecoBytes);

            inserido = true;
            
        } catch (IOException e) {
            System.out.println("\nERRO: Ocorreu um erro de escrita no" +
                               "arquivo \"" + bucketDB + "\"\n");
        } finally {
            if (bucketFile != null) bucketFile.close();
        }

        return inserido;
    }


    public boolean remover (long posBucket, int chave) throws Exception {

        boolean inserido = false;

        // Carregar bucket para memoria primaria
        buscarBucket(posBucket);

        if (isFull()) {
            throw new Exception ("\nERRO: Bucket.inserir() -> Bucket esta cheio!");
        }

        // Inserir no arquivo novo registro
        RandomAccessFile bucketFile = null;
        try {
            bucketFile = new RandomAccessFile (bucketDB, "rw");

            // Procurar bucket, mover e reescrever

            inserido = true;

        } catch (IOException e) {
            System.out.println("\nERRO: Ocorreu um erro de escrita no" +
                               "arquivo \"" + bucketDB + "\"\n");
        } finally {
            if (bucketFile != null) bucketFile.close();
        }

        return inserido; 
    }

    public long getFinalArquivo () throws Exception {

        RandomAccessFile bucketFile = null;
        long finalArq = 0;
        try {
            bucketFile = new RandomAccessFile (bucketDB, "rw");
            finalArq = bucketFile.length();

        } catch (IOException e) {
            System.out.println("\nERRO: Ocorreu um erro de escrita no" +
                               "arquivo \"" + bucketDB + "\"\n");
        } finally {
            if (bucketFile != null) bucketFile.close();
        }

        return finalArq; 
    }

    private void moverRegistros (int pos) {
        for (int i = pos; i < numElementos-1; i++) {
            chave[i] = chave[i+1];
            endereco[i] = endereco[i+1];
        }
    }

    public boolean isFull() {
        return numElementos == tamBucket;
    }

    public boolean temEspacoLivre() {
        return numElementos < tamBucket;
    }
}
// Package
package hashing;

// Bibliotecas
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * Classe Bucket responsavel por criar e manipular o bucket de armazenamento
 * para o Hashing Extensivel.
*/
public class Bucket {
    protected int profundidadeLocal;
    protected short numElementos;
    protected int chave[];
    protected long endereco[];

    protected static final int tamBucket = 1136;  // 5% * 22725 registros
    private static final String bucketDB = "./src/resources/Bucket.db";

    /**
     * Construtor padrao da classe Bucket.
    */
    public Bucket() {
        this(1);
    }

    /**
     * Construtor da classe Bucket com passagem de parametros.
    */
    public Bucket(int profundidadeLocal) {

        // Gerar excecao caso a pronfundidade seja menor que 1
        if (profundidadeLocal < 1) {
            throw new IllegalArgumentException("ERRO: profundidade local invalida: " + profundidadeLocal);
        }

        // Instanciar os arrays e inicializar com -1 as posicoes dos buckets 
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
    */
    public void inicializarBuckets() {

        // Inicializar os primeiros buckets vazios
        int numBuckets = (int)Math.pow (2.0, profundidadeLocal);
        for (int i = 0; i < numBuckets; i++) {
            criarBucket();
        }
    }

    /**
     * Metodo para escrever um bucket em um arquivo binario como fluxo de bytes.
    */
    public void criarBucket() {
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

                // Escrever chave
                byte[] chaveBytes = ByteBuffer.allocate(4).putInt(chave[i]).array();
                bucketFile.write(chaveBytes);

                // Escrever endereco
                byte[] enderecoBytes = ByteBuffer.allocate(8).putLong(endereco[i]).array();
                bucketFile.write(enderecoBytes);
            }
            
            // Fechar arquivo
            bucketFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao escrever o arquivo \"" + bucketDB + "\"\n");
        }
    }

    /**
     * Metodo para aumentar a profundidade de um bucket em arquivo.
     * @param posBucket - posicao do bucket em arquivo que tera' a profundidade
     * aumentada.
    */
    public void aumentarProfundidade (long posBucket) {
        RandomAccessFile bucketFile = null;

        try {
            bucketFile = new RandomAccessFile (bucketDB, "rw");

            // Posicionar ponteiro no bucket desejado
            bucketFile.seek(posBucket);

            // Aumentar profundidade
            profundidadeLocal = bucketFile.readInt();
            profundidadeLocal++;

            // Reescrever novo valor
            bucketFile.seek(posBucket);
            byte[] profundidadeBytes = ByteBuffer.allocate(4).putInt(profundidadeLocal).array();
            bucketFile.write(profundidadeBytes);

            // Fechar arquivo
            bucketFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler/escrever o arquivo \"" + bucketDB + "\"\n");
        }
    }

    /**
     * Metodo para inserir um registro em um bucket no arquivo.
     * @param posBucket - posicao do bucket, no qual se deseja inserir.
     * @param newChave - id da Musica a ser inserida.
     * @param newEndereco - posicao da Musica no arquivo "Registros.db".
     * @return true, se inserido corretamente; false, caso contrario.
    */
    public boolean inserir (long posBucket, int newChave, long newEndereco) {

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

            // Posicionar o ponteiro, buscando bucket vazio
            int cont = 0;
            while(cont < tamBucket && inserido == false) {
                long posicaoInserir = bucketFile.getFilePointer();
                int chave = bucketFile.readInt();
                if (chave == -1) {

                    // Voltar a posicao lida
                    bucketFile.seek(posicaoInserir);

                    // Escrever nova chave
                    byte[] chaveBytes = ByteBuffer.allocate(4).putInt(newChave).array();
                    bucketFile.write(chaveBytes);

                    // Escrever novo endereco do arquivo "Registros.db"
                    byte[] enderecoBytes = ByteBuffer.allocate(8).putLong(newEndereco).array();
                    bucketFile.write(enderecoBytes);

                    inserido = true;
                } else {

                    // Ler endereco indesejado (== -1)
                    bucketFile.readLong();
                }
                cont++;
            }

            // Incrementar numero de elementos
            long posNumElementos = posBucket + 4;
            bucketFile.seek(posNumElementos);
            byte[] numElementosBytes = ByteBuffer.allocate(2).putShort(numElementos).array();
            bucketFile.write(numElementosBytes);

            inserido = true;

            // Fechar arquivo
            bucketFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler/escrever o arquivo \"" + bucketDB + "\"\n");
        }

        return inserido;
    }

    /**
     * Metodo para remover um registro em um bucket no arquivo.
     * @param posBucket - posicao de inicio do bucket.
     * @param posElemento - posicao do elemento que se deseja remover.
     * @return true, se removido corretamente; false, caso contrario.
    */
    public boolean remover (long posBucket, long posElemento) {

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

            // Decrementar numElementos
            bucketFile.seek(posBucket+4);
            byte[] numElementosBytes = ByteBuffer.allocate(2).putShort(numElementos).array();
            bucketFile.write(numElementosBytes);

            // Posicionar ponteiro na posicao a se remover
            bucketFile.seek(posElemento);

            // Escrever chave (ID) null
            int chave = -1;
            byte[] chaveBytes = ByteBuffer.allocate(4).putInt(chave).array();
            bucketFile.write(chaveBytes);

            // Escrever endereco null
            long endereco = -1;
            byte[] enderecoBytes = ByteBuffer.allocate(8).putLong(endereco).array();
            bucketFile.write(enderecoBytes);

            // Fechar arquivo
            bucketFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler/escrever o arquivo \"" + bucketDB + "\"\n");
        }

        return removido; 
    }

    /**
     * Metodo para se obter a posicao final do arquivo de buckets.
     * @return finalArq - posicao final do arquivo.
    */
    public long getFinalArquivo () {

        RandomAccessFile bucketFile = null;
        long finalArq = -1;
        try {
            bucketFile = new RandomAccessFile (bucketDB, "rw");

            // Obter posicao final do arquivo
            finalArq = bucketFile.length();

            // Fechar arquivo
            bucketFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler o arquivo \"" + bucketDB + "\"\n");
        }

        return finalArq; 
    }

    /**
     * Metodo para determinar se um bucket esta' cheio.
     * @param posBucket - posicao do bucket no arquivo.
     * @return true, se estiver cheio; false, caso contrario.
    */
    public boolean isFull(long posBucket){

        RandomAccessFile bucketFile = null;

        try {
            bucketFile = new RandomAccessFile (bucketDB, "rw");

            // Obter profundidade do bucket
            bucketFile.seek(posBucket);
            profundidadeLocal = bucketFile.readInt();

            // Obter numero de elementos
            numElementos = bucketFile.readShort();

            // Fechar arquivo
            bucketFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler o arquivo \"" + bucketDB + "\"\n");
        }

        return numElementos == tamBucket;     
    }

    /**
     * Metodo para, a partir de uma posicao, ler o bucket contido nela.
     * @param posicao - so bucket desejado.
    */
    public void lerBucket (long posicao) {

        RandomAccessFile bucketFile = null;
        try {
            bucketFile = new RandomAccessFile (bucketDB, "rw");

            // Obter profundidade do bucket
            bucketFile.seek(posicao);
            profundidadeLocal = bucketFile.readInt();

            // Obter numero de elementos
            numElementos = bucketFile.readShort();

            // Ler bucket 
            for(int i = 0; i < tamBucket; i++) {
                chave[i] = bucketFile.readInt();
                endereco[i] = bucketFile.readLong();
            }

            // Fechar arquivo
            bucketFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler o arquivo \"" + bucketDB + "\"\n");
        }
    }
}
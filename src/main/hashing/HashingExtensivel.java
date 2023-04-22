// Package
package hashing;

// Bibliotecas
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

// Bibliotecas proprias
import app.IO;
import app.Musica;

/**
 * Classe HasshingExtensivel responsavel por implementar o Hash extensivel em
 * memoria secundaria, utilizando as classes Bucket e Diretorio.
 */
public class HashingExtensivel {

    private Diretorio diretorio;
    private static final String registroDB = "./src/resources/Registro.db";
    private static final String diretorioDB = "./src/resources/Diretorio.db";
    private static final String bucketDB = "./src/resources/Bucket.db";

    /**
     * Construtor padrao da classe HashExtensivel.
     */
    public HashingExtensivel() {
        diretorio = new Diretorio();

        // Se diretorio ja existir, carregar em memoria primaria
        File arqDiretorio = new File(diretorioDB);
        if(arqDiretorio.exists()) {
            diretorio.lerDiretorio();
        }
    }

    /**
     * Metodo para inicializar o diretorio em arquivo.
     */
    public void inicializarDiretorio() {
        diretorio = new Diretorio();
        diretorio.salvarDiretorio();
        diretorio.lerDiretorio();
    }

    /**
     * Metodo para inicializar os buckets no arquivo, com valores nulos.
     */
    public void inicializarBuckets() {
        Bucket bucket = new Bucket();
        bucket.inicializarBuckets();
    }

    /**
     * Metodo publico para trazer diretorio para a memoria primaria
     */
    public void lerDiretorio() {
        diretorio.lerDiretorio();
    }

    /**
     * Metodo de hash privado para calcular a posicao do arquivo no diretorio
     * a partir do id e da profundidade Global.
     * @param id - que se deseja obter a posicao.
     * @retun posicao esperada.
     */
    private int hash (int id) {
        return (id % (int)Math.pow(2.0, diretorio.profundidadeGlobal));
    }

    /**
     * Metodo para inserir uma musica nos buckets.
     * @param musica - a ser inserida.
     * @param posicaoRegistro - posicao da musica no arquivo "Registro.db".
     * @return true, se a música foi inserida corretamente; false, caso contrario.
     */
    public boolean inserir (Musica musica, long posicaoRegistro) {
        boolean inserido = false;
        Bucket bucket = new Bucket();

        // Calcular hash
        int id = musica.getId();
        int posHash = hash(id);

        // Encontrar bucket desejado
        long posicao = diretorio.posBucket[posHash];

        // Enquanto bucket apontado pela posicao estiver cheio, 
        // aumentar profundidade
        while (bucket.isFull(posicao)) {

            // Testar se pode fazer novo hash sem aumentar diretorio
            if (diretorio.profundidadeGlobal > bucket.profundidadeLocal) {

                // Criar novo bucket vazio no fim do arquivo
                Bucket bucketAux = new Bucket(diretorio.profundidadeGlobal);
                long finalArquivo = bucketAux.getFinalArquivo();
                bucketAux.criarBucket();

                // Atualizar diretorio
                diretorio.atualizarPonteiro(id, finalArquivo, bucket.profundidadeLocal);

                // Reposicionar buckets
                diretorio.redistribuir(posicao);

                // Corrigir profundidade
                bucket.aumentarProfundidade(posicao);

            // Senao, aumentar profundidade
            } else {
                long finalArqBucket = bucket.getFinalArquivo();
                diretorio.aumentarProfundidade(finalArqBucket);
            }

        }

        // Recalcular hash
        posHash = hash(id);
        posicao = diretorio.posBucket[posHash];

        // Inserir no bucket
        inserido = bucket.inserir(posicao, id, posicaoRegistro);

        // Salvar alteracoes do diretorio
        diretorio.salvarDiretorio();
        
        return inserido;
    }

    /**
     * Metodo para procurar e exibir as informacoes de uma musica a partir do 
     * seu ID.
     * @param idProcurado - id da musica para pesquisar.
     * @return posicao da musica no arquivo "Registro.db".
     */
    public long read(int idProcurado) {

        RandomAccessFile bucketFile = null;

        int chave = -1;
        long endereco = -1;
        boolean find = false;

        try {
            bucketFile = new RandomAccessFile (bucketDB, "rw");

            // Calcular hash
            int posHash = hash(idProcurado);

            // Encontrar bucket desejado
            long posicao = diretorio.posBucket[posHash];
            Bucket bucket = new Bucket();
           
            // Obter profundidade do bucket
            bucketFile.seek(posicao);
            bucket.profundidadeLocal = bucketFile.readInt();

            // Obter numero de elementos
            bucket.numElementos = bucketFile.readShort();

            // Percorrer bucket
            int cont = 0;
            while(cont < bucket.tamBucket && find == false) {

                chave = bucketFile.readInt();
                endereco = bucketFile.readLong();

                // Testar id
                if (chave == idProcurado) {
                    find = true;
                }
                cont++;
            }

            // Fechar arquivo
            bucketFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler o arquivo \"" + bucketDB + "\"\n");
        }

        return endereco;
    }

    /**
     * Metodo para procurar e deletar uma musica a partir do seu ID.
     * @param idProcurado - id da musica para deletar.
     * @return true, se a música foi deletada; false, caso contrario.
     */
    public boolean delete(int idProcurado) {
        boolean find = false;

        RandomAccessFile bucketFile = null;
        RandomAccessFile dbFile = null;

        try {
            bucketFile = new RandomAccessFile (bucketDB, "rw");
            dbFile = new RandomAccessFile (registroDB, "rw");

            // Calcular hash
            int posHash = hash(idProcurado);

            // Encontrar bucket desejado
            long posicao = diretorio.posBucket[posHash];
            Bucket bucket = new Bucket();
           
            // Obter profundidade do bucket
            bucketFile.seek(posicao);
            bucket.profundidadeLocal = bucketFile.readInt();

            // Obter numero de elementos
            bucket.numElementos = bucketFile.readShort();

            // Percorrer bucket
            int cont = 0;
            while(cont < bucket.tamBucket && find == false) {

                long posElemento = bucketFile.getFilePointer();

                int chave = bucketFile.readInt();
                long endereco = bucketFile.readLong();

                // Testar id
                if (chave == idProcurado) {
                    find = true;
                    Musica musicaProcurada = new Musica();
                    dbFile.seek(endereco);

                    // Ler informacoes do registro
                    boolean lapide = dbFile.readBoolean();
                    int tamRegistro = dbFile.readInt();

                    // Testar se registro e' valido
                    if (lapide == false) {

                        // Deletar musica
                        bucket.remover(posicao, posElemento);
                    }

                }
                cont++;
            }

            // Salvar alteracoes do diretorio
            diretorio.salvarDiretorio();

            // Fechar arquivos
            bucketFile.close();
            dbFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler o arquivo \"" + bucketDB + "\"\n");
            System.out.println("\nERRO: " + e.getMessage() + " ao ler o arquivo \"" + registroDB + "\"\n");
        }

        return find;
    }

    /**
     * Metodo para procurar e alterar endereco uma musica a partir do seu ID.
     * @param idProcurado - id da musica para atualizar endereco.
     * @param newEndereco - novo endereco da musica.
     * @return true, se a música foi deletada; false, caso contrario.
     */
    public boolean update(int idProcurado, long newEndereco) {
        boolean find = false;
        RandomAccessFile bucketFile = null;

        try {
            bucketFile = new RandomAccessFile (bucketDB, "rw");

            // Calcular hash
            int posHash = hash(idProcurado);

            // Encontrar bucket desejado
            long posicao = diretorio.posBucket[posHash];
            Bucket bucket = new Bucket();
           
            // Obter profundidade do bucket
            bucketFile.seek(posicao);
            bucket.profundidadeLocal = bucketFile.readInt();

            // Obter numero de elementos
            bucket.numElementos = bucketFile.readShort();

            // Percorrer bucket
            int cont = 0;
            while(cont < bucket.tamBucket && find == false) {

                int chave = bucketFile.readInt();

                long posEndereco = bucketFile.getFilePointer();
                long endereco = bucketFile.readLong();

                // Testar id
                if (chave == idProcurado) {

                    // Atualizar endereco da musica
                    bucketFile.seek(posEndereco);
                    byte[] enderecoBytes = ByteBuffer.allocate(8).putLong(newEndereco).array();
                    bucketFile.write(enderecoBytes);

                    find = true;
                }
                cont++;
            }
            
            // Salvar alteracoes do diretorio
            diretorio.salvarDiretorio();

            // Fechar arquivos
            bucketFile.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler/escrever o arquivo \"" + bucketDB + "\"\n");
        }


        return find;
    }

    public Diretorio getDiretorio() {
        return this.diretorio;
    }

}
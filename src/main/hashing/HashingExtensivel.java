package hashing;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import app.Musica;
import app.IO;

public class HashingExtensivel {

    protected Diretorio diretorio;
    private static final String registroDB = "./src/resources/Registro.db";
    private static final String bucketDB = "./src/resources/Bucket.db";

    /**
     * Construtor padrao da classe HashExtensivel.
     * @throws Exception Se ocorrer algum erro ao manipular o arquivo.
     */
    public HashingExtensivel() throws Exception {
        diretorio = new Diretorio();
    }

    /**
     * Metodo para inicializar o diretorio em arquivo, com valores nulos.
     * @throws Exception Se ocorrer algum erro ao manipular o arquivo.
     */
    public void inicializarDiretorio() throws Exception {
        diretorio.criarDiretorio();
    }

    /**
     * Metodo para inicializar os buckets no arquivo, com valores nulos.
     * @throws Exception Se ocorrer algum erro ao manipular o arquivo.
     */
    public void inicializarBuckets() throws Exception {
        Bucket bucket = new Bucket();
        bucket.inicializarBuckets();
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
     * @throws Exception Se ocorrer algum erro ao manipular o arquivo.
     */
    public boolean inserir (Musica musica, long posicaoRegistro) throws Exception {
        boolean inserido = false;
        Bucket bucket = new Bucket();

        IO io = new IO();

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
        bucket.inserir(posicao, id, posicaoRegistro);
        System.out.println("Inserindo id: " + id);

        return inserido;
    }

    /**
     * Metodo para procurar e exibir as informacoes de uma musica a partir do 
     * seu ID.
     * @param idProcurado - id da musica para pesquisar.
     * @return true, se a música foi encontrada; false, caso contrario.
     * @throws Exception Se ocorrer algum erro ao manipular o arquivo.
     */
    public boolean read(int idProcurado) throws Exception {
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

                int chave = bucketFile.readInt();
                long endereco = bucketFile.readLong();

                // Testar id
                if (chave == idProcurado) {
                    
                    Musica musicaProcurada = new Musica();
                    dbFile.seek(endereco);

                    // Ler informacoes do registro
                    boolean lapide = dbFile.readBoolean();
                    int tamRegistro = dbFile.readInt();

                    // Testar se registro e' valido
                    if (lapide == false) {

                        // Trazer musica para a memoria primaria
                        byte[] registro = new byte[tamRegistro];
                        dbFile.read(registro);
                        musicaProcurada.fromByteArray(registro);

                        find = true;
                        System.out.println(musicaProcurada);
                    }

                }
                cont++;
            }
            
        } catch (IOException e) {
            System.out.println("\nERRO: Ocorreu um erro de escrita no " +
                               "arquivo \"" + bucketDB + "\"\n");
        } finally {
            if (bucketFile != null) bucketFile.close();
            if (dbFile != null) dbFile.close();
        }

        return find;
    }

    /**
     * Metodo para procurar e deletar uma musica a partir do seu ID.
     * @param idProcurado - id da musica para deletar.
     * @return true, se a música foi deletada; false, caso contrario.
     * @throws Exception Se ocorrer algum erro ao manipular o arquivo.
     */
    public boolean delete(int idProcurado) throws Exception {
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
            
        } catch (IOException e) {
            System.out.println("\nERRO: Ocorreu um erro de escrita no " +
                               "arquivo \"" + bucketDB + "\"\n");
        } finally {
            if (bucketFile != null) bucketFile.close();
            if (dbFile != null) dbFile.close();
        }

        return find;
    }

    /**
     * Metodo para procurar e alterar endereco uma musica a partir do seu ID.
     * @param idProcurado - id da musica para atualizar endereco.
     * @param newEndereco - novo endereco da musica.
     * @return true, se a música foi deletada; false, caso contrario.
     * @throws Exception Se ocorrer algum erro ao manipular o arquivo.
     */
    public boolean update(int idProcurado, long newEndereco) throws Exception {
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
                 

        } catch (IOException e) {
            System.out.println("\nERRO: Ocorreu um erro de escrita no " +
                               "arquivo \"" + bucketDB + "\"\n");
        } finally {
            if (bucketFile != null) bucketFile.close();
            if (dbFile != null) dbFile.close();
        }

        return find;
    }

    /**
     * Metodo para refazer hashing, utilizado apos as ordenacoes.
     * @throws Exception Se ocorrer algum erro ao manipular os arquivos.
     */
    public void refazerHashing () throws Exception {
        RandomAccessFile dbFile = null;

        try {
            dbFile = new RandomAccessFile (registroDB, "rw");

            if (dbFile.length() > 0) {

                Musica musica = null;

                // Ler ultimo ID adicionado
                dbFile.seek(0);
                dbFile.readInt();
                long posicaoAtual = dbFile.getFilePointer();

                while (dbFile.length() != posicaoAtual) {
                                        
                    musica = new Musica();

                    // Ler informacoes do registro
                    boolean lapide = dbFile.readBoolean();
                    int tamRegistro = dbFile.readInt();

                    // Trazer musica para memoria primaria
                    byte[] registro = new byte[tamRegistro];
                    dbFile.read(registro);
                    musica.fromByteArray(registro);

                    // Atualizar endereco da musica
                    update(musica.getId(), posicaoAtual);                       

                    // Atualizar ponteiro
                    posicaoAtual = dbFile.getFilePointer();
                }
            } else {
                System.out.println("\nERRO: Registro vazio!" +
                                   "\n      Tente carregar os dados iniciais primeiro!\n");
            }

        } catch (FileNotFoundException e) {
                System.out.println("\nERRO: Registro nao encontrado!" +
                                   "\n      Tente carregar os dados iniciais primeiro!\n");
        } finally {
            if (dbFile != null) dbFile.close();
        }
    }

}
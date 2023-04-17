package listaInvertida;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import app.Musica;

public class ListaInvertida_AnoLancamento {

    /**
     * Construtor padrao da ListaInvertida_AnoLancamento.
     */
    public ListaInvertida_AnoLancamento() {}

    /**
     * Metodo para inserir uma musica na lista invertida a partir dos nomes do
     * artista.
     * @param musica - a ser inserida.
     * @param endereco - posicao da musica no "Registro.db".
     * @throws Exception Se ocorrer algum erro ao manipular os arquivos.
     */
    public void inserir(Musica musica, long endereco) throws Exception {

        // Obter data de lancamento da musica
        Date dataLancamento = musica.getDataLancamento();

        // Obter string com o ano de lancamento
        SimpleDateFormat date = new SimpleDateFormat("yyyy");
        String anoInserir = date.format(dataLancamento);
        String nomeArquivo = "./src/resources/listaInvertida_AnoLancamento/" + anoInserir + ".db";

        RandomAccessFile dataDB = null;

        try{
            // Tenta abrir arquivo para inserir
            dataDB = new RandomAccessFile (nomeArquivo, "rw");

        } catch (FileNotFoundException e) {
            // Arquivo nao encontrado, criar o arquivo aqui
            File file = new File(nomeArquivo);
            file.createNewFile();
        }

        try{
            // Tenta abrir o arquivo novamente
            dataDB = new RandomAccessFile(nomeArquivo, "rw");

            // Posiciona ponteiro no fim do arquivo
            long finalArquivo = dataDB.length();
            dataDB.seek(finalArquivo);

            // Inserir chave do registro
            int chave = musica.getId();
            byte[] chaveBytes = ByteBuffer.allocate(4).putInt(chave).array();
            dataDB.write(chaveBytes);

            // Inserir endereco do registro
            byte[] enderecoBytes = ByteBuffer.allocate(8).putLong(endereco).array();
            dataDB.write(enderecoBytes);

        } catch (IOException e) {
            System.out.println("\nERRO: Ocorreu um erro de escrita no " +
                                "arquivo \"" + nomeArquivo + "\"\n");
        } finally {
            if (dataDB != null) dataDB.close();
        }
    }

    /**
     * Metodo para pesquisar uma musica a partir do ano de lancamento.
     * @param dataBusca - data de lancamento da musica para se pesquisar.
     * @return Array List com os enderecos das musicas desejadas.
     * @throws Exception Se ocorrer algum erro ao manipular os arquivos.
     */
    public List<Long> read(Date dataBusca) throws Exception {

        List<Long> enderecos = new ArrayList<>();

        // Obter nome do arquivo
        SimpleDateFormat date = new SimpleDateFormat("yyyy");
        String anoProcurar = date.format(dataBusca);
        String nomeArquivo = "./src/resources/listaInvertida_AnoLancamento/" + anoProcurar + ".db";

        RandomAccessFile dataDB = null;

        try{
            // Tenta abrir arquivo
            dataDB = new RandomAccessFile (nomeArquivo, "rw");

            long posicao = dataDB.getFilePointer();
            while(posicao != dataDB.length()) {
                
                // Ler chave
                dataDB.readInt();

                // Ler e adicionar endereco ao array
                long endereco = dataDB.readLong();
                enderecos.add(endereco);

                // Atualizar ponteiro
                posicao = dataDB.getFilePointer();
            }

        } catch (FileNotFoundException e) {
            // Nao existe a musica desejada
            // Lista vazia
            enderecos = null;

        } catch (IOException e) {
            System.out.println("\nERRO: Ocorreu um erro de leitura no " +
                                "arquivo \"" + nomeArquivo + "\"\n");
        } finally {
            if (dataDB != null) dataDB.close();
            return enderecos;
        }
    }
}
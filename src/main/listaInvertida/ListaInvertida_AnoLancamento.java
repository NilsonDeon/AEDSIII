// Package
package listaInvertida;

// Bibliotecas
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
import java.util.Date;
import java.util.List;

// Bibliotecas proprias
import app.Musica;

/**
 * Classe responsavel por criar e manipular uma lista invertida sobre o atributo
 * ano de lancamento da musica presente no banco de dados.
 */
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
     */
    public void inserir(Musica musica, long endereco) {

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

        // Excecao caso arquivo nao exista
        } catch (FileNotFoundException e1) {

            try{
                // Se arquivo nao encontrado, deve-se cria-lo
                File file = new File(nomeArquivo);
                file.createNewFile();
            } catch (IOException e2) {
                System.out.println("\nERRO: " + e2.getMessage() + " ao criar o arquivo \"" + nomeArquivo + "\"\n");
            }
            
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

            // Fechar arquivo
            dataDB.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao escrever o arquivo \"" + nomeArquivo + "\"\n");
        }
    }

    /**
     * Metodo para pesquisar uma musica a partir do ano de lancamento.
     * @param dataBusca - data de lancamento da musica para se pesquisar.
     * @return Array List com os enderecos das musicas desejadas.
     */
    public List<Long> read(Date dataBusca) {

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

            // Fechar arquivo
            dataDB.close();

        } catch (FileNotFoundException e) {
            // Nao existe a musica com parametro desejado, logo lista sera null
            enderecos = null;

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler o arquivo \"" + nomeArquivo + "\"\n");

        } finally {
            return enderecos;
        }
    }
}
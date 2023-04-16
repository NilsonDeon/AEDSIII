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

import app.Musica;

public class ListaInvertida_Artistas {

    private String arrayArtistas[] = new String[200000];
    private int tamArray = 0;

    public ListaInvertida_Artistas() {}

    public void inserir(Musica musica, long endereco) throws Exception {

        String nomeArtistas = musica.getArtistas();
        String arrayNomes[] = nomeArtistas.split(" ");

        for(int i = 0; i < arrayNomes.length; i++) {
            String newArtista = normalizarString(arrayNomes[i]);
            if (!newArtista.equals("")){
                inserir(musica, newArtista, endereco);
            }
        }
    }

    private void inserir(Musica musica, String newArtista, long endereco) throws Exception {

        // Obter nome do arquivo
        String nomeArquivo = "./src/resources/listaInvertida_Artistas/" + newArtista + ".db";

        RandomAccessFile artistasDB = null;

        try{
            // Tenta abrir arquivo para inserir
            artistasDB = new RandomAccessFile (nomeArquivo, "rw");

        } catch (FileNotFoundException e) {
            // Arquivo nao encontrado, criar o arquivo
            File file = new File(nomeArquivo);
            file.createNewFile();
        }

        try{
            // Tenta abrir o arquivo novamente
            artistasDB = new RandomAccessFile(nomeArquivo, "rw");

            // Posiciona ponteiro no fim do arquivo
            long finalArquivo = artistasDB.length();
            artistasDB.seek(finalArquivo);

            // Inserir chave do registro
            int chave = musica.getId();
            byte[] chaveBytes = ByteBuffer.allocate(4).putInt(chave).array();
            artistasDB.write(chaveBytes);

            // Inserir endereco do registro
            byte[] enderecoBytes = ByteBuffer.allocate(8).putLong(endereco).array();
            artistasDB.write(enderecoBytes);

        } catch (IOException e) {
            System.out.println("\nERRO: Ocorreu um erro de escrita no " +
                                "arquivo \"" + nomeArquivo + "\"\n");
        } finally {
            if (artistasDB != null) artistasDB.close();
        }
    }

    protected String normalizarString(String texto) {

        // Remover os acentos da string
        String strNormalizada = Normalizer.normalize(texto, Normalizer.Form.NFD)
            .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

        // Substituir caracteres especiais por seus equivalentes sem acento
        strNormalizada = strNormalizada.replaceAll("[Ç|ç]", "c");
        strNormalizada = strNormalizada.replaceAll("[Æ|æ|Ǽ|ǽ]", "ae");
        strNormalizada = strNormalizada.replaceAll("[Ø|ø|Ǿ|ǿ]", "o");
        strNormalizada = strNormalizada.replaceAll("[Å|å|Ǻ|ǻ]", "a");

        // Remover caracteres especiais
        strNormalizada = strNormalizada.replaceAll("[\\\"'!@#$%¨&*()|\\\\/\\-+.,;:?\\[\\]{}]", "");

        // Converter para letras maiusculas
        strNormalizada = strNormalizada.toLowerCase();

        return strNormalizada;
    }

    public List<Long> read(String chaveBusca) throws Exception {

        List<Long> enderecos = new ArrayList<>();

        // Obter nome do arquivo
        String nomeArquivo = "./src/resources/listaInvertida_Artistas/" + chaveBusca + ".db";

        RandomAccessFile artistasDB = null;

        try{
            // Tenta abrir arquivo
            artistasDB = new RandomAccessFile (nomeArquivo, "rw");

            long posicao = artistasDB.getFilePointer();
            while(posicao != artistasDB.length()) {
                
                // Ler chave
                artistasDB.readInt();

                // Ler e adicionar endereco ao array
                long endereco = artistasDB.readLong();
                enderecos.add(endereco);

                // Atualizar ponteiro
                posicao = artistasDB.getFilePointer();
            }

        } catch (FileNotFoundException e) {
            // Nao existe a musica desejada
            // Lista vazia
            enderecos = null;

        } catch (IOException e) {
            System.out.println("\nERRO: Ocorreu um erro de leitura no " +
                                "arquivo \"" + nomeArquivo + "\"\n");
        } finally {
            if (artistasDB != null) artistasDB.close();
            return enderecos;
        }
    }

}
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
import java.util.List;

// Bibliotecas proprias
import app.Musica;

/**
 * Classe responsavel por criar e manipular uma lista invertida sobre o atributo
 * nome do artista da musica presente no banco de dados.
 */
public class ListaInvertida_Artistas {

    /**
     * Construtor padrao da ListaInvertida_Artistas.
     */
    public ListaInvertida_Artistas() {}

    /**
     * Metodo para inserir uma musica na lista invertida a partir de uma 
     * lista de palavras.
     * @param musica - a ser inserida.
     * @param endereco - posicao da musica no "Registro.db".
     */
    public void inserir(Musica musica, long endereco) {

        String nomeArtistas = musica.getArtistas();
        String arrayNomes[] = nomeArtistas.split(" ");

        for(int i = 0; i < arrayNomes.length; i++) {
            String newArtista = normalizarString(arrayNomes[i]);
            if (!newArtista.equals("")){
                inserir(musica, newArtista, endereco);
            }
        }
    }

    /**
     * Metodo privado para inserir uma musica na lista invertida a partir de 
     * uma unica palavra.
     * @param musica - a ser inserida.
     * @param newArista
     * @param endereco - posicao da musica no "Registro.db".
     */
    private void inserir(Musica musica, String newArtista, long endereco) {

        // Obter nome do arquivo
        String nomeArquivo = "./src/resources/listaInvertida_Artistas/" + newArtista + ".db";

        RandomAccessFile artistasDB = null;

        try{
            // Tenta abrir arquivo para inserir
            artistasDB = new RandomAccessFile (nomeArquivo, "rw");

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

            // Fechar arquivo
            artistasDB.close();

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao escrever o arquivo \"" + nomeArquivo + "\"\n");
        }
    }

    /**
     * Metodo para normalizar a string de busca, substituindo caracteres nao
     * padronizados, retirando os acentos e os caracteres especiais, alem de
     * converter as letrar para letra minuscula.
     * @param texto - a ser normalizado
     * @return strNormalizada - contendo a string pronta para ser utilizada.
     */
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
        strNormalizada = strNormalizada.replaceAll("[\\\"'!@#$%¨&><*()|\\\\/\\-+.,;:?\\[\\]{}✦]", "");

        // Converter para letras maiusculas
        strNormalizada = strNormalizada.toLowerCase();

        return strNormalizada;
    }

    /**
     * Metodo para pesquisar uma musica a partir do artista.
     * @param chaveBusca - da musica para se pesquisar.
     * @return Array List com os enderecos das musicas desejadas.
     */
    public List<Long> read(String chaveBusca) {

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
                int chave = artistasDB.readInt();

                // Ler endereco
                long endereco = artistasDB.readLong();

                // Atualizar ponteiro
                posicao = artistasDB.getFilePointer();

                // Testar se chave e' valida
                if (chave != -1) {
                    // Adicionar endereco ao array
                    enderecos.add(endereco);
                }
            }

            // Fechar arquivo
            artistasDB.close();

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

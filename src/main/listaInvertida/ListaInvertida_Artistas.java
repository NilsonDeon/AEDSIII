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
import app.*;

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
     * @param newArista - nome do artista.
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
        strNormalizada = strNormalizada.replaceAll("[\\\"'!@#$%¨&*()|\\\\/\\-+.,;:?\\[\\]{}✦<>]", "");

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

    /**
     * Metodo para remover uma musica na lista invertida a partir de uma 
     * lista de palavras.
     * @param musica - a ser removida.
     */
    public void delete(Musica musica) {

        int chave = musica.getId();
        String nomeArtistas = musica.getArtistas();
        String arrayNomes[] = nomeArtistas.split(" ");

        for(int i = 0; i < arrayNomes.length; i++) {
            String artista = normalizarString(arrayNomes[i]);
            if (!artista.equals("")){
                delete(artista, chave);
            }
        }
    }

    /**
     * Metodo privado para remover uma musica na lista invertida a partir de 
     * uma unica palavra.
     * @param nomeArtista - nome Artista a se remover.
     * @param chaveProcurada - id da musica a se remover.
     */
    private void delete(String nomeArtista, int chaveProcurada) {

        boolean find = false;

        // Obter nome do arquivo
        String nomeArquivo = "./src/resources/listaInvertida_Artistas/" + nomeArtista + ".db";

        RandomAccessFile artistasDB = null;

        try{
            // Tenta abrir arquivo para remover
            artistasDB = new RandomAccessFile (nomeArquivo, "rw");

            // Remover logicamente
            long posicao = artistasDB.getFilePointer();
            while(posicao != artistasDB.length() && find == false) {

                // Ler chave
                int chave = artistasDB.readInt();

                // Ler endereco
                long endereco = artistasDB.readLong();

                // Testar se chave e' valida
                if (chave == chaveProcurada) {
                    // Reposicionar ponteiro
                    artistasDB.seek(posicao);

                    // Inserir chave do registro
                    chave = -1;
                    byte[] chaveBytes = ByteBuffer.allocate(4).putInt(chave).array();
                    artistasDB.write(chaveBytes);

                    // Inserir endereco do registro
                    endereco = -1;
                    byte[] enderecoBytes = ByteBuffer.allocate(8).putLong(endereco).array();
                    artistasDB.write(enderecoBytes);

                    // Marcar como encontrado
                    find = true;
                }

                // Atualizar ponteiro
                posicao = artistasDB.getFilePointer();
            }

            // Fechar arquivo
            artistasDB.close();

        // Excecao caso arquivo nao exista
        } catch (FileNotFoundException e1) {
            // Nao esta inserido
        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao escrever o arquivo \"" + nomeArquivo + "\"\n");
        }
    }

    /**
     * Metodo para atualizar uma musica na lista, trocando enderenco e o
     * arquivo correspondente.
     * @param musicaAntiga - musica antes da atualizacao no arquivo 
     * "Registro.db".
     * @param musicaNova - musica depois da atualizacao no arquivo 
     * "Registro.db".
     * @param newEndereco - na posicao da musica.
     */
    public void update (Musica musicaAntiga, Musica newMusica, long newEndereco) {

        // Obter nomes dos artistas        
        String nomeAntigo = musicaAntiga.getArtistas();
        String nomeNovo = newMusica.getArtistas();

        // Chave para se atualizar
        int chave = musicaAntiga.getId();
        
        // Deletar nome antigo
        delete(musicaAntiga);

        // Reinserir com posicao e nome corretos
        inserir(newMusica, newEndereco);
    }
    
}
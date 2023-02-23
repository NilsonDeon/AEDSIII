/**
 * TP01 - Trabalho Pratico 01 de Algoritmos e Estruturas de Dados III
 * @author Gabriel Vargas Bento de Souza e Nilson Deon Cordeiro Filho
 * @version 1.0 02/2023
 */

// bibliotecas
import java.util.Date;
import java.util.Locale;

import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.DecimalFormat;

import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Classe Musica que contem todos os atributos presentes nos registros do
 * banco de dados do csv.
 */
public class Musica {
  
    protected char lapede;
    protected int id;

    protected String nome;          // String de tamanho variavel
    protected String artistas;
    protected String nomeAlbum;   
    protected String[] imagens;     // Lista de valores com separador " "
    protected String pais;          // String tamanho fixo
    protected Date dataLancamento;  // Data
    protected int popularidade;     // inteiro
    protected int duracao;          
    protected String uri;      


    /**
     * Construtor padrao da classe Musica.
     */
    public Musica () {
        lapede = ' ';
        id = 0;
        nome = "";
        artistas = "";
        nomeAlbum = "";
        imagens = new String[10];
        pais = "";
        dataLancamento = null;
        popularidade = 0;
        duracao = 0;
        uri = "";
    }

    /**
     * Construtor da classe Musica, por meio de uma string com os atributos do
     * objeto.
     * @param linha - string contendo todas as informacoes sobre o objeto a ser
     * criado.
     * @param id - id do objeto criado.
     */
    protected Musica (String linha, int id) {
        String[] atributos = linha.split(",");

        lapede = ' ';
        this.id = id;
        nome = atributos[0];
        artistas = atributos[1];
        nomeAlbum = atributos[2];
        lerImagens(atributos[3]);
        pais = atributos[4];
        lerDataLancamento(atributos[5]);
        popularidade = Integer.parseInt(atributos[6]);
        duracao = Integer.parseInt(atributos[7]);
        uri = atributos[8];
    }

    /**
     * Metodo para transformar os atributos da classe em uma string, sendo
     * possivel exibir na tela.
     * @return String com os atributos.
     */
    public String toString () {
        return "\nLapede: [" + lapede + "]" +
               "\nId: " + id +
               "\nNome: " + nome +
               "\nArtistas " + artistas +
               "\nNome Álbum: " + nomeAlbum +
               "\nImagens: " + mostrarImagens() +
               "\nPaís: " + pais +
               "\nData Lançamento: " + dataLancamento +
               "\nPopularidade: " + popularidade + 
               "\nDuração: " + duracao +
               "\nUri: " + uri;

    }

    /**
     * Metodo para configurar a exibicao do array de imagens.
     * @return string no segunte formato: [...].
     */
    private String mostrarImagens () {
        
        int i = 0;
        String arrayImagens = "[ ";

        while (imagens[i++] != null) {
            arrayImagens += imagens[i] + " ";
        }
        arrayImagens += "]";

        return arrayImagens;
    }

    /**
     * Metodo para preencher o atributo imagens com um array de string, na qual
     * elas estao separadas por espaco.
     * @param arrayImagens - string contendo a lista de imagens.
     */
    private void lerImagens (String arrayImagens) {
        imagens = arrayImagens.split(" ");
    }

    /**
     * Metodo para preencher o atributo dataLancamento, passando a data como
     * uma string.
     * @param strDate - string, contendo a data de lancamento.
     */
    private void lerDataLancamento (String strDate) {

        try {
            Locale US = new Locale("US");
            DateFormat df;

            if (strDate.length() == 4) {
                df = new SimpleDateFormat("yyyy", US);
            } else {
                df = new SimpleDateFormat("yyyy-MM-dd", US);
            }
            dataLancamento = df.parse(strDate);

        } catch (Exception e) {
            System.out.print("ERRO: data invalida: " + strDate);
        }

    }

    /**
     * Metodo para converter os atributos da classe em um array de bytes.
     * @return - array de bytes.
     */
    protected byte[] toByteArray () {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        try {

            short lengthNome = (short)nome.length();
            
            dos.writeChar(lapede);
            dos.writeInt(id);
            dos.writeShort(lengthNome);
            dos.writeUTF(nome);

        } catch (IOException e) {
            System.out.println("\nERRO ao converter Musica para um array de bytes");
        }

        return baos.toByteArray();
    }

}
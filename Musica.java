/**
 * Musica - Trabalho Pratico 01 de Algoritmos e Estruturas de Dados III
 * @author Gabriel Vargas Bento de Souza e Nilson Deon Cordeiro Filho
 * @version 1.0 02/2023
 */

// bibliotecas
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.InputMismatchException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Classe Musica que contem todos os atributos presentes nos registros do
 * banco de dados do csv.
 */
public class Musica {
  
    protected boolean lapide;
    protected int id;

    protected String nome;          // String de tamanho variavel
    protected String artistas;
    protected String nomeAlbum;   
    protected String[] imagens;     // Lista de valores com separador " "
    protected String pais;          // String tamanho fixo
    protected Date dataLancamento;  // Data
    protected int dancabilidade;   // Inteiro
    protected int duracao;
    protected int vivacidade;
    protected int popularidade;       
    protected String uri;      


    /**
     * Construtor padrao da classe Musica.
     */
    public Musica () {
        lapide = false;
        id = 0;
        nome = null;
        artistas = null;
        nomeAlbum = null;
        imagens = new String[10];
        pais = null;
        dataLancamento = null;
        dancabilidade = 0;
        duracao = 0;
        vivacidade = 0;
        popularidade = 0;
        uri = null;
    }

    /**
     * Construtor da classe Musica, por meio de uma string com os atributos do
     * objeto.
     * @param linha - string contendo todas as informacoes sobre o objeto a ser
     * criado.
     * @param id - id do objeto criado.
     */
    public Musica (String linha, int id) {

        String[] atributos = linha.split(",");

        lapide = true;
        this.id = id;
        nome = atributos[0];
        artistas = atributos[1];
        nomeAlbum = atributos[2];
        lerImagens(atributos[3]);
        pais = atributos[4];
        lerDataLancamento(atributos[5]);
        dancabilidade = Integer.parseInt(atributos[6]);
        duracao = Integer.parseInt(atributos[7]);
        vivacidade = Integer.parseInt(atributos[8]);
        popularidade = Integer.parseInt(atributos[9]);
        uri = atributos[10];
    }

    /**
     * Metodo para transformar os atributos da classe em uma string, sendo
     * possivel exibir na tela.
     * @return String com os atributos.
     */
    public String toString () {
        return "\nId             : " + id +
               "\nNome           : " + nome +
               "\nArtistas       : " + artistas +
               "\nNome Album     : " + nomeAlbum +
               "\nImagens        : " + mostrarImagens() +
               "\nPais           : " + pais +
               "\nData Lançamento: " + mostrarDataLancamento() +
               "\ndancabilidade : " + dancabilidade +
               "\nDuracao        : " + duracao +
               "\nVivacidade     : " + vivacidade +
               "\nPopularidade   : " + popularidade + 
               "\nUri            : " + uri;

    }

    /**
     * Metodo para configurar a exibicao do array de imagens.
     * @return string com as imagens
     */
    private String mostrarImagens () {
        
        String string = "";

        for (String img : imagens) {
            string += (img + ", ");
        }
        
        return string.substring(0, string.length()-2);
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
     * Metodo para ler do teclado a quantidade de imagens que se deseja
     * adicionar, e, em seguida, le-las.
     */
    private void lerImagens () {
        try {
            IO io = new IO();

            System.out.print("\nDigite o numero de imagens para adicionar: ");
            int tam = io.readInt();
            imagens = new String[tam];
            for (int i = 0; i < tam; i++) {
                System.out.print("[" + (i+1) + "]: ");
                imagens[i] = io.readLine();
            }

        } catch (Exception e) {
            System.out.println("\nERRO: Informacoes invalidas!\n\n");
        }
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

            if (strDate.length() == 10) {
                df = new SimpleDateFormat("yyyy-MM-dd", US);
            } else if (strDate.length() == 7) {
                df = new SimpleDateFormat("yyyy-MM", US);
            } else {
                df = new SimpleDateFormat("yyyy", US);
            }
            dataLancamento = df.parse(strDate);

        } catch (ParseException e) {
            System.out.print("\nERRO: data invalida (" + strDate + ")");
        } catch (IllegalArgumentException e) {
            System.out.print("\nERRO: data invalida (" + strDate + ")");
        }

    }

    /**
     * Metodo para formatar a data de lancamento, transformando-a em string.
     * @return strDate - data formatada.
     */
    private String mostrarDataLancamento () {
        SimpleDateFormat date = new SimpleDateFormat("dd/MMM/yyyy");
        String strDate = date.format(dataLancamento);
        return strDate;
    }

    /**
     * Metodo para realizar a leitura de uma Musica pelo teclado, atribuidos
     * as caracteristicas ao objeto Musica.
     */
    public void lerMusica () {
        try {
            IO io = new IO();

            lapide = true;
            id = 0;

            System.out.print("\nNome: ");
            nome = io.readLine ();

            System.out.print("\nArtistas: ");
            artistas = io.readLine ();

            System.out.print("\nNome Album: ");
            nomeAlbum = io.readLine ();

            lerImagens();

            System.out.print("\nSigla pais: ");
            pais = io.readLine ();

            System.out.print("\nData Lancamento YYYY-MM-DD: ");
            String strDate = io.readLine();
            lerDataLancamento(strDate);

            System.out.print("\ndancabilidade: ");
            dancabilidade = io.readInt ();

            System.out.print("\nDuracao: ");
            duracao = io.readInt ();

            System.out.print("\nVivacidade: ");
            vivacidade = io.readInt ();
 
            System.out.print("\nPopularidade: ");
            popularidade = io.readInt ();

            System.out.print("\nUri: ");
            uri = io.readLine();

        } catch (Exception e) {
            System.out.println("\nERRO: Informacoes invalidas!\n\n");
        }
    }

    /**
     * Metodo privado para atualizar algum atributo do objeto.
     */
    protected void atualizar() {

        IO io = new IO();
        int opcao = -1;

        String menu = "\n 0 - Nome            1 - Artistas" +
                      "\n 2 - Nome do Album   3 - Imagens" +
                      "\n 4 - Pais            5 - Data Lancamento" +
                      "\n 6 - Dancabilidade  7 - Duracao" + 
                      "\n 8 - Vivacidade      9 - Popularidade" +
                      "\n10 - Uri"; 

        do {
            try {
                System.out.println(menu);
                opcao = io.readInt("\nEscolha qual atributo deseja alterar: ");

                switch (opcao) {
                    case 0:
                        System.out.println("\nNome atual: " + this.nome);
                        String newNome = io.readLine("Digite novo nome: ");
                        this.nome = newNome;
                        break;
                    case 1:
                        System.out.println("\nArtistas atual: " + this.artistas);
                        String newArtista = io.readLine("Digite novo artista: ");
                        this.artistas = newArtista;
                        break;
                    case 2:
                        System.out.println("\nNome do album atual: " + this.nomeAlbum);
                        String newAlbum = io.readLine("Digite novo nome do album: ");
                        this.nomeAlbum = newAlbum;
                        break;
                    case 3:
                        System.out.println("\nCapas atuais:");
                        for (String imagem: this.imagens) {
                            System.out.println(" " + imagem);
                        }
                        lerImagens();
                        break;
                    case 4:
                    System.out.println("\nPaís de lançamento atual: " + this.pais);
                        String newPais = io.readLine("Digite novo pais de origem: ");
                        this.pais = newPais;
                        break;
                    case 5:
                        System.out.println("\nData de lançamento atual: " + this.dataLancamento);
                        String newData = io.readLine("Digite nova data de laçamento: ");
                        lerDataLancamento (newData);
                        break;
                    case 6:
                        System.out.println("\nDaçabilidade atual: " + this.dancabilidade);
                        int newDancabilidade = io.readInt("Digite novo valor de dançabilidade: ");
                        this.dancabilidade = newDancabilidade;
                        break;
                    case 7:
                        System.out.println("\nDurção atual: " + this.duracao);
                        int newDuracao = io.readInt("Digite nova duração: ");
                        this.duracao = newDuracao;
                        break;
                    case 8:
                        System.out.println("\nVivacidade atual: " + this.vivacidade);
                        int newVivacidade = io.readInt("Digite nova vivacidade: ");
                        this.vivacidade = newVivacidade;
                        break;
                    case 9:
                        System.out.println("\nPopularidade atual: " + this.popularidade);
                        int newPopularidade = io.readInt("Digite nova popularidade: ");
                        this.popularidade = newPopularidade;
                        break;
                    case 10:
                        System.out.println("\nLink da música atual: " + this.uri);
                        String newUri = io.readLine("Digite novo link: ");
                        this.nome = newUri;
                        break;
                    default: break;
                }
            } catch (InputMismatchException e) {
                System.out.println("\nERRO: Por favor, digite uma opcao valida"+
                                   "de 0 a 10.");
                io.readLine();
            }
        } while (opcao < 0 || opcao > 10);
    }

    /**
     * Metodo para converter os atributos da classe em um array de bytes.
     * @return - array de bytes.
     */
    public byte[] toByteArray () {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        try {

            ByteArrayOutputStream tmp = new ByteArrayOutputStream();
            DataOutputStream aux = new DataOutputStream(tmp);      

            aux.writeInt(id);

            aux.writeShort(nome.length());
            aux.writeUTF(nome);

            aux.writeShort(artistas.length());
            aux.writeUTF(artistas);

            aux.writeShort(nomeAlbum.length());
            aux.writeUTF(nomeAlbum);

            aux.writeShort(imagens.length);
            for(int i = 0; i < imagens.length; i++) {
                aux.writeShort(imagens[i].length());
                aux.writeUTF(imagens[i]);
            }

            aux.writeUTF(pais);

            long dataEmMilissegundos = dataLancamento.getTime();
            aux.writeLong(dataEmMilissegundos);

            aux.writeInt(dancabilidade);
            aux.writeInt(duracao);
            aux.writeInt(vivacidade);
            aux.writeInt(popularidade);

            aux.writeShort(uri.length());
            aux.writeUTF(uri);

            dos.writeBoolean(lapide);
            dos.writeInt(aux.size());
            dos.write(tmp.toByteArray(), 0, tmp.size());

        } catch (IOException e) {
            System.out.println("\nERRO ao converter Musica para um array de " +
                               "bytes");
        }

        return baos.toByteArray();
    }

    /**
     * Metodo para converter um array de bytes em um objeto Musica.
     * @return - array de bytes.
     */
    public void fromByteArray (byte ba[]) {

        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(ba);
            DataInputStream dis = new DataInputStream(bais);

            lapide = true;
            id = dis.readInt();
            
            short tamNome = dis.readShort();
            nome = dis.readUTF();

            short tamArtistas = dis.readShort();
            artistas = dis.readUTF();

            short tamNomeAlbum = dis.readShort();
            nomeAlbum = dis.readUTF();

            short tamImagens = dis.readShort();
            imagens = new String[tamImagens];
            for (int i = 0; i < tamImagens; i++) {
                short tmp = dis.readShort();
                imagens[i] = dis.readUTF();
            }

            pais = dis.readUTF();

            long dataEmMilissegundos = dis.readLong();
            dataLancamento = new Date(dataEmMilissegundos);

            dancabilidade = dis.readInt();
            duracao = dis.readInt();
            vivacidade = dis.readInt();
            popularidade = dis.readInt();

            short tamUri = dis.readShort();
            uri = dis.readUTF();

       } catch (IOException e) {
            System.out.println("\nERRO ao converter array de bytes para uma " +
                               "Musica");
       }
    }

}
// Bibliotecas
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

    protected boolean lapide;      // implementada como boolean, por ser um byte
    protected int id;

    protected String nome;         // String de tamanho variavel
    protected String artistas;
    protected String nomeAlbum;   
    protected String[] imagens;    // Lista de valores com separador " "
    protected char[] pais;         // String tamanho fixo
    protected Date dataLancamento; // Data
    protected int dancabilidade;   // Inteiro
    protected int duracao;
    protected int vivacidade;
    protected int popularidade;       
    protected String uri;      


    /**
     * Construtor padrao da classe Musica.
     */
    public Musica () {
        lapide = true;
        id = 0;
        nome = null;
        artistas = null;
        nomeAlbum = null;
        imagens = new String[10];
        pais = new char[2];
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
     * @throws Exception Se ocorrer algum erro ao manipular data.
     */
    public Musica (String linha, int id) throws Exception{

        String[] atributos = linha.split(",");

        lapide = false;
        this.id = id;
        nome = atributos[0];
        artistas = atributos[1];
        nomeAlbum = atributos[2];
        lerImagens(atributos[3]);
        pais = atributos[4].toCharArray();
        lerDataLancamento(atributos[5]);
        dancabilidade = Integer.parseInt(atributos[6]);
        duracao = Integer.parseInt(atributos[7]);
        vivacidade = Integer.parseInt(atributos[8]);
        popularidade = Integer.parseInt(atributos[9]);
        uri = atributos[10];
    }

    /**
     * Metodo para clonar uma Musica.
     * @return musica - com os mesmos atributos.
     */
    public Musica clone () {
        Musica musica = new Musica();

        musica.lapide = lapide;
        musica.id = id;
        musica.nome = nome;
        musica.artistas = artistas;
        musica.nomeAlbum = nomeAlbum;
        musica.imagens = imagens;
        musica.pais = pais;
        musica.dataLancamento = dataLancamento;
        musica.dancabilidade = dancabilidade;
        musica.duracao = duracao;
        musica.vivacidade = vivacidade;
        musica.popularidade = popularidade;
        musica.uri = uri;
        
        return musica;
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
               "\nImagens da capa: " + mostrarImagens() +
               "\nPais           : " + pais[0] + pais[1] +
               "\nData Lancamento: " + mostrarDataLancamento() +
               "\nDancabilidade  : " + dancabilidade +
               "\nDuracao        : " + duracao +
               "\nVivacidade     : " + vivacidade +
               "\nPopularidade   : " + popularidade + 
               "\nLink da musica : " + uri;
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

        if (string.compareTo("") != 0) {
            string = string.substring(0, string.length()-2);
        }
        
        return string;
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
     * Metodo para preencher o atributo pais com um array de char, de modo a 
     * simular uma string de tamanho fixo igual a 2.
     * @param arrayImagens - string contendo a lista de imagens.
     */
    private void lerPais (String siglaPais) {
        
        pais = new char[2];
        pais[0] = pais[1] = ' ';

        for (int i = 0; i < siglaPais.length() && i < 2; i++) {
            pais[i] = siglaPais.charAt(0);
        }
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
     * @throws Exception Se ocorrer algum erro ao manipular data.
     */
    private void lerDataLancamento (String strDate) throws Exception {
     
        Locale US = new Locale("US");
        DateFormat df;

        try {
            if (strDate.length() == 10) {
                df = new SimpleDateFormat("yyyy-MM-dd", US);
            } else if (strDate.length() == 7) {
                df = new SimpleDateFormat("yyyy-MM", US);
            } else {
                df = new SimpleDateFormat("yyyy", US);
            }
            dataLancamento = df.parse(strDate);

        } catch (ParseException e) {
            System.out.print("ERRO: Data invalida (" + strDate + ")\n");
            strDate = "0001";
            df = new SimpleDateFormat("yyyy", US);
            dataLancamento = df.parse(strDate);
        } catch (IllegalArgumentException e) {
            System.out.print("ERRO: Data invalida (" + strDate + ")\n");
            strDate = "0001";
            df = new SimpleDateFormat("yyyy", US);
            dataLancamento = df.parse(strDate);  
        }

    }

    /**
     * Metodo para formatar a data de lancamento, transformando-a em string.
     * @return strDate - data formatada.
     */
    private String mostrarDataLancamento () {
        SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy");
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

            lapide = false;
            id = 0;

            nome = io.readLine ("\nNome: ");

            artistas = io.readLine ("\nArtistas: ");

            nomeAlbum = io.readLine ("\nNome Album: ");

            lerImagens();

            String strPais = io.readLine ("\nSigla pais [--]: ");
            lerPais(strPais);

            String strDate = io.readLine("\nData Lancamento [YYYY-MM-DD]: ");
            lerDataLancamento(strDate);

            dancabilidade = io.readInt ("\nDancabilidade: ");

            duracao = io.readInt ("\nDuracao: ");

            vivacidade = io.readInt ("\nVivacidade: ");
 
            popularidade = io.readInt ("\nPopularidade: ");

            uri = io.readLine("\nLink da musica: ");

        } catch (Exception e) {
            System.out.println("\nERRO: Informacoes invalidas!\n\n");
        }
    }

    /**
     * Metodo privado para atualizar algum atributo do objeto.
     * @return true, se atualizado; false, caso contrario.
     * @throws Exception Se ocorrer algum erro ao manipular data.
     */
    protected boolean atualizar() throws Exception {

       IO io = new IO();
       int opcao = -1;
       boolean updated = true;

       String menu = "\n+------------------------------------------+" +
                     "\n|             MENU ATUALIZACAO             |" +
                     "\n|------------------------------------------|" +
                     "\n|  1 - Nome            2 - Artistas        |" +
                     "\n|  3 - Nome do Album   4 - Imagens da capa |" +
                     "\n|  5 - Pais            6 - Data Lancamento |" +
                     "\n|  7 - Dancabilidade   8 - Duracao         |" +
                     "\n|  9 - Vivacidade     10 - Popularidade    |" +
                     "\n| 11 - Uri            12 - Voltar          |" +
                     "\n+------------------------------------------+";

        do {
            try {
                System.out.println(menu);
                opcao = io.readInt("\nEscolha qual atributo deseja alterar: ");

                switch (opcao) {
                    case 1:
                        System.out.println("\nNome atual: " + this.nome);
                        String newNome = io.readLine("Digite o novo nome: ");
                        this.nome = newNome;
                        break;
                    case 2:
                        System.out.println("\nArtistas atuais: " + this.artistas);
                        String newArtista = io.readLine("Digite o(s) novo(s) artista(s): ");
                        this.artistas = newArtista;
                        break;
                    case 3:
                        System.out.println("\nNome do album atual: " + this.nomeAlbum);
                        String newAlbum = io.readLine("Digite o novo nome do album: ");
                        this.nomeAlbum = newAlbum;
                        break;
                    case 4:
                        System.out.println("\nImagens atuais da capa:");
                        System.out.print(mostrarImagens());
                        System.out.println("");
                        lerImagens();
                        break;
                    case 5:
                        System.out.println("\nPais de lancamento atual: " + this.pais[0] + this.pais[1]);
                        String newPais = io.readLine("Digite o novo pais de origem [--]: ");
                        this.pais[0] = newPais.charAt(0);
                        this.pais[1] = newPais.charAt(1);
                        break;
                    case 6:
                        System.out.println("\nData de lancamento atual: " + mostrarDataLancamento());
                        String newData = io.readLine("Digite a nova data de lancamento: ");
                        lerDataLancamento (newData);
                        break;
                    case 7:
                        System.out.println("\nDancabilidade atual: " + this.dancabilidade);
                        int newDancabilidade = io.readInt("Digite o novo valor de dancabilidade: ");
                        this.dancabilidade = newDancabilidade;
                        break;
                    case 8:
                        System.out.println("\nDuracao atual: " + this.duracao);
                        int newDuracao = io.readInt("Digite a nova duracao: ");
                        this.duracao = newDuracao;
                        break;
                    case 9:
                        System.out.println("\nVivacidade atual: " + this.vivacidade);
                        int newVivacidade = io.readInt("Digite a nova vivacidade: ");
                        this.vivacidade = newVivacidade;
                        break;
                    case 10:
                        System.out.println("\nPopularidade atual: " + this.popularidade);
                        int newPopularidade = io.readInt("Digite a nova popularidade: ");
                        this.popularidade = newPopularidade;
                        break;
                    case 11:
                        System.out.println("\nLink da musica atual: " + this.uri);
                        String newUri = io.readLine("Digite o novo link: ");
                        this.nome = newUri;
                        break;
                    case 12:
                        updated = false;
                        break;
                    default: 
                        System.out.println("\nERRO: Por favor, digite uma opcao valida"+
                                   "de 1 a 12.");
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("\nERRO: Por favor, digite uma opcao valida"+
                                   "de 1 a 12.");
                io.readLine();
            }
        } while (opcao < 1 || opcao > 12);

        return updated;
    }

      /**
     * Metodo para converter os atributos da classe em um array de bytes.
     * @param tamanho - do array de bytes pre-definido.
     * @return - array de bytes.
     */
    public byte[] toByteArray (int tamanho) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        try {

            dos.writeBoolean(lapide);
            dos.writeInt(tamanho);
 
            dos.writeInt(id);

            dos.writeShort(nome.length());
            dos.writeUTF(nome);

            dos.writeShort(artistas.length());
            dos.writeUTF(artistas);

            dos.writeShort(nomeAlbum.length());
            dos.writeUTF(nomeAlbum);

            dos.writeShort(imagens.length);
            for(int i = 0; i < imagens.length; i++) {
                dos.writeShort(imagens[i].length());
                dos.writeUTF(imagens[i]);
            }

            dos.writeChar(pais[0]);
            dos.writeChar(pais[1]);

            long dataEmMilissegundos = dataLancamento.getTime();
            dos.writeLong(dataEmMilissegundos);           

            dos.writeInt(dancabilidade);
            dos.writeInt(duracao);
            dos.writeInt(vivacidade);
            dos.writeInt(popularidade);

            dos.writeShort(uri.length());
            dos.writeUTF(uri);

        } catch (IOException e) {
            System.out.println("\nERRO ao converter Musica para um array de " +
                               "bytes");
        }

        return baos.toByteArray();
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

            aux.writeChar(pais[0]);
            aux.writeChar(pais[1]);

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
     * Metodo para converter valor booleano em array de bytes.
     * @param bool - valor logico a ser convertido.
     * @return - array de bytes.
     */
    public byte[] booleanToByteArray (boolean bool) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        try {
            dos.writeBoolean(lapide);
        } catch (IOException e) {
            System.out.println("\nERRO ao converter Booleano para um array de " +
                               "bytes");
        }

        return baos.toByteArray();
    }

    /**
     * Metodo para converter numero inteiro em array de bytes.
     * @param bool - numero inteiro a ser convertido.
     * @return - array de bytes.
     */
    public byte[] intToByteArray (int inteiro) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        try {
            dos.writeInt(inteiro);
        } catch (IOException e) {
            System.out.println("\nERRO ao converter Inteiro para um array de " +
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

            lapide = false;
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

            pais = new char[2];
            pais[0] = dis.readChar();
            pais[1] = dis.readChar();

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

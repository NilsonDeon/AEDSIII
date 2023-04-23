// Package
package app;

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

    // Implementada como boolean, por ser um byte
    protected boolean lapide;

    // Identificador unico sequencial
    protected int id;

    // Atributos presentes no dataBase Spotify.csv
    protected String nome;
    protected String artistas;
    protected String nomeAlbum;
    protected String[] imagens;    
    protected char[] pais;         // String tamanho fixo == 2
    protected Date dataLancamento;
    protected int dancabilidade;
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
     */
    public Musica (String linha, int id) {

        String[] atributos = linha.split(",");

        lapide = false;
        this.id = id;
        nome = atributos[0];
        artistas = atributos[1];
        nomeAlbum = atributos[2];
        lerImagens(atributos[3]);
        pais = atributos[4].toCharArray();
        lerDataLancamentoUS(atributos[5]);
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
            pais[i] = siglaPais.charAt(i);
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
     * uma string no formato yyyy-MM-dd
     * @param strDate - string, contendo a data de lancamento.
     */
    private void lerDataLancamentoUS (String strDate) {
     
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
            try {
                dataLancamento = df.parse(strDate);
            } catch (ParseException ex) {}

        } catch (IllegalArgumentException e) {
            System.out.print("ERRO: Data invalida (" + strDate + ")\n");
            strDate = "0001";
            df = new SimpleDateFormat("yyyy", US);
            try {
                dataLancamento = df.parse(strDate);
            } catch (ParseException ex) {}
        }
    }

    /**
     * Metodo para preencher o atributo dataLancamento, passando a data como
     * uma string no formato dd-MM-yyyy
     * @param strDate - string, contendo a data de lancamento.
     */
    private void lerDataLancamentoBR (String strDate)  {
     
        Locale US = new Locale("US");
        DateFormat df;

        try {
            if (strDate.length() == 10) {
                df = new SimpleDateFormat("dd-MM-yyyy", US);
            } else if (strDate.length() == 7) {
                df = new SimpleDateFormat("MM-yyyy", US);
            } else {
                df = new SimpleDateFormat("yyyy", US);
            }
            dataLancamento = df.parse(strDate);

        } catch (ParseException e) {
            System.out.print("ERRO: Data invalida (" + strDate + ")\n");
            strDate = "0001";
            df = new SimpleDateFormat("yyyy", US);
            try {
                dataLancamento = df.parse(strDate);
            } catch (ParseException ex) {}
            
        } catch (IllegalArgumentException e) {
            System.out.print("ERRO: Data invalida (" + strDate + ")\n");
            strDate = "0001";
            df = new SimpleDateFormat("yyyy", US);
            try {
                dataLancamento = df.parse(strDate);
            } catch (ParseException ex) {}
        }
    }

    /**
     * Metodo para formatar a data de lancamento, transformando-a em string.
     * @return strDate - data formatada.
     */
    public String mostrarDataLancamento () {
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

            String strDate = io.readLine("\nData Lancamento [DD-MM-AAAA]: ");
            lerDataLancamentoBR(strDate);

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
     */
    public boolean atualizar() {

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
                        lerDataLancamentoBR(newData);
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
                        this.uri = newUri;
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

            dis.readShort();
            nome = dis.readUTF();

            dis.readShort();
            artistas = dis.readUTF();

            dis.readShort();
            nomeAlbum = dis.readUTF();

            short tamImagens = dis.readShort();
            imagens = new String[tamImagens];
            for (int i = 0; i < tamImagens; i++) {
                dis.readShort();
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

            dis.readShort();
            uri = dis.readUTF();

       } catch (IOException e) {
            System.out.println("\nERRO ao converter array de bytes para uma " +
                               "Musica");
       }
    }

    /**
    * Getter para o atributo lapide.
    * @return o valor atual do atributo lapide.
    */
    public boolean isLapide() {
        return lapide;
    }

    /**
    * Setter para o atributo lapide.
    * @param lapide - o novo valor para o atributo lapide.
    */
    public void setLapide(boolean lapide) {
        this.lapide = lapide;
    }

    /**
    * Getter para o atributo id.
    * @return o valor atual do atributo id.
    */
    public int getId() {
        return id;
    }

    /**
    * Setter para o atributo id.
    * @param id - o novo valor para o atributo id.
    */
    public void setId(int id) {
        this.id = id;
    }

    /**
    * Getter para o atributo nome.
    * @return o valor atual do atributo nome.
    */
    public String getNome() {
        return nome;
    }

    /**
    * Setter para o atributo nome.
    * @param nome - o novo valor para o atributo nome.
    */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
    * Getter para o atributo artistas.
    * @return o valor atual do atributo artistas.
    */
    public String getArtistas() {
        return artistas;
    }

    /**
    * Setter para o atributo artistas.
    * @param artistas - o novo valor para o atributo artistas.
    */
    public void setArtistas(String artistas) {
        this.artistas = artistas;
    }

    /**
    * Getter para o atributo nomeAlbum.
    * @return o valor atual do atributo nomeAlbum.
    */
    public String getNomeAlbum() {
        return nomeAlbum;
    }

    /**
    * Setter para o atributo nomeAlbum.
    * @param nomeAlbum - o novo valor para o atributo nomeAlbum.
    */
    public void setNomeAlbum(String nomeAlbum) {
        this.nomeAlbum = nomeAlbum;
    }

    /**
    * Getter para o atributo imagens.
    * @return o valor atual do atributo imagens.
    */
    public String[] getImagens() {
        return imagens;
    }

    /**
    * Setter para o atributo imagens.
    * @param imagens - o novo valor para o atributo imagens.
    */
    public void setImagens(String[] imagens) {
        this.imagens = imagens;
    }

    /**
    * Getter para o atributo pais.
    * @return o valor atual do atributo pais.
    */
    public char[] getPais() {
        return pais;
    }

    /**
    * Setter para o atributo pais.
    * @param pais - o novo valor para o atributo pais.
    */
    public void setPais(char[] pais) {
        this.pais = pais;
    }

    /**
    * Getter para o atributo dataLancamento.
    * @return o valor atual do atributo dataLancamento.
    */
    public Date getDataLancamento() {
        return dataLancamento;
    }

    /**
    * Setter para o atributo dataLancamento.
    * @param dataLancamento - o novo valor para o atributo dataLancamento.
    */
    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    /**
    * Getter para o atributo dancabilidade.
    * @return o valor atual do atributo dancabilidade.
    */
    public int getDancabilidade() {
        return dancabilidade;
    }

    /**
    * Setter para o atributo dancabilidade.
    * @param dancabilidade um inteiro representando a dancabilidade da música.
    */
    public void setDancabilidade(int dancabilidade) {
        this.dancabilidade = dancabilidade;
    }

    /**
    * Getter para o atributo duracao.
    * @return o valor atual do atributo duracao.
    */
    public int getDuracao() {
        return duracao;
    }

    /**
    * Setter para o atributo duracao.
    * @param duracao um inteiro representando a duração da música.
    */
    public void setDuracao(int duracao) {
        this.duracao = duracao;
    }

    /**
    * Getter para o atributo vivacidade.
    * @return o valor atual do atributo vivacidade.
    */
    public int getVivacidade() {
        return vivacidade;
    }

    /**
    * Setter para o atributo vivacidade.
    * @param vivacidade um inteiro representando a vivacidade da música.
    */
    public void setVivacidade(int vivacidade) {
        this.vivacidade = vivacidade;
    }

    /**
    * Getter para o atributo popularidade.
    * @return o valor atual do atributo popularidade.
    */
    public int getPopularidade() {
        return popularidade;
    }

    /**
    * Setter para o atributo popularidade.
    * @param popularidade um inteiro representando a popularidade da música.
    */
    public void setPopularidade(int popularidade) {
        this.popularidade = popularidade;
    }

    /**
    * Getter para o atributo URI.
    * @return o valor atual do atributo URI.
    */
    public String getUri() {
        return uri;
    }

    /**
    * Setter para o atributo URI.
    * @param uri uma String representando a URI da música.
    */
    public void setUri(String uri) {
        this.uri = uri;
    }
}

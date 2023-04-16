package crud;

// Bibliotecas
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.InputMismatchException;
import java.util.ArrayList;
import java.util.List;
import java.text.ParseException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import app.IO;
import app.Musica;
import hashing.HashingExtensivel;
import arvoreB.ArvoreB;
import listaInvertida.ListaInvertida;

/**
 * CRUD - Classe responsavel por realizar as operacoes de manipulacao do arquivo
 * - popular a base de dados, cadastrar, pesquisar, atualizar, deletar.
 */
public class CRUD {

    // Arquivo sequencial
    private static final String arquivoCSV = "./src/resources/Spotify.csv";
    private static final String registroDB = "./src/resources/Registro.db";
    private static final String registroTXT = "./src/resources/Registro.txt";

    // Hashing
    private static HashingExtensivel hash;
    private static final String diretorioDB = "./src/resources/Diretorio.db";
    private static final String bucketDB = "./src/resources/Bucket.db";

    // Arvore B
    private static ArvoreB arvoreB;
    private static final String arvoreBDB = "./src/resources/ArvoreB.db";

    // Lista invertida
    private static ListaInvertida lista;

    // IO
    private static IO io;

    /**
     * Construtor padrao da classe CRUD.
     */
    public CRUD () throws Exception {
        io = new IO();
        hash = new HashingExtensivel();
        arvoreB = new ArvoreB();
        lista = new ListaInvertida();
    }
    
    /**
     * Metodo para carregar todas as musicas do arquivo csv e salva-las em
     * arquivo.
     * @throws Exception Se ocorrer algum erro ao manipular os arquivos.
     */
    public void carregarCSV() throws Exception {
        BufferedReader csvFile = null;
        RandomAccessFile dbFile = null;

        try {
            csvFile = new BufferedReader (new FileReader (arquivoCSV));
            dbFile = new RandomAccessFile (registroDB, "rw");

            boolean continuar = true;
            if (dbFile.length() > 0) {
                // Perguntar somente uma ver e apagar se digitar 1
                // Entrada invalida ou igual a 2, manter arquivo
                String menu = "\n+------------------------------------------+" +
                              "\n|        Banco de dados ja populado!       |" +
                              "\n|         Deseja resetar o arquivo?        |" +
                              "\n|------------------------------------------|" +
                              "\n|         1 - SIM          2 - NAO         |" +
                              "\n+------------------------------------------+";                
                
                System.out.println(menu);
                int opcao = io.readInt("\nDigite uma opcao: ");
                if (opcao != 1) {
                    continuar = false;
                    System.out.println("\nArquivo \"" + registroDB + "\" mantido com sucesso!");
                }
            }

            if (continuar == true) {
                // Apagar antigo "Registros.db"
                File antigoDB = new File(registroDB);
                antigoDB.delete();
                dbFile = new RandomAccessFile (registroDB, "rw");

                // Apagar antigo "Bucket.db"
                File antigoBucket = new File(bucketDB);
                antigoBucket.delete();
                hash.inicializarBuckets();

                // Apagar antigo "Diretorio.db"
                File antigoDiretorio = new File(diretorioDB);
                antigoDiretorio.delete();
                hash.inicializarDiretorio();

                // Apagar antiga "ArvoreB.db"
                File antigaArvoreB = new File(arvoreBDB);
                antigaArvoreB.delete();
                arvoreB.inicializarArvoreB();

                // Apagar antigas listas invertidas
                lista.delete();
                lista.inicializarListas();

                Musica musica = new Musica();
                byte[] newId;

                int ultimoId = 0;

                // Reservar espaco no cabecalho do arquivo para o inteiro ultimo ID
                dbFile.seek(0);
                newId = musica.intToByteArray(ultimoId);
                dbFile.write(newId);

                // Ler CSV, criar array de bytes e salva-lo em arquivo
                String linhaLida;
                while ((linhaLida = csvFile.readLine()) != null) {

                    // Obter posicao do registro no arquivo
                    long posRegistro = dbFile.getFilePointer();

                    ultimoId++;
                    musica = new Musica (linhaLida, ultimoId);
                    byte[] byteArray = musica.toByteArray();
                    dbFile.write(byteArray);

                    // Inserir, utilizando hashing
                    //hash.inserir(musica, posRegistro);

                    // Inserir, utilizando arvore B
                    //arvoreB.inserir(musica, posRegistro);

                    // Inserir nas listas invertidas
                    lista.inserir(musica, posRegistro);
                }

                /*
                // Mostrar arquivo dar arvore B
                int totalElementos = arvoreB.contarChaves();
                System.out.println("Ha' " + totalElementos + " na arvore:\n");
                arvoreB.mostrarArquivo();
                io.readLine();
                */

                // Atualizar ultimo ID no cabecalho do arquivo
                dbFile.seek(0);
                newId = musica.intToByteArray(ultimoId);
                dbFile.write(newId);

                System.out.println("\nArquivo \"" + registroDB + 
                                "\" criado com sucesso!");
            }
        } catch (FileNotFoundException e) {
            System.out.println("\nERRO: O arquivo \""+ arquivoCSV + 
                               "\"não encontrado!\n");
        } catch (IOException e) {
            System.out.println("\nERRO: Ocorreu um erro de escrita no " +
                               "arquivo \"" + registroDB + "\"\n");
        } finally {
            if (csvFile != null) csvFile.close();
            if (dbFile != null) dbFile.close();
        }
    }

    /**
     * Metodo para cadastrar uma nova musica no banco de dados.
     * @throws Exception Se ocorrer algum erro ao manipular os arquivos.
     */
    public void create () throws Exception {
        RandomAccessFile dbFile = null;

        try {
            dbFile = new RandomAccessFile (registroDB, "rw");

            if (dbFile.length() > 0) {

                // Ler ultimo ID no cabecalho do arquivo
                dbFile.seek(0);
                int ultimoId = dbFile.readInt();

                // Criar nova Musica
                Musica musica = new Musica();
                musica.lerMusica();
                ultimoId++;
                musica.setId(ultimoId);
                System.out.println(musica);

                // Atualizar ultimo ID no cabecalho do arquivo
                dbFile.seek(0);
                byte[] newId = musica.intToByteArray(ultimoId);
                dbFile.write(newId);

                // Escrever musica no final do arquivo
                long finalRegistro = dbFile.length();
                dbFile.seek(finalRegistro);
                byte[] byteArray = musica.toByteArray();
                dbFile.write(byteArray);

                // Inserir no hashing extensivel
                hash.inserir(musica, finalRegistro);

                // Inserir, utilizando arvore B
                arvoreB.inserir(musica, finalRegistro);

                // Mostrar arquivo dar arvore B
                int totalElementos = arvoreB.contarChaves();
                System.out.println("Ha' " + totalElementos + " na arvore:\n");
                arvoreB.mostrarArquivo();
                io.readLine();

                System.out.println("\nMusica [" + musica.getId() + "]: \"" +
                                            musica.getNome() + "\" " +
                                            "cadastrada com sucesso!");
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


    /**
     * Metodo para exibir as informacoes de uma musica a partir do seu ID.
     * @return true, se a música foi encontrada; false, caso contrario.
     * @throws Exception Se ocorrer algum erro ao manipular o arquivo.
     */

    public void read () throws Exception {
        
        boolean pesquisaFeita = false;
        int opcao = -1;

        String menu = "\n+------------------------------------------+" +
                        "\n|               MENU PESQUISA              |" +
                        "\n|------------------------------------------|" +
                        "\n| 1 - Id                                   |" +
                        "\n| 2 - Data de lancamento                   |" +
                        "\n| 3 - Nome do artista                      |" +
                        "\n| 4 - Data de lancamento e nome do artista |" +
                        "\n+------------------------------------------+";
        
        do {
            try{
                System.out.println(menu);
                opcao = io.readInt("\nDigite uma opcao: ");

                switch(opcao) {
                    case 1: procurarId();       break;
                    case 2: procurarDatas();    break;
                    case 3: procurarArtistas(); break;
                    case 4: System.out.println("\nNao implementado ainda!\n"); break;
                }

            } catch (InputMismatchException e) {
                System.out.println("\nERRO: Por favor, digite uma opcao valida de " + 
                           "1 a 4.");
                io.readLine();
            }       
        } while(opcao < 1 || opcao > 4);
    }

    private void procurarId() throws Exception {
        int idProcurado = 0;
        do{
            System.out.print("\nDigite o ID procurado: ");
            try {
                idProcurado = io.readInt();
            } catch (InputMismatchException e) {
                io.readLine();
                System.out.println("\nERRO: ID invalido!\n");
            }
        } while (idProcurado == 0);

        // Procurar sequenciamente
        read(idProcurado);

        // Procurar no hashing extensivel
        hash.read(idProcurado);
    }

    private void procurarArtistas() throws Exception {

        // Ler palavras para filtar os artistas
        System.out.print("\nDigite o artista procurado: ");
        String texto = io.readLine();
        texto = lista.normalizarString(texto);

        // Separar o texto em um array de palavras
        String arrayPalavras[] = texto.split(" ");

        // Procurar posicoes correspondentes
        List<Long> enderecos = lista.readArtistas(arrayPalavras[0]);
        List<Long> listaTmp = null;

        for(int i = 1; i < arrayPalavras.length; i++) {

            // Obter intersecao entre as listas
            listaTmp = lista.readArtistas(arrayPalavras[i]);
            enderecos.retainAll(listaTmp);
        }

        // Abrir "Registros.db" e obter artistas procurados
        RandomAccessFile dbFile = null;

        try {
            dbFile = new RandomAccessFile (registroDB, "rw");

            for(int i = 0; i < enderecos.size(); i++) {

                // Posicionar ponteiro
                long posicao = enderecos.get(i).longValue();
                dbFile.seek(posicao);

                // Ler informacoes do registro
                boolean lapide = dbFile.readBoolean();
                int tamRegistro = dbFile.readInt();

                // Ler e criar novo Objeto musica
                Musica musica = new Musica();
                byte[] registro = new byte[tamRegistro];
                dbFile.read(registro);
                musica.fromByteArray(registro);
                
                // Mostrar
                System.out.println(musica);
            }

        } catch (FileNotFoundException e) {
                System.out.println("\nERRO: Registro nao encontrado!" +
                                   "\n      Tente carregar os dados iniciais primeiro!\n");
        } finally {
            if (dbFile != null) dbFile.close();
        }        
        
    }

    private void procurarDatas() throws Exception {

        // Ler palavras para filtar os artistas
        System.out.print("\nDigite o ano procurado: ");
        String strDate = io.readLine();

        // Converter para data
        Locale US = new Locale("US");
        DateFormat df;
        Date dataProcurada;
        
        try{
            df = new SimpleDateFormat("yyyy", US);
            dataProcurada = df.parse(strDate);

        // Em caso de execao, data sera' 01-01-0001
        } catch (ParseException e) {
            System.out.print("ERRO: Data invalida (" + strDate + ")\n");
            strDate = "0001";
            df = new SimpleDateFormat("yyyy", US);
            dataProcurada = df.parse(strDate);
        } catch (IllegalArgumentException e) {
            System.out.print("ERRO: Data invalida (" + strDate + ")\n");
            strDate = "0001";
            df = new SimpleDateFormat("yyyy", US);
            dataProcurada = df.parse(strDate);  
        }        
        

        // Procurar posicoes correspondentes
        List<Long> enderecos = lista.readAnosLancamento(dataProcurada);

        // Abrir "Registros.db" e obter datas procurados
        RandomAccessFile dbFile = null;

        try {
            dbFile = new RandomAccessFile (registroDB, "rw");

            for(int i = 0; i < enderecos.size(); i++) {

                // Posicionar ponteiro
                long posicao = enderecos.get(i).longValue();
                dbFile.seek(posicao);

                // Ler informacoes do registro
                boolean lapide = dbFile.readBoolean();
                int tamRegistro = dbFile.readInt();

                // Ler e criar novo Objeto musica
                Musica musica = new Musica();
                byte[] registro = new byte[tamRegistro];
                dbFile.read(registro);
                musica.fromByteArray(registro);
                
                // Mostrar
                System.out.println(musica);
            }

        } catch (FileNotFoundException e) {
                System.out.println("\nERRO: Registro nao encontrado!" +
                                   "\n      Tente carregar os dados iniciais primeiro!\n");
        } finally {
            if (dbFile != null) dbFile.close();
        }        
        
    }

    /**
     * Metodo para excluir uma musica a partir do seu ID.
     * @return true, se a música foi excluida; false, caso contrario.
     * @throws Exception Se ocorrer algum erro ao manipular o arquivo.
     */
    public boolean delete () throws Exception {
        int idProcurado = 0;

       do {
           System.out.print("\nDigite o ID procurado: ");
           try {
               idProcurado = io.readInt();
           } catch (InputMismatchException e) {
               io.readLine();
               System.out.println("\nERRO: ID invalido!\n");
               idProcurado = 0;
           }
       } while (idProcurado == 0);

        // Deletar no hashing extensivel
        hash.delete(idProcurado);

       return delete(idProcurado);
    }

    /**
     * Metodo para atualizar uma musica a partir do seu ID.
     * @return true, se a música foi atualizada; false, caso contrario.
     * @throws Exception Se ocorrer algum erro ao manipular o arquivo.
     */
    public boolean update () throws Exception {
        int idProcurado = 0;

       do {
           System.out.print("\nDigite o ID procurado: ");
           try {
               idProcurado = io.readInt();
           } catch (InputMismatchException e) {
               io.readLine();
               System.out.println("\nERRO: ID invalido!\n");
               idProcurado = 0;
           }
       } while (idProcurado == 0);

       return update(idProcurado);
    }

    /**
     * Metodo privado para exibir as informacoes de uma musica a partir do seu
     * ID.
     * @return true, se a música foi encontrada; false, caso contrario.
     * @throws Exception Se ocorrer algum erro ao manipular o arquivo.
     */
    private boolean read (int idProcurado) throws Exception {
        RandomAccessFile dbFile = null;
        boolean find = false;

        try {
            dbFile = new RandomAccessFile (registroDB, "rw");

            if (dbFile.length() > 0) {

                Musica musica = null;
                boolean lapide;
                int tamRegistro;


                // Obter ultimo ID adicionado
                dbFile.seek(0);
                long posicaoAtual = dbFile.getFilePointer();
                int ultimoId = dbFile.readInt();

                while (dbFile.length() != posicaoAtual && find == false) {
                                        
                    musica = new Musica();

                    // Ler informacoes do registro
                    lapide = dbFile.readBoolean();
                    tamRegistro = dbFile.readInt();

                    // Se registro for valido, ler e comparar com ID procurado
                    if (lapide == false) {
                        byte[] registro = new byte[tamRegistro];
                        dbFile.read(registro);
                        musica.fromByteArray(registro);

                        if (idProcurado == musica.getId()) {
                            find = true;
                            System.out.println(musica);
                        }

                    // Se nao for, pular o registro e reposicionar ponteiro
                    } else {
                        posicaoAtual = dbFile.getFilePointer();
                        long proximaPosicao = posicaoAtual + (long)tamRegistro;
                        dbFile.seek(proximaPosicao);
                    }
                    posicaoAtual = dbFile.getFilePointer();
                }

                if (find == false) {
                    System.out.println("\nMusica de ID (" + idProcurado + 
                                    ") não esta cadastrada!"); 
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
            return find;
        }
    }

    /**
     * Metodo privado para excluir uma musica a partir do seu ID.
     * @return true, se a música foi excluida; false, caso contrario.
     * @throws Exception Se ocorrer algum erro ao manipular o arquivo.
     */
    private boolean delete (int idProcurado) throws Exception {
        RandomAccessFile dbFile = null;
        boolean find = false;

        try {
            dbFile = new RandomAccessFile (registroDB, "rw");

            if (dbFile.length() > 0) {

                Musica musica = null;
                Musica aux = new Musica();
                boolean lapide;
                int tamRegistro;

                // Obter ultimo ID adicionado
                dbFile.seek(0);
                long posicaoAtual = dbFile.getFilePointer();
                int tamArquivo = dbFile.readInt();

                while (dbFile.length() != posicaoAtual && find == false) {
                    
                    musica = new Musica();

                    // Ler informacoes do registro  
                    long posicaoInicio = dbFile.getFilePointer();
                    lapide = dbFile.readBoolean();
                    tamRegistro = dbFile.readInt();

                    // Se registro for valido, ler e comparar com ID procurado
                    if (lapide == false) {
                        byte[] registro = new byte[tamRegistro];
                        dbFile.read(registro);
                        musica.fromByteArray(registro);

                        if (idProcurado == musica.getId()) {
                            musica.setLapide(true);

                            // Guardar ponteiro atual
                            long posicaoFinal = dbFile.getFilePointer();

                            // Apagar logicamente o registro
                            dbFile.seek(posicaoInicio);
                            byte[] newLapide = aux.booleanToByteArray(musica.isLapide());
                            dbFile.write(newLapide);
                            find = true;

                            // Retornar ponteiro para final do registro
                            dbFile.seek(posicaoFinal);
                            
                            System.out.println("\nMusica [" + musica.getId() + "]: \"" +
                                            musica.getNome() + "\" " +
                                            "deletada com sucesso!");
                        }

                    // Se nao for, pular o registro e reposicionar ponteiro    
                    } else {
                        posicaoAtual = dbFile.getFilePointer();
                        long proximaPosicao = posicaoAtual + (long)tamRegistro;
                        dbFile.seek(proximaPosicao);
                    }
                    posicaoAtual = dbFile.getFilePointer();
                }

                if (find == false) {
                    System.out.println("\nMusica de ID (" + idProcurado + 
                                    ") não esta cadastrada!");
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
            return find;
        }
    }

    /**
     * Metodo privado para atualizar uma musica a partir do seu ID.
     * @return true, se a música foi excluida; false, caso contrario.
     * @throws Exception Se ocorrer algum erro ao manipular o arquivo.
     */
    private boolean update (int idProcurado) throws Exception {
        RandomAccessFile dbFile = null;
        boolean find = false;
        boolean atualizado = false;

        try {
            dbFile = new RandomAccessFile (registroDB, "rw");

            if (dbFile.length() > 0) {

                Musica musica = null;
                Musica aux = new Musica();
                boolean lapide;
                int tamRegistro;

                // Obter ultimo ID adicionado
                dbFile.seek(0);
                long posicaoAtual = dbFile.getFilePointer();
                int tamArquivo = dbFile.readInt();
                int ultimoId = tamArquivo;

                while (dbFile.length() != posicaoAtual && find == false) {
                    
                    musica = new Musica();

                    // Ler informacoes do registro
                    long posicaoInicio = dbFile.getFilePointer();
                    lapide = dbFile.readBoolean();
                    tamRegistro = dbFile.readInt();

                    // Se registro for valido, ler e comparar com ID procurado
                    if (lapide == false) {
                        byte[] registro = new byte[tamRegistro];
                        dbFile.read(registro);
                        musica.fromByteArray(registro);

                        if (idProcurado == musica.getId()) {

                            // Ler e criar novo Objeto musica
                            Musica newMusica = musica.clone();
                            atualizado  = newMusica.atualizar();
                            byte[] newRegistro = newMusica.toByteArray();

                            // Obter tamanho dos registros
                            ByteArrayInputStream bais = new ByteArrayInputStream(newRegistro);
                            DataInputStream dis = new DataInputStream(bais);
                            dis.readBoolean();

                            // Nao basta usar a funcao length, pois caso ja tenha sido alterado
                            // o tamanho reservado sera' menor que o efetivamente usado
                            int newRegistroLength = dis.readInt();
                            int registroAtualLength = tamRegistro;

                            if (newRegistroLength <= registroAtualLength) {

                                // Guardar ponteiro atual
                                long posicaoFinal = dbFile.getFilePointer();

                                // Salvar novo registro, mas manter tamanho antigo
                                newRegistro = newMusica.toByteArray(registro.length);
                                dbFile.seek(posicaoInicio);
                                dbFile.write(newRegistro);

                                // Retornar ponteiro para final do registro
                                dbFile.seek(posicaoFinal);

                            } else {
                                
                                // Marcar registro como invalido
                                musica.setLapide(true);

                                dbFile.seek(posicaoInicio);
                                byte[] newLapide = aux.booleanToByteArray(musica.isLapide());
                                dbFile.write(newLapide);

                                // Escrever a musica atualizada no final do arquivo
                                long finalArquivo = dbFile.length();
                                dbFile.seek(finalArquivo);
                                newRegistro = newMusica.toByteArray();
                                dbFile.write(newRegistro);

                               // Atualizar no hashing extensivel
                               hash.update(idProcurado, finalArquivo);
                            }

                            if (atualizado == true) {
                                System.out.println("\nMusica [" + newMusica.getId() + "]: \"" +
                                                newMusica.getNome() + "\" " +
                                                "atualizada com sucesso!");
                            }
                            find = true;
                        }

                    // Se nao for, pular o registro e reposicionar ponteiro    
                    } else {
                        posicaoAtual = dbFile.getFilePointer();
                        long proximaPosicao = posicaoAtual + (long)tamRegistro;
                        dbFile.seek(proximaPosicao);
                    }
                    posicaoAtual = dbFile.getFilePointer();
                }

                if (find == false) {
                    System.out.println("\nMusica de ID (" + idProcurado + 
                                    ") não esta cadastrada!"); 
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
            return atualizado;
        }
    }

    /**
     * Metodo para exibir as informacoes de uma musica a partir do seu ID.
     * @return true, se a música foi encontrada; false, caso contrario.
     * @throws Exception Se ocorrer algum erro ao manipular o arquivo.
    */
    public void abrirMusica() throws Exception {
        int idProcurado = 0;

       do {
           System.out.print("\nDigite o ID procurado: ");
           try {
               idProcurado = io.readInt();
           } catch (InputMismatchException e) {
               io.readLine();
               System.out.println("\nERRO: ID invalido!\n");
               idProcurado = 0;
           }
       } while (idProcurado == 0);

       abrirMusica(idProcurado);
    }

    /**
     * Metodo privado para exibir as informacoes de uma musica a partir do seu
     * ID.
     * @throws Exception Se ocorrer algum erro ao manipular o arquivo ou ID nao
     * for encontrado
     */
    public void abrirMusica (int idProcurado) throws Exception{

        RandomAccessFile dbFile = null;
        boolean find = false;
        Musica musica = new Musica();

        try {
            dbFile = new RandomAccessFile (registroDB, "r");

            if (dbFile.length() > 0) {

                boolean lapide;
                int tamRegistro;

                // Obter ultimo ID adicionado
                dbFile.seek(0);
                long posicaoAtual = dbFile.getFilePointer();
                int ultimoId = dbFile.readInt();

                while (dbFile.length() != posicaoAtual && find == false) {
                    
                    musica = new Musica();

                    // Ler informacoes do registro
                    long posicaoInicio = dbFile.getFilePointer();
                    lapide = dbFile.readBoolean();
                    tamRegistro = dbFile.readInt();

                    // Se registro for valido, ler e comparar com ID procurado
                    if (lapide == false) {
                        byte[] registro = new byte[tamRegistro];
                        dbFile.read(registro);
                        musica.fromByteArray(registro);

                        if (idProcurado == musica.getId()) {
                            find = true;
                        }

                    // Se nao for, pular o registro e reposicionar ponteiro    
                    } else {
                        posicaoAtual = dbFile.getFilePointer();
                        long proximaPosicao = posicaoAtual + (long)tamRegistro;
                        dbFile.seek(proximaPosicao);
                    }
                    posicaoAtual = dbFile.getFilePointer();
                }

                if (find == false) {
                    System.out.println("\nMusica de ID (" + idProcurado + 
                                    ") nao esta cadastrada!");
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

            if (find == true) {
                abrirMusica (musica.getUri());
            }
        }
    }

    /**
    * Metodo privado para abrir a musica no aplicativo do Spotify, apartir da sua URI.
    * @param uri link da musica.
    */
    private void abrirMusica (String uri) throws IOException {
        // Tratamento da string URI para ser adaptavel ao link
        // Pegar terceiro elemento correspondente ao trackID da musica
        String[] parts = uri.split(":");
        String trackId = parts[2];
        
        // Obter uri da musica
        String url = "https://open.spotify.com/track/" + trackId;
        try {
            // Codigo para Linux ou Mac
            ProcessBuilder pb = new ProcessBuilder("xdg-open", url);
            pb.start();
        } catch (IOException e){
            // Codigo para windows
            Runtime.getRuntime().exec("cmd /c start " + url);
        } catch (Exception e) {
            System.out.print("\nERRO: " + e + " Link incorreto. Tente atualizar o URI!");
            System.out.print("\nModelo: \"spotify:track:(INSERIR TRACK ID)\"\n");
        }
    }

    /**
     * Metodo privado para salvar todo banco de dados em arquivo txt.
     * @throws Exception Se ocorrer algum erro ao manipular o arquivo.
     */
    public void saveTXT () throws Exception {
        RandomAccessFile dbFile = null;
        BufferedWriter dbFileTXT = null;

        try {
            // Apagar arquivo anterior caso exista
            File antigoTXT = new File(registroTXT);
            antigoTXT.delete();

            dbFileTXT = new BufferedWriter (new FileWriter (registroTXT));
            dbFile = new RandomAccessFile (registroDB, "r");

            if (dbFile.length() > 0) {

                Musica musica = null;
                boolean lapide;
                int tamRegistro;

                // Obter ultimo ID adicionado
                dbFile.seek(0);
                long posicaoAtual = dbFile.getFilePointer();
                int ultimoId = dbFile.readInt();

                while (dbFile.length() != posicaoAtual) {
                                        
                    musica = new Musica();

                    // Ler informacoes do registro
                    lapide = dbFile.readBoolean();
                    tamRegistro = dbFile.readInt();

                    // Se registro for valido, escrever
                    if (lapide == false) {
                        byte[] registro = new byte[tamRegistro];
                        dbFile.read(registro);
                        musica.fromByteArray(registro);
                        dbFileTXT.write(musica + "\n");

                    // Se nao for, pular o registro e reposicionar ponteiro
                    } else {
                        posicaoAtual = dbFile.getFilePointer();
                        long proximaPosicao = posicaoAtual + (long)tamRegistro;
                        dbFile.seek(proximaPosicao);
                    }
                    posicaoAtual = dbFile.getFilePointer();
                }

                System.out.println("\nArquivo \"" + registroTXT + 
                                "\" criado com sucesso!");

            } else {
                System.out.println("\nERRO: Registro vazio!" +
                                   "\n      Tente carregar os dados iniciais primeiro!\n");
            }

        } catch (FileNotFoundException e) {
                System.out.println("\nERRO: Registro nao encontrado!" +
                                   "\n      Tente carregar os dados iniciais primeiro!\n");
        } finally {
            if (dbFile != null) dbFile.close();
            if (dbFileTXT != null) dbFileTXT.close();
        }
    }

}

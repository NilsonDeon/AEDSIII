// Package
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
import java.io.LineNumberReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.List;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// Bibliotecas proprias
import app.IO;
import app.Musica;
import hashing.HashingExtensivel;
import arvores.arvoreB.ArvoreB;
import arvores.arvoreBestrela.ArvoreBestrela;
import arvores.arvoreBmais.ArvoreBmais;
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

    // Arvore B*
    private static ArvoreBestrela arvoreBestrela;
    private static final String arvoreBestrelaDB = "./src/resources/ArvoreBestrela.db";

    // Arvore B+
    private static ArvoreBmais arvoreBmais;
    private static final String arvoreBmaisDB = "./src/resources/ArvoreBmais.db";

    // Lista invertida
    private static ListaInvertida lista;

    // IO
    private static IO io;

    /**
     * Construtor padrao da classe CRUD.
     */
    public CRUD () {
        io = new IO();
        hash = new HashingExtensivel();
        arvoreB = new ArvoreB();
        arvoreBestrela = new ArvoreBestrela();
        //arvoreBmais = new ArvoreBmais();
        lista = new ListaInvertida();
    }
    
    /**
     * Metodo para carregar todas as musicas do arquivo csv e salva-las em
     * arquivo.
     */
    public void carregarCSV() {
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

                // Apagar antiga "ArvoreBestrela.db"
                File antigaArvoreBestrela = new File(arvoreBestrelaDB);
                antigaArvoreBestrela.delete();
                arvoreBestrela.inicializarArvoreB();

                // Apagar antiga "ArvoreBmais.db"
                //File antigaArvoreBmais = new File(arvoreBmaisDB);
                //antigaArvoreBmais.delete();
                //arvoreBmais.inicializarArvoreB();

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

                // Obter tamanho arquivo csv
                LineNumberReader lnr = new LineNumberReader(new FileReader(arquivoCSV));
                lnr.skip(Long.MAX_VALUE);
                long tamanhoArqCSV = lnr.getLineNumber();
                lnr.close();

                // Mostrar mensagem de insercao
                System.out.println("\nCarregando dados do CSV: ");

                // Ler CSV, criar array de bytes e salva-lo em arquivo
                String linhaLida;
                int count = 0;
                while ((linhaLida = csvFile.readLine()) != null) {

                    // Mostrar barra progresso
                    io.gerarBarraProgresso(tamanhoArqCSV, count);
                    count++;

                    // Obter posicao do registro no arquivo
                    long posRegistro = dbFile.getFilePointer();

                    ultimoId++;
                    musica = new Musica (linhaLida, ultimoId);
                    byte[] byteArray = musica.toByteArray();
                    dbFile.write(byteArray);

                    // Inserir, utilizando hashing
                    //hash.inserir(musica, posRegistro);

                    // Inserir, utilizando arvore B
                    arvoreB.inserir(musica, posRegistro);

                    // Inserir, utilizando arvore B*
                    //arvoreBestrela.inserir(musica, posRegistro);

                    // Inserir, utilizando arvore B+
                    //arvoreBmais.inserir(musica, posRegistro);

                    // Inserir nas listas invertidas
                    //lista.inserir(musica, posRegistro);
                }

                // Mostrar barra de progresso completa
                io.gerarBarraProgresso(tamanhoArqCSV, count);

                System.out.println("\n");
                arvoreB.mostrarArquivo();
                              
                // Atualizar ultimo ID no cabecalho do arquivo
                dbFile.seek(0);
                newId = musica.intToByteArray(ultimoId);
                dbFile.write(newId);

                System.out.println("\nArquivo \"" + registroDB + 
                                "\" criado com sucesso!");
            
            }

            // Fechar arquivos
            csvFile.close();
            dbFile.close();

        } catch (FileNotFoundException e) {
            System.out.println("\nERRO: O arquivo \""+ arquivoCSV + 
                               "\"não encontrado!\n");
        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao escrever o arquivo \"" + dbFile + "\"\n");
            System.out.println("\nERRO: " + e.getMessage() + " ao ler o arquivo \"" + csvFile + "\"\n");
        }
    }

    /**
     * Metodo para cadastrar uma nova musica no banco de dados.
     */
    public void create () {
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

                // Inserir na arvore B
                arvoreB.inserir(musica, finalRegistro);

                // Inserir nas listas invertidas
                lista.inserir(musica, finalRegistro);

                System.out.println("\nMusica [" + musica.getId() + "]: \"" +
                                            musica.getNome() + "\" " +
                                            "cadastrada com sucesso!");
            } else {
                System.out.println("\nERRO: Registro vazio!" +
                                   "\n      Tente carregar os dados iniciais primeiro!\n");
            }

            // Fechar arquivos
            dbFile.close();

        } catch (FileNotFoundException e) {
            System.out.println("\nERRO: Registro vazio!" +
                               "\n      Tente carregar os dados iniciais primeiro!\n");
        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao escrever o arquivo \"" + dbFile + "\"\n");
        }
    }


    /**
     * Metodo para exibir as informacoes de uma musica a partir do seu ID.
     * @return true, se a música foi encontrada; false, caso contrario.
     */

    public void read () {
        
        // Testar se arquivo existe
        File arquivoRegistro = new File(registroDB);

        // Se existir, fazer a busca
        if (arquivoRegistro.exists()) {

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
                        case 1 : procurarId();             break;
                        case 2 : procurarDatas();          break;
                        case 3 : procurarArtistas();       break;
                        case 4 : procurarDatasEArtistas(); break;
                        default: System.out.println("\nERRO: Por favor, digite uma opcao valida de 1 a 4.");
                    }

                } catch (InputMismatchException e) {
                    System.out.println("\nERRO: Por favor, digite uma opcao valida de 1 a 4.");
                    io.readLine();
                }       
            } while(opcao < 1 || opcao > 4);

        // Senao, mensagem de erro
        } else {
            System.out.println("\nERRO: Registro vazio!" +
                               "\n      Tente carregar os dados iniciais primeiro!\n");
        
        }
    }

    /**
     * Metodo para obter horario atual.
     * @return timestamp atual em milissegundos
     */
    private long now() {
      return System.currentTimeMillis();
    }

    /**
     * Metodo para obter o tempo de busca durante a pesquisa.
     * @param inicio - horario de inicio da busca.
     * @param fim - horario que a busca terminou.
     * @return tempo relativo em segundos.
     */
    private String getTempoBusca(long inicio, long fim) {
        return ((fim - inicio) / 1000.0 + " segundos");
    }

    /**
     * Metodo para procurar um Id em todas as estrututuras cadastradas.
     */
    private void procurarId() {

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
        long sequencialInicio = now();
        long posicaoSequencial = read(idProcurado);
        long sequencialFim = now();
        System.out.println("\nposicao sequencial: " + posicaoSequencial);

        // Procurar no hashing extensivel
        long hashInicio = now();
        long posicaoHash = hash.read(idProcurado);
        long hashFim = now();
        System.out.println("posicao hash: " + posicaoHash);

        // Procurar na Arvore B
        long arvoreBInicio = now();
        long posicaoArvoreB = arvoreB.read(idProcurado);
        long arvoreBFim = now();
        System.out.println("posicao arvoreB: " + posicaoArvoreB);

        // Verificar se todas as estruturas encontraram a mesma musica com sucesso
        boolean find = (posicaoSequencial == posicaoHash) && (posicaoHash == posicaoArvoreB) && (posicaoSequencial != -1);
        
        if(find) {

            // Mostrar musica
            Musica musicaProcurada = lerMusica(posicaoSequencial);
            System.out.println(musicaProcurada);

            // Mostrar tempos de busca
            String temposBusca = "\nTempos de busca:\n" +
                                "\nSequencialmente ----------------------------- " + getTempoBusca(sequencialInicio, sequencialFim) +
                                "\nHash Estensivel ----------------------------- " + getTempoBusca(hashInicio, hashFim) +
                                "\nArvore B ------------------------------------ " + getTempoBusca(arvoreBInicio, arvoreBFim);
            System.out.println(temposBusca);

        } else {
            System.out.println("\nMusica de ID (" + idProcurado + ") não esta cadastrada!");
        }
        
    }

    private void procurarArtistas() {

        // Ler palavras para filtar os artistas
        System.out.print("\nDigite o artista procurado: ");
        String texto = io.readLine();
        String textoBak = texto;
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

            // Se nao encontrar musicas, printar que nao foi encontrado
            if (enderecos.size() == 0) {
                System.out.println("\nNao foram encontradas musica para (" + textoBak + ")\n");

            // Se somente tiver uma musica, printar ela
            } else if (enderecos.size() == 1) {
                System.out.println("\nApenas uma musica foi encontrada:\n");

                // Posicionar ponteiro
                long posicao = enderecos.get(0).longValue();
                dbFile.seek(posicao);

                // Ler informacoes do registro
                boolean lapide = dbFile.readBoolean();
                int tamRegistro = dbFile.readInt();

                // Ler e criar novo Objeto musica
                Musica musica = new Musica();
                byte[] registro = new byte[tamRegistro];
                dbFile.read(registro);
                musica.fromByteArray(registro);
                
                // Mostrar musica
                System.out.println(musica);

            // Se tiver mais de uma, deixar como opcao escolher a desejada
            } else {
                System.out.println("\nForam encontradas " + enderecos.size() + " musicas:\n");
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
                    
                    // Mostrar nome e artistas das musicas encontradas
                    System.out.println(String.format("% 4d - %s", i+1, musica.getNome()));
                }

                // Perguntar qual musica deseja obter as informacoes
                int opcao = 0;
                do {
                    try{
                        // Ler musica desejada
                        opcao = io.readInt("\nSelecione a musica desejada: ");

                        // Posicionar ponteiro
                        long posicao = enderecos.get(opcao-1).longValue();
                        dbFile.seek(posicao);

                        // Ler informacoes do registro
                        boolean lapide = dbFile.readBoolean();
                        int tamRegistro = dbFile.readInt();

                        // Ler e criar novo Objeto musica
                        Musica musica = new Musica();
                        byte[] registro = new byte[tamRegistro];
                        dbFile.read(registro);
                        musica.fromByteArray(registro);
                        
                        // Mostrar musica desejada
                        System.out.println(musica);
                    
                    // Caso le-se um valor nao numerico
                    } catch (InputMismatchException e) {

                        // Mostrar erro
                        System.out.println("\nERRO: Por favor, digite uma opcao valida de 1 a " + enderecos.size() + "."); 
                        io.readLine();
                        opcao = 0;
                    
                    // Caso o valor nao pertenca ao array
                    } catch (Exception e) {

                        // Mostrar erro
                        System.out.println("\nERRO: Por favor, digite uma opcao valida de 1 a " + enderecos.size() + "."); 
                        opcao = 0;
                    }
                } while (opcao == 0);
            }

            // Fechar arquivos
            dbFile.close();

        } catch (FileNotFoundException e) {
            System.out.println("\nERRO: Registro vazio!" +
                               "\n      Tente carregar os dados iniciais primeiro!\n");
        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler o arquivo \"" + registroDB + "\"\n");
        }        
    }

    private void procurarDatas() {

        // Ler data para filtar as musicas
        System.out.print("\nDigite o ano procurado: ");
        String strDate = io.readLine();
        String strDateBak = strDate;

        // Converter para data
        Locale US = new Locale("US");
        DateFormat df;
        Date dataProcurada = new Date();
        
        try{
            df = new SimpleDateFormat("yyyy", US);
            dataProcurada = df.parse(strDate);

        // Em caso de execao, data sera' 01-01-0001
        } catch (ParseException e) {
            System.out.print("ERRO: Data invalida (" + strDate + ")\n");
            strDate = "0001";
            df = new SimpleDateFormat("yyyy", US);
            try {
                dataProcurada = df.parse(strDate);
            } catch (ParseException ex) {}

        } catch (IllegalArgumentException e) {
            System.out.print("ERRO: Data invalida (" + strDate + ")\n");
            strDate = "0001";
            df = new SimpleDateFormat("yyyy", US);
            try {
                dataProcurada = df.parse(strDate);
            } catch (ParseException ex) {}
        }        
        

        // Procurar posicoes correspondentes
        List<Long> enderecos = lista.readAnosLancamento(dataProcurada);

        // Abrir "Registros.db" e obter datas procurados
        RandomAccessFile dbFile = null;

        try {
            dbFile = new RandomAccessFile (registroDB, "rw");

            // Se nao encontrar musicas, printar que nao foi encontrado
            if (enderecos.size() == 0) {
                System.out.println("\nNao foram encontradas musicas para a data (" + strDateBak + ")\n");

            // Se somente tiver uma musica, printar ela
            } else if (enderecos.size() == 1) {
                System.out.println("\nApenas uma musica foi encontrada:\n");

                // Posicionar ponteiro
                long posicao = enderecos.get(0).longValue();
                dbFile.seek(posicao);

                // Ler informacoes do registro
                boolean lapide = dbFile.readBoolean();
                int tamRegistro = dbFile.readInt();

                // Ler e criar novo Objeto musica
                Musica musica = new Musica();
                byte[] registro = new byte[tamRegistro];
                dbFile.read(registro);
                musica.fromByteArray(registro);
                
                // Mostrar musica
                System.out.println(musica);

            // Se tiver mais de uma, deixar como opcao escolher a desejada
            } else {
                System.out.println("\nForam encontradas " + enderecos.size() + " musicas:\n");
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
                    
                    // Mostrar nome e data de lancamento das musicas encontradas
                    System.out.println(String.format("% 4d - %s", i+1, musica.getNome()));
                }

                // Perguntar qual musica deseja obter as informacoes
                int opcao = 0;
                do {
                    try{
                        // Ler musica desejada
                        opcao = io.readInt("\nSelecione a musica desejada: ");

                        // Posicionar ponteiro
                        long posicao = enderecos.get(opcao-1).longValue();
                        dbFile.seek(posicao);

                        // Ler informacoes do registro
                        boolean lapide = dbFile.readBoolean();
                        int tamRegistro = dbFile.readInt();

                        // Ler e criar novo Objeto musica
                        Musica musica = new Musica();
                        byte[] registro = new byte[tamRegistro];
                        dbFile.read(registro);
                        musica.fromByteArray(registro);
                        
                        // Mostrar musica desejada
                        System.out.println(musica);
                    
                    // Caso le-se um valor nao numerico
                    } catch (InputMismatchException e) {

                        // Mostrar erro
                        System.out.println("\nERRO: Por favor, digite uma opcao valida de 1 a " + enderecos.size() + "."); 
                        io.readLine();
                        opcao = 0;
                    
                    // Caso o valor nao pertenca ao array
                    } catch (Exception e) {

                        // Mostrar erro
                        System.out.println("\nERRO: Por favor, digite uma opcao valida de 1 a " + enderecos.size() + "."); 
                        opcao = 0;
                    }
                } while (opcao == 0);
            }

            // Fechar arquivos
            dbFile.close();

        } catch (FileNotFoundException e) {
            System.out.println("\nERRO: Registro vazio!" +
                               "\n      Tente carregar os dados iniciais primeiro!\n");
        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler o arquivo \"" + registroDB + "\"\n");
        }
    }

    private void procurarDatasEArtistas() {

        // Ler palavras para filtar os artistas
        System.out.print("\nDigite o artista procurado: ");
        String texto = io.readLine();
        String textoBak = texto;
        texto = lista.normalizarString(texto);

        // Separar o texto em um array de palavras
        String arrayPalavras[] = texto.split(" ");

        // Ler data para filtar as musicas
        System.out.print("\nDigite o ano procurado: ");
        String strDate = io.readLine();
        String strDateBak = strDate;

        // Converter para data
        Locale US = new Locale("US");
        DateFormat df;
        Date dataProcurada = new Date();
        
        try{
            df = new SimpleDateFormat("yyyy", US);
            dataProcurada = df.parse(strDate);

        // Em caso de execao, data sera' 01-01-0001
        } catch (ParseException e) {
            System.out.print("ERRO: Data invalida (" + strDate + ")\n");
            strDate = "0001";
            df = new SimpleDateFormat("yyyy", US);
            try {
                dataProcurada = df.parse(strDate);
            } catch (ParseException ex) {}

        } catch (IllegalArgumentException e) {
            System.out.print("ERRO: Data invalida (" + strDate + ")\n");
            strDate = "0001";
            df = new SimpleDateFormat("yyyy", US);
            try {
                dataProcurada = df.parse(strDate);
            } catch (ParseException ex) {}            
        }

        // Procurar posicoes correspondentes pela data
        List<Long> enderecos = lista.readAnosLancamento(dataProcurada);
        List<Long> listaTmp = new ArrayList<>();

        // Comparar pelo artista tambem
        for(int i = 0; i < arrayPalavras.length; i++) {
            // Obter intersecao entre as listas
            listaTmp = lista.readArtistas(arrayPalavras[i]);
            enderecos.retainAll(listaTmp);
        }

        // Abrir "Registros.db" e obter musicas
        RandomAccessFile dbFile = null;

        try {
            dbFile = new RandomAccessFile (registroDB, "rw");

            // Se nao encontrar musicas, printar que nao foi encontrado
            if (enderecos.size() == 0) {
                System.out.println("\nNao foram encontradas musicas para (" + textoBak + ") na data (" + strDateBak + ")\n");

            // Se somente tiver uma musica, printar ela
            } else if (enderecos.size() == 1) {
                System.out.println("\nApenas uma musica foi encontrada:\n");

                // Posicionar ponteiro
                long posicao = enderecos.get(0).longValue();
                dbFile.seek(posicao);

                // Ler informacoes do registro
                boolean lapide = dbFile.readBoolean();
                int tamRegistro = dbFile.readInt();

                // Ler e criar novo Objeto musica
                Musica musica = new Musica();
                byte[] registro = new byte[tamRegistro];
                dbFile.read(registro);
                musica.fromByteArray(registro);
                
                // Mostrar musica
                System.out.println(musica);

            // Se tiver mais de uma, deixar como opcao escolher a desejada
            } else {
                System.out.println("\nForam encontradas " + enderecos.size() + " musicas:\n");
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
                    
                    // Mostrar nome musicas encontradas
                    System.out.println(String.format("% 4d - %s", i+1, musica.getNome()));
                }

                // Perguntar qual musica deseja obter as informacoes
                int opcao = 0;
                do {
                    try{
                        // Ler musica desejada
                        opcao = io.readInt("\nSelecione a musica desejada: ");

                        // Posicionar ponteiro
                        long posicao = enderecos.get(opcao-1).longValue();
                        dbFile.seek(posicao);

                        // Ler informacoes do registro
                        boolean lapide = dbFile.readBoolean();
                        int tamRegistro = dbFile.readInt();

                        // Ler e criar novo Objeto musica
                        Musica musica = new Musica();
                        byte[] registro = new byte[tamRegistro];
                        dbFile.read(registro);
                        musica.fromByteArray(registro);
                        
                        // Mostrar musica desejada
                        System.out.println(musica);
                    
                    // Caso le-se um valor nao numerico
                    } catch (InputMismatchException e) {

                        // Mostrar erro
                        System.out.println("\nERRO: Por favor, digite uma opcao valida de 1 a " + enderecos.size() + "."); 
                        io.readLine();
                        opcao = 0;
                    
                    // Caso o valor nao pertenca ao array
                    } catch (Exception e) {

                        // Mostrar erro
                        System.out.println("\nERRO: Por favor, digite uma opcao valida de 1 a " + enderecos.size() + "."); 
                        opcao = 0;
                    }
                } while (opcao == 0);
            }

            // Fechar arquivos
            dbFile.close();

        } catch (FileNotFoundException e) {
            System.out.println("\nERRO: Registro vazio!" +
                               "\n      Tente carregar os dados iniciais primeiro!\n");
        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler o arquivo \"" + registroDB + "\"\n");
        }
    }    

    /**
     * Metodo para excluir uma musica a partir do seu ID.
     * @return true, se a música foi excluida; false, caso contrario.
     */
    public boolean delete () {

        boolean delete = false;

        // Testar se arquivo existe
        File arquivoRegistro = new File(registroDB);
        
        // Se existir, fazer a busca
        if (arquivoRegistro.exists()) {

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

            // Deletar sequencialmente
            delete = delete(idProcurado);

            // Deletar no hashing extensivel
            //hash.delete(idProcurado);

            // Deletar na Arvore B
            System.out.println("\nEntrou pra deletar");
            arvoreB.delete(idProcurado);

            System.out.println("\ndeletou");
            arvoreB.mostrarArquivo();
            System.out.println("\nsucesso"); 

        // Senao, mensagem de erro
        } else {
            System.out.println("\nERRO: Registro vazio!" +
                               "\n      Tente carregar os dados iniciais primeiro!\n");
        }

       return delete;
    }

    /**
     * Metodo para atualizar uma musica a partir do seu ID.
     * @return true, se a música foi atualizada; false, caso contrario.
     */
    public boolean update () {
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
     * @param idProcurado - id para ser a chave de busca.
     * @return posArquivo - posicao do registro no arquivo.
     */
    private long read (int idProcurado) {
        RandomAccessFile dbFile = null;
        boolean find = false;
        long posArquivo = -1;

        try {
            dbFile = new RandomAccessFile (registroDB, "rw");

            if (dbFile.length() > 0) {

                Musica musica = null;
                boolean lapide;
                int tamRegistro;


                // Obter ultimo ID adicionado
                dbFile.seek(0);
                int ultimoId = dbFile.readInt();
                long posicaoAtual = dbFile.getFilePointer();

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
                            posArquivo = posicaoAtual;
                        }

                    // Se nao for, pular o registro e reposicionar ponteiro
                    } else {
                        posicaoAtual = dbFile.getFilePointer();
                        long proximaPosicao = posicaoAtual + (long)tamRegistro;
                        dbFile.seek(proximaPosicao);
                    }
                    posicaoAtual = dbFile.getFilePointer();
                }

            } else {
                System.out.println("\nERRO: Registro vazio!" +
                                   "\n      Tente carregar os dados iniciais primeiro!\n");
            }

            // Fechar arquivos
            dbFile.close();

        } catch (FileNotFoundException e) {
            System.out.println("\nERRO: Registro vazio!" +
                               "\n      Tente carregar os dados iniciais primeiro!\n");
        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler o arquivo \"" + dbFile + "\"\n");
        } finally {
            return posArquivo;
        }
    }

    /**
     * Metodo privado para excluir uma musica a partir do seu ID.
     * @return true, se a música foi excluida; false, caso contrario.
     */
    private boolean delete (int idProcurado) {
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

                            // Deletar na lista
                            //lista.delete(musica);

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
            } else {
                System.out.println("\nERRO: Registro vazio!" +
                                   "\n      Tente carregar os dados iniciais primeiro!\n");
            }

            // Fechar arquivos
            dbFile.close();

        } catch (FileNotFoundException e) {
            System.out.println("\nERRO: Registro vazio!" +
                               "\n      Tente carregar os dados iniciais primeiro!\n");

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler/escrever o arquivo \"" + dbFile + "\"\n");
        } finally {
            return find;
        }
    }

    /**
     * Metodo privado para atualizar uma musica a partir do seu ID.
     * @return true, se a música foi excluida; false, caso contrario.
     */
    private boolean update (int idProcurado) {
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
                            atualizado = newMusica.atualizar();
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

                               // Atualizar nas listas invertidas
                               lista.update(musica, newMusica, posicaoInicio);

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

                               // Atualizar na ArvoreB
                               arvoreB.update(idProcurado, finalArquivo);

                               // Atualizar nas listas invertidas
                               lista.update(musica, newMusica, finalArquivo);
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

            // Fechar arquivos
            dbFile.close();

        } catch (FileNotFoundException e) {
            System.out.println("\nERRO: Registro vazio!" +
                               "\n      Tente carregar os dados iniciais primeiro!\n");
        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler/escrever o arquivo \"" + dbFile + "\"\n");
        } finally {
            return find;
        }
    }

    /**
     * Metodo para exibir as informacoes de uma musica a partir do seu ID.
     * @return true, se a música foi encontrada; false, caso contrario.
    */
    public void abrirMusica() {
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
     */
    public void abrirMusica (int idProcurado) {

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

            // Fechar arquivos
            dbFile.close();

        } catch (FileNotFoundException e) {
            System.out.println("\nERRO: Registro vazio!" +
                               "\n      Tente carregar os dados iniciais primeiro!\n");
        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler o arquivo \"" + dbFile + "\"\n");
        } finally {
            if (find == true) {
                abrirMusica (musica.getUri());
            }
        }
    }

    /**
    * Metodo privado para abrir a musica no aplicativo do Spotify, apartir da sua URI.
    * @param uri link da musica.
    */
    private void abrirMusica (String uri) {
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
            try{
                Runtime.getRuntime().exec("cmd /c start " + url);
            } catch (Exception ex ) {
                System.out.println("\nERRO:" + ex.getMessage() + "ao abrir a musica");
            }
            
        } catch (Exception e) {
            System.out.print("\nERRO: " + e + " Link incorreto. Tente atualizar o URI!");
            System.out.print("\nModelo: \"spotify:track:(INSERIR TRACK ID)\"\n");
        }
    }

    /**
     * Metodo privado para salvar todo banco de dados em arquivo txt.
     */
    public void saveTXT () {
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
            // Fechar arquivos
            dbFile.close();
            dbFileTXT.close();

        } catch (FileNotFoundException e) {
            System.out.println("\nERRO: Registro vazio!" +
                               "\n      Tente carregar os dados iniciais primeiro!\n");
        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao escrever o arquivo \"" + dbFile + "\"\n");
        }
    }

    private Musica lerMusica(long posicaoProcurada) {
        RandomAccessFile dbFile = null;
        Musica musicaProcurada = new Musica();

        try {
            dbFile = new RandomAccessFile (registroDB, "rw");

            // Posicionar ponteiro
            dbFile.seek(posicaoProcurada);

            // Ler informacoes do registro  
            dbFile.readBoolean();
            int tamRegistro = dbFile.readInt();

            // Ler musica
            byte[] registro = new byte[tamRegistro];
            dbFile.read(registro);
            musicaProcurada.fromByteArray(registro);

            // Fechar arquivos
            dbFile.close();

        } catch (FileNotFoundException e) {
            System.out.println("\nERRO: Registro vazio!" +
                               "\n      Tente carregar os dados iniciais primeiro!\n");

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler/escrever o arquivo \"" + dbFile + "\"\n");
        } finally {
            return musicaProcurada;
        }
    }

}

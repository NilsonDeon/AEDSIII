// Package
package compressao;

// Bibliotecas
import java.io.File;
import java.util.InputMismatchException;

// Bibliotecas proprias
import app.IO;

public class Compressao {

    private static final String registroDB = "./src/resources/Registro.db";
    private static final String caminhoPastaCompressao = "./src/resources/compressao";

    // Classes para compressao
    private static IO io;
    private static Huffman huffman;
    private static LZW lzw;
    private static LZ78 lz78;

    // Constantes
    private static final int NUM_COMPRESSOES = 3;
    private static final int posLZW = 1;
    private static final int posLZ78 = 2;
    private static final int posHuffman = 3;

    // Variaveis para controle
    private boolean compressoes[];
    private String nomeArquivo;
    private int versaoAtual;

    /**
     * Construtor padrao da classe Compressao.
     */
    public Compressao() {
        io = new IO();
        huffman = new Huffman();
        lzw = new LZW();
        lz78 = new LZ78();

        compressoes = new boolean[NUM_COMPRESSOES+1];

        // Variaveis para definir se existe arquivo para cada tipo de compressao
        compressoes[posLZW] = (lzw.versaoAtual != 0);
        compressoes[posLZ78] = (lz78.versaoAtual != 0);
        compressoes[posHuffman] = (huffman.versaoAtual != 0);

        versaoAtual = 0;
        nomeArquivo = "";
    }

    /**
     * Metodo para comprimir um arquivo binario, utilizando Huffman, LZW ou LZ78.
     */
    public void comprimir() {

        // Testar se arquivo existe
        File arquivoRegistro = new File(registroDB);

        compressoes[posLZW] = (lzw.versaoAtual != 0 || arquivoRegistro.length() > 0);
        compressoes[posLZ78] = (lz78.versaoAtual != 0 || arquivoRegistro.length() > 0);
        compressoes[posHuffman] = (huffman.versaoAtual != 0 || arquivoRegistro.length() > 0);

        if (temArquivo()) {

            int opcao = 0;
            int numCompressoes = 0;

            String menu = "\n+------------------------------------------+" +
                         "\n|   Escolha o algoritmo para compressao:   |" +
                         "\n|------------------------------------------|" +
                         "\n| 1 - LZW                                  |" +
                         "\n| 2 - LZ78                                 |" +
                         "\n| 3 - Huffman                              |" +
                         "\n+------------------------------------------+";
            
            // Obter opcao desejada
            do {
                try {
                    System.out.println(menu);
                    opcao = io.readInt("\nDigite o algoritmo desejado: ");

                    if (opcaoInvalida(opcao)) {
                        System.out.println("\nERRO: Por favor, digite uma opcao valida de 1 a " + NUM_COMPRESSOES);
                    }

                } catch (InputMismatchException e) {
                    System.out.println("\nERRO: Por favor, digite uma opcao valida de 1 a " + NUM_COMPRESSOES);
                    io.readLine();
                }
            } while (opcaoInvalida(opcao));

            // Obter numero de compressoes desejadas
            mostrarArquivoAtual(opcao);
            do {
                try {
                    numCompressoes = io.readInt("\nDigite o numero de compressoes desejadas: ");

                    if (numCompressoes <= 0) {
                        System.out.println("\nERRO: Por favor, digite um valor positivo.");
                    }

                } catch (InputMismatchException e) {
                    System.out.println("\nERRO: Por favor, digite um numero positivo.");
                    io.readLine();
                }
            } while (numCompressoes <= 0);

            // Executar compressao
            switch (opcao){
                case posLZW    : nomeArquivo = lzw.comprimir(numCompressoes);     break;
                case posLZ78   : nomeArquivo = lz78.comprimir(numCompressoes);    break;
                case posHuffman: nomeArquivo = huffman.comprimir(numCompressoes); break;
            }

            // Mensagem de sucesso
            System.out.println("\nArquivo comprimido com sucesso: \"" + nomeArquivo + "\"");
        
        // Senao, mensagem de erro
        } else {
            arquivoRegistro.delete();
            System.out.println("\nERRO: Registro vazio!" +
                                "\n      Tente carregar os dados iniciais primeiro!\n");

        }

    }

    /**
     * Metodo para descomprimir um arquivo binario, utilizando Huffman, LZW ou
     * LZ78.
     */
    public void descomprimir() {

        compressoes[posLZW] = (lzw.versaoAtual != 0);
        compressoes[posLZ78] = (lz78.versaoAtual != 0);
        compressoes[posHuffman] = (huffman.versaoAtual != 0);

        // Somente se existir arquivo para descompressao
        if (temArquivo()) {

            int opcao = 0;
            int numDescompressoes = 0;
            
            // Obter opcao desejada
            mostrarMenuDescompressao();
            do {
                try {
                    opcao = io.readInt("\nDigite o arquivo desejado: ");
    
                    if (! opcaoExiste(opcao)) {
                        System.out.println("\nERRO: Por favor, digite uma opcao valida.");
                    }
    
                } catch (InputMismatchException e) {
                    System.out.println("\nERRO: Por favor, digite uma opcao valida.");
                    io.readLine();
                }
            } while (! opcaoExiste(opcao));

            // Definir versao atual
            switch (opcao) {
                case posLZW    : versaoAtual = lzw.versaoAtual;     break;
                case posLZ78   : versaoAtual = lz78.versaoAtual;    break;
                case posHuffman: versaoAtual = huffman.versaoAtual; break;
            }


            // Obter numero de descompressoes desejadas se necessario
            if(versaoAtual != 1) {
                boolean isNumValido = false;
                mostrarArquivoAtual(opcao);
                do {
                    try {
                        numDescompressoes = io.readInt("\nDigite o numero de descompressoes desejadas: ");

                        if (isInvalido(numDescompressoes)) {
                            System.out.println("\nERRO: Por favor, digite um valor menor que a compressao atual [1," + versaoAtual + "].");
                        } else {
                            isNumValido = true;
                        }

                    } catch (InputMismatchException e) {
                        System.out.println("\nERRO: Por favor, digite um valor menor que a compressao atual [1," + versaoAtual + "].");
                        io.readLine();
                    }
                } while (!isNumValido);
            
            // Se somente tiver uma versao
            } else {
                numDescompressoes = 1;
            }

            // Executar descompressao
            switch (opcao){
                case posLZW    : nomeArquivo = lzw.descomprimir(numDescompressoes);     break;
                case posLZ78   : nomeArquivo = lz78.descomprimir(numDescompressoes);    break;
                case posHuffman: nomeArquivo = huffman.descomprimir(numDescompressoes); break;
            }

            // Mensagem de sucesso
            System.out.println("\n\nArquivo descomprimido com sucesso: \"" + nomeArquivo + "\"");

        // Senao, mensagem de erro
        } else {
            System.out.println("\nERRO: Tente carregar e comprimir o registro primeiro!\n");

        }

    }

    /**
     * Metodo para reinicializar as informacoes sobre as compressoes atuais e
     * apagar os arquivos antigos.
     */
    public void reinicializar() {

        // Apagar pasta se existir
        File pastaCompressao = new File(caminhoPastaCompressao);
        deletePasta(pastaCompressao);

        // Atualizar atributos da classe
        huffman = new Huffman();
        lzw = new LZW();
        lz78 = new LZ78();

        compressoes[posLZW] = (lzw.versaoAtual != 0);
        compressoes[posLZ78] = (lz78.versaoAtual != 0);
        compressoes[posHuffman] = (huffman.versaoAtual != 0);

        versaoAtual = 0;
        nomeArquivo = "";    
    }

    /**
     * Metodo para apagar uma pasta e todo seu conteudo.
     * @param pasta - que se deseja deletar.
     */
    private void deletePasta (File pasta) {

        // Testar se e' pasta com arquivo
        if (pasta.exists() && pasta.isDirectory()) {
            File[] arquivos = pasta.listFiles();

            // Percorrer arquivo por arquivo e apagar
            for (File arquivo : arquivos) {

                // Se for pasta novamente, chamar recursividade
                if (arquivo.isDirectory()) {
                    deletePasta(arquivo);
                } else {
                    arquivo.delete();
                }
            }

            // Apagar, de fato, a pasta desejada
            pasta.delete();
        }
    }

    /**
     * Metodo para mostrar o menu de descompressao, de acordo com os arquivos
     * comprimidos existentes.
     */
    private void mostrarMenuDescompressao() {

        String menu = null;

        // Verificar arquivos para descomprimir
        compressoes[posLZW] = (lzw.versaoAtual != 0);
        compressoes[posLZ78] = (lz78.versaoAtual != 0);
        compressoes[posHuffman] = (huffman.versaoAtual != 0);
        
        menu = "\nArquivos para descomprimir:";

        if (compressoes[posLZW]) menu += "\n1 - \"" + lzw.nomeArquivo + "\"";
        if (compressoes[posLZ78]) menu += "\n2 - \"" + lz78.nomeArquivo + "\"";
        if (compressoes[posHuffman]) menu += "\n3 - \"" + huffman.nomeArquivo + "\"";
                   
        System.out.println(menu);
    }

    /**
     * Metodo para a versao de compressao do arquivo atual
     * @param opcao - 1: huffman; 2: LZW.
     */
    private void mostrarArquivoAtual(int opcao) {

        // Printar versao atual
        switch (opcao) {
            case posLZW    : System.out.println("\nVersao atual: " + lzw.versaoAtual);     break;
            case posLZ78   : System.out.println("\nVersao atual: " + lz78.versaoAtual);    break;
            case posHuffman: System.out.println("\nVersao atual: " + huffman.versaoAtual); break;
        }
    }

    /**
     * Metodo para verificar se o valor para descompressao e' invalido.
     * @param numDescompressoes - numero de descompressoes que se deseja
     * realizar.
     * @return true, se for invalido; false, caso contrario.
     */
    private boolean isInvalido(int numDescompressoes) {
        return (numDescompressoes <= 0 || numDescompressoes > versaoAtual);
    }

    /**
     * Metodo para determinar se existe arquivo para comprimir ou descomprimir.
     * @return true, se existir arquivo; false, caso contrario.
     */
    private boolean temArquivo () {
        return compressoes[posLZW] || compressoes[posLZ78] || compressoes[posHuffman];
    }
    
    /**
     * Metodo para verificar se a opcao para descomprimir existe.
     * @param opcao - que se deseja descomprimir.
     * @return true, se existir; false, caso contrario.
     */
    private boolean opcaoExiste (int opcao) {

        boolean resp = false;

        switch (opcao) {
            case posLZW    : resp = (lzw.versaoAtual != 0);     break;
            case posLZ78   : resp = (lz78.versaoAtual != 0);    break;
            case posHuffman: resp = (huffman.versaoAtual != 0); break;
        }

        return resp;
    }

    /**
     * Metodo para determinar se a opcao selecionada corresponde a alguma
     * possibilidade de algoritmo de compressao.
     * @param opcao - desejada.
     * @return true, se corresponder; false, caso contrario.
     */
    private boolean opcaoInvalida (int opcao) {

        return (opcao < 1 || opcao > NUM_COMPRESSOES);
    }
}

// Package
package compressao;

import java.io.File;
// Bibliotecas
import java.util.InputMismatchException;

// Bibliotecas proprias
import app.IO;

public class Compressao {

    private static final String registroDB = "./src/resources/Registro.db";
    private static IO io;
    //private static Huffman huffman;
    private static LZW lzw;

    // Variaveis para controle dos arquivos de descompressao
    private boolean hasHuffman, hasLZW;
    private int versaoAtual;

    /**
     * Construtor padrao da classe Compressao.
     */
    public Compressao() {
        io = new IO();
        //huffman = new Huffman();
        lzw = new LZW();

        //hasHuffman = (huffman.versaoAtual != 0);
        hasHuffman = false;
        hasLZW = (lzw.versaoAtual != 0);
        this.versaoAtual = 0;
    }

    /**
     * Metodo para comprimir um arquivo binario, utilizando Huffman ou LZW.
     */
    public void comprimir() {

        // Testar se arquivo existe
        File arquivoRegistro = new File(registroDB);

        //hasHuffman = (huffman.versaoAtual != 0 || arquivoRegistro.length() > 0);
        hasHuffman = false;
        hasLZW = (lzw.versaoAtual != 0 || arquivoRegistro.length() > 0);

        if (hasHuffman || hasLZW) {

            int opcao = 0;
            int numCompressoes = 0;

            String menu = "\n+------------------------------------------+" +
                        "\n|   Escolha o algoritmo para compressao:   |" +
                        "\n|------------------------------------------|" +
                        "\n| 1 - Huffman                              |" +
                        "\n| 2 - LZW                                  |" +
                        "\n+------------------------------------------+";
            
            // Obter opcao desejada
            do {
                try {
                    System.out.println(menu);
                    opcao = io.readInt("\nDigite o algoritmo desejado: ");

                    if (opcao < 1 || opcao > 2) {
                        System.out.println("\nERRO: Por favor, digite uma opcao valida de 1 a 2.");
                    }

                } catch (InputMismatchException e) {
                    System.out.println("\nERRO: Por favor, digite uma opcao valida de 1 a 2.");
                    io.readLine();
                }
            } while (opcao < 1 || opcao > 2);

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
            String nomeArquivo = "";
            switch (opcao){
            //case 1: nomeArquivo = huffman.comprimir(numCompressoes); break;
                case 2: nomeArquivo = lzw.comprimir(numCompressoes);     break;
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
     * Metodo para descomprimir um arquivo binario, utilizando Huffman ou LZW.
     */
    public void descomprimir() {

        //hasHuffman = (huffman.versaoAtual != 0);
        hasHuffman = false;
        hasLZW = (lzw.versaoAtual != 0);

        if (hasHuffman || hasLZW) {

            int opcao = 0;
            int numDescompressoes = 0;

            boolean hasOpcao = mostrarMenuDescompressao();
            
            // Obter opcao desejada se necessario
            if (hasOpcao) {
                do {
                    try {
                        opcao = io.readInt("\nDigite o arquivo desejado: ");
        
                        if (opcao < 1 || opcao > 2) {
                            System.out.println("\nERRO: Por favor, digite uma opcao valida de 1 a 2.");
                        }
        
                    } catch (InputMismatchException e) {
                        System.out.println("\nERRO: Por favor, digite uma opcao valida de 1 a 2.");
                        io.readLine();
                    }
                } while (opcao < 1 || opcao > 2);
            
            // Se nao tiver, settar opcao automaticamente
            } else {
                
                // Huffman = 1 e LZW = 2
                if(hasHuffman) {
                    opcao = 1;
                } else {
                    opcao = 2;
                }
            }
            // Obter numero de descompressoes desejadas se necessario
            if(versaoAtual != 1) {
                boolean isNumValido = false;
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
            String nomeArquivo = "";
            switch (opcao){
            //case 1: nomeArquivo = huffman.descomprimir(numDescompressoes); break;
                case 2: nomeArquivo = lzw.descomprimir(numDescompressoes);     break;
            }

            // Mensagem de sucesso
            System.out.println("\nArquivo descomprimido com sucesso: \"" + nomeArquivo + "\"");

        // Senao, mensagem de erro
        } else {
            System.out.println("\nERRO: Registro vazio!" +
                                "\n      Tente carregar os dados iniciais primeiro!\n");

        }

    }

    /**
     * Metodo para mostrar o menu de descompressao, de acordo com os arquivos
     * comprimidos existentes.
     * @return true, se ja existir comprimido LZW e huffman; 
     *         false, caso contrario.
     */
    private boolean mostrarMenuDescompressao() {

        String menu = null;

        // Verificar arquivos para descomprimir
        //hasHuffman = (huffman.versaoAtual != 0);
        hasHuffman = false;
        hasLZW = (lzw.versaoAtual != 0);

        // Se apenas existir LZW
        if(hasLZW && !hasHuffman) {
            menu = "\nArquivo para descomprimir: \"" + lzw.nomeArquivo + "\"";
            System.out.println(menu);
            versaoAtual = lzw.versaoAtual;

        }

        return (hasLZW && hasHuffman);
    }

    /**
     * Metodo para a versao de compressao do arquivo atual
     * @param opcao - 1: huffman; 2: LZW.
     */
    private void mostrarArquivoAtual(int opcao) {

        // Se for Huffman
        if (opcao == 1) {
            //System.out.println("\nVersao atual: " + huffman.versaoAtual);
        
            // Se for LZW
        } else {
            System.out.println("\nVersao atual: " + lzw.versaoAtual);
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
}
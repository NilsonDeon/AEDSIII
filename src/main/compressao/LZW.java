// Package
package compressao;

// Bibliotecas
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.ArrayList;

import app.IO;

public class LZW {

    private static final String caminhoPasta = "./src/resources/compressao";
    private static final String registroDB = "./src/resources/Registro.db";

    private ArrayList<byte[]> dicionario;
    protected int versaoAtual;
    protected String nomeArquivo;

    IO io = new IO();

    /**
     * Construtor padrao da classe LZW.
     */
    public LZW() {

        // Inicializar o dicionario
        inicializarDicionario();

        // Atulizar versao e nome arquivo atuais
        updateVersaoAtual();
    }

    /**
     * Metodo para comprimir um arquivo binario, utilizando o algoritmo LZW.
     * @param numCompressoes - numero de compressoes a serem realizadas.
     * @return nomeArquivo gerado.
     */
    public String comprimir(int numCompressoes) {

        String nomeArquivo = "";
        while (numCompressoes > 0) {
            nomeArquivo = comprimir();
            numCompressoes--;
        }

        return nomeArquivo;
    }

    /**
     * Metodo para descomprimir um arquivo binario, utilizando o algoritmo LZW.
     * @param numCompressoes - numero de descompressoes a serem realizadas.
     * @return nomeArquivo gerado.
     */
    public String descomprimir(int numDescompressoes) {
        
        String nomeArquivo = "";
        while (numDescompressoes > 0) {
            nomeArquivo = descomprimir();
            numDescompressoes--;
        }

        return nomeArquivo;
    }

    /**
     * Metodo privado para comprimir um arquivo binario, utilizando o algoritmo
     * LZW.
     * @return nomeArquivo gerado.
     */
    private String comprimir() {
        
        RandomAccessFile arqAntigo = null;
        RandomAccessFile arqNovo = null;

        // Atualizar nome arquivo
        String nomeArquivoAntigo = nomeArquivo;

        try {

            // Inicializar o dicionario
            inicializarDicionario();

            // Abrir a pasta se nao existir
            File pasta = new File(caminhoPasta);
            if (!pasta.exists()) {
                pasta.mkdirs();
            }

            // Abrir arquivo antigo
            arqAntigo = new RandomAccessFile (nomeArquivoAntigo, "rw");
            arqAntigo.seek(0);

            // Somente comprimir se houver arquivo
            if (arqAntigo.length() > 0) {

                // Abrir arquivo novo
                versaoAtual++;
                nomeArquivo = caminhoPasta + "/RegistroLZWCompressao" + versaoAtual + ".db";
                arqNovo = new RandomAccessFile (nomeArquivo, "rw");
                arqNovo.seek(0);

                // Variaveis para leitura
                long posAtual = arqAntigo.getFilePointer();
                ArrayList<Byte> bytesLido;

                // Obter tamanho do arquivo original
                File arquivoOriginal = new File(nomeArquivoAntigo);
                long tamArquivoOriginal = arquivoOriginal.length();

                // Mostrar barra de progresso
                System.out.println("\nComprimindo arquivo: ");

                // Comprimir até o fim do arquivo
                while (posAtual != arqAntigo.length()) {

                    // Mostrar barra progresso
                    io.gerarBarraProgresso(tamArquivoOriginal, (int)posAtual);

                    // Resetar bytes lidos
                    bytesLido = new ArrayList<>();

                    // Ler e adicionar byte
                    bytesLido.add(arqAntigo.readByte());
                
                    // Obter posicao do byte[] no dicionario
                    int posDicionario = getPosicaoDicionario(bytesLido);

                    // Comparar se array esta' presente no dicionario, ate' encontrar um que nao esteja
                    while (posDicionario != -1) {

                        byte proximoByte;

                        // Obter posicao atual
                        posAtual = arqAntigo.getFilePointer();

                        // Ler somente se nao for fim de arquivo
                        if (posAtual != arqAntigo.length()) {

                            // Obter proximo byte
                            proximoByte = arqAntigo.readByte();

                            // Adicionar mais um byte
                            bytesLido.add(proximoByte);

                            // Testar se, ao ler o proximo byte, o array ainda existe no dicionario
                            int newPosDicionario = getPosicaoDicionario(bytesLido);

                            // Se existir, continuar busca
                            if (newPosDicionario != -1) {
                            
                                // Atualizar ponteiro
                                posAtual = arqAntigo.getFilePointer();
                            
                            // Senao, inserir no arquivo novo o byte codificado
                            } else {
                            
                                // Inserir no novo arquivo a posicao no dicionario como inteiro
                                byte[] posDicionarioBytes = ByteBuffer.allocate(4).putInt(posDicionario).array();
                                arqNovo.write(posDicionarioBytes);

                                // Atualizar dicionario
                                byte[] byteArray = toByteArray(bytesLido);
                                dicionario.add(byteArray);

                                // Voltar ponteiro
                                arqAntigo.seek(posAtual);
                            }

                            // Atualizar posicao
                            posDicionario = newPosDicionario;
                        
                        // Se foi fim de arquivo, salvar o valor ja encontrado
                        } else {

                            // Inserir no novo arquivo a posicao no dicionario como inteiro
                            byte[] posDicionarioBytes = ByteBuffer.allocate(4).putInt(posDicionario).array();
                            arqNovo.write(posDicionarioBytes);
                            posDicionario = -1;

                            // Atualizar dicionario
                            byte[] byteArray = toByteArray(bytesLido);
                            dicionario.add(byteArray);
                        }
                    }
                }

                // Mostrar barra progresso completa
                io.gerarBarraProgresso(tamArquivoOriginal, (int)posAtual);

                // Fechar arquivo novo
                arqNovo.close();

                // Obter tamanho do arquivo novo
                File arquivoComprimido = new File(nomeArquivo);
                long tamArquivoComprimido = arquivoComprimido.length();

                // Mostrar compressão
                double compressao = (double)(tamArquivoOriginal - tamArquivoComprimido) / tamArquivoOriginal * 100;
                System.out.println(String.format("\nTaxa compressao: %.2f%%", compressao));

            // Solicitar que carregue dados iniciais
            } else {
                System.out.println("\nERRO: Registro vazio!" +
                                   "\n      Tente carregar os dados iniciais primeiro!\n");
            }

            // Fechar arquivo antigo
            arqAntigo.close();

            // Apagar arquivo antigo se nao for a base de dados
            if(! nomeArquivoAntigo.equals(registroDB)) {
                File arquivo = new File(nomeArquivoAntigo);
                arquivo.delete();
            }
            
        } catch (FileNotFoundException e) {
            System.out.println("\nERRO: Registro vazio!" +
                               "\n      Tente carregar os dados iniciais primeiro!\n");
        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler o arquivo \"" + nomeArquivoAntigo + "\"\n");
            System.out.println("\nERRO: " + e.getMessage() + " ao escrever o arquivo \"" + nomeArquivo + "\"\n");
        }
        
        return nomeArquivo;
    }

    /**
     * Metodo privado para descomprimir um arquivo binario, utilizando o algoritmo
     * LZW.
     * @return nomeArquivo gerado.
     */
    private String descomprimir() {
        
        RandomAccessFile arqAntigo = null;
        RandomAccessFile arqNovo = null;

        // Atualizar nome arquivo
        String nomeArquivoAntigo = nomeArquivo;

        try {

            // Inicializar o dicionario
            inicializarDicionario();

            // Abrir arquivo antigo
            arqAntigo = new RandomAccessFile (nomeArquivoAntigo, "rw");
            arqAntigo.seek(0);

            // Somente descomprimir se houver arquivo
            if (arqAntigo.length() > 0) {

                // Atualizar versao
                versaoAtual--;

                // Se versao atual for a zero, significa que esta' voltando ao original
                if(versaoAtual == 0) {
                    nomeArquivo = registroDB;

                    // Apagar Registro antigo
                    File arquivo = new File(nomeArquivo);
                    arquivo.delete();
                
                // Se for mais que zero, ainda esta comprimido
                } else {
                    nomeArquivo = caminhoPasta + "/RegistroLZWCompressao" + versaoAtual + ".db";
                }

                // Abrir arquivo
                arqNovo = new RandomAccessFile (nomeArquivo, "rw");
                arqNovo.seek(0);

                // Ler primeira posicao
                int posDicionario = arqAntigo.readInt();

                // Obter array na posicao lida
                byte[] bytesLido = dicionario.get(posDicionario);
                ArrayList<Byte> bytesAntigo = toByteArrayList(dicionario.get(posDicionario));

                // Adicionar no dicionario
                dicionario.add(dicionario.get(posDicionario));
                int ultimaPosicao = dicionario.size() - 1;

                // Escrever no arquivo
                arqNovo.write(bytesLido);

                // Atualizar ponteiro
                long posAtual = arqAntigo.getFilePointer();

                // Obter tamanho do arquivo original
                File arquivoOriginal = new File(nomeArquivoAntigo);
                long tamArquivoOriginal = arquivoOriginal.length();

                // Descomprimir ate' acabar arquivo
                while (posAtual != arqAntigo.length()) {

                    // Mostrar barra progresso
                    io.gerarBarraProgresso(tamArquivoOriginal, (int)posAtual);

                    // Ler proxima posicao
                    posDicionario = arqAntigo.readInt();

                    // Obter proxima posicao do array
                    bytesLido = dicionario.get(posDicionario);

                    // Atualizar ultima posicao dicionario
                    boolean stop = false;
                    for (int i = 0; i < bytesLido.length && stop == false; i++) {

                        // Testar se posicao ja existe no dicionario
                        int posTeste = getPosicaoDicionario(bytesAntigo);
                        if(posTeste != -1) {
                            bytesAntigo.add(bytesLido[i]);
                        
                        // Se posicao ainda nao existir, parar de adicionar
                        } else {
                            stop = true;
                        }
                        
                    }
                    dicionario.remove(ultimaPosicao);
                    dicionario.add(toByteArray(bytesAntigo));

                    // Atualizar posicao do array
                    bytesLido = dicionario.get(posDicionario);

                    // Escrever no arquivo
                    arqNovo.write(bytesLido);

                    // Atualizar array antigo
                    bytesAntigo = toByteArrayList(bytesLido);

                    // Atualizar posicao nova dicionario
                    dicionario.add(bytesLido);
                    ultimaPosicao++;

                    // Atualizar ponteiro
                    posAtual = arqAntigo.getFilePointer();
                }

                // Mostrar barra progresso completa
                io.gerarBarraProgresso(tamArquivoOriginal, (int)posAtual);

                // Fechar arquivo novo
                arqNovo.close();
            }

            // Fechar arquivo antigo
            arqAntigo.close();

            // Apagar arquivo antigo
            File arquivo = new File(nomeArquivoAntigo);
            arquivo.delete();

            // Se voltou ao original e so tinha esse arquivo, apagar pasta tambem
            if (versaoAtual == 0) {

                // Abrir pasta de compressao
                File pasta = new File(caminhoPasta);
                
                // Verificar se e' uma pasta existente
                if (pasta.exists() && pasta.isDirectory()) {

                    // Verificar se a pasta esta' vazia
                    File[] arquivos = pasta.listFiles();
                    if (arquivos == null || arquivos.length == 0) {

                        // Apagar pasta
                        pasta.delete();
                    }
                }
            }   

        } catch (FileNotFoundException e) {
            System.out.println("\nERRO: Registro vazio!" +
                               "\n      Tente carregar os dados iniciais primeiro!\n");
        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler o arquivo \"" + nomeArquivoAntigo + "\"\n");
            System.out.println("\nERRO: " + e.getMessage() + " ao escrever o arquivo \"" + nomeArquivo + "\"\n");
        }     

        return nomeArquivo;

    }

    /**
     * Metodo para obter a posicao de um array de bytes no dicionario.
     * @param bytesLido - Array de bytes para se procurar no dicionario.
     * @return posicao no dicionario, se existir; -1, se nao existir.
     */
    private int getPosicaoDicionario(ArrayList<Byte> bytesLido) {
        int posicao = -1;
    
        // Converter ArrayList<Byte> para byte[]
        byte[] bytesLidoArray = toByteArray(bytesLido);
    
        // Percorrer dicionario para testar se existe
        for (int i = 0; i < dicionario.size() && posicao == -1; i++) {
    
            // Se existir marcar como encontrado
            if (Arrays.equals(dicionario.get(i), bytesLidoArray)) {
                posicao = i;
            }
        }
    
        return posicao;
    }

    /**
     * Metodo para converter um ArrayList em um array de bytes.
     * @param listBytes - ArrayList a ser convertido.
     * @return array de bytes.
     */
    private byte[] toByteArray (ArrayList<Byte> listBytes) {

        // Converter ArrayList<Byte> para byte[]
        byte[] bytesLidoArray = new byte[listBytes.size()];
        for (int i = 0; i < listBytes.size(); i++) {
            bytesLidoArray[i] = listBytes.get(i);
        }

        return bytesLidoArray;
    }

    /**
     * Metodo para converter um array de bytes em um ArrayList
     * @param listBytes - byte[] a ser convertido.
     * @return arrayList de bytes.
     */
    private ArrayList<Byte> toByteArrayList (byte[] listBytes) {

        // Converter byte[] para ArrayList<Byte>
        ArrayList<Byte> arrayBytes = new ArrayList<>(); 
        for (byte b : listBytes) {
            arrayBytes.add(b);
        }

        return arrayBytes;
    }

    /**
     * Metodo para inicializar dicionario com os valores de 0 a 255.
     */
    private void inicializarDicionario() {

        dicionario = new ArrayList<>();
        // Preencher dicionario com byte de 0 a 255 bits
        for(int i = 0; i <= 255; i++) {
            byte[] item = new byte[1];
            item[0] = (byte) i;
            dicionario.add(item);
        }
    } 

    /**
     * Metodo para atualizar a versao e o nome do arquivo atual.
     */
    private void updateVersaoAtual () {

        // Abrir pasta de compressao
        File pasta = new File(caminhoPasta);
        
        // Se pasta existir, procurar versao existente
        if (pasta.exists() && pasta.isDirectory()) {
            
            boolean find = false;

            // Percorrer todas as possibilidades de compressao para encontrar
            for(int i = 1; i <= Integer.MAX_VALUE && find == false; i++) {

                // Obter nome arquivo atual
                versaoAtual = i;
                nomeArquivo = caminhoPasta + "/RegistroLZWCompressao" + versaoAtual + ".db";
                File arquivo = new File(nomeArquivo);

                // Verificar se existe
                if (arquivo.exists()) {
                    find = true;
                }
            }

            // Se ocorrer o erro de chegar ao fim do loop e nao encontrar, resetar arquivo
            if (find == false) {

                // Apagar conteudo pasta
                File[] arquivos = pasta.listFiles();
                for (File arquivo : arquivos) {
                   arquivo.delete();
                }

                // Redefinir versao para comprimir
                versaoAtual = 0;
                nomeArquivo = registroDB;
            }

        // Se pasta nao existir, o arquivo ainda nao foi comprimido
        } else {

            // Nao definir nova versao comprimida ainda
            versaoAtual = 0;
            nomeArquivo = registroDB;
        }
    }
}
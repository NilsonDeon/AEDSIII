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

public class LZW {

    private static final String caminhoPasta = "./src/resources/compressao";

    private ArrayList<byte[]> dicionario;
    private int versaoAtual;
    private String nomeArquivo;

    /**
     * Construtor padrao da classe LZW.
     */
    public LZW() {

        // Inicializar o dicionario
        dicionario = new ArrayList<>();

        // Preencher dicionario com byte de 0 a 255 bits
        for(int i = 0; i <= 255; i++) {
            byte[] item = new byte[1];
            item[0] = (byte) i;
            dicionario.add(item);
        }

        // Atulizar versao e nome arquivo atuais
        updateVersaoAtual();
    }

    /**
     * Metodo para comprimir um arquivo binario, utilizando o algoritmo LZW.
     * @param numCompressoes - numero de compressoes a serem realizadas.
     */
    public void comprimir(int numCompressoes) {
        
        while (numCompressoes > 0) {
            comprimir();
            numCompressoes--;
        }
    }

    /**
     * Metodo privado para comprimir um arquivo binario, utilizando o algoritmo
     * LZW.
     */
    private void comprimir() {
        
        RandomAccessFile arqAntigo = null;
        RandomAccessFile arqNovo = null;

        String nomeArquivoAntigo = nomeArquivo;

        try {
            // Abrir arquivo antigo
            arqAntigo = new RandomAccessFile (nomeArquivoAntigo, "rw");
            arqAntigo.seek(0);

            // Abrir arquivo novo
            versaoAtual++;
            nomeArquivo = caminhoPasta + "/RegistroLZWCompressao" + versaoAtual + "db";
            arqNovo = new RandomAccessFile (nomeArquivo, "rw");
            arqNovo.seek(0);

            // Variaveis para leitura
            long posAtual = arqAntigo.getFilePointer();
            ArrayList<Byte> bytesLido;

            // Comprimir até o fim do arquivo
            while (posAtual != arqAntigo.length()) {

                // Resetar bytes lidos
                bytesLido = new ArrayList<>();

                // Ler e adicionar byte
                bytesLido.add(arqAntigo.readByte());
               
                // Obter posicao do byte[] no dicionario
                int posDicionario = getPosicaoDIcionario(bytesLido);

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
                        int newPosDicionario = getPosicaoDIcionario(bytesLido);

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

                        // Atualizar dicionario
                        byte[] byteArray = toByteArray(bytesLido);
                        dicionario.add(byteArray);

                    }                   

                }
            }

            // Fechar arquivos
            arqAntigo.close();
            arqNovo.close();

            // Apagar arquivo antigo
            File arquivo = new File(nomeArquivoAntigo);
            arquivo.delete();

        } catch (FileNotFoundException e1) {
            System.out.println("\nERRO: " + e1.getMessage() + " ao ler o arquivo \"" + nomeArquivoAntigo + "\"\n");
            System.out.println("\nERRO: " + e1.getMessage() + " ao escrever o arquivo \"" + nomeArquivo + "\"\n");
        } catch (IOException e2) {
            System.out.println("\nERRO: " + e2.getMessage() + " ao ler o arquivo \"" + nomeArquivoAntigo + "\"\n");
            System.out.println("\nERRO: " + e2.getMessage() + " ao escrever o arquivo \"" + nomeArquivo + "\"\n");
        }        
    }
    
    private int getPosicaoDIcionario(ArrayList<Byte> bytesLido) {
        int posicao = -1;
    
        // Converter ArrayList<Byte> para byte[]
        byte[] bytesLidoArray = toByteArray (bytesLido);
    
        // Percorrer dicionario para testar se existe
        for (int i = 0; i < dicionario.size() && posicao != -1; i++) {
    
            // Se existir marcar como encontrado
            if (Arrays.equals(dicionario.get(i), bytesLidoArray)) {
                posicao = i;
            }
        }
    
        return posicao;
    }
    
    private byte[] toByteArray (ArrayList<Byte> listBytes) {

        // Converter ArrayList<Byte> para byte[]
        byte[] bytesLidoArray = new byte[listBytes.size()];
        for (int i = 0; i < listBytes.size(); i++) {
            bytesLidoArray[i] = listBytes.get(i);
        }

        return bytesLidoArray;
    }

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
                nomeArquivo = caminhoPasta + "/RegistroLZWCompressao" + versaoAtual + "db";
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
                nomeArquivo = "./src/resources/Registro.db";
            }

        // Se pasta nao existir, o arquivo ainda nao foi comprimido
        } else {

            // Abrir a pasta
            if (!pasta.exists()) {
                pasta.mkdirs();
            }

            // Nao definir nova versao comprimida ainda
            versaoAtual = 0;
            nomeArquivo = "./src/resources/Registro.db";
        }
    }
}
// Package
package compressao;

// Bibliotecas
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;

// Bibliotecas proprias
import app.IO;

public class LZ78 {

    // Caminhos para arquivos
    private static final String caminhoPasta = "./src/resources/compressao";
    private static final String registroDB = "./src/resources/Registro.db";

    private Dicionario dicionario;
    protected int versaoAtual;
    protected String nomeArquivo;

    IO io = new IO();

    /**
     * Construtor padrao da classe LZ78.
     */
    public LZ78() {

        // Atulizar versao e nome arquivo atuais
        updateVersaoAtual();
    }

    /**
     * Metodo para comprimir um arquivo binario, utilizando o algoritmo LZ78.
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
     * Metodo para descomprimir um arquivo binario, utilizando o algoritmo LZ78.
     * @param numDescompressoes - numero de descompressoes a serem realizadas.
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
     * LZ78.
     * @return nomeArquivo gerado.
     */
    private String comprimir() {
        
        RandomAccessFile arqAntigo = null;
        RandomAccessFile arqNovo = null;

        // Atualizar nome arquivo
        String nomeArquivoAntigo = nomeArquivo;

        try {

            // Reiniciar dicionario
            dicionario = new Dicionario();

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
                nomeArquivo = caminhoPasta + "/RegistroLZ78Compressao" + versaoAtual + ".db";
                arqNovo = new RandomAccessFile (nomeArquivo, "rw");
                arqNovo.seek(0);

                // Variaveis para leitura
                long posAtual = arqAntigo.getFilePointer();
                ArrayList<Byte> bytesLido;

                // Obter tamanho do arquivo original
                File arquivoOriginal = new File(nomeArquivoAntigo);
                long tamArquivoOriginal = arquivoOriginal.length();

                // Obter tamanho do arquivo "Registro.db"
                File arquivoRegistroDB = new File(registroDB);
                long tamArquivoRegistroDB = arquivoRegistroDB.length();

                // Mostrar barra de progresso
                System.out.println("\nComprimindo arquivo: ");

                // Comprimir até o fim do arquivo
                boolean primeiraOcorrencia = true;
                while (posAtual != arqAntigo.length()) {

                    // Mostrar barra progresso
                    io.gerarBarraProgresso(tamArquivoOriginal, (int)posAtual);

                    // Resetar bytes lidos
                    bytesLido = new ArrayList<>();

                    // Ler e adicionar byte
                    bytesLido.add(arqAntigo.readByte());
                
                    // Obter posicao do byte[] no dicionario
                    Integer posDicionario = dicionario.get(toByteArray(bytesLido));

                    // Comparar se array esta' presente no dicionario, ate' encontrar um que nao esteja
                    primeiraOcorrencia = true;
                    while (posDicionario != null) {

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
                            Integer newPosDicionario = dicionario.get(toByteArray(bytesLido));
                            
                            // Se nao existir, inserir no arquivo novo o byte codificado
                            if (newPosDicionario == null) {
                            
                                // Inserir no novo arquivo a posicao no dicionario como inteiro
                                byte[] posDicionarioBytes = ByteBuffer.allocate(4).putInt(posDicionario).array();
                                arqNovo.write(posDicionarioBytes);

                                // Inserir byte novo
                                primeiraOcorrencia = false;
                                arqNovo.write(proximoByte);

                                // Atualizar dicionario
                                byte[] byteArray = toByteArray(bytesLido);
                                dicionario.put(byteArray);
                            }

                            // Atualizar posicao
                            posDicionario = newPosDicionario;
                        
                        // Se foi fim de arquivo, salvar o valor ja encontrado
                        } else {

                            // Inserir no novo arquivo soemente a posicao no dicionario como inteiro
                            byte[] posDicionarioBytes = ByteBuffer.allocate(4).putInt(posDicionario).array();
                            arqNovo.write(posDicionarioBytes);
                            
                            posDicionario = null;
                            primeiraOcorrencia = false;
                        }
                    }

                    if (primeiraOcorrencia) {
                        // Atualizar dicionario primeira ocorrencia do simbolo
                        byte[] byteArray = toByteArray(bytesLido);
                        dicionario.put(byteArray);

                        // Escrever em arquivo posicao ZERO e novo valor
                        byte[] zero = ByteBuffer.allocate(4).putInt(0).array();
                        arqNovo.write(zero);
                        arqNovo.write(byteArray);
                    }

                    // Atualizar ponteiro
                    posAtual = arqAntigo.getFilePointer();
                }

                // Mostrar barra progresso completa
                io.gerarBarraProgresso(tamArquivoOriginal, (int)posAtual);

                // Fechar arquivo novo
                arqNovo.close();

                // Obter tamanho do arquivo novo
                File arquivoComprimido = new File(nomeArquivo);
                long tamArquivoComprimido = arquivoComprimido.length();

                // Mostrar compressao
                double compressao = (double)(tamArquivoRegistroDB - tamArquivoComprimido) / tamArquivoRegistroDB * 100;
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
     * LZ78.
     * @return nomeArquivo gerado.
     */
    private String descomprimir() {
        
        RandomAccessFile arqAntigo = null;
        RandomAccessFile arqNovo = null;

        // Atualizar nome arquivo
        String nomeArquivoAntigo = nomeArquivo;

        try {

            // Reiniciar dicionario
            dicionario = new Dicionario();
            
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
                    nomeArquivo = caminhoPasta + "/RegistroLZ78Compressao" + versaoAtual + ".db";
                }

                // Abrir arquivo
                arqNovo = new RandomAccessFile (nomeArquivo, "rw");
                arqNovo.seek(0);

                // Mostrar barra de progresso
                System.out.println("\n\nDescomprimindo arquivo: ");

                // Atualizar ponteiro
                long posAtual = arqAntigo.getFilePointer();

                // Obter tamanho do arquivo original
                File arquivoOriginal = new File(nomeArquivoAntigo);
                long tamArquivoOriginal = arquivoOriginal.length();

                // Descomprimir ate' acabar arquivo
                while (posAtual != arqAntigo.length()) {

                    // Mostrar barra progresso
                    io.gerarBarraProgresso(tamArquivoOriginal, (int)posAtual);

                    // Ler posicao de recuperacao
                    Integer posDicionario = arqAntigo.readInt();

                    // Atualizar ponteiro
                    posAtual = arqAntigo.getFilePointer();

                    // Testar se houve ultimo byte ou se e' apenas posicao dicionario
                    if (posAtual != arqAntigo.length()) {

                        // Ler byte novo
                        byte byteNovo = arqAntigo.readByte();

                        // Testar se ha' bytes para recuperar
                        ArrayList<Byte> bytesAntigo = new ArrayList<>();
                        if (posDicionario != 0) {

                            // Escrever bytes antigos
                            bytesAntigo = toByteArrayList(dicionario.get(posDicionario));
                            byte[] bytesAntigos = dicionario.get(posDicionario);
                            arqNovo.write(bytesAntigos);
                        }

                        // Escrever byte novo
                        arqNovo.write(byteNovo);

                        // Atualizar dicionario
                        bytesAntigo.add(byteNovo);
                        dicionario.put(toByteArray(bytesAntigo));

                    } else {
                        
                        // Recuperar ultimo array de byte
                        if (posDicionario != 0) {

                            // Escrever bytes antigos
                            byte[] bytesAntigos = dicionario.get(posDicionario);
                            arqNovo.write(bytesAntigos);
                        }

                    }

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
     * Metodo para atualizar a versao e o nome do arquivo atual.
     */
    private void updateVersaoAtual () {

        // Abrir pasta de compressao
        File pasta = new File(caminhoPasta);
        
        // Se pasta existir, procurar versao existente
        if (pasta.exists() && pasta.isDirectory()) {
            
            // Listar arquivos na pasta compressao
            File[] arquivos = pasta.listFiles();

            // Percorrer os arquivos de compressao para encontrar
            boolean find = false;
            int i;
            for (i = 0; i < arquivos.length && find == false; i++) {               

                // Verificar se tamanho e' valido (quando invalido significa que e' a pasta da arvore)
                if(arquivos[i].isFile()) {

                    // Testar se e' arquivo LZ78
                    String nomeCompressao = arquivos[i].getName().substring(8, 11);
                    find = nomeCompressao.equals("LZ78");
                }
            }

            // Se encontrar renomear atributos da classe se encontrar
            if (find) {

                // Posicionar ponteiro no nome do arquivo
                String nomeCompressao = "RegistroLZ78Compressao";
                String arqEncontrado = arquivos[i-1].getName();
                int posInicio = arqEncontrado.indexOf(nomeCompressao) + nomeCompressao.length();
                int posFim = arqEncontrado.indexOf(".db");

                // Encontrar versao atual
                String versaoAtualStr = arqEncontrado.substring(posInicio, posFim);
                versaoAtual = Integer.parseInt(versaoAtualStr);
                nomeArquivo = caminhoPasta + "/RegistroLZ78Compressao" + versaoAtual + ".db";

            // Se ocorrer o erro de chegar ao fim do loop e nao encontrar, resetar arquivo
            } else {

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
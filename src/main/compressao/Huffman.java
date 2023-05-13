// Package
package compressao;

// Bibliotecas
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

// Bibliotecas proprias
import app.IO;

public class Huffman {

    // Caminhos para arquivos
    private static final String caminhoPasta = "./src/resources/compressao";
    private static final String caminhoPastaArvore = "./src/resources/compressao/arvoreHuffman";
    private static final String registroDB = "./src/resources/Registro.db";

    private No raiz;
    protected int versaoAtual;
    protected String nomeArquivo;
    protected String nomeArquivoArvore;

    IO io = new IO();

    /**
     * Construtor padrao da classe Huffman.
     */
    public Huffman() {
        raiz = null;

        // Atualizar versao e nome arquivos atuais
        updateVersaoAtual();
    }

    /**
     * Metodo para comprimir um arquivo binario, utilizando o algoritmo Huffman.
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
     * Metodo para descomprimir um arquivo binario, utilizando o algoritmo 
     * Huffman.
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
     * Huffman.
     * @return nomeArquivo gerado.
     */
    private String comprimir() {

        RandomAccessFile inputFile = null;
        RandomAccessFile outputFile = null;

        // Atualizar nome arquivo
        String nomeArquivoAntigo = nomeArquivo;

        try {

            // Abrir a pasta se nao existir
            File pasta = new File(caminhoPasta);
            if (!pasta.exists()) {
                pasta.mkdirs();
            }

            // Abrir a pasta para arvore se nao existir
            File pastaArvore = new File(caminhoPastaArvore);
            if (!pastaArvore.exists()) {
                pastaArvore.mkdirs();
            }

            // Abrir arquivo antigo
            inputFile = new RandomAccessFile (nomeArquivoAntigo, "rw");
            inputFile.seek(0);

            // Somente comprimir se houver arquivo
            if (inputFile.length() > 0) {

                // Abrir arquivo novo
                versaoAtual++;
                nomeArquivo = caminhoPasta + "/RegistroHuffmanCompressao" + versaoAtual + ".db";
                outputFile = new RandomAccessFile (nomeArquivo, "rw");
                outputFile.seek(0);

                // Obter tamanho do arquivo "Registro.db"
                File arquivoRegistroDB = new File(registroDB);
                long tamArquivoRegistroDB = arquivoRegistroDB.length();

                // Obter tamanho do arquivo original
                File arquivoOriginal = new File(nomeArquivoAntigo);
                long tamArquivoOriginal = arquivoOriginal.length();
                
                // Mostrar barra de progresso
                long posAtual = inputFile.getFilePointer();
                System.out.println("\nComprimindo arquivo: ");
                io.gerarBarraProgresso(tamArquivoOriginal, (int)posAtual);

                // Obter frequencia de cada byte no arquivo
                Map<Byte, Integer> frequencias = recorrenciaDeBytes(nomeArquivoAntigo);

                // Gerar arvore Huffman e salvar em arquivo
                gerarArvoreHuffman(frequencias);

                // Gravar arvore em arquivo
                nomeArquivoArvore = caminhoPastaArvore + "/ArvoreHuffmanCompressao" + versaoAtual + ".db";
                RandomAccessFile output = new RandomAccessFile(nomeArquivoArvore, "rw");
                escreverArvore(raiz, output);
                output.close();

                // Criar o mapa de codigos para cada byte da arvore de Huffman
                Map<Byte, String> codigos = gerarCodigos();

                // String para os codigos gerados
                StringBuilder bits = new StringBuilder();

                // Percorrer ate' fim de arquivo
                while (posAtual != inputFile.length()) {

                    // Mostrar barra progresso
                    io.gerarBarraProgresso(tamArquivoOriginal, (int)posAtual);

                    // Ler byte
                    byte byteLido = inputFile.readByte();
                    
                    // Adicionar codigo do bit lido
                    String codigo = codigos.get(byteLido);
                    bits.append(codigo);

                    // Se tiver 4 caracteres ou mais
                    while (bits.length() >= 8) {

                        // Escrever como 8 bits (1 byte)
                        String byteStr = bits.substring(0, 8);
                        byte byteCompleto = (byte)Integer.parseInt(byteStr, 2);
                        outputFile.write(byteCompleto);
                        bits.delete(0, 8);

                    }

                    // Atualizar ponteiro
                    posAtual = inputFile.getFilePointer();
                }
        
                // Se faltou bits, escreve-los no arquivo
                if (bits.length() > 0) {

                    // Completar com zeros no fim
                    String byteStr = bits.toString();
                    byte byteCompleto = (byte)Integer.parseInt(byteStr, 2);

                    // Escrever em arquivo
                    outputFile.write(byteCompleto);
                }

                // Fechar arquivos
                inputFile.close();
                outputFile.close();

                // Mostrar barra progresso completa
                io.gerarBarraProgresso(tamArquivoOriginal, (int)posAtual);

                // Obter tamanho do arquivo novo
                File arquivoComprimido = new File(nomeArquivo);
                long tamArquivoComprimido = arquivoComprimido.length();

                // Mostrar compressao
                double compressao = (double)(tamArquivoRegistroDB - tamArquivoComprimido) / tamArquivoRegistroDB * 100;
                System.out.println(String.format("\nTaxa compressao: %.2f%%", compressao));

                // Apagar arquivo antigo se nao for a base de dados
                if(! nomeArquivoAntigo.equals(registroDB)) {
                    File arquivo = new File(nomeArquivoAntigo);
                    arquivo.delete();
                }
                
            // Solicitar que carregue dados iniciais
            } else {
                System.out.println("\nERRO: Registro vazio!" +
                                   "\n      Tente carregar os dados iniciais primeiro!\n");
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
        RandomAccessFile arqArvore = null;

        // Atualizar nome arquivo
        String nomeArquivoAntigo = nomeArquivo;

        try {

            // Abrir arquivo antigo
            arqAntigo = new RandomAccessFile (nomeArquivoAntigo, "rw");
            arqAntigo.seek(0);

            // Somente descomprimir se houver arquivo
            if (arqAntigo.length() > 0) {

                // Carregar arvore atual de compressao
                nomeArquivoArvore = caminhoPastaArvore + "/ArvoreHuffmanCompressao" + versaoAtual + ".db";
                arqArvore = new RandomAccessFile(nomeArquivoArvore, "r");
                raiz = lerArvore(arqArvore);
                arqArvore.close();

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
                    nomeArquivo = caminhoPasta + "/RegistroHuffmanCompressao" + versaoAtual + ".db";
                }

                // Abrir arquivo
                arqNovo = new RandomAccessFile (nomeArquivo, "rw");
                arqNovo.seek(0);

                // Obter tamanho do arquivo original
                File arquivoOriginal = new File(nomeArquivoAntigo);
                long tamArquivoOriginal = arquivoOriginal.length();

                // Mostrar barra de progresso
                System.out.println("\nDescomprimindo arquivo: ");

                // String para os codigos gerados
                StringBuilder bits = new StringBuilder();

                // Percorrer ate' fim de arquivo
                long posAtual = arqAntigo.getFilePointer();
                while (posAtual != arqAntigo.length()) {

                    // Mostrar barra progresso
                    io.gerarBarraProgresso(tamArquivoOriginal, (int)posAtual);

                    // Ler byte
                    byte byteLido = arqAntigo.readByte();

                    // Converter para string binaria
                    String byteStr = String.format("%8s", Integer.toBinaryString(byteLido & 0xFF)).replace(' ', '0');
                    bits.append(byteStr);

                    // Percorrer string binaria procurando codigo valido
                    boolean findByte = true;
                    while (findByte) {
                        int pos = 0;
                        No no = raiz;
                        while (pos < bits.length() && no != null && !isFolha(no)) {

                            // Procurar No folha com codigo valido
                            char bit = bits.charAt(pos);
                            pos++;

                            if (bit == '0') {
                                no = no.esquerda;
                            } else {
                                no = no.direita;
                            }
                        }

                        // Testar se encontrou o codigo
                        findByte = pos != bits.length();

                        if (findByte) {

                            // Obter valor do byte codificado e salvar
                            byte byteOriginal = no.valor;
                            arqNovo.write(byteOriginal);

                            // Deletar String utilizada
                            bits.delete(0, pos);
                        }
                    }

                    // Atualizar ponteiro
                    posAtual = arqAntigo.getFilePointer();

                }
                
                // Mostrar barra progresso completa
                io.gerarBarraProgresso(tamArquivoOriginal, (int)posAtual);
                
                // Fechar arquivos
                arqAntigo.close();
                arqNovo.close();

                // Apagar arquivos antigos
                File arquivo = new File(nomeArquivoAntigo);
                File arquivoArvore = new File(nomeArquivoArvore);
                arquivo.delete();
                arquivoArvore.delete();

                // Se voltou ao original e so tinha esse arquivo, apagar pasta tambem
                if (versaoAtual == 0) {

                    // Abrir pasta de compressao
                    File pasta = new File(caminhoPasta);
                    File pastaArvore = new File(caminhoPastaArvore);

                    // Verificar se pasta das arvores existe
                    if (pastaArvore.exists() && pastaArvore.isDirectory()) {

                        // Verificar se a pasta esta' vazia
                        File[] arquivos = pastaArvore.listFiles();
                        if (arquivos == null || arquivos.length == 0) {

                            // Apagar pasta
                            pastaArvore.delete();
                        }
                    }

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

            // Solicitar que carregue dados iniciais
            } else {
                System.out.println("\nERRO: Registro vazio!" +
                                "\n      Tente carregar os dados iniciais primeiro!\n");
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
     * Metodo para obter um mapeamento com a recorrencia de cada byte no arquivo.
     * @param caminhoArquivo - para arquivo que se deseja comprimir.
     * @return mapa com a recorrencia desejada.
     */
    private Map<Byte, Integer> recorrenciaDeBytes(String caminhoArquivo) {
        Map<Byte, Integer> recorrencia = new HashMap<>();

        try (RandomAccessFile arquivo = new RandomAccessFile(caminhoArquivo, "r")) {

            // Obter posicao arquivo
            long posAtual = arquivo.getFilePointer();

            // Percorrer ate' fim de arquivo
            while (posAtual != arquivo.length()) {
                byte byteLido = arquivo.readByte();

                // Adicionar ao mapa, aumentando a frequencia se existir
                if (recorrencia.containsKey(byteLido)) {
                    recorrencia.put(byteLido, recorrencia.get(byteLido) + 1);
                } else {
                    recorrencia.put(byteLido, 1);
                }

                posAtual = arquivo.getFilePointer();
            }

        } catch (FileNotFoundException e) {
            System.out.println("\nERRO: Registro vazio!" +
                               "\n      Tente carregar os dados iniciais primeiro!\n");
        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler o arquivo \"" + caminhoArquivo + "\"\n");
        }

        return recorrencia;
    }

    /**
     * Metodo para gerar a arvore de Huffman, a partir da frequencia de cada
     * byte.
     * @param frequencias - mapeamento da frequencia de cada byte no arquivo.
     */
    private void gerarArvoreHuffman(Map<Byte, Integer> frequencias) {

        // Cria uma fila de prioridade com os Nos folha
        PriorityQueue<No> fila = new PriorityQueue<>();
        for (Map.Entry<Byte, Integer> entry : frequencias.entrySet()) {
            No no = new No(entry.getKey(), entry.getValue());
            fila.offer(no);
        }
    
        // Cria a arvore de Huffman a partir da fila de prioridade
        while (fila.size() > 1) {

            // Remove os dois Nos com menor frequência
            No noEsquerda = fila.poll();
            No noDireita = fila.poll();
    
            // Cria um novo No interno com a soma das frequências dos nós removidos
            int novaFrequencia = noEsquerda.frequencia + noDireita.frequencia;
            No novoNo = new No(novaFrequencia, noEsquerda, noDireita);
    
            // Adiciona o novo No 'a fila
            fila.offer(novoNo);
        }
    
        // Adiciona o No 'a raiz da arvore de Huffman
        raiz = fila.poll();
    }

    /**
     * Metodo para gerar os codigos para cada byte da arvore Huffman.
     * @return - String com o codigo gerado de zeros e uns.
     */
    private Map<Byte, String> gerarCodigos() {
        Map<Byte, String> codigos = new HashMap<>();
        gerarCodigosRecursivo(raiz, "", codigos);
        return codigos;
    }
    
    /**
     * Metodo recursivo para gerar os codigos da arvore Huffman.
     * @param no - No atual em analise.
     * @param codigoAtual - String com o codigo atual em desenvolvimento.
     * @param codigos - Mapeamento do codigo para cada byte.
     */
    private void gerarCodigosRecursivo(No no, String codigoAtual, Map<Byte, String> codigos) {
        if (no == null) {
            return;
        }
        
        if (no.esquerda == null && no.direita == null) {
            codigos.put(no.valor, codigoAtual);
        } else {
            gerarCodigosRecursivo(no.esquerda, codigoAtual + "0", codigos);
            gerarCodigosRecursivo(no.direita, codigoAtual + "1", codigos);
        }
    }

    /**
     * Metodo para escrever a arvore Huffman gerada em arquivo.
     * @param no - No atual em analise.
     * @param output - Ponteiro para o arquivo da arvore.
     */
    private void escreverArvore(No no, RandomAccessFile output) {

        try {
            // Escrever enquanto No diferente de null
            if (no != null) {

                // Se for folha, marcar com o byte 0 (parar)
                if (isFolha(no)) {
                    output.writeByte(0);
                    output.writeByte(no.valor);
                
                // Se nao for, marcar com byte 1 (continuar)
                } else {
                    output.writeByte(1);
                    escreverArvore(no.esquerda, output);
                    escreverArvore(no.direita, output);
                }
                
            }
        
        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao escrever o arquivo \"" + nomeArquivoArvore + "\"\n");
        }
    }

    /**
     * Metodo para ler a arvore Huffman gerada de arquivo.
     * @param input - Ponteiro para o arquivo da arvore.
     * @return a raiz da arvore Huffman.
     */
    public No lerArvore(RandomAccessFile input) {

        No novoNo = null;

        try {
        
            // Ler byte
            byte bitMarcador = input.readByte();

            // Se for folha, gerar novo No
            if (bitMarcador == 0) {
                byte valor = input.readByte();
                novoNo = new No(valor);
            
            // Se nao for, continuar
            } else {
                No esquerda = lerArvore(input);
                No direita = lerArvore(input);
                novoNo = new No(esquerda, direita);
            }

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler o arquivo \"" + nomeArquivoArvore + "\"\n");
        }

        return novoNo;
    }


    /**
     * Metodo para determinar se um No e' ou nao uma folha
     * @param no - No para analisar
     * @return true, se for; false, caso contrario.
     */
    private boolean isFolha (No no) {
        return (no.esquerda == null && no.direita == null);
    } 

    /**
     * Metodo para atualizar a versao e o nome do arquivo atual.
     */
    private void updateVersaoAtual () {

        // Abrir pasta de compressao
        File pasta = new File(caminhoPasta);
        File pastaArvore = new File(caminhoPastaArvore);
        
        // Se pastas existirem, procurar versao existente
        if (pasta.exists() && pasta.isDirectory() &&
            pastaArvore.exists() && pastaArvore.isDirectory()) {
            
            // Listar arquivos na pasta compressao
            File[] arquivos = pasta.listFiles();

            // Percorrer os arquivos de compressao para encontrar
            boolean find = false;
            int i;
            for (i = 0; i < arquivos.length && find == false; i++) {

                // Verificar se tamanho e' valido (quando invalido significa que e' a pasta da arvore)
                if(arquivos[i].isFile()) {

                    // Testar se e' arquivo Huffman
                    String nomeCompressao = arquivos[i].getName().substring(8, 15);
                    find = nomeCompressao.equals("Huffman");
                }
            }

            // Se encontrar renomear atributos da classe se encontrar
            if (find) {

                // Posicionar ponteiro no nome do arquivo
                String nomeCompressao = "RegistroHuffmanCompressao";
                String arqEncontrado = arquivos[i-1].getName();
                int posInicio = arqEncontrado.indexOf(nomeCompressao) + nomeCompressao.length();
                int posFim = arqEncontrado.indexOf(".db");
 
                // Encontrar versao atual
                String versaoAtualStr = arqEncontrado.substring(posInicio, posFim);
                System.out.println("versaoAtualStr: " + versaoAtualStr);

                versaoAtual = Integer.parseInt(versaoAtualStr);
                nomeArquivo = caminhoPasta + "/RegistroHuffmanCompressao" + versaoAtual + ".db";
            
                // Se ocorrer o erro de chegar ao fim do loop e nao encontrar, resetar arquivo
            } else {

                // Redefinir versao para comprimir
                versaoAtual = 0;
                nomeArquivo = registroDB;
                nomeArquivoArvore = caminhoPastaArvore + "/ArvoreHuffmanCompressao1.db";
            }

        // Se pasta nao existir, o arquivo ainda nao foi comprimido
        } else {

            // Nao definir nova versao comprimida ainda
            versaoAtual = 0;
            nomeArquivo = registroDB;
            nomeArquivoArvore = caminhoPasta + "/ArvoreHuffman/ArvoreHuffmanCompressao1.db";
        }
    }
}
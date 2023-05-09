package compressao;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class Huffman {
    
    // classe interna para representar um no na arvore de Huffman
    private static class No implements Comparable<No> {
        byte valor;
        int frequencia;
        No esquerda;
        No direita;
        
        public No(byte valor, int frequencia) {
            this.valor = valor;
            this.frequencia = frequencia;
        }

        public No(byte valor) {
            this.valor = valor;
            this.esquerda = null;
            this.direita = null;
        }

        public No(No esquerda, No direita) {
            this.valor = 0;
            this.esquerda = esquerda;
            this.direita = direita;
        }
        
        // compara os nos com base em sua frequencia, para a construcão da fila de prioridade
        public int compareTo(No other) {
            return this.frequencia - other.frequencia;
        }
    }
    
    // retorna um mapa com a recorrencia de cada byte no arquivo
    public static Map<Byte, Integer> recorrenciaDeBytes(String caminhoArquivo) throws IOException {
        Map<Byte, Integer> recorrencia = new HashMap<>();

        try (RandomAccessFile arquivo = new RandomAccessFile(caminhoArquivo, "r")) {
            byte[] buffer = new byte[1024];
            int bytesLidos;

            while ((bytesLidos = arquivo.read(buffer)) != -1) {
                for (int i = 0; i < bytesLidos; i++) {
                    byte b = buffer[i];
                    if (recorrencia.containsKey(b)) {
                        recorrencia.put(b, recorrencia.get(b) + 1);
                    } else {
                        recorrencia.put(b, 1);
                    }
                }
            }
        }

        return recorrencia;
    }
    
    public static void imprimirRecorrenciaDeBytes(Map<Byte, Integer> recorrencia) {
        for (Map.Entry<Byte, Integer> entry : recorrencia.entrySet()) {
            byte b = entry.getKey();
            int ocorrencias = entry.getValue();
            System.out.printf("Byte 0x%02X: %d ocorrencias%n", b & 0xFF, ocorrencias);
        }
    }

    // constroi a arvore de Huffman a partir do mapa de recorrencia de bytes
    public static No construirArvore(Map<Byte, Integer> recorrencia) {
        PriorityQueue<No> fila = new PriorityQueue<>();
        
        // adicionar cada byte como um no na fila de prioridade
        for (Map.Entry<Byte, Integer> entry : recorrencia.entrySet()) {
            fila.offer(new No(entry.getKey(), entry.getValue()));
        }
        
        // combinar os nos com menor frequencia ate que haja apenas um no restante na fila
        while (fila.size() > 1) {
            No esquerda = fila.poll();
            No direita = fila.poll();
            No pai = new No((byte) 0, esquerda.frequencia + direita.frequencia);
            pai.esquerda = esquerda;
            pai.direita = direita;
            fila.offer(pai);
        }
        
        // o ultimo no restante na fila e a raiz da arvore de Huffman
        return fila.poll();
    }

    // escreve a arvore de Huffman em um arquivo binario
    public static void escreverArvore(No no, DataOutputStream out) throws IOException {
        if (no == null) {
            return;
        }

        if (no.esquerda == null && no.direita == null) {
            // escreve um byte 0 seguido do valor do byte
            out.writeByte(0);
            out.writeByte(no.valor);
        } else {
            // escreve um byte 1
            out.writeByte(1);
            escreverArvore(no.esquerda, out);
            escreverArvore(no.direita, out);
        }
    }

    public static No gerarArvoreHuffman(Map<Byte, Integer> frequencias) {
        // cria uma fila de prioridade com os nós folha
        PriorityQueue<No> fila = new PriorityQueue<>();
        for (Map.Entry<Byte, Integer> entry : frequencias.entrySet()) {
            No no = new No(entry.getKey(), entry.getValue());
            fila.offer(no);
        }
    
        // cria a arvore de Huffman a partir da fila de prioridade
        while (fila.size() > 1) {
            // remove os dois nós com menor frequência
            No noEsquerda = fila.poll();
            No noDireita = fila.poll();
    
            // cria um novo nó interno com a soma das frequências dos nós removidos
            No novoNo = new No((byte) 0, noEsquerda.frequencia + noDireita.frequencia);
            novoNo.esquerda = noEsquerda;
            novoNo.direita = noDireita;
    
            // adiciona o novo nó à fila
            fila.offer(novoNo);
        }
    
        // retorna a raiz da arvore de Huffman
        return fila.poll();
    }

    public static Map<Byte, String> gerarCodigos(No raiz) {
        Map<Byte, String> codigos = new HashMap<>();
        gerarCodigosRecursivo(raiz, "", codigos);
        return codigos;
    }
    
    private static void gerarCodigosRecursivo(No no, String codigoAtual, Map<Byte, String> codigos) {
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
    
    public static void comprimir(String caminhoArquivo, String caminhoArquivoComprimido, No raiz) throws IOException {

        // cria um mapa de códigos para cada byte da arvore de Huffman
        Map<Byte, String> codigos = gerarCodigos(raiz);

        try (RandomAccessFile inputFile = new RandomAccessFile(caminhoArquivo, "r");
             RandomAccessFile outputFile = new RandomAccessFile(caminhoArquivoComprimido, "rw")) {
    
            // cria um buffer para armazenar os bytes do arquivo original
            byte[] buffer = new byte[1024];
            int bytesLidos;
    
            // escreve os bytes comprimidos no arquivo de saída
            StringBuilder bits = new StringBuilder();
            long posicaoArquivo = 0;
            while ((bytesLidos = inputFile.read(buffer)) != -1) {
                for (int i = 0; i < bytesLidos; i++) {
                    byte b = buffer[i];
                    String codigo = codigos.get(b);
                    bits.append(codigo);
                    while (bits.length() >= 8) {
                        String byteStr = bits.substring(0, 8);
                        int byteInt = Integer.parseInt(byteStr, 2);
                        outputFile.seek(posicaoArquivo);
                        outputFile.write(byteInt);
                        posicaoArquivo++;
                        bits.delete(0, 8);
                    }
                }
            }
    
            // se houver bits não utilizados, escreve o ultimo byte no arquivo de saída
            if (bits.length() > 0) {
                String byteStr = bits.toString();
                while (byteStr.length() < 8) {
                    byteStr += "0";
                }
                int byteInt = Integer.parseInt(byteStr, 2);
                outputFile.seek(posicaoArquivo);
                outputFile.write(byteInt);
            }
        }

        escreverArvore(raiz, new DataOutputStream(new FileOutputStream("arvore.db")));
    }
    
    public static No lerArvore(DataInputStream in) throws IOException {
        int bit = in.readByte();
        if (bit == 0) {
            byte valor = in.readByte();
            return new No(valor);
        } else {
            No esquerda = lerArvore(in);
            No direita = lerArvore(in);
            return new No(esquerda, direita);
        }
    }

    public static void descomprimir(String caminhoArquivoComprimido, String caminhoArquivoOriginal) throws IOException {
        
        try (RandomAccessFile inputFile = new RandomAccessFile(caminhoArquivoComprimido, "r");
             FileOutputStream outputFile = new FileOutputStream(caminhoArquivoOriginal)) {
            
            // Lê a árvore de Huffman a partir do arquivo "arvore.db"
            No raiz = lerArvore(new DataInputStream(new FileInputStream("arvore.db")));

            // Cria um buffer para armazenar os bytes do arquivo comprimido
            byte[] buffer = new byte[1024];
            int bytesLidos;
            
            // Decodifica os bytes e escreve no arquivo de saída
            StringBuilder bits = new StringBuilder();
            while ((bytesLidos = inputFile.read(buffer)) != -1) {
                for (int i = 0; i < bytesLidos; i++) {
                    int byteInt = buffer[i] & 0xFF;
                    String byteStr = Integer.toBinaryString(byteInt);
                    while (byteStr.length() < 8) {
                        byteStr = "0" + byteStr;
                    }
                    bits.append(byteStr);
                }
                while (bits.length() >= 8) {
                    No no = raiz;
                    while (no.esquerda != null && no.direita != null) {
                        char bit = bits.charAt(0);
                        bits.deleteCharAt(0);
                        if (bit == '0') {
                            no = no.esquerda;
                        } else {
                            no = no.direita;
                        }
                    }
                    byte b = no.valor;
                    outputFile.write(b);
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            // caminho do arquivo de entrada
            String caminhoArquivo = "Registro.db";

            // obtém a frequência de cada byte no arquivo
            Map<Byte, Integer> frequencias = recorrenciaDeBytes(caminhoArquivo);

            // gera a arvore de Huffman
            No raiz = gerarArvoreHuffman(frequencias);

            // abre o arquivo para escrita
            FileOutputStream fileOut = new FileOutputStream("arvore.db");
            DataOutputStream dataOut = new DataOutputStream(fileOut);

            // escreve a arvore de Huffman no arquivo
            escreverArvore(raiz, dataOut);

            comprimir(caminhoArquivo, "arquivoComprimido.db", raiz);
            descomprimir("arquivoComprimido.db", "arquivoDescomprimido.db");
            System.out.println("Taxa de compressão: " + (1 - (double) new File("arquivoComprimido.db").length() / new File("Registro.db").length()));

            // fecha o arquivo
            dataOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
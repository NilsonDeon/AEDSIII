package compressao;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;

public class Huffman {

    public static class ArvoreHuffman {

        // Classe interna que representa um nó da árvore
        public class NoHuffman implements Comparable<NoHuffman> {
            public byte valor;
            public int frequencia;
            public NoHuffman esquerda;
            public NoHuffman direita;

            public NoHuffman(byte valor, int frequencia) {
                this.valor = valor;
                this.frequencia = frequencia;
            }
    
            // Compara os nós com base em suas frequências
            public int compareTo(NoHuffman outro) {
                return this.frequencia - outro.frequencia;
            }
        }
    
        // Cria a árvore a partir de uma string de entrada
        public NoHuffman criarArvoreHuffman(byte[] entrada) {
            // Cria o mapa de frequência
            Map<Byte, Integer> frequencia = new HashMap<>();
            for (byte b : entrada) {
                frequencia.put(b, frequencia.getOrDefault(b, 0) + 1);
            }
            
            // Cria a lista de nós da árvore de Huffman
            List<NoHuffman> nos = new ArrayList<>();
            for (Map.Entry<Byte, Integer> entry : frequencia.entrySet()) {
                nos.add(new NoHuffman(entry.getKey(), entry.getValue()));
            }
            
            // Monta a árvore de Huffman
            while (nos.size() > 1) {
                // Remove os dois nós com menores frequências
                Collections.sort(nos, (a, b) -> a.frequencia - b.frequencia);
                NoHuffman esquerda = nos.remove(0);
                NoHuffman direita = nos.remove(0);
                
                // Cria um novo nó com a soma das frequências dos nós removidos
                NoHuffman novoNo = new NoHuffman((byte) 0, esquerda.frequencia + direita.frequencia);
                novoNo.esquerda = esquerda;
                novoNo.direita = direita;
                
                // Adiciona o novo nó na lista
                nos.add(novoNo);
            }
            
            // Retorna a raiz da árvore de Huffman
            return nos.get(0);
        }
    
        // Cria um mapa que associa cada byte a seu código Huffman correspondente
        public Map<Byte, String> criarTabelaCodigo(NoHuffman raiz) {
            Map<Byte, String> tabelaCodigo = new HashMap<>();
            criarTabelaCodigoRecursivo(raiz, "", tabelaCodigo);
            return tabelaCodigo;
        }
    
        // Percorre a árvore recursivamente, construindo a tabela de códigos
        private void criarTabelaCodigoRecursivo(NoHuffman no, String codigo, Map<Byte, String> tabelaCodigo) {
            if (no == null) {
                return;
            }
            
            if (no.esquerda == null && no.direita == null) {
                tabelaCodigo.put(no.valor, codigo);
                return;
            }
            
            criarTabelaCodigoRecursivo(no.esquerda, codigo + "0", tabelaCodigo);
            criarTabelaCodigoRecursivo(no.direita, codigo + "1", tabelaCodigo);
        }
    
        // Codifica uma string usando a tabela de códigos de Huffman
        public byte[] codificar(byte[] entrada) {
            // Cria a árvore de Huffman
            NoHuffman raiz = criarArvoreHuffman(entrada);
            
            // Cria a tabela de códigos
            Map<Byte, String> tabelaCodigo = criarTabelaCodigo(raiz);
            
            // Codifica a entrada usando a tabela de códigos
            StringBuilder resultado = new StringBuilder();
            for (byte b : entrada) {
                resultado.append(tabelaCodigo.get(b));
            }
            
            // Converte o resultado para um array de bytes
            String resultadoStr = resultado.toString();
            byte[] resultadoBytes = new byte[resultadoStr.length() / 8 + 1];
            int posicaoByte = 0;
            int posicaoBit = 0;
            for (int i = 0; i < resultadoStr.length(); i++) {
                if (resultadoStr.charAt(i) == '1') {
                    resultadoBytes[posicaoByte] |= (1 << (7 - posicaoBit));
                }
                posicaoBit++;
                if (posicaoBit == 8) {
                    posicaoBit = 0;
                    posicaoByte++;
                }
            }
            
            return resultadoBytes;
        }

        public byte[] decodificar(byte[] entrada, NoHuffman raiz) {
            // Converte a entrada para uma string de bits
            StringBuilder bits = new StringBuilder();
            for (byte b : entrada) {
                for (int i = 7; i >= 0; i--) {
                    bits.append((b >> i) & 1);
                }
            }
            
            // Decodifica os bits usando a árvore de Huffman
            List<Byte> resultado = new ArrayList<>();
            NoHuffman no = raiz;
            for (int i = 0; i < bits.length(); i++) {
                if (bits.charAt(i) == '0') {
                    no = no.esquerda;
                } else {
                    no = no.direita;
                }
                if (no.esquerda == null && no.direita == null) {
                    resultado.add(no.valor);
                    no = raiz;
                }
            }
        }
    }

    

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
            System.out.printf("Byte 0x%02X: %d ocorrências%n", b & 0xFF, ocorrencias);
        }
    }

}
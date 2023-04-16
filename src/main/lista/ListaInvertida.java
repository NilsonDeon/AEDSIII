package lista;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;

class ListaInvertida {

    public static void main(String[] args) {
        String arquivo = "frases.db";
        String arquivo2 = "palavras.db";
        try {
            // Cria um objeto BufferedReader usando o arquivo de entrada
            BufferedReader br = new BufferedReader(new FileReader("Spotify.csv"));
        
            // Lê o cabeçalho para saber o índice da coluna desejada
            int columnIndex = 2;
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (columnIndex >= 0 && columnIndex < values.length) {
                    inserirFrase(values[columnIndex], arquivo, arquivo2);
                }
            }

            // Fecha o objeto BufferedReader
            br.close();

            String[] frases = lerPalavras(arquivo);
            for (String frase : frases) {
                System.out.println(frase);
            }
        } catch (IOException e) {
            System.out.println("Erro ao inserir ou ler frases: " + e.getMessage());
        }
    }

    public static void inserirFrase(String frase, String arquivoFrase, String arquivoPalavras) throws IOException {
        byte[] bytesFrase = frase.getBytes();
        FileOutputStream fosFrase = new FileOutputStream(arquivoFrase, true);
        DataOutputStream dosFrase = new DataOutputStream(fosFrase);
        dosFrase.writeInt(bytesFrase.length);
        dosFrase.write(bytesFrase);
        dosFrase.close();
        fosFrase.close();
        
        String[] palavras = frase.split(" ");
        RandomAccessFile rafPalavras = new RandomAccessFile(arquivoPalavras, "rw");
        long posicaoPalavras = rafPalavras.length();
        for (String palavra : palavras) {
            rafPalavras.writeInt(palavra.getBytes().length);
            rafPalavras.write(palavra.getBytes());
            rafPalavras.writeLong(posicaoPalavras);
        }
        rafPalavras.close();
    }

    public static String[] lerFrases(String arquivo) throws IOException {
        FileInputStream fis = new FileInputStream(arquivo);
        DataInputStream dis = new DataInputStream(fis);
        String[] frases = new String[0];
        while (dis.available() > 0) {
            int tamanhoFrase = dis.readInt();
            byte[] bytesFrase = new byte[tamanhoFrase];
            dis.read(bytesFrase);
            String frase = new String(bytesFrase);
            frases = adicionaFrase(frases, frase);
        }
        dis.close();
        fis.close();
        return frases;
    }

    private static String[] adicionaFrase(String[] frases, String frase) {
        String[] novoArray = new String[frases.length + 1];
        for (int i = 0; i < frases.length; i++) {
            novoArray[i] = frases[i];
        }
        novoArray[frases.length] = frase;
        return novoArray;
    }

    public static String[] lerPalavras(String arquivoPalavras) throws IOException {
        FileInputStream fisPalavras = new FileInputStream(arquivoPalavras);
        DataInputStream disPalavras = new DataInputStream(fisPalavras);
        String[] palavras = new String[0];
        while (disPalavras.available() > 0) {
            int tamanhoPalavra = disPalavras.readInt();
            byte[] bytesPalavra = new byte[tamanhoPalavra];
            disPalavras.read(bytesPalavra);
            String palavra = new String(bytesPalavra);
            System.out.println(palavra);
            long posicao = disPalavras.readLong(); // Lê a posição da palavra (não utilizada aqui)
        }
        disPalavras.close();
        fisPalavras.close();
        return palavras;
    }

    private static String[] adicionaPalavra(String[] palavras, String palavra) {
        // Verifica se a palavra já existe no array de palavras
        for (String p : palavras) {
            if (p.equals(palavra)) {
                return palavras; // Se a palavra já existe, retorna o mesmo array de palavras
            }
        }
        // Se a palavra não existe, cria um novo array com a palavra adicionada
        String[] novoArray = new String[palavras.length + 1];
        for (int i = 0; i < palavras.length; i++) {
            novoArray[i] = palavras[i];
        }
        novoArray[palavras.length] = palavra;
        return novoArray;
    }

}
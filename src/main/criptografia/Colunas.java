package criptografia;

import java.util.ArrayList;
import java.util.Arrays;

public class Colunas {

    // Chave para criptografia
    private static final String CHAVE = "aeds";
    private ArrayList <Integer> prioridadeColunas;

    /**
     * Construtor da Colunas
     */
    public Colunas() {
        gerarPrioridadeColunas();
    }

    /**
     * Metodo para criptografar um array de bytes, usando cifra de cesar.
     * @param registro - registro original para se ciptografar.
     * @return - registro criptografado.
     */
    public byte[] criptografar(byte[] registro) {

        int length = registro.length;
        byte[] bytesCriptografados = new byte[length];

        int colunas = CHAVE.length();
        int linhas = (int)Math.ceil((double)registro.length/(double)colunas);
        int ultimaColuna = (registro.length % colunas == 0) ? colunas : (registro.length % colunas) - 1;
        
        // Colunas para armazenar os bytes
        byte[][] cifra = new byte[linhas][colunas];

        // Preencher colunas
        int pos = 0;
        for(int i = 0; i < linhas; i++) {
            for(int j = 0; j < colunas; j++) {

                // Testar se posicao ainda e' valida
                if (pos < length) {
                    cifra[i][j] = registro[pos++];
                
                // Se registro acabar
                } else {
                    break;
                }
            }
        }

        // Alinhar matriz em um unico array
        int index = 0;
        for(int i = 0; i < colunas; i++) {

            int k = prioridadeColunas.get(i);

            // Ajustar se ultima linha existe
            int linhasCifra = linhas;
            if (k > ultimaColuna) {
                linhasCifra = linhas-1;
            }

            for(int j = 0; j < linhasCifra; j++) {
                bytesCriptografados[index++] = cifra[j][k];
            }
        }

        return bytesCriptografados;
    }

    private void gerarPrioridadeColunas() {

        prioridadeColunas = new ArrayList<>();

        char[] caracteres = CHAVE.toCharArray();
        Arrays.sort(caracteres);

        for(int i = 0; i < CHAVE.length(); i++) {
            int posicao = CHAVE.indexOf(caracteres[i]);
            prioridadeColunas.add(posicao);
        }
    }

    /**
     * Metodo para descriptografar o array de bytes, a partir da chave.
     * @param registroCriptografado - registro sob criptografia
     * @return - registro original.
     */
    public byte[] descriptografar(byte[] registroCriptografado) {

        int length = registroCriptografado.length;
        byte[] registro = new byte[length];

        int colunas = CHAVE.length();
        int linhas = (int)Math.ceil((double)registroCriptografado.length/(double)colunas);
        int ultimaColuna = (registro.length % colunas == 0) ? colunas : (registro.length % colunas) - 1;


        // Colunas para armazenar os bytes
        byte[][] cifra = new byte[linhas][colunas];

        // Converter array em matriz
        int index = 0;
        for(int i = 0; i < colunas; i++) {

            int k = prioridadeColunas.get(i);

            // Ajustar se ultima linha existe
            int linhasCifra = linhas;
            if (k > ultimaColuna) {
                linhasCifra = linhas-1;
            }

            for(int j = 0; j < linhasCifra; j++) {
                cifra[j][k] = registroCriptografado[index++];
            }
        }

        // Desfazer colunas
        int pos = 0;
        for(int i = 0; i < linhas; i++) {
            for(int j = 0; j < colunas; j++) {

                // Testar se posicao ainda e' valida
                if (pos < length) {
                    registro[pos++] = cifra[i][j];
                
                // Se registro acabar
                } else {
                    break;
                }
            }
        }

        return registro;

    }


}

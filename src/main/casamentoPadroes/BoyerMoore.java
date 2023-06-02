// Package
package casamentoPadroes;

// Bibliotecas
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

// Bibliotecas proprias
import app.Musica;
import casamentoPadroes.auxiliar.Contador;

public class BoyerMoore {

    // Arquivo de registro
    private static final String registroDB = "./src/resources/Registro.db";

    /**
     * Construtor padrao da classe BoyerMoore.
     */
    public BoyerMoore() {}

    /**
     * Metodo para procurar um padrao String nos registros de Musica
     *
     * @param padraoProcurado - string, contendo o padrao procurado.
     * @param numComparacoes  - contador para contabilizar o numero de comparacoes que o algoritmo realiza.
     * @param numOcorrencias  - contador para contabilizar o numero de ocorrencias do padrao que o algoritmo encontrar.
     * @param listID - ArrayList com os IDs encontrados durante a busca.
     */
    public void procurarPadrao(String padraoProcurado, Contador numComparacoes, Contador numOcorrencias, List<Integer> listID) {

        // Abrir arquivo para busca
        RandomAccessFile dbFile = null;

        try {
            dbFile = new RandomAccessFile(registroDB, "r");

            // Obter ultimo ID adicionado
            dbFile.seek(0);
            dbFile.readInt();
            long pontAtual = dbFile.getFilePointer();

            // Ler ate o fim dos registros
            while (pontAtual != dbFile.length()) {

                Musica musica = new Musica();

                // Ler informacoes do registro
                boolean lapide = dbFile.readBoolean();
                int tamRegistro = dbFile.readInt();

                if (lapide == false) {

                    // Trazer musica para memoria primaria
                    byte[] registro = new byte[tamRegistro];
                    dbFile.read(registro);
                    musica.fromByteArray(registro);

                    // Converter texto a ser procurado
                    String textoAtual = musica.musicaToString();

                    int posAtual = 0;
                    int ultimaPos = textoAtual.length() - (padraoProcurado.length() - 1);

                    // Percorrer ate a ultima posicao possivel de match
                    while (posAtual < ultimaPos) {
                        int i = padraoProcurado.length() - 1;
                        boolean find = true;

                        // Comparar caracteres de tras para frente
                        while (i >= 0 && find) {
                            // Contabilizar comparacoes
                            numComparacoes.cont++;

                            if (textoAtual.charAt(posAtual + i) != padraoProcurado.charAt(i)) {
                                find = false;
                                int shift = Math.max(1, i - badCharposicao(textoAtual.charAt(posAtual + i), padraoProcurado, i));
                                int suffixShift = sufixoPos(padraoProcurado, i);
                                posAtual += Math.max(shift, suffixShift);
                            }
                            i--;
                        }

                        // Testar se encontrou
                        if (find) {
                            numOcorrencias.cont++;
                            listID.add(musica.getId());
                            posAtual++;
                        }
                    }
                } else {
                    // Se nao for, pular o registro e reposicionar ponteiro
                    pontAtual = dbFile.getFilePointer();
                    long proximaPosicao = pontAtual + (long) tamRegistro;
                    dbFile.seek(proximaPosicao);
                }
                pontAtual = dbFile.getFilePointer();
            }

            // Fechar arquivos
            dbFile.close();

        } catch (FileNotFoundException e) {
            System.out.println("\nERRO: Registro vazio!" +
                    "\n      Tente carregar os dados iniciais primeiro!\n");
        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler o arquivo \"" + registroDB + "\"\n");
        }
    }

    /**
     * Metodo para obter o indice de ocorrencia do ultimo caractere (bad character) no padrao.
     *
     * @param caractere - caractere do texto atual a ser comparado.
     * @param padrao - padrao a ser procurado.
     * @param posPadrao  - indice atual do padrao.
     * @return o indice de ocorrencia do ultimo caractere no padrao ou -1 se nao houver ocorrencia.
     */
    private int badCharposicao(char caractere, String padrao, int posPadrao) {
        for (int i = posPadrao - 1; i >= 0; i--) {
            if (caractere == padrao.charAt(i))
                return i;
        }
        return -1;
    }

    /**
     * Metodo para obter o deslocamento baseado no "sufixo bom" no padrao.
     *
     * @param padrao - padrao a ser procurado.
     * @param posPadrao - indice atual do padrao.
     * @return o deslocamento a ser realizado no texto atual.
     */
    private int sufixoPos(String padrao, int posPadrao) {
        int padraoLen = padrao.length();
        int j = 0;
        for (int i = posPadrao - 1; i >= 0; i--) {
            if (isSufixo(padrao, i))
                j = padraoLen - i;
        }
        return j;
    }

    /**
     * Metodo auxiliar para verificar se uma string e um sufixo valido.
     *
     * @param padrao - padrao a ser procurado.
     * @param posicao - indice atual do padrao.
     * @return true se a string for um sufixo valido, caso contrario, false.
     */
    private boolean isSufixo(String padrao, int posicao) {
        int padraoLen = padrao.length();
        for (int i = 0; i <= posicao; i++) {
            if (padrao.charAt(i) != padrao.charAt(padraoLen - posicao + i - 1))
                return false;
        }
        return true;
    }
}

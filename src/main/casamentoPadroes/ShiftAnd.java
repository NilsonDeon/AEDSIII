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

public class ShiftAnd {

    // Arquivo de registro
    private static final String registroDB = "./src/resources/Registro.db";

    /**
     * Construtor padrao da classe ShiftAnd.
     */
    public ShiftAnd() {}

    /**
     * Metodo para procurar um padrao String nos registros de Musica.
     * @param padraoProcurado - string, contendo o padrao procurado.
     * @param numComparacoes - contador para contabilizar o numero de
     * comparacoes que o algoritmo realiza.
     * @param numOcorrencias - contador para contabilizar o numero de
     * ocorrencias do padrao que o algoritmo encontrar.
     * @param listID - arrayList com os IDs encontrados durante a busca.
     */
    public void procurarPadrao(String padraoProcurado, Contador numComparacoes, Contador numOcorrencias, List<Integer> listID) {

        // Converter padrao para vetor de bytes
        byte[] padrao = padraoProcurado.getBytes();

        // Inicializar vetor de shift
        int[] shift = new int[256];

        // Pre-processamento do padrao para calcular o vetor de shift
        for (int i = 0; i < padrao.length; i++) {
            shift[padrao[i] & 0xFF] |= (1 << i);
        }

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

                    // Converter texto a se procurar
                    byte[] textoAtual = musica.musicaToString().getBytes();

                    int posAtual = 0;
                    int ultimaPos = textoAtual.length - padrao.length;

                    // Percorrer ate a ultima posicao possivel de match
                    while (posAtual <= ultimaPos) {
                        int deslocamento = 0;
                        boolean find = true;

                        for (int i = padrao.length - 1; i >= 0 && find; i--) {
                            // Contabilizar comparacoes
                            numComparacoes.cont++;

                            // Se nao houve correspondencia, analisar deslocamento possivel
                            if ((textoAtual[posAtual + i] & 0xFF) != (padrao[i] & 0xFF)) {
                                deslocamento = Math.max(1, i - shift[textoAtual[posAtual + i] & 0xFF]);
                                find = false;
                            }
                        }

                        // Testar se encontrou
                        if (find) {
                            numOcorrencias.cont++;
                            listID.add(musica.getId());
                            posAtual++;
                        }

                        // Deslocar a posicao atual
                        posAtual += deslocamento;
                    }

                } else {
                    // Se nao for, pular o registro e reposicionar ponteiro
                    pontAtual = dbFile.getFilePointer();
                    long proximaPosicao = pontAtual + tamRegistro;
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

}

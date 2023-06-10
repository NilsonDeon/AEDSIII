// Package
package casamentoPadroes;

// Bibliotecas
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

// Bibliotecas próprias
import app.Musica;
import casamentoPadroes.auxiliar.Contador;

/**
 * Implementação do algoritmo de casamento de padrões Rabin-Karp.
 */
public class RabinKarp {

    private static final String registroDB = "./src/resources/Registro.db";

    // Valor primo para cálculo do hash
    private static final long PRIMO = 31L;

    // Tamanho máximo do valor hash suportado
    private static final long MAX_HASH_VALUE = Long.MAX_VALUE;

    /**
     * Construtor padrão da classe RabinKarp.
     */
    public RabinKarp() {
    }

    /**
     * Procura por um padrão em uma sequência de textos.
     *
     * @param padraoProcurado - O padrão a ser procurado.
     * @param numComparacoes - Contador para o número de comparações realizadas.
     * @param numOcorrencias - Contador para o número de ocorrências encontradas.
     * @param listID - Lista para armazenar os IDs das músicas encontradas.
     */
    public void procurarPadrao(String padraoProcurado, Contador numComparacoes, Contador numOcorrencias, List<Integer> listID) {

        // Calcular o hash do padrão a ser procurado
        long padraoHash = calcularHash(padraoProcurado);

        RandomAccessFile dbFile = null;

        try {
            dbFile = new RandomAccessFile(registroDB, "r");

            // Pular a primeira posição do arquivo que armazena a quantidade de registros
            dbFile.seek(0);
            dbFile.readInt();

            // Obter a posição atual do ponteiro
            long pontAtual = dbFile.getFilePointer();

            // Percorrer o arquivo de registros
            while (pontAtual != dbFile.length()) {

                Musica musica = new Musica();

                // Verificar se o registro está marcado como lápide
                boolean lapide = dbFile.readBoolean();
                int tamRegistro = dbFile.readInt();

                // Se não for lápide, ler o registro e converter para o objeto Musica
                if (!lapide) {

                    byte[] registro = new byte[tamRegistro];
                    dbFile.read(registro);
                    musica.fromByteArray(registro);

                    String textoAtual = musica.musicaToString();

                    // Verificar se o texto atual é maior ou igual ao padrão procurado
                    if (textoAtual.length() >= padraoProcurado.length()) {
                        // Calcular o hash do texto atual inicial, com o mesmo tamanho do padrão
                        long textoHash = calcularHash(textoAtual.substring(0, padraoProcurado.length()));

                        // Verificar se o hash do texto inicial é igual ao hash do padrão e se os dois são iguais
                        if (textoHash == padraoHash && textoAtual.substring(0, padraoProcurado.length()).equals(padraoProcurado)) {
                            // Incrementar o contador de ocorrências e adicionar o ID da música à lista
                            numOcorrencias.cont++;
                            listID.add(musica.getId());
                        }

                        // Percorrer o restante do texto atual
                        for (int i = padraoProcurado.length(); i < textoAtual.length(); i++) {
                            // Recalcular o hash a cada iteração
                            textoHash = recalculaHash(textoHash, textoAtual.charAt(i - padraoProcurado.length()), textoAtual.charAt(i),
                                    padraoProcurado.length());

                            // Incrementar o contador de comparações
                            numComparacoes.cont++;

                            // Verificar se o hash recalculado é igual ao hash do padrão e se os dois são iguais
                            if (textoHash == padraoHash && textoAtual.substring(i - padraoProcurado.length() + 1, i + 1).equals(padraoProcurado)) {
                                // Incrementar o contador de ocorrências e adicionar o ID da música à lista
                                numOcorrencias.cont++;
                                listID.add(musica.getId());
                            }
                        }
                    }
                } else {
                    // Se for lápide, pular para a próxima posição de registro
                    pontAtual = dbFile.getFilePointer();
                    long proximaPosicao = pontAtual + (long) tamRegistro;
                    dbFile.seek(proximaPosicao);
                }
                pontAtual = dbFile.getFilePointer();
            }

            dbFile.close();

        } catch (FileNotFoundException e) {
            System.out.println("\nERRO: Registro vazio!" +
                    "\n      Tente carregar os dados iniciais primeiro!\n");
        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler o arquivo \"" + registroDB + "\"\n");
        }
    }

    /**
     * Calcula o hash de uma string.
     *
     * @param str - A string para a qual o hash será calculado.
     * @return - O valor hash calculado.
     */
    private long calcularHash(String str) {
        long hash = 0;
        long potencia = 1;

        for (int i = 0; i < str.length(); i++) {
            hash = (hash * PRIMO + (str.charAt(i) - 'a' + 1)) % MAX_HASH_VALUE;
            potencia = (potencia * PRIMO) % MAX_HASH_VALUE;
        }

        return hash;
    }

    /**
     * Recalcula o hash durante a busca.
     *
     * @param oldHash - O valor hash anterior.
     * @param charAntigo - O caractere antigo que será removido.
     * @param novoChar - O novo caractere que será adicionado.
     * @param patternLength - O tamanho do padrão.
     * @return - O novo valor hash calculado.
     */
    private long recalculaHash(long oldHash, char charAntigo, char novoChar, int patternLength) {
        long novoHash = (oldHash - (charAntigo - 'a' + 1) * calcularPotencia(PRIMO, patternLength - 1)) % MAX_HASH_VALUE;
        novoHash = (novoHash * PRIMO + (novoChar - 'a' + 1)) % MAX_HASH_VALUE;

        return novoHash;
    }

    private long calcularPotencia(long base, int expoente) {
        long resultado = 1;
        for (int i = 0; i < expoente; i++) {
            resultado = (resultado * base) % Integer.MAX_VALUE;
        }
        return resultado;
    }

}

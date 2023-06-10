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

    // Valor primo para calculo do hash
    private static final long PRIMO = 31L;

    // Tamanho maximo do valor hash suportado
    private static final long MAX_HASH_VALUE = Long.MAX_VALUE;

    /**
     * Construtor padrão da classe RabinKarp.
     */
    public RabinKarp() {}

    /**
     * Metodo para procurar um padrao String nos registros de Musica
     * @param padraoProcurado - string, contendo o padrao procurado.
     * @param numComparacoes  - contador para contabilizar o numero de comparacoes que o algoritmo realiza.
     * @param numOcorrencias  - contador para contabilizar o numero de ocorrencias do padrao que o algoritmo encontrar.
     * @param listID - ArrayList com as musicas encontrados durante a busca.
     */
    public void procurarPadrao(String padraoProcurado, Contador numComparacoes, Contador numOcorrencias, List<Musica> listMusic) {

        // Calcular o hash do padrao a ser procurado
        long padraoHash = calcularHash(padraoProcurado);

        // Abrir arquivo para busca
        RandomAccessFile dbFile = null;

        try {
            dbFile = new RandomAccessFile(registroDB, "r");

            // Pular a primeira posicao do arquivo que armazena a quantidade de registros
            dbFile.seek(0);
            dbFile.readInt();

            // Obter a posicao atual do ponteiro
            long pontAtual = dbFile.getFilePointer();

            // Percorrer o arquivo de registros
            while (pontAtual != dbFile.length()) {

                Musica musica = new Musica();

                // Verificar se o registro esta' marcado como lápide
                boolean lapide = dbFile.readBoolean();
                int tamRegistro = dbFile.readInt();

                // Se nao for lapide, ler o registro e converter para o objeto Musica
                if (!lapide) {

                    byte[] registro = new byte[tamRegistro];
                    dbFile.read(registro);
                    musica.fromByteArray(registro);

                    String textoAtual = musica.musicaToString();

                    // Verificar se o texto atual e' maior ou igual ao padrao procurado
                    if (textoAtual.length() >= padraoProcurado.length()) {

                        // Calcular o hash do texto atual inicial, com o mesmo tamanho do padrao
                        long textoHash = calcularHash(textoAtual.substring(0, padraoProcurado.length()));

                        // Incrementar o contador de comparacoes entre hash
                        numComparacoes.cont++;

                        // Verificar se o hash do texto inicial e' igual ao hash do padrao
                        if (textoHash == padraoHash){
                            
                            // Incrementar o contador de comparacoes entre caracteres
                            numComparacoes.cont++;

                            // Se hash igual, analisar possivel match
                            if(textoAtual.substring(0, padraoProcurado.length()).equals(padraoProcurado)) {

                                    // Incrementar o contador de ocorrencias
                                    numOcorrencias.cont++;

                                    // Adicionar 'a lista se nao existir ainda
                                    if(! listMusic.contains(musica)) {
                                        listMusic.add(musica);
                                    }

                            }
                        }

                        // Percorrer o restante do texto atual
                        for (int i = padraoProcurado.length(); i < textoAtual.length(); i++) {

                            // Recalcular o hash a cada iteracao
                            textoHash = recalcularHash(textoHash, textoAtual.charAt(i - padraoProcurado.length()), textoAtual.charAt(i), padraoProcurado.length());

                            // Incrementar o contador de comparacoes entre hash
                            numComparacoes.cont++;

                            // Verificar se o hash do texto inicial e' igual ao hash do padrao
                            if (textoHash == padraoHash) {

                            // Incrementar o contador de comparacoes
                            numComparacoes.cont++;
                                
                               // Se hash igual, analisar possivel match
                                if(textoAtual.substring(i - padraoProcurado.length() + 1, i + 1).equals(padraoProcurado)) {
                                
                                    // Incrementar o contador de ocorrencias
                                    numOcorrencias.cont++;

                                    // Adicionar 'a lista se nao existir ainda
                                    if(! listMusic.contains(musica)) {
                                        listMusic.add(musica);
                                    }

                                }
                            }
                        }
                    }
                } else {

                    // Se for lapide, pular para a proxima posicao de registro
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
     * Calcula o hash de uma string, de forma a minimizar o numero de colisoes
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
     * Recalcular o hash durante a busca, reaproveitando hash anterior.
     * @param oldHash - O valor hash anterior.
     * @param charAntigo - O caractere antigo que será removido.
     * @param novoChar - O novo caractere que será adicionado.
     * @param patternLength - O tamanho do padrão.
     * @return - O novo valor hash calculado.
     */
    private long recalcularHash(long oldHash, char charAntigo, char novoChar, int patternLength) {
        long novoHash = (oldHash - (charAntigo - 'a' + 1) * calcularPotencia(PRIMO, patternLength - 1)) % MAX_HASH_VALUE;
        novoHash = (novoHash * PRIMO + (novoChar - 'a' + 1)) % MAX_HASH_VALUE;

        return novoHash;
    }

    /**
     * Calcula a potencia para auxiliar na obtencao do novo hash.
     * @param base - base da potencia a se calcular.
     * @param expoente - expoente da potencia a se calcular.
     * @return - potencia do hash.
     */
    private long calcularPotencia(long base, int expoente) {
        long resultado = 1;
        for (int i = 0; i < expoente; i++) {
            resultado = (resultado * base) % Integer.MAX_VALUE;
        }
        return resultado;
    }
}

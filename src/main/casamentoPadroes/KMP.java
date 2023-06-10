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

public class KMP {

    // Arquivo de registro
    private static final String registroDB = "./src/resources/Registro.db";

    // Funcao de prefixo basico
    private int prefixoBasico[];

    /**
     * Construtor padrao da classe KMP.
     */
    public KMP() {}

    /**
     * Metodo para procurar um padrao String nos registros de Musica
     * @param padraoProcurado - string, contendo o padrao procurado.
     * @param numComparacoes  - contador para contabilizar o numero de comparacoes que o algoritmo realiza.
     * @param numOcorrencias  - contador para contabilizar o numero de ocorrencias do padrao que o algoritmo encontrar.
     * @param listID - ArrayList com as musicas encontrados durante a busca.
     */
    public void procurarPadrao(String padraoProcurado, Contador numComparacoes, Contador numOcorrencias, List<Musica> listMusic) {

        // Montar tabela hash de prefixo basico;
        montarPrefixoBasico(padraoProcurado);

        // Abrir arquivo para busca
        RandomAccessFile dbFile = null;

        try {
            dbFile = new RandomAccessFile(registroDB, "r");

            // Obter ultimo ID adicionado
            dbFile.seek(0);
            dbFile.readInt();
            long pontAtual = dbFile.getFilePointer();

            // Ler ate fim dos registros
            while (pontAtual != dbFile.length()) {

                Musica musica = new Musica();

                // Ler informacoes do registro
                boolean lapide = dbFile.readBoolean();
                int tamRegistro = dbFile.readInt();

                if(lapide == false) {

                    // Trazer musica para memoria primaria
                    byte[] registro = new byte[tamRegistro];
                    dbFile.read(registro);
                    musica.fromByteArray(registro);

                    // Converter texto a se procurar
                    String textoAtual = musica.musicaToString();

                    int posAtual = 0;
                    int posBak = 0;
                    int ultimaPos = textoAtual.length() - (padraoProcurado.length()-1);

                    // Percorrer ate' ultima posicao possivel de match
                    while(posAtual < ultimaPos) {

                        posBak = posAtual+1;
                        
                        int deslocamento = 0;
                        boolean find = true;

                        for(int i = 0; i < padraoProcurado.length() && find; i++){

                            // Contabilizar comparacoes
                            numComparacoes.cont++;

                            // Se nao houve correspondencia, analisar deslocamento possivel
                            if (textoAtual.charAt(posAtual++) != padraoProcurado.charAt(i)) {
                                posAtual--;
                                deslocamento = i - prefixoBasico[i];
                                find = false;
                            }
                        }

                        // Testar se encontrou
                        if (find) {
                            posAtual = posBak;
                            numOcorrencias.cont++;

                            // Adicionar 'a lista se nao existir ainda
                            if(! listMusic.contains(musica)) {
                                listMusic.add(musica);
                            }
                                                    
                        // Se nao encontrou, deslocar
                        } else {
                            posAtual = posAtual + deslocamento;
                        }
                    }
                
                // Se nao for, pular o registro e reposicionar ponteiro
                } else {
                    pontAtual = dbFile.getFilePointer();
                    long proximaPosicao = pontAtual + (long)tamRegistro;
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
     * Metodo para obter a funcao de prefixo basico para o deslocamento ao
     * encontrar caractere errado no KMP.
     * @param padraoProcurado
     */
    private void montarPrefixoBasico(String padraoProcurado) {

        // Settar array
        prefixoBasico = new int[padraoProcurado.length()];

        // Duas primeiras posicoes, por definicao, -1 e 0
        if (padraoProcurado.length() > 0) prefixoBasico[0] = -1;
        if (padraoProcurado.length() > 1) prefixoBasico[1] = 0;

        // Adicionar valor para outras posicoes
        for(int i = 2; i < padraoProcurado.length(); i++) {

            char charAnterior = padraoProcurado.charAt(i-1);

            // Obter proxima posicao a ser analisada
            int pos = i-2;

            // Settar default para posicao atual
            prefixoBasico[i] = prefixoBasico[i-1] + 1;

            // Repetir ate' nao ser necessario mais a busca
            boolean stop = false;
            while (!stop) {

                // Tentar encontrar padrao repetido
                while(pos >= 0 && charAnterior != padraoProcurado.charAt(pos)) {
                    prefixoBasico[i] = pos;
                    pos--;
                }

                // Testar se encontrou de fato
                if (pos > 0) {
                    int newPos = pos;
                    int dif = i-1;;

                    // Verificar se o padrao se mantem aparecendo
                    while (newPos > 0 && padraoProcurado.charAt(newPos--) == padraoProcurado.charAt(dif--));

                    // Testar se ultimo prefixo a se analisar e' o prefixo do padrao
                    if (newPos == 0 && padraoProcurado.charAt(newPos) == padraoProcurado.charAt(dif)) {
                        stop = true;
                    
                    // Se nao for, resetar busca
                    } else {
                        prefixoBasico[i] = 0;
                    }

                // Marcar como fim se tiver chegado 'a ultima comparacao
                } else {
                    stop = true;
                }

                pos--;
            }

        }

    }


}
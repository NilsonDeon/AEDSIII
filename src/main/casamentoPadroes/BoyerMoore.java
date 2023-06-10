// Package
package casamentoPadroes;

// Bibliotecas
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

// Bibliotecas proprias
import app.Musica;
import casamentoPadroes.auxiliar.Contador;

public class BoyerMoore {

    // Arquivo de registro
    private static final String registroDB = "./src/resources/Registro.db";

    // Funcoes deslocamento
    private HashMap<Character, Integer> caractereRuim;
    private int sufixoBom[];
    

    /**
     * Construtor padrao da classe BoyerMoore.
     */
    public BoyerMoore() {}

    /**
     * Metodo para procurar um padrao String nos registros de Musica
     * @param padraoProcurado - string, contendo o padrao procurado.
     * @param numComparacoes  - contador para contabilizar o numero de comparacoes que o algoritmo realiza.
     * @param numOcorrencias  - contador para contabilizar o numero de ocorrencias do padrao que o algoritmo encontrar.
     * @param listID - ArrayList com as musicas encontrados durante a busca.
     */
    public void procurarPadrao(String padraoProcurado, Contador numComparacoes, Contador numOcorrencias, List<Musica> listMusic) {

        // Montar funcoes para deslocamento
        montarBadChar(padraoProcurado);
        montarGoodSuffix(padraoProcurado);

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

                            char charAtual = textoAtual.charAt(posAtual + i);

                            if (charAtual != padraoProcurado.charAt(i)) {
                                find = false;

                                // Calcular deslocamentos
                                int badChar = obterBadChar(charAtual, padraoProcurado);
                                int goodSuffix = sufixoBom[i];
                                
                                posAtual += Math.max(badChar, goodSuffix);
                            }
                            i--;
                        }

                        // Testar se encontrou
                        if (find) {
                            numOcorrencias.cont++;
                            posAtual++;

                            // Adicionar 'a lista se nao existir ainda
                            if(! listMusic.contains(musica)) {
                                listMusic.add(musica);
                            }
                            
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
     * Metodo para calcular o hash de ocorrencia do ultimo caractere 
     * (bad character) no padrao.
     * @param padrao - padrao a ser procurado.
     */
    private void montarBadChar(String padrao) {

        caractereRuim = new HashMap<>();

        for (int i = (padrao.length()-1) - 1; i >= 0; i--) {

            if(!caractereRuim.containsKey(padrao.charAt(i))) {
                caractereRuim.put(padrao.charAt(i), i);
            }
        }
    }

    /**
     * Metodo para calcular o indice de sufixo bom no padrao.
     * @param padrao - padrao a ser procurado.
     */
    private void montarGoodSuffix(String padrao) {

        // Settar array
        sufixoBom = new int[padrao.length()];
        Arrays.fill(sufixoBom, -1);

        // Por definicao, ultima posicao e' igual a 1
        if(padrao.length()>0) sufixoBom[padrao.length()-1] = 1;

        // Percorrer padrao de tras para frente
        for (int i = (padrao.length()-1) - 1; i >= 0; i--) {

            // Verificar da posicao atual ate' a primeira
            for(int j = i; j >0; j--) {
                
                // Variaveis de controle do sufixo analisado
                String sufixo = padrao.substring(i+1);
                int sufixoLen = sufixo.length();
                char prefixoAtual = padrao.charAt(i);

                // Testar se sufixo aparece antes com caractere anterior e' diferente
                if(padrao.substring(j, j+sufixoLen).equals(sufixo) &&
                   prefixoAtual != padrao.charAt(j-1)) {
                    
                    sufixoBom[i] = i-(j+1);
                }
            }

            // Se sufixo nao aparecer com caractere diferente antes
            // Verificar da posicao atual ate' a primeira
            int j = i;
            while(j < padrao.length() && sufixoBom[i] == -1) {
                
                // Variaveis de controle do sufixo analisado
                String sufixo = padrao.substring(j+1);
                int sufixoLen = sufixo.length();

                // Testar se parte dele (ou tudo) e' prefixo do padrao
                if(padrao.substring(0, sufixoLen).equals(sufixo)) {

                    sufixoBom[i] = j+1;
                }

                j++;
            }
        }
    }

    private int obterBadChar(char charAtual, String padrao) {

        // Obter resultado do hash
        Integer badChar = caractereRuim.get(charAtual);
        if(badChar == null) badChar = -1;

        // Calcular deslocamento
        return (padrao.length()-1) - badChar;
    }


}

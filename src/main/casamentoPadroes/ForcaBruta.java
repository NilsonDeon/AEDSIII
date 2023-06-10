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

public class ForcaBruta {

    // Arquivo de registro
    private static final String registroDB = "./src/resources/Registro.db";

    /**
     * Construtor padrao da classe ForcaBruta.
     */
    public ForcaBruta() {}
    
    /**
     * Metodo para procurar um padrao String nos registros de Musica
     * @param padraoProcurado - string, contendo o padrao procurado.
     * @param numComparacoes  - contador para contabilizar o numero de comparacoes que o algoritmo realiza.
     * @param numOcorrencias  - contador para contabilizar o numero de ocorrencias do padrao que o algoritmo encontrar.
     * @param listID - ArrayList com as musicas encontrados durante a busca.
     */
    public void procurarPadrao(String padraoProcurado, Contador numComparacoes, Contador numOcorrencias, List<Musica> listMusic) {

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
                        
                        boolean find = true;

                        for(int i = 0; i < padraoProcurado.length() && find; i++){

                            // Contabilizar comparacoes
                            numComparacoes.cont++;

                            // Se nao houve correspondencia, quebrar loop e tentar novamente
                            if (textoAtual.charAt(posAtual++) != padraoProcurado.charAt(i)) {
                                find = false;
                            }
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

                        // Mover sempre uma posicao
                        posAtual = posBak;
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
}
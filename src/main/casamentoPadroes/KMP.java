// Package
package casamentoPadroes;

import java.io.FileNotFoundException;
import java.io.IOException;
// Bibliotecas
import java.io.RandomAccessFile;

// Bibliotecas proprias
import app.Musica;

public class KMP {

    // Arquivo de registro
    private static final String registroDB = "./src/resources/Registro.db";

    // Funcao de prefixo basico
    private int prefixoBasico[];

    /**
     * Construtor padrao da classe KMP.
     */
    public KMP() {}

    public int procurarPadrao (String padraoProcurado) {

        // Montar tabela hash de prefixo basico;
        montarPrefixoBasico(padraoProcurado);

        System.out.print("Prefixo basico: ");
        for(int i = 0; i < prefixoBasico.length; i++) {
            System.out.print(prefixoBasico[i] + " ");
        }
        System.out.println();

        // Contar comparacoes
        int numComparacoes = 0;

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
                    String textoAtual = musicaToString(musica);
                    System.out.println("textoAtual.length(): " + textoAtual.length()+"\n");

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
                            numComparacoes++;

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
                            System.out.println(musica + "\n");
                        
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

        System.out.println("\nnumComparacoes: " + numComparacoes);

        return numComparacoes;
    }

    /*
    public int procurarPadrao (Musica[] musicas, String padraoProcurado) {

        // Montar tabela hash de prefixo basico;
        montarPrefixoBasico(padraoProcurado);

        // Contar comparacoes
        int numComparacoes = 0;

        for(int k = 0; k < musicas.length; k++) {

            // Carregar primeiro texto a se procurar
            String textoAtual = musicaToString(musicas[k]);

            int posAtual = 0;
            int ultimaPos = textoAtual.length() - (padraoProcurado.length()-1);

            // Percorrer ate' ultima posicao possivel de match
            while(posAtual <= ultimaPos) {
                
                int deslocamento = 0;
                boolean find = true;

                for(int i = 0; i < padraoProcurado.length() && find; i++){

                    // Contabilizar comparacoes
                    numComparacoes++;

                    // Se nao houve correspondencia, analisar deslocamento possivel
                    if (textoAtual.charAt(posAtual++) != padraoProcurado.charAt(i)) {
                        deslocamento = i - prefixoBasico[i];
                        find = false;
                    }
                }

                // Testar se encontrou
                if (find) {
                    posAtual++;
                    System.out.println("\nfind\n");
                
                // Se nao encontrou, deslocar
                } else {
                    posAtual = posAtual + deslocamento;
                }
            }
        
        }

        return numComparacoes;

    }
    */

    private String musicaToString(Musica musica) {

        String listaImagens = "";
        for(String img : musica.getImagens()) {
            listaImagens += img + " ";
        }

        String pais = musica.getPais()[0] + musica.getPais()[1] + " ";

        return musica.getId() + " " +
               musica.getNome() + " " +
               musica.getArtistas() + " " +
               musica.getNomeAlbum() + " " + 
               listaImagens + 
               pais +
               musica.mostrarDataLancamento() + " " +
               musica.getDancabilidade() + " " +
               musica.getDuracao() + " " +
               musica.getVivacidade() + " " +
               musica.getPopularidade() + " " +
               musica.getPopularidade() + " " +
               musica.getUri();
    }

    private void montarPrefixoBasico(String padraoProcurado) {

        // Settar array
        prefixoBasico = new int[padraoProcurado.length()];

        // Duas primeiras posicoes, por definicao, -1 e 0
        prefixoBasico[0] = -1;
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
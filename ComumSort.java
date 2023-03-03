// bibliotecas
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

/**
 * ComumSort - Classe responsavel por realizar a Intercalcao Balanceada Comum.
 */
public class ComumSort {

    private static final String registroDB = "Registro.db";
    private static final int NUM_REGISTROS = 1500;
    private static final int NUM_CAMINHOS = 4;

    /**
     * Construtor padrao da classe ComumSort.
     */
    public ComumSort(){}

    /**
     * Metodo principal de ordenacao, no qual a distribuicao e as intercalacoes
     * sao chamadas.
     * @throws IOException Caso haja erro de leitura ou escrita com os arquivos.
     */
    public void ordenar() throws IOException {

        boolean paridade = true;
        int numIntercalacao = 1;
        int numArquivos = 0;

        distribuicao();
        while (numArquivos != 1) {
            numArquivos = intercalacao(numIntercalacao, paridade);
            paridade = !paridade;
            numIntercalacao++;
        }

        // Apagar antigo "Registros.db"
        File antigoDB = new File("Registro.db");
        antigoDB.delete();

        // Renomear arquivo novo
        File novoArquivo = new File("Registro.db");
        File antigoArquivo = null;
        if (paridade == true) antigoArquivo = new File("arqTemp0.db");
        else antigoArquivo = new File("arqTemp4.db");
        antigoArquivo.renameTo(novoArquivo);

        // Apagar arquivos temporarios
        for (int i = 0; i < NUM_CAMINHOS*2; i++) {
            File file = new File("arqTemp" + i + ".db");
            file.delete();
        }

        System.out.println("\nArquivo \"" + registroDB + "\" ordenado com sucesso!");
    }

    /**
     * Metodo privado da ordenacao, representando a primeira fase da Ordenacao
     * Externa para distribuir o arquivo principal em NUM_CAMINHOS * arquivos, 
     * contendo cada um NUM_REGISTROS * registros.
     * @throws IOException Caso haja erro de leitura ou escrita com os arquivos.
     */
    private void distribuicao() throws IOException {

        RandomAccessFile arqTemp = null;
        RandomAccessFile dbFile = null;

        try {
            dbFile = new RandomAccessFile (registroDB, "r");

            // Se tiver registro cadastrado
            if (dbFile.length() > 0) {

                // Ler cabecalho do arquivo
                dbFile.seek(0);
                int ultimoId = dbFile.readInt();
                long posicaoAtual = dbFile.getFilePointer();

                // Criar os 4 primeiros arquivos temporarios
                for (int i = 0; i < NUM_CAMINHOS*2; i++) {
                   try {
                        arqTemp = new RandomAccessFile("arqTemp"+ i +".db", "rw");

                        // Ao terminar a ordenacao, SEMPRE o arquivo ordenado
                        // sera' o primeiro a ser criado, ou seja, "arqTemp0.db"
                        // ou "arqTemp4.db". Entao, deve-se ja salvar o ultimoID
                        if (i == 0 || i == 4) {
                            Musica auxMus = new Musica();
                            byte[] salvarID = auxMus.intToByteArray(ultimoId);
                            arqTemp.write(salvarID);
                        }
                        arqTemp.close();
                
                    } catch (IOException e) {
                        System.out.println("\nERRO: Ocorreu um erro de escrita no" +
                                           "arquivo \"" + registroDB + "\"\n");
                    }
                }

                Musica[] musicas;

                while (dbFile.length() != posicaoAtual) {

                    // Percorrer cada um dos caminhos de arquivo
                    for(int k = 0; k < NUM_CAMINHOS; k++){
                        int posArray = 0;
                        musicas = new Musica[NUM_REGISTROS];

                        // Condicao de parada sera' fim de arquivo ou
                        // ter trazido para a memoria primaria os NUM_REGISTROS
                        while (dbFile.length() != posicaoAtual && posArray < NUM_REGISTROS) {
                            boolean lapide = dbFile.readBoolean();
                            int tamRegistro = dbFile.readInt();

                            // Ler e salvar apenas se nao tiver lapide
                            if (lapide == false){
                                byte[] registro = new byte[tamRegistro];
                                dbFile.read(registro);
                                musicas[posArray] = new Musica();
                                musicas[posArray].fromByteArray(registro);
                                posArray++;
                            } else {
                                posicaoAtual = dbFile.getFilePointer();
                                long proximaPosicao = posicaoAtual + (long)tamRegistro;
                                dbFile.seek(proximaPosicao);
                            }
                            posicaoAtual = dbFile.getFilePointer();
                        }

                        // Ordenar os registros em memoria principal
                        QuickSort quick = new QuickSort(posArray);
                        quick.quicksort(musicas);

                        // Salvar no novo arquivo de modo os registros ordenados
                        for (int i = 0; i < posArray; i++) {
                            byte[] bytes = musicas[i].toByteArray();
                            RandomAccessFile temp = null;
                            try{
                                temp = new RandomAccessFile ("arqTemp" + k + ".db", "rw");
                                temp.seek(temp.length());
                                temp.write(bytes);

                            }catch(Exception e) {
                                System.out.println("\nERRO: Ocorreu um erro na escrita do " +
                                "arquivo \"" + "arqTemp\"" + k + ".db -> " + e +"\n");
                            }finally{
                                temp.close();
                            }
                        }
                    }
                }

            } else {
            System.out.println("\nERRO: Registro vazio!" +
                               "\n      Tente carregar os dados iniciais primeiro!\n");
            }
       } catch (FileNotFoundException e) {
                System.out.println("\nERRO: Registro nao encontrado!" +
                                   "\n      Tente carregar os dados iniciais primeiro!\n");
       } finally {
            if (dbFile != null) dbFile.close();
       }
    }

    /**
     * Metodo para realizar a intercalacao propriamente dita.
     * @param numIntercalacao - contador para indicar qual a intercalacao esta'
     * sendo feita (primeira, segunda, terceira, ...)
     * @param paridade - indicador para saber se e' uma intercalacao par ou
     * impar, implicando em qual arquivo sera' leitura e qual, escrita
     * @return numArquivos - numero de arquivos que foram criados.
     * @throws IOException Caso haja erro de leitura ou escrita com os arquivos.
     */
    public int intercalacao (int numIntercalacao, boolean paridade) throws IOException {

        RandomAccessFile arqTemp0 = null;
        RandomAccessFile arqTemp1 = null;
        RandomAccessFile arqTemp2 = null;
        RandomAccessFile arqTemp3 = null;
        RandomAccessFile newTemp = null;

        File file;
        int numArquivos = 0;

        try {
            int ultimoId, k, aux;

            // Abrir arquivos temporarios
            // A paridade ira' controlar qual registro sera' lido e em qual
            // sera' gravado
            if (paridade == true) {
                arqTemp0 = new RandomAccessFile ("arqTemp0.db", "r");
                arqTemp1 = new RandomAccessFile ("arqTemp1.db", "r");
                arqTemp2 = new RandomAccessFile ("arqTemp2.db", "r");
                arqTemp3 = new RandomAccessFile ("arqTemp3.db", "r");

                // Para evitar sobrescrever, deletar arquivo antigo
                file = new File ("arqTemp4.db"); file.delete();
                file = new File ("arqTemp5.db"); file.delete();
                file = new File ("arqTemp6.db"); file.delete();
                file = new File ("arqTemp7.db"); file.delete();

                // Garantir, mais na frente, criacao dos arquivos certos para 
                // a escrita
                k = NUM_CAMINHOS;
                aux = NUM_CAMINHOS;

            } else {
                arqTemp0 = new RandomAccessFile ("arqTemp4.db", "r");
                arqTemp1 = new RandomAccessFile ("arqTemp5.db", "r");
                arqTemp2 = new RandomAccessFile ("arqTemp6.db", "r");
                arqTemp3 = new RandomAccessFile ("arqTemp7.db", "r");

                file = new File ("arqTemp0.db"); file.delete();
                file = new File ("arqTemp1.db"); file.delete();
                file = new File ("arqTemp2.db"); file.delete();
                file = new File ("arqTemp3.db"); file.delete();

                k = 0;
                aux = 0;
            }

            long tamArq0 = arqTemp0.length();
            long tamArq1 = arqTemp1.length();
            long tamArq2 = arqTemp2.length();
            long tamArq3 = arqTemp3.length();

            // Testar se todos os arquivos existem
            if (tamArq0 > 0 && tamArq1 > 0 && tamArq2 > 0 && tamArq3 > 0) {

                // Controle se intercalacao acabou
                boolean quatroArquivosCompletos = false;
                boolean intercalacaoCompleta = false;

                // Posicionar e salvar ponteiros
                // Ler o ultimoID escrito no arquivo
                arqTemp0.seek(0); ultimoId = arqTemp0.readInt();
                arqTemp1.seek(0);
                arqTemp2.seek(0);
                arqTemp3.seek(0);
                long posAtual0 = arqTemp0.getFilePointer();
                long posAtual1 = arqTemp1.getFilePointer();
                long posAtual2 = arqTemp2.getFilePointer();
                long posAtual3 = arqTemp3.getFilePointer();

                // Variaveis de controle para cada arquivo
                // Contador para indicar quantos registros ja foram lidos
                int cont0 = 0;
                int cont1 = 0;
                int cont2 = 0;
                int cont3 = 0;
                // Booleano para indicar se o arquivo ainda esta' valido
                boolean arq0_OK = true;
                boolean arq1_OK = true;
                boolean arq2_OK = true;
                boolean arq3_OK = true;

                // Garantir que a primeira leitura passe por todos os arquivos
                boolean carregamentoInicial = true;

                Musica mus0 = new Musica();
                Musica mus1 = new Musica();
                Musica mus2 = new Musica();
                Musica mus3 = new Musica();

                Musica menorMusica = null;

                // Escrever ultimoID no primeiro arquivo
                newTemp = new RandomAccessFile ("arqTemp" + k + ".db", "rw");
                Musica auxMus = new Musica();
                byte[] salvarID = auxMus.intToByteArray(ultimoId);
                newTemp.write(salvarID);
                if (newTemp != null) newTemp.close();


                while (tamArq0 != posAtual0 && tamArq1 != posAtual0 && 
                       tamArq2 != posAtual0 && tamArq3 != posAtual0) {

                    while(k < (NUM_CAMINHOS + aux)){

                        // Settar variaveis de controle
                        carregamentoInicial = true;
                        quatroArquivosCompletos = false;
                        cont0 = cont1 = cont2 = cont3 = 0;
                        arq0_OK = arq1_OK = arq2_OK = arq3_OK = true;

                        while (quatroArquivosCompletos == false) {
                            
                            // Testar se deve passar para proxima musica do arquivo
                            if (menorMusica == mus0 || carregamentoInicial) {

                                // Testar se todos os arquivos estao validos para serem lidos
                                if (testarSeTemRegistro(posAtual0, tamArq0, cont0, numIntercalacao) == true) {                                   
                                    boolean lapide0 = arqTemp0.readBoolean();
                                    int tamRegistro0 = arqTemp0.readInt();

                                    byte[] registro0 = new byte[tamRegistro0];
                                    arqTemp0.read(registro0);
                                    mus0.fromByteArray(registro0);

                                    posAtual0 = arqTemp0.getFilePointer();
                                    cont0++;

                                } else {
                                    arq0_OK = false;
                                }
                            }

                            if (menorMusica == mus1 || carregamentoInicial) {

                                // Testar se todos os arquivos estao validos para serem lidos
                                if (testarSeTemRegistro(posAtual1, tamArq1, cont1, numIntercalacao) == true) {
                                    boolean lapide1 = arqTemp1.readBoolean();
                                    int tamRegistro1 = arqTemp1.readInt();

                                    byte[] registro1 = new byte[tamRegistro1];
                                    arqTemp1.read(registro1);
                                    mus1.fromByteArray(registro1);

                                    posAtual1 = arqTemp1.getFilePointer();
                                    cont1++;

                                } else {
                                    arq1_OK = false;
                                }
                            }

                            if (menorMusica == mus2 || carregamentoInicial) {

                                // Testar se todos os arquivos estao validos para serem lidos
                                if (testarSeTemRegistro(posAtual2, tamArq2, cont2, numIntercalacao) == true) {
                                    boolean lapide2 = arqTemp2.readBoolean();
                                    int tamRegistro2 = arqTemp2.readInt();

                                    byte[] registro2 = new byte[tamRegistro2];
                                    arqTemp2.read(registro2);
                                    mus2.fromByteArray(registro2);

                                    posAtual2 = arqTemp2.getFilePointer();
                                    cont2++;

                                } else {
                                    arq2_OK = false;
                                }
                            }

                            if (menorMusica == mus3 || carregamentoInicial) {

                                // Testar se todos os arquivos estao validos para serem lidos
                                if (testarSeTemRegistro(posAtual3, tamArq3, cont3, numIntercalacao) == true) {
                                    boolean lapide3 = arqTemp3.readBoolean();
                                    int tamRegistro3 = arqTemp3.readInt();

                                    byte[] registro3 = new byte[tamRegistro3];
                                    arqTemp3.read(registro3);
                                    mus3.fromByteArray(registro3);

                                    posAtual3 = arqTemp3.getFilePointer();
                                    cont3++;

                                } else {
                                    arq3_OK = false;
                                }
                            }

                            carregamentoInicial = false;
                            menorMusica = getMaiorId(mus0, mus1, mus2, mus3, arq0_OK, arq1_OK, arq2_OK, arq3_OK);

                            if (menorMusica != null) {

                                // Escrever menor musica
                                newTemp = new RandomAccessFile ("arqTemp" + k + ".db", "rw");
                                newTemp.seek(newTemp.length());
                                byte[] bytes = menorMusica.toByteArray();
                                newTemp.write(bytes);

                                // Contabilizar numero de arquivos criados
                                numArquivos = k+1;

                            } else {
                                quatroArquivosCompletos = true;
                            }
                        }

                        if (newTemp != null) newTemp.close();
                        k++;
                    }
                }

            } else {
               System.out.println("\nERRO: Arquivos temporarios estao vazios\n");
            }
        } catch (FileNotFoundException e) {
                System.out.println("\nERRO: Arquivos temporarios nao encontrados\n");
        } finally {
            if (arqTemp0 != null) arqTemp0.close();
            if (arqTemp1 != null) arqTemp1.close();
            if (arqTemp2 != null) arqTemp2.close();
            if (arqTemp3 != null) arqTemp3.close();

            // Corrigir valor, do contador do numero de arquivos criados
            if (numArquivos > NUM_CAMINHOS) numArquivos-= NUM_CAMINHOS;

            return numArquivos;
       } 
    }

    /**
     * Metodo para obter a música com o maior ID entre 4 músicas.
     * @param mus0 - primeira musica a se comparar
     * @param mus1 - segunda musica a se comparar
     * @param mus2 - terceira musica a se comparar
     * @param mus3 - quarta musica a se comparar
     * @return maiorMusica
     */
    private Musica getMaiorId (Musica mus0, Musica mus1, Musica mus2, Musica mus3) {
        Musica maiorMusica = mus0;
        if (mus1.id > maiorMusica.id) {
            maiorMusica = mus1;
        }
        if (mus2.id > maiorMusica.id) {
            maiorMusica = mus2;
        }
        if (mus3.id > maiorMusica.id) {
            maiorMusica = mus3;
        }
        return maiorMusica;
    }

    /**
     * Metodo para obter a música com o maior ID entre 3 músicas.
     * @param mus0 - primeira musica a se comparar
     * @param mus1 - segunda musica a se comparar
     * @param mus2 - terceira musica a se comparar
     * @return maiorMusica
     */
    private Musica getMaiorId (Musica mus0, Musica mus1, Musica mus2) {
        Musica maiorMusica = mus0;
        if (mus1.id > maiorMusica.id) {
            maiorMusica = mus1;
        }
        if (mus2.id > maiorMusica.id) {
            maiorMusica = mus2;
        }
        return maiorMusica;
    }

    /**
     * Metodo para obter a música com o maior ID entre 2 músicas.
     * @param mus0 - primeira musica a se comparar
     * @param mus1 - segunda musica a se comparar
     * @return maiorMusica
     */
    private Musica getMaiorId (Musica mus0, Musica mus1) {
        return mus0.id > mus1.id ? mus0 : mus1;
    }

    /**
     * Metodo para obter a música com o maior ID entre 4 músicas. Porem, a
     * depender dos valores booleanos para saber quais musicas se comparar.
     * @param mus0 - primeira musica a se comparar
     * @param mus1 - segunda musica a se comparar
     * @param mus2 - terceira musica a se comparar
     * @param mus3 - quarta musica a se comparar
     * @param arq0_OK - indicador se primeira musica e' valida
     * @param arq0_OK - indicador se segunda musica e' valida
     * @param arq0_OK - indicador se terceira musica e' valida
     * @param arq0_OK - indicador se quarta musica e' valida
     * @return maiorMusica se houver; null, caso nao tenha registro valido
     */
    private Musica getMaiorId (Musica mus0, Musica mus1, Musica mus2, Musica mus3,
                            boolean arq0_OK, boolean arq1_OK, boolean arq2_OK, boolean arq3_OK) {

        boolean[] combinacoes = {arq0_OK, arq1_OK, arq2_OK, arq3_OK};
        
        Musica musica = new Musica();

        // De acordo com as musicas validas, obter o maior ID
        switch (Arrays.toString(combinacoes)) {

            case "[true, true, true, true]":    musica = getMaiorId(mus0, mus1, mus2, mus3); break;
            case "[true, true, true, false]":   musica = getMaiorId(mus0, mus1, mus2);       break;
            case "[true, true, false, true]":   musica = getMaiorId(mus0, mus1, mus3);       break;
            case "[true, true, false, false]":  musica = getMaiorId(mus0, mus1);             break;
            case "[true, false, true, true]":   musica = getMaiorId(mus0, mus2, mus3);       break;
            case "[true, false, true, false]":  musica = getMaiorId(mus0, mus2);             break;
            case "[true, false, false, true]":  musica = getMaiorId(mus0, mus3);             break;
            case "[true, false, false, false]": musica = mus0;                               break;
            case "[false, true, true, true]":   musica = getMaiorId(mus1, mus2, mus3);       break;
            case "[false, true, true, false]":  musica = getMaiorId(mus1, mus2);             break;
            case "[false, true, false, true]":  musica = getMaiorId(mus1, mus3);             break;
            case "[false, true, false, false]": musica = mus1;                               break;
            case "[false, false, true, true]":  musica = getMaiorId(mus2, mus3);             break;
            case "[false, false, true, false]": musica = mus2;                               break;
            case "[false, false, false, true]": musica = mus3;                               break;
            case "[false, false, false, false]":musica = null;                               break;
        }

        return musica;
    }

    /**
     * Metodo para testar se registro ainda deve ser lido
     * Para isso, testa-se se o ponteiro chegou ao final do arquivo
     * E, tambem, se o contador esta' menor que o numero de registros que pode
     * ser lido. Este aumenta exponencialmente a cada intercalacao que se passa
     * @param posAtual - ponteiro de leitura do arquivo
     * @param tamanhoRegistro - posicao final do arquivo
     * @param cont - contador de quantos registros foram lidos do arquivo dentro
     * daquele caminho
     * @param numIntercalacoes - contador para o numero de intercalacoes que ja
     * foram executadas
     */
    private boolean testarSeTemRegistro(long posAtual, long tamanhoRegistro, int cont, int numIntercalacao) {
        return (posAtual < tamanhoRegistro) && (cont < Math.pow(NUM_REGISTROS, numIntercalacao));
    }

}
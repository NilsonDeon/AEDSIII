import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;


public class ComumSort{

    private static final String registroDB = "Registro.db";
    private static final int NUM_REGISTROS = 1500;
    private static final int NUM_CAMINHOS = 4;

    public ComumSort(){}

    public void ordenar() throws IOException {
        distribuicao();
        primeiraIntercalacao();
    }

    public void distribuicao() throws IOException {

        RandomAccessFile arqTemp = null;
        RandomAccessFile dbFile = null;

        // Criar os 4 arquivos temporarios
        for (int i = 0; i < NUM_CAMINHOS; i++) {
            try {
                arqTemp = new RandomAccessFile("arqTemp"+ i +".db", "rw");
                arqTemp.close();
                
            } catch (IOException e) {
                System.out.println("\nERRO: Ocorreu um erro de escrita no" +
                                   "arquivo \"" + registroDB + "\"\n");
            }
        }

        try {
            dbFile = new RandomAccessFile (registroDB, "r");

            if (dbFile.length() > 0) {

                dbFile.seek(0);
                int ultimoId = dbFile.readInt();
                long posicaoAtual = dbFile.getFilePointer();

                Musica[] musicas;

                while (dbFile.length() != posicaoAtual) {
                    for(int k = 0; k < 4; k++){
                        int posArray = 0;
                        musicas = new Musica[NUM_REGISTROS];
                        while (dbFile.length() != posicaoAtual && posArray < 1500) {
                            boolean lapide = dbFile.readBoolean();
                            int tamRegistro = dbFile.readInt();
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
                        QuickSort quick = new QuickSort(posArray);
                        quick.quicksort(musicas);
                        for (int i = 0; i < posArray; i++) {
                            byte[] bytes = musicas[i].toByteArray();
                            RandomAccessFile temp = null;
                            try{
                                temp = new RandomAccessFile ("arqTemp" + k + ".db", "rw");
                                temp.seek(temp.length());
                                temp.write(bytes);

                            }catch(Exception e) {
                                System.out.println("\nERRO: Ocorreu um erro na leitura do " +
                                "arquivo \"" + "arqTemp\"" + k + " -> " + e +"\n");
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
     *
     */
    public void primeiraIntercalacao () throws IOException {
        RandomAccessFile arqTemp0 = null;
        RandomAccessFile arqTemp1 = null;
        RandomAccessFile arqTemp2 = null;
        RandomAccessFile arqTemp3 = null;

        RandomAccessFile newTemp = null;

        try {
            // Abrir arquivos temporarios
            arqTemp0 = new RandomAccessFile ("arqTemp0.db", "r");
            arqTemp1 = new RandomAccessFile ("arqTemp1.db", "r");
            arqTemp2 = new RandomAccessFile ("arqTemp2.db", "r");
            arqTemp3 = new RandomAccessFile ("arqTemp3.db", "r");

            long tamArq0 = arqTemp0.length();
            long tamArq1 = arqTemp1.length();
            long tamArq2 = arqTemp2.length();
            long tamArq3 = arqTemp3.length();

            // Testar se todos os arquivos existem
            if (tamArq0 > 0 && tamArq1 > 0 && tamArq2 > 0 && tamArq3 > 0) {

                boolean quatroArquivosCompletos = false;
                boolean intercalacaoCompleta = false;

                arqTemp0.seek(0);
                arqTemp1.seek(0);
                arqTemp2.seek(0);
                arqTemp3.seek(0);

                long posAtual0 = arqTemp0.getFilePointer();
                long posAtual1 = arqTemp1.getFilePointer();
                long posAtual2 = arqTemp2.getFilePointer();
                long posAtual3 = arqTemp3.getFilePointer();

                int cont0 = 0;
                int cont1 = 0;
                int cont2 = 0;
                int cont3 = 0;

                int contMus = 0;

                boolean arq0_OK = true;
                boolean arq1_OK = true;
                boolean arq2_OK = true;
                boolean arq3_OK = true;

                Musica mus0 = new Musica();
                Musica mus1 = new Musica();
                Musica mus2 = new Musica();
                Musica mus3 = new Musica();

                Musica menorMusica = null;

                boolean teste = true;

                while (tamArq0 != posAtual0 && tamArq1 != posAtual0 && 
                       tamArq2 != posAtual0 && tamArq3 != posAtual0) {

                    for(int k = 0; k < NUM_CAMINHOS; k++){

                        // Settar variaveis de controle
                        teste = true;
                        quatroArquivosCompletos = false;
                        cont0 = cont1 = cont2 = cont3 = 0;
                        arq0_OK = arq1_OK = arq2_OK = arq3_OK = true;

                        while (quatroArquivosCompletos == false) {
                            
                            // Testar se deve passar para proxima musica do arquivo
                            if (menorMusica == mus0 || teste) {

                                // Testar se todos os arquivos estao validos para serem lidos
                                if (testarSeTemRegistro(posAtual0, tamArq0, cont0) == true) {                                   
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

                            if (menorMusica == mus1 || teste) {

                                // Testar se todos os arquivos estao validos para serem lidos
                                if (testarSeTemRegistro(posAtual1, tamArq1, cont1) == true) {
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

                            if (menorMusica == mus2 || teste) {

                                // Testar se todos os arquivos estao validos para serem lidos
                                if (testarSeTemRegistro(posAtual2, tamArq2, cont2) == true) {
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

                            if (menorMusica == mus3 || teste) {

                                // Testar se todos os arquivos estao validos para serem lidos
                                if (testarSeTemRegistro(posAtual3, tamArq3, cont3) == true) {
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

                            teste = false;

                            menorMusica = getMaiorId(mus0, mus1, mus2, mus3, arq0_OK, arq1_OK, arq2_OK, arq3_OK);

                            if (menorMusica != null) {

                                // Escrever menor musica
                                newTemp = new RandomAccessFile ("newTemp" + k + ".db", "rw");
                                newTemp.seek(newTemp.length());
                                byte[] bytes = menorMusica.toByteArray();
                                newTemp.write(bytes);
                                contMus++;

                            } else {
                                quatroArquivosCompletos = true;
                            }
                        }

                        if (newTemp != null) newTemp.close(); 

                    }
                }

            } else {
               System.out.println("\nERRO: Primeiros arquivos temporarios estao vazios\n");
            }
        } catch (FileNotFoundException e) {
                System.out.println("\nERRO: Primeiros arquivos temporarios nao encontrados\n");
        } finally {
            if (arqTemp0 != null) arqTemp0.close();
            if (arqTemp1 != null) arqTemp1.close();
            if (arqTemp2 != null) arqTemp2.close();
            if (arqTemp3 != null) arqTemp3.close();
       } 
    }


/**
 * Retorna a música com o maior ID entre 4 músicas.
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
 * Retorna a música com o maior ID entre 3 músicas.
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
 * Retorna a música com o maior ID entre 2 músicas.
 */
private Musica getMaiorId (Musica mus0, Musica mus1) {
    return mus0.id > mus1.id ? mus0 : mus1;
}

    private Musica getMaiorId (Musica mus0, Musica mus1, Musica mus2, Musica mus3,
                            boolean arq0_OK, boolean arq1_OK, boolean arq2_OK, boolean arq3_OK) {

        boolean[] combinacoes = {arq0_OK, arq1_OK, arq2_OK, arq3_OK};
        
        Musica musica = new Musica();

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

    private boolean testarSeTemRegistro(long posAtual, long tamanhoRegistro, int cont) {
        return ((posAtual != tamanhoRegistro) && (cont < NUM_REGISTROS));
    }

}
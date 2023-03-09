// bibliotecas
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

/**
 * ComumSort - Classe responsavel por realizar a Intercalcao Balanceada ComumSort.
 */
public class ComumSort {

    private static final String registroDB = "scr/dataBase/Registro.db";

    private static int NUM_REGISTROS;
    private static int NUM_CAMINHOS;

    private Musica musicas[];
    private int contador[];
    private RandomAccessFile arqTemp[];
    private long tamArq[];
    private long posAtual[];
    private boolean arqOK[];

    /**
     * Construtor padrao da classe ComumSort.
     */
    public ComumSort(){
        this(1500, 4);
    }

    /**
     * Construtor da classe ComumSort com passagem de parametros.
     * @param m - numero de registros por bloco a ser ordenado em memoria
     * primaria.
     * @param n - numero de caminhos, correspondendo a quantos arquivos os
     * registros serao divididos.
     */
    public ComumSort(int m, int n){
        if (m > 0 && n > 2) {
            NUM_REGISTROS = m;
            NUM_CAMINHOS = n;
        } else {
            NUM_REGISTROS = 1500;
            NUM_CAMINHOS = 4;
        }

        musicas = new Musica [NUM_CAMINHOS];
        contador = new int [NUM_CAMINHOS];
        arqTemp = new RandomAccessFile [NUM_CAMINHOS];
        tamArq = new long [NUM_CAMINHOS];
        posAtual = new long [NUM_CAMINHOS];
        arqOK = new boolean[NUM_CAMINHOS];
    }

    /**
     * Metodo principal de ordenacao, no qual a distribuicao e as intercalacoes
     * sao chamadas.
     * @throws IOException Caso haja erro de leitura ou escrita com os arquivos.
     */
    public void ordenar() throws IOException {

        boolean paridade = true;
        int numIntercalacao = 1;
        int numArquivos = 0;

        boolean ok = distribuicao();
        if (ok) {
            while (numArquivos != 1) {           
                numArquivos = intercalacao(numIntercalacao, paridade);
                paridade = !paridade;
                numIntercalacao++;
            }

            // Apagar antigo "Registros.db"
            File antigoDB = new File(registroDB);
            antigoDB.delete();

            // Renomear arquivo novo
            File novoArquivo = new File(registroDB);
            File antigoArquivo = null;
            if (paridade == true) antigoArquivo = new File("arqTemp0.db");
            else antigoArquivo = new File("arqTemp" + NUM_CAMINHOS + ".db");
            antigoArquivo.renameTo(novoArquivo);

            // Apagar arquivos temporarios
            for (int i = 0; i < NUM_CAMINHOS*2; i++) {
                File file = new File("arqTemp" + i + ".db");
                file.delete();
            }

            System.out.println("\nArquivo \"" + registroDB + "\" ordenado com sucesso!");
        }
    }

    /**
     * Metodo privado da ordenacao, representando a primeira fase da Ordenacao
     * Externa para distribuir o arquivo principal em NUM_CAMINHOS * arquivos, 
     * contendo cada um NUM_REGISTROS * registros.
     * @return true, se distribuicao ocorreu corretamente; false, caso 
     * contrario.
     * @throws IOException Caso haja erro de leitura ou escrita com os arquivos.
     */
    private boolean distribuicao() throws IOException {

        RandomAccessFile arqTemp = null;
        RandomAccessFile dbFile = null;

        boolean distribuicaoOK = true;

        try {
            dbFile = new RandomAccessFile (registroDB, "r");

            // Se tiver registro cadastrado
            if (dbFile.length() > 0) {

                // Ler cabecalho do arquivo
                dbFile.seek(0);
                int ultimoId = dbFile.readInt();
                long posicaoAtual = dbFile.getFilePointer();

                // Criar os primeiros arquivos temporarios
                for (int i = 0; i < NUM_CAMINHOS; i++) {
                   try {
                        arqTemp = new RandomAccessFile("arqTemp"+ i +".db", "rw");

                        // Ao terminar a ordenacao, SEMPRE o arquivo ordenado
                        // sera' o primeiro a ser criado, ou seja, "arqTemp0.db".
                        // Entao, deve-se ja salvar o ultimoID
                        if (i == 0) {
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
                        if (posArray > 0) {
                            QuickSort quick = new QuickSort(posArray);
                            quick.quicksort(musicas);
                        }

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
            distribuicaoOK = false;
            System.out.println("\nERRO: Registro vazio!" +
                               "\n      Tente carregar os dados iniciais primeiro!\n");
            }
       } catch (FileNotFoundException e) {
                System.out.println("\nERRO: Registro nao encontrado!" +
                                   "\n      Tente carregar os dados iniciais primeiro!\n");
                distribuicaoOK = false;
       } finally {
            if (dbFile != null) dbFile.close();
            return distribuicaoOK;
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

        RandomAccessFile newTemp = null;
        int numArquivos = 0;

        try {
            int ultimoId, k, aux;

            // Abrir arquivos temporarios
            // A paridade ira' controlar qual registro sera' lido e em qual
            // sera' gravado
            if (paridade == true) {
                for (int i = 0; i < NUM_CAMINHOS; i++) {
                    arqTemp[i] = new RandomAccessFile ("arqTemp" + i + ".db", "r");
                    tamArq[i] = arqTemp[i].length();
                }

                // Para evitar sobrescrever, apagar conteudo do arquivo
                for (int i = 0; i < NUM_CAMINHOS; i++) {
                    File file = new File ("arqTemp" + (i+NUM_CAMINHOS) +".db");
                    FileOutputStream fos = new FileOutputStream(file, false);
                    fos.write(new byte[0]);
                    fos.close();
                }

                // Garantir, mais na frente, criacao dos arquivos certos para 
                // a escrita
                k = NUM_CAMINHOS;
                aux = NUM_CAMINHOS;

            } else {
                for (int i = 0; i < NUM_CAMINHOS; i++) {
                    arqTemp[i] = new RandomAccessFile ("arqTemp" + (i+NUM_CAMINHOS) + ".db", "r");
                    tamArq[i] = arqTemp[i].length();
                }
                
                // Para evitar sobrescrever, deletar arquivo antigo
                for (int i = 0; i < NUM_CAMINHOS; i++) {
                    File file = new File ("arqTemp" + i +".db");
                    FileOutputStream fos = new FileOutputStream(file, false);
                    fos.write(new byte[0]);
                    fos.close();
                }

                // Garantir, mais na frente, criacao dos arquivos certos para 
                // a escrita
                k = 0;
                aux = 0;
            }

            // Testar se os arquivos existem
            if (arquivosExistirem()) {

                // Controle se intercalacao acabou
                boolean todosArquivosCompletos = false;
                boolean intercalacaoCompleta = false;

                // Garantir que a primeira leitura passe por todos os arquivos
                boolean carregamentoInicial = true;

                // Settar variaves de controle dos arquivos
                for (int i = 0; i < NUM_CAMINHOS; i++) {

                    // Posicionar e salvar ponteiros
                    arqTemp[i].seek(0);
                    posAtual[i] = arqTemp[i].getFilePointer();

                    // Contador para indicar quantos registros ja foram lidos
                    contador[i] = 0;

                    // Booleano para indicar se o arquivo ainda esta' valido
                    arqOK[i] = true;

                    // Array para salvar musicas que virao para memoria principal
                    musicas[i] = new Musica();
                }

                // Ler o ultimoID escrito no arquivo
                ultimoId = arqTemp[0].readInt();

                // Settar menorMuica
                Musica menorMusica = null;

                // Escrever ultimoID no primeiro arquivo
                newTemp = new RandomAccessFile ("arqTemp" + k + ".db", "rw");
                Musica auxMus = new Musica();
                byte[] salvarID = auxMus.intToByteArray(ultimoId);
                newTemp.write(salvarID);
                if (newTemp != null) newTemp.close();

                while (tiverArquivoParaLer()) {

                    int j = k;
                    while(j < (NUM_CAMINHOS + aux)){

                        // Settar variaveis de controle
                        carregamentoInicial = true;
                        todosArquivosCompletos = false;
                        setContador();
                        setArqOK();

                        // Resetar musicas
                        setMusicas();
                        menorMusica = null;
                        
                        while (todosArquivosCompletos == false) {
                            
                            // Testar se deve passar para proxima musica do arquivo
                            // Ou a menor musica == ultima musica lida do arquivo
                            // Ou esta' carregando pela primeira vez
                            for (int i = 0; i < NUM_CAMINHOS; i++) {
                                if(menorMusica == musicas[i] || carregamentoInicial) {

                                    // Testar se todos os arquivos estao validos para serem lidos
                                    if(testarSeTemRegistro(posAtual[i], tamArq[i], contador[i], numIntercalacao) == true) {

                                        // Ler atributos iniciais do registro
                                        boolean lapide = arqTemp[i].readBoolean();
                                        int tamRegistro = arqTemp[i].readInt();

                                        // Ler e salvar registro no array
                                        byte[] registro = new byte[tamRegistro];
                                        arqTemp[i].read(registro);
                                        musicas[i].fromByteArray(registro);

                                        // Ajustar variaveis
                                        posAtual[i] = arqTemp[i].getFilePointer();
                                        contador[i]++;
                                    } else {

                                        // Marcar aqruivo como invalido
                                        arqOK[i] = false;
                                    }
                                }
                            }

                            carregamentoInicial = false;
                            menorMusica = getMenorId();

                            if (menorMusica != null) {

                                // Escrever menor musica
                                newTemp = new RandomAccessFile ("arqTemp" + j + ".db", "rw");
                                newTemp.seek(newTemp.length());
                                byte[] bytes = menorMusica.toByteArray();
                                newTemp.write(bytes);

                                // Contabilizar numero de arquivos criados
                                numArquivos = Math.max(numArquivos, j+1);

                            } else {

                                // Quebrar o loop
                                todosArquivosCompletos = true;
                            }
                        }

                        if (newTemp != null) newTemp.close();
                        j++;
                    }
                }

            } else {
               System.out.println("\nERRO: Arquivos temporarios estao vazios\n");
            }
        } catch (FileNotFoundException e) {
                System.out.println("\nERRO: Arquivos temporarios nao encontrados\n");
        } finally {
            for (int i = 0; i < NUM_CAMINHOS; i++){
                if (arqTemp[i] != null) arqTemp[i].close();
            }

            // Corrigir valor, do contador do numero de arquivos criados
            if (numArquivos > NUM_CAMINHOS) numArquivos-= NUM_CAMINHOS;

            return numArquivos;
       } 
    }

    /**
     * Metodo para obter a Musica de menor ID.
     * @return menorMusica pelo ID.
     */
    private Musica getMenorId() {
        Musica menorMusica = null;

        for (int i = 0; i < NUM_CAMINHOS; i++) {

            // Testar se arquivo e' valido
            if (arqOK[i] == true) {

                // Se menorMusica nao for == null, comparar ID.
                if (menorMusica != null) {
                    menorMusica = (menorMusica.id < musicas[i].id) ? menorMusica : musicas[i];
                } else {
                    menorMusica = musicas[i];
                }
            }
        }

        return menorMusica;
    }

    /**
     * Metodo para testar se ainda tem arquivo para ser lido.
     * @return true, se tiver; false, caso contrario.
     */
    private boolean tiverArquivoParaLer() {
        boolean resp = false;
        for (int i = 0; i < NUM_CAMINHOS; i++) {
            resp = resp || (tamArq[i] != posAtual[i]);
        }

        return resp;
    }

    /**
     * Metodo para testar os arquivos a serem lidos existem.
     * @return true, se existirem; false, caso contrario.
     */
    private boolean arquivosExistirem() {
        boolean resp = true;
        for (int i = 0; i < NUM_CAMINHOS; i++) {
            resp = resp || (tamArq[i] > 0);
        }

        return resp;
    }

    /**
     * Metodo para settar array de contadores para zero.
     */
    private void setContador() {
        for (int i = 0; i < NUM_CAMINHOS; i++) {
            contador[i] = 0;
        }
    }

    /**
     * Metodo para settar array de verificadores de arquivo para true.
     */
    private void setArqOK() {
        for (int i = 0; i < NUM_CAMINHOS; i++) {
            arqOK[i] = true;
        }
    }

    /**
     * Metodo para settar array de Musicas para new Musica().
     */
    private void setMusicas() {
        for (int i = 0; i < NUM_CAMINHOS; i++) {
            musicas[i] = new Musica();
        }        

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
        return (posAtual < tamanhoRegistro) && (cont < (NUM_REGISTROS * Math.pow(NUM_CAMINHOS, numIntercalacao-1)));
    }

}
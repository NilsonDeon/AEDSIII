// Package
package sort;

// Bibliotecas
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

// Bibliotecas proprias
import app.Musica;
import sort.auxiliar.MinHeap;

/**
 * SelecaoPorSubstituicaoSort - Classe responsavel por realizar a Intercalcao 
 * Balanceada com Selecao por Substituicao
 */
public class SelecaoPorSubstituicaoSort {

    private static final String registroDB = "./src/resources/Registro.db";
    private static final String arquivoTemp = "./src/resources/arqTemp";

    private static int NUM_REGISTROS;
    private static int NUM_CAMINHOS;

    private Musica musicas[];
    private RandomAccessFile arqTemp[];
    private long tamArq[];
    private long posAtual[];
    private boolean arqOK[];

    private MinHeap minHeap;

    /**
     * Construtor padrao da classe SelecaoPorSubstituicaoSort.
     */
    public SelecaoPorSubstituicaoSort(){
        this(1500, 4);
    }

    /**
     * Construtor da classe SelecaoPorSubstituicaoSort com passagem de parametros.
     * @param m - numero de registros por bloco a ser ordenado em memoria
     * primaria.
     * @param n - numero de caminhos, correspondendo a quantos arquivos os
     * registros serao divididos.
     */
    public SelecaoPorSubstituicaoSort(int m, int n){
        if (m > 0 && n > 2) {
            NUM_REGISTROS = m;
            NUM_CAMINHOS = n;
        } else {
            NUM_REGISTROS = 1500;
            NUM_CAMINHOS = 4;
        }

        musicas = new Musica [NUM_CAMINHOS];
        arqTemp = new RandomAccessFile [NUM_CAMINHOS];
        tamArq = new long [NUM_CAMINHOS];
        posAtual = new long [NUM_CAMINHOS];
        arqOK = new boolean[NUM_CAMINHOS];

        minHeap = new MinHeap(NUM_REGISTROS);
    }

    /**
     * Metodo principal de ordenacao, no qual a distribuicao e as intercalacoes
     * sao chamadas.
     * @param atributo - a ser usado na ordenacao.
     */
    public void ordenar(int atributo) {

        boolean paridade = true;
        int numArquivos = 0;

        boolean ok = distribuicao(atributo);
        if (ok) {
            while (numArquivos != 1) {           
                numArquivos = intercalacao(atributo, paridade);
                paridade = !paridade;
            }

            // Apagar antigo "Registros.db"
            File antigoDB = new File(registroDB);
            antigoDB.delete();

            // Renomear arquivo novo
            File novoArquivo = new File(registroDB);
            File antigoArquivo = null;
            if (paridade == true) antigoArquivo = new File(arquivoTemp + "0.db");
            else antigoArquivo = new File(arquivoTemp + NUM_CAMINHOS + ".db");
            antigoArquivo.renameTo(novoArquivo);

            // Apagar arquivos temporarios
            for (int i = 0; i < NUM_CAMINHOS*2; i++) {
                File file = new File(arquivoTemp + i + ".db");
                file.delete();
            }

            System.out.println("\nArquivo \"" + registroDB + "\" ordenado com sucesso!");
        }
    }

    /**
     * Metodo privado da ordenacao, representando a primeira fase da Ordenacao
     * Externa para distribuir o arquivo principal em NUM_CAMINHOS * arquivos.
     * @param atributo - a ser usado na ordenacao.
     * @return true, se distribuicao ocorreu corretamente; false, caso 
     * contrario.
     */
    private boolean distribuicao(int atributo) {


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
                        arqTemp = new RandomAccessFile(arquivoTemp + i +".db", "rw");

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

                int posHeap = 0;

                // Popular o heap inicial com os NUM_REGISTROS
                while (dbFile.length() != posicaoAtual && posHeap < NUM_REGISTROS) {
                    boolean lapide = dbFile.readBoolean();
                    int tamRegistro = dbFile.readInt();

                    // Ler e salvar apenas se nao tiver lapide
                    if (lapide == false){
                        byte[] registro = new byte[tamRegistro];
                        dbFile.read(registro);

                        // Salvar musica
                        Musica musica = new Musica();
                        musica.fromByteArray(registro);

                        // Inserir no heap
                        // True, indicando que e' carga inical
                        minHeap.inserir(atributo, musica, true);
                        posHeap++;
                    } else {
                        posicaoAtual = dbFile.getFilePointer();
                        long proximaPosicao = posicaoAtual + (long)tamRegistro;
                        dbFile.seek(proximaPosicao);
                    }
                    posicaoAtual = dbFile.getFilePointer();
                }

                // Enquanto tiver arquivo ou heap tiver musica ou elemento no heap
                while (dbFile.length() != posicaoAtual || minHeap.hasElement()) {

                    // Percorrer cada um dos caminhos de arquivo
                    for(int k = 0; k < NUM_CAMINHOS; k++){

                        // Obter a prioridade do heap[0]
                        int prioridadeAtual = minHeap.getMenorPrioridade();

                        // Condicao de parada sera' fim de arquivo ou
                        // mudanca de prioridade
                        while (dbFile.length() != posicaoAtual &&
                               (prioridadeAtual % NUM_CAMINHOS) == k) {
                            
                            Musica menorMusica = minHeap.remover();
                            byte[] bytes = menorMusica.toByteArray();

                            // Salvar menor musica no arquivo
                            RandomAccessFile temp = null;
                            try{
                                temp = new RandomAccessFile (arquivoTemp + k + ".db", "rw");
                                temp.seek(temp.length());
                                temp.write(bytes);

                            }catch(Exception e) {
                                System.out.println("\nERRO: Ocorreu um erro na escrita do " +
                                "arquivo \"" + arquivoTemp + "\"" + k + ".db -> " + e +"\n");
                            }finally{
                                temp.close();
                            }

                            // Ler e salvar apenas se nao tiver lapide
                            boolean lapide = dbFile.readBoolean();
                            int tamRegistro = dbFile.readInt();
                            if (lapide == false){
                                byte[] registro = new byte[tamRegistro];
                                dbFile.read(registro);

                                // Obter musica
                                Musica musica = new Musica();
                                musica.fromByteArray(registro);

                                // Adiciona-la no heap
                                // False, indicando que nao e' carga inical
                                minHeap.inserir(atributo, musica, false);

                            } else {
                                posicaoAtual = dbFile.getFilePointer();
                                long proximaPosicao = posicaoAtual + (long)tamRegistro;
                                dbFile.seek(proximaPosicao);
                            }
                            posicaoAtual = dbFile.getFilePointer();

                            // Obter prioridade atual
                            prioridadeAtual = minHeap.getMenorPrioridade();
                        }

                        // Se acabar arquivo para leitura, continuar removendo do heap
                        while ((prioridadeAtual % NUM_CAMINHOS) == k &&
                                minHeap.hasElement()) {
                            Musica menorMusica = minHeap.remover();
                            byte[] bytes = menorMusica.toByteArray();

                            // Salvar menor musica no arquivo
                            RandomAccessFile temp = null;
                            try{
                                temp = new RandomAccessFile (arquivoTemp + k + ".db", "rw");
                                temp.seek(temp.length());
                                temp.write(bytes);

                            }catch(Exception e) {
                                System.out.println("\nERRO: Ocorreu um erro na escrita do " +
                                "arquivo \"" + arquivoTemp + "\"" + k + ".db -> " + e +"\n");
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

            // Fechar arquivo
            dbFile.close();

       } catch (FileNotFoundException e) {
                System.out.println("\nERRO: Registro nao encontrado!" +
                                   "\n      Tente carregar os dados iniciais primeiro!\n");
                distribuicaoOK = false;
       } finally {
            return distribuicaoOK;
       }
    }

    /**
     * Metodo para realizar a intercalacao propriamente dita.
     * @param atributo - a ser usado na ordenacao.
     * @param paridade - indicador para saber se e' uma intercalacao par ou
     * impar, implicando em qual arquivo sera' leitura e qual, escrita
     * @return numArquivos - numero de arquivos que foram criados.
     */
    public int intercalacao (int atributo, boolean paridade) {

        RandomAccessFile newTemp = null;
        int numArquivos = 0;

        try {
            int ultimoId, k, aux;

            // Abrir arquivos temporarios
            // A paridade ira' controlar qual registro sera' lido e em qual
            // sera' gravado
            if (paridade == true) {
                for (int i = 0; i < NUM_CAMINHOS; i++) {
                    arqTemp[i] = new RandomAccessFile (arquivoTemp + i + ".db", "r");
                    tamArq[i] = arqTemp[i].length();
                }

                // Para evitar sobrescrever, apagar conteudo do arquivo
                for (int i = 0; i < NUM_CAMINHOS; i++) {
                    File file = new File (arquivoTemp + (i+NUM_CAMINHOS) +".db");
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
                    arqTemp[i] = new RandomAccessFile (arquivoTemp + (i+NUM_CAMINHOS) + ".db", "r");
                    tamArq[i] = arqTemp[i].length();
                }
                
                // Para evitar sobrescrever, deletar arquivo antigo
                for (int i = 0; i < NUM_CAMINHOS; i++) {
                    File file = new File (arquivoTemp + i +".db");
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
                newTemp = new RandomAccessFile (arquivoTemp + k + ".db", "rw");
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

                                    // Testar se chegou ao fim do arquivo
                                    if (posAtual[i] < tamArq[i]) {
                                            
                                        // Ler atributos iniciais do registro
                                        boolean lapide = arqTemp[i].readBoolean();
                                        int tamRegistro = arqTemp[i].readInt();

                                        // Ler registro
                                        byte[] registro = new byte[tamRegistro];
                                        arqTemp[i].read(registro);

                                        if (arqOK[i] == true) {

                                            // Salvar registro em varivavel temporaria
                                            Musica musicaTmp = new Musica();
                                            musicaTmp.fromByteArray(registro);

                                            // Comparar e ver se ainda esta' ordenado
                                            if (carregamentoInicial || isOrdenado(atributo, musicaTmp, musicas[i])) {
                                                
                                                // Atualizar musica no array
                                                musicas[i] = musicaTmp.clone();

                                                // Ajustar variaveis
                                                posAtual[i] = arqTemp[i].getFilePointer();
                                            } else {

                                                // Como leu registro desordenado, voltar ponteiro
                                                arqTemp[i].seek(posAtual[i]);

                                                // Marcar arquivo como invalido
                                                arqOK[i] = false;
                                            }
                                        }
                                    } else {

                                        // Marcar arquivo como invalido
                                        arqOK[i] = false;
                                    }
                                }
                            }

                            carregamentoInicial = false;

                            // Ordenar pelo atributo escolhido
                            switch (atributo) {
                                case 1: menorMusica = getMenorId();   break;
                                case 2: menorMusica = getMenorNome(); break;
                                case 3: menorMusica = getMenorData(); break;
                            }

                            if (menorMusica != null) {

                                // Escrever menor musica
                                newTemp = new RandomAccessFile (arquivoTemp + j + ".db", "rw");
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

            // Fechar arquivos
            for (int i = 0; i < NUM_CAMINHOS; i++){
                if (arqTemp[i] != null) arqTemp[i].close();
            }

        } catch (FileNotFoundException e) {
                System.out.println("\nERRO: Arquivos temporarios nao encontrados\n");

        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao escrever nos arquivos temporarios\n");

        } finally {
            // Corrigir valor, do contador do numero de arquivos criados
            if (numArquivos > NUM_CAMINHOS) numArquivos-= NUM_CAMINHOS;

            return numArquivos;
       } 
    }

    /**
     * Metodo para obter a Musica de menor Data de Lancamento.
     * @return menorMusica pela Data de Lancamento.
     */
    private Musica getMenorData() {
        Musica menorMusica = null;

        for (int i = 0; i < NUM_CAMINHOS; i++) {

            // Testar se arquivo e' valido
            if (arqOK[i] == true) {

                // Se menorMusica nao for == null, comparar data de lancamento.
                if (menorMusica != null) {
                    menorMusica = (menorMusica.getDataLancamento().compareTo(musicas[i].getDataLancamento()) < 0) ? menorMusica : musicas[i];
                } else {
                    menorMusica = musicas[i];
                }
            }
        }

        return menorMusica;
    }

    /**
     * Metodo para obter a Musica de menor Nome.
     * @return menorMusica pelo Nome.
     */
    private Musica getMenorNome() {
        Musica menorMusica = null;

        for (int i = 0; i < NUM_CAMINHOS; i++) {

            // Testar se arquivo e' valido
            if (arqOK[i] == true) {

                // Se menorMusica nao for == null, comparar nome.
                if (menorMusica != null) {
                    menorMusica = (menorMusica.getNome().compareTo(musicas[i].getNome()) < 0) ? menorMusica : musicas[i];
                } else {
                    menorMusica = musicas[i];
                }
            }
        }

        return menorMusica;
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
                    menorMusica = (menorMusica.getId() < musicas[i].getId()) ? menorMusica : musicas[i];
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
        boolean resp = false;
        for (int i = 0; i < NUM_CAMINHOS; i++) {
            resp = resp || (tamArq[i] > 0);
        }

        return resp;
    }

    /**
     * Metodo para testar se arquivo esta' ordenado de acordo com atributo.
     * @param musicaTmp - nova musica lida.
     * @param musica - musica lida anteriormente.
     * @param atributo - escohlido.
     * @return true, se estiver ordenado; false, caso contrario.
     */
    private boolean isOrdenado (int atributo, Musica musicaTmp, Musica musica) {
        boolean resp = false;

        switch (atributo) {
            case 1: resp = (musicaTmp.getId() >= musica.getId());                                      break;
            case 2: resp = (musicaTmp.getNome().compareTo(musica.getNome()) >= 0);                     break;
            case 3: resp = (musicaTmp.getDataLancamento().compareTo(musica.getDataLancamento()) >= 0); break;
        }

        return resp;
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
}
package arvoreB;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import app.Musica;

public class ArvoreB {

    protected NoB raiz;
    private static final String arvoreBDB = "./src/resources/ArvoreB.db";

    /**
     * Construtor padrao da classe ArvoreB
     */
    public ArvoreB() {
        raiz = new NoB();
    }

    /**
     * Metodo para inicializar o arquivo "Arvore.db", inicializando a raiz.
     * @throws Exception Se ocorrer algum erro ao manipular os arquivos.
     */
    public void inicializarArvoreB() throws Exception {
        RandomAccessFile arvoreBFile = null;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBDB, "rw");

            // Posicionar ponteiro no inicio do arquivo
            arvoreBFile.seek(0);

            // Escrever posicao da raiz (proximos 8 bytes)
            long posRaiz = 8;
            byte[] posRaizBytes = ByteBuffer.allocate(8).putLong(posRaiz).array();
            arvoreBFile.write(posRaizBytes);

            // Escrever raiz no arquivo
            raiz.escreverNoB();

        } catch (IOException e) {
            System.out.println("\nERRO: Ocorreu um erro de escrita no " +
                               "arquivo \"" + arvoreBDB + "\"\n");
        } finally {
            if (arvoreBFile != null) arvoreBFile.close();
        }
    }

    /**
     * Metodo para inserir uma chave de pesquisa na arvore B.
     * @param musica - musica a se inserir.
     * @param newEndereco - endereco da musica no arquivo "Registro.db".
     * @throws Exception Se ocorrer algum erro ao manipular os arquivos.
     */
    public void inserir(Musica musica, long newEndereco) throws Exception {
        raiz = inserir(raiz, musica, newEndereco);
    }

    /**
     * Metodo para inserir uma chave de pesquisa na arvore B.
     * @param noB - no em analise.
     * @param musica - musica a se inserir.
     * @param newEndereco - endereco da musica no arquivo "Registro.db".
     * @return novo No.
     * @throws Exception Se ocorrer algum erro ao manipular os arquivos.
     */
    private NoB inserir(NoB noB, Musica musica, long newEndereco) throws Exception {
        RandomAccessFile arvoreBFile = null;
        NoB noInserir = null;

        try {
            arvoreBFile = new RandomAccessFile (arvoreBDB, "rw");

            // Obter chave de insercao
            int newChave = musica.getId();

            // Localizar a raiz
            arvoreBFile.seek(0);
            long posRaiz = arvoreBFile.readLong();
            
            // Ler No raiz
            noB.lerNoB(posRaiz);

            // Testar se no folha tem espaco livre para insercao
            if (noB.temEspacoLivre() && noB.isFolha()) {
                noB.inserir(posRaiz, newChave, newEndereco);
                noInserir = noB;
            
            // Se nao couber na folha, deve-se procurar No de insercao
            } else {
                
            }

        } catch (IOException e) {
            System.out.println("\nERRO: Ocorreu um erro de escrita no " +
                               "arquivo \"" + arvoreBDB + "\"\n");
        } finally {
            if (arvoreBFile != null) arvoreBFile.close();
            return noInserir;
        }
    }
}
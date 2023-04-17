package listaInvertida;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import app.Musica;

public class ListaInvertida {

    protected static ListaInvertida_AnoLancamento listaAnosLancamento = new ListaInvertida_AnoLancamento();
    protected static ListaInvertida_Artistas listaArtistas = new ListaInvertida_Artistas();

    /**
     * Construtor padrao da ListaInvertida.
     */
    public ListaInvertida() {}

    /**
     * Metodo para inserir uma musica nas duas invertidas.
     * @param musica - a ser inserida.
     * @param endereco - posicao dela no arquivo "Registros.db".
     * @throws Exception Se ocorrer algum erro ao manipular os arquivos.
     */
    public void inserir(Musica musica, long endereco) throws Exception {
        listaAnosLancamento.inserir(musica, endereco);
        listaArtistas.inserir(musica, endereco);
    }

    /**
     * Metodo para criar as pastas para armazenar as listas invertidas.
     */
    public void inicializarListas() {
        // Obter caminho para a pasta
        String pasta1 = "./src/resources/listaInvertida_AnoLancamento";
        
        // Cria um objeto File para representar a pasta
        File novaPasta1 = new File(pasta1);
        
        // Se a pasta nao existir, cria ela
        if (!novaPasta1.exists()) {
            novaPasta1.mkdirs();
        }

        // Obter caminho para a pasta
        String pasta2 = "./src/resources/listaInvertida_Artistas";
        
        // Cria um objeto File para representar a pasta
        File novaPasta2 = new File(pasta2);
        
        // Se a pasta nao existir, cria ela
        if (!novaPasta2.exists()) {
            novaPasta2.mkdirs();
        }
    }

    /**
     * Metodo para deletar as pastas e o conteudo delas, apagando, assim, as
     * litas invertidas.
     */
    public void delete() {

        // Caminho para a pasta
        String pasta1 = "./src/resources/listaInvertida_Artistas";
        String pasta2 = "./src/resources/listaInvertida_AnoLancamento";

        // Obter arquivo correspondente
        File diretorio1 = new File(pasta1);
        File diretorio2 = new File(pasta2);

        // Verificar se a pasta1 existe
        if (diretorio1.exists() && diretorio1.isDirectory()) {
            
            // Obter todos os arquivos
            File[] arquivos1 = diretorio1.listFiles();

            // Excluir um por um
            for (File arq : arquivos1) {
                if (arq.isFile()) arq.delete();
            }
        }

        // Verificar se a pasta2 existe
        if (diretorio2.exists() && diretorio2.isDirectory()) {
            
            // Obter todos os arquivos
            File[] arquivos2 = diretorio2.listFiles();

            // Excluir um por um
            for (File arq : arquivos2) {
                if (arq.isFile()) arq.delete();
            }
        }
    }

    /**
     * Metodo para pesquisar na lista pelos artistas.
     * @param artistaBusca - texto correspondente ao nome do artista procurado.
     * @return Array list de enderecos para aqueles nomes.
     * @throws Exception Se ocorrer algum erro ao manipular os arquivos.
     */
    public List<Long> readArtistas(String artistaBusca) throws Exception {
        return listaArtistas.read(artistaBusca);
    }

    /**
     * Metodo para pesquisar na lista pela data.
     * @param dataBusca - cada correspondente 'as musicas procuradas.
     * @return Array list de enderecos para aqueles nomes.
     * @throws Exception Se ocorrer algum erro ao manipular os arquivos.
     */
    public List<Long> readAnosLancamento(Date dataBusca) throws Exception {
        return listaAnosLancamento.read(dataBusca);
    }

    /**
     * Metodo para normalizar a string, apagando caracteres especiais e 
     * convertendo as letras em minusculo.
     * @param texto - a ser normalizado.
     * @return nova string com caracteres corretos para a busca eficiente.
     */
    public String normalizarString(String texto) {
        return listaArtistas.normalizarString(texto);
    } 
}
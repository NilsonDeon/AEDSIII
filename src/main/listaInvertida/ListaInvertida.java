// Package
package listaInvertida;

// Bibliotecas
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

// Bibliotecas proprias
import app.Musica;

/**
 * Classe responsavel por criar e manipular as listas invertidas do banco de
 * dados com os atributos de Musica: ano de lancamento e nome do artista.
 */
public class ListaInvertida {

    // Instancias da lista
    private static ListaInvertida_AnoLancamento listaAnosLancamento = new ListaInvertida_AnoLancamento();
    private static ListaInvertida_Artistas listaArtistas = new ListaInvertida_Artistas();

    // Enderecos dos arquivos da lista
    private static String pastaAnoLancamento = "./src/resources/listaInvertida_AnoLancamento";
    private static String pastaNomeArtista = "./src/resources/listaInvertida_Artistas";

    /**
     * Construtor padrao da ListaInvertida.
     */
    public ListaInvertida() {}

    /**
     * Metodo para inserir uma musica nas duas invertidas.
     * @param musica - a ser inserida.
     * @param endereco - posicao dela no arquivo "Registros.db".
     */
    public void inserir(Musica musica, long endereco) {
        listaAnosLancamento.inserir(musica, endereco);
        listaArtistas.inserir(musica, endereco);
    }

    /**
     * Metodo para inserir uma musica nas duas invertidas.
     * @param musica - a ser inserida.
     */
    public void delete(Musica musica) {
        listaArtistas.delete(musica);
    }

    /**
     * Metodo para criar as pastas para armazenar as listas invertidas.
     */
    public void inicializarListas() {
        
        // Cria um objeto File para representar a pasta
        File novaPasta1 = new File(pastaAnoLancamento);
        
        // Se a pasta nao existir, cria ela
        if (!novaPasta1.exists()) {
            novaPasta1.mkdirs();
        }

        // Cria um objeto File para representar a pasta
        File novaPasta2 = new File(pastaNomeArtista);
        
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

        // Obter arquivo correspondente
        File diretorio1 = new File(pastaAnoLancamento);
        File diretorio2 = new File(pastaNomeArtista);

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
     */
    public List<Long> readArtistas(String artistaBusca) {
        return listaArtistas.read(artistaBusca);
    }

    /**
     * Metodo para pesquisar na lista pela data.
     * @param dataBusca - cada correspondente 'as musicas procuradas.
     * @return Array list de enderecos para aqueles nomes.
     */
    public List<Long> readAnosLancamento(Date dataBusca) {
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
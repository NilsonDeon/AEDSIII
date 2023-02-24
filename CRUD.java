/**
 * CRUD - Trabalho Pratico 01 de Algoritmos e Estruturas de Dados III
 * @author Gabriel Vargas Bento de Souza e Nilson Deon Cordeiro Filho
 * @version 1.0 02/2023
 */

// bibliotecas
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;

public class CRUD {

    private static final String arquivoCSV = "dados/spotify.csv";
    private static final String registroDB = "Registro.db";
    private int ultimoId;

    public CRUD() {
        ultimoId = 0;
    }
    
    public void carregarCSV() throws Exception {
        try {
            BufferedReader csv = new BufferedReader (new FileReader (arquivoCSV));
            String linhaLida;

            while ((linhaLida = csv.readLine()) != null) {
                ultimoId++;
                Musica musica = new Musica (linhaLida, ultimoId);
                System.out.println(musica);
            }

            csv.close();
        } catch (FileNotFoundException e) {
            System.out.println("ERRO: arquivo csv não encontrado!\n");
        } finally {
            //criarRegistro();
        }
    }

    public void mostrarErro() {
        System.out.println("\nERRO: Por favor, digite uma opção válida de 0 a 4.");
    }

}
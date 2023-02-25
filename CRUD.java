/**
 * CRUD - Trabalho Pratico 01 de Algoritmos e Estruturas de Dados III
 * @author Gabriel Vargas Bento de Souza e Nilson Deon Cordeiro Filho
 * @version 1.0 02/2023
 */

// bibliotecas
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.InputMismatchException;
import java.util.Scanner;

public class CRUD {

    private static final String arquivoCSV = "Spotify.csv";
    private static final String registroDB = "Registro.db";

    public CRUD () {}
    
    /**
     * Metodo para carregar todas as musicas do arquivo csv e salva-las em
     * arquivo.
     * @throws Exception Se ocorrer algum erro ao manipular os arquivos.
     */
    public void carregarCSV() throws Exception {
        BufferedReader csvFile = null;
        RandomAccessFile dbFile = null;

        try {
            csvFile = new BufferedReader (new FileReader (arquivoCSV));
            dbFile = new RandomAccessFile (registroDB, "rw");

            int ultimoId = 0;

            dbFile.seek(0);
            dbFile.writeInt(ultimoId);

            String linhaLida;
            while ((linhaLida = csvFile.readLine()) != null) {
                ultimoId++;
                Musica musica = new Musica (linhaLida, ultimoId);
                byte[] byteArray = musica.toByteArray();
                dbFile.write(byteArray); 
            }

            dbFile.seek(0);
            dbFile.writeInt(ultimoId);

            System.out.println("\nArquivo \"" + registroDB + 
                               "\" criado com sucesso!");

        } catch (FileNotFoundException e) {
            System.out.println("\nERRO: O arquivo \""+ arquivoCSV + 
                               "\"não encontrado!\n");
        } catch (IOException e) {
            System.out.println("\nERRO: Ocorreu um erro de escrita no" +
                               "arquivo \"" + registroDB + "\"\n");
        } finally {
            if (csvFile != null) csvFile.close();
            if (dbFile != null) dbFile.close();
        }
    }

    /**
     * Metodo para cadastrar uma nova musica no banco de dados.
     * @throws Exception Se ocorrer algum erro ao manipular os arquivos.
     */
    public void create () throws Exception {
        RandomAccessFile dbFile = null;

        try {
            dbFile = new RandomAccessFile (registroDB, "rw");

            dbFile.seek(0);
            int ultimoId = dbFile.readInt();

            Musica musica = new Musica();
            musica.lerMusica();
            musica.id = ultimoId + 1;

            System.out.println(musica);

            dbFile.seek(0);
            dbFile.writeInt(ultimoId);

            long finalRegistro = dbFile.length();
            dbFile.seek(finalRegistro);

            byte[] byteArray = musica.toByteArray();
            dbFile.write(byteArray);

            System.out.println("\nMusica \"" + musica.nome + 
                               "\" cadastrada com sucesso!");            

        } catch (FileNotFoundException e) {
            System.out.println("\nERRO: registro não encontrado!\n");
        } finally {
            if (dbFile != null) dbFile.close();
        }
    }
    
    /**
     * Metodo para exibir as informacoes de uma musica a partir do seu ID.
     * @return true, se a música foi encontrada; false, caso contrario.
     * @throws Exception Se ocorrer algum erro ao manipular o arquivo.
     */
    public boolean read () throws Exception {
        Scanner sc = new Scanner(System.in);
        int idProcurado = 0;

       do {
           System.out.print("\nDigite o ID procurado: ");
           try {
               idProcurado = sc.nextInt();
               sc.nextLine();
           } catch (InputMismatchException e) {
               sc.nextLine();
               System.out.println("\nERRO: ID invalido!\n");
               idProcurado = 0;
           }
       } while (idProcurado == 0);

       sc.close();

       return read(idProcurado);
    }

    /**
     * Metodo para excluir uma musica a partir do seu ID.
     * @return true, se a música foi excluida; false, caso contrario.
     * @throws Exception Se ocorrer algum erro ao manipular o arquivo.
     */
    public boolean delete () throws Exception {
        Scanner sc = new Scanner(System.in);
        int idProcurado = 0;

       do {
           System.out.print("\nDigite o ID procurado: ");
           try {
               idProcurado = sc.nextInt();
               sc.nextLine();
           } catch (InputMismatchException e) {
               sc.nextLine();
               System.out.println("\nERRO: ID invalido!\n");
               idProcurado = 0;
           }
       } while (idProcurado == 0);

       sc.close();

       return delete(idProcurado);
    }

    /**
     * Metodo privado para exibir as informacoes de uma musica a partir do seu
     * ID.
     * @return true, se a música foi encontrada; false, caso contrario.
     * @throws Exception Se ocorrer algum erro ao manipular o arquivo.
     */
    private boolean read (int idProcurado) throws Exception {
        RandomAccessFile dbFile = null;
        boolean find = false;

        try {
            dbFile = new RandomAccessFile (registroDB, "rw");
            Musica musica = null;
            boolean lapide;
            int tamRegistro;

            dbFile.seek(0);
            int tamArquivo = dbFile.readInt();

            int i = 0;
            while (i < tamArquivo && find == false) {
                
                musica = new Musica();

                lapide = dbFile.readBoolean();
                tamRegistro = dbFile.readInt();

                if (lapide == true) {
                    byte[] registro = new byte[tamRegistro];
                    dbFile.read(registro);
                    musica.fromByteArray(registro);

                    if (idProcurado == musica.id) {
                        find = true;
                        System.out.println(musica); 
                    }
                } else {
                    long posicaoAtual = dbFile.getFilePointer();
                    long proximaPosicao = posicaoAtual + (long)tamRegistro;
                    dbFile.seek(proximaPosicao);
                }

                i++;
            }

            if (find == false) {
                System.out.println("\nMusica de ID (" + idProcurado + 
                                   ") nao esta' cadastrada!"); 
            }

        } catch (FileNotFoundException e) {
            System.out.println("\nERRO: registro não encontrado!\n");
        } finally {
            if (dbFile != null) dbFile.close();
            return find;
        }
    }

    /**
     * Metodo privado para excluir uma musica a partir do seu ID.
     * @return true, se a música foi excluida; false, caso contrario.
     * @throws Exception Se ocorrer algum erro ao manipular o arquivo.
     */
    private boolean delete (int idProcurado) throws Exception {
        RandomAccessFile dbFile = null;
        boolean find = false;

        try {
            dbFile = new RandomAccessFile (registroDB, "rw");
            Musica musica = null;
            boolean lapide;
            int tamRegistro;
            long posicaoInicial;

            dbFile.seek(0);
            int tamArquivo = dbFile.readInt();

            int i = 0;
            while (i < tamArquivo && find == false) {
                
                musica = new Musica();

                posicaoInicial = dbFile.getFilePointer();
                lapide = dbFile.readBoolean();
                tamRegistro = dbFile.readInt();

                if (lapide == true) {
                    byte[] registro = new byte[tamRegistro];
                    dbFile.read(registro);
                    musica.fromByteArray(registro);

                    if (idProcurado == musica.id) {
                        musica.lapide = false;

                        dbFile.seek(posicaoInicial);
                        byte[] byteArray = musica.toByteArray();
                        dbFile.write(byteArray);

                        find = true;
                        System.out.println("\nMusica [" + musica.id + "]: \"" +
                                           musica.nome + "\" " +
                                           "deletada com sucesso!");
                    }
                } else {
                    long proximaPosicao = posicaoInicial + (long)tamRegistro;
                    dbFile.seek(proximaPosicao);
                }

                i++;
            }

            if (find == false) {
                System.out.println("\nMusica de ID (" + idProcurado + 
                                   ") nao esta' cadastrada!"); 
            }
        } catch (FileNotFoundException e) {
            System.out.println("\nERRO: registro nao encontrado!\n");
        } finally {
            if (dbFile != null) dbFile.close();
            return find;
        }
    }

}
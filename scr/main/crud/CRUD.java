// Bibliotecas
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.InputMismatchException;

/**
 * CRUD - Classe responsavel por realizar as operacoes de manipulacao do arquivo
 * - popular a base de dados, cadastrar, pesquisar, atualizar, deletar.
 */
public class CRUD {

    private static final String arquivoCSV = "scr/resources/Spotify.csv";
    private static final String registroDB = "scr/dataBase/Registro.db";

    private static IO io = new IO();

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

            boolean continuar = true;
            if (dbFile.length() > 0) {
                // Perguntar somente uma ver e apagar se digitar 1
                // Entrada invalida ou igual a 2, manter arquivo
                String menu = "\n+------------------------------------------+" +
                              "\n|        Banco de dados ja populado!       |" +
                              "\n|         Deseja resetar o arquivo?        |" +
                              "\n|------------------------------------------|" +
                              "\n|         1 - SIM          2 - NAO         |" +
                              "\n+------------------------------------------+";                
                
                System.out.println(menu);
                int opcao = io.readInt("\nDigite uma opcao: ");
                if (opcao != 1) {
                    continuar = false;
                    System.out.println("\nArquivo \"" + registroDB + "\" mantido com sucesso!");
                }
            }

            if (continuar == true) {
                // Apagar antigo "Registros.db"
                File antigoDB = new File(registroDB);
                antigoDB.delete();
                dbFile = new RandomAccessFile (registroDB, "rw");

                Musica musica = new Musica();
                byte[] newId;

                int ultimoId = 0;

                // Reservar espaco no cabecalho do arquivo para o inteiro ultimo ID
                dbFile.seek(0);
                newId = musica.intToByteArray(ultimoId);
                dbFile.write(newId);

                // Ler CSV, criar array de bytes e salva-lo em arquivo
                String linhaLida;
                while ((linhaLida = csvFile.readLine()) != null) {
                    ultimoId++;
                    musica = new Musica (linhaLida, ultimoId);
                    byte[] byteArray = musica.toByteArray();
                    dbFile.write(byteArray); 
                }

                // Atualizar ultimo ID no cabecalho do arquivo
                dbFile.seek(0);
                newId = musica.intToByteArray(ultimoId);
                dbFile.write(newId);

                System.out.println("\nArquivo \"" + registroDB + 
                                "\" criado com sucesso!");
            }
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

            if (dbFile.length() > 0) {

                // Ler ultimo ID no cabecalho do arquivo
                dbFile.seek(0);
                int ultimoId = dbFile.readInt();

                // Criar nova Musica
                Musica musica = new Musica();
                musica.lerMusica();
                ultimoId++;
                musica.id = ultimoId;
                System.out.println(musica);

                // Atualizar ultimo ID no cabecalho do arquivo
                dbFile.seek(0);
                byte[] newId = musica.intToByteArray(ultimoId);
                dbFile.write(newId);

                // Escrever musica no final do arquivo
                long finalRegistro = dbFile.length();
                dbFile.seek(finalRegistro);
                byte[] byteArray = musica.toByteArray();
                dbFile.write(byteArray);
                
                System.out.println("\nMusica [" + musica.id + "]: \"" +
                                            musica.nome + "\" " +
                                            "cadastrada com sucesso!");
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
     * Metodo para exibir as informacoes de uma musica a partir do seu ID.
     * @return true, se a música foi encontrada; false, caso contrario.
     * @throws Exception Se ocorrer algum erro ao manipular o arquivo.
     */
    public boolean read () throws Exception {
        int idProcurado = 0;

       do {
           System.out.print("\nDigite o ID procurado: ");
           try {
               idProcurado = io.readInt();
           } catch (InputMismatchException e) {
               io.readLine();
               System.out.println("\nERRO: ID invalido!\n");
               idProcurado = 0;
           }
       } while (idProcurado == 0);

       return read(idProcurado);
    }

    /**
     * Metodo para excluir uma musica a partir do seu ID.
     * @return true, se a música foi excluida; false, caso contrario.
     * @throws Exception Se ocorrer algum erro ao manipular o arquivo.
     */
    public boolean delete () throws Exception {
        int idProcurado = 0;

       do {
           System.out.print("\nDigite o ID procurado: ");
           try {
               idProcurado = io.readInt();
           } catch (InputMismatchException e) {
               io.readLine();
               System.out.println("\nERRO: ID invalido!\n");
               idProcurado = 0;
           }
       } while (idProcurado == 0);

       return delete(idProcurado);
    }

    /**
     * Metodo para atualizar uma musica a partir do seu ID.
     * @return true, se a música foi atualizada; false, caso contrario.
     * @throws Exception Se ocorrer algum erro ao manipular o arquivo.
     */
    public boolean update () throws Exception {
        int idProcurado = 0;

       do {
           System.out.print("\nDigite o ID procurado: ");
           try {
               idProcurado = io.readInt();
           } catch (InputMismatchException e) {
               io.readLine();
               System.out.println("\nERRO: ID invalido!\n");
               idProcurado = 0;
           }
       } while (idProcurado == 0);

       return update(idProcurado);
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

            if (dbFile.length() > 0) {

                Musica musica = null;
                boolean lapide;
                int tamRegistro;


                // Obter ultimo ID adicionado
                dbFile.seek(0);
                long posicaoAtual = dbFile.getFilePointer();
                int ultimoId = dbFile.readInt();

                while (dbFile.length() != posicaoAtual && find == false) {
                                        
                    musica = new Musica();

                    // Ler informacoes do registro
                    lapide = dbFile.readBoolean();
                    tamRegistro = dbFile.readInt();

                    // Se registro for valido, ler e comparar com ID procurado
                    if (lapide == false) {
                        byte[] registro = new byte[tamRegistro];
                        dbFile.read(registro);
                        musica.fromByteArray(registro);

                        if (idProcurado == musica.id) {
                            find = true;
                            System.out.println(musica);
                        }

                    // Se nao for, pular o registro e reposicionar ponteiro
                    } else {
                        posicaoAtual = dbFile.getFilePointer();
                        long proximaPosicao = posicaoAtual + (long)tamRegistro;
                        dbFile.seek(proximaPosicao);
                    }
                    posicaoAtual = dbFile.getFilePointer();
                }

                if (find == false) {
                    System.out.println("\nMusica de ID (" + idProcurado + 
                                    ") não esta cadastrada!"); 
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

            if (dbFile.length() > 0) {

                Musica musica = null;
                Musica aux = new Musica();
                boolean lapide;
                int tamRegistro;

                // Obter ultimo ID adicionado
                dbFile.seek(0);
                long posicaoAtual = dbFile.getFilePointer();
                int tamArquivo = dbFile.readInt();

                while (dbFile.length() != posicaoAtual && find == false) {
                    
                    musica = new Musica();

                    // Ler informacoes do registro  
                    long posicaoInicio = dbFile.getFilePointer();
                    lapide = dbFile.readBoolean();
                    tamRegistro = dbFile.readInt();

                    // Se registro for valido, ler e comparar com ID procurado
                    if (lapide == false) {
                        byte[] registro = new byte[tamRegistro];
                        dbFile.read(registro);
                        musica.fromByteArray(registro);

                        if (idProcurado == musica.id) {
                            musica.lapide = true;

                            // Guardar ponteiro atual
                            long posicaoFinal = dbFile.getFilePointer();

                            // Apagar logicamente o registro
                            dbFile.seek(posicaoInicio);
                            byte[] newLapide = aux.booleanToByteArray(musica.lapide);
                            dbFile.write(newLapide);
                            find = true;

                            // Retornar ponteiro para final do registro
                            dbFile.seek(posicaoFinal);
                            
                            System.out.println("\nMusica [" + musica.id + "]: \"" +
                                            musica.nome + "\" " +
                                            "deletada com sucesso!");
                        }

                    // Se nao for, pular o registro e reposicionar ponteiro    
                    } else {
                        posicaoAtual = dbFile.getFilePointer();
                        long proximaPosicao = posicaoAtual + (long)tamRegistro;
                        dbFile.seek(proximaPosicao);
                    }
                    posicaoAtual = dbFile.getFilePointer();
                }

                if (find == false) {
                    System.out.println("\nMusica de ID (" + idProcurado + 
                                    ") não esta cadastrada!");
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
            return find;
        }
    }

    /**
     * Metodo privado para atualizar uma musica a partir do seu ID.
     * @return true, se a música foi excluida; false, caso contrario.
     * @throws Exception Se ocorrer algum erro ao manipular o arquivo.
     */
    private boolean update (int idProcurado) throws Exception {
        RandomAccessFile dbFile = null;
        boolean find = false;
        boolean atualizado = false;

        try {
            dbFile = new RandomAccessFile (registroDB, "rw");

            if (dbFile.length() > 0) {

                Musica musica = null;
                Musica aux = new Musica();
                boolean lapide;
                int tamRegistro;

                // Obter ultimo ID adicionado
                dbFile.seek(0);
                long posicaoAtual = dbFile.getFilePointer();
                int tamArquivo = dbFile.readInt();
                int ultimoId = tamArquivo;

                while (dbFile.length() != posicaoAtual && find == false) {
                    
                    musica = new Musica();

                    // Ler informacoes do registro
                    long posicaoInicio = dbFile.getFilePointer();
                    lapide = dbFile.readBoolean();
                    tamRegistro = dbFile.readInt();

                    // Se registro for valido, ler e comparar com ID procurado
                    if (lapide == false) {
                        byte[] registro = new byte[tamRegistro];
                        dbFile.read(registro);
                        musica.fromByteArray(registro);

                        if (idProcurado == musica.id) {

                            // Ler e criar novo Objeto musica
                            Musica newMusica = musica.clone();
                            atualizado  = newMusica.atualizar();
                            byte[] newRegistro = newMusica.toByteArray();

                            if (newRegistro.length <= registro.length) {

                                // Guardar ponteiro atual
                                long posicaoFinal = dbFile.getFilePointer();

                                // Salvar novo registro, mas manter tamanho antigo
                                newRegistro = newMusica.toByteArray(registro.length);
                                dbFile.seek(posicaoInicio);
                                dbFile.write(newRegistro);

                                // Retornar ponteiro para final do registro
                                dbFile.seek(posicaoFinal);

                            } else {
                                
                                // Marcar registro como invalido
                                musica.lapide = true;

                                dbFile.seek(posicaoInicio);
                                byte[] newLapide = aux.booleanToByteArray(musica.lapide);
                                dbFile.write(newLapide);

                                // Escrever a musica atualizada no final do arquivo
                                long finalArquivo = dbFile.length();
                                dbFile.seek(finalArquivo);
                                newRegistro = newMusica.toByteArray();
                                dbFile.write(newRegistro);
                            }

                            if (atualizado == true) {
                                System.out.println("\nMusica [" + newMusica.id + "]: \"" +
                                                newMusica.nome + "\" " +
                                                "atualizada com sucesso!");
                            }
                            find = true;
                        }

                    // Se nao for, pular o registro e reposicionar ponteiro    
                    } else {
                        posicaoAtual = dbFile.getFilePointer();
                        long proximaPosicao = posicaoAtual + (long)tamRegistro;
                        dbFile.seek(proximaPosicao);
                    }
                    posicaoAtual = dbFile.getFilePointer();
                }

                if (find == false) {
                    System.out.println("\nMusica de ID (" + idProcurado + 
                                    ") não esta cadastrada!"); 
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
            return atualizado;
        }
    }

    /**
     * Metodo para exibir as informacoes de uma musica a partir do seu ID.
     * @return true, se a música foi encontrada; false, caso contrario.
     * @throws Exception Se ocorrer algum erro ao manipular o arquivo.
    */
    public void abrirMusica() throws Exception {
        int idProcurado = 0;

       do {
           System.out.print("\nDigite o ID procurado: ");
           try {
               idProcurado = io.readInt();
           } catch (InputMismatchException e) {
               io.readLine();
               System.out.println("\nERRO: ID invalido!\n");
               idProcurado = 0;
           }
       } while (idProcurado == 0);

       abrirMusica(idProcurado);
    }

    /**
     * Metodo privado para exibir as informacoes de uma musica a partir do seu
     * ID.
     * @throws Exception Se ocorrer algum erro ao manipular o arquivo ou ID nao
     * for encontrado
     */
    public void abrirMusica (int idProcurado) throws Exception{

        RandomAccessFile dbFile = null;
        boolean find = false;
        Musica musica = new Musica();

        try {
            dbFile = new RandomAccessFile (registroDB, "r");

            if (dbFile.length() > 0) {

                boolean lapide;
                int tamRegistro;

                // Obter ultimo ID adicionado
                dbFile.seek(0);
                long posicaoAtual = dbFile.getFilePointer();
                int ultimoId = dbFile.readInt();

                while (dbFile.length() != posicaoAtual && find == false) {
                    
                    musica = new Musica();

                    // Ler informacoes do registro
                    long posicaoInicio = dbFile.getFilePointer();
                    lapide = dbFile.readBoolean();
                    tamRegistro = dbFile.readInt();

                    // Se registro for valido, ler e comparar com ID procurado
                    if (lapide == false) {
                        byte[] registro = new byte[tamRegistro];
                        dbFile.read(registro);
                        musica.fromByteArray(registro);

                        if (idProcurado == musica.id) {
                            find = true;
                        }

                    // Se nao for, pular o registro e reposicionar ponteiro    
                    } else {
                        posicaoAtual = dbFile.getFilePointer();
                        long proximaPosicao = posicaoAtual + (long)tamRegistro;
                        dbFile.seek(proximaPosicao);
                    }
                    posicaoAtual = dbFile.getFilePointer();
                }

                if (find == false) {
                    System.out.println("\nMusica de ID (" + idProcurado + 
                                    ") nao esta cadastrada!");
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

            if (find == true) {
                pesquisar(musica.uri);
            }
        }

    }

    /**
    * Metodo privado para abrir a musica no aplicativo do Spotify, apartir da sua URI.
    * @param uri link da musica.
    */
    private void pesquisar(String uri) {
        try {
            // Tratamento da string URI para ser adaptavel ao link
            // Pegar terceiro elemento correspondente ao trackID da musica
            String[] parts = uri.split(":");
            String trackId = parts[2];

            // Obter uri da musica
            String url = "https://open.spotify.com/track/" + trackId;

            // Se valido, abrir na web a musica
            ProcessBuilder pb = new ProcessBuilder("xdg-open", url);
            pb.start();
        } catch (Exception e) {
            System.out.print("\nERRO: Link incorreto. Tente atualizar o URI!");
            System.out.print("\nModelo: \"spotify:track:(INSERIR TRACK ID)\"\n");
        }
    }
}



// Bibliotecas
// package src;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.InputMismatchException;
import java.awt.Desktop;
import java.net.URI;

public class CRUD {

    private static final String arquivoCSV = "dados/Spotify.csv";
    private static final String registroDB = "Registro.db";
    
//    private static final boolean registroValido = false;
//    private static final boolean registroInvalido = true;

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

                System.out.println("\nMusica \"" + musica.nome + 
                                "\" cadastrada com sucesso!");
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
        IO io = new IO();
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
        IO io = new IO();
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
        IO io = new IO();
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


                // Obter tamanho do arquivo == ultimo ID adicionado
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
                                    ") não está cadastrada!"); 
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

                // Obter tamanho do arquivo == ultimo ID adicionado
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
                                    ") não está cadastrada!"); 
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

        try {
            dbFile = new RandomAccessFile (registroDB, "rw");

            if (dbFile.length() > 0) {

                Musica musica = null;
                Musica aux = new Musica();
                boolean lapide;
                int tamRegistro;

                // Obter tamanho do arquivo == ultimo ID adicionado
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
                            newMusica.atualizar();
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

                            find = true;
                            System.out.println("\nMusica [" + newMusica.id + "]: \"" +
                                            musica.nome + "\" " +
                                            "atualizada com sucesso!");
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
                                    ") não está cadastrada!"); 
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
     * Metodo para exibir as informacoes de uma musica a partir do seu ID.
     * @return true, se a música foi encontrada; false, caso contrario.
     * @throws Exception Se ocorrer algum erro ao manipular o arquivo.
    */
    public void abrirMusica() throws Exception {
        IO io = new IO();
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
     * seja encontrado
     */
    public void abrirMusica(int idProcurado) throws Exception{

        RandomAccessFile dbFile = null;
        boolean find = false;
        Musica musica = new Musica();

        try {
            dbFile = new RandomAccessFile (registroDB, "r");

            if (dbFile.length() > 0) {

                boolean lapide;
                int tamRegistro;

                // Obter tamanho do arquivo == ultimo ID adicionado
                dbFile.seek(0);
                long posicaoAtual = dbFile.getFilePointer();
                int tamArquivo = dbFile.readInt();


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
                                    ") nao está cadastrada!");
                    throw new Exception("ID não encontrado");
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
            URI uri = new URI(musica.uri);
            pesquisar(uri);
        }

    }

    /**
     * Metodo privado para abrir a musica no aplicativo do Spotify, apartir da sua URI.
     * @param uri link da musica.
     */
    private void pesquisar(URI uri){
        try {
            Desktop.getDesktop().browse(uri);
        } catch (Exception e) {
            System.out.println("Erro ao abrir o link: " + e.getMessage());
        }
        /* Para abrir no navegador usar https://open.spotify.com/track/ + código da música*/
    }

    /**
     * Metodo privado para atualizar uma musica a partir do seu ID.
     * @return true, se a música foi excluida; false, caso contrario.
     * @throws Exception Se ocorrer algum erro ao manipular o arquivo.
     */
    public boolean mostrar () throws Exception {
        RandomAccessFile dbFile = null;
        boolean find = false;

        try {
            dbFile = new RandomAccessFile (registroDB, "rw");

            if (dbFile.length() > 0) {

                Musica musica = null;
                Musica aux = new Musica();
                boolean lapide;
                int tamRegistro;

                // Obter tamanho do arquivo == ultimo ID adicionado
                dbFile.seek(0);
                long posicaoAtual = dbFile.getFilePointer();
                int tamArquivo = dbFile.readInt();
                int ultimoId = tamArquivo;

                while (dbFile.length() != dbFile.getFilePointer() && find == false) {
                    
                    musica = new Musica();

                    // Ler informacoes do registro
                    posicaoAtual = dbFile.getFilePointer();
                    lapide = dbFile.readBoolean();
                    tamRegistro = dbFile.readInt();

                        byte[] registro = new byte[tamRegistro];
                        dbFile.read(registro);
                        musica.fromByteArray(registro);
                        System.out.println("Lapide       : " + musica.lapide);
                        System.out.println(musica);
                }

                if (find == false) {
                    System.out.println("\nMusica de ID (" + 
                                    ") não está cadastrada!"); 
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

}
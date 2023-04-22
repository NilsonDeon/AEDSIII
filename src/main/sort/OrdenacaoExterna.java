// Package
package sort;

// Bibliotecas
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.InputMismatchException;

// Bibliotecas proprias
import app.*;
import hashing.HashingExtensivel;
import arvores.arvoreB.ArvoreB;
import arvores.arvoreBStar.ArvoreBStar;
import listaInvertida.ListaInvertida;

public class OrdenacaoExterna {

    private static final String registroDB = "./src/resources/Registro.db";
    private static IO io;
    private static HashingExtensivel hash;
    private static ArvoreB arvoreB;
    private static ArvoreBStar arvoreBStar;
    private static ListaInvertida lista;

    public OrdenacaoExterna() {
        io = new IO();
        hash = new HashingExtensivel();
        arvoreB = new ArvoreB();
        arvoreBStar = new ArvoreBStar();
        lista = new ListaInvertida();
    }

    public void ordenarArquivo () {

        // Testar se arquivo existe
        File arquivoRegistro = new File(registroDB);

        // Se existir, fazer a busca
        if (arquivoRegistro.exists()) {
            
            int opcao = 0;
            int atributo = 0;
            int numCaminhos = 0;
            int numRegistros = 0;

            String menu1 = "\n+------------------------------------------+" +
                           "\n|    Escolha o atributo para ordenacao:    |" +
                           "\n|------------------------------------------|" +
                           "\n| 1 - Id                                   |" +
                           "\n| 2 - Nome                                 |" +
                           "\n| 3 - Data de lancamento                   |" +
                           "\n+------------------------------------------+";    

            String menu2 = "\n+------------------------------------------+" +
                           "\n|        MENU INTECALACAO BALANCEADA       |" +
                           "\n|------------------------------------------|" +
                           "\n| 1 - Comum                                |" +
                           "\n| 2 - Blocos de Tamanho Variavel           |" +
                           "\n| 3 - Selecao por Substituicao             |" +
                           "\n| 4 - Voltar                               |" +
                           "\n+------------------------------------------+";          

            do {
                try {
                    System.out.println(menu1);
                    atributo = io.readInt("\nDigite o atributo desejado: ");

                    if (atributo < 1 || atributo > 3) {
                        System.out.println("\nERRO: Por favor, digite uma opcao valida de 1 a 3.");
                        }

                } catch (InputMismatchException e) {
                    System.out.println("\nERRO: Por favor, digite uma opcao valida de 1 a 3.");
                    io.readLine();
                }
            } while (atributo < 1 || atributo > 3);

            try {
                // Caso valor lido for invalido, construtor trata execao
                numCaminhos  = io.readInt("\nDigite o numero de caminhos : ");
                numRegistros = io.readInt("\nDigite o numero de registros: ");

                if (numCaminhos < 2 || numRegistros < 1) {
                    System.out.println("\nERRO: valores invalidos!");
                    System.out.println("Redefinido para 4 caminhos e 1500 registros!");
                }

            } catch (InputMismatchException e) {
                io.readLine();
            }

            do {
                try {
                        System.out.println(menu2);
                        opcao = io.readInt("\nDigite a ordenacao desejada: ");

                        switch (opcao) {
                        case 1 :
                                ComumSort sort1 = new ComumSort(numRegistros, numCaminhos);
                                sort1.ordenar(atributo);
                                break;
                        case 2 :
                                TamanhoVariavelSort sort2 = new TamanhoVariavelSort(numRegistros, numCaminhos);
                                sort2.ordenar(atributo);
                                break;                   
                        case 3 :
                                SelecaoPorSubstituicaoSort sort3 = new SelecaoPorSubstituicaoSort(numRegistros, numCaminhos);
                                sort3.ordenar(atributo);
                                break;
                        case 4 : break;
                        default: System.out.println("\nERRO: Por favor, digite uma opcao valida de 1 a 4."); break;
                        }
                } catch (InputMismatchException e) {
                    System.out.println("\nERRO: Por favor, digite uma opcao valida de 1 a 4.");
                    io.readLine();
                }
            } while (opcao < 1 || opcao > 4);

            // Se foi ordenado
            if (opcao == 1 || opcao == 2 || opcao == 3) {
                ordenarEstruturas();
            }

        // Senao, mensagem de erro
        } else {
            System.out.println("\nERRO: Registro vazio!" +
                               "\n      Tente carregar os dados iniciais primeiro!\n");
        
        }
    }

    /**
     * Metodo para refazer e ordenar todas as estruturas a partir da ordenacao
     * do "Registros.db".
    */
    private void ordenarEstruturas() {
        RandomAccessFile dbFile = null;

        // Ler diretorio do Hash
        hash.getDiretorio().lerDiretorio();

        // Apagar listas antigas
        lista.delete();

        // Mostrar mensagem de ordenacao
        System.out.println("\nOrdenando registros: ");

        try {
            dbFile = new RandomAccessFile (registroDB, "rw");

            if (dbFile.length() > 0) {

                Musica musica = null;

                // Ler ultimo ID adicionado
                dbFile.seek(0);
                dbFile.readInt();
                long posicaoAtual = dbFile.getFilePointer();

                long tamanhoArqDB = dbFile.length();

                while (posicaoAtual != tamanhoArqDB) {
                    
                    // Resetar musica
                    musica = new Musica();

                    // Mostrar barra progresso
                    io.gerarBarraProgresso(tamanhoArqDB, (int)posicaoAtual);

                    // Ler informacoes do registro
                    dbFile.readBoolean();
                    int tamRegistro = dbFile.readInt();

                    // Trazer musica para memoria primaria
                    byte[] registro = new byte[tamRegistro];
                    dbFile.read(registro);
                    musica.fromByteArray(registro);

                    // Atualizar no Hash
                    hash.update(musica.getId(), posicaoAtual);

                    // Atualizar na arvore B
                    arvoreB.update(musica.getId(), posicaoAtual);

                    // Atualizar na arvore B*
                    arvoreBStar.update(musica.getId(), posicaoAtual);
                    
                    // Atualizar nas listas                   
                    lista.inserir(musica, posicaoAtual);

                    // Atualizar ponteiro
                    posicaoAtual = dbFile.getFilePointer();
                }

                // Mostrar barra de progresso completa
                io.gerarBarraProgresso(tamanhoArqDB, (int)posicaoAtual);

                System.out.println("\n\nArquivo \"" + registroDB + 
                                   "\" ordenado com sucesso!");

            } else {
                System.out.println("\nERRO: Registro vazio!" +
                                   "\n      Tente carregar os dados iniciais primeiro!\n");
            }

            // Fechar arquivos
            dbFile.close();

        } catch (FileNotFoundException e) {
                System.out.println("\nERRO: Registro nao encontrado!" +
                                   "\n      Tente carregar os dados iniciais primeiro!\n");
        } catch (IOException e) {
            System.out.println("\nERRO: " + e.getMessage() + " ao ler o arquivo \"" + registroDB + "\"\n");
        }
    }
}
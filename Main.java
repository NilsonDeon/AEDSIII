/**
 * TP01 - Trabalho Pratico 01 de Algoritmos e Estruturas de Dados III
 * @author Gabriel Vargas Bento de Souza e Nilson Deon Cordeiro Filho
 * @version 1.0 02/2023
 */

// bibliotecas
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;

class Main {

    private static final String arquivoCSV = "Spotify.csv";
    private static final String registroTXT = "registro.txt";

    static int ultimoId;

    public Main () {
        ultimoId = 0;
    }
    
    private static void carregarCSV () {
        try {
            BufferedReader csv = new BufferedReader (new FileReader (arquivoCSV));
            String linhaLida;

            while ((linhaLida = csv.readLine()) != null) {
                ultimoId++;
                Musica musica = new Musica (linhaLida, ultimoId);
                System.out.println(musica);
            }

            csv.close();
        } catch (Exception e) {
            System.out.println("ERRO: arquivo csv não encontrado!\n");
        } finally {
            //criarRegistro();
        }
    }

    private static void mostrarErro() {
        System.out.println("\nERRO: Por favor, digite uma opção válida de 0 a 4.");
    }


    public static void main (String args[]) {

        Scanner sc = new Scanner(System.in);


        String menu = "\n0 - Sair" +
                      "\n1 - Realizar cargas inicial dos dados" +
                      "\n2 - Ler" +
                      "\n3 - Atualizar" +
                      "\n4 - Deletar";
        int opcao = -1;

        do {
            try {
                System.out.println(menu);
                System.out.print("\nDigite uma opção: ");
                opcao = sc.nextInt();

                switch (opcao) {
                    case 0 :                      break;
                    case 1 : carregarCSV();       break;
                    //case 2 : lerRegistro();       break;
                    //case 3 : atualizarRegistro(); break;
                    //case 4 : deletarRegistro();   break;
                    default: mostrarErro();       break;
                }
            } catch (Exception e) {
                mostrarErro();
                sc.nextLine();
            }
        } while (opcao != 0);
        
    }

}
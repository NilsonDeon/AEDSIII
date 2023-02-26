package src.ordenacoes;

import src.Musica;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Comum extends QuickSort {

    public static void main(String[] args) {
        System.out.println("olá mundo!");
    }

    private static final String registroDB = "../Registro.db";
    private static final int REGISTROS = 1500;
    private static final int CAMINHOS = 4;
    private RandomAccessFile arqTemp = null;

    public Comum(){
        for (int i = 0; i < CAMINHOS; i++) {
            
            try {
                arqTemp = new RandomAccessFile("arqTemp"+i, "w");
                arqTemp.close();
            } catch (IOException e) {
                System.out.println("\nERRO: Ocorreu um erro de escrita no" +
                                   "arquivo \"" + registroDB + "\"\n");
            }
        }
    }

}

/**
 * OrdenacaoExterna - Trabalho Pratico 01 de Algoritmos e Estruturas de Dados 
 * III
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

public class OrdenacaoExterna {

    private static final String arquivoCSV = "Spotify.csv";
    private static final String registroDB = "Registro.db";

    public OrdenacaoExterna () {}

    public void orderBy () {
        // ler atributo

        int atributo = 0, opcao = 0;

        int inteiro;
        String string;
        //Date date;

        switch (atributo) {
            case 1: 
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7: 
            
        }

        // ler opcao de ordenacao

        switch (opcao) {
            case 1: intercalacaoBlanceada();
            case 2: intercalacaoPorSubstituicao();
        }

        // ler 
    }

    private void intercalacaoBlanceada(){}
    private void intercalacaoPorSubstituicao(){}
}
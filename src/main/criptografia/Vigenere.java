package src.main.criptografia;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class Vigenere {
    private static final String registroDB = "./src/resources/Registro.db";
    private static final String chave = "aedsiii";

    public Vigenere() {
    }

   public void criptografar() {
        try {
            // Leitura do arquivo de bytes
            FileInputStream fis = new FileInputStream(registroDB);
            byte[] fileBytes = fis.readAllBytes();
            fis.close();

            // Criptografar os bytes
            byte[] bytesCriptografados = criptografarBytes(fileBytes);

            // Escrever os bytes criptografados no mesmo arquivo
            FileOutputStream fos = new FileOutputStream(registroDB);
            fos.write(bytesCriptografados);
            fos.close();

            System.out.println("Arquivo criptografado com sucesso!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] criptografarBytes(byte[] registro) {
        byte[] bytesCriptografados = new byte[registro.length];
        int tamanhoChave = chave.length();

        for (int i = 0; i < registro.length; i++) {
            byte original = registro[i];
            byte ChaveBytes = (byte) chave.charAt(i % tamanhoChave);

            // Criptografar o byte
            byte criptografado = (byte) (original ^ ChaveBytes);
            bytesCriptografados[i] = criptografado;
        }

        return bytesCriptografados;
    }

    public void descriptografar() {
        try {
            // Leitura do arquivo de bytes criptografados
            FileInputStream fis = new FileInputStream(registroDB);
            byte[] bytesCriptografados = fis.readAllBytes();
            fis.close();

            // Descriptografar os bytes
            byte[] bytesDescriptografados = descriptografarBytes(bytesCriptografados);

            // Escrever os bytes descriptografados no mesmo arquivo
            FileOutputStream fileOutputStream = new FileOutputStream(registroDB);
            fileOutputStream.write(bytesDescriptografados);
            fileOutputStream.close();

            System.out.println("Arquivo descriptografado com sucesso!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] descriptografarBytes(byte[] bytesCriptografados) {
        byte[] bytesDescriptografados = new byte[bytesCriptografados.length];
        int tamanhoChave = chave.length();

        for (int i = 0; i < bytesCriptografados.length; i++) {
            byte byteCriptografado = bytesCriptografados[i];
            byte ChaveBytes = (byte) chave.charAt(i % tamanhoChave);

            // Descriptografar o byte
            byte byteDescriptografado = (byte) (byteCriptografado ^ ChaveBytes);
            bytesDescriptografados[i] = byteDescriptografado;
        }

        return bytesDescriptografados;
    }
}
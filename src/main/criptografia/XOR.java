package criptografia;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A classe XOR é responsável por criptografar e descriptografar um arquivo usando o algoritmo XOR.
 */
public class XOR {
    private static final String registroDB = "./src/resources/Registro.db";
    private static final String CHAVE = "aedsiii";

    /**
     * Cria uma instância da classe XOR.
     */
    public XOR() {
    }

    /**
     * Criptografa um array de bytes usando o algoritmo XOR.
     *
     * @param registro o array de bytes a ser criptografado
     * @return um novo array de bytes contendo os bytes criptografados
     */
    private byte[] criptografar(byte[] registro) {
        byte[] bytesCriptografados = new byte[registro.length];
        int tamanhoChave = CHAVE.length();

        for (int i = 0; i < registro.length; i++) {
            byte original = registro[i];
            byte chaveBytes = (byte) CHAVE.charAt(i % tamanhoChave);

            // Criptografar o byte
            byte criptografado = (byte) (original ^ chaveBytes);
            bytesCriptografados[i] = criptografado;
        }

        return bytesCriptografados;
    }

    /**
     * Descriptografa um array de bytes criptografados usando o algoritmo XOR.
     *
     * @param bytesCriptografados o array de bytes criptografados
     * @return um novo array de bytes contendo os bytes descriptografados
     */
    private byte[] descriptografar(byte[] bytesCriptografados) {
        byte[] bytesDescriptografados = new byte[bytesCriptografados.length];
        int tamanhoChave = CHAVE.length();

        for (int i = 0; i < bytesCriptografados.length; i++) {
            byte byteCriptografado = bytesCriptografados[i];
            byte chaveBytes = (byte) CHAVE.charAt(i % tamanhoChave);

            // Descriptografar o byte
            byte byteDescriptografado = (byte) (byteCriptografado ^ chaveBytes);
            bytesDescriptografados[i] = byteDescriptografado;
        }

        return bytesDescriptografados;
    }
}

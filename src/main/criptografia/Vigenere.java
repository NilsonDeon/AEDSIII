package criptografia;

public class Vigenere {

    private static final String CHAVE = "hayalaessaehpravoce";

    public void Vigenere() {}

    /**
     * Metodo para criptografar um array de bytes, usando vigenere.
     * @param registroOriginal - registro original para se ciptografar.
     * @return - registro criptografado.
     */
    public byte[] criptografar(byte[] registro) {
        byte[] bytesCriptografados = new byte[registro.length];
        int tamanhoChave = CHAVE.length();

        for (int i = 0; i < registro.length; i++) {
            byte original = registro[i];
            byte ChaveBytes = (byte) CHAVE.charAt(i % tamanhoChave);

            // Criptografar o byte
            byte criptografado = (byte) ((int)original + (int)(ChaveBytes) % 256);
            bytesCriptografados[i] = criptografado;
        }

        return bytesCriptografados;
    }
    
    /**
     * Metodo para descriptografar o array de bytes, a partir da chave.
     * @param registroCriptografado - registro sob criptografia
     * @return - registro original.
     */
    public byte[] descriptografar(byte[] bytesCriptografados) {
        byte[] bytesDescriptografados = new byte[bytesCriptografados.length];
        int tamanhoChave = CHAVE.length();

        for (int i = 0; i < bytesCriptografados.length; i++) {
            byte byteCriptografado = bytesCriptografados[i];
            byte ChaveBytes = (byte) CHAVE.charAt(i % tamanhoChave);

            // Descriptografar o byte
            byte byteDescriptografado = (byte) ((int)byteCriptografado - (int)(ChaveBytes) % 256);
            bytesDescriptografados[i] = byteDescriptografado;
        }

        return bytesDescriptografados;
    }
}
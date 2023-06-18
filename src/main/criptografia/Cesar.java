package criptografia;

public class Cesar {

    // Chave para criptografia
    private static final int CHAVE = 5;
    
    /**
     * Construtor da classe Cesar
     */
    public Cesar() {}

    /**
     * Metodo para criptografar um array de bytes, usando cifra de cesar.
     * @param registroOriginal - registro original para se ciptografar.
     * @return - registro criptografado.
     */
    public byte[] criptografar(byte[] registroOriginal) {

        byte[] registroCriptografado = new byte[registroOriginal.length];

        // Percorre todo o registro e substitui pelo valor + chave
        for (int i = 0; i < registroOriginal.length; i++) {
            registroCriptografado[i] = (byte) ((int) ((int)registroOriginal[i] + CHAVE) % 256);
        }

        return registroCriptografado;
    }

    /**
     * Metodo para descriptografar o array de bytes, a partir da chave.
     * @param registroCriptografado - registro sob criptografia
     * @return - registro original.
     */
    public byte[] descriptografar(byte[] registroCriptografado) {

        byte[] registroOriginal = new byte[registroCriptografado.length];

        // Percorre todo o registro criptografado e substitui pelo valor - chave
        for (int i = 0; i < registroCriptografado.length; i++) {
            registroOriginal[i] = (byte) ((int) ((int)registroCriptografado[i] - CHAVE) % 256);
        }

        return registroOriginal;    
    }
}

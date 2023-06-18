package criptografia;

public class Criptografia {

    // Classes de criptografia
    private static final Cesar cesar = new Cesar();
    private static final XOR xor = new XOR();
    private static final Vigenere vigenere = new Vigenere();
    private static final Colunas colunas = new Colunas();
    //private static final RSA rsa = new RSA();

    /*
     * Construtor padrao da classe Criptografia.
     */
    public Criptografia() {}

    /**
     * Metodo para criptografar um registro, utilizando diferentes metodos de
     * criptografia.
     * @param registroOriginal - registro original para ser criptografado.
     * @return - texto criptografado.
     */
    public byte[] criptografar (byte[] registroOriginal) {

        byte[] criptografado;
        
        criptografado = cesar.criptografar(registroOriginal);
        criptografado = xor.criptografar(criptografado);
        criptografado = colunas.criptografar(criptografado);
        criptografado = vigenere.criptografar(criptografado);

        return criptografado;
    }

    /**
     * Metodo para descriptografar um registro, utilizando diferentes os 
     * metodos de criptografia.
     * @param registroOriginal - registro criptografado.
     * @return - texto original.
     */
    public byte[] descriptografar (byte[] registroCriptografado) {

        byte[] decriptografado;

        decriptografado = vigenere.descriptografar(registroCriptografado);
        decriptografado = colunas.descriptografar(decriptografado);
        decriptografado = xor.descriptografar(decriptografado);
        decriptografado = cesar.descriptografar(decriptografado);

        return decriptografado;
    }
}

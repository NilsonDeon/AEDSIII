package src.main.criptografia;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

public class RSA {
    private static final int bitLength = 1024;
    private static BigInteger primoP;
    private static BigInteger primoQ;
    private BigInteger n;
    private BigInteger e;
    private BigInteger d;

    /**
     * Construtor da classe RSA.
     * Gera numeros primos grandes e inicializa as chaves publica e privada.
     */
    public RSA() {
        primoP = gerarLargePrimo();
        primoQ = gerarLargePrimo();
        gerarPublicKey();
        gerarPrivateKey();
    }

    /**
     * Gera um numero primo grande com a quantidade de bits especificada.
     *
     * @return Um numero primo grande.
     */
    private static BigInteger gerarLargePrimo() {
        Random rnd = new Random();
        return BigInteger.probablePrime(bitLength, rnd);
    }

    /**
     * Gera a chave publica 'n' e 'e' com base nos primos gerados.
     */
    private void gerarPublicKey() {
        n = primoP.multiply(primoQ);
        BigInteger phi = primoP.subtract(BigInteger.ONE).multiply(primoQ.subtract(BigInteger.ONE));
        e = BigInteger.valueOf(65537);
    }

    /**
     * Gera a chave privada 'd' com base na chave publica gerada.
     */
    private void gerarPrivateKey() {
        BigInteger phi = primoP.subtract(BigInteger.ONE).multiply(primoQ.subtract(BigInteger.ONE));
        d = e.modInverse(phi);
    }

    /**
     * Criptografa uma mensagem representada como um array de bytes.
     *
     * @param bytesOriginais A bytes a ser criptografada.
     * @return Os bytes criptografados como um BigInteger.
     */
    private BigInteger criptografar(byte[] bytesOriginais) {
        BigInteger messageInt = new BigInteger(bytesOriginais);
        return messageInt.modPow(e, n);
    }

    /**
     * Descriptografa um byte criptografado representado como um BigInteger.
     *
     * @param bytesCifrados O byte criptografado a ser descriptografado.
     * @return Os bytes da mensagem original descriptografada.
     */
    private byte[] descriptografar(BigInteger bytesCifrados) {
        BigInteger descriptografaredInt = bytesCifrados.modPow(d, n);
        byte[] descriptografaredBytes = descriptografaredInt.toByteArray();

        if (descriptografaredBytes.length > 1 && descriptografaredBytes[0] == 0) {
            descriptografaredBytes = Arrays.copyOfRange(descriptografaredBytes, 1, descriptografaredBytes.length);
        }

        return descriptografaredBytes;
    }
}
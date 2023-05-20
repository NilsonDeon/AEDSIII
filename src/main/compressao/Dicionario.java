// Package
package compressao;

// Bibliotecas
import java.nio.ByteBuffer;
import java.util.HashMap;

/**
 * Classe para criar um dicionario de inteiro e byte[] capaz de realizar uma
 * busca eficiente de 1 pra 1.
 */
public class Dicionario {

    private HashMap<Integer, byte[]> mapPosByte;
    private HashMap<ByteBuffer, Integer> mapBytePos;
    private int posInserir;

    /**
     * Construtor padrao da classe Dicionario
     */
    protected Dicionario() {
        mapPosByte = new HashMap<Integer, byte[]>(0);
        mapBytePos = new HashMap<ByteBuffer, Integer>(0);
        posInserir = 1;
    }

    /**
     * Metodo para inserir um elemento e sua posicao no dicionario.
     * @param bytes - array de bytes a ser adicionado.
     * @return posicao de insercao do elemento
     */
    protected int put(byte[] bytes) {
        mapPosByte.put(posInserir, bytes);
        mapBytePos.put(ByteBuffer.wrap(bytes), posInserir);

        return posInserir++;
    }

    /**
     * Metodo para obter o tamanho do dicionario.
     * @return tamanho do dicionario.
     */
    protected int size() {
        return mapPosByte.size();
    }

    /**
     * Metodo para obter a posicao de um array de bytes no dicionario.
     * @param bytes - array a ser procurado.
     * @return - posicao do array; null se nao existir.
     */
    protected Integer get(byte[] bytes) {
        Integer posProcurada = null;

        if (! mapBytePos.isEmpty()) {
            posProcurada = mapBytePos.get(ByteBuffer.wrap(bytes));
        }

        return posProcurada;
    }

    /**
     * Metodo para obter o array de bytes a partir da sua posicao.
     * @param pos - posicao do array no dicionario.
     * @return array de bytes procurado.
     */
    protected byte[] get(Integer pos) {
        return mapPosByte.get(pos);
    }
    
    /**
     * Metodo para remover um elemento do dicionario.
     * @param pos - posicao do elemento a se deletar.
     */
    protected void remove(int pos) {
        byte[] bytes = get(pos);

        mapPosByte.remove(pos);
        mapBytePos.remove(ByteBuffer.wrap(bytes));

        posInserir--;
    }
}

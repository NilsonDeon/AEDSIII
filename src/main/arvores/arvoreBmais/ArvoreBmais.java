// Package
package arvores.arvoreBmais;

// Bibliotecas
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

// Bibliotecas proprias
import app.*;

public class ArvoreBmais {

    protected NoBmais raiz;
    private static final String arvoreBmaisDB = "./src/resources/ArvoreBmais.db";

    /**
     * Construtor padrao da classe ArvoreB+
     */
    public ArvoreBmais() {
        raiz = new NoBmais();
    }

}
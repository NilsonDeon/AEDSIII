import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class Diretorio {

    private static final String diretorioDB = "./src/resources/Diretorio.db";

    // Tamanho ocupado pelo bucket
    /* 
       p'  + numElementos + 1136 * (cahve  + endereco)
       int + short        + 1136 * (int    + long)
       4   + 2            + 1136 * (4 + 8)   = 13638
    */
    private static final long tamTotalBucket = 13638;

    protected int profundidadeGlobal;
    protected int tamDiretorio;
    protected long posBucket[];

    public Diretorio () throws Exception {
        this(1);
    }

    public Diretorio (int profundidadeGlobal) throws Exception {
        if (profundidadeGlobal <= 0) {
            throw new Exception ("\nERRO: Diretorio(" + profundidadeGlobal + ") -> profundidade global invalida!\n");
        }

        this.profundidadeGlobal = profundidadeGlobal;
        tamDiretorio = (int)Math.pow(2.0, this.profundidadeGlobal);
        posBucket = new long[tamDiretorio];

        long pos = 0;
        for(int i = 0; i < tamDiretorio; i++) {
            posBucket[i] = pos;
            pos += tamTotalBucket;
        }

        criarDiretorio();
    }

    private void criarDiretorio() throws Exception {
        RandomAccessFile diretorioFile = null;

        try {
            diretorioFile = new RandomAccessFile (diretorioDB, "rw");

            // Escrever, no cabecalho, a profundidade global
            diretorioFile.seek(0);
            byte[] profundidadeBytes = ByteBuffer.allocate(4).putInt(profundidadeGlobal).array();
            diretorioFile.write(profundidadeBytes);

            // Escrever as posicoes dos buckets, inicializando com zero
            for (int i = 0; i < tamDiretorio; i++) {
                long posLong = posBucket[i];
                byte[] posBytes = ByteBuffer.allocate(8).putLong(posLong).array();
                diretorioFile.write(posBytes);
            }
        } catch (IOException e) {
            System.out.println("\nERRO: Ocorreu um erro de escrita no" +
                               "arquivo \"" + diretorioDB + "\"\n");
        } finally {
            if (diretorioFile != null) diretorioFile.close();
        }
    }

    public void lerDiretorio() throws Exception {
        RandomAccessFile diretorioFile = null;

        try {
            diretorioFile = new RandomAccessFile (diretorioDB, "rw");

            // Ler profundidade atual
            diretorioFile.seek(0);
            profundidadeGlobal = diretorioFile.readInt();
            tamDiretorio = (int)Math.pow(2.0, profundidadeGlobal);
            
            // Ler os buckets em arquivo
            posBucket = new long[tamDiretorio];
            for (int i = 0; i < tamDiretorio; i++) {
                posBucket[i] = diretorioFile.readLong();
            }

        } catch (IOException e) {
            System.out.println("\nERRO: Ocorreu um erro de leitura do" +
                               "arquivo \"" + diretorioDB + "\"\n");
        } finally {
            if (diretorioFile != null) diretorioFile.close();
        }
    }

    public void aumentarProfundidade(int posicaoCheiaBucket, long finalArquivoBucket) throws Exception {
        RandomAccessFile diretorioFile = null;

        try {
            diretorioFile = new RandomAccessFile (diretorioDB, "rw");

            // Ler profundidade atual
            diretorioFile.seek(0);
            profundidadeGlobal = diretorioFile.readInt();
            profundidadeGlobal++;

            // Atualizar profundidade
            diretorioFile.seek(0);
            byte[] profundidadeBytes = ByteBuffer.allocate(4).putInt(profundidadeGlobal).array();
            diretorioFile.write(profundidadeBytes);

            // Atualizar tamanho diretorio
            int tamDiretorioAntigo = tamDiretorio;
            tamDiretorio = (int)Math.pow(2.0, profundidadeGlobal);
            long newPosBucket[] = new long[tamDiretorio];
            
            // Reposicionar ponteiro no fim do arquivo
            long fimArquivoAntigo = diretorioFile.length();
            diretorioFile.seek(fimArquivoAntigo);

            // Atualizar novos buckets
            for (int i = 0; i < tamDiretorioAntigo; i++) {
                
                // Na posicao que esta' cheia, deve-se criar um novo bucket
                if (i == posicaoCheiaBucket) {
                    newPosBucket[i] = posBucket[i];
                    newPosBucket[tamDiretorioAntigo+i] = finalArquivoBucket;
                } else {
                    newPosBucket[i] = newPosBucket[tamDiretorioAntigo+i] = posBucket[i];
                }

                // Salvar novas posicoes, apontando, ainda, para as antigas do diretorio
                long posLong = newPosBucket[2*i];
                byte[] posBytes = ByteBuffer.allocate(8).putLong(posLong).array();
                diretorioFile.write(posBytes);   
            }

        } catch (IOException e) {
            System.out.println("\nERRO: Ocorreu um erro de escrita no" +
                               "arquivo \"" + diretorioDB + "\"\n");
        } finally {
            if (diretorioFile != null) diretorioFile.close();
        }
    }

}

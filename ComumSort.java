import java.io.IOException;
import java.io.RandomAccessFile;

public class ComumSort{

    private static final String registroDB = "Registro.db";
    private static final int REGISTROS = 1500;
    private static final int CAMINHOS = 4;

    public ComumSort(){}

    public void distribuicao() throws IOException {

        RandomAccessFile arqTemp = null;
        RandomAccessFile dbFile = null;

        // Criar os 4 arquivos temporarios
        for (int i = 0; i < CAMINHOS; i++) {
            try {
                arqTemp = new RandomAccessFile("arqTemp"+ i +".db", "rw");
                arqTemp.close();
                
            } catch (IOException e) {
                System.out.println("\nERRO: Ocorreu um erro de escrita no" +
                                   "arquivo \"" + registroDB + "\"\n");
            }
        }

        try {
            dbFile = new RandomAccessFile (registroDB, "rw");

            dbFile.seek(0);
            int ultimoId = dbFile.readInt();
            long posicaoAtual = dbFile.getFilePointer();

            Musica[] musicas;

            while (dbFile.length() != posicaoAtual) {
                for(int k = 0; k < 4; k++){
                    int posArray = 0;
                    musicas = new Musica[REGISTROS];
                    while (dbFile.length() != posicaoAtual && posArray < 1500) {
                        boolean lapide = dbFile.readBoolean();
                        int tamRegistro = dbFile.readInt();
                        if (lapide == false){
                            byte[] registro = new byte[tamRegistro];
                            dbFile.read(registro);
                            musicas[posArray] = new Musica();
                            musicas[posArray].fromByteArray(registro);
                            posArray++;
                        } else {
                            posicaoAtual = dbFile.getFilePointer();
                            long proximaPosicao = posicaoAtual + (long)tamRegistro;
                            dbFile.seek(proximaPosicao);
                        }
                        posicaoAtual = dbFile.getFilePointer();
                    }
                    //QuickSort quick = new QuickSort(posArray);
                    //quick.quicksort(musicas);
                    for (int i = 0; i < posArray; i++) {
                        byte[] bytes = musicas[i].toByteArray();
                        RandomAccessFile temp = null;
                        try{
                            temp = new RandomAccessFile ("arqTemp" + k + ".db", "rw");
                            temp.seek(temp.length());
                            temp.write(bytes);

                        }catch(Exception e) {
                            System.out.println("\nERRO: Ocorreu um erro na leitura do " +
                            "arquivo \"" + "arqTemp\"" + k + " -> " + e +"\n");
                        }finally{
                            temp.close();
                        }
                    }
                }
            }

       } catch (Exception e) {
            System.out.println("\nERRO: Ocorreu um erro na leitura do " +
            "arquivo \"" + registroDB + "\" -> " + e +"\n");
       }finally{
            if (dbFile != null) {
                dbFile.close();
            }
       }
    }
}
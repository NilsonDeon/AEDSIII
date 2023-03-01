import java.io.IOException;
import java.io.RandomAccessFile;

public class ComumSort{

    private static final String registroDB = "Registro.db";
    private static final int REGISTROS = 1500;
    private static final int CAMINHOS = 4;
    private RandomAccessFile arqTemp = null;
    Musica[] musicas = new Musica[REGISTROS];

    public ComumSort(){
        for (int i = 0; i < CAMINHOS; i++) {
            try {
                arqTemp = new RandomAccessFile("arqTemp"+ i +".db", "rw");
                arqTemp.close();
                
            } catch (IOException e) {
                System.out.println("\nERRO: Ocorreu um erro de escrita no" +
                                   "arquivo \"" + registroDB + "\"\n");
            }
        }
    }

    public void distribuicao() throws IOException {

        RandomAccessFile dbFile = null;

        try {
            dbFile = new RandomAccessFile (registroDB, "rw");
            int tamanho = dbFile.readInt();

            int i = 0;
            while (i < tamanho) {
                for(int k = 0; k < 4; k++){
                    int posArray = 0;
                    while (i < tamanho && posArray < 1500) {
                        boolean lapide = dbFile.readBoolean();
                        int tamRegistro = dbFile.readInt();
                       if( lapide == true){
                            byte[] registro = new byte[tamRegistro];
                            dbFile.read(registro);
                            musicas[posArray] = new Musica();
                            musicas[posArray].fromByteArray(registro);
                            //System.out.println(musicas[0]);
                            posArray++;
                        } else {
                            long posicaoAtual = dbFile.getFilePointer();
                            long proximaPosicao = posicaoAtual + (long)tamRegistro;
                            dbFile.seek(proximaPosicao);
                            i++;
                        }
                        QuickSort quick = new QuickSort(REGISTROS);
                        quick.quicksort(musicas, 0, posArray);
                        for (Musica musica : musicas) {
                            byte[] bytes = musica.toByteArray();
                            RandomAccessFile temp = null;
                            try{
                                temp = new RandomAccessFile ("arqTemp" + k + ".db", "rw");
                                temp.write(bytes);

                            }catch(Exception e) {
                                System.out.println("\nERRO: Ocorreu um erro na leitura do " +
                                "arquivo \"" + "arqTemp" + k + " " + e +"\"\n");
                            }finally{
                                temp.close();
                            }
                        }
                    }
                }
            }

       } catch (Exception e) {
            System.out.println("\nERRO: Ocorreu um erro na leitura do " +
            "arquivo \"" + registroDB + " " + e +"\"\n");
       }finally{
            if (dbFile != null) {
                dbFile.close();
            }
       }
    }
}
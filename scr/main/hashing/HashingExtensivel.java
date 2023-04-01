package hashing;

import app.Musica;

public class HashingExtensivel {

    protected Diretorio diretorio;

    public HashingExtensivel() throws Exception {
        diretorio = new Diretorio();
    }

    private int hash (int id) {
        return (id % (int)Math.pow(2.0, diretorio.profundidadeGlobal));
    }

    public boolean inserir (Musica musica, long posicaoRegistro) throws Exception {
        boolean inserido = false;
        int id = musica.getId();
        int posHash = hash(id);

        Bucket bucket = new Bucket();

        // Encontrar bucket desejado
        long posicao = diretorio.posBucket[posHash];
        bucket.buscarBucket(posicao);

        // Enquanto bucket apontado pela posicao estiver cheio, 
        // aumentar profundidade
        while (bucket.isFull()) {

            // Testar se pode fazer novo hash sem aumentar diretorio
            if (diretorio.profundidadeGlobal > bucket.profundidadeLocal) {
                bucket.aumentarProfundidade(posicao);
                bucket.criarBucket();
                //bucket.redistribuir(posicao); // fazer metodo

            // Senao, aumentar profundidade
            } else {
                long finalArqBucket = bucket.getFinalArquivo();
                diretorio.aumentarProfundidade(posHash, finalArqBucket);
            }
        }

        return inserido;
    }
}
package arvoreB;

public class ArvoreB {

    protected NoB raiz;

    private static final String arvoreBDB = "./src/resources/ArvoreB.db";

    public ArvoreB() {
        raiz = null;
    }

    public void inserir(int newChave, long newEndereco) {
        raiz = inserir(raiz, newChave, newEndereco);
    }

    private NoB inserir(NoB no, int newChave, long newEndereco) {
        
        NoB noInserir = null;

        // Se No for null, inserir primeiro elemento na 'arvore (raiz)
        if (no == null) {
            //System.out.println("inserir RAIZ");
            no = new NoB(newChave, newEndereco);
            noInserir = no;
        
        // Se couber no No, basta inserir na raiz
        } else if (no.temEspacoLivre() && no.isFolha()) {
            //System.out.println("inserir FOLHA");
            no.inserir(newChave, newEndereco);
            noInserir = no;

        // Se nao couber, deve-se procurar No de insercao
        } else {
            noInserir = no.encontrarInsercao(newChave);

            // Se couber no No, basta inserir
            if (noInserir.temEspacoLivre()) {
                noInserir.inserir(newChave, newEndereco);
            
            // Senao, deve-se dividir o No
            } else {
                // Encontrar um No filho, tal qual seu pai suporte mais um elemento
                NoB filho = noInserir;
                NoB pai = noInserir.noPai;

                // Se for raiz cheia
                if (noInserir.isRaiz()) {

                    // Dividir No cheio
                    NoB noEsq = noInserir.getFilhoEsq();
                    NoB noDir = noInserir.getFilhoDir();
                    
                    // Separar elemento do meio para subir na arvore
                    noInserir = noInserir.getMeio();
                    int chave = noInserir.getChave(0);

                    //System.out.println("\nchave: " + chave + "\n");

                    // Alterar raiz
                    noInserir.remontarPonteiros(chave, noEsq, noDir);
                    raiz = noInserir;
                    /*
                        System.out.println("\n\n------------------noInserir-------------\n\n");
                        for(int i = 0; i < noInserir.ordemArvore-1; i++) {
                            System.out.print(noInserir.chave[i] + " ");
                        }
                        System.out.println();

    

                    System.out.println("\nok\n");
                    */
                
                // Senao, inserir no Pai
                } else if (pai.isFull()) {
     
                    // Dividir No cheio
                    NoB noEsq = noInserir.getFilhoEsq();
                    NoB noDir = noInserir.getFilhoDir();
                    
                    // Separar elemento do meio para subir na arvore
                    noInserir = noInserir.getMeio();
                    int chave = noInserir.getChave(0);
                    long elemento = noInserir.getEndereco(0);

                    // Inserir no pai o elemento do meio
                    inserir(pai, chave, elemento);

                    // Atualizar os filhos do No que subiu
                    pai.remontarPonteiros(chave, noEsq, noDir);
                    noInserir = raiz;
                }

                // Inserir, de fato, a chave desejada
                inserir(newChave, newEndereco);
            }
            
        }

        return noInserir;
    }

    public void mostrar() {
        if (raiz != null) {
            System.out.print("RAIZ   : ");
            for(int i = 0; i < raiz.ordemArvore-1; i++) {
                System.out.print(String.format("%2d ", raiz.chave[i]));
            }
            System.out.println();
        }
        for(int i = 0; i < raiz.ordemArvore; i++) {
            if (raiz.noFilho[i] != null) {
                System.out.print("FILHO " + i + ": ");
                for(int j = 0; j < raiz.ordemArvore-1; j++) {
                    System.out.print(String.format("%2d ", raiz.noFilho[i].chave[j]));
                }
                System.out.println();
            } else {
                System.out.println("FILHO " + i + ": null");
            }
        }

        System.out.println();
    }

}
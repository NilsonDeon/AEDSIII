package arvoreB;

public class TesteArvoreB {

    public static void main (String args[]) {
        ArvoreB tree = new ArvoreB();

        int chave = 5;
        long endereco = 100;
        tree.inserir(chave, endereco);
        System.out.println("\nInserir: " + chave);
        tree.mostrar();

        chave = 9;
        endereco = 200;
        tree.inserir(chave, endereco);
        System.out.println("\nInserir: " + chave);
        tree.mostrar();

        chave = 6;
        endereco = 300;
        tree.inserir(chave, endereco);
        System.out.println("\nInserir: " + chave);
        tree.mostrar();

        chave = 10;
        endereco = 500;
        tree.inserir(chave, endereco);
        System.out.println("\nInserir: " + chave);
        tree.mostrar();

        chave = 1;
        endereco = 500;
        tree.inserir(chave, endereco);
        System.out.println("\nInserir: " + chave);
        tree.mostrar();

        chave = 2;
        endereco = 500;
        tree.inserir(chave, endereco);
        System.out.println("\nInserir: " + chave);
        tree.mostrar();

        chave = 3;
        endereco = 500;
        tree.inserir(chave, endereco);
        System.out.println("\nInserir: " + chave);
        tree.mostrar();

        chave = 7;
        endereco = 500;
        tree.inserir(chave, endereco);
        System.out.println("\nInserir: " + chave);
        tree.mostrar();

        chave = 11;
        endereco = 500;
        tree.inserir(chave, endereco);
        System.out.println("\nInserir: " + chave);
        tree.mostrar();

        chave = 16;
        endereco = 500;
        tree.inserir(chave, endereco);
        System.out.println("\nInserir: " + chave);
        tree.mostrar();

        chave = 8;
        endereco = 500;
        tree.inserir(chave, endereco);
        System.out.println("\nInserir: " + chave);
        tree.mostrar();
        
    }
}
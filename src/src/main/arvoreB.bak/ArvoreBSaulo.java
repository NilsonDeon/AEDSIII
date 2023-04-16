package serviços.arquvosindexados.arvore;


import java.io.FileNotFoundException;

import entidades.Item;

/**
 * ArvoreB
 */
public class ArvoreB {


    /*
     * obs: valores vazios são tratacos como -1
     */
    // ------------------------------- classe  pagina --------------------------------
    private class page{

        // ---- atributos ----
        int     n;      // num bytes = 4
        Item    r[];    // num bytes = (4* ordem - 1) + (8 * ordem - 1)
        page    p[];    // num bytes = 8 * ordem
        // --- constructor ---
        public page(int size){
            this.n = 0; this.r = new Item[size-1]; this.p = new page[size];
        }
    }

    //--------------------------------------------------------------------------------
    
    //----------------------------------- atributos ----------------------------------
    
    private page raiz;
    private int m, mm;

    //--------------------------------------------------------------------------------

    //----------------------------- Getters and setters ------------------------------
    public page getRaiz() {
        return raiz;
    }

    public void setRaiz(page raiz) {
        this.raiz = raiz;
    }

    public int getM() {
        return m;
    }

    public void setM(int m) {
        this.m = m;
    }

    public int getMm() {
        return mm;
    }

    public void setMm(int mm) {
        this.mm = mm;
    }
    //--------------------------------------------------------------------------------

    // -------------------------------- constructors ---------------------------------

    public ArvoreB(int m, String fileName) throws FileNotFoundException{
        this.raiz = null; this.m = m; this.mm = 2*m;
    }

    //--------------------------------------------------------------------------------
    
    //----------------------------------- pesquisa -----------------------------------

    public Item pesquisa(int ID){
        return pesquisa(new Item(ID), this.raiz);
    }
    private Item pesquisa(Item reg, page ap){
        if (ap == null) return null;
        else {
            int i = 0;
            while( (i < ap.n-1) && reg.compara(ap.r[i]) > 0) i++;
            if(reg.compara(ap.r[i]) == 0) return ap.r[i];
            else if(reg.compara(ap.r[i]) < 0) return pesquisa(reg, ap.p[i]);
            else return pesquisa(reg, ap.p[i+1]);
        }
    }

    //--------------------------------------------------------------------------------

    //------------------------------------ inserir -----------------------------------

    public void inserir(int id, long pos){
        inserir(new Item(id, pos));
    }
    private void inserir(Item reg){
        Item[] regRetorno = new Item[1];
        boolean[] cresceu = new boolean[1];
        page apRetorno = this.inserir(reg, this.raiz, regRetorno, cresceu);
        if (cresceu[0]) {
            page apTemp = new page(this.mm);
            apTemp.r[0] = regRetorno[0];
            apTemp.p[0] = this.raiz;
            apTemp.p[1] = apRetorno;
            this.raiz= apTemp;
            this.raiz.n++;
        } else this.raiz = apRetorno;
    }
    private page inserir(Item reg, page ap, Item[] regRetorno, boolean[] cresceu){
        page apRetorno = null;
        if (ap == null) {
            cresceu[0] = true; regRetorno[0] = reg;
        }
        else{
            int i = 0;
            while ((i < ap.n - 1) && reg.compara(ap.r[i]) > 0);
            if (reg.compara(ap.r[i]) == 0) {
                System.out.println("ERROR: Registro já existente");
                cresceu[0] = false;
            }
            else{
                if (reg.compara(ap.r[i]) > 0) i++;
                apRetorno = inserir(reg, ap.p[i], regRetorno, cresceu);
                if (cresceu[0]) 
                    if (ap.n < this.mm) {
                        this.insereNaPagina(ap, regRetorno[0], apRetorno);
                        cresceu[0] = false;
                        apRetorno = ap;
                    }
                    else{
                        page apTemp = new page(this.mm);
                        apTemp.p[0] = null;
                        if (i <= this.m) {
                            this.insereNaPagina(apTemp, ap.r[this.mm-1], ap.p[this.mm]);
                            ap.n--;
                            this.insereNaPagina(ap, regRetorno[0], apRetorno);
                        } else this.insereNaPagina(apTemp, regRetorno[0], apRetorno);
                        for (int j = this.m+1; j < this.mm; j++) {
                            this.insereNaPagina(apTemp, ap.r[j], ap.p[j+1]);
                            ap.p[j+1] = null;
                        }
                        ap.n = this.m; 
                        apTemp.p[0] = ap.p[this.m+1];
                        regRetorno[0] = ap.r[this.m]; apRetorno = apTemp;
                    }
            }
        }
        return (cresceu[0] ? apRetorno : ap);
    }

    //--------------------------------------------------------------------------------
    
    //------------------------------------ remover -----------------------------------

    public void remover(int id) throws Exception{
        remover(new Item(id)); 
    }
    private void remover( Item reg )throws Exception{
        boolean[] diminuiu = new boolean[1];
        this.raiz = this.remover(reg, this.raiz, diminuiu);
        if (diminuiu[0] && (this.raiz.n == 0)) {
            this.raiz = this.raiz.p[0];
        }
    }
    private page remover(Item reg, page ap, boolean[] diminuiu){
        if (ap == null) {
            System.out.println("ERROR: Regisrto não encontrado");
            diminuiu[0] = false;
        }
        else{
            int ind = 0;
            while ((ind < ap.n-1) && (reg.compara(ap.r[ind]) > 0)) ind++;
            if (reg.compara(ap.r[ind]) == 0) {
                if(ap.p[ind] == null){
                    ap.n--; diminuiu[0] = ap.n < this.m;
                    for (int j = 0; j < ap.n; j++) {
                        ap.r[j] = ap.r[j+1]; ap.p[j] = ap.p[j+1];
                    }
                    ap.p[ap.n] = ap.p[ap.n+1];
                    ap.p[ap.n+1] = null;
                }
                else{
                    diminuiu[0] = antecessor(ap, ind, ap.p[ind]);
                    if (diminuiu[0]) diminuiu[0] = reconstitui(ap.p[ind], ap, ind);
                }
            }
            else{
                if (reg.compara(ap.r[ind]) > 0) ind++;
                ap.p[ind] = remover(reg, ap.p[ind], diminuiu);
                if(diminuiu[0]) diminuiu[0] = reconstitui(ap.p[ind], ap, ind);
            }
        }
        return ap;
    }

    //--------------------------------------------------------------------------------
    
    //------------------------------- métodos auxiliares -----------------------------

    private void insereNaPagina(page ap, Item reg, page apDir){
        int k = ap.n - 1;
        while((k >= 0) && reg.compara(ap.r[k]) < 0){
            ap.r[k+1] = ap.r[k];
            ap.p[k+2] = ap.p[k+1];
            k--;
        }
        ap.r[k+1] = reg; ap.p[k+2] = apDir; ap.n++;
    }

    private boolean antecessor(page ap, int ind, page apPai) {
        boolean diminuiu = true;
        if(apPai.p[apPai.n] != null){
            diminuiu = antecessor(ap, ind, apPai.p[apPai.n]);
            if (diminuiu) diminuiu = reconstitui(apPai.p[apPai.n], apPai, apPai.n);
        }else{
            ap.r[ind] = apPai.r[--apPai.n]; diminuiu = apPai.n < this.m;
        }
        return diminuiu;
    }

    private boolean reconstitui(page apPag, page apPai, int posPai) {
        boolean diminuiu = true;
        if(posPai < apPai.n){
            page aux = apPai.p[posPai+1];
            int dispAux = (aux.n - this.m + 1)/2;
            apPag.r[apPag.n++] = apPai.r[posPai]; apPag.p[apPag.n] = aux.p[0];
            aux.p[0] = null;
            if(dispAux > 0){
                for (int i = 0; i < dispAux-1; i++) {
                    this.insereNaPagina(apPag, aux.r[i], aux.p[i+1]);
                    aux.p[i+i] = null;
                }
                apPai.r[posPai] = aux.r[dispAux - 1];
                aux.n = aux.n - dispAux;
                for (int i = 0; i < aux.n; i++) aux.r[i] = aux.r[i+dispAux];
                for (int i = 0; i <= aux.n; i++) aux.r[i] = aux.r[i+dispAux];
                aux.p[aux.n+dispAux] = null;
                diminuiu = false;
            }
            else{
                for (int i = 0; i < this.m; i++) {
                    this.insereNaPagina(apPag, aux.r[i], aux.p[i+1]);
                    aux.p[i+1] = null;
                }
                aux = apPai.p[posPai+1] = null;
                for (int i = posPai; i < apPai.n-1; i++) {
                    apPai.r[i] = apPai.r[i+1]; apPai.p[i+1] = apPai.p[i+2];
                }
                apPai.p[apPai.n--] = null;
                diminuiu = apPai.n < this.m;
            }
        }
        else{
            page aux = apPai.p[posPai-1];
            int dispAux = (aux.n - this.m + 1)/2;
            for (int i = apPag.n-1; i >= 0; i--) apPag.r[i+1] = apPag.r[i];
            apPag.r[0] = apPai.r[posPai-1];
            for (int i = apPag.n; i >= 0; i--) apPag.p[i+1] = apPag.p[i];
            apPag.n++;
            if(dispAux > 0){
                for (int i = 0; i < dispAux-1; i++) {
                    this.insereNaPagina(apPag, aux.r[aux.n-i-1], apPag.p[i]);
                    aux.p[aux.n-i] = null;
                }
                apPag.p[0] = aux.p[aux.n - dispAux +1];
                aux.p[aux.n - dispAux +1] = null;
                apPai.r[posPai-1] = aux.r[aux.n - dispAux];
                aux.n = aux.n - dispAux; diminuiu = false;
            }
            else{
                for (int i = 0; i < this.m; i++) {
                    this.insereNaPagina(aux, apPag.r[i], apPag.p[i+1]);
                    apPag.p[i+1] = null;
                }
                apPag = null;
                apPai.p[apPai.n--] = null;
                diminuiu = apPai.n < this.m;
            }
        }
        return diminuiu;
    }

    //--------------------------------------------------------------------------------

}
// Package
package app;

// Bibliotecas
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.PrintStream;
import java.nio.charset.Charset;

/**
 * CRUD - Classe responsavel por tratar a leitura de dados do teclado dos tipos:
 * String e Inteiro.
 */
public class IO {

   private static BufferedReader in = new BufferedReader(new InputStreamReader(System.in, Charset.forName("UTF-8")));
   private static String charset = "UTF-8";

   /**
    * Construtor padrao da classe IO.
    */
   public IO () {}

   /**
    * Metodo para ler um inteiro do teclado.
    * @return - numero digitado, se entrada for valida; 0, caso contrario.
    */
   public int readInt(){
      int i = 0;
      String string = "";
      try{
         string = readLine();
         i = Integer.parseInt(string.trim());
      }catch(Exception e){
         System.out.println("ERRO: Valor invalido (" + string + ")\n");
      }
      return i;
   }

   /**
    * Metodo para ler um inteiro do teclado.
    * @param str - string de uma mensagem a ser exibida na tela antes da 
    * leitura.
    * @return - numero digitado, se entrada for valida; 0, caso contrario.
    */
   public int readInt(String str){
      try {
         PrintStream out = new PrintStream(System.out, true, charset);
         out.print(str);
      }catch(UnsupportedEncodingException e){
         System.out.println("ERRO: charset invalido");
      }
      return readInt();
   }

   /**
    * Metodo para ler uma linha do teclado.
    * @return - linha digitada, se entrada for valida; string vazia, caso
    * contrario.
    */
   public String readLine(){
      String s = "";
      char tmp;
      try{
         do{
            tmp = (char)in.read();
            if(tmp != '\n' && tmp != 13){
               s += tmp;
            }
         }while(tmp != '\n');
      }catch(IOException ioe){
         System.out.println("lerPalavra: " + ioe.getMessage());
      }
      return s;
   }

   /**
    * Metodo para ler uma linha do teclado.
    * @param str - string de uma mensagem a ser exibida na tela antes da 
    * leitura.
    * @return - linha digitada, se entrada for valida; string vazia, caso
    * contrario.
    */
   public String readLine(String str){
      try {
         PrintStream out = new PrintStream(System.out, true, charset);
         out.print(str);
      }catch(UnsupportedEncodingException e){ 
         System.out.println("Erro: charset invalido");
      }
      return readLine();
   }
   
   /**
    * Metodo para gerar barra de progresso, sendo utilizada durante as
    * insercoes.
    * @param tamanhoArq - tamanho do arquivo total
    * @param linhaAtual - posicao atual de leitura.
    */
   public void gerarBarraProgresso(long tamanhoArq, int linhaAtual) {
      int progresso = (int) ((double) linhaAtual / tamanhoArq * 100);
      int barras = progresso / 2;
      String barra = "[";
      for (int i = 0; i < barras; i++) {
         barra += "|";
      }
      for (int i = barras; i < 50; i++) {
         barra += " ";
      }
      barra += "] " + progresso + "%";
      System.out.print("\r" + barra);
   }

   /**
     * Metodo para obter horario atual.
     * @return timestamp atual em milissegundos
    */
   public long now() {
      return System.currentTimeMillis();
   }

   /**
    * Metodo para obter o tempo de busca durante a pesquisa.
    * @param inicio - horario de inicio da busca.
    * @param fim - horario que a busca terminou.
    * @return tempo relativo em segundos.
   */
   public String getTempo(long inicio, long fim) {
      double tempo = (fim - inicio) / 1000.0;
      String strTempo = String.format("%.4f segundos", tempo);
      return strTempo;
   }
}

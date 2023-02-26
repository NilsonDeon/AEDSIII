/**
 * IO - Trabalho Pratico 01 de Algoritmos e Estruturas de Dados III
 * @author Gabriel Vargas Bento de Souza e Nilson Deon Cordeiro Filho
 * @version 1.0 02/2023
 */

import java.io.*;
import java.nio.charset.*;

public class IO {

   private static BufferedReader in = new BufferedReader(new InputStreamReader(System.in, Charset.forName("ISO-8859-1")));
   private static String charset = "ISO-8859-1";

   public IO () {}

   public int readInt(){
      int i = -1;
      try{
         i = Integer.parseInt(readLine().trim());
      }catch(Exception e){}
      return i;
   }

   public int readInt(String str){
      try {
         PrintStream out = new PrintStream(System.out, true, charset);
         out.print(str);
      }catch(UnsupportedEncodingException e){ System.out.println("Erro: charset invalido"); }
      return readInt();
   }

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

   public String readLine(String str){
      try {
         PrintStream out = new PrintStream(System.out, true, charset);
         out.print(str);
      }catch(UnsupportedEncodingException e){ System.out.println("Erro: charset invalido"); }
      return readLine();
   }
}
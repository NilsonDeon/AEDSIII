

// Bibliotecas
//package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.PrintStream;
import java.nio.charset.Charset;

public class IO {

   private static BufferedReader in = new BufferedReader(new InputStreamReader(System.in, Charset.forName("UTF-8")));
   private static String charset = "UTF-8";

   public IO () {}

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

   public int readInt(String str){
      try {
         PrintStream out = new PrintStream(System.out, true, charset);
         out.print(str);
      }catch(UnsupportedEncodingException e){
         System.out.println("ERRO: charset invalido");
      }
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
package SEIS.Trust;

import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

public class PrintData {

public void Print(String filename,String data){
	  PrintWriter fluxSortie = null;
	  
	  try
	  {
	  fluxSortie = new PrintWriter(new FileOutputStream(filename,true)); // False pour ecraser le fichier 
	  }
	  catch(FileNotFoundException e)
	  {  
		 System.out.println("Erreur d�ouverture fichier.txt.");
	     System.exit(0);
	  }
	  
	  fluxSortie.println();
	  fluxSortie.print(data);
	  fluxSortie.close( );
}

}
package SEIS.Trust;


import java.io.IOException;
import java.io.File;
import java.util.Calendar;
import java.util.Random;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class EBotnet {
	
	//----------------Paramètres de Réseau ----------------------------------
	public static final int taille = 100; // N = Nombre de pair 
	public static int K = 10; // degree node 
	public static double P = 0.1 ; // Probability small world
	public static int maxLeave = 20;
	public static int maxJoin = 10;
	public static double proJoin = 0;
	public static double proLeave = 0;
	public static int minSize = 20;
	public static int maxSize = 150;
	//----------------Paramètres de Modèle --------------------------------
	public static int nbNoeudInfected = 10; // Nombre  initial des pair infected (malicious Nodes)
	public static final double landa_S = 1; // Download
	public static double landa_E = 0.1; // Infected
	public static double landa_R = 0.05; // Infected //0.001 // 0.05
	//---------------------- Fichier ------------------------------------------------------
	public static int nbSearch = 1; // Nombre de recherche par pair
	public static final int nb_File = 4; // Nombre de fichier initial par pair
	public static int initial_Files = 19; // 19 Number of Categories disponible sur le rï¿½seau
	public static int maxHop = 7; // TTL = Time To Life 
	//----------------- Remove Trust System ------------------------------
	public static double T; //
	//----------------Paramètres de Eigen Trust ---------------------------------------------
	public static final int trustedPeers = 0; // le nombre des pairs les plus en confiance
	public static final int preTrustedPeers = 20; // le nombre des pairs qui ont jamais fait une transaction
	public static double alpha = 0.2; // Pre trusted rate //0.2
	public static double beta = 0.1; // (0.1 Trust Peer --- 0.9 Trust File)
	public static double Pi = 0.0; //0.001
	public static double Thr = 0.0; // threshold 
	public static final double intialLocalTrust = 1;
	public static final double Pd = 0.5; // La probabiltï¿½ pour un antivirus detecte un virus
	//----------------Paramètres de Simulation--------------------
	public static final int Nb_Simulation = 1;  //6000
	public static final int temps = 500;
	
	public static String filename_s;
	public static String filename_e;
	public static String filename_i;
	public static String filename_id;
	
	public static String filename_dN;
	public static String filename_dM;

	//------------------------- Tableau d'affichage ----------------
	public static double Sus[] = new double[temps];
	public static double Exp[] = new double[temps];
	public static double Inf[] = new double[temps];
	
	public static double dN[] = new double[temps];
	public static double dM[] = new double[temps];
	
	public static double nI ;
	public static double nC ;
	
	
	
	public static void main(String[] args) throws IOException {
		
		PrintData pd=new PrintData();
		File dir = new File ("D:\\Simulations\\EigenBotnet v3 - Size = "+taille+" -  Mal = "+nbNoeudInfected+" - P = "+P+" - Landa R = "+landa_R +"\\"); 
	    dir.mkdir();
	    String str = dir.getName();
	    
	    for(int p=0;p<=10;p++){   
	    	T= (double) p/10;
	    	
	    	System.out.println("Start Simulation : "+p);
	    	
	    	filename_s 	= "D:\\Simulations\\"+str+"\\Sus - T = "+T+".dat";
	    	filename_e 	= "D:\\Simulations\\"+str+"\\Exp - T = "+T+".dat";
	    	filename_i 	= "D:\\Simulations\\"+str+"\\Inf - T = "+T+".dat";
	    	filename_id = "D:\\Simulations\\"+str+"\\SS-Dow.dat";
	    	
	    	filename_dN = "D:\\Simulations\\"+str+"\\dNromal - T = "+T+".dat";
	    	filename_dM = "D:\\Simulations\\"+str+"\\dMal - T = "+T+".dat";
	    	 
	    	
	    	// Initialisation des tableaux
		    for(int i=0;i<temps;i++){
		    	Sus[i] = 0;
		    	Exp[i] = 0;
		    	Inf[i] = 0;	
		    	
		    	dN[i] = 0;
		    	dM[i] = 0;
		    }
		    
		    nC = 0;
		    nI = 0;

		    for (int s=1;s<=Nb_Simulation;s++){
		    	System.out.println("Ran : "+s);
		    	Node.nextPeerID = 0;
		    	Graph g = new Graph(taille);
				Random rand = new Random();
				Graph.wireWS(g, K, P, rand);
				g.disFiles(nb_File);
				g.setInfectedNode(nbNoeudInfected);
				g.createBotnetCluster();
				//g.setInfectedMaxDegreeNode(nbNoeudInfected);

				for(int t=0;t<temps;t++){
					int infected = 0;
					int Exposed = 0;
					int supstible = g.size();
					g.Flood(g, nbSearch);
					g.clickFile(g);
					g.prosSus();
					g.addNewReputatedNeighbor();
					g.removeUntrustedNeighbor();


					if(t == 1 || t == 499) {
						g.graphToGephiNodes(t,T);
						g.graphToGephiEdges(t,T);
					}
					
					for(int k=0;k<g.size();k++){
						Node myPeer = g.nodeList.get(k);
						if(myPeer.getEtat() == 1){
			    			infected++;
							supstible--;
							}
						if(myPeer.getEtat() == 2){
			    			Exposed++;
							supstible--;
							}
					}
					Sus[t] = Sus[t] + supstible;
					Exp[t] = Exp[t] + Exposed;
					Inf[t] = Inf[t] + infected;
					
					dN[t] = dN[t] + g.total_Normal_Degree();
					dM[t] = dM[t] + g.total_Bot_Degree();
					
					if(s==Nb_Simulation){
						pd.Print(filename_s,""+t+" "+(Sus[t]/Nb_Simulation)/g.size());
						pd.Print(filename_e,""+t+" "+(Exp[t]/Nb_Simulation)/g.size());
						pd.Print(filename_i,""+t+" "+(Inf[t]/Nb_Simulation)/g.size());
						pd.Print(filename_dN,""+t+" "+(dN[t]/Nb_Simulation));
						pd.Print(filename_dM,""+t+" "+(dM[t]/Nb_Simulation));
						}
					g.updateEtat();
				}//boucle temps	
				nI = nI + g.numberOfDownloadInfected();
				nC = nC + g.numberOfDownloadClean();
		    }//boucle Simulation
		    double infectRate = 0;
		    double infect = 0;
		    for(int i=250;i<500;i++) {
		    	infectRate = infectRate + Inf[i]; 
		    }
		    infect = (infectRate/taille)/250;
			pd.Print(filename_id,""+T+" "+(nI/(nC+nI))+" "+(nC/(nC+nI))+" "+((nI)/Nb_Simulation)+" "+((nC)/Nb_Simulation)+" "+((nC+nI)/Nb_Simulation)+" "+(infect/Nb_Simulation));
		    
			//--------------En fonction ------------------------
		    //nbNoeudInfected += 5;
		    //maxHop += 1;
		    //T_f += 0.1;
		    //P += 0.1;
		    //nbSearch++;
		    //alpha += 0.05;
			//---------------- Date -----------------
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			System.out.println("Ok : "+dateFormat.format(cal.getTime()));
		    }
	    }
}

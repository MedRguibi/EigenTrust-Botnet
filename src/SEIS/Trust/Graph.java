package SEIS.Trust;





import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;


public class Graph {
	
	public ArrayList<Node> nodeList;
	public ArrayList<Link> Liens;
	public ArrayList<Node> botnetList;
	int nLinks;
	public double C[][];
	
	public Graph(int max){
		Liens = new ArrayList<Link>();
		nodeList = new ArrayList<Node>();
		botnetList = new ArrayList<Node>();
		for(int i=0;i<max;i++){
			addNoeud();
			}
		C = new double[max][max];
		init();
		}
	//------------------------- addNode() ---------------------------------------------
	public int addNode(){
		Node n = new Node();
		nodeList.add(n);
		return nodeList.size()-1;
	}
	//--------------------------------- addLink() -----------------------------
	public boolean addLink(int from, int to) {
		Node s = nodeList.get(from);
		Node t = nodeList.get(to);
		if(!s.getNeighbours().contains(t) && !t.getNeighbours().contains(s)) {
			 Link l1 = new Link(from, to);
			 Link l2 = new Link(to, from);
	         double w = 0.5;
	         l1.weight = w;
	         l2.weight = w;
	         s.addNeighbours(t);
	         t.addNeighbours(s);
	         Liens.add(l1);
	         Liens.add(l2);
	         nLinks++;
	         return true;
		}
		
		return false;
	}
	//---------------------------------- FreeScaleBA --------------------------------------
	 public void createScaleFree(int numberOfTrustees, int delta_m) {
	        int new_node = 0; //Select a new node to add to network
	        double CDF[] = new double[numberOfTrustees];
	        nLinks = 0;
	        //Start from scratch
	        addNode(); //Create initial 3-node network
	        addNode();
	        addNode();
	        addLink(1, 0);
	        addLink(2, 0);
	        addLink(1, 2);
	        //Add a node at a time
	        while (nodeList.size() < numberOfTrustees) {
	            Node n = nodeList.get(0);
	            CDF[0] = (float) (n.getNeighbours().size()) / (2 * nLinks); //Initialize preferences
	            new_node = addNode();
	            int n_links = Math.min(delta_m, (nodeList.size() - 1)); //Delta_m must be < nNodes
	            for (int m = 1; m <= n_links; m++) { //Connect to n_links other nodes
	                double r = StdRandom.uniform(); //Sample variate from CDF
	                for (int i = 1; i < nodeList.size(); i++) { //Find preferred nodes
	                    n = nodeList.get(i);
	                    CDF[i] = CDF[i - 1] + (float) (n.getNeighbours().size()) / (2 * nLinks);
	                    int j = 0; //Destination node
	                    if (r < CDF[0] || CDF[i - 1] <= r && r < CDF[i]) {
	                        if (r < CDF[0]) {
	                            j = 0;
	                        } else {
	                            j = i;
	                        }
	                        //Avoid duplicate links
	                        //Node jj = nodes.get(j);
	                        while (!addLink(new_node, j)) {
	                            j++;
	                            if (j >= nodeList.size()) {
	                                j = 0; //Roll forward
	                            }		    					//jj = nodes.get(j);
	                        }
	                        break; //Linked!
	                    }
	                }
	            }
	        }
	    }
		//----------------------------------------------------------------------------------------------------------
		public static Graph wireWS( Graph g, int k, double p, Random r ) {
			final int n = g.size();
			for(int i=0; i<n; ++i)
			for(int j=-k/2; j<=k/2; ++j)
			{
				if( j==0 ) continue;
				int newedge = (i+j+n)%n;
				if( r.nextDouble() < p )
				{
					newedge = r.nextInt(n-1);
					if( newedge >= i ) newedge++; // random _other_ node
				}
				g.addNeighbour(i,newedge);
			}
			return g;
		}
		//----------------------------------------------------------------------------------------------------------
		public static Graph CreateSmallWorld(Graph g, double p, int K){
			final int n = g.size();
			
			int kk = K / 2;

			for (int i = 0; i < n; i++) {
				for (int j = 1; j <= kk; j++) {
					int jj = (i + j) % n;
					g.addNeighbour(i, jj);
					jj = (i - j + n) % n;
					g.addNeighbour(i, jj);
				}
			}
			
			for(int j = 0;j<n;j++){
				Node v = g.nodeList.get(j);
				for(int k = 0;k<v.getNeighbours().size();k++){
					int l =  v.getNeighbours().get(k).getId();
					if(Math.random()<p){
						int Rand;
						do{
							 Rand = (int) (n*Math.random());
						}while(j==Rand || v.getNeighbours().contains(g.nodeList.get(Rand)) || l == Rand);
						g.addNeighbour(l,Rand);
					}
				}
			}
			return g;
		}
	//---------------------------------------------------------------------------------
	public void addNoeud(){
		nodeList.add(new Node());
		}
	//--------------------------------------------------------------------------------
	public void addNeighbour(int a, int b){
		if(!nodeList.get(a).getNeighbours().contains(nodeList.get(b))){
			nodeList.get(a).addNeighbours(nodeList.get(b));
			//Liens.add(new Link(a,b));
			Liens.add(new Link(b,a));
		}
		if(!nodeList.get(b).getNeighbours().contains(nodeList.get(a))){
			nodeList.get(b).addNeighbours(nodeList.get(a));
		}
	}
	//---------------------------------------------------------------------------------------------
	public void SuppLien(int a, int b){
		nodeList.get(a).suppNeighbours(nodeList.get(b));
		nodeList.get(b).suppNeighbours(nodeList.get(a));
	}
	//--------------------------------------------------------------------------------------
	public boolean addNewNeighbour(Node a, Node b){
		if(!a.getNeighbours().contains(b) && !b.getNeighbours().contains(a)){
			a.addNeighbours(b);
			b.addNeighbours(a);
			return true;
		}else return false;
	}
	//---------------------------------------------------------------------------------------------
	//----------------------------------------------------------------------------------------------
	public int size(){
		return nodeList.size();
		}
	//---------------------------------------------------------------------------------------------------
	public void disFiles(int max){
		Node myPeer;
		for(int i=0;i<size();i++){
			myPeer = nodeList.get(i);
			File file;
			for(int j=0;j<max;j++){
				do{
					file = new File();
				}while(file.getInfected() != 0 || myPeer.doExistFile(file.id));
				myPeer.addFile(file);
			}
		}
	}
	//---------------------------------------------------------------------------------------------------
	public void addNewFile(int max){
		Node myPeer;
		for(int i=0;i<max;i++){
			File file = null;
			for(int j=0;j<size();j++){
				myPeer = nodeList.get(j);
				if(myPeer.getFileList().size()<EBotnet.initial_Files){
					do{
						file = new File();
					}while(myPeer.doExistFile(file.id));
					myPeer.addFile(file);
				}
			}
		}
	}
	//-----------------------------------------------------------------------------------------------------
	//-----------------------------------------------------------------------------------------------------
	public void setInfectedFile(int nb_Inf){
		for(int i=0;i<nb_Inf;i++){
			Random r = new Random();
			int temp;
			do{
				temp = r.nextInt(size());
			}while(nodeList.get(temp).getEtat()==1);
			File f = new File();
			f.setInfected();
			nodeList.get(temp).addFile(f);
			nodeList.get(temp).setInf();
		}
	}
	//-----------------------------------------------------------------------------------------------------
	public void setInfectedNode(int nb_Inf){
		for(int i=0;i<nb_Inf;i++){
			Random r = new Random();
			int temp;
			do{
				temp = r.nextInt(size());
			}while(nodeList.get(temp).getEtat()==1);
			for(int j=0;j<nodeList.get(temp).getFileList().size();j++){
				nodeList.get(temp).getFileList().get(j).setInfected();
			}
			nodeList.get(temp).setInf();
			nodeList.get(temp).setMal(1);
			botnetList.add(nodeList.get(temp));
		}
	}
	//---------------------------- Botnet Cluster ------------------------------------------------
	//---------------------------------------------------------------------------------------------
	public void createBotnetCluster() {
		Node Bot;
		Node newLink;
		for(int i=0;i<botnetList.size();i++) {
			Random r = new Random();
			int temp;
			Bot = botnetList.get(i);
			int comp = 0;
			while(true) {
				temp = r.nextInt(botnetList.size());
				newLink = botnetList.get(temp);
				if(Bot.getId()==newLink.getId())
					continue;
				addNewNeighbour(Bot,newLink);
				if(comp>=botnetList.size())
					break;
				comp++;
			}	
		}
	}
	//------------------------------------ Add reputated Peer trans ------------------------------
	//--------------------------------------------------------------------------------------------
	public void addNewReputatedNeighbor() {
		Node Peer;
		for(int i=0;i<size();i++){
			Peer = nodeList.get(i);
			if(Peer.getMal() == 1)
				continue;
			Node newNeighbor = Peer.getReputatedPeer();
			if(newNeighbor != null) {
				addNewNeighbour(Peer,newNeighbor);
			}
		}
	}
	//---------------------------------- Remove Untrusted Neighbor -------------------------------
	//--------------------------------------------------------------------------------------------
	public void removeUntrustedNeighbor() {
		Node Peer;
		for(int i=0;i<size();i++){
			Peer = nodeList.get(i);
			if(Peer.getMal() == 1)
				continue;
			Node Untrusted = Peer.getUntrustedNeighbor();
			if(Untrusted != null) {
				//double p = (Untrusted.getGlobalTrust()*size())/(Peer.peerNeighRating(Untrusted)*Untrusted.getNeighbours().size());
				double p = (Untrusted.getGlobalTrust()*size())/(ratePeer(Untrusted)*Untrusted.getNeighbours().size());
				//System.out.println(Peer.peerNeighRating(Untrusted)+" "+p+" "+Untrusted.getMal());
				if(EBotnet.T>p) {
					//System.out.println(Peer.peerNeighRating(Untrusted)+" "+p+" "+Untrusted.getMal());
					//System.out.println(ratePeer(Untrusted)+" "+p+" "+Untrusted.getMal());
					Peer.suppNeighbours(Untrusted);
					Untrusted.suppNeighbours(Peer);
				}
			}	
		}
	}
	//----------------------------------- Rating Peer ---------------------------------------------
	//---------------------------------------------------------------------------------------------
	public double ratePeer(Node p) {
		double rate = 0.0;
		int count = 0;
		Node Peer;
		for(int i=0;i<size();i++){
			Peer = nodeList.get(i);
			if(Peer.peerTrans(p)) {
				count++;
				rate = rate + Peer.peerRating(p);
			}
		}
		if(count == 0) {
			return 0.001; //1.0
		}else {
			return (double) rate/count;
			}
	}
	//----------------------------------- Isolation Process ---------------------------------------
	//---------------------------------------------------------------------------------------------
	public void isolationProcess() {
		addNewReputatedNeighbor();
		removeUntrustedNeighbor();
	}
	//---------------------------------------------------------------------------------------------
	public int sizeOfActifNode(){
		int size = 0;
		Node p; 
		for(int i = 0;i<size;i++){
			p = nodeList.get(i);
			if(p.getEtat() == 4)
				continue;
			size++;
		}
		return size;
	}
	//---------------------------------------------------------------------------------------------
	public double clustering_coefficient(Node  v){
        if (v.getNeighbours().size()<=1) return 0.0;
        double l = 0.;
        for (int j = v.getNeighbours().size() - 1; j >= 0; j--) {
            Node w = v.getNeighbours().get(j);
            if(w.getEtat()==4)
            	continue;
            for (int x = v.getNeighbours().size() - 1; x >= 0; x--) {
                Node u = v.getNeighbours().get(x);
                if(u.getEtat()==4)
                	continue;
                if ( w.getNeighbours().contains(u)) l += 0.5;
                }
        }
         return 2.0*l/(v.getNeighbours().size()*(v.getNeighbours().size()-1));
	}
	//---------------------------------------------------------------------------------------------
	public double total_CC(){
        double total = 0;
        int size = 0;
        for (int v = 0; v < size(); v++) {
        	if(nodeList.get(v).getEtat()==4)
        		continue;
            total += clustering_coefficient(nodeList.get(v));
            size++;
        }
        return (double) total/size;
        }
	//----------------------------------------------------------------------------------------------
	public int Degree(Node node){
		if(node.getNeighbours() == null)
			return 0;
		else 
			return node.getNeighbours().size();
	}
	//-------------------------------------------------------------------------------
	public double total_Degree(){
		double total = 0.;
        for (int v = 0;v<size(); v++) {
            total += Degree(nodeList.get(v));
        }
        return (double) total/size();
	}
	//-------------------------------------------------------------------------------
	public double total_Normal_Degree(){
		double total = 0.;
        for (int v = 0;v<size(); v++) {
        	total += DegreeMal(nodeList.get(v));
        }
        return (double) total/(size()-botnetList.size());
	}
	//-------------------------------------------------------------------------------
	//----------------------------------------------------------------------------------------------
	public int DegreeMal(Node node){
		if(node.getNeighbours() == null)
			return 0;
		else {
			int count = 0;
			for(int i=0;i<node.getNeighbours().size();i++) {
				if(node.getNeighbours().get(i).getMal() == 1)
					count++;
			}
			return count;
		}	
	}
	//-------------------------------------------------------------------------------
	public double total_Bot_Degree(){
		double total = 0.;
        for (int v = 0;v<botnetList.size(); v++) {
        	total += Degree(botnetList.get(v));
        }
        return (double) total/botnetList.size();
	}
	//------------------------------------------------------------------------------
	public void Flood(Graph g,int nb_search){
		for(int i=0;i<g.size();i++){
			Node myPeer = g.nodeList.get(i);
			myPeer.searchForFile(nb_search);
		}
	}
	//-------------------------------------------------------------------------------
		public int nFile(){
			int totalFile = 0;
			Node Peer;
			for(int i=0;i<size();i++){
				Peer = nodeList.get(i);
				totalFile += Peer.getFileList().size();
			}
			return totalFile;
		}
	//-----------------------------------------------------------------------------------------------------
		public int nFileInf(){
			int totalFile = 0;
			Node Peer;
			for(int i=0;i<size();i++){
				Peer = nodeList.get(i);
				for(int j=0;j<Peer.getFileList().size();j++){
					if(Peer.getFileList().get(j).getInfected() == 1)
						totalFile++;
				}
			}
			return totalFile;
		}
	//-----------------------------------------------------------------------------------------------------
		public double rateQ(){
			double rate = (double) nFileInf()/(nFile()+nFileInf());
			return rate;
		}
		//------------------------------------------------------------------------------------------------
		public void updateEtat(){
			for(int k=0;k<size();k++){
				Node myPeer = nodeList.get(k);
				myPeer.updateEtat();
				}
		}
		//---------------////------------------------------------------------------------------------------
		public void clickFile(Graph g){
			Node Peer;
			for(int i = 0;i<g.size();i++){
				Peer = g.nodeList.get(i);
				if(Peer.getMal()==0)
					Peer.chooseFile();
				else
					Peer.chooseRandomFile();	
				//Peer.ChooseEigen(); // Avec Eigen Trust
				Peer.traitReply(); 
				if(Peer.getEtat() == 2 && Peer.getMal() != 1 )
					Peer.clickFileInfection(EBotnet.landa_E); 
			}
			g.C();
		}
		//------------------------------------------------------------------------------------------------
		public void prosSus(){
			for(int k=0;k<size();k++){
				Node myPeer = nodeList.get(k);
				//Recovred
				if(myPeer.getEtat() == 1 && myPeer.getMal() == 0){
					double r = Math.random();
					if(EBotnet.landa_R>r){
						myPeer.setSus();
						for(int q=0;q<myPeer.getFileList().size();q++){
							File f = myPeer.getFileList().get(q);
							myPeer.getFileList().remove(f);
							}
						//myPeer.removeUntrustedNeighbour();
						}
					}
				}
		}
		//------------------------------------------------------------------------------------------------------
		public int nInfectedPeer(Graph g){
			int infected = 0;
			Node myPeer;
			for(int i=0;i<g.size();i++){
				myPeer = g.nodeList.get(i);
				if(myPeer.getEtat() == 1)
					infected++;
				}
			return infected;
		}
		//----------------------------------------------------------------------------------------
		public double rateOfFile(File f){
			double som = 0; 
			Node myPeer;
			for(int i=0;i<nodeList.size();i++){
				myPeer = nodeList.get(i);
				som = som + myPeer.Rhi(f);
			}
			return (double) som/size(); 
		}	
	//-----------------------------------------------------------------------------------------------------
		public double [] disMcl(){
			int nc = 0; // nombre de cluster
			int ic_tot = 0;
			int ic2_tot= 0;
			int nc_tot = 0;

			int ig[] = new int[size()];
			for(int i=0;i<ig.length;i++){
				ig[i]=0;
				}
			double vg[] = new double[size()];
			for(int i=0;i<vg.length;i++){
				vg[i]=0;
				}
			Node next [] = new Node[size()];
			
			for(int k=0;k<nodeList.size();k++){
				nodeList.get(k).wasVisited = false;
			}
			
			for(int k=0;k<nodeList.size();k++){
				Node myPeer = nodeList.get(k);
				if(myPeer.wasVisited || myPeer.getEtat() != 1) 
					continue;
				//c k is the seed of a new cluster
					nc = nc + 1;
				//c start growing a new cluster around site k
				int ic  = 1;
				int	in  = ic;
				next[in] = myPeer; 
				myPeer.wasVisited = true;

				while (in <= ic){
					Node  k0  = next[in];
					for(int i=0;i<k0.getNeighbours().size();i++){ 
						Node k1 = k0.getNeighbours().get(i);
						if (k1.wasVisited) 
							continue;     
						if (k0.getEtat() != 1 || k1.getEtat() != 1)  
							continue;
						ic  =  ic + 1;
						k1.wasVisited= true ;
						next[ic] = k1;
						}
					in = in + 1;
				}
				ic_tot = ic_tot + ic;
				ic2_tot = ic2_tot + ic^2;
			
				for( int i=0; i<ig.length;i++){
					if(i == ic)  
						ig[i] = ig[i] + 1;
				    }
				}
			nc_tot = nc_tot + nc;
			
			for( int i=0; i<vg.length;i++){ 
					vg[i] = (double) ig[i] / nc_tot;
			    }
			
			return vg;
			}
		//-----------------------------------------------------------------------------------------------------
		public double Mcl(){
			int nc = 0; // nombre de cluster
			int ic_tot = 0;
			int ic2_tot= 0;
			int nc_tot = 0;

			double ig[] = new double[size()];
		
			Node next [] = new Node[size()+1];
			
			for(int k=0;k<nodeList.size();k++){
				nodeList.get(k).wasVisited = false;
			}
			
			for(int k=0;k<nodeList.size();k++){
				Node myPeer = nodeList.get(k);
				if(myPeer.wasVisited || myPeer.getEtat() != 1) 
					continue;
				//c k is the seed of a new cluster
					nc = nc + 1;
				//c start growing a new cluster around site k
				int ic  = 1;
				int	in  = ic;
				next[in] = myPeer; 
				myPeer.wasVisited = true;

				while (in <= ic){
					Node  k0  = next[in];
					for(int i=0;i<k0.getNeighbours().size();i++){ 
						Node k1 = k0.getNeighbours().get(i);
						if (k1.wasVisited) 
							continue;     
						if (k0.getEtat() != 1 || k1.getEtat() != 1)  
							continue;
						ic  =  ic + 1;
						k1.wasVisited= true ;
						next[ic] = k1;
						}
					in = in + 1;
				}
				ic_tot = ic_tot + ic;
				ic2_tot = ic2_tot + ic^2;
				
				for(int s=0;s<ig.length;s++){
					ig[s]=0;
					}
				
				for( int s=0; s<ig.length;s++){
					if(ic == s)  
						ig[s] = ig[s] + 1;
				    }
				}
			nc_tot = nc_tot + nc;
			
			return (double) ic_tot/nc;
			}
	//------------------------------------------------------------------------------------------------------
		public class LayoutReply {
			int big; //Diameter
			int small; //Central radius
			double mean; //Average path length
			double efficiency; //Link efficiency
			}

	//------------------------------------------------------------------------------------------------------
			public LayoutReply doFindCenters(){
				LayoutReply reply = new LayoutReply(); //Returned values
				int k = 0;
				int j = 0;
				int max_radius = 0; //Max radius of a node
			    int min_radius = 0; //Min radius of a node
			    double mean_radius = 0.0; //Return avg. path length
			    Node next_node; //Neighbor at level k+1
			    int n_paths = 0; //#paths in network
			    
			    for(int i = 0; i < size(); i++){ //Average over all nodes
			    	Queue<Integer> s = new LinkedList<Integer>();
		            Node n0 = nodeList.get(i);
		            if(n0.getEtat() == 4)
		            	continue;
		            n0.level = 0; k = 0; //Starting level
		            for(j = 0; j < size(); j++){ //Reset flags
		        	   Node n = nodeList.get(j);
		               n.visited = false; //Clear previous settings
		               n.level = 0;
		            }
		            s.add(i); //Remember n0 = v
		            
		            while(!s.isEmpty()){
		                   j = s.remove(); //Recall n0 
		                   Node n = nodeList.get(j);
		                   n.visited = true; //Avoid circuit
		                   k = n.level; //Recall level
		                   for(int e = 0; e < n.getNeighbours().size(); e++){ //Visit neighbors 
		                	   next_node = n.getNeighbours().get(e);
		                	   if(next_node.getEtat() == 4)
		                		   continue;
		                	   if(!next_node.visited){
		                		   next_node.level = k + 1;
		                		   next_node.visited = true;
		                		   s.add(next_node.getId());
		                	   }
		                   }
		            }
		           max_radius = 0; //Find largest radius
		           for(int d = 0; d < size(); d++){
		        	   Node v = nodeList.get(d);
		        	   if(v.level > max_radius) max_radius = v.level;
		        	   if(v.level > 0) { //Ignore all others
		        		   n_paths++;
		        		   mean_radius += v.level; //Avg. over all non-zeros
		        	   }
		           }
		           n0.radius = max_radius; //Maximum distance to all
			    }
			    min_radius = size()+1; //Center
		        max_radius = 0; //Diameter
		        
		        for(int d = 0; d < size(); d++){
		        	Node v = nodeList.get(d);
		            if(v.radius > 0 && (v.radius <= min_radius)) {
		            	min_radius = v.radius;
		            }
		            if(v.radius >= max_radius) max_radius = v.radius;
		        }
		        
		        if(min_radius == (size()+1)) min_radius = 0; //Must be no path
		        
		        for(int d = 0; d < nodeList.size(); d++) { //Could be many centers
		        	Node v = nodeList.get(d);
		        	if(v.radius == min_radius) v.center = true;
		        	if(v.radius == max_radius) v.outer = true;
		        }
		        
		        reply.big = max_radius; //Return diameter
		        reply.small = min_radius; //Return center(s)
		        
		        if(n_paths > 0)
		                reply.mean = mean_radius/n_paths; //Return avg. path length
		        else reply.mean = 0.0; //Return link efficiency
		        
		        int nLinks = 0;
		        for(int m=0;m<size();m++){
		        	Node v = nodeList.get(m);
		        	nLinks+=v.getNeighbours().size();
		        }
		        nLinks=nLinks/2;
		        reply.efficiency = (nLinks - reply.mean)/nLinks;        
		        return reply;
			}
		//-------------------------------------------------------------------------------------------------------------
		// Rupetition 
			public void C() {
				Node a;
				Node b;
				for(int i=0;i<size();i++){
					a = nodeList.get(i);
					for(int j=0;j<size();j++){
						if(i == j){
							if(a.getTrans().isEmpty()){
								C[i][j] = (double) 1/EBotnet.preTrustedPeers;
							}else{
								C[i][j] = 0.0;
							}	
						}else{
							b = nodeList.get(j);
							C[i][j] = a.localTrustEigen(b);
						}
					}
				}
				int it = 0; //--------------------
				while(it<=100){
					double t = 1/EBotnet.preTrustedPeers; //0.0;
					double tk = 0.0;
					for(int i=0;i<size();i++){
						double cc = 0.0;
						double score = 0.0;
						for(int j=0;j<size();j++){
							cc = cc + (C[j][i] * nodeList.get(j).getGlobalTrust());
						}
						score = ((1 - EBotnet.alpha) * cc) + (EBotnet.alpha * ((double) 1/EBotnet.preTrustedPeers));
						t = t + nodeList.get(i).getGlobalTrust();
						nodeList.get(i).setGlobalTrust(score);
						tk = tk + score;
					}
					if(Math.abs((tk-t)) < 0.0000001)
						break;
					it++;
				}
				
			}
	//-----------------------------------------------------------------------------------------------------------
	//Affiche C 
			public void DisplayC(){
				for(int i=0;i<C.length;i++){
					for(int j=0;j<C.length;j++){
						System.out.print(C[i][j]+"\t | ");
					}
					System.out.println("");
				}
			}
	//-------------------------------------------------------------------
			//----------------------------------------------------------------------------------------------------------
			//intialisation.
			public void init(){
				for(int i=0;i<nodeList.size();i++){
					nodeList.get(i).setGlobalTrust((double)1/EBotnet.preTrustedPeers);
				}
			}
			//----------------------------------------------------------------------------------------------------------
			public int numberOfDownload(){
				int Total = 0; 
				for(int i=0;i<size();i++){
					Total = Total + nodeList.get(i).getReply();
					}
				return Total; 
				}
			//------------------------------------------------------------------
			public int numberOfDownloadClean(){
				int Total = 0; 
				for(int i=0;i<size();i++){
					Total = Total + nodeList.get(i).getNDClean();
					}
				return Total; 
				}
			//------------------------------------------------------------------
			public int numberOfDownloadInfected(){
				int Total = 0; 
				for(int i=0;i<size();i++){
					Total = Total + nodeList.get(i).getNDInfected();
					}
				return Total; 
				}
			//------------------------------------------------------------------
			public void cleanDownloadPeers(){
				for(int i=0;i<size();i++){
					nodeList.get(i).iniNDClean();
					nodeList.get(i).iniNDInfected();
					}
			}
			//----------------------------------------------------------------------------------------------------
			public  ArrayList <Node> Max_Degree_Nodes(int max) {
				 ArrayList<Node> max_nodes = new ArrayList<Node>();
				 ArrayList<Integer> list1 = new ArrayList<Integer>();
				 list1.add(nodeList.get(0).getNeighbours().size());
				 
				 for(int i=0;i<size();i++){
		             if(list1.contains(nodeList.get(i).getNeighbours().size())) 
		            	 continue;
		             list1.add(nodeList.get(i).getNeighbours().size());
				 }
				 Collections.sort(list1);
				 for (int v = list1.size()-1; v >= 0; v--) {
					 for (int n = 0; n < size(); n++) {
						 if(nodeList.get(n).getNeighbours().size() == list1.get(v)) 
							 max_nodes.add(nodeList.get(n));
						 if(max_nodes.size() == max) 
							 break;
						 }
					 if(max_nodes.size() == max) 
						 break;
					 }
				 return max_nodes; 
			}
			//-----------------------------------------------------------------------------------------------------
			public void setInfectedMaxDegreeNode(int nb_Inf){
				ArrayList<Node> max_nodes =  Max_Degree_Nodes(nb_Inf);
				for(int i=0;i<max_nodes.size();i++){ 
					Node myPeer = max_nodes.get(i);
					for(int j=0;j<myPeer.getFileList().size();j++){
						myPeer.getFileList().get(j).setInfected();
					}
					myPeer.setInf();
					myPeer.setMal(1);
				}
			}
			public Node getNodeById(int id) {
				Node myPeer = null;
				for(int i=0;i<size();i++){
					if(nodeList.get(i).getId() == id)
						myPeer = nodeList.get(i);	
				}
				return myPeer;
			}
			//-----------------------------------------------------------------------------------------------------
		    public int[] computeDegreeDistributionSusceptible(Graph g){
		        int distribution[] = new int[g.size()+1];
		    	int degree = 0;  //temporary Variable
		    	Node myPeer;
		    	for (int i=0; i<g.size(); i++){
		    		myPeer = g.nodeList.get(i);
		    		if(myPeer.getEtat() == 0) {
		        		degree = myPeer.getNeighbours().size();
		        		distribution[degree]++;
		    		}
				}
		        return distribution;
		    }
			//-----------------------------------------------------------------------------------------------------
		    public int[] computeDegreeDistributionInfected(Graph g){
		        int distribution[] = new int[g.size()+1];
		    	int degree = 0;  //temporary Variable
		    	Node myPeer;
		    	for (int i=0; i<g.size(); i++){
		    		myPeer = g.nodeList.get(i);
		    		if(myPeer.getEtat() == 1) {
		        		degree = myPeer.getNeighbours().size();
		        		distribution[degree]++;
		    		}
				}
		        return distribution;
		    }
		    
			//---------------------------------------------------------------------------------------------
			public void addNewNodes(int n) {
				for(int i=0;i<n;i++) {
					nodeList.add(new Node());
					Node newPeer = nodeList.get(size()-1);
					while(newPeer.getNeighbours().size()<=EBotnet.K) {
						Random r = new Random();
						Node Peer = nodeList.get(r.nextInt(size()));
						addNewNeighbour(newPeer,Peer);
					}
				}
			}
			public void erasefromNieghbors(Node n) {
				for(int i=0;i<n.getNeighbours().size();i++) {
					Node neigh = n.getNeighbours().get(i);
					neigh.suppNeighbours(n);
				}
			}
			
			public void removeOldNodes(int n) {
				int i = 0;
				while(i<n) {
					Random r = new Random();
					Node peer = nodeList.get(r.nextInt(size()));
					if(peer.getMal() == 0) {
						erasefromNieghbors(peer);
						nodeList.remove(peer);
						i++;
						}
					}
				}
			public void join_departNodes() {
				double r = Math.random();
				if(((nodeList.size() - EBotnet.maxLeave) >= EBotnet.minSize) && (EBotnet.proLeave>r))
					removeOldNodes(EBotnet.maxLeave);
				r = Math.random();
				if(((nodeList.size() + EBotnet.maxJoin) <= EBotnet.maxSize) && (EBotnet.proJoin>r))
					addNewNodes(EBotnet.maxJoin);
			}
			//---------------------------------------------------------------------------------------------
	//-----------------------------------------------------------------------------------------------------
	public void displayGraph(){
		for(int i=0;i<nodeList.size();i++)
			System.out.println(nodeList.get(i).toString());
		}
	//------------------------------------------------------------------------------------------------------
	public void graphToGephiNodes(int t, double tr) throws IOException{
		PrintWriter No;
		No =  new PrintWriter(new BufferedWriter(new FileWriter("Nodes = "+t+" - "+tr+".csv")));
		No.println("id,etat,mal");
		for(int i=0;i<nodeList.size();i++){
			No.println(nodeList.get(i).getId()+1+","+nodeList.get(i).getEtat()+","+nodeList.get(i).getMal());
			}
		No.close();
		}
	//--------------------------------------------------------------------------------------------------------
	public void graphToGephiEdges(int t, double tr) throws IOException{
		PrintWriter Ed;
		Ed =  new PrintWriter(new BufferedWriter(new FileWriter("Edges = "+t+" - "+tr+".csv")));
		int Edge = 1;
		Ed.println("Source,Target,Type,Id");
		for(int i=0;i<nodeList.size();i++){
			for(int j=0;j<nodeList.get(i).getNeighbours().size();j++){
				Ed.print((nodeList.get(i).getId()+1)+","+(nodeList.get(i).getNeighbours().get(j).getId()+1)+",Undirected,"+Edge);
				Ed.println();
				Edge++;
				}
			}
		Ed.close();
		}
	}

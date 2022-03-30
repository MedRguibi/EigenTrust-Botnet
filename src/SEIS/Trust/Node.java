package SEIS.Trust;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;




public class Node {
	
	protected static int nextPeerID = 0;
	private int id ; // l'identifient du noeud 
	private int etat; // l'etat de l'infection du noeud 0 = Susptible 1 = infecté
	private int etatp;
	private double landa;
	private int Mal;
	private int reply; // Number of replies 
	private int request; // Number of requests
	private double globalReputationScore;
	private Vector<Node> neighbours; // list des voisins
	private List<File> fileList;
	private Vector<Query> Querys;
	private Vector<Reply> Replies;
	private Vector<listDown> downloads;
	private Vector<Transaction> Trans;  //--------------la liste des des transaction 
	private int nombreOfDownClean;
	private int nombreOfDownInfected;

	public boolean wasVisited = false;
	public boolean visited = false;
	public boolean outer = false;
	public boolean center = false;
	public int radius = 0;
	public int level = 0;
	
	
	// ------------------------------- Constructeur -------------------------------------------
	public Node() {
		nextPeerID++;
		id = nextPeerID;
		etat = 0;
		etatp = 0;
		neighbours = new Vector<Node>();
		fileList = new ArrayList<File>(); 
		Querys = new Vector<Query>();
		Replies = new Vector<Reply>();
		Trans = new Vector<Transaction>();
		downloads = new Vector<listDown>();
		landa = EBotnet.landa_E;
		Mal = 0;
		reply = 0;
		request = 0;
		globalReputationScore = 0;
		nombreOfDownClean = 0;
		nombreOfDownInfected = 0;
	}
	//---------------------------------------------------------------------------------------------
	public int getId() {
		return id;
	}
	public int getMal() {
		return Mal;
	}
	public void setMal(int mal) {
		Mal = mal;
	}
	//----------------------------------------------------------------------------------------------
	public int getNDClean(){
		return nombreOfDownClean;
	}
	public int getNDInfected(){
		return nombreOfDownInfected;
	}
	public void iniNDClean(){
		nombreOfDownClean = 0;
	}
	public void iniNDInfected(){
		nombreOfDownInfected = 0;
	}
	//-----------------------------------------------------------------------------------------------
	public int getReply(){
		return reply;
	}
	public void augReply(){
		reply++;
	}
	public int getRequests(){
		return request;
	}
	public void augResqusts(){
		request++;
	}
	//-----------------------------------------------------------------------------------------------
	public int getEtat() {
		return etat;
	}

	public void setEtat(int etat) {
		this.etat = etat;
	}
	//------------------------------------EigenTrust --------------------------------------
	//-------------------------------------------------------------------------------------
	public double getGlobalTrust() {
		return globalReputationScore;
	}
	public void setGlobalTrust(double globalReputationScore) {
		this.globalReputationScore = globalReputationScore;
	}
	//------------------------------- EigenTrust ---------------------------------------------
	//-------------------------------- Local EigenTrust --------------------------------------
	public double localTrustEigen(Node Target){
		double local = (double) 1/EBotnet.preTrustedPeers;
		//double local = 0.01;
		double Sat = 0.0;
		if(!Trans.isEmpty()){
			for(int i=0;i<Trans.size();i++){
				if(Trans.get(i).getServer().equals(Target)){
					Sat = Sat + Trans.get(i).getrFile();
				}
			}
			local = (double) Sat/Trans.size();
			if(local<=0){local = 0;}
		}
		return local;
	}
	//---------------------------------Choose EigenTrust---------------------
	//-----------------------------------------------------------------------
	public void ChooseEigen(){
		if (downloads.size() != 0) {
			listDown down;
			double temp = 0;
			int id = -1;
			for (int q=0; q<downloads.size(); q++){
				down = downloads.get(q);
				if(down.getServer().getGlobalTrust() > temp){
					temp = down.getServer().getGlobalTrust();
					id = q;
					}
			}
			if(id != -1){
				down = downloads.get(id);
				Reply rep = new Reply(down.getServer(),down.searchedFile());
				addReplies(rep);
				}
			removeAllDown();
			}
	}
	//----------------------------------Version 3-----------------------------------------------
	//-----------------------------------------------------------------------------------
/*	public void chooseFile(){
		if (downloads.size() != 0) {
			listDown down;
			int id = 0;
			for (int q=0; q<downloads.size(); q++){
				down = downloads.get(q);
				if((down.searchedFile().getInfected() == 1 && down.getServer().getMal() == 1) ||
						(down.searchedFile().getInfected() == 0 && down.getServer().getMal() == 0)){
					id = q;
					}
				}
			down = downloads.get(id);
			Reply rep = new Reply(down.getServer(),down.searchedFile());
			addReplies(rep);
			removeAllDown();
			}
	}*/
	//----------------------------------Version 2-----------------------------------------------
	//-----------------------------------------------------------------------------------
	public void chooseFile(){
		if (downloads.size() != 0) {
			listDown down;
			int id = 0;
			for (int q=0; q<downloads.size(); q++){
				down = downloads.get(q);
				if((down.searchedFile().getInfected() == 1 && down.getServer().getMal() == 1)){
					id = q;
					}
				}
			down = downloads.get(id);
			Reply rep = new Reply(down.getServer(),down.searchedFile());
			addReplies(rep);
			removeAllDown();
			}
	}
	//--------------------------------------------------------------------------------
	//------------------------------------SEIRS----------------------------------------
	public void setExp(){
		etatp = 2; // Exposed
	}
	public void setInf(){
		etatp = 1; // Infected
	}
	public void setSus(){
		etatp = 0; // Susceptible
	}
	public void updateEtat(){
		etat = etatp;
	}
	//---------------------------------------------------------------------------------
	public double getLanda() {
		return landa;
	}
	public void setLanda(double landa) {
		this.landa = landa;
	}
	//-----------------------------------------------------------------------------
	public Vector<Node> getNeighbours() {
		return neighbours;
	}
	public void addNeighbours(Node neigh) {
		neighbours.add(neigh);
	}
	public void suppNeighbours(Node neigh) {
		neighbours.remove(neigh);
	}
	public int actifNeigh(){
		int neigh = 0;
		for(int i=0;i<neighbours.size();i++){
			if(neighbours.get(i).getEtat()==4)
				continue;
			neigh++;
		}
		return neigh;
	}
	//------------------------------------------------------------------------------
	public List<File> getFileList() {
		return fileList;
	}
	public void addFile(File file) {
		if(!fileList.contains(file)){
			fileList.add(file);
		}
	}
	//-------------------------------- Transactions -----------------------------------------------
	public Vector<Transaction> getTrans() {
		return Trans;
	}
	public void addTrans(Transaction trans) {
		Trans.add(trans);
	}
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	//--------------------------------------------------------------------------------------------------------
	public Node getReputatedPeer() {
		Node myPeer;
		Node chosen = null;
		double max = 1.0;
		for(int i=0;i<Trans.size();i++) {
			myPeer = Trans.get(i).getServer();
			if(myPeer.getGlobalTrust()>max) {
				max = myPeer.getGlobalTrust();
				chosen = myPeer;
			}
		}
		return chosen;
	}
	//----------------------------------------------------------------------------------------------------------
	public Node getUntrustedNeighbor() {
		Node neigh;
		Node chosen = null;
		double min = 1.0;
		for(int i=0;i<neighbours.size();i++) {
			neigh = neighbours.get(i);
			if(neigh.getGlobalTrust()<min) {
				min = neigh.getGlobalTrust();
				chosen = neigh;
			}	
		}
		if(this.getGlobalTrust()>=min)
			return chosen;
		else
			return null;
	}
	//------------------------------------------------------------------------------------------------------------
/*	public void removeUntrustedNeighbour() {
		Node Untrusted = getUntrustedNeighbor();
		if(Untrusted != null) {
			double p = 1 - (Untrusted.getGlobalTrust()*EBotnet.taille/neighbours.size());
			if(p>EBotnet.T) {
				suppNeighbours(Untrusted);
				Untrusted.suppNeighbours(this);
			}
		}
	}*/
	//-------------------------------------------------------------------------------------
	//------------------------------------------------------------------------------------
	////////////// is that peer have a transaction with a neighbour or not 
	//------------------------------------------------------------------------------------
	public double peerRating(Node peer) {
		int count = 0;
		int nTrans = 0;
		Transaction T;
		boolean flag = false;
		for(int i=0;i<Trans.size();i++) {
			T = Trans.get(i);
			if(T.getServer().equals(peer)) {
				flag = true;
				nTrans++;
				if(T.isSatisfied()== false)
					count++;
			}
		}
		if(flag) {
			if(count!=0)
				return (double)count/nTrans;
			else
				return 0.0001;
		}else
			return 0.0001;
	}
	
	public boolean peerTrans(Node peer) {
		Transaction T;
		boolean flag = false;
		for(int i=0;i<Trans.size();i++) {
			T = Trans.get(i);
			if(T.getServer().equals(peer)) {
				flag = true;
			}
		}
		return flag;
	}
	public double peerNeighRating(Node peer) {
		double rate = 0.0;
		int count = 0;
		Node neigh;
		for(int i=0;i<neighbours.size();i++) {
			neigh = neighbours.get(i);
			if(neigh.peerTrans(peer)) {
				count++;
				rate = rate + neigh.peerRating(peer);
			}
		}
		if(count == 0) {
			return 1.0;
		}else {
			return (double) rate/count;
			}
	}
	//---------------------------------------------------------------------------------------
	//--------------------------------- Query ---------------------------------------------
	public Vector<Query> getQuerys() {
		return Querys;
	}
	public void addQuerys(Query querys) {
		Querys.add(querys);
	}
	//-------------------------------------------------------------------------------
	public Vector<Reply> getReplies() {
		return Replies;
	}

	public void addReplies(Reply replies) {
		Replies.add(replies);
	}
	//------------------------------------------------------------------------
	public Vector<Reply> getDown() {
		return Replies;
	}
	public void addDown(listDown Down) {
		downloads.add(Down);
	}
	public void removeAllDown() {
		downloads.clear();
	}
	//----------------------------------------------------------------------------------
	public Query genQuery(){
		File file;
		Query query = null;
		do{
			file = new File();
		}while(doExistFile(file.id));
		query = new Query(this, file);
		request++;
		return query;
	}
	//-----------------------------------------------------------------------------------
	public boolean doExistFile(int id){
		boolean flag = false;
		for(int i=0;i<fileList.size();i++){
			if(fileList.get(i).id == id)
				flag = true;
		}
		return flag;
	}
	//-----------------------------------------------------------------------------------
	public File getFileById(int id){
		File f = null;
		for(int i=0;i<fileList.size();i++){
			if(fileList.get(i).id == id)
				f = fileList.get(i);
		}
		return f;
	}
	//-----------------------------------------------------------------------------------
	public void searchForFile(int nbSearch){
		for(int j=0;j<nbSearch;j++){
			Query query = null;
			if(fileList.size()==EBotnet.initial_Files)
				continue;
			do{
				query = genQuery();
			}while(query == null);
			Flooding(query);
		}
	}
	//-------------------------------------------------------------------------------------
	public boolean Flooding(Query in){
		boolean fileFound = false;
		in.hopCount++;
		in.nodeIdsVisited.add(getId());
		File f = null;
		if(in.hopCount >= EBotnet.maxHop){
			fileFound = false;
		}else{
			if(this.fileList != null){
				if(doExistFile(in.file.id)){
					  fileFound=true;
					  f = this.getFileById(in.file.id);
				}
			}
			if(fileFound){
				listDown down = new listDown(this,f);
				in.from.addDown(down);
			}else{
				if(getNeighbours() != null){
					for(int i= 0; i<getNeighbours().size(); i++){
						if(getNeighbours().get(i).getId() != in.from.getId() && !in.nodeIdsVisited.contains(getNeighbours().get(i).getId())){
							 fileFound = getNeighbours().get(i).Flooding(in);
						}
					}
				}
			}
			
		}
		return fileFound;
	}
	//-----------------------------------------------------------------------------------
	//-----------------------------------------------------------------------------------
	public void chooseRandomFile(){
		if (downloads.size() != 0) {
			listDown down;
			Random rand = new Random();
			int id = rand.nextInt(downloads.size());
			down = downloads.get(id);
			Reply rep = new Reply(down.getServer(),down.searchedFile());
			addReplies(rep);
			removeAllDown();
			}
	}
	//-----------------------------------------------------------------------------------
	public void traitReply(){
		if (Replies.size() != 0) {
			Reply rep;
			for (int q=0; q<Replies.size(); q++){
				rep = Replies.get(q);
				double r = Math.random();
				if(EBotnet.landa_S>r){
					Transaction Tr;
					if(rep.file.getInfected() == 1){
						if(getEtat() == 0)
							setExp();
						Tr = new Transaction(this,rep.from,false, rep.file.id,-1);
						fileList.add(rep.file);
						addTrans(Tr);
						nombreOfDownInfected++;
						}
					if(rep.file.getInfected() == 0){
						if(this.getMal()==1) {
							File f = new File(rep.file.id);
							f.setInfected();
							fileList.add(f);
						}else {
							fileList.add(rep.file);
						}
						Tr = new Transaction(this,rep.from,true, rep.file.id,1);
						addTrans(Tr);
						nombreOfDownClean++;
						}
					reply++;
				}
				Replies.remove(rep);
			}
		}
	}
	//-----------------------------------------------------------------------------------
	//-----------------------------------------------------------------------------------
	public void clickFileInfection(double p){
		if (fileList.size() != 0) {
			File file;
			for (int q=0; q<fileList.size(); q++){
				file = fileList.get(q);
				if(file.getInfected() == 1){
					boolean inTrans = false;
					Transaction tr;
					for(int i=Trans.size()-1;i>=0;i--){
						tr = Trans.get(i);
						if(tr.getidFile() == file.id){
							inTrans = true;
							break;
						}
					}
					if(inTrans == false)
						continue;
					double r = Math.random();
					if(p>r){
						setInf();
					}
				}
			}
		}
	}
	//-----------------------------------------------------------------------------------
	public double fileTrust(File f){
		double trust = 0;
		int source = 0;
		double som = 0;
		Node myPeer;
		for(int i=0;i<neighbours.size();i++){
			myPeer = neighbours.get(i);
			if(!fileList.isEmpty()){
				int s = 0;
				int t = 0;
				boolean flag = false;
				for(int j=0;j<myPeer.getFileList().size();j++){
					if(myPeer.getFileList().get(j).id == f.id){
						flag = true;
						if(myPeer.getFileList().get(j).getInfected() == 1)
							s--;
						else
							s++;
						t++;
					}
				}
				if(flag){
					som = som + (double) s/t;
					source++;
				}
			}
		}
		if(source == 0 )
			trust = 0;
		else
			trust = (double) som/source;
		return trust;
	}
	//----------------------------------------------------------------------------------
	public double fileTrustv1(File f){
		double trust = 0;
		double som = 0;
		Node myPeer;
		int count = 0;
		for(int i=0;i<neighbours.size();i++){
			myPeer = neighbours.get(i);
			for(int j=0;j<myPeer.neighbours.size();j++){
				som = som + myPeer.Rhi(f);
				count++;
				}
			}
		trust = (double) som/count;
		return trust;
	}
	//--------------File-----------------
		public double Rhi(File file){
			double local = 0;
			if(!Trans.isEmpty()){
				int som = 0;
				int total = 0;
				boolean flag = true;
				for(int i=0;i<Trans.size();i++){
					if(Trans.get(i).getidFile() == file.id){
						flag = false;
						som = som + Trans.get(i).getrFile();
						total++;
					}
				}
				if(flag){
					local = 1; //1
				}else{
					local = (double) som/total;
				}
			}else{
				local = 1; //1
			}
			if(local<0)
				local=0;
			return local;
		}
	//------------------------------------------------------------------------------------
	@Override
	public String toString() {
		String desc = "Node : "+id+" Etat :"+etat+" Trust :"+getGlobalTrust();
		if (neighbours.size() != 0) {
		    desc += "\nVoisins : ";
		    for (int q=0; q<neighbours.size(); q++)
		    	desc += "\t"+neighbours.get(q).id;
		    desc += "\t";
		}
		desc += "\n";
		if (fileList.size() != 0) {
		    desc += "\nFiles : ";
		    for (int q=0; q<fileList.size(); q++)
		    	desc += "\t"+fileList.get(q).toString();
		    desc += "\t";
		}
		desc += "\n";
		if (Trans.size() != 0) {
		    desc += "\nTransactions : ";
		    for (int q=0; q<Trans.size(); q++)
		    	desc += "\t"+Trans.get(q).toString();
		    desc += "\t";
		}
		desc += "\n";
		return desc;
	}
	

}

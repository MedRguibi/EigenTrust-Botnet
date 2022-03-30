package SEIS.Trust;

public class File {
	
	int id;
	String FileName;
	double Reputation;
	private int infected;
	int pFiles = EBotnet.initial_Files;
	Zipf zipf = new Zipf(pFiles);
	private Node from;
	private boolean click;
	
	
	public File(){
		int r = zipf.nextZipf();
		id = 1 + r;
		FileName = "file" + (1 + r);
		infected = 0;
		Reputation = 1;
		from = null;
		click = false;
	}
	public File(int id){
		this.id = id;
		FileName = "file" + id;
		infected = 0;
		Reputation = 1;
		from = null;
		click = false;
	}
	public void setInfected(){
		infected = 1;
	}
	public void setSus(){
		infected = 0;
	}
	public int getInfected(){
		return infected;
	}
	public void trustFile(){
		this.Reputation = Reputation / 2;
	}
	public void setTrustFile(double t){
		Reputation = t;
	}
	public double getTrustFile(){
		return Reputation; 
	}
	public Node peerFrom() {
		return from;
	}
	public void setSource(Node n) {
		from = n;
	}
	public void clickOn(){
		click = true;
	}
	public boolean itClicked() {
		return click;
	}
	
	public String toString() {
		String description = FileName;
		description += " ";
		description += Reputation;
		description += " ";
		if(this.infected==1)
			description += "{Infected}";
		else
			description += "{Good}";
		return description;
	}
	
	
}
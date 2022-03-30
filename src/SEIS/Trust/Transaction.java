package SEIS.Trust;

public class Transaction {
	protected Integer id = 0;
	protected static int nextTransID = 0;
	private Node client; // Client 
	private Node server;  // Server
	private boolean isSatisfied;
	private int idFile;
	private int rFile;
	
	
	public Transaction (Node Client, Node Server, boolean isSatisfied,  int idFile, int rateFile){
		nextTransID++;
		id = nextTransID;
		client = Client;
		server = Server;
		this.isSatisfied = isSatisfied;
		this.idFile = idFile;
		rFile = rateFile;
	}
	
	// Getters and Setters
	public Node getClient() {
		return client;
	}

	public Node getServer() {
		return server;
	}

	public boolean isSatisfied() {
		return isSatisfied;
	}
	
	public int getidFile() {
		return idFile;
	}
	
	public int getrFile() {
		return rFile;
	}

	@Override
	public String toString() {
		return "Transaction [id=" + id + ", Sender=" + client.getId() + ", Target="
				+ server.getId() + ", idFile=" + idFile + ", Rate File=" + rFile + "]";
	}

}

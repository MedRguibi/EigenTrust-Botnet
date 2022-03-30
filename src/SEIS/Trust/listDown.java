package SEIS.Trust;

public class listDown {
	protected Integer id = 0;
	protected static int nextdownID = 0;
	private Node from;
	private File file;
	private boolean selected ;
	
	public listDown (Node p, File fileName){
		nextdownID++;
		id = nextdownID;
		from = p;
		file = fileName;
		selected = false;
		}
	public Node getServer(){
		return from;
	}
	public File searchedFile(){
		return file;
	}
	public void setSelected(){
		selected = true;
	}
	public boolean isSelected(){
		return selected; 
	}
	public double jRh(){
		double val = ((Node) from).Rhi(file);
		return val;
	}
}

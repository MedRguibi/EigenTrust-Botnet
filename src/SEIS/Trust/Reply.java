package SEIS.Trust;

public class Reply {
	protected Integer id = 0;
	protected static int nextReplyID = 0;
	Node from;
	File file;
	
	public Reply (Node p, File fileName){
		nextReplyID++;
		id = nextReplyID;
		from = p;
		file = fileName;
	}

}

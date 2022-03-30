package SEIS.Trust;


import java.util.*;

public class Query {
	protected Integer id = 0;
	protected static int nextQueryID = 0;
	Node from;
	File file;
	List <Integer> nodeIdsVisited = new ArrayList<Integer>();
	int hopCount;
	
	public Query(Node p, File fileName){
		nextQueryID++;
		id = nextQueryID;
		from = p;
		file = fileName;
		hopCount =0;
	}

	@Override
	public String toString() {
		String description = "Query : "+ id +" From : "+ from +" Search File : "+file.FileName+"\n\tNode visited : ";
		for(int i=0;i<nodeIdsVisited.size();i++)
			description += nodeIdsVisited.get(i).intValue() + " ";
		description +="\n\tHops : " + hopCount;
		description += "\n\t-------------------";
		return description;
	}
	
}

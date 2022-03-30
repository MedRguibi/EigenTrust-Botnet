package SEIS.Trust;

public class Link  {

        public String name; //Name of link
        private int tail; //From Node #
        private int head; //To Node #
        public double value; //Value of link
        public int count;
        //////////////////////////////////////////////
        public double weight_th;
        public double weight_ht;
        public double weight;
 

    public Link() {
     name = "";
     value = 0;
     weight_th = 0.0;
     weight_ht = 0.0;
     weight = 0.0;
    }
    public Link(int h,int t) {
    	this.head = h ; 
    	this.tail = t; 
       }
	public int getTail() {
		return tail;
	}
	public void setTail(int tail) {
		this.tail = tail;
	}
	public int getHead() {
		return head;
	}
	public void setHead(int head) {
		this.head = head;
	}
}//Link
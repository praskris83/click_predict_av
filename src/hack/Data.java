package hack;

public class Data {

	String id;
	String cuntry;
	String carrier;
	String traffic;
	String clickDate;
	String device;
	String browser;		
	String os;
	
	String reffer;
	String ip;
	String conversion;
	String cDate;
	
	String pay;
	String pub;
	String sub;
	
	String add;
	String frd;
	double clas;
	double cost;
	
	
	public double getCost() {
		return cost;
	}


	public void setCost(double cost) {
		this.cost = cost;
	}


	public double getClas() {
		return clas;
	}


	public void setClas(double classification) {
		this.clas = classification;
	}


	@Override
	public String toString() {
		return id + "\t" + clas + "\t" + cost + "\n";
	}
	
}

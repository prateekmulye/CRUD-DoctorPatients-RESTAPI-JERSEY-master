package drpatients;

import javax.xml.bind.annotation.XmlRootElement; 
import javax.xml.bind.annotation.XmlElement;

@XmlRootElement(name = "patient")
public class Patients implements Comparable<Patients> {
    private String name;   
    private String insurance;
    private int    id;

    public Patients() { }

    public Patients(String name, String insurance) {
		super();
		this.name = name;
		this.insurance = insurance;
	}

	@Override
    public String toString() {
	return "\t"+id+": "+name+" ----- "+insurance;
    }

    @XmlElement    
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement
	public String getInsurance() {
		return insurance;
	}

	public void setInsurance(String insurance) {
		this.insurance = insurance;
	}

	@XmlElement
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	// implementation of Comparable interface
    public int compareTo(Patients other) {
	return this.id - other.id;
    }	
}
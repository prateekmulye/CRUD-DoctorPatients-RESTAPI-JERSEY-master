package drpatients;

import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "doctor")
public class Doctors {
	private String name;
	private List<Patients> noOfPatients;
	private int id;
	
	public Doctors(){}

	public Doctors(String name, List<Patients> noOfPatients) {
		super();
		this.name = name;
		this.noOfPatients = noOfPatients;
	}

	@Override
	public String toString() {
		Iterator it = noOfPatients.iterator();
		StringBuilder output = new StringBuilder("\n"+id+") " + name + "---");
		while(it.hasNext())
		{
			Object patient = it.next();
			output.append(patient.toString()+"\n");
		}
		return output.toString();
	}

	@XmlElement
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement
	public List<Patients> getNoOfPatients() {
		return noOfPatients;
	}

	public void setNoOfPatients(List<Patients> noOfPatients) {
		this.noOfPatients = noOfPatients;
	}

	@XmlElement
	public int getId() {
		return id;
	}

	public void setId(int did) {
		this.id = did;
	}
	
	

}

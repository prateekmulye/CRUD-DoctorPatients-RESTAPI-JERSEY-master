package drpatients;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import javax.xml.bind.annotation.XmlElement; 
import javax.xml.bind.annotation.XmlElementWrapper; 
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "PatientsList")
public class PatientsList {
    private List<Patients> patients; 
    private AtomicInteger patientId;
    private static int lastID;
    
    public PatientsList() { 
    patients = new CopyOnWriteArrayList<Patients>(); 
    patientId = new AtomicInteger();
    }

    @XmlElement 
    @XmlElementWrapper(name = "Patients") 
    public List<Patients> getPatients() { 
	return this.patients;
    } 
    public void setPatients(List<Patients> patie) { 
	this.patients = patie;
    }

    @Override
    public String toString() {
	String s = "";
	for (Patients p : patients) s += p.toString();
	return s;
    }

    public Patients find(int id) {
	Patients patie = null;
	for (Patients p : patients) {
	    if (p.getId() == id) {
	    patie = p;
		break;
	    }
	}	
	return patie;
    }
    
	// Returns the increment ID
    public int getCurrentID()
    {	
    	return patientId.incrementAndGet();
    }
    
    public int add(String name, String insurance) {
	int id = patientId.incrementAndGet();
	Patients p = new Patients();
	p.setName(name);
	p.setInsurance(insurance);
	p.setId(id);
	patients.add(p);
	return id;
    }
}
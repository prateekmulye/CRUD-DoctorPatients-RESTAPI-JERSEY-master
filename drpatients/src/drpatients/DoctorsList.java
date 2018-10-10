package drpatients;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import javax.xml.bind.annotation.XmlElement; 
import javax.xml.bind.annotation.XmlElementWrapper; 
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DoctorsList")
public class DoctorsList {
    private List<Doctors> doctor; 
    private AtomicInteger doctorsId;

    public DoctorsList() { 
    doctor = new CopyOnWriteArrayList<Doctors>(); 
    doctorsId = new AtomicInteger();
    }

    @XmlElement 
    @XmlElementWrapper(name = "Doctors") 
    public List<Doctors> getDoctors() { 
	return this.doctor;
    } 
    public void setDoctors(List<Doctors> doct) { 
	this.doctor = doct;
    }

    @Override
    public String toString() {
	String s = "";
	for (Doctors d : doctor) s += d.toString();
	return s;
    }

    public Doctors find(int id) {
    Doctors doct = null;
	for (Doctors d : doctor) {
	    if (d.getId() == id) {
	    doct = d;
		break;
	    }
	}	
	return doct;
    }
    
    
    public int add(String name, List<Patients> noOfPatients) {
	int id = doctorsId.incrementAndGet();
	Doctors d = new Doctors();
	d.setName(name);
	d.setNoOfPatients(noOfPatients);

	d.setId(id);
	doctor.add(d);
	return id;
    }
}
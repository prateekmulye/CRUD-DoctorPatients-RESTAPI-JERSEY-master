package drpatients;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.servlet.ServletContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.core.ResourceContext;

@Path("/doctor")
public class DoctorRS {
	
	
    @Context 
    private ServletContext sctx;          // dependency injection
    private static PatientsList plist;
    private static DoctorsList dlist; 
	@Context
	private ResourceContext resourceContext;

    public DoctorRS() { }

    @GET
    @Path("/xml")
    @Produces({MediaType.APPLICATION_XML}) 
    public Response getXml() {
	checkContext();
	return Response.ok(dlist, "application/xml").build();
    }

    @GET
    @Path("/xml/{id: \\d+}")
    @Produces({MediaType.APPLICATION_XML}) // could use "application/xml" instead
    public Response getXml(@PathParam("id") int id) {
	checkContext();
	return toRequestedType(id, "application/xml");
    }
        
    @GET
    @Path("/plain")
    @Produces({MediaType.TEXT_PLAIN}) 
    public String getPlain() {
	checkContext(); 
	return dlist.toString();
    }
    
    @GET
    @Path("/plain/{id: \\d+}")
    @Produces({MediaType.TEXT_PLAIN}) // could use "application/xml" instead
    public Response getPlain(@PathParam("id") int id) {
	checkContext();
	return toRequestedType(id, "text/plain");
    }

    
    @POST
    @Produces({MediaType.TEXT_PLAIN})
    @Path("/create")
    public Response create(@FormParam("DoctorsName") String name, 
			   @FormParam("Patients") List<String> noOfPatients) {
	checkContext();
	String msg = null;
	// Require both properties to create.
	if (name == null || noOfPatients == null) {
	    msg = "Property 'NAME' or 'PATIENTS' is missing.\n";
	    return Response.status(Response.Status.BAD_REQUEST).
		                                   entity(msg).
		                                   type(MediaType.TEXT_PLAIN).
		                                   build();
	}	    
	// Otherwise, create the Patients and add it to the collection.
	List<Patients> patients = new ArrayList<Patients>();

	for(String newpatient : noOfPatients){
		String[] indipatients = newpatient.split("!");
		Patients p = new Patients();
		
		p.setId(plist.getCurrentID());//Gets the current ID to assign for the new patient
		p.setName(indipatients[0]);
		p.setInsurance(indipatients[1]);
		patients.add(p);
		
	}
	int id = addDoctor(name, patients);
	msg = "Doctor " + id + " created: (Name = " + name + " Number of Patients = " + noOfPatients +").\n";
	return Response.ok(msg, "text/plain").build();
    }

    
    @PUT
    @Produces({MediaType.TEXT_PLAIN})
    @Path("/update")
    public Response update(@FormParam("id") int id,
			   @FormParam("name") String name) {
	checkContext();

	// Check that sufficient data are present to do an edit.
	String msg = null;
	if (name == null) 
	    msg = "Doctors Name is not given: nothing to edit.\n";

	Doctors d = dlist.find(id);
	if (d == null)
	    msg = "There is no doctor with ID " + id + "\n";

	if (msg != null)
	    return Response.status(Response.Status.BAD_REQUEST).
		                                   entity(msg).
		                                   type(MediaType.TEXT_PLAIN).
		                                   build();
	// Update.
	if (name != null) d.setName(name);
	msg = "Doctor " + id + " has been updated.\n";
	return Response.ok(msg, "text/plain").build();
    }

    
    @DELETE
    @Produces({MediaType.TEXT_PLAIN})
    @Path("/delete/{id: \\d+}")
    public Response delete(@PathParam("id") int id) {
	checkContext();
	String msg = null;
	Doctors d = dlist.find(id);
	if (d == null) {
	    msg = "There is no Doctor with ID " + id + ". Cannot delete.\n";
	    return Response.status(Response.Status.BAD_REQUEST).
		                                   entity(msg).
		                                   type(MediaType.TEXT_PLAIN).
		                                   build();
	}
	dlist.getDoctors().remove(d);
	msg = "Doctor with " + id + " has been deleted.\n";

	return Response.ok(msg, "text/plain").build();
    }
 
    //** utilities
    private void checkContext() {
	if (dlist == null) populate();
    }

    private void populate() {
	dlist = new DoctorsList();
	plist = new PatientsList();
	

	InputStream dr = sctx.getResourceAsStream("/WEB-INF/data/doctors.db");
	

	//gets all the available list of doctors from the doctors data file
	if (dr != null) {
	    try {
		BufferedReader doctorReader = new BufferedReader(new InputStreamReader(dr));
		int i = 0;
		String record = null;
		List<Patients> allpatients = PatientsLists();
		while ((record = doctorReader.readLine()) != null) {
		    String[] parts = record.split("!");
		    List<Patients> docpatients = new ArrayList<Patients>();
		    for(int k=0;k<Integer.parseInt(parts[1]);k++)
		    {
		    	docpatients.add(allpatients.get(k));
		    }
		    allpatients = allpatients.subList(Integer.parseInt(parts[1]),allpatients.size());
		    addDoctor(parts[0], docpatients);
		}
	    }
	    catch (Exception e) { 
		throw new RuntimeException("I/O failed!"); 
	    }
	}
	
    }
	
	//gets all the available list of patients from the patients data file
    private List<Patients> PatientsLists()
    {
    	
    	InputStream pt = sctx.getResourceAsStream("/WEB-INF/data/patients.db");
    	if (pt != null) {
    	    try {
    		System.out.println("---==== In Patients Populated");
    		BufferedReader doctorReader = new BufferedReader(new InputStreamReader(pt));
    		
    		String record = null;
    		while ((record = doctorReader.readLine()) != null) {
    		    String[] parts = record.split("!");
    		    addPatients(parts[0], parts[1]);
    		}
    	    }
    	    catch (Exception e) { 
    		throw new RuntimeException("I/O failed!"); 
    	    }
    	}

    	return plist.getPatients();
    }	
    
    // Add a new prediction to the list.
    private int addDoctor(String name, List<Patients> noOfPatients) {
	int id = dlist.add(name, noOfPatients);
	return id;
    }
    
    private int addPatients(String name, String insurance) {
    	int id = plist.add(name, insurance);
    	return id;
    }
    
    // Patients --> JSON document
    private String toJson(Doctors drs) {
	String json = "If you see this, there's a problem.";
	try {
	    json = new ObjectMapper().writeValueAsString(drs);
	}
	catch(Exception e) { }
	return json;
    }

    // PatientsList --> JSON document
    private String toJson(DoctorsList dlist) {
	String json = "If you see this, there's a problem.";
	try {
	    json = new ObjectMapper().writeValueAsString(dlist);
	}
	catch(Exception e) { }
	return json;
    }

    // Generate an HTTP error response or typed OK response.
    private Response toRequestedType(int id, String type) {
	Doctors drs = dlist.find(id);
	if (drs == null) {
	    String msg = id + " is a bad ID.\n";
	    return Response.status(Response.Status.BAD_REQUEST).
		                                   entity(msg).
		                                   type(MediaType.TEXT_PLAIN).
		                                   build();
	}
	else if (type.contains("json"))
	    return Response.ok(toJson(drs), type).build();
	else if (type.contains("plain"))
	    return Response.ok(drs.toString(), type).build();
	else
	   return Response.ok(drs, type).build(); // toXml is automatic
    }
}




package dan.langford;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

// Controllers are entry points
@Controller
public class DataController {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	private DataService service;

	@Autowired
	public DataController(DataService service) {
		this.service = service;
	}

	public void putData(String body, String filename) {
		service.persistData(body, filename);
	}

	public String getData(String filename) {
		return service.retrieveData(filename);
	}


}

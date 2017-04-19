package dan.langford;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Services are for business logic
public class DataService {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	private DataRepository repo;

	public DataService(DataRepository repo) {
		this.repo = repo;
	}

	public void persistData(String data, String id) {
		repo.writeFile(data, id);
	}

	public String retrieveData(String id) {
		return repo.readFile(id);
	}
}

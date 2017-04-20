package dan.langford;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Services are for business logic
@Service
public class DataService {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	private DataRepository repo;

	@Autowired
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

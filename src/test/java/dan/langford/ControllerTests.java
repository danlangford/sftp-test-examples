package dan.langford;

import com.jcraft.jsch.JSch;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ControllerTests {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	private DataController controller;

	@Before
	public void setup() throws IOException {
		Properties props = new Properties();
		props.load(getClass().getResourceAsStream("/test.properties"));
		DataRepository repo = new DataRepository(props, new JSch());
		controller = new DataController(new DataService(repo));
	}

	// This isn't actually a "unit" test but i can use it to test things are working before i move on
	@Test
	public void testWriteRead() {
		String value = "Your awesome! #" + Math.random(), name = "fortune.txt";
		log.info("trying to write: {}", value);
		controller.putData(value, name);
		String actual = controller.getData(name);
		log.info("data read: {}", actual);
		assertThat(actual, is(value));
	}
}

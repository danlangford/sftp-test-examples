package dan.langford;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

/*
its important to me that these tests do not require an sftp server to be running

note: there is more we can do to make this file smaller but i wanted to show
how it works without any special RUNNER doing work behind the scenes

its important that you decide the BEHAVIOR that you want and only test to that BEHAVIOR
maybe the desired BEHAVIOR is that it tries X number of times,
maybe the desired BEHAVIOR is that it waits Y number of ms
 */

public class RepositoryTestsViaMock {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	private StopWatch stopWatch = new StopWatch();
	private Properties props;
	private JSch jSch;
	private Session session;
	private ChannelSftp channel;
	private DataRepository repo;

	@Before
	public void setup() throws Exception {
		props = new Properties();
		props.load(getClass().getResourceAsStream("/application-test.properties"));
		jSch = mock(JSch.class);
		session = mock(Session.class);
		channel = mock(ChannelSftp.class);
		repo = new DataRepository(props, jSch);
	}

	@Test
	public void testReconnectNotNeeded() throws Exception {

		// GIVEN a known value and filename
		String seedData = "We believe in you! #" + Math.random(), filename = "confidence.txt";

		// and GIVEN normal conditions
		when(jSch.getSession(anyString(),anyString(),anyInt()))
				.thenReturn(session);
		when(session.openChannel("sftp"))
				.thenReturn(channel);
		when(channel.get(filename))
				.thenReturn(IOUtils.toInputStream(seedData, "utf-8"));

		// WHEN we attempt to read a file
		log.info("hoping to read: {}", seedData);
		stopWatch.start();
		String readData = repo.readFile(filename);
		stopWatch.stop();
		log.info("read data: {}", readData);

		// THEN it should return the expected data
		assertThat(readData, is(seedData));

		// and succeed according to methods called
		verify(channel).get(filename);

		// and it should have taken little time cause NO reconnects
		Long time = stopWatch.getLastTaskTimeMillis();
		assertThat(time, lessThan(100L));

	}

	@Test
	public void testSessionReconnectLogic() throws Exception {

		// GIVEN a known value and filename
		String seedData = "We believe in you! #" + Math.random(), filename = "confidence.txt";

		// and GIVEN conditions that make creation of a session difficult yet still possible
		when(jSch.getSession(anyString(),anyString(),anyInt()))
				.thenThrow(new JSchException(),new JSchException(),new JSchException(),new JSchException())
				.thenReturn(session);
		when(session.openChannel("sftp"))
				.thenReturn(channel);
		when(channel.get(filename))
				.thenReturn(IOUtils.toInputStream(seedData,"utf-8"));

		// WHEN we attempt to read a file
		log.info("hoping to read: {}", seedData);
		stopWatch.start();
		String readData = repo.readFile(filename);
		stopWatch.stop();
		log.info("read data: {}", readData);

		// THEN it should return the expected data
		assertThat(readData, is(seedData));

		// and it should succeed according to methods called
		verify(channel).get(filename);

		// and i know it should have called get session 5 times
		verify(jSch,times(5)).getSession(anyString(),anyString(),anyInt());

		// and it should have taken a bit of time for the reconnects
		Long time = stopWatch.getLastTaskTimeMillis();
		assertThat(time, allOf(greaterThan(1100L), lessThan(1900L)));

	}
}

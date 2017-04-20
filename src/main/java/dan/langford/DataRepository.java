package dan.langford;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.util.Properties;

// Repositories are for persisting data or making outbound calls
@Repository
public class DataRepository {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	private Properties props;
	private JSch jSch;
	private RetryTemplate retry = new RetryTemplate();

	@Autowired
	public DataRepository(Properties props, JSch jSch) {
		this.props = props;
		this.jSch = jSch;
		retry.setRetryPolicy(new SimpleRetryPolicy(6));
		retry.setBackOffPolicy(new ExponentialBackOffPolicy());
	}

	private Session getSession() {
		try {
			return retry.execute(context -> {
				Session session = jSch.getSession(props.getProperty("sftp.user"), props.getProperty("sftp.host"), Integer.valueOf(props.getProperty("sftp.port")));
				session.setPassword(props.getProperty("sftp.pass"));
				session.setConfig("StrictHostKeyChecking", props.getProperty("sftp.StrictHostKeyChecking"));
				session.connect();
				return session;
			});
		} catch (JSchException jse) {
			throw new RuntimeException("problem building sftp session", jse);
		}
	}

	private ChannelSftp getChannel(Session session) {
		try {
			return retry.execute(
					context -> {
						ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
						channel.connect();
						return channel;
					}
			);
		} catch (JSchException jse) {
			throw new RuntimeException("problem building sftp channel", jse);
		}

	}

	public void writeFile(String contents, String filename) {
		Session session = getSession();
		ChannelSftp channel = getChannel(session);
		try {
			retry.execute(context -> {
				channel.put(IOUtils.toInputStream(contents, "utf-8"), filename);
				return null;
			});
		} catch (Exception e) {
			throw new RuntimeException("problem writing file", e);
		} finally {
			channel.disconnect();
			session.disconnect();
		}
	}

	public String readFile(String filename) {
		Session session = getSession();
		ChannelSftp channel = getChannel(session);
		try {
			return retry.execute(context -> IOUtils.toString(channel.get(filename), "utf-8"));
		} catch (Exception e) {
			throw new RuntimeException("problem reading file", e);
		} finally {
			channel.disconnect();
			session.disconnect();
		}
	}
}

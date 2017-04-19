package dan.langford;

import com.jcraft.jsch.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

// Repositories are for persisting data or making outbound calls
public class DataRepository {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	private Properties props;
	private JSch jSch;

	public DataRepository(Properties props, JSch jSch) {
		this.props = props;
		this.jSch = jSch;
	}

	private Session getSession() {
		try {
			Session session = jSch.getSession(props.getProperty("sftp.user"), props.getProperty("sftp.host"), Integer.valueOf(props.getProperty("sftp.port")));
			session.setPassword(props.getProperty("sftp.pass"));
			session.setConfig("StrictHostKeyChecking", props.getProperty("sftp.StrictHostKeyChecking"));
			session.connect();
			return session;
		} catch (JSchException jse) {
			throw new RuntimeException("problem building sftp session", jse);
		}
	}

	private ChannelSftp getChannel(Session session) {
		try {
			ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
			channel.connect();
			return channel;
		} catch (JSchException jse) {
			throw new RuntimeException("problem building sftp channel", jse);
		}

	}

	public void writeFile(String contents, String filename) {
		Session session = getSession();
		ChannelSftp channel = getChannel(getSession());
		try {
			channel.put(IOUtils.toInputStream(contents, "utf-8"), filename);
		} catch (SftpException | IOException e) {
			throw new RuntimeException("problem writing file", e);
		} finally {
			channel.disconnect();
			session.disconnect();
		}
	}

	public String readFile(String filename) {
		Session session = getSession();
		ChannelSftp channel = getChannel(getSession());
		InputStream is = null;
		try {
			is = channel.get(filename);
			return IOUtils.toString(is, "utf-8");
		} catch (SftpException | IOException e) {
			throw new RuntimeException("problem reading file", e);
		} finally {
			channel.disconnect();
			session.disconnect();
		}
	}
}

package dan.langford;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sftp")
public class SftpProps {
	private String host, user, pass, StrictHostKeyChecking;
	private Integer port;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getStrictHostKeyChecking() {
		return StrictHostKeyChecking;
	}

	public void setStrictHostKeyChecking(String strictHostKeyChecking) {
		StrictHostKeyChecking = strictHostKeyChecking;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}
}

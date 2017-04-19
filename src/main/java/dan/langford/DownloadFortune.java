package dan.langford;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloadFortune {

	private static Logger LOG = LoggerFactory.getLogger(DownloadFortune.class);

	public static void main(String... args) {
		LOG.info(new DownloadFortune().run());
	}

	public String run() {
		return "You are more valuable than you give yourself credit for.";
	}
}

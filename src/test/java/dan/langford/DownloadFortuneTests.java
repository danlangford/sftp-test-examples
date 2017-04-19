package dan.langford;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

public class DownloadFortuneTests {

	@Test
	public void test() {

		DownloadFortune df = new DownloadFortune();

		assertThat(df.run(), containsString("more valuable"));

	}
}

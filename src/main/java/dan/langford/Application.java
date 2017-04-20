package dan.langford;

import com.jcraft.jsch.JSch;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties
public class Application {

	@Bean
	public JSch getJSch() {
		return new JSch();
	}

}

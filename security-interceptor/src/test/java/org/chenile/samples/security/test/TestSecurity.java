package org.chenile.samples.security.test;

import org.chenile.core.context.HeaderUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringConfig.class)
@ActiveProfiles("unittest")
@AutoConfigureMockMvc
public class TestSecurity {
	@Autowired private MockMvc mvc;
	@Test public void testSecurity() throws Exception {
		String token = "";
		InputStream is = getClass().getClassLoader().getResourceAsStream("token.txt");
		if (is != null){
			token = new String(is.readAllBytes(), StandardCharsets.UTF_8);
		}
		// System.err.println("token is " + token);

		mvc.perform( MockMvcRequestBuilders
            .get("/test")
            .header("security-interceptor-preprocessheader","some_message")
        	.header("security-interceptor-postprocessheader","some_message")
			.header(HeaderUtils.TENANT_ID_KEY,"quickstart")
			// .header("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7l")
			.header("Authorization", "Bearer " + token.strip())

			//.header("User-Agent",
					//"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36")
			.accept(MediaType.APPLICATION_JSON))
        	.andDo(print())
        	.andExpect(status().isOk())
        	.andExpect(jsonPath("$.payload.test").value("test"));
	}
}

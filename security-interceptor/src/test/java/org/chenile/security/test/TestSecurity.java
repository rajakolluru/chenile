package org.chenile.security.test;

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

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringConfig.class)
@ActiveProfiles("unittest")
@AutoConfigureMockMvc
public class TestSecurity extends BaseSecurityTest {
    @Autowired
    private MockMvc mvc;

    private void doTestSuccess(String realm,String url, String user, String password, String result)
            throws Exception{
        mvc.perform(MockMvcRequestBuilders
            .get(url)
            .header(HeaderUtils.TENANT_ID_KEY, realm)
            .header("Authorization", "Bearer " + getToken(realm, user,password))
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.payload.test").value(result));
    }

    private void doTestFailure(String realm, String url, String user, String password) throws Exception{
        mvc.perform(MockMvcRequestBuilders
            .get(url)
            .header(HeaderUtils.TENANT_ID_KEY, realm)
            .header("Authorization", "Bearer " + getToken(realm, user,password))
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isForbidden());
    }

    @Test
    public void testUnauthorized() throws Exception {
        doTestFailure("quickstart","/test","alice","alice");
    }

    @Test
    public void testAuthorized() throws Exception {
        doTestSuccess("quickstart","/test","jdoe","jdoe","test");
    }

    @Test
    public void testAuthoritiesSupplierLambdaWithFoo() throws Exception {
        doTestSuccess("quickstart","/test1/foo","alice","alice","foo");
    }

    @Test
    public void testAuthoritiesSupplierLambdaWithOthers() throws Exception {
        doTestFailure("quickstart","/test1/bar","alice","alice");
    }
    @Test
    public void testAuthoritiesSupplierClassWithFoo() throws Exception {
        doTestSuccess("quickstart","/test2/foo","alice","alice","foo");
    }

    @Test
    public void testAuthoritiesSupplierClassWithOthers() throws Exception {
        doTestFailure("quickstart","/test2/bar","alice","alice");
    }

    @Test
    public void testAuthoritiesSupplierClassWithOthersForSuccess() throws Exception {
        doTestSuccess("quickstart","/test2/bar","jdoe","jdoe","bar");
    }

    @Test
    public void testPremiumUserTenant1Test() throws Exception {
        doTestSuccess("tenant1", "/test", "james", "james", "test");
    }

    @Test
    public void testPremiumUserTenant1Test1() throws Exception {
        doTestSuccess("tenant1", "/test1/bar", "james", "james", "bar");
    }

    @Test
    public void testPremiumUserTenant1Test2() throws Exception {
        doTestSuccess("tenant1", "/test2/bar", "james", "james", "bar");
    }

    @Test
    public void testNormalUserTenant1Test() throws Exception {
        doTestFailure("tenant1","/test","sam","sam");
    }
}

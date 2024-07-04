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

    private void doTestFailureWithIncorrectTenancy(String realm,String tenant,String url, String user, String password)
            throws Exception{
        mvc.perform(MockMvcRequestBuilders
            .get(url)
            .header(HeaderUtils.TENANT_ID_KEY, tenant)
            .header("Authorization", "Bearer " + getToken(realm, user,password))
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isUnauthorized());
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
    public void tenant0ANormalUserAccessesPremiumResource() throws Exception {
        doTestFailure("tenant0","/premium","t0-normal","t0-normal");
    }

    @Test
    public void tenant0APremiumUserAccessesPremiumResource() throws Exception {
        doTestSuccess("tenant0","/premium","t0-premium","t0-premium","test");
    }

    @Test
    public void tenant0ANormalUserAccessesSelectivelyPremiumResourceForNonPremium() throws Exception {
        doTestSuccess("tenant0","/selective-premium/foo","t0-normal","t0-normal","foo");
    }

    @Test
    public void tenant0ANormalUserAccessesSelectivelyPremiumResourceForPremium() throws Exception {
        doTestFailure("tenant0","/selective-premium/bar","t0-normal","t0-normal");
    }
    @Test
    public void tenant0ANormalUserAccessesSelectivelyPremiumResourceForNonPremium1() throws Exception {
        doTestSuccess("tenant0","/selective-premium1/foo","t0-normal","t0-normal","foo");
    }

    @Test
    public void tenant0ANormalUserAccessesSelectivelyPremiumResourceForPremium1() throws Exception {
        doTestFailure("tenant0","/selective-premium1/bar","t0-normal","t0-normal");
    }

    @Test
    public void tenant0APremiumUserAccessesSelectivelyPremiumResourceForPremium() throws Exception {
        doTestSuccess("tenant0","/selective-premium1/bar","t0-premium","t0-premium","bar");
    }

    @Test
    public void tenant1APremiumUserAccessesPremiumResource() throws Exception {
        doTestSuccess("tenant1", "/premium", "t1-premium", "t1-premium", "test");
    }

    @Test
    public void tenant1APremiumUserAccessesSelectivelyPremiumResourceForPremium() throws Exception {
        doTestSuccess("tenant1", "/selective-premium/bar", "t1-premium", "t1-premium", "bar");
    }

    @Test
    public void tenant1APremiumUserAccessesSelectivelyPremiumResourceForPremium1() throws Exception {
        doTestSuccess("tenant1", "/selective-premium1/bar", "t1-premium", "t1-premium", "bar");
    }

    @Test
    public void tenant1ANormalUserAccessesPremiumResource() throws Exception {
        doTestFailure("tenant1","/premium","t1-normal","t1-normal");
    }

    @Test
    public void tenant1ANormalUserAccessesNormalResource() throws Exception {
        doTestSuccess("tenant1","/normal","t1-normal","t1-normal","test");
    }

    @Test
    public void tenant0APremiumUserAccessesPremiumResourceWithWrongButValidTenancy() throws Exception{
        doTestFailureWithIncorrectTenancy("tenant0","tenant1","/normal","t0-premium","t0-premium");
    }

    @Test
    public void tenant0APremiumUserAccessesPremiumResourceWithIncorrectTenancy() throws Exception{
        doTestFailureWithIncorrectTenancy("tenant0","tenant8","/normal","t0-premium","t0-premium");
    }
}

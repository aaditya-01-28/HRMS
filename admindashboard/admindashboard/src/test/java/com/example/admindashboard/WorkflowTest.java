package com.example.admindashboard;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class WorkflowTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username="EMP401", roles={"MANAGER"})
    public void testManagerWorkflow() throws Exception {
        System.out.println("TESTING /manager/workflow AS EMP401...");
        mockMvc.perform(get("/manager/workflow"))
               .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
               .andExpect(status().isOk());
    }
}

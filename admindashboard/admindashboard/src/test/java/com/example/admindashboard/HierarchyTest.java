package com.example.admindashboard;

import com.example.admindashboard.controller.SeniorHrOrganizationController;
import com.example.admindashboard.dto.HierarchyNodeDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.List;

@SpringBootTest
public class HierarchyTest {

    @Autowired
    private SeniorHrOrganizationController controller;

    @Test
    public void printHierarchy() throws Exception {
        ResponseEntity<List<HierarchyNodeDTO>> response = controller.getOrganizationHierarchy();
        List<HierarchyNodeDTO> roots = response.getBody();
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("API_JSON_START");
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(roots));
        System.out.println("API_JSON_END");
    }
}

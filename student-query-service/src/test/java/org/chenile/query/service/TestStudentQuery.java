package org.chenile.query.service;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.chenile.TestApplication;
import org.chenile.query.model.ResponseRow;
import org.chenile.query.model.SearchRequest;
import org.chenile.query.model.SearchResponse;
import org.chenile.query.model.SortCriterion;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("unittest")
public class TestStudentQuery {

    @Autowired
    private SearchService<Map<String, Object>> searchService;
    @Autowired private SecuritySettings securitySettings;

    /**
     * @param searchService the searchService to set
     */
    public void setSearchService(SearchService<Map<String, Object>> searchService) {
        this.searchService = searchService;
    }

    @Before
    public void setUp() throws Exception{
        securitySettings.initializeMockRolesAcls();
    }
    
    @Test
    public void testById() {
        SearchRequest<Map<String, Object>> searchRequest = new SearchRequest<Map<String, Object>>();
        searchRequest.setQueryName("student");
        Map<String, Object> filters = new HashMap<String, Object>();
        filters.put("id", 1);
        searchRequest.setFilters(filters);

        SearchResponse searchResponse = searchService.search(searchRequest );

        assertNotNull(searchResponse.getList());
        assertEquals("There must be only one row returning" ,1, searchResponse.getList().size());
        for (ResponseRow rr : searchResponse.getList()) {
        	@SuppressWarnings("unchecked")
			Map<String,Object> map =  (Map<String,Object>)rr.getRow();
        	assertEquals("TestByID: ID must be 1",1,map.get("id"));
        }
    }

    @Test
    public void testOrderbyPagination() {
        SearchRequest<Map<String, Object>> searchRequest = new SearchRequest<Map<String, Object>>();
        searchRequest.setQueryName("students-all");
        SortCriterion sc = new SortCriterion();
        sc.setName("name");
        sc.setAscendingOrder(true);
        List<SortCriterion> criteria = new ArrayList<SortCriterion>();
        criteria.add(sc);
        searchRequest.setSortCriteria(criteria);
        searchRequest.setPageNum(2);
        searchRequest.setNumRowsInPage(15);

        SearchResponse searchResponse = searchService.search(searchRequest );

        assertNotNull(searchResponse.getList());
        assertEquals("Num rows obtained must be 15",15,searchResponse.getList().size());
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try {
        	String json = ow.writeValueAsString(searchResponse.getList());
        	System.out.println(json);
        }catch (Exception e) {
        	e.printStackTrace();
        }
       // System.out.println("search Response list is " + searchResponse.getList());
    }

    @Test
    public void testSpecific() {
        SearchRequest<Map<String, Object>> searchRequest = new SearchRequest<Map<String, Object>>();
        searchRequest.setQueryName("students");
        Map<String, Object> filters = new HashMap<String, Object>();
        filters.put("name","ja");
        searchRequest.setFilters(filters);

        SearchResponse searchResponse = searchService.search(searchRequest );

        assertNotNull(searchResponse.getList());
        assertEquals("Num rows obtained must be 1",1,searchResponse.getList().size());
        for (ResponseRow rr : searchResponse.getList()) {
        	@SuppressWarnings("unchecked")
			Map<String,Object> map =  (Map<String,Object>)rr.getRow();
        	assertEquals("Name must be Vijay","Vijay",map.get("name"));
        }
    }
    
    @Test
    public void testContainsWithArray() {
        SearchRequest<Map<String, Object>> searchRequest = new SearchRequest<Map<String, Object>>();
        searchRequest.setQueryName("students");
        Map<String, Object> filters = new HashMap<String, Object>();
        filters.put("branch",new String[] { "Gurgaon", "Jaipur", "Trivandrum"});
        searchRequest.setFilters(filters);

        SearchResponse searchResponse = searchService.search(searchRequest );

        assertNotNull(searchResponse.getList());
        assertEquals("Num rows obtained must be 3",3,searchResponse.getList().size());
    }
    
    @Test
    public void testTwoParams() {
        SearchRequest<Map<String, Object>> searchRequest = new SearchRequest<Map<String, Object>>();
        searchRequest.setQueryName("students");
        Map<String, Object> filters = new HashMap<String, Object>();
        filters.put("branch",new String[] { "Bangalore"});
        filters.put("name","ka");
        searchRequest.setFilters(filters);

        SearchResponse searchResponse = searchService.search(searchRequest );

        assertNotNull(searchResponse.getList());
        // only Akash and Vikas from Bangalore will match
        assertEquals("Num rows obtained must be 2",2,searchResponse.getList().size()); 
    }
    @Test
    public void testContainsWithList() {
        SearchRequest<Map<String, Object>> searchRequest = new SearchRequest<Map<String, Object>>();
        searchRequest.setQueryName("students");
        Map<String, Object> filters = new HashMap<String, Object>();
        List<String> list = new ArrayList<String>();
        list.add("Jaipur"); list.add("Gurgaon"); list.add("Trivandrum");
        filters.put("branch",list);
        searchRequest.setFilters(filters);

        SearchResponse searchResponse = searchService.search(searchRequest );

        assertNotNull(searchResponse.getList());
        assertEquals("Num rows obtained must be 3",3,searchResponse.getList().size());
    }
    
    @Test
    public void testContainsWithString() {
        SearchRequest<Map<String, Object>> searchRequest = new SearchRequest<Map<String, Object>>();
        searchRequest.setQueryName("students");
        Map<String, Object> filters = new HashMap<String, Object>();
  
        filters.put("branch","Trivandrum");
        searchRequest.setFilters(filters);

        SearchResponse searchResponse = searchService.search(searchRequest );

        assertNotNull(searchResponse.getList());
        assertEquals("Num rows obtained must be 1",1,searchResponse.getList().size());
    }    

}

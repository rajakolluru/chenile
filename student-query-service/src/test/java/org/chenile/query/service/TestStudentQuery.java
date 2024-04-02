package org.chenile.query.service;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.chenile.TestApplication;
import org.chenile.query.model.ColumnMetadata;
import org.chenile.query.model.QueryMetadata;
import org.chenile.query.model.ResponseRow;
import org.chenile.query.model.SearchRequest;
import org.chenile.query.model.SearchResponse;
import org.chenile.query.model.SortCriterion;
import org.chenile.test.query.store.QueryStoreSettings;
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
    @Autowired private QueryStoreSettings queryStoreSettings;

    /**
     * @param searchService the searchService to set
     */
    public void setSearchService(SearchService<Map<String, Object>> searchService) {
        this.searchService = searchService;
    }

    @Before
    public void setUp() throws Exception{
        securitySettings.initializeMockRolesAcls();
        Map<String, QueryMetadata> store = new HashMap<String, QueryMetadata>();
        populateQueryStore(store);
        queryStoreSettings.init(store);
    }

    private void populateQueryStore(Map<String, QueryMetadata> store) {
        Map<String, ColumnMetadata> cmdmap = getColumnMetadataMap();
        QueryMetadata qm = new QueryMetadata();
        qm.setId("Student.getById");
        qm.setColumnMetadata(cmdmap);
        qm.setAcls(null);
        qm.setFlexiblePropnames(false);
        qm.setPaginated(false);
        qm.setWorkflowName(null);
        qm.setSortable(false);
        store.put(qm.getId(),qm);

        qm = new QueryMetadata();
        qm.setId("Student.getAll");
        qm.setColumnMetadata(cmdmap);
        qm.setAcls(null);
        qm.setFlexiblePropnames(true);
        qm.setPaginated(true);
        qm.setWorkflowName(null);
        qm.setSortable(true);
        store.put(qm.getId(),qm);

        qm = new QueryMetadata();
        qm.setId("Student.getSpecific");
        qm.setColumnMetadata(cmdmap);
        qm.setAcls(null);
        qm.setFlexiblePropnames(false);
        qm.setPaginated(false);
        qm.setWorkflowName(null);
        qm.setSortable(false);
        store.put(qm.getId(),qm);		
    }


    private Map<String, ColumnMetadata> getColumnMetadataMap() {
        Map<String,ColumnMetadata> cmdmap = new HashMap<String, ColumnMetadata>();
        ColumnMetadata cmd = new ColumnMetadata();
        cmd.setName("id");
        cmd.setFilterable(true);
        cmd.setColumnType(ColumnMetadata.ColumnType.Text);
        cmd.setDropDownValues(null);
        cmd.setLikeQuery(false);
        cmdmap.put(cmd.getName(), cmd);

        cmd = new ColumnMetadata();
        cmd.setName("name");
        cmd.setFilterable(true);
        cmd.setColumnType(ColumnMetadata.ColumnType.Text);
        cmd.setDropDownValues(null);
        cmd.setLikeQuery(true);
        cmdmap.put(cmd.getName(), cmd);

        cmd = new ColumnMetadata();
        cmd.setName("branch");
        cmd.setFilterable(true);
        cmd.setColumnType(ColumnMetadata.ColumnType.Text);
        cmd.setDropDownValues(null);
        cmd.setLikeQuery(false);
        cmd.setContainsQuery(true);
        cmdmap.put(cmd.getName(), cmd);

        cmd = new ColumnMetadata();
        cmd.setName("phone");
        cmd.setFilterable(true);
        cmd.setColumnType(ColumnMetadata.ColumnType.Text);
        cmd.setDropDownValues(null);
        cmd.setLikeQuery(false);
        cmdmap.put(cmd.getName(), cmd);

        cmd = new ColumnMetadata();
        cmd.setName("percentage");
        cmd.setFilterable(true);
        cmd.setColumnType(ColumnMetadata.ColumnType.Text);
        cmd.setDropDownValues(null);
        cmd.setLikeQuery(false);
        cmdmap.put(cmd.getName(), cmd);

        cmd = new ColumnMetadata();
        cmd.setName("email");
        cmd.setFilterable(true);
        cmd.setColumnType(ColumnMetadata.ColumnType.Text);
        cmd.setDropDownValues(null);
        cmd.setLikeQuery(true);
        cmdmap.put(cmd.getName(), cmd);
        return cmdmap;
    }


    @Test
    public void testById() {
        SearchRequest<Map<String, Object>> searchRequest = new SearchRequest<Map<String, Object>>();
        searchRequest.setQueryName("Student.getById");
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
        searchRequest.setQueryName("Student.getAll");
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
        searchRequest.setQueryName("Student.getSpecific");
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
        searchRequest.setQueryName("Student.getSpecific");
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
        searchRequest.setQueryName("Student.getSpecific");
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
        searchRequest.setQueryName("Student.getSpecific");
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
        searchRequest.setQueryName("Student.getSpecific");
        Map<String, Object> filters = new HashMap<String, Object>();
  
        filters.put("branch","Trivandrum");
        searchRequest.setFilters(filters);

        SearchResponse searchResponse = searchService.search(searchRequest );

        assertNotNull(searchResponse.getList());
        assertEquals("Num rows obtained must be 1",1,searchResponse.getList().size());
    }    

}

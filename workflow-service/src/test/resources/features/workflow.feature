Feature: Tests the Chenile Workflow Service using a REST client. An issue service exists and is under test.
It helps to create an issue and manages the state of the issue as follows:
OPENED -(assign) -> ASSIGNED -(resolve) -> RESOLVED -(close) -> CLOSED
 
  Scenario: Test create issue
    When I POST a REST request to URL "/issue" with payload
    """
    {
	    "openedBy": "USER1",
	    "description": "Unable to login to my mail account"
		}
		"""
	  Then the REST response contains key "mutatedEntity"
	  And the REST response key "mutatedEntity.id" is "1"
	  And the REST response key "mutatedEntity.currentState.stateId" is "OPENED"
	  And the REST response key "mutatedEntity.openedBy" is "USER1"
	  
	Scenario: Retrieve the issue that just got created
		When I GET a REST request to URL "/issue/1" 
		Then the REST response contains key "mutatedEntity"
	  And the REST response key "mutatedEntity.id" is "1"
	  And the REST response key "mutatedEntity.currentState.stateId" is "OPENED"
	  And the REST response key "mutatedEntity.openedBy" is "USER1"
	  
	Scenario: Assign the issue to an assignee with comments
		When I PUT a REST request to URL "/issue/1/assign" with payload
		"""
		{
			"assignee": "MY-ASSIGNEE",
			"comment": "MY-ASSIGNEE-CAN-FIX-THIS"
		}
		"""
		Then the REST response contains key "mutatedEntity"
	  And the REST response key "mutatedEntity.id" is "1"
	  And the REST response key "mutatedEntity.currentState.stateId" is "ASSIGNED"
	  And the REST response key "mutatedEntity.assignee" is "MY-ASSIGNEE"
	  And the REST response key "mutatedEntity.assignComment" is "MY-ASSIGNEE-CAN-FIX-THIS"
	  
	 Scenario: Resolve the issue with comments
		When I PUT a REST request to URL "/issue/1/resolve" with payload
		"""
		{
			"comment": "CANNOT-DUPLICATE"
		}
		"""
		Then the REST response contains key "mutatedEntity"
	  And the REST response key "mutatedEntity.id" is "1"
	  And the REST response key "mutatedEntity.currentState.stateId" is "RESOLVED"
	  And the REST response key "mutatedEntity.resolveComment" is "CANNOT-DUPLICATE"
	  
	 Scenario: Close the issue with comments
		When I PUT a REST request to URL "/issue/1/close" with payload
		"""
		{
			"comment": "OK"
		}
		"""
		Then the REST response contains key "mutatedEntity"
	  And the REST response key "mutatedEntity.id" is "1"
	  And the REST response key "mutatedEntity.currentState.stateId" is "CLOSED"
	  And the REST response key "mutatedEntity.closeComment" is "OK" 
	  
	 Scenario: Assign a closed issue to someone. This will err out.
		When I PUT a REST request to URL "/issue/1/assign" with payload
		"""
		{
			"assignee": "MY-ASSIGNEE",
			"comment": "MY-ASSIGNEE-CAN-FIX-THIS"
		}
		"""
		Then the REST response does not contain key "mutatedEntity"
		And the http status code is 422

	  
Feature: Tests the Aurora Query Service. A location service exists and is under test for serving info about different locations.
 
  Scenario: Test full request. If the request contains all projections the service must return the entire response
    When I POST a request to service "queryService" and operation "query" with payload
    """
    {
	    "asia":{
	    	"india":{
	    		"karnataka":{
	    			"bangalore":{}
	    		},
	    		"tamilNadu":{
	    			"chennai":{}
	    		}
	    	},
	    	"china":{}
	    },
	    "northAmerica":{
	    	"us":{
	    		"california":{},
	    		"newYork":{
	    			"nyc":{},
	    			"syracuse":{}
	    		}
	    	},
	    	"canada":{}
	    }
		}
		"""
	  Then the response contains key "asia"
	  And the response contains key "northAmerica"
	  And the response contains key "northAmerica.canada"
	  And the response contains key "asia.china"
	  And the response key "asia.india.karnataka.bangalore.data.row" is "bangalore"
	  And the response key "northAmerica.us.newYork.nyc.data.row" is "nyc"
	  And the response key "northAmerica.us.newYork.syracuse.data.row" is "syracuse"
    
	Scenario: Test partial requests. If syracuse does not have a request, then syracuse must not have a response
    When I POST a request to service "queryService" and operation "query" with payload
    """
    {
	    "asia":{
	    	"india":{
	    		"karnataka":{
	    			"bangalore":{}
	    		},
	    		"tamilNadu":{
	    			"chennai":{}
	    		}
	    	},
	    	"china":{}
	    },
	    "northAmerica":{
	    	"us":{
	    		"california":{},
	    		"newYork":{
	    			"nyc":{}
	    		}
	    	},
	    	"canada":{}
	    }
		}
		"""
	  Then the response contains key "asia"
	  And the response contains key "northAmerica"
	  And the response contains key "northAmerica.canada"
	  And the response contains key "asia.china"
	  And the response key "asia.india.karnataka.bangalore.data.row" is "bangalore"
	  And the response does not contain key "northAmerica.us.newYork.syracuse"
    
    Scenario: If syracuse requests for an exception to be thrown, then syracuse must not have a response. 
    Instead a warning must be returned stating the exception
    When I POST a request to service "queryService" and operation "query" with payload
    """
    {
	    "asia":{
	    	"india":{
	    		"karnataka":{
	    			"bangalore":{}
	    		},
	    		"tamilNadu":{
	    			"chennai":{}
	    		}
	    	},
	    	"china":{}
	    },
	    "northAmerica":{
	    	"us":{
	    		"california":{},
	    		"newYork":{
	    			"nyc":{},
	    			"syracuse":{"filters": {"exceptionMessage": "SyracuseException"}}
	    		}
	    	},
	    	"canada":{}
	    }
		}
		"""
	  Then the response contains key "asia"
	  And the response contains key "northAmerica"
	  And the response contains key "northAmerica.canada"
	  And the response contains key "asia.china"
	  And the response key "asia.india.karnataka.bangalore.data.row" is "bangalore"
	  And the response does not contain key "northAmerica.us.newYork.syracuse"
	  And a warning must be thrown that says "SyracuseException"
	  
	  
	  Scenario: If an invalid key is there at the root level the response will be an error
    When I POST a request to service "queryService" and operation "query" with payload
    """
    {
    	"invalidKey":{},
	    "asia":{
	    	"india":{
	    		"karnataka":{
	    			"bangalore":{}
	    		},
	    		"tamilNadu":{
	    			"chennai":{}
	    		}
	    	},
	    	"china":{}
	    },
	    "northAmerica":{
	    	"us":{
	    		"california":{},
	    		"newYork":{
	    			"nyc":{},
	    			"syracuse":{"filters": {"exceptionMessage": "SyracuseException"}}
	    		}
	    	},
	    	"canada":{}
	    }
		}
		"""
	  Then the response is null
	  And an exception is thrown with message code 101
	  And an exception is thrown with param number 1 value "invalidKey"
	  And an exception is thrown with param number 2 value "root"
	  
	  Scenario: If an invalid key is there at the asia level the response will only contain the northAmerica part
	  The asia part will return null. There will also be a warning with code 101.Also, the 
	  warning contains a param number 1 with value "asia" 
    When I POST a request to service "queryService" and operation "query" with payload
    """
    {
	    "asia":{
	    	"invalidKey":{},
	    	"india":{
	    		"karnataka":{
	    			"bangalore":{}
	    		},
	    		"tamilNadu":{
	    			"chennai":{}
	    		}
	    	},
	    	"china":{}
	    },
	    "northAmerica":{
	    	"us":{
	    		"california":{},
	    		"newYork":{
	    			"nyc":{},
	    			"syracuse":{}
	    		}
	    	},
	    	"canada":{}
	    }
		}
		"""
	  Then the response does not contain key "asia"
	  And the response contains key "northAmerica"
	  And the response key "northAmerica.us.newYork.syracuse.data.row" is "syracuse"
	  And a warning must be thrown with code 101
	  And a warning must be thrown with param number 1 value "invalidKey"
	  And a warning must be thrown with param number 2 value "root.asia"
    
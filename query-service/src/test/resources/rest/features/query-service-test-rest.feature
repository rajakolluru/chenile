Feature: Tests the Aurora Query Service using a REST client. A location service exists and is under test for serving info about different locations.
 
  Scenario: Test full request. If the request contains all projections the service must return the entire response
    When I POST a REST request to URL "/query5" with payload
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
	    	"mexico": {},
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
	  Then the http status code is 200
	  And the top level code is 200
	  And success is true
	  And the REST response contains key "asia"
	  And the REST response contains key "northAmerica"
	  And the REST response contains key "northAmerica.canada"
	  And the REST response contains key "asia.china"
	  And the REST response key "asia.india.karnataka.bangalore.data.row" is "bangalore"
	  And the REST response key "northAmerica.us.newYork.nyc.data.row" is "nyc"
	  And the REST response key "northAmerica.us.newYork.syracuse.data.row" is "syracuse"
	  And the REST response key "northAmerica.mexico" is "true"
    
	Scenario: Test partial requests. If syracuse & Mexico dont have requests, then syracuse must not have a response
    When I POST a REST request to URL "/query" with payload
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
	  Then the http status code is 200
	  And the top level code is 200
	  And success is true
	  And the REST response contains key "asia"
	  And the REST response contains key "northAmerica"
	  And the REST response contains key "northAmerica.canada"
	  And the REST response contains key "asia.china"
	  And the REST response key "asia.india.karnataka.bangalore.data.row" is "bangalore"
	  And the REST response does not contain key "northAmerica.us.newYork.syracuse"
	  And the REST response does not contain key "northAmerica.mexico"
	  
	  
	  Scenario: If syracuse requests to an exception to be thrown, then syracuse must not have a response. 
    Instead a warning must be returned stating the exception
    When I POST a REST request to URL "/query" with payload
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
	    			"syracuse":{"filters": {"errorCode":2000,"exceptionMessage": "SyracuseException"}}
	    		}
	    	},
	    	"canada":{}
	    }
		}
		"""
	  Then the http status code is 206
	  And the top level code is 206
	  And the top level subErrorCode is 2000
	  And the top level description is "SyracuseException"
	  And success is true
	  And the error array size is 1
	  And the REST response contains key "asia"
	  And the REST response contains key "northAmerica"
	  And the REST response contains key "northAmerica.canada"
	  And the REST response contains key "asia.china"
	  And the REST response key "asia.india.karnataka.bangalore.data.row" is "bangalore"
	  And the REST response does not contain key "northAmerica.us.newYork.syracuse"
	  And a REST warning must be thrown that says "SyracuseException" with code 2000
	  
	  Scenario: If syracuse and nyc requests for an exception to be thrown, then syracuse must not have a response. 
	  NYC will not have a response.
	  newYork will not have a response
    Instead a warning must be returned stating the exception
    When I POST a REST request to URL "/query" with payload
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
	    			"nyc":{"filters": {"errorCode": 2000,"exceptionMessage": "NYCException"}},
	    			"syracuse":{"filters": {"errorCode": 2000,"exceptionMessage": "SyracuseException"}}
	    		}
	    	},
	    	"canada":{}
	    }
		}
		"""
	  Then the http status code is 206	  
	  And success is true
	  And the error array size is 2
	  And the top level code is 206
	  And the top level subErrorCode is 2000
	  And the top level description is "NYCException"
	  And the REST response contains key "asia"
	  And the REST response contains key "northAmerica"
	  And the REST response contains key "northAmerica.canada"
	  And the REST response contains key "asia.china"
	  And the REST response key "asia.india.karnataka.bangalore.data.row" is "bangalore"
	  And the REST response contains key "northAmerica.us"
	  And the REST response does not contain key "northAmerica.us.newYork.nyc"
	  And the REST response does not contain key "northAmerica.us.newYork.syracuse"
	  And a REST warning must be thrown that says "SyracuseException" with code 2000
	  And a REST warning must be thrown that says "NYCException" with code 2000
	  And a REST warning must be thrown that has field "root.northAmerica.us.newYork.syracuse"
	  
	  Scenario: If all of the requests fail, there must be an exception
    When I POST a REST request to URL "/query" with payload
    """
    {
	    "asia":{
	    	"india":{
	    		"karnataka":{
	    			"bangalore":{"filters": {"errorCode":2000,"exceptionMessage": "bangaloreException"}}
	    		},
	    		"tamilNadu":{
	    			"chennai":{"filters": {"errorCode":2001,"exceptionMessage": "chennaiException"}}
	    		}
	    	},
	    	"china":{"filters": {"errorCode":2002,"exceptionMessage": "chinaException"}}
	    },
	    "northAmerica":{
	    	"mexico": {"errorCode":2003,"exceptionMessage": "mexicoException"},
	    	"us":{
	    		"california":{"filters": {"errorCode":2004,"exceptionMessage": "californiaException"}},
	    		"newYork":{
	    			"nyc":{"filters": {"errorCode":2005,"exceptionMessage": "NYCException"}},
	    			"syracuse":{"filters": {"errorCode":2006,"exceptionMessage": "SyracuseException"}}
	    		}
	    	},
	    	"canada":{"filters": {"errorCode":2007,"exceptionMessage": "canadaException"}}
	    }
		}
		"""
	  Then the http status code is 500
	  And the top level code is 500
	  And success is false
	  And the error array size is 8
	  And the REST response does not contain key "asia"
	  And a REST exception is thrown with status 500 and message code 0
	  And a REST warning must be thrown that says "bangaloreException" with code 2000
	  And a REST warning must be thrown that says "chennaiException" with code 2001
	  And a REST warning must be thrown that says "chinaException" with code 2002
	  And a REST warning must be thrown that says "mexicoException" with code 2003
	  And a REST warning must be thrown that says "californiaException" with code 2004
	  And a REST warning must be thrown that says "NYCException" with code 2005
	  And a REST warning must be thrown that says "SyracuseException" with code 2006
	  And a REST warning must be thrown that says "canadaException" with code 2007
	  
	 
	 
	  Scenario: If an invalid key is there at the root level the response will be an error
    When I POST a REST request to URL "/query" with payload
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
	  Then a REST exception is thrown with status 400 and message code 101
	  And the top level code is 400
	  And the top level subErrorCode is 101
	  And the error array size is 1
	  And success is false
	  And a REST exception is thrown with param number 1 value "invalidKey"
	  And a REST exception is thrown with param number 2 value "root"
	  
 Scenario: If an invalid key is there at the asia level the response will only contain the northAmerica part
	  The asia part will return null. There will also be a warning with code 101.Also, the 
	  warning contains a param number 1 with value "asia" 
    When I POST a REST request to URL "/query" with payload
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
	  Then the http status code is 206
	  And the top level code is 206
	  And the top level subErrorCode is 101
	  And success is true
		And the error array size is 1
	  And the REST response does not contain key "asia"
	  And the REST response contains key "northAmerica"
	  And the REST response key "northAmerica.us.newYork.syracuse.data.row" is "syracuse"
	  And a REST warning must be thrown with code 101
	  And a REST warning must be thrown with param number 1 value "invalidKey"
	  And a REST warning must be thrown with param number 2 value "root.asia"
	  
	  Scenario: Make Bangalore throw an exception. 
	  Make no more requests. Since there is only one request that failed,
	  expect an error. Top level Error details will be the same as the Bangalore error details 
    When I POST a REST request to URL "/query" with payload
    """
    {
	    "asia":{
	    	"india":{
	    		"karnataka":{
	    			"bangalore":{"filters": {"errorCode":2000,"exceptionMessage": "BangaloreException"}}
	    		}
	    	}
	    }
		}
		"""
	  Then the http status code is 500
	  And success is false
	  And a REST exception is thrown with message "BangaloreException"
	  And the error array size is 1
	  And a REST warning must be thrown that says "BangaloreException" with code 2000
	  And the top level code is 500
	  And the top level subErrorCode is 2000
	  And the top level description is "BangaloreException"
	  
	 Scenario: INFO test
	 When I GET a REST request to URL "/info" 
	
	  
	  
       
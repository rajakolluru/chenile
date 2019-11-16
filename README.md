# chenile
An Open source framework for creating services (with spring boot) , kafka event processors, schedulers (with quartz), a file watcher etc. by writing simple POJOs and using a simple configuration JSON to hook it up. 

Chenile has a state machine and an orchestration engine.  

The orchestration engine is internally used by Chenile to provide an interception framework that helps in disintermediating traffic irrespective of the incoming protocol (HTTP, message etc.)

# chenile

Chenile is an open source framework for creating Micro services using Java and Spring Boot. 

It provides an interception framework to decouple functional and non functional requirements. Chenile avoids the need to write repetitive code. It encourages modular coding best practices. 

In addition to creating REST services, Chenile services can also be used to create kafka event processors, schedulers (with quartz), a file watcher etc. without the need for rewriting the code. 

Chenile has a state machine and an orchestration engine.  

The orchestration engine is internally used by Chenile to provide an interception framework that helps in disintermediating traffic irrespective of the incoming protocol (HTTP, message etc.)

Hence Chenile also serves like an IN-VM message bus. Chenile also facilitates easy swagger documentation (using Spring doc). 
Chenile allows the development of Cucumber based BDD tests with most of the plumbing already in place

Finally, Chenile ships with its own code generators to ease the development of micro services. 



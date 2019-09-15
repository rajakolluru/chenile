#Keywords Summary : Chenile FileWatch 

@unittest
Feature: Chenile File Watch
  Tests the features of chenile-filewatch

  @unittest
  Scenario: It must be possible to watch a file
    Given that a file watch definition with ID "watch1" was configured with source folder "foo"
    And a service "fooService" with operation "saveFoo" is configured
    And service "fooService" is listening to "watch1"
    And the main test is blocking 
    When a "CSV" file is uploaded with contents:
    | bar | baz |
    | 123 | 456 |
    | abc | def | 
    Then the fooService receives a request 
    And updates a count down latch 2 times
   
    

  
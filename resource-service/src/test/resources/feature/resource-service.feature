Feature: resource-service
  Scenario: client makes call to POST /resources
    When the client calls '/resources'
    Then the client receives status code of 200
    Then clean resources
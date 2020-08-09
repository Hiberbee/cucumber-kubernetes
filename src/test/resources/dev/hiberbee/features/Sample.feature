Feature: Cucumber Java project template

  Scenario: Untagged step examples
    Given simple step
    And step with parameter 'string type'
    And step with autowired bean dependency

  @Tag
  Scenario: Tagged step examples
    Given simple step
    When step returns 'value' example

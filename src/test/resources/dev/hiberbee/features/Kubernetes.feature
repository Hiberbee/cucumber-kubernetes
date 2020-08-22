Feature: Automate Kubernets cluster testing with Cucumber

  Background: Dependencies are installed
    Given 'minikube' command is executable
    And 'kubectl' command is executable

  Scenario: Kubernetes connection
    Given 'kubectl' command is executable
    When context is 'minikube'
    And namespace is 'kube-system'
    When I get secrets
    Then list size is greater then 15
    And contains resource with 'metadata.name' is equal to 'default-token'
    When I get pods
    Then list size is greater then 7
    And contains resource with 'metadata.name' is equal to 'kube-apiserver'
    When I get services
    Then list size is greater then 1
    And contains resource with 'metadata.name' is equal to 'kubernetes'
    When I get ingresses
    Then list size is greater then 0
    When I get deployments
    Then list size is greater then 0
    When I get replica sets
    Then list size is greater then 1
    When I get daemon sets
    Then list size is greater then 0
    When I get stateful sets
    Then list size is greater then 0

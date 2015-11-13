# vaadin-activiti
Integration sample between Vaadin 7.5 and Activiti 5 using Spring Boot.

This project uses Maven and produce a war file deployable on a container (such as Apache Tomcat).
This demo aims to embed the Activiti Workflow engine in a 'classic' Vaadin based application.
The idea is to show that is it possible to delegate form based task to Vaadin.

Look at ui/WelcomePanel.java which is the dispatcher. It routes Activiti tasks to its Vaadin user interface.  

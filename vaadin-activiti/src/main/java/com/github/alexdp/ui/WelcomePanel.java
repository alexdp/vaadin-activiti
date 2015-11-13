package com.github.alexdp.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.alexdp.dao.ApplicantRepository;
import com.github.alexdp.model.Applicant;
import com.github.alexdp.service.DispatcherService;
import com.github.alexdp.util.spring.SpringDependencyInjector;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class WelcomePanel extends Panel {
	
	@Autowired
	private DispatcherService dispatcherService;

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private TaskService taskService;
	
	@Autowired
	private ApplicantRepository applicantRepository;
	
	public WelcomePanel() {
		super("Welcome");
		SpringDependencyInjector.getInjector().inject(this);
		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		String userId = VaadinSession.getCurrent().getAttribute("userId") + "";
		// Startable processes
		for (ProcessDefinition aDef : dispatcherService.retreiveProcess(userId)) {
			String name = aDef.getName();
			String key = aDef.getKey();
			Button startProcessButton = new Button(name);
			startProcessButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					Applicant applicant = new Applicant();
					applicantRepository.save(applicant);
					Map<String, Object> variables = new HashMap<String, Object>();
					variables.put("userId", userId);
					variables.put("applicant", applicant);
					ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key, variables);
					List<Task> todoList = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
					for (Task aTask : todoList) {
						getUI().setContent(new RegistrationFormPanel(applicant, aTask.getId()));
					}
				}
			});
			layout.addComponent(startProcessButton);
		}
		// Tasks to do;
		for (Task aTask : dispatcherService.retreiveTaskAssignee(userId)) {
			String name = aTask.getName();
			Button taskButton = new Button(name);
			taskButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					Applicant applicant = (Applicant) runtimeService.getVariable(aTask.getProcessInstanceId(), "applicant");
					String formKey = aTask.getFormKey();
					if ("registrationForm".equals(formKey)) {
						getUI().setContent(new RegistrationFormPanel(applicant, aTask.getId()));
					}
					if ("validationForm".equals(formKey)) {
						getUI().setContent(new ValidationFormPanel(applicant, aTask.getId()));
					}
				}
			});
			layout.addComponent(taskButton);
		}
		setContent(layout);
	}
	
}

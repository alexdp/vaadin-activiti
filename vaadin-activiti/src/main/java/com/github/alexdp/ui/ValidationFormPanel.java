package com.github.alexdp.ui;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.TaskService;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.alexdp.dao.ApplicantRepository;
import com.github.alexdp.model.Applicant;
import com.github.alexdp.util.spring.SpringDependencyInjector;
import com.github.alexdp.util.vaadin.WorkflowFormLayout;
import com.vaadin.ui.Panel;

public class ValidationFormPanel extends Panel {
	
	@Autowired
	private TaskService taskService;

	public ValidationFormPanel(Applicant applicant, String taskId) {
		super("Registration Form");
		SpringDependencyInjector.getInjector().inject(this);
		WorkflowFormLayout<Applicant> form = new WorkflowFormLayout<Applicant>(applicant);
		form.addButton("accept", false, new WorkflowFormLayout.Command<Applicant>() {
			@Override
			public void execute(Applicant bean) {
		        Map<String, Object> variables = new HashMap<String, Object>();
				variables.put("approved", true);
		        taskService.complete(taskId, variables);
		        getUI().setContent(new LoginPanel());
			}
		});
		form.addButton("reject", false, new WorkflowFormLayout.Command<Applicant>() {
			@Override
			public void execute(Applicant bean) {
		        Map<String, Object> variables = new HashMap<String, Object>();
				variables.put("approved", false);
		        taskService.complete(taskId, variables);
		        getUI().setContent(new LoginPanel());
			}
		});
		form.setReadOnly(true);
		setContent(form);
	}

	

}

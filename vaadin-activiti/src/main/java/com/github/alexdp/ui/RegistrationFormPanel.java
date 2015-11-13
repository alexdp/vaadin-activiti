package com.github.alexdp.ui;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.TaskService;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.alexdp.dao.ApplicantRepository;
import com.github.alexdp.model.Applicant;
import com.github.alexdp.util.spring.SpringDependencyInjector;
import com.github.alexdp.util.vaadin.WorkflowFormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;

public class RegistrationFormPanel extends Panel {
	
	@Autowired
    private ApplicantRepository applicantRepository;
	
	@Autowired
	private TaskService taskService;

	public RegistrationFormPanel(Applicant applicant, String taskId) {
		super("Registration Form");
		SpringDependencyInjector.getInjector().inject(this);
		WorkflowFormLayout<Applicant> form = new WorkflowFormLayout<Applicant>(applicant);
		form.addButton("submit", true, new WorkflowFormLayout.Command<Applicant>() {
			@Override
			public void execute(Applicant bean) {
				applicantRepository.save(bean);
				Notification.show("Saved");
				Map<String, Object> variables = new HashMap<String, Object>();
		        variables.put("applicant", bean);
		        taskService.complete(taskId, variables);
		        getUI().setContent(new LoginPanel());
			}
		});
		setContent(form);
	}

	

}

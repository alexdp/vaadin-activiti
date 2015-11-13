package com.github.alexdp.service;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.FormService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DispatcherService {

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private IdentityService identityService;

	@Autowired
	private RepositoryService repositoryService;

	public List<ProcessDefinition> retreiveProcess(String User) {
		List<ProcessDefinition> result = new ArrayList<ProcessDefinition>();
		result.addAll(repositoryService.createProcessDefinitionQuery().startableByUser(User).list());
		return result;
	}
	
	public List<Task> retreiveTaskAssignee(String user) {
		List<Task> result = new ArrayList<Task>();
		result.addAll(taskService.createTaskQuery().taskAssignee(user).active().list());
		result.addAll(taskService.createTaskQuery().taskCandidateUser(user).active().list());
		return result;
	}

	public List<User> listUsers() {
		List<User> list = identityService.createUserQuery().list();
		return list;
	}

}

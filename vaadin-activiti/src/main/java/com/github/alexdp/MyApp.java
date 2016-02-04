package com.github.alexdp;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.context.annotation.EnableLoadTimeWeaving.AspectJWeaving;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

@SpringBootApplication
@EnableLoadTimeWeaving(aspectjWeaving=AspectJWeaving.ENABLED)
@EnableSpringConfigured
@EnableAspectJAutoProxy
public class MyApp extends SpringBootServletInitializer {


	public static void main(String[] args) {
		SpringApplication.run(MyApp.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(MyApp.class);
	}

	@Bean
	InitializingBean usersAndGroupsInitializer(final IdentityService identityService) {
		return new InitializingBean() {
			public void afterPropertiesSet() throws Exception {
				Group attendees = identityService.newGroup("applicants");
				identityService.saveGroup(attendees);
				Group staff = identityService.newGroup("staff");
				identityService.saveGroup(staff);

				User kermit = identityService.newUser("kermit");
				identityService.saveUser(kermit);
				identityService.createMembership("kermit", "applicants");

				User gonzo = identityService.newUser("gonzo");
				identityService.saveUser(gonzo);
				identityService.createMembership("gonzo", "staff");
			}
		};
	}

	@Bean
	public BeanPostProcessor activitiConfigurer() {
		return new BeanPostProcessor() {
			@Override
			public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
				if (bean instanceof SpringProcessEngineConfiguration) {
					SpringProcessEngineConfiguration config = (SpringProcessEngineConfiguration) bean;
					config.setMailServerHost("smtp.gmail.com");
					config.setMailServerPort(587);
					config.setMailServerUseSSL(false);
					config.setMailServerUseTLS(true);
					config.setMailServerDefaultFrom("alexandre.de.pellegrin@gmail.com");
					config.setMailServerUsername("alexandre.de.pellegrin@gmail.com");
					config.setMailServerPassword("xxxxxxx");
				}
				return bean;
			}

			@Override
			public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
				return bean;
			}

		};
	}

}

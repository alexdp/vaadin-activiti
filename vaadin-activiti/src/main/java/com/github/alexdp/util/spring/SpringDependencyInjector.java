package com.github.alexdp.util.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringDependencyInjector implements ApplicationContextAware {

	/**
     * Spring constructor. NOT PART OF THE PUBLIC API.
     */
    public SpringDependencyInjector() {
    	INSTANCE = this;
    }
    
    /**
     * @return the only object instance
     */
    public static SpringDependencyInjector getInjector() {
        return INSTANCE;
    }
    
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException
    {
        this.context = context;
    }


    /**
     * Injects Spring beans on autowired fields 
     * @param o
     */
    public void inject(Object o) {
    	AutowireCapableBeanFactory beanFactory = this.context.getAutowireCapableBeanFactory();
    	beanFactory.autowireBean(o);
    }
    
    
    /**
     * Spring context
     */
    private ApplicationContext context;
    
    /**
     * Singleton instance
     */
    private static SpringDependencyInjector INSTANCE;
	
}

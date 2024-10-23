package org.demo.useraccounts.validators;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class ValidatorsConfig {

    @Bean
    @Primary
    public Validator localValidatorFactoryBean() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public DefaultSpringBeanValidator genericSpringBeanValidator(){
       return new DefaultSpringBeanValidator(localValidatorFactoryBean());
    }

}

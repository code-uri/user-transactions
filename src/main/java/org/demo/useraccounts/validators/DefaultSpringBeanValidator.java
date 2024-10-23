package org.demo.useraccounts.validators;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Component
public class DefaultSpringBeanValidator {

       Validator validator;

       DefaultSpringBeanValidator(Validator validator){
           this.validator = validator;
       }

       public <T> Mono<T> validateAndGet(Class<T> validationClass, final ServerRequest request, Function<T, Mono<T>> function ) {
           return request.bodyToMono(validationClass)
                   .flatMap(body -> {
                       Errors errors = new BeanPropertyBindingResult(
                               body,
                               validationClass.getName());
                       validator.validate(body, errors);

                       if (errors.getAllErrors().isEmpty()) {
                           return function.apply(body);
                       } else {
                           throw new ResponseStatusException(
                                   HttpStatus.BAD_REQUEST,
                                   errors.getAllErrors().toString());
                       }
                   });
       }

   }
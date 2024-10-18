package org.demo.useraccounts.controllers;

import lombok.extern.slf4j.Slf4j;
import org.demo.useraccounts.model.UserAccount;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/users")
@Slf4j
public class UserAccountsController extends AbstractCRUDController<UserAccount, Long> {

  protected UserAccountsController(ReactiveCrudRepository<UserAccount, Long> repository) {
    super(repository);
  }
}
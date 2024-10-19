package org.demo.useraccounts.controllers;

import lombok.extern.slf4j.Slf4j;
import org.demo.useraccounts.model.UserAccount;
import org.demo.useraccounts.repository.BaseRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/accounts")
@Slf4j
public class UserAccountsController extends AbstractCRUDController<UserAccount, Long> {

  protected UserAccountsController(BaseRepository<UserAccount, Long> repository) {
    super(repository);
  }
}
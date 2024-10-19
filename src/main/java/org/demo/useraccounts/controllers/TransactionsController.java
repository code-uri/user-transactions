package org.demo.useraccounts.controllers;

import lombok.extern.slf4j.Slf4j;
import org.demo.useraccounts.model.Transaction;
import org.demo.useraccounts.repository.BaseRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/accounts/{accountId}/transactions")
@Slf4j
public class TransactionsController extends AbstractCRUDController<Transaction, Long> {

  protected TransactionsController(BaseRepository<Transaction, Long> repository) {
    super(repository);
  }
}
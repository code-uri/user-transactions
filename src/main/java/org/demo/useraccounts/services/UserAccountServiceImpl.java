package org.demo.useraccounts.services;

import org.demo.useraccounts.model.UserAccount;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserAccountServiceImpl implements UserAccountService{

    @Override
    public Mono<UserAccount> create(UserAccount entity) {
        return null;
    }

    @Override
    public Mono<UserAccount> findById(Long id) {
        return null;
    }

    @Override
    public Mono<UserAccount> update(Long id, UserAccount entity) {
        return null;
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return null;
    }
}

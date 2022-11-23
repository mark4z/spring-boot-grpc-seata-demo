package com.example.demoa;

import demo.a.account.AccountReply;
import demo.a.account.AccountRequest;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author mark4z
 * @date 2022/11/23 15:47
 */
@Service
public class AccountGrpc extends demo.a.account.AccountGrpc.AccountImplBase {
    private final AccountRepository accountRepository;

    public AccountGrpc(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public void getAccount(AccountRequest request, StreamObserver<AccountReply> responseObserver) {
        Optional<Account> account = accountRepository.findById(request.getId());
        AccountReply.Builder reply = AccountReply.newBuilder();
        reply.setId(account.get().getId());
        reply.setAmount(account.get().getAmount());
        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(AccountRequest request, StreamObserver<AccountReply> responseObserver) {
        Optional<Account> account = accountRepository.findById(request.getId());
        AccountReply.Builder reply = AccountReply.newBuilder();
        if (account.isPresent()) {
            account.get().setAmount(request.getAmount());
            accountRepository.save(account.get());

            reply.setId(account.get().getId());
            reply.setAmount(account.get().getAmount());
        }
        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }
}

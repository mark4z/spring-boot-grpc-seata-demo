package com.example.demob;

import demo.a.account.AccountReply;
import demo.a.account.AccountRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import io.seata.integration.grpc.interceptor.client.ClientTransactionInterceptor;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

/**
 * @author mark4z
 * @date 2022/11/23 15:47
 */
@Service
@Slf4j
public class AccountGrpc extends demo.a.account.AccountGrpc.AccountImplBase {
    private final AccountRepository accountRepository;
    private final demo.a.account.AccountGrpc.AccountBlockingStub accountBlockingStub;

    public AccountGrpc(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
        ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:50052")
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                // needing certificates.
                .usePlaintext()
                .build();
        this.accountBlockingStub = demo.a.account.AccountGrpc.newBlockingStub(channel).withInterceptors(new ClientTransactionInterceptor());
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
    @Transactional(rollbackOn = Exception.class)
    @GlobalTransactional(rollbackFor = Exception.class)
    public void update(AccountRequest request, StreamObserver<AccountReply> responseObserver) {
        Optional<Account> account = accountRepository.findById(request.getId());
        AccountReply.Builder reply = AccountReply.newBuilder();
        if (account.isPresent()) {
            AccountReply update = accountBlockingStub.update(AccountRequest.newBuilder()
                    .setId(request.getId())
                    .setAmount(request.getAmount())
                    .build());
            log.info("update: {}", update);
            reply.setId(account.get().getId());
            reply.setAmount(account.get().getAmount());

            if (request.getAmount() == -1L) {
                throw new RuntimeException("amount can not be -1");
            }
            account.get().setAmount(request.getAmount());
            accountRepository.save(account.get());
        }
        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }
}

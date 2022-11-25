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
import org.springframework.transaction.annotation.Transactional;

/**
 * @author mark4z
 * @date 2022/11/23 15:47
 */
@Service
@Slf4j
public class AccountGrpc extends demo.a.account.AccountGrpc.AccountImplBase {
    private final AccountMapper accountMapper;
    private final demo.a.account.AccountGrpc.AccountBlockingStub accountBlockingStub;

    public AccountGrpc(AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
        ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:50052")
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                // needing certificates.
                .usePlaintext()
                .build();
        this.accountBlockingStub = demo.a.account.AccountGrpc.newBlockingStub(channel).withInterceptors(new ClientTransactionInterceptor());
    }

    @Override
    public void getAccount(AccountRequest request, StreamObserver<AccountReply> responseObserver) {
        Account account = accountMapper.selectById(request.getId());
        AccountReply.Builder reply = AccountReply.newBuilder();
        reply.setId(account.getId());
        reply.setAmount(account.getAmount());
        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @GlobalTransactional(rollbackFor = Exception.class)
    public void update(AccountRequest request, StreamObserver<AccountReply> responseObserver) {
        Account account = accountMapper.selectById(request.getId());
        AccountReply.Builder reply = AccountReply.newBuilder();
        if (account != null) {
            AccountReply update = accountBlockingStub.update(AccountRequest.newBuilder()
                    .setId(request.getId())
                    .setAmount(request.getAmount())
                    .build());
            log.info("update: {}", update);
            reply.setId(account.getId());
            reply.setAmount(account.getAmount());


            account.setAmount(request.getAmount());
            accountMapper.updateById(account);

            if (request.getAmount() == -1L) {
                throw new RuntimeException("amount can not be -1");
            }
        }
        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }
}

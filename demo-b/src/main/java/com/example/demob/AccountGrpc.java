package com.example.demob;

import demo.a.account.AccountReply;
import demo.a.account.AccountRequest;
import io.grpc.stub.StreamObserver;
import io.seata.core.context.RootContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author mark4z
 * @date 2022/11/23 15:47
 */
@Service
public class AccountGrpc extends demo.a.account.AccountGrpc.AccountImplBase {
    private final AccountMapper accountMapper;

    public AccountGrpc(AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
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
    public void update(AccountRequest request, StreamObserver<AccountReply> responseObserver) {
        System.out.println("xid:" + RootContext.getXID());
        Account account = accountMapper.selectById(request.getId());
        AccountReply.Builder reply = AccountReply.newBuilder();
        if (account != null) {
            account.setAmount(request.getAmount());
            accountMapper.updateById(account);
            System.out.println("update: " + account);

            reply.setId(account.getId());
            reply.setAmount(account.getAmount());
        }
        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }
}

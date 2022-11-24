package com.example.demob;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;
import io.grpc.protobuf.services.ProtoReflectionService;
import io.seata.integration.grpc.interceptor.server.ServerTransactionInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@SpringBootApplication
@Slf4j
public class DemoBApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoBApplication.class, args);
        log.info("demo-b started");
    }

    @Component
    static class GrpcServerRunner implements CommandLineRunner {
        @Resource
        private AccountGrpc accountGrpc;
        @Resource
        private AccountMapper accountMapper;

        @Override
        public void run(String... args) throws Exception {
            int port = 50052;
            Server start = ServerBuilder.forPort(port)
                    .addService(accountGrpc)
                    .addService(ProtoReflectionService.newInstance())
                    .addService(ServerInterceptors.intercept(accountGrpc, new ServerTransactionInterceptor()))
                    .build()
                    .start();
            log.info("Server started, listening on " + start.getPort());

            accountMapper.delete(new QueryWrapper<>());
            Account account = new Account();
            account.setId(1L);
            account.setAmount(1000L);
            accountMapper.insert(account);
        }
    }

}

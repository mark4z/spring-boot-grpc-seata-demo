package com.example.demob;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@SpringBootApplication
@Slf4j
public class DemoAApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoAApplication.class, args);
        log.info("demo-a started");
    }

    @Service
    static class GrpcServerRunner implements CommandLineRunner {
        @Resource
        private AccountGrpc accountGrpc;
        @Resource
        private AccountMapper accountRepository;

        @Override
        public void run(String... args) throws Exception {
            int port = 50051;
            Server start = ServerBuilder.forPort(port)
                    .addService(accountGrpc)
                    .addService(ProtoReflectionService.newInstance())
                    .build()
                    .start();
            log.info("Server started, listening on " + start.getPort());

            Account account = new Account();
            account.setId(1L);
            account.setAmount(1000L);
            System.out.println("init accounting");
            Account save = accountRepository.save(account);
            System.out.println("init account: " + save);
        }
    }

}

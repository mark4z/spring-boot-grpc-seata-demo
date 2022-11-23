package com.example.demoa;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;
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
        log.info("demo-a started");
    }

    @Component
    static class GrpcServerRunner implements CommandLineRunner {
        @Resource
        private AccountGrpc accountGrpc;

        @Override
        public void run(String... args) throws Exception {
            int port = 50052;
            Server start = ServerBuilder.forPort(port)
                    .addService(accountGrpc)
                    .addService(ProtoReflectionService.newInstance())
                    .build()
                    .start();
            log.info("Server started, listening on " + start.getPort());
        }
    }

}

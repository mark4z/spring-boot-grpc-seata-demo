package com.example.demob;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Account {
    @Id
    private Long id;
    private Long amount;
}
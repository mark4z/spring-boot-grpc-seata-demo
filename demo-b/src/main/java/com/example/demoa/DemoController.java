package com.example.demoa;

import org.springframework.web.bind.annotation.*;


@RestController
public class DemoController {
    private final AccountRepository accountRepository;

    public DemoController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @GetMapping("/list")
    public Iterable<Account> getAccounts() {
        return accountRepository.findAll();
    }

    @PostMapping("/add")
    public Long add(@RequestBody Account account) {
        accountRepository.save(account);
        return account.getId();
    }

    @PostMapping("/update")
    public Long update(@RequestBody Account account) {
        Iterable<Account> all = accountRepository.findAll();
        for (Account a : all) {
            a.setAmount(a.getAmount() - account.getAmount());
        }
        accountRepository.saveAll(all);
        return account.getId();
    }

    @DeleteMapping("/delete")
    public void delete() {
        accountRepository.deleteAll();
    }
}

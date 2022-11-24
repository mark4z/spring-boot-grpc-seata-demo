package com.example.demob;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Mapper
public interface AccountMapper extends BaseMapper<Account> {
}
package com.agan.boot;

import com.agan.boot.controller.UserController;
import com.agan.boot.entity.User;
import com.agan.boot.mapper.UserMapper;
import com.agan.boot.util.SpringUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import tk.mybatis.mapper.entity.Example;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TkBatchTest {

    @Autowired
    private UserMapper userMapper;

    /**
     *
     SELECT
         id,
         username,
         password,
         sex,
         deleted,
         update_time,
         create_time
     FROM
        users
     WHERE
        id in (1004,1005,1006);
     */
    @Test
    public void selectByIdList(){
        // 继承IdListMapper接口: 拥有: selectByIdList(), deleteByIdList()
        List<User> userList = userMapper.selectByIdList(Arrays.asList(1004, 1005, 1006));
        System.out.println(userList);
        //userMapper.deleteByIdList();
    }

    @Test
    public void yyy(){
        // 继承IdsMapper接口: 拥有: selectByIds(), deleteByIds()
        //userMapper.selectByIds();
        //userMapper.deleteByIds();
    }

    @Test
    public void zzz(){
        userMapper.selectByBusinessId(1);
    }
}


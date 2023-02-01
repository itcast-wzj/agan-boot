package com.agan.boot;
import java.util.Arrays;
import java.util.Date;

import com.agan.boot.entity.User;
import com.agan.boot.mapper.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import tk.mybatis.mapper.entity.Example;

import javax.jws.soap.SOAPBinding;

/**
 * 增删改查
 * 分页
 * 总数
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootMybatisApplicationTests {

    @Autowired
    private UserMapper userMapper;

    /**
     * 建表时的default, 只对insert语句生效
     */
    @Test
    public void insert() {
        User user = new User();
        user.setUsername("wzj-1");
        user.setPassword("wzj-1");
        user.setSex((byte)0);
        user.setDeleted((byte)0);
        user.setUpdateTime(new Date());
        user.setCreateTime(new Date());
        userMapper.insert(user);
    }

    /**
     * INSERT INTO users (id, username, PASSWORD ) VALUES( 'null', 'wzj-2', 'wzj-2' );
     */
    @Test
    public void insertSelective() {
        User user = new User();
        user.setUsername("wzj-2");
        user.setPassword("wzj-2");
        userMapper.insertSelective(user);
    }

    /**
     * 根据主键删除
     * DELETE FROM users WHERE id = 1;
     */
    @Test
    public void deleteByPrimaryKey(){
        userMapper.deleteByPrimaryKey(1);
    }

    /**
     * 根据基本条件删除(赋值运算符 = )
     * DELETE
     * FROM
     * 	users
     * WHERE
     * 	id = 1
     * 	AND username = 'user2'
     * 	AND PASSWORD = 'user2';
     */
    @Test
    public void delete(){
        User user = new User();
        user.setId(1);
        user.setUsername("user2");
        user.setPassword("user2");
        userMapper.delete(user);
    }

    /**
     * 根据复杂条件删除(in, or, like, is null, is not null, between, >, < .....)
     * DELETE
     * FROM
     * 	users
     * WHERE
     * 	( sex = 0 AND id IN ( 1, 2, 3 ) AND PASSWORD LIKE '%user2' AND update_time IS NOT NULL );
     */
    @Test
    public void deleteByExample(){
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("sex", 0);
        criteria.andIn("id", Arrays.asList(1,2,3));
        criteria.andLike("password", "%user2");
        criteria.andIsNotNull("updateTime");
        userMapper.deleteByExample(example);
    }

    /**
     * 根据主键全部更新(根据什么: 主键, 更新什么: 除主键外所有字段)
     * 这里会报错: 是因为字段tinyint:[sex、deleted], timestamp:[update_time, create_time]在建表的时候定义的是NOT NULL
     *
     * UPDATE
     *     users
     * SET
     *     id = id,
     *     username = 'update-username',
     *     password = 'update-password',
     *     sex = null,
     *     deleted = null,
     *     update_time = null,
     *     create_time = null
     * WHERE
     *     id = 4;
     */
    @Test
    public void updateByPrimaryKey(){
        User user = new User();
        user.setId(4);
        user.setUsername("update-username");
        user.setPassword("update-password");
        userMapper.updateByPrimaryKey(user);
    }

    /**
     * 根据主键选择更新(根据什么: 主键, 选择更新什么: 选择不为空的值进行更新)
     * UPDATE
     *     users
     * SET
     *     id = id,
     *     username = 'update-username',
     *     password = 'update-password'
     * WHERE
     *     id = 4;
     */
    @Test
    public void updateByPrimaryKeySelective(){
        User user = new User();
        user.setId(4);
        user.setUsername("update-username");
        user.setPassword("update-password");
        userMapper.updateByPrimaryKeySelective(user);
    }

    /**
     *
     * 根据自定义复杂条件全部更新(根据什么: 自定义复杂条件(Example), 更新什么: 所有字段)
     * 这里会报错: 是因为字段tinyint:[sex、deleted], timestamp:[update_time, create_time]在建表的时候定义的是NOT NULL
     *
     * UPDATE
     *     users
     * SET
     *     id = id, -- 不影响
     *     username = 'updateByExample-username',
     *     password = 'updateByExample-password',
     *     sex = null,
     *     deleted = null,
     *     update_time = null,
     *     create_time = null
     * WHERE
     * (
     * 		id = 1004
     * 		and username like 'user1'
     * );
     */
    @Test
    public void updateByExample(){
        User user = new User();
        user.setUsername("updateByExample-username");
        user.setPassword("updateByExample-password");

        Example example = new Example(User.class);
        example.createCriteria().andEqualTo("id", 1004).andLike("username", "user1");
        // 参数一: 更新的内容, 参数二: 更新的条件
        userMapper.updateByExample(user, example);
    }

    /**
     * 根据自定义复杂条件选择性更新(根据什么: 自定义复杂条件(Example), 更新什么: 选择不为空的值进行更新)
     * UPDATE
     *     users
     * SET
     *     id = id,
     *     username = 'updateByExample-username',
     *     password = 'updateByExample-password'
     * WHERE
     * (
     * 		id in (1005 , 1006) and username like '%user2'
     * );
     */
    @Test
    public void updateByExampleSelective(){
        User user = new User();
        user.setUsername("updateByExample-username");
        user.setPassword("updateByExample-password");

        Example example = new Example(User.class);
        example.createCriteria().andIn("id", Arrays.asList(1005, 1006)).andLike("username", "%user2");
        // 参数一: 更新的内容, 参数二: 更新的条件
        userMapper.updateByExampleSelective(user, example);

        // 查询:
        //userMapper.selectByPrimaryKey(); // 根据简单条件查询单个: 根据主键一个条件查询
        //userMapper.selectOne(); // 根据简单条件查询单个: 根据多个条件查询
        //userMapper.select(); // 根据简单条件查询多个
        //userMapper.selectAll(); // 查询所有

        //userMapper.selectOneByExample(); // 根据复杂条件查询单个
        //userMapper.selectByExample(); // 根据复杂条件查询多个

        // 总数
        //userMapper.selectCount();
        //userMapper.selectCountByExample()

        // 分页
        //userMapper.selectByRowBounds();
        //userMapper.selectByExampleAndRowBounds()

        // 批量(继承批量接口)

        // 扩展: 这些接口也可以自定义
    }

    @Test
    public void selectByPrimaryKey(){

    }

    @Test
    public void selectOne(){

    }

    @Test
    public void select(){

    }

    @Test
    public void selectAll(){

    }
}


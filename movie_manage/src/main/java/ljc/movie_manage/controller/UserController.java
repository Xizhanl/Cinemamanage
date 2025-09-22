package ljc.movie_manage.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ljc.movie_manage.common.QueryPageParam;
import ljc.movie_manage.common.Result;
import ljc.movie_manage.entity.User;
import ljc.movie_manage.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 用户信息表 前端控制器
 * </p>
 *
 * @author ljc
 * @since 2025-05-29
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IUserService userService;
    @GetMapping("/list")
    public List<User> list(){return userService.list();}
    //登录
    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        List list=userService.lambdaQuery()
                .eq(User::getUsername,user.getUsername())
                .eq(User::getPassword,user.getPassword())
                .eq(User::getRole,user.getRole())
                .list();
        return !list.isEmpty() ?Result.suc(list.get(0)):Result.fail();
    }
    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        // 1. 检查用户名是否已存在
        long usernameCount = userService.lambdaQuery()
                .eq(User::getUsername, user.getUsername())
                .count();
        if (usernameCount > 0) {
            return Result.fail("用户名已存在");
        }
        boolean saveResult = userService.save(user);
        return saveResult ? Result.suc(user) : Result.fail();
    }

    //新增
    @PostMapping("/save")
    public Result save(@RequestBody User user) {
        return userService.save(user)?Result.suc():Result.fail();
    }
    //修改
    @PostMapping("/mod")
    public Result mod(@RequestBody User user){
        return userService.updateById(user)?Result.suc():Result.fail();
    }
    //新增或修改
    @PostMapping("/saveOrMod")
    public Result saveOrMod(@RequestBody User user){
        return userService.saveOrUpdate(user)?Result.suc():Result.fail();
    }
    //删除
    @GetMapping("/delete")
    public boolean delete(Integer id){
        return userService.removeById(id);
    }
    //删除
    @GetMapping("/del")
    public Result del(@RequestParam String id){
        return userService.removeById(id)?Result.suc():Result.fail();
    }
    //查询
    @PostMapping("/listP")
    public Result listP(@RequestBody User user){
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper();
        //判断用户名是否为空
        if(StringUtils.isNotBlank(user.getUsername())){
            queryWrapper.like(User::getUsername,user.getUsername());
        }
        return Result.suc(userService.list(queryWrapper));
    }
    //分页查询
    @PostMapping("/listPage")
    public List<User> listPage(@RequestBody QueryPageParam query){
        HashMap<String, Object> param = query.getParam();
        String username =(String)param.get("username");
        System.out.println("username=="+(String)param.get("username"));
        //分页mybatis
        Page<User> page = new Page<>();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(User::getUsername,username);

        IPage<User> result = userService.page(page,queryWrapper);
        System.out.println("total==" + result.getTotal());

        return result.getRecords();
    }
    //自定义分页
    @PostMapping("/listPageC")
    public List<User> listPageC(@RequestBody QueryPageParam query){
        HashMap<String, Object> param = query.getParam();
        String username =(String)param.get("username");

        //分页mybatis
        Page<User> page = new Page<>();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(User::getUsername,username);
        IPage<User> result = userService.pageC(page);

        return result.getRecords();
    }
    @PostMapping("/listPageC1")
    public Result listPageC1(@RequestBody QueryPageParam query){
        HashMap<String, Object> param = query.getParam();
        String username =(String)param.get("username");
        //分页mybatis
        Page<User> page = new Page<>();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(username)){
            queryWrapper.like(User::getUsername,username);
        }
        IPage<User> result = userService.pageCC(page,queryWrapper);
        return Result.suc(result.getRecords(),result.getTotal());
    }

}

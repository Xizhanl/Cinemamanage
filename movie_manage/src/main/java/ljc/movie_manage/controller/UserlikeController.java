package ljc.movie_manage.controller;

import ljc.movie_manage.common.Result;
import ljc.movie_manage.entity.Userlike;
import ljc.movie_manage.service.IUserlikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ljc
 * @since 2025-11-25
 */
@RestController
@RequestMapping("/userlike")
public class UserlikeController {
    @Autowired
    private IUserlikeService userlikeService;
    //获取列表
    @GetMapping("/list")
    public List<Userlike> list(){return userlikeService.list();}
    //新增
    @PostMapping("/save")
    public Result save(@RequestBody Userlike userlike){
        return userlikeService.save(userlike)?Result.suc():Result.fail();
    }
    //删除
    @PostMapping("/del")
    public Result del(@RequestBody Userlike userlike){
        return userlikeService.removeById(userlike)?Result.suc():Result.fail();
    }
}

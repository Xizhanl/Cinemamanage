package ljc.movie_manage.controller;

import ljc.movie_manage.common.Result;
import ljc.movie_manage.service.IReviewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ljc.movie_manage.entity.Reviews;
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
@RequestMapping("/reviews")
public class ReviewsController {
    @Autowired
    private IReviewsService reviewsService;
    //获取列表
    @GetMapping("/list")
    public List<Reviews> list(){return reviewsService.list();}
    //新增
    @PostMapping("/save")
    public Result save(@RequestBody Reviews reviews){
        return reviewsService.save(reviews)?Result.suc():Result.fail();
    }
    //删除
    @PostMapping("/del")
    public Result del(@RequestBody Reviews reviews){
        return reviewsService.removeById(reviews)?Result.suc():Result.fail();
    }
    //编辑
    @PostMapping("/mod")
    public Result mod(@RequestBody Reviews reviews){
        return reviewsService.updateById(reviews)?Result.suc():Result.fail();
    }

}

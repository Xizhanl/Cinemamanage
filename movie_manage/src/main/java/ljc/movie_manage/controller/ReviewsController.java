package ljc.movie_manage.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ljc.movie_manage.common.QueryPageParam;
import ljc.movie_manage.common.Result;
import ljc.movie_manage.service.IReviewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ljc.movie_manage.entity.Reviews;

import java.util.HashMap;
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
    @GetMapping("/del")
    public Result del(@RequestParam String id){
        return reviewsService.removeById(id)?Result.suc():Result.fail();
    }
    //编辑
    @PostMapping("/mod")
    public Result mod(@RequestBody Reviews reviews){
        return reviewsService.updateById(reviews)?Result.suc():Result.fail();
    }
    @PostMapping("/listPageC1")
    public Result listPageC1(@RequestBody QueryPageParam query){
        HashMap<String, Object> param = query.getParam();
        String username =(String)param.get("username");
        String moviename =(String)param.get("moviename");
        //分页mybatis
        Page<Reviews> page = new Page<>();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<Reviews> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(Reviews::getUsername,username);
        queryWrapper.like(Reviews::getMoviename,moviename);
        IPage<Reviews> result = reviewsService.page(page,queryWrapper);
        return Result.suc(result.getRecords(),result.getTotal());
    }

}

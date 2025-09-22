package ljc.movie_manage.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ljc.movie_manage.common.QueryPageParam;
import ljc.movie_manage.common.Result;
import ljc.movie_manage.entity.Cinema;
import ljc.movie_manage.service.ICinemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ljc
 * @since 2025-06-17
 */
@RestController
@RequestMapping("/cinema")
public class CinemaController {
    @Autowired
    private ICinemaService cinemaService;
    @GetMapping("/list")
    public List<Cinema> list(){return cinemaService.list();}
    @PostMapping("/mod")
    public Result mod(@RequestBody Cinema cinema){
        return cinemaService.updateById(cinema)?Result.suc():Result.fail();
    }
    @PostMapping("/listPageC1")
    public Result listPageC1(@RequestBody QueryPageParam query){
        HashMap<String, Object> param = query.getParam();
        String name =(String)param.get("name");

        Page<Cinema> page = new Page<>();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<Cinema> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name)){
            queryWrapper.like(Cinema::getName,name);
        }
        IPage<Cinema> result = cinemaService.page(page,queryWrapper);
        return Result.suc(result.getRecords(),result.getTotal());
    }
}

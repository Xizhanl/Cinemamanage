package ljc.movie_manage.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ljc.movie_manage.common.QueryPageParam;
import ljc.movie_manage.common.Result;
import ljc.movie_manage.entity.Movie;
import ljc.movie_manage.entity.Movietypes;
import ljc.movie_manage.service.IMovieService;
import ljc.movie_manage.service.IMovietypesService;
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
 * @since 2025-06-26
 */
@RestController
@RequestMapping("/movietypes")
public class MovietypesController {
    @Autowired
    private IMovietypesService movietypesService;
    @GetMapping("/list")
    public List<Movietypes> list(){return movietypesService.list();}
    //新增
    @PostMapping("/save")
    public Result save(@RequestBody Movietypes movietypes) {
        return movietypesService.save(movietypes)?Result.suc():Result.fail();
    }
    //修改
    @PostMapping("/mod")
    public Result mod(@RequestBody Movietypes movietypes){
        return movietypesService.updateById(movietypes)?Result.suc():Result.fail();
    }
    //删除
    @GetMapping("/del")
    public Result del(@RequestParam String id){
        return movietypesService.removeById(id)?Result.suc():Result.fail();
    }
    @PostMapping("/listPageC1")
    public Result listPageC1(@RequestBody QueryPageParam query){
        HashMap<String, Object> param = query.getParam();
        String type =(String)param.get("type");

        Page<Movietypes> page = new Page<>();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<Movietypes> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(type)){
            queryWrapper.like(Movietypes::getType,type);
        }
        IPage<Movietypes> result = movietypesService.pageCC(page,queryWrapper);
        return Result.suc(result.getRecords(),result.getTotal());
    }

}

package ljc.movie_manage.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ljc.movie_manage.common.QueryPageParam;
import ljc.movie_manage.common.Result;
import ljc.movie_manage.entity.Hall;
import ljc.movie_manage.service.IHallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/hall")
public class HallController {
    @Autowired
    private IHallService hallService;
    @GetMapping("/list")
    public List<Hall> list(){return hallService.list();}
    @PostMapping("/mod")
    public Result mod(@RequestBody Hall hall){
        return hallService.updateById(hall)?Result.suc():Result.fail();
    }
    @GetMapping("/del")
    public Result del(@RequestParam String id){
        return hallService.removeById(id)?Result.suc():Result.fail();
    }
    @PostMapping("/save")
    public Result save(@RequestBody Hall hall) {
        return hallService.save(hall)?Result.suc():Result.fail();
    }
    //新增或修改
    @PostMapping("/saveOrMod")
    public Result saveOrMod(@RequestBody Hall hall){
        return hallService.saveOrUpdate(hall)?Result.suc():Result.fail();
    }
    @PostMapping("/listPage")
    public Result listPage(@RequestBody QueryPageParam query){
        HashMap<String, Object> param = query.getParam();
        String hallname =(String)param.get("hallname");
        String halltype = (String) param.get("halltype"); // 新增影厅类型参数
        String statu = (String) param.get("statu");

        Page<Hall> page = new Page<>();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<Hall> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(hallname)){
            queryWrapper.like(Hall::getHallname,hallname);
        }
        // 影厅类型查询（新增）
        if (StringUtils.isNotBlank(halltype)) {
            queryWrapper.eq(Hall::getHalltype, halltype); // 使用eq精确匹配
        }
        if (StringUtils.isNotBlank(statu)) {
            queryWrapper.eq(Hall::getStatu, statu);
        }
        IPage<Hall> result = hallService.page(page,queryWrapper);
        return Result.suc(result.getRecords(),result.getTotal());
    }
}

package ljc.movie_manage.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ljc.movie_manage.common.QueryPageParam;
import ljc.movie_manage.common.Result;
import ljc.movie_manage.entity.Movie;
import ljc.movie_manage.service.IMovieService;
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
 * @since 2025-06-01
 */
@RestController
@RequestMapping("/movie")
public class MovieController {
    @Autowired
    private IMovieService movieService;

    @GetMapping("/list")
    public List<Movie> list(){return movieService.list();}
    /**
     * 获取正在热映的电影列表
     * @return 统一格式的响应
     */
    @GetMapping("/ongoing")
    public Result getNowShowingMovies() {
        try {
            List<Movie> movies = movieService.getNowShowingMovies();
            return Result.suc(movies, (long) movies.size());
        } catch (Exception e) {
            return Result.fail("获取电影列表失败: " + e.getMessage());
        }
    }
    /**
     * 获取即将上映的电影列表
     * @return 统一格式的响应
     */
    @GetMapping("/upcoming")
    public Result getComingSoonMovies() {
        try {
            List<Movie> movies = movieService.getComingSoonMovies();
            return Result.suc(movies, (long) movies.size());
        } catch (Exception e) {
            return Result.fail("获取即将上映电影失败: " + e.getMessage());
        }
    }
    //新增
    @PostMapping("/save")
    public Result save(@RequestBody Movie movie) {
        return movieService.save(movie)?Result.suc():Result.fail();
    }
    //修改
    @PostMapping("/mod")
    public Result mod(@RequestBody Movie movie){
        return movieService.updateById(movie)?Result.suc():Result.fail();
    }
    //删除
    @GetMapping("/del")
    public Result del(@RequestParam String id){
        return movieService.removeById(id)?Result.suc():Result.fail();
    }
    @PostMapping("/updateBoxOffice")
    public Result updateBoxOffice(@RequestParam String id, @RequestParam int amount) {
        try {
            Movie movie = movieService.getById(id);
            if (movie == null) {
                return Result.fail("电影不存在");
            }
            // 更新票房（假设moviebox是Double类型）
            movie.setMoviebox(movie.getMoviebox() + amount);
            boolean updated = movieService.updateById(movie);

            return updated ? Result.suc() : Result.fail("票房更新失败");
        } catch (Exception e) {
            return Result.fail("票房更新失败: " + e.getMessage());
        }
    }
    @GetMapping("/search")
    public Result searchMovies(@RequestParam(required = false) String moviename) {
        try {
            LambdaQueryWrapper<Movie> queryWrapper = new LambdaQueryWrapper<>();
            // 按电影名称模糊查询
            if (StringUtils.isNotBlank(moviename)) {
                queryWrapper.like(Movie::getMoviename, moviename);
            }
            // 执行查询
            List<Movie> movies = movieService.list(queryWrapper);
            // 返回结果，包含数据列表和总数
            return Result.suc(movies, (long) movies.size());
        } catch (Exception e) {
            // 异常处理
            return Result.fail("搜索失败: " + e.getMessage());
        }
    }
    @PostMapping("/listPageC1")
    public Result listPageC1(@RequestBody QueryPageParam query){
        HashMap<String, Object> param = query.getParam();
        String moviename =(String)param.get("moviename");
        // 安全处理数组类型的movietypes
        Object movietypeObj = param.get("movietype");

        Page<Movie> page = new Page<>();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<Movie> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(moviename)){
            queryWrapper.like(Movie::getMoviename,moviename);
        }
        // 电影类型查询（有值才添加条件）
        if (movietypeObj != null) {
            if (movietypeObj instanceof String && !((String)movietypeObj).isEmpty()) {
                // 处理单个类型字符串
                queryWrapper.like(Movie::getMovietype, "\"" + movietypeObj + "\"");
            } else if (movietypeObj instanceof List && !((List<?>)movietypeObj).isEmpty()) {
                // 处理类型数组
                List<String> types = (List<String>) movietypeObj;
                queryWrapper.and(wrapper -> {
                    for (String type : types) {
                        if (StringUtils.isNotBlank(type)) {
                            wrapper.or().like(Movie::getMovietype, "\"" + type + "\"");
                        }
                    }
                });
            }
        }

        IPage<Movie> result = movieService.pageCC(page,queryWrapper);
        return Result.suc(result.getRecords(),result.getTotal());
    }
}

package ljc.movie_manage.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import ljc.movie_manage.entity.Movie;
import com.baomidou.mybatisplus.extension.service.IService;
import ljc.movie_manage.entity.User;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ljc
 * @since 2025-06-01
 */
public interface IMovieService extends IService<Movie> {
    IPage<Movie> pageCC(IPage<Movie> page, Wrapper<Movie> Wrapper);
    /**
     * 获取正在热映的电影
     * @return 电影列表
     */
    List<Movie> getNowShowingMovies();

    /**
     * 获取即将上映的电影
     * @return 电影列表
     */
    List<Movie> getComingSoonMovies();
}

package ljc.movie_manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import ljc.movie_manage.entity.Movie;
import ljc.movie_manage.entity.User;
import ljc.movie_manage.mapper.MovieMapper;
import ljc.movie_manage.mapper.UserMapper;
import ljc.movie_manage.service.IMovieService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ljc
 * @since 2025-06-01
 */
@Service
public class MovieServiceImpl extends ServiceImpl<MovieMapper, Movie> implements IMovieService {
    @Resource
    private MovieMapper movieMapper;
    @Override
    public List<Movie> getNowShowingMovies() {
        Date currentDate = new Date();
        return movieMapper.selectNowShowingMovies(currentDate, 20);
    }

    @Override
    public List<Movie> getComingSoonMovies() {
        Date currentDate = new Date();
        return movieMapper.selectComingSoonMovies(currentDate, 20);
    }
    @Override
    public IPage<Movie> pageCC(IPage<Movie> page, Wrapper<Movie> wrapper) {
        return movieMapper.pageCC(page,wrapper);
    }
}

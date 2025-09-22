package ljc.movie_manage.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import ljc.movie_manage.entity.Movie;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ljc.movie_manage.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ljc
 * @since 2025-06-01
 */
@Mapper
public interface MovieMapper extends BaseMapper<Movie> {
    IPage<Movie> pageCC(IPage<Movie> page,@Param(Constants.WRAPPER) Wrapper<Movie> wrapper);
    /**
     * 获取正在热映的电影列表
     * @param currentDate 当前日期
     * @param limit 限制数量
     * @return 电影列表
     */
    List<Movie> selectNowShowingMovies(@Param("currentDate") Date currentDate,
                                       @Param("limit") Integer limit);

    /**
     * 获取即将上映的电影列表
     * @param currentDate 当前日期
     * @param limit 限制数量
     * @return 电影列表
     */
    List<Movie> selectComingSoonMovies(@Param("currentDate") Date currentDate,
                                       @Param("limit") Integer limit);
}

package ljc.movie_manage.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import ljc.movie_manage.entity.Cinema;
import com.baomidou.mybatisplus.extension.service.IService;
import ljc.movie_manage.entity.Movie;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ljc
 * @since 2025-06-17
 */
public interface ICinemaService extends IService<Cinema> {
    IPage<Cinema> pageCC(IPage<Cinema> page, Wrapper<Cinema> Wrapper);
}

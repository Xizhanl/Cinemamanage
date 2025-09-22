package ljc.movie_manage.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import ljc.movie_manage.entity.Movie;
import ljc.movie_manage.entity.Movietypes;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ljc
 * @since 2025-06-26
 */
public interface IMovietypesService extends IService<Movietypes> {
    IPage<Movietypes> pageCC(IPage<Movietypes> page, Wrapper<Movietypes> Wrapper);
}

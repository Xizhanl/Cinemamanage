package ljc.movie_manage.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import ljc.movie_manage.entity.Movie;
import ljc.movie_manage.entity.Movietypes;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ljc
 * @since 2025-06-26
 */
@Mapper
public interface MovietypesMapper extends BaseMapper<Movietypes> {
    IPage<Movietypes> pageCC(IPage<Movietypes> page, @Param(Constants.WRAPPER) Wrapper<Movietypes> wrapper);
}

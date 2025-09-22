package ljc.movie_manage.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;


import ljc.movie_manage.entity.Cinema;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ljc
 * @since 2025-06-17
 */
@Mapper
public interface CinemaMapper extends BaseMapper<Cinema> {
    IPage<Cinema> pageCC(IPage<Cinema> page, @Param(Constants.WRAPPER) Wrapper<Cinema> wrapper);
}

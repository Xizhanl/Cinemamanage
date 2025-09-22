package ljc.movie_manage.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import ljc.movie_manage.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
 * <p>
 * 用户信息表 Mapper 接口
 * </p>
 *
 * @author ljc
 * @since 2025-05-29
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    IPage<User> pageC(IPage<User> page);
    IPage<User> pageCC(IPage<User> page,@Param(Constants.WRAPPER) Wrapper<User> wrapper);
}

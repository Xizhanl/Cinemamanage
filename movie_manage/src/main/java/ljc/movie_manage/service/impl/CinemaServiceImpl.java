package ljc.movie_manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import ljc.movie_manage.entity.Cinema;
import ljc.movie_manage.mapper.CinemaMapper;
import ljc.movie_manage.service.ICinemaService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ljc
 * @since 2025-06-17
 */
@Service
public class CinemaServiceImpl extends ServiceImpl<CinemaMapper, Cinema> implements ICinemaService {
    @Resource
    private CinemaMapper CinemaMapper;
    @Override
    public IPage<Cinema> pageCC(IPage<Cinema> page, Wrapper<Cinema> wrapper) {
        return CinemaMapper.pageCC(page,wrapper);
    }
}

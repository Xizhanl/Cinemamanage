package ljc.movie_manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import ljc.movie_manage.entity.Movie;
import ljc.movie_manage.entity.Movietypes;
import ljc.movie_manage.mapper.MovieMapper;
import ljc.movie_manage.mapper.MovietypesMapper;
import ljc.movie_manage.service.IMovietypesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ljc
 * @since 2025-06-26
 */
@Service
public class MovietypesServiceImpl extends ServiceImpl<MovietypesMapper, Movietypes> implements IMovietypesService {
    @Resource
    private MovietypesMapper movietypesMapper;

    @Override
    public IPage<Movietypes> pageCC(IPage<Movietypes> page, Wrapper<Movietypes> wrapper) {
        return movietypesMapper.pageCC(page,wrapper);
}}

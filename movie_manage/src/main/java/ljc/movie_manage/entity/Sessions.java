package ljc.movie_manage.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * 场次信息表
 * </p>
 *
 * @author ljc
 * @since 2025-06-28
 */
@Getter
@Setter
@TableName("sessions")
public class Sessions implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 场次ID，主键自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 影厅名称
     */
    @TableField("hallname")
    private String hallname;

    /**
     * 电影名称
     */
    @TableField("moviename")
    private String moviename;

    /**
     * 场次日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate screeningtime;

    /**
     * 播放时长(分钟)
     */
    @TableField("duration")
    private String duration;

    @TableField("endtime")
    private String endtime;

    /**
     * 场次价格
     */
    @TableField("price")
    private Integer price;

    /**
     * 总座位数
     */
    @TableField("totalseats")
    private Integer totalseats;

    /**
     * 已售座位数
     */
    @TableField("seatssold")
    private Integer seatssold;

    /**
     * 剩余座位数(计算字段)
     */
    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    private Integer seatsremaining;

    /**
     * 提示信息(如"特惠场""首映礼"等)
     */
    @TableField("notice")
    private String notice;

    @TableField("seatstatus")
    private String seatstatus;
}

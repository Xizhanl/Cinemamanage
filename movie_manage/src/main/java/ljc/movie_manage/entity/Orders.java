package ljc.movie_manage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.sql.Time;
import java.time.LocalDate;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * 电影票订单表
 * </p>
 *
 * @author ljc
 * @since 2025-07-01
 */
@Getter
@Setter
@TableName("orders")
public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    @TableField("username")
    private String username;

    /**
     * 电影名称
     */
    @TableField("moviename")
    private String moviename;

    /**
     * 影厅名称
     */
    @TableField("hallname")
    private String hallname;

    /**
     * 场次日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate screeningdate;

    /**
     * 播放开始时间
     */
    @TableField("starttime")
    private String starttime;

    /**
     * 播放结束时间
     */
    @TableField("endtime")
    private String endtime;

    /**
     * 座位号(多个座位用逗号分隔)
     */
    @TableField("seatnumbers")
    private String seatnumbers;

    /**
     * 订单总价
     */
    @TableField("totalprice")
    private Integer totalprice;

    /**
     * 订票时间
     */
    @TableField("ordertime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date ordertime;

    /**
     * 订单状态
     */
    @TableField("orderstatus")
    private String orderstatus;
}

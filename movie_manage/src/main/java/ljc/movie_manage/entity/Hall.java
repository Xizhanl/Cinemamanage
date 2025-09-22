package ljc.movie_manage.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serial;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 影厅信息表
 * </p>
 *
 * @author ljc
 * @since 2025-06-28
 */
@Getter
@Setter
@TableName("hall")
public class Hall implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /*** 影厅ID，主键自增*/
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /*** 影厅名称*/
    @TableField("hallname")
    private String hallname;

    /*** 影厅类别["普通厅", "3D厅", "IMAX厅", "杜比全景声厅", "4DX厅"]*/
    @TableField("halltype")
    private String halltype;

    /*** 起始行号(通常是1)*/
    @TableField("startrow")
    private Integer startrow;

    /*** 行数*/
    @TableField("rowcount")
    private Integer rowcount;

    /*** 每行座位数*/
    @TableField("seatsrow")
    private Integer seatsrow;

    /*** 总座位数(行数×每行座位数)*/
    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    private Integer totalseats;

    /*** 状态(1-可用,0-维修中,2-停用)*/
    @TableField("statu")
    private Integer statu;

    //座位的状态，使用json存储每个座位的状态，0表示可用，2表示禁用(1表示售出，在场次表中统计)
    @TableField("seatstatus")
    private String seatstatus;
}

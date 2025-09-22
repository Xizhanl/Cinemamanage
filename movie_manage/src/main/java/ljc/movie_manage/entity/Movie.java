package ljc.movie_manage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * 
 * </p>
 *
 * @author ljc
 * @since 2025-06-01
 */
@Getter
@Setter
@TableName(autoResultMap = true)
public class Movie implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("moviename")
    private String moviename;

    @TableField("movieduration")
    private String movieduration;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> movietype;

    @TableField("durationday")
    private int durationday;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate movietime;

    @TableField("describes")
    private String describes;

    @TableField("moviecover")
    private String moviecover;

    @TableField("moviebox")
    private int moviebox;
}

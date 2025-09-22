package ljc.movie_manage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.List;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author ljc
 * @since 2025-06-17
 */
@Getter
@Setter
@TableName(autoResultMap = true)
public class Cinema implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("name")
    private String name;

    @TableField("address")
    private String address;

    @TableField("phone")
    private String phone;

    @TableField("opening_time")
    private String openingTime;

    @TableField("closing_time")
    private String closingTime;

    @TableField("image_url")
    private String imageUrl;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> hallTypes;
}

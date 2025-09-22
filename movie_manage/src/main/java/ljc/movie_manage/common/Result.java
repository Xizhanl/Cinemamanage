package ljc.movie_manage.common;

import lombok.Data;
//数据的封装
@Data
public class Result {
    private int code;//200成功,400失败
    private String msg;//信息
    private Object data;//数据
    private Long total;//总数
    public static Result fail(String errorMsg) {
        return result(400, errorMsg, null, 0L);
    }
    public static Result fail(){
        return result(400,"失败",null, 0L);
    }
    public static Result suc(){
        return result(200,"成功",null, 0L);
    }
    public static Result suc(Object data){
        return result(200,"成功",0L,data);
    }
    public static Result suc(Object data,Long total){
        return result(200,"成功",total,data);
    }
    private static Result result(int code, String msg, Long total,Object data) {
        Result res = new Result();
        res.setCode(code);
        res.setMsg(msg);
        res.setData(data);
        res.setTotal(total);
        return res;
    }

}
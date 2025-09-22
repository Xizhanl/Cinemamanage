package ljc.movie_manage.common;

import lombok.Data;

import java.util.HashMap;
//分页查询定义方法
@Data
public class QueryPageParam {
    private static int PAGE_SIZE=20;
    private static int PAGE_NUM=1;
    private int pageSize=PAGE_SIZE;
    private int pageNum=PAGE_NUM;
    private HashMap<String, Object> param =new HashMap<>();
    public int getPageNum() {return pageNum;}
    public void setPageNum(int pageNum) {this.pageNum = pageNum;}
    public int getPageSize() {return pageSize;}
    public void setPageSize(int pageSize) {this.pageSize = pageSize;}
    public HashMap<String, Object> getParam() {return param;}
    public void setParam(HashMap<String, Object> param) {this.param = param;}


}

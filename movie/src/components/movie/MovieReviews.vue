<script>

export default {
  data() {
    return {
      tableData:[],
      pageSize:10,
      pageNum:1,
      total:0,
      username:'',
      moviename:'',
      content:'',
      dialogVisible:false,
    }
  },
  methods:{
    loadGet(){
      this.$axios.get(this.$httpUrl+'/reviews/list')
          .then(res=>res.data)
          .then(res=>{console.log(res)
          })
    },
    loadPost() {
      this.$axios.post(this.$httpUrl + '/reviews/listPageC1', {
        pageSize:this.pageSize,
        pageNum:this.pageNum,
        param:{
          username:this.username,
          moviename:this.moviename
        }
      }).then(res => res.data).then(res => {
        console.log(res)//处理数据，只要data
        if(res.code===200){//封装数据
          this.tableData =res.data
          this.total=res.total
        }else{
          alert("获取数据失败")
        }
      })
    },
    resetParam(){
      this.username=''
      this.moviename=''
      this.loadPost()
    },
    del(id){//删除
      this.$axios.get(this.$httpUrl + '/reviews/del?id='+id).then(res => res.data).then(res => {
        console.log(res)//处理数据，只要data
        if(res.code==200){//封装数据
          this.$message.success("操作成功");
          this.loadPost();
        }else{
          this.$message.error("操作失败");
        }
      })
    },
    handleSizechange(val){//el方法
      console.log(`每页${val}条`)
      this.pageNum=1//永远是第一页
      this.pageSize=val//获取页数
      this.loadPost()
    },
    handleCurrentchange(val){
      console.log(`当前页:${val}`)
      this.pageNum=val
      this.loadPost()
    }
  },
  beforeMount() {
    //创建请求方法
    this.loadGet();
    this.loadPost();
  },
}
</script>

<template>
  <div>
    <div style="margin-bottom: 10px; font-size: 16px;">
      <el-input v-model="username" placeholder="请输入用户名" suffix-icon="el-icon-search" style="width: 200px;margin-right: 5px"
                size="small" @keyup.enter.native="loadPost"></el-input>
      <el-input v-model="moviename" placeholder="请输入电影名字" suffix-icon="el-icon-search" style="width: 200px ;margin-right: 5px"
                size="small" @keyup.enter.native="loadPost"></el-input>
      <el-button
          type="success"
          size="medium"
          style="margin-left: 8px"
          @click="loadPost">
        查询
      </el-button>

      <el-button
          type="primary"
          size="medium"
          style="margin-left: 8px"
          @click="resetParam">
        重置
      </el-button>

    </div>
    <el-table
        :data="tableData"
        style="width: 100%; font-size: 16px;"
        :header-cell-style="{background:'#f2f5fc',color:'#555', fontSize: '16px'}"
        :cell-style="{padding: '12px 0'}"
        border
        size="medium"
    >
      <el-table-column prop="id" label="影评ID" width="120"></el-table-column>
      <el-table-column prop="username" label="用户名" width="120"></el-table-column>
      <el-table-column prop="moviename" label="电影名称" width="120"></el-table-column>
      <el-table-column prop="content" label="内容" min-width="200"></el-table-column>
      <el-table-column label="操作" width="180" align="center">
        <template v-slot:default='scope'>
          <el-popconfirm
              title="确定删除吗？"
              style="margin-left: 5px"
              @confirm="del(scope.row.id)"
              :close-delay="200"
          >
            <el-button slot="reference"
                       size="small"
                       type="danger"
                       style="margin-left: 5px"
            >删除</el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <!--页码-->
    <el-pagination
        @size-change="handleSizechange"
        @current-change="handleCurrentchange"
        :current-page="pageNum"
        :page-size="pageSize"
        :page-sizes="[10,15]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
    </el-pagination>

  </div>
</template>

<style scoped>
</style>
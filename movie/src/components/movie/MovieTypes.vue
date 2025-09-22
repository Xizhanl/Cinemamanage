<script>

export default {
  data() {
    return {
      tableData:[],
      pageSize:10,
      pageNum:1,
      total:0,
      type:'',
      dialogVisible:false,
      form:{
        type:'',
      },
      rules:{
        type:[
          {required: true, message: '请输入电影；类型', trigger: 'blur' }]
      },
    }
  },
  methods:{
    resetForm(){
      this.form = {
        type: '',
      };
      // 重置表单验证状态
      if (this.$refs.form) {
        this.$nextTick(() => {
          this.$refs.form.clearValidate();
        });
      }
    },
    loadGet(){
      this.$axios.get(this.$httpUrl+'/movietypes/list')
          .then(res=>res.data)
          .then(res=>{console.log(res)
          })
    },
    loadPost() {
      this.$axios.post(this.$httpUrl + '/movietypes/listPageC1', {
        pageSize:this.pageSize,
        pageNum:this.pageNum,
        param:{
          type:this.type
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
      this.type=''
    },
    add(){//新增
      this.dialogVisible=true
      this.$nextTick(()=>{
        this.resetForm();
      })
    },
    doSave(){
      this.$axios.post(this.$httpUrl + '/movietypes/save',this.form).then(res => res.data).then(res => {
        console.log(res)//处理数据，只要data
        if(res.code===200){//封装数据
          this.$message.success("操作成功");
          this.dialogVisible = false;
          this.loadPost();
          this.resetForm();
        }else{
          this.$message.error("操作失败");
        }
      })
    },
    doMod(){
      this.$axios.post(this.$httpUrl + '/movietypes/mod',this.form).then(res => res.data).then(res => {
        console.log(res)//处理数据，只要data
        if(res.code==200){//封装数据
          this.$message.success("操作成功");
          this.dialogVisible = false;
          this.loadPost();
          this.resetForm();
        }else{
          this.$message.error("操作失败");
        }
      })
    },
    save(){//新增
      this.$refs.form.validate((valid) => {
        if (valid) {
          if(this.form.id){
            this.doMod();
          }else{
            this.doSave();
          }
        } else {
          console.log('error submit!!');
          return false;
        }
      });
    },
    mod(row){//编辑赋值
      console.log(row)
      this.dialogVisible=true
      this.$nextTick(()=>{
        this.form.id=row.id
        this.form.type=row.type
      })
    },
    del(id){//删除
      console.log(id)
      this.$axios.get(this.$httpUrl + '/movietypes/del?id='+id).then(res => res.data).then(res => {
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
      <el-input
          v-model="type"
          placeholder="请输入电影类型"
          suffix-icon="el-icon-search"
          style="width: 240px"
          size="medium"
      @keyup.enter.native="loadPost">
      </el-input>

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

      <el-button
          type="warning"
          size="medium"
      style="margin-left: 8px"
      @click="add">
      新增
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
      <el-table-column prop="id" label="类型ID" width="120"></el-table-column>
      <el-table-column prop="type" label="电影类型" min-width="200"></el-table-column>
      <el-table-column label="操作" width="180" align="center">
        <template v-slot:default='scope'>
          <el-button size="mini" type='success' @click="mod(scope.row)">编辑</el-button>
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
    <!--新增表单-->
    <el-dialog
        title="电影类型管理"
        :visible.sync="dialogVisible"
        width="30%"
        center>
      <el-form ref="form"
               :model="form"
               label-width="100px">

        <el-form-item label="电影类型">
          <el-col :span="30">
            <el-input v-model="form.type"></el-input>
          </el-col>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
    <el-button @click="dialogVisible = false">取 消</el-button>
    <el-button type="primary" @click="save">确 定</el-button>
  </span>
    </el-dialog>
  </div>
</template>

<style scoped>

</style>
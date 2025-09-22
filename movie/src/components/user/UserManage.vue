<script>

export default {
  data() {
    return {
      tableData:[],
      pageSize:5,
      pageNum:1,
      total:0,
      username:'',
      user:{},
      roles:[{value:"1",label:"管理员"},{value:"0",label:"普通用户"}],
      dialogVisible:false,
      form:{
        username:'',
        password:'',
        email:'',
        phone:'',
        role:'',
      },
      rules:{
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 8, message: '长度在 3到 8个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          {
            pattern: /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/,
            message: '密码至少8位，且包含字母和数字',
            trigger: 'blur'
          }
        ],
        email: [
          {required: true, message: '请输入邮箱', trigger: 'blur'},
          {
            type: 'email',
            message: '请输入正确的邮箱格式',
            trigger: ['blur', 'change']
          }
        ],
        phone: [
          { required: true, message: '请输入电话', trigger: 'blur' },
          {
            pattern: /^1[3-9]\d{9}$/, // 匹配中国大陆 11 位手机号
            message: '请输入正确的手机号码',
            trigger: 'blur'
          }
        ]
      }
    }
  },
  methods:{
    init(){
      this.user = JSON.parse(sessionStorage.getItem('CurUser'))
    },
    resetForm(){
      this.form = {
        username: '',
        password: '',
        email: '',
        phone: '',
        role: ''
      };
      // 重置表单验证状态
      if (this.$refs.form) {
        this.$nextTick(() => {
          this.$refs.form.clearValidate();
        });
      }
    },
    loadGet(){
      this.$axios.get(this.$httpUrl+'/user/list')
          .then(res=>res.data)
          .then(res=>{console.log(res)
          })
    },
    loadPost() {
      this.$axios.post(this.$httpUrl + '/user/listPageC1', {
        pageSize:this.pageSize,
        pageNum:this.pageNum,
        param:{
          username:this.username
        }
      }).then(res => res.data).then(res => {
        console.log(res)//处理数据，只要data
        if(res.code==200){//封装数据
          this.tableData =res.data
          this.total=res.total
        }else{
          alert("获取数据失败")
        }
      })
    },
    resetParam(){
      this.username=''
    },
    add(){//新增
      this.dialogVisible=true
      this.$nextTick(()=>{
        this.resetForm();
      })
    },
    doSave(){
      this.$axios.post(this.$httpUrl + '/user/save',this.form).then(res => res.data).then(res => {
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
    doMod(){
      this.$axios.post(this.$httpUrl + '/user/mod',this.form).then(res => res.data).then(res => {
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
      if (this.user.role === 1 && row.role === 1 && row.id !== this.user.id) {
        this.$message.error("您不能编辑其他管理员！");
        return;
      }
      this.dialogVisible=true
      this.$nextTick(()=>{
        this.form.id=row.id
        this.form.username=row.username
        this.form.password=row.password
        this.form.phone=row.phone
        this.form.email=row.email
        this.form.role=row.role
      })
    },
    del(id){//删除
      if (this.user.role === 1 && this.id !== this.user.id) {
        this.$message.error("您不能删除其他管理员！");
        return;
      }
      this.$axios.get(this.$httpUrl + '/user/del?id='+id).then(res => res.data).then(res => {
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
    this.init()
  },
}
</script>

<template>
  <div>
    <div style="margin-bottom: 5px" >
      <el-input v-model="username" placeholder="请输入用户名" suffix-icon="el-icon-search" style="width: 200px"
                size="small" @keyup.enter.native="loadPost"></el-input>
      <el-button type="success" size="small" style="margin-left: 5px" @click="loadPost">查询</el-button>
      <el-button type="primary" size="small" @click="resetParam">重置</el-button>
      <el-button type="warning" size="small" @click="add">新增</el-button>
    </div>
    <el-table :data="tableData"
              :header-cell-style="{background:'#f2f5fc',color:'#555'}"
              border
    >
      <el-table-column prop="id" label="用户ID" >
      </el-table-column>
      <el-table-column prop="username" label="用户名" >
      </el-table-column>
      <el-table-column prop="password" label="密码" >
      </el-table-column>
      <el-table-column prop="email" label="邮箱" >
      </el-table-column>
      <el-table-column prop="phone" label="电话" >
      </el-table-column>
      <el-table-column prop="role" label="角色" >
        <template slot-scope="scope">
          <el-tag
              :type="scope.row.role === 1 ? 'danger' : 'primary'"
              disable-transitions>{{scope.row.role===1?'管理员':'普通用户'}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="operate" label="操作">
        <template v-slot:default='scope'>
          <el-button
              size="small"
              type="success"
              @click="mod(scope.row)">编辑</el-button>
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
        :page-sizes="[5, 10]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
    </el-pagination>
    <!--新增表单-->
    <el-dialog
        title="用户信息"
        :visible.sync="dialogVisible"
        width="30%"
        center>
      <el-form ref="form" :rules="rules" :model="form" label-width="80px">

        <el-form-item label="用户名" prop="username">
          <el-col :span="25">
            <el-input v-model="form.username"></el-input>
          </el-col>
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-col :span="25">
            <el-input v-model="form.password"></el-input>
          </el-col>
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-col :span="25">
            <el-input v-model="form.email" type="email"></el-input>
          </el-col>
        </el-form-item>

        <el-form-item label="电话" prop="phone">
          <el-col :span="25">
            <el-input v-model.number="form.phone"></el-input>
          </el-col>
        </el-form-item>

        <el-form-item label="角色" prop="role">
          <el-col :span="25">
            <el-select v-model="form.role" placeholder="请选择">
              <el-option
                  v-for="item in roles"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
              </el-option>
            </el-select>
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
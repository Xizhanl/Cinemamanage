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
      hallname:'',
      orderstatus:'',
      orderstatu:["已取消","已完成","待支付","待取票"],
      dialogVisible:false,
      form:{
        username:'',
        moviename:'',
        hallname:'',
        screeningdate:'',
        starttime:'',
        endtime:'',
        seatnumbers:'',
        totalprice:'',
        ordertime:'',
        orderstatus:'',
      },
      rules:{
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },

        ],
      }
    }
  },
  methods:{
    resetForm(){
      this.form = {
        username:'',
        moviename:'',
        hallname:'',
        screeningdate:'',
        starttime:'',
        endtime:'',
        seatnumbers:'',
        totalprice:'',
        ordertime:'',
        orderstatus:'',
      };
      // 重置表单验证状态
      if (this.$refs.form) {
        this.$nextTick(() => {
          this.$refs.form.clearValidate();
        });
      }
    },
    loadGet(){
      this.$axios.get(this.$httpUrl+'/orders/list')
          .then(res=>res.data)
          .then(res=>{console.log(res)
          })
    },
    loadPost() {
      this.$axios.post(this.$httpUrl + '/orders/listPage', {
        pageSize:this.pageSize,
        pageNum:this.pageNum,
        param:{
          username:this.username,
          moviename:this.moviename,
          hallname:this.hallname,
          orderstatus:this.orderstatus,
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
      this.moviename=''
      this.hallname=''
      this.orderstatus=''
      this.loadPost()
    },
    doMod(){
      this.$axios.post(this.$httpUrl + '/orders/mod',this.form).then(res => res.data).then(res => {
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
    mod(row) {
      if (row.orderstatus=="已完成"){
        this.$message.warning("不能编辑已完成订单")
      }else {
        this.dialogVisible = true;
        this.$nextTick(() => {
          this.form = {
            id: row.id,
            username: row.username,
            moviename: row.moviename,
            hallname: row.hallname,
            screeningdate: row.screeningdate,
            starttime: row.starttime,
            endtime: row.endtime,
            seatnumbers: row.seatnumbers,
            totalprice: row.totalprice,
            ordertime: row.ordertime,
            orderstatus: row.orderstatus
          };
        });
      }
    },
    del(id){//删除
      console.log(id)
      this.$axios.get(this.$httpUrl + '/orders/del?id='+id).then(res => res.data).then(res => {
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
      this.pageNum=1//永远是第一页
      this.pageSize=val//获取页数
      this.loadPost()
    },
    handleCurrentchange(val){
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
    <div style="margin-bottom: 5px" >
      <el-input v-model="username" placeholder="请输入用户名" suffix-icon="el-icon-search" style="width: 200px;margin-right: 5px"
                size="small" @keyup.enter.native="loadPost"></el-input>
      <el-input v-model="moviename" placeholder="请输入电影名字" suffix-icon="el-icon-search" style="width: 200px ;margin-right: 5px"
                size="small" @keyup.enter.native="loadPost"></el-input>
      <el-input v-model="hallname" placeholder="请输入影厅名字" suffix-icon="el-icon-search" style="width: 200px;margin-right: 5px"
                size="small" @keyup.enter.native="loadPost"></el-input>
      <el-select
          v-model="orderstatus"
          filterable
          clearable
          placeholder="请选择订单状态"
          style="width: 200px; margin-right: 5px"
          size="small">
        <el-option
            v-for="type in orderstatu"
            :key="type"
            :label="type"
            :value="type">
        </el-option>
      </el-select>
      <el-button type="success" size="small" style="margin-left: 5px" @click="loadPost">查询</el-button>
      <el-button type="primary" size="small" @click="resetParam">重置</el-button>
    </div>
    <el-table :data="tableData"
              :header-cell-style="{background:'#f2f5fc',color:'#555'}"
              border
    >
      <el-table-column prop="id" label="订单ID" width="70px">
      </el-table-column>
      <el-table-column prop="username" label="用户名" >
      </el-table-column>
      <el-table-column prop="moviename" label="电影名称" >
      </el-table-column>
      <el-table-column prop="hallname" label="影厅名称" >
      </el-table-column>
      <el-table-column prop="screeningdate" label="场次日期" width="100px">
      </el-table-column>
      <el-table-column prop="starttime" label="开始时间" width="100px">
      </el-table-column>
      <el-table-column prop="endtime" label="结束时间" width="100px">
      </el-table-column>
      <el-table-column prop="seatnumbers" label="座位号" >
      </el-table-column>
      <el-table-column prop="totalprice" label="订单总价" width="80px">
      </el-table-column>
      <el-table-column prop="ordertime" label="订票时间" >
      </el-table-column>
      <el-table-column prop="orderstatus" label="订单状态" width="100px">
        <template slot-scope="scope">
          <el-tag
              :type="scope.row.orderstatus === '已完成' ? 'success' : (scope.row.orderstatus === '已取消' ? 'danger' : 'warning')"
              effect="plain"
              size="small">
            {{ scope.row.orderstatus }}
          </el-tag>
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
        :page-sizes="[10, 15]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
    </el-pagination>
    <el-dialog
        title="订单信息"
        :visible.sync="dialogVisible"
        width="30%"
        center>
      <el-form ref="form" :rules="rules" :model="form" label-width="80px">

        <el-form-item label="用户名" prop="username">
          <el-col :span="25">
            <el-input v-model="form.username" :disabled="true"></el-input>
          </el-col>
        </el-form-item>

        <el-form-item label="电影名称" prop="moviename">
          <el-col :span="25">
            <el-input v-model="form.moviename" :disabled="true"></el-input>
          </el-col>
        </el-form-item>

        <el-form-item label="影厅名称" prop="hallname">
          <el-col :span="25">
            <el-input v-model="form.hallname" :disabled="true"></el-input>
          </el-col>
        </el-form-item>

        <el-form-item label="场次日期" prop="screeningdate" >
          <el-col :span="25">
            <el-input v-model="form.screeningdate" :disabled="true"></el-input>
          </el-col>
        </el-form-item>

        <el-form-item label="播放时间" prop="starttime" :disabled="true">
          <el-col :span="25">
            <el-input v-model="form.starttime" :disabled="true"></el-input>
          </el-col>
        </el-form-item>
        <el-form-item label="座位号" prop="seatnumbers" :disabled="true">
          <el-col :span="25">
            <el-input v-model="form.seatnumbers" :disabled="true"></el-input>
          </el-col>
        </el-form-item>
        <el-form-item label="订单总价" prop="totalprice">
          <el-col :span="25">
            <el-input v-model="form.totalprice" :disabled="true"></el-input>
          </el-col>
        </el-form-item>
        <el-form-item label="订票时间" prop="ordertime">
          <el-col :span="25">
            <el-input v-model="form.ordertime" :disabled="true"></el-input>
          </el-col>
        </el-form-item>
        <el-form-item label="订单状态" prop="orderstatus">
          <el-col :span="25">
            <el-select v-model="form.orderstatus" placeholder="请选择">
              <el-option
                  v-for="item in orderstatu"
                  :key="item"
                  :label="item"
                  :value="item">
              </el-option>
            </el-select>
          </el-col>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
    <el-button @click="dialogVisible = false">取 消</el-button>
    <el-button type="primary" @click="doMod">确 定</el-button>
  </span>
    </el-dialog>
  </div>
</template>

<style scoped>

</style>
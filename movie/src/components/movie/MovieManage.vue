<script>
export default {
  data() {
    return {
      tableData:[],
      pageSize:10,
      pageNum:1,
      total:0,
      moviename:'',
      movietype:[],
      dialogVisible:false,
      form:{
        moviename:'',
        movieduration:'',
        movietype:[],
        durationday:'',
        movietime:'',
        describes:'',
        moviecover:'',
        moviebox:''
      },
      rules:{
        moviename:[
          {required: true, message: '请输入电影名称', trigger: 'blur' }],
        movietype:[
          {required: true,type:'array' ,message: '请至少选择一个电影类型', trigger: 'change'}],
        movieduration:[
          {required: true, message: '请输入电影时长', trigger: 'blur' },
          { type: 'number', message: '必须输入数字', trigger: 'blur' },],
        movietime:[
          {required: true, message: '请选择电影上映时间', trigger: 'blur' }],
        durationday:[
          { type: 'number', message: '必须输入数字', trigger: 'blur' },
        ],
        moviebox:[
          { type: 'number', message: '必须输入数字', trigger: 'blur' },
        ]
      },
      movietypes:[],
    }
  },
  methods:{
    resetForm(){
      this.form = {
        id: null,
        moviename: '',
        movieduration: '',
        durationday: '',
        movietime: '',
        movietype: [],
        describes: '',
        moviebox: '',
      };
      // 重置表单验证状态
      if (this.$refs.form) {
        this.$nextTick(() => {
          this.$refs.form.clearValidate();
        });
      }
    },
    loadGet(){
      this.$axios.get(this.$httpUrl+'/movie/list')
          .then(res=>res.data)
          .then(res=>{console.log(res)
          })
    },
    loadPost() {
      this.$axios.post(this.$httpUrl + '/movie/listPageC1', {
        pageSize:this.pageSize,
        pageNum:this.pageNum,
        param:{
          moviename:this.moviename,
          movietype:this.movietype
        }
      }).then(res => res.data).then(res => {
        if (res.code === 200) {
          this.tableData = res.data.map(item => ({
            ...item,
            movietype: Array.isArray(item.movietype)
                ? item.movietype
                : typeof item.movietype === 'string'
                    ? JSON.parse(item.movietype) || []
                    : []
          }));
          this.total=res.total
        }else{
          alert("获取数据失败")
        }
      })
    },
    resetParam(){
      this.moviename=''
      this.movietype=[]
      this.loadPost() //查询所有数据
    },
    add(){//新增
      this.dialogVisible=true
      this.$nextTick(()=>{
        this.resetForm();
      })
    },
    doSave() {
      const postData = {
        ...this.form
      };
      this.$axios.post(this.$httpUrl + '/movie/save',postData)
          .then(res => res.data)
          .then(res => {
            if(res.code===200) {
              this.$message.success("操作成功");
              this.dialogVisible = false;
              this.loadPost();
              this.resetForm();
            } else {
              this.$message.error("操作失败");
            }
          });
    },
    doMod(){
      this.$axios.post(this.$httpUrl + '/movie/mod',this.form).then(res => res.data).then(res => {
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
    mod(row) {
      this.dialogVisible = true;
      this.$nextTick(() => {
        this.form = {
          id: row.id,
          moviename: row.moviename,
          movieduration: Number(row.movieduration),
          durationday: row.durationday,
          movietime: row.movietime,
          movietype: Array.isArray(row.movietype) ? row.movietype : [row.movietype],
          describes: row.describes,
          moviebox: Number(row.moviebox),
          moviecover: row.moviecover
        };
      });
    },
    del(id){//删除
      console.log(id)
      this.$axios.get(this.$httpUrl + '/movie/del?id='+id).then(res => res.data).then(res => {
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
    },
    fetchMovieTypes() {
      this.$axios.get(this.$httpUrl + '/movietypes/list')
          .then(res => {
            if (res.data && Array.isArray(res.data)) {
              // 直接存储类型名称数组
              this.movietypes = res.data.map(item => item.type);
            }
          })
          .catch(error => {
        console.error("获取电影类型失败:", error);
      });
    },
    getTypeName(id) {
      if (id === null || id === undefined) return '';
      const numId = Number(id);
      const type = this.movietypes.find(item => item.id === numId);
      return type ? type.type : id;
    },
    handleAvatarSuccess(res, file) {
      this.form.moviecover=res.data.url
      console.log(res.data.url)
      console.log(file)
      this.imageUrl = URL.createObjectURL(file.raw);
    },
    beforeAvatarUpload(file) {
      const typeArr = ["image/jpeg","image/png","image/jpg"]
      if(!typeArr.includes(file.type)){
        this.$message.error("上传图片只能是JPG、PNG、JPEG格式!")
        return false
      }
      const isLt2M = file.size / 1024 / 1024 < 2;
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 2MB!');
      }
      return isLt2M
    },
  },
  beforeMount() {
    //创建请求方法
    this.loadGet();
    this.loadPost();
    this.fetchMovieTypes()
  },
}
</script>
<template>
  <div>
    <div style="margin-bottom: 5px" >
      <el-input v-model="moviename" placeholder="请输入电影名" suffix-icon="el-icon-search" style="width: 200px;margin-right:5px"
                size="small" @keyup.enter.native="loadPost"></el-input>
      <!-- 电影类型多选框 -->
      <el-select
          v-model="movietype"
          multiple
          filterable
          clearable
          placeholder="请选择电影类型"
          style="width: 200px; margin-right: 5px"
          size="small">
        <el-option
            v-for="type in movietypes"
            :key="type"
            :label="type"
            :value="type">
        </el-option>
      </el-select>
      <el-button type="success" size="small" style="margin-left: 5px" @click="loadPost">查询</el-button>
      <el-button type="primary" size="small" @click="resetParam">重置</el-button>
      <el-button type="warning" size="small" @click="add">新增</el-button>
    </div>
    <el-table :data="tableData"
              :header-cell-style="{background:'#f2f5fc',color:'#555'}"
              border
    >
      <el-table-column prop="id" label="电影ID" width="80">
      </el-table-column>
      <el-table-column prop="moviename" label="电影名称" width="160">
      </el-table-column>
      <el-table-column prop="movieduration" label="电影时长/分钟" width="110">
      </el-table-column>
      <el-table-column prop="movietype" label="电影类型" width="200">
        <template v-slot:default='scope'>
          <div v-if="scope.row.movietype && scope.row.movietype.length" class="tag-container">
            <el-tag
                v-for="(type, index) in scope.row.movietype"
                :key="index"
                :type="['','success','info','warning','danger'][index % 5]"
                size="small"
                style="margin: 2px 3px;"
            >
              {{ type }}
            </el-tag>
          </div>
          <span v-else style="color: #c0c4cc; font-size: 12px;">-</span>
        </template>
      </el-table-column>

      <el-table-column prop="movietime" label="电影上映时间" width="120">
      </el-table-column>
      <el-table-column prop="durationday" label="电影持续时间/天" width="120">
      </el-table-column>
      <el-table-column prop="describes" label="电影描述" width="260">
      </el-table-column>
      <el-table-column prop="moviecover" label="电影封面" width="100">
        <template v-slot:default='scope'>
          <el-image
              v-if="scope.row.moviecover"
              style="width: 80px; height: 100px;"
              :src="scope.row.moviecover"
              :preview-src-list="[scope.row.moviecover]"
              fit="cover">
            <div slot="error" class="image-slot">
              <i class="el-icon-picture-outline"></i>
            </div>
          </el-image>
          <div v-else style="width:100px;height:140px;line-height:140px;text-align:center;background:#f5f7fa;">
            <i class="el-icon-picture-outline" style="font-size:20px;color:#909399;"></i>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="moviebox" label="电影票房" width="100">
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
        :page-sizes="[10,15]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
    </el-pagination>
    <!--新增表单-->
    <el-dialog
        title="电影信息"
        :visible.sync="dialogVisible"
        width="50%"
        center
        class="movie-form-dialog">
      <el-form
          ref="form"
          :model="form"
          label-width="100px"
          :rules="rules"
          label-position="left"
          class="movie-form">

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="电影名称" prop="moviename">
              <el-input
                  v-model="form.moviename"
                  placeholder="请输入电影名称"
                  clearable>
              </el-input>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="电影类型" prop="movietype">
              <el-select
                  v-model="form.movietype"
                  multiple
                  placeholder="请选择电影类型"
                  clearable
                  style="width:100%">
                <el-option
                    v-for="(type, index) in movietypes"
                    :key="index"
                    :label="type"
                    :value="type"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="电影时长" prop="movieduration">
              <el-input
                  v-model.number="form.movieduration"
                  placeholder="例如：120"
                  clearable>
              </el-input>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="上映时间" prop="movietime">
              <el-date-picker
                  v-model="form.movietime"
                  type="date"
                  value-format="yyyy-MM-dd"
                  placeholder="选择日期"
                  style="width:100%">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24">
            <el-form-item label="电影票房" prop="moviebox">
              <el-input
                  v-model.number="form.moviebox"
                  placeholder="例如：156"
                  clearable>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24">
            <el-form-item label="电影持续时间" prop="durationday">
              <el-input
                  v-model.number="form.durationday"
                  :rows="4"
                  placeholder="请输入电影上映持续时间"
                  resize="none"
                  style="width:100%"
                  class="el-textarea-consistent">
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="电影描述" prop="describes">
              <el-input
                  v-model="form.describes"
                  type="textarea"
                  :rows="4"
                  placeholder="请输入电影简介"
                  resize="none"
                  style="width:100%"
                  class="el-textarea-consistent">
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="电影封面" prop="moviecover">
          <el-upload
              class="avatar-uploader"
              action="http://localhost:9999/upload"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload">
            <img v-if="form.moviecover" :src="form.moviecover" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
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
.movie-form-dialog {
  border-radius: 8px;
}

.movie-form {
  padding: 0 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  padding: 10px 20px 0;
}
.el-textarea-consistent {
  font-family: "Helvetica Neue",Helvetica,"PingFang SC","Hiragino Sans GB","Microsoft YaHei","微软雅黑",Arial,sans-serif;
  font-size: 14px;
  font-weight: 400;
  line-height: 1.5;
  letter-spacing: normal;
}
/* 上传容器 */
.avatar-uploader {
  width: 178px;
  height: 178px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
}

.avatar-uploader:hover {
  border-color: #409EFF;
}
/* 封面图片 */
.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

/* 表格中的图片样式 */
.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
}
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}
.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
.tag-container {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}

</style>
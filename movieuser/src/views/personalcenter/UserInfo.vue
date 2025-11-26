<script>
import axios from "axios";

export default {
  data() {
    return {
      userInfo: {
        id:'',
        username: '',
        password:'',
        email: '',
        phone: '',
      },
      form: {
        id:'',
        username: '',
        password:'',
        email: '',
        phone: '',
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
      },
      dialogVisible:false,
      likeList:[]
    }
  },
  methods: {
      save() {
        this.$refs.form.validate((valid) => {
          if (valid) {
            this.$axios.post(this.$httpUrl + '/user/mod', this.form)
                .then(res => res.data)
                .then(res => {
                  if(res.code === 200) {
                    // 1. 更新sessionStorage
                    const curUser = JSON.parse(sessionStorage.getItem("CurUser"));
                    const updatedUser = {
                      ...curUser,
                      username: this.form.username,
                      password: this.form.password,
                      email: this.form.email,
                      phone: this.form.phone
                    };
                    sessionStorage.setItem("CurUser", JSON.stringify(updatedUser));
                    // 2. 更新本地数据
                    this.userInfo = { ...this.form };
                    // 3. 关闭对话框并提示
                    this.$message.success("修改成功");
                    this.dialogVisible = false;
                  } else {
                    this.$message.error( "操作失败");
                  }
                })
                .catch(err => {
                  console.error(err);
                  this.$message.error("请求失败");
                });
          } else {
            this.$message.warning('请填写正确的表单信息');
          }
        });
      },
    mod(){//编辑赋值
      this.dialogVisible=true
      this.$nextTick(()=>{
        this.form.id=this.userInfo.id
        this.form.username=this.userInfo.username
        this.form.password=this.userInfo.password
        this.form.phone=this.userInfo.phone
        this.form.email=this.userInfo.email
      })
    },
    go(){
      this.$router.push("/BuyTicket")
    },
    gomoviedetail(like){
      if(like.moviename)
        this.getmovieidbyname(like.moviename).then(movieid=>{
          this.$router.push({
            path:"/MovieInfo",
            query:{id:movieid}})
        })
    },
    async getmovieidbyname(moviename){
        const res = await axios.get(this.$httpUrl + "/movie/list");
        const movie = res.data.find(movie => movie.moviename === moviename);
        return movie.id;
    },
    initializeUser() {
      // 从sessionStorage获取当前用户信息
      const curUser = JSON.parse(sessionStorage.getItem("CurUser"));
      // 将获取到的用户信息赋值给data中的userInfo对象
      this.userInfo = {
        id:curUser.id,
        username: curUser.username,
        email: curUser.email,
        phone: curUser.phone,
        password: curUser.password,
      };
    },
    // 获取用户收藏列表
    async getLikeList() {
      const user =  JSON.parse(sessionStorage.getItem('CurUser'));
      const username = user.username
      const likelist = await axios.get(this.$httpUrl + "/userlike/list");
      this.likeList = likelist.data.filter(like => like.username === username);
    }
  },
  created() {
    this.initializeUser();
    this.getLikeList();
  }
}
</script>
<template>
  <div class="main">
    <div class="header">
      <h1 class="el-icon-s-order" style="font-size: 30px;margin-top: 20px">个人信息</h1>
      <div class="divider"></div>
    </div>
    <div class="content">
      <el-card class="user-card">
          <!-- 用户基本信息 -->
        <div class="user-details">
          <div class="detail-item">
            <span class="label">用户名：</span>
            <span class="value">{{ userInfo.username }}</span>
          </div>
          <div class="detail-item">
            <span class="label">密码：</span>
            <span class="value">{{ userInfo.password }}</span>
          </div>
          <div class="detail-item">
            <span class="label">邮箱：</span>
            <span class="value">{{ userInfo.email }}</span>
          </div>
          <div class="detail-item">
            <span class="label">手机号：</span>
            <span class="value">{{ userInfo.phone }}</span>
          </div>
        </div>
        <el-button class="button" @click="mod">修改信息</el-button>
        <el-button class="button2" @click="go">立即购票</el-button>
      </el-card>
    </div>

    <!-- 收藏列表卡片 -->
    <el-card class="like-card">
      <div slot="header">
        <span style="font-size: 18px; font-weight: bold;color: #f56c6c">
          <i class="el-icon-star-on"></i> 我的收藏
        </span>
      </div>

      <!-- 空状态 -->
      <div v-if="likeList.length === 0" class="empty-state">
        <i class="el-icon-star-off"></i>
        <div>暂无收藏的影片</div>
      </div>

      <!-- 收藏列表 - 水平滚动布局 -->
      <div v-else class="movie-scroll-container">
        <div class="movie-scroll-wrapper">
          <div
              v-for="like in likeList"
              :key="like.id"
              class="movie-item">
            <div class="poster-container">
              <img
                  :src="like.moviecover"
                  :alt="like.moviename"
                  class="movie-poster"
                  @click="gomoviedetail(like)">
            </div>
            <div class="movie-name">{{ like.moviename }}</div>
          </div>
        </div>
      </div>
    </el-card>


    <el-dialog :visible.sync="dialogVisible" width="30%" title="用户信息">
      <el-form ref="form"
               :rules="rules"
               :model="form"
               label-width="80px">

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

      </el-form>
      <span slot="footer" class="dialog-footer" >
    <el-button @click="dialogVisible = false">取 消</el-button>
    <el-button type="primary" @click="save">确 定</el-button>
    </span>
    </el-dialog>
  </div>
</template>
<style scoped>
/* 1. 页面背景：极淡的蓝-白渐变 */
.main {
  min-height: 100vh;
  background: linear-gradient(160deg, #f5f9ff 0%, #ffffff 100%);
  padding: 20px;
}

/* 2. 头部 */
.header {
  color: #409eff;
  text-align: center;
}
.divider {
  height: 1px;
  background: linear-gradient(90deg, transparent, #d0e4ff, transparent);
  margin: 20px auto 0;
  width: 85%;
}

/* 3. 卡片：圆角+淡阴影 */
.user-card {
  max-width: 640px;
  margin: 24px auto;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  background: #ffffff;
  padding: 30px 40px;
}

/* 4. 字段 */
.detail-item {
  display: flex;
  align-items: baseline;
  margin-bottom: 18px;
  font-size: 15px;
}
.label {
  color: #8cc5ff;
  font-weight: 600;
  width: 70px;
  flex-shrink: 0;
}
.value {
  color: #303133;
  margin-left: 12px;
}

/* 5. 按钮 */
.button {
  margin-top: 24px;
  width: 120px;
  border-radius: 20px;
  background: #409eff;
  color: #fff;
  border: none;
  transition: all 0.25s;
}
.button:hover {
  background: #66b1ff;
  transform: translateY(-2px);
}

.button2 {
  margin-top: 24px;
  width: 120px;
  border-radius: 20px;
  background: #ebb563;
  color: #fff;
  border: none;
  transition: all 0.25s;
}
.button2:hover {
  background: #ebb563;
  transform: translateY(-2px);
}

/* 6. 弹窗 */
.el-dialog__wrapper /deep/ .el-dialog {
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}
.dialog-footer {
  display: flex;
  justify-content: center;
  padding-top: 10px;
}
/* 7. 收藏列表样式 - 水平滚动布局 */
.empty-state {
  text-align: center;
  padding: 60px 0;
  color: #999;
}

.empty-state .el-icon-star-off {
  font-size: 48px;
  margin-bottom: 16px;
  display: block;
}

/* 水平滚动容器 */
.movie-scroll-container {
  width: 100%;
  overflow-x: auto;
  padding: 10px 0 20px 0;
}

.movie-scroll-wrapper {
  display: flex;
  gap: 20px;
  padding: 0 10px;
  min-width: min-content; /* 确保内容不会被压缩 */
}

/* 隐藏滚动条但保持滚动功能 */
.movie-scroll-container::-webkit-scrollbar {
  height: 6px;
}

.movie-scroll-container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.movie-scroll-container::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.movie-scroll-container::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 电影项样式 */
.movie-item {
  flex: 0 0 auto; /* 不伸缩，不收缩 */
  width: 150px; /* 固定宽度 */
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: center;
}

.movie-item:hover {
  transform: translateY(-5px);
}

.movie-item:hover .movie-poster {
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.poster-container {
  margin-bottom: 12px;
}

.movie-poster {
  width: 100%;
  height: 200px; /* 固定高度，保持统一 */
  object-fit: cover; /* 保持图片比例，裁剪多余部分 */
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.movie-name {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  line-height: 1.4;
  padding: 0 4px;
  word-break: break-all;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 40px; /* 固定高度，保持对齐 */
}

/* 响应式调整 */
@media (max-width: 768px) {
  .movie-item {
    width: 130px;
  }

  .movie-poster {
    height: 180px;
  }

  .movie-scroll-wrapper {
    gap: 15px;
  }
}

@media (max-width: 480px) {
  .movie-item {
    width: 110px;
  }

  .movie-poster {
    height: 150px;
  }

  .movie-name {
    font-size: 12px;
    min-height: 36px;
  }

  .movie-scroll-wrapper {
    gap: 12px;
  }
}
</style>
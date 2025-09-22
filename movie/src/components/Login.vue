<script>
export default {
  name: "Login",
  data() {
    return {
      confirm_disabled: false,
      loginForm: {
        username: '',
        password: '',
        role: 1
      },
      roles:[{value:"1",label:"管理员"},{value:"0",label:"普通用户"}],
      rules: {
        username: [
          {required: true, message: '请输入账号', trigger: 'blur'},
        ],
        password: [
          {required: true, message: '请输入密码', trigger: 'blur'},
        ],
        role:[
          {require:true,message:"请选择角色",trigger:'change'}
        ]
      }
    }
  },
  methods: {
    confirm() {
      this.confirm_disabled=true;
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
          this.$axios.post(this.$httpUrl +'/user/login',this.loginForm).then(res=>res.data).then(res=>{
            if (res.code==200) {
              sessionStorage.setItem("CurUser",JSON.stringify(res.data))
              this.$router.replace('/Index').then(() => {
                // 如果需要默认打开Home，可以在这里处理
              });
            } else {
              this.confirm_disabled = false;
              this.$message.error('用户名或密码错误!');
              return false;
            }
          });
        } else {
          this.confirm_disabled = false;
          console.log('校验失败')
          return false;
        }
      });
    }
  }
}
</script>

<template>
  <div class="login-container">
    <div class="login-background"></div>

    <div class="login-card">
      <div class="login-header">
        <h2>影院后台管理系统</h2>
        <p>请输入您的账号密码</p>
      </div>

      <el-form
          :model="loginForm"
          :rules="rules"
          ref="loginForm"
          class="login-form"
      >
        <el-form-item prop="username">
          <el-input
              v-model="loginForm.username"
              placeholder="用户名"
              prefix-icon="el-icon-user"
              clearable
          ></el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input
              v-model="loginForm.password"
              placeholder="密码"
              prefix-icon="el-icon-lock"
              show-password
          ></el-input>
        </el-form-item>

        <el-form-item>
          <el-button
              type="primary"
              @click="confirm"
              :disabled="confirm_disabled"
              class="login-btn"
          >
            {{ confirm_disabled ? '登录中...' : '登 录' }}
          </el-button>
        </el-form-item>
      </el-form>

    </div>
  </div>
</template>

<style scoped>
.login-container {
  position: relative;
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
}

.login-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: url('D:/java/moviemanage/images/movielogin.png') no-repeat center center;
  background-size: cover;
  filter: blur(3px);
  z-index: 1;
}

.login-card {
  position: relative;
  width: 400px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 10px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
  z-index: 2;
  animation: fadeInUp 0.5s;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h2 {
  color: #303133;
  font-size: 24px;
  margin-bottom: 10px;
}

.login-header p {
  color: #909399;
  font-size: 14px;
}

.login-form {
  margin-top: 20px;
}

.login-btn {
  width: 100%;
  height: 45px;
  font-size: 16px;
  letter-spacing: 2px;
  margin-top: 10px;
}


/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-card {
    width: 90%;
    padding: 30px 20px;
  }

  .login-header h2 {
    font-size: 20px;
  }
}
</style>
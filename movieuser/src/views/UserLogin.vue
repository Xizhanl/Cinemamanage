<script>
export default {
  name: "UserLogin",
  data() {
    return {
      confirm_disabled: false,
      loginForm: {
        username: '',
        password: '',
        role: 0
      },
      captchaKey: '', // 验证码key
      captchaText: '', // 用户输入的验证码
      rules: {
        username: [
          {required: true, message: '请输入账号', trigger: 'blur'},
        ],
        password: [
          {required: true, message: '请输入密码', trigger: 'blur'},
        ],
        captchaText: [
          {required: true, message: '请输入验证码', trigger: 'blur'},
          {min: 4, max: 4, message: '验证码长度为4个字符', trigger: 'blur'}
        ]
      }
    }
  },
  mounted() {
    this.refreshCaptcha();
  },
  methods: {
    toregister(){
      this.$router.push('/Register')
    },
    refreshCaptcha(){
      this.captchaKey=''
      this.$axios.get(this.$httpUrl+'/captcha/image',{responseType:'blob'}).then(response=>{
        this.captchaKey = response.headers['captcha-key'];
        document.getElementById("captcha-image").src = window.URL.createObjectURL(new Blob([response.data]));
      })
    },
    confirm() {
      this.confirm_disabled=true;
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
          this.$axios.post(this.$httpUrl+'/captcha/verify',{
            captchaKey:this.captchaKey,
            captchaText:this.captchaText
          }).then(res=>res.data).then(res=>{
            if(res.code!==200){
              this.confirm_disabled=false;
              this.$message.error(res.msg||"验证码错误");
              this.refreshCaptcha();
            }
            else {
              this.$axios.post(this.$httpUrl +'/user/login',this.loginForm).then(res=>res.data).then(res=>{
                console.log(res)
                if (res.code===200) {
                  sessionStorage.setItem("CurUser",JSON.stringify(res.data))
                  this.$router.replace('/UserIndex').then(() => {
                    // 如果需要默认打开Home，可以在这里处理
                  });
                } else {
                  this.confirm_disabled = false;
                  this.$message.error('用户名或密码错误!');
                }
              }).catch(error => {
                this.confirm_disabled = false;
                if(error !== "验证码错误") {
                  this.$message.error('登录失败，请重试');
                }
              });
            }
          })
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
        <h2>秋水影院</h2>
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
          <div class="captcha-container">
            <el-input v-model="captchaText"
                      placeholder="验证码"
            style="width: 60%">
            </el-input>
            <img id="captcha-image"
                  @click="refreshCaptcha"
                  style="width: 35%; height:40px; margin-left: 5%;cursor: pointer;"
            alt="验证码">


          </div>
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

      <div class="login-footer">
        <span>还没有账号？</span>
        <el-link type="primary" @click.native="toregister">立即注册</el-link>
      </div>
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

.login-footer {
  margin-top: 20px;
  text-align: center;
  font-size: 14px;
  color: #909399;
}
.captcha-container {
  display: flex;
  align-items: center;
  width: 100%;
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
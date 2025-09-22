<script>
export default {
  name: "UserRegister",
  data() {
    return {
      loading: false,
      role:0,
      registerForm: {
        username: '',
        password: '',
        confirmpassword:'',
        phone: '',
        email: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名(3到8个字符)', trigger: 'blur' },
          { min: 3, max: 8, message: '长度在 3到 8个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码(8位至少包含字母和数字)', trigger: 'blur' },
          {
            pattern: /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/,
            message: '密码至少8位，且包含字母和数字',
            trigger: 'blur'
          }
        ],
        confirmpassword: [
          { required: true, message: '请再次输入密码', trigger: 'blur' },
          { validator: this.validatePassword, trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号码', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱地址', trigger: 'blur' },
          { message: '请输入邮箱地址', trigger: 'blur' },
          {
            type: 'email',
            message: '请输入正确的邮箱格式',
            trigger: ['blur', 'change']
          }
        ]
      }
    }
  },
  methods: {
    validatePassword(rule, value, callback) {
      if (value !== this.registerForm.password) {
        callback(new Error('两次输入的密码不一致'));
      } else {
        callback();
      }
    },
    handleRegister() {
      this.$refs.registerForm.validate(valid => {
        if (valid) {
          this.loading = true
          // 调用注册API
          this.$axios.post(this.$httpUrl + '/user/register', {
            username: this.registerForm.username,
            password: this.registerForm.password,
            phone: this.registerForm.phone,
            email: this.registerForm.email,
            role:this.role
          }).then(res => {
            if (res.data.code === 200) {
              this.$message.success('注册成功！')
              // 注册成功后跳转到登录页面
              this.$router.push('/')
            } else {
              this.$message.error(res.data.msg || '注册失败')
            }
          }).catch(error => {
            this.$message.error('请求出错：' + error.message)
          }).finally(() => {
            this.loading = false
          })
        } else {
          return false
        }
      })
    },
    toLogin(){
      this.$router.push('/')
    }
  }
}
</script>

<template>
  <div class="register-container">
    <div class="register-background"></div>

    <div class="register-card">
      <div class="register-header">
        <h2>用户注册</h2>
        <p>创建您的账户</p>
      </div>

      <el-form
          ref="registerForm"
          :model="registerForm"
          :rules="rules"
          class="register-form"
      >
        <el-form-item prop="username">
          <el-input
              v-model="registerForm.username"
              placeholder="用户名"
              prefix-icon="el-icon-user"
              clearable
          ></el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input
              v-model="registerForm.password"
              placeholder="密码"
              prefix-icon="el-icon-lock"
              show-password
          ></el-input>
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input
              v-model="registerForm.confirmpassword"
              placeholder="确认密码"
              prefix-icon="el-icon-lock"
              show-password
          ></el-input>
        </el-form-item>

        <el-form-item prop="phone">
          <el-input
              v-model="registerForm.phone"
              placeholder="手机号"
              prefix-icon="el-icon-mobile-phone"
          ></el-input>
        </el-form-item>

        <el-form-item prop="email">
          <el-input
              v-model="registerForm.email"
              placeholder="邮箱"
              prefix-icon="el-icon-message"
          ></el-input>
        </el-form-item>

        <el-form-item>
          <el-button
              type="primary"
              @click="handleRegister"
              :loading="loading"
              class="register-btn"
          >
            注 册
          </el-button>
        </el-form-item>
      </el-form>

      <div class="register-footer">
        <span>已有账号？</span>
        <el-link type="primary" @click="toLogin">立即登录</el-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
.register-container {
  position: relative;
  width: 100%;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
  overflow: hidden;
}

.register-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: url('D:/java/moviemanage/images/movielogin.png') no-repeat center center;
  background-size: cover;
  filter: blur(4px);
  z-index: 1;
  opacity: 0.8;
}

.register-card {
  position: relative;
  width: 450px;
  padding: 40px 50px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
  z-index: 2;
  animation: fadeInUp 0.6s ease-out;
  transition: all 0.3s ease;
}

.register-card:hover {
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
  transform: translateY(-5px);
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-header h2 {
  color: #303133;
  font-size: 28px;
  margin-bottom: 8px;
  font-weight: 600;
  letter-spacing: 1px;
}

.register-header p {
  color: #909399;
  font-size: 14px;
}

.register-form {
  margin-top: 25px;
}

.register-form .el-form-item {
  margin-bottom: 22px;
}

.register-form .el-input__inner {
  height: 46px;
  line-height: 46px;
  border-radius: 8px;
  padding-left: 40px;
  transition: all 0.3s;
}

.register-form .el-input__inner:hover {
  border-color: #c0c4cc;
}

.register-form .el-input__inner:focus {
  border-color: #409EFF;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.register-form .el-input__prefix {
  left: 10px;
}

.register-form .el-input__prefix .el-icon {
  font-size: 18px;
  color: #c0c4cc;
}

.register-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  letter-spacing: 2px;
  margin-top: 15px;
  background: linear-gradient(45deg, #409EFF, #64b5ff);
  border: none;
  border-radius: 8px;
  transition: all 0.3s;
}

.register-btn:hover {
  background: linear-gradient(45deg, #64b5ff, #409EFF);
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(64, 158, 255, 0.3);
}

.register-btn:active {
  transform: translateY(0);
}

.register-footer {
  margin-top: 25px;
  text-align: center;
  font-size: 14px;
  color: #909399;
}

.el-link {
  margin-left: 5px;
  font-weight: 500;
}

/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .register-card {
    width: 90%;
    padding: 30px 25px;
    margin: 20px 0;
  }

  .register-header h2 {
    font-size: 24px;
  }

  .register-form .el-form-item {
    margin-bottom: 18px;
  }

  .register-form .el-input__inner {
    height: 42px;
    line-height: 42px;
  }

  .register-btn {
    height: 44px;
  }
}

@media (max-width: 480px) {
  .register-card {
    padding: 25px 20px;
  }

  .register-header h2 {
    font-size: 22px;
  }

  .register-form .el-input__inner {
    padding-left: 35px;
  }
}
</style>
<script>
import DateUtils from "./DateUtils";
export default {
  name: "Home",
  components: {DateUtils},
  data() {
    return {
      user:{}
    }
  },
  methods:{
    init(){
      this.user = JSON.parse(sessionStorage.getItem('CurUser'))
    }
  },
  created(){
    this.init()
  }
}
</script>

<template>
  <div class="personal-center-container">
    <div class="content-wrapper">
      <div class="info-section">
        <div class="info-card">
          <div class="info-item">
            <span class="info-label"><i class="el-icon-user"></i> 用户名</span>
            <span class="info-value">{{user.username}}</span>
          </div>
          <div class="info-item">
            <span class="info-label"><i class="el-icon-phone"></i> 电话</span>
            <span class="info-value">{{user.phone || '未设置'}}</span>
          </div>
          <div class="info-item">
            <span class="info-label"><i class="el-icon-message"></i> 邮箱</span>
            <span class="info-value">{{user.email || '未设置'}}</span>
          </div>
          <div class="info-item">
            <span class="info-label"><i class="el-icon-s-flag"></i> 角色</span>
            <span class="info-value">
              <span :class="['role-tag', user.role==1?'admin':'user']">
                {{user.role==1?"管理员":"普通用户"}}
              </span>
            </span>
          </div>
        </div>
      </div>

      <div class="utils-section">
        <DateUtils></DateUtils>
      </div>
    </div>
  </div>
</template>

<style scoped>
.personal-center-container {
  min-height: 100vh;
  background-color: #f8fafc;
  padding: 30px 20px;
  box-sizing: border-box;
}

.welcome-header h1 {
  font-size: 2.2rem;
  color: #2d3748;
  margin-bottom: 8px;
  font-weight: 600;
}

.content-wrapper {
  max-width: 800px;
  margin: 0 auto;
}

.info-section {
  margin-bottom: 30px;
}

.info-card {
  background: white;
  border-radius: 10px;
  padding: 25px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: 15px 0;
  border-bottom: 1px solid #edf2f7;
}

.info-item:last-child {
  border-bottom: none;
}

.info-label {
  color: #4a5568;
  font-size: 0.95rem;
}

.info-label i {
  margin-right: 8px;
  width: 20px;
  text-align: center;
}

.info-value {
  font-weight: 500;
  color: #2d3748;
}

.role-tag {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 500;
}

.role-tag.admin {
  background-color: #fff5f5;
  color: #c53030;
}

.role-tag.user {
  background-color: #f0fff4;
  color: #2f855a;
}

.utils-section {
  background: white;
  border-radius: 10px;
  padding: 25px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .welcome-header h1 {
    font-size: 1.8rem;
  }

  .info-card, .utils-section {
    padding: 20px 15px;
  }

  .info-item {
    flex-direction: column;
    gap: 5px;
  }

  .info-label, .info-value {
    width: 100%;
  }
}
</style>
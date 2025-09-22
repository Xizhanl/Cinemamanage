<template>
  <div class="welcome-container">
    <el-container>
      <el-main class="main-content">
        <div class="welcome-card">
          <h1 class="welcome-title">欢迎使用秋水影院管理系统</h1>
          <p class="welcome-text">在这里您可以管理影片信息、管理场次、订单数据等</p>

          <el-row :gutter="20" class="quick-actions">
            <el-col :span="8">
              <el-card
                  shadow="hover"
                  class="action-card movie-card"
                  @click.native="navigateTo('MovieManage')"
              >
                <i class="el-icon-video-camera card-icon"></i>
                <h3>影片管理</h3>
                <p>管理影院所有影片信息</p>
                <div class="card-footer"></div>
              </el-card>
            </el-col>

            <el-col :span="8">
              <el-card
                  shadow="hover"
                  class="action-card session-card"
                  @click.native="navigateTo('Session')"
              >
                <i class="el-icon-date card-icon"></i>
                <h3>场次管理</h3>
                <p>设置影片放映时间</p>
                <div class="card-footer"></div>
              </el-card>
            </el-col>

            <el-col :span="8">
              <el-card
                  shadow="hover"
                  class="action-card order-card"
                  @click.native="navigateTo('Order')"
              >
                <i class="el-icon-tickets card-icon"></i>
                <h3>订单管理</h3>
                <p>查看和管理所有订单</p>
                <div class="card-footer"></div>
              </el-card>
            </el-col>
          </el-row>

          <div class="recent-activities">
            <h2 class="activity-title">
              <i class="el-icon-time"></i> 近期活动
            </h2>
            <el-timeline>
              <el-timeline-item
                  v-for="(activity, index) in activities"
                  :key="index"
                  :timestamp="activity.time"
                  :color="getTimelineColor(index)"
              >
                <div class="activity-item">
                  <i class="el-icon-info activity-icon"></i>
                  <span>{{ activity.content }}</span>
                </div>
              </el-timeline-item>
            </el-timeline>
          </div>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script>
export default {
  data() {
    return {
      activities: [
        { time: '2025-09-01 10:00', content: '新增了《唐人街探案4》影片信息' },
        { time: '2025-08-28 14:30', content: '对数据进行了整合' },
        { time: '2025-08-05 09:15', content: '新增了50个订单' },
        { time: '2025-07-20 16:45', content: '系统进行了版本更新' }
      ]
    }
  },
  methods: {
    navigateTo(path) {
      this.$router.push(`/${path}`)
    },
    getTimelineColor(index) {
      const colors = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C']
      return colors[index % colors.length]
    }
  }
}
</script>

<style scoped>
.welcome-container {
  height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ed 100%);
}

.main-content {
  padding: 20px;
  background: transparent;
}

.welcome-card {
  background-color: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.08);
}

.welcome-title {
  color: #303133;
  font-size: 28px;
  text-align: center;
  margin-bottom: 15px;
  background: linear-gradient(to right, #409EFF, #67C23A);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.welcome-text {
  color: #606266;
  text-align: center;
  margin: 0 auto 40px;
  max-width: 600px;
  font-size: 16px;
}

.quick-actions {
  margin: 40px 0;
}

.action-card {
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 8px;
  border: none;
  position: relative;
  overflow: hidden;
}

.action-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 8px 20px 0 rgba(0, 0, 0, 0.12);
}

.card-icon {
  font-size: 50px;
  margin: 20px 0 15px;
  display: block;
}

.action-card h3 {
  margin: 10px 0;
  font-size: 18px;
  color: #303133;
}

.action-card p {
  color: #909399;
  font-size: 14px;
  margin-bottom: 30px;
}

.card-footer {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 4px;
}

.movie-card .card-footer {
  background: linear-gradient(to right, #409EFF, #79bbff);
}

.session-card .card-footer {
  background: linear-gradient(to right, #67C23A, #85ce61);
}

.order-card .card-footer {
  background: linear-gradient(to right, #E6A23C, #ebb563);
}

.movie-card .card-icon {
  color: #409EFF;
}

.session-card .card-icon {
  color: #67C23A;
}

.order-card .card-icon {
  color: #E6A23C;
}

.recent-activities {
  margin-top: 50px;
  background-color: #f9fafc;
  padding: 25px;
  border-radius: 8px;
}

.activity-title {
  color: #303133;
  margin-bottom: 25px;
  font-size: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.activity-title i {
  margin-right: 10px;
  color: #409EFF;
}

.activity-item {
  display: flex;
  align-items: center;
  padding: 8px 0;
}

.activity-icon {
  margin-right: 10px;
  font-size: 16px;
}

.el-timeline-item {
  padding-bottom: 20px;
}
</style>
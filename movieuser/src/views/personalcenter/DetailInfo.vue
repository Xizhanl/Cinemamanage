<script>
import axios from "axios";
import QRCode from 'qrcode'
export default {
  data() {
    return {
      orderDetails: {
        id:'',
        moviename: '',
        hallname: '',
        screeningdate: '',
        starttime: '',
        endtime: '',
        seatnumbers: {},
        totalprice: '',
        ordertime: '',
        orderstatus: '',
        notice: ''
      },
      qrDataUrl: '',  // base64 图片
      paymentTimer: null, // 支付倒计时定时器
      remainingTime: 15 * 60, // 15分钟倒计时(秒)
      payImgUrl: require('@/assets/pay.jpg')
    }
  },
  methods: {
    getActiveStep(status) {
      switch (status) {
        case '待取票':
          return 4
        case '已完成':
          return 5
        case '待支付':
          return 2
        case '已取消':
          return 2
        default:
          return 1
      }
    },
    // 格式化时间显示
    formatTime(seconds) {
      const mins = Math.floor(seconds / 60);
      const secs = seconds % 60;
      return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
    },

    // 开始支付倒计时
    startPaymentCountdown() {
      if (this.paymentTimer) clearInterval(this.paymentTimer);

      // 计算剩余时间（从订单创建时间开始15分钟）
      const orderTime = new Date(this.orderDetails.ordertime);
      const now = new Date();
      const elapsedSeconds = Math.floor((now - orderTime) / 1000);
      this.remainingTime = Math.max(0, 15 * 60 - elapsedSeconds);

      if (this.remainingTime <= 0) {
        this.autoCancelOrder();
        return;
      }

      this.paymentTimer = setInterval(() => {
        this.remainingTime--;

        if (this.remainingTime <= 0) {
          clearInterval(this.paymentTimer);
          this.autoCancelOrder();
        }
      }, 1000);
    },

    // 自动取消订单
    async autoCancelOrder() {
      try {
        // 避免重复取消
        if (this.orderDetails.orderstatus !== '待支付') return;
        // 调用取消订单逻辑
        await this.cancelOrder();
        // 更新订单状态显示
        this.orderDetails.orderstatus = '已取消';
        this.$message.warning('订单因超时未支付已自动取消');
      } catch (error) {
        console.error('自动取消订单失败:', error);
      }
    },
    async GetSessionList() {
      const res = await axios.get(this.$httpUrl + "/sessions/list");
      this.sessions = res.data.map(session => ({
        id: session.id,
        moviename: session.moviename,
        hallname: session.hallname,
        screeningtime: session.screeningtime,
        duration: session.duration,
        notice: session.notice || '暂无提示信息',
        seatstatus:session.seatstatus,
        seatssold: session.seatssold
      }));
      return this.sessions;
    },
    async GetOrderList() {
      const ordersRes = await axios.get(this.$httpUrl + "/orders/list");
      const sessions = await this.GetSessionList();
      return ordersRes.data.map(order => {
        // 通过电影名、影厅、日期和时间匹配场次
        const matchedSession = sessions.find(session =>
            session.moviename === order.moviename &&
            session.hallname === order.hallname &&
            session.screeningtime === order.screeningdate &&
            session.duration === order.starttime
        );
        return {
          ...order,
          notice: matchedSession ? matchedSession.notice : '暂无提示信息',
        };
      });
    },
    goToPay(orderId) {
      const payImg = `<div style="text-align:center">
                  <img src="${this.payImgUrl}" style="width:160px;border-radius:4px"/><br/>
                  <small style="color:#999">点击确定后将完成支付</small>
                </div>`
      this.$confirm(payImg,'确定要支付当前订单吗?', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        dangerouslyUseHTMLString: true
      }).then(() => {
        this.confirmOrder(orderId)
      }).catch(() => {
        this.$message.info('已取消操作')
      })
    },
    async confirmOrder(orderId) {
      try {
        const response = await this.$axios.post(this.$httpUrl+'/orders/confirm', {
          id:this.orderDetails.id,
        });
        if (response.data.code === 200) {
          this.orderDetails.orderstatus = '待取票'   // 本地状态立即刷新
          this.$message.success('支付成功！2秒后自动跳转')
          setTimeout(() => {
            this.$router.push({ path: '/DetailInfo', query: { id: orderId } })
          }, 2000)
        } else {
          this.$message.error(response.data.msg);
        }
      } catch (error) {
        this.$message.error('确认订单失败: ' + (error.response?.data?.msg || error.message));
      }
    },
    goTopay(orderId) {
      this.$confirm('确认已完成取票', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
      }).then(() => {
        this.confirmorder(orderId)
      }).catch(() => {
        this.$message.info('已取消操作')
      })
    },
    async confirmorder(orderId) {
      try {
        const response = await this.$axios.post(this.$httpUrl+'/orders/confirmz', {
          id:this.orderDetails.id,
        });
        if (response.data.code === 200) {
          setTimeout(() => {
            this.$router.push({ path: '/DetailInfo', query: { id: orderId } })
          }, 2000)
          this.orderDetails.orderstatus = '已完成';
            await this.$router.push({path:'/DetailInfo',query:{id:orderId}});
            this.$message.success('取票完成')
        } else {
          this.$message.error(response.data.msg);
        }
      } catch (error) {
        this.$message.error('取票确认失败: ' + (error.response?.data?.msg || error.message));
      }
    },
    deleteorder() {
      this.$confirm('确定要取消当前订单吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 取消订单逻辑
        this.cancelOrder()
      }).catch(() => {
        this.$message.info('已取消操作')
      })
    },
    async cancelOrder() {
      try {
        // 清除倒计时
        if (this.paymentTimer) clearInterval(this.paymentTimer);
        // 1. 获取当前订单对应的场次信息
        const sessions = await this.GetSessionList();
        const matchedSession = sessions.find(session =>
            session.moviename === this.orderDetails.moviename &&
            session.hallname === this.orderDetails.hallname &&
            session.screeningtime === this.orderDetails.screeningdate &&
            session.duration === this.orderDetails.starttime
        );
        // 2. 解析座位状态
        let seatStatus = {};
        try {
          seatStatus = matchedSession.seatstatus ? JSON.parse(matchedSession.seatstatus) : {};
        } catch (e) {
          console.error('解析座位状态失败:', e);
          seatStatus = {};
        }
        // 3. 更新座位状态
        const updatedSeatStatus = JSON.parse(JSON.stringify(seatStatus));
        this.orderDetails.seatnumbers.split(',').forEach(seat => {
          const match = seat.match(/(\d+)排(\d+)座/); // 提取数字部分
          if (match) {
            const [, row, col] = match; // row是"10"，col是"9"
            updatedSeatStatus[row][col-1] = 0; // 设置为可用
          }
        });
        // 5. 调用API取消订单
        const response = await this.$axios.post(this.$httpUrl+'/orders/cancel', {
          id: this.orderDetails.id,
        });
        if (response.data.code === 200) {
          // 6. 更新场次座位状态
          const updateResponse = await this.$axios.post(this.$httpUrl + '/sessions/cancelseatstatus', {
            id: matchedSession.id,
            seatstatus: JSON.stringify(updatedSeatStatus),
            seatsold: this.orderDetails.seatnumbers.split(',').length
          });

          if (updateResponse.data.code === 200) {
            this.$message.success('订单已取消');
            await this.$router.push('/BuyTicket');
          } else {
            this.$message.error(updateResponse.data.msg || '座位状态更新失败');
          }
        } else {
          this.$message.error(response.data.msg);
        }
      } catch (error) {
        console.error('取消订单失败:', error);
        this.$message.error('取消订单失败: ' + (error.response?.data?.msg || error.message));
      }
    },
    async loadOrderData() {
      const orders = await this.GetOrderList();
      const orderId = this.$route.query.id;
      if (orderId) {
        const order = orders.find(m => m.id == orderId);
        if (order) {
          this.orderDetails = order;
        } else {
          this.$message.error('未找到对应订单');
        }
      }
    },
  },
  async created() {
    await this.loadOrderData();
    const orders = await this.GetOrderList();
    const orderId = this.$route.query.id;
    if (orderId) {
      const order = orders.find(m => m.id == orderId);
      this.qrDataUrl = await QRCode.toDataURL(orderId, { width: 160 })
      if (order) {
        this.orderDetails = order;
        if (order.orderstatus === '待支付') {
          this.startPaymentCountdown();
        }
      } else {
        this.$message.error('未找到对应订单');
      }
    }
  },
  beforeDestroy() {
    // 组件销毁时清除定时器
    if (this.paymentTimer) clearInterval(this.paymentTimer);
  }
}

</script>

<template>
  <div class="detail-container">
    <!-- 步骤条 -->
    <div class="steps-container">
      <el-steps
          :active="getActiveStep(orderDetails.orderstatus)"
          finish-status="success"
          align-center
      >
        <el-step title="选择电影场次" />
        <el-step title="选择座位" />
        <el-step title="支付" />
        <el-step title="取票观影" />
        <el-step title="已完成取票" />
      </el-steps>
    </div>

    <!-- 状态横幅 -->
    <div class="completion-banner" :class="orderDetails.orderstatus">
      <div class="text-area">
        <h1>{{ orderDetails.orderstatus }}
          <span v-if="orderDetails.orderstatus === '待支付'" class="countdown">
        (剩余支付时间: {{ formatTime(remainingTime) }})</span></h1>
        <h2 style="font-size: 30px; color: #f56c6c">总金额: ¥{{ orderDetails.totalprice }}</h2>
        <div class="order-info">
          <span>订单编号: {{ orderDetails.id }}</span>
          <span>下单时间: {{ orderDetails.ordertime }}</span>
        </div>
      </div>

      <div class="qr-area" v-if="orderDetails.orderstatus === '待取票'">
        <img :src="qrDataUrl" alt="取票二维码" />
      </div>
    </div>

    <!-- 订单详情表格 -->
    <div class="order-table">
      <div class="table-header">
        <div class="header-item" style="width: 25%">影片信息</div>
        <div class="header-item" style="width: 25%">播放时间</div>
        <div class="header-item" style="width: 25%">座位信息</div>
        <div class="header-item" style="width: 25%">提示信息</div>
      </div>
      <div class="table-content">
        <div class="content-item movie-info" style="width: 25%">
          <div class="movie-name">{{orderDetails.moviename}}</div>
          <div class="hall-name">{{orderDetails.hallname}}</div>
        </div>
        <div class="content-item time-info" style="width: 25%">
          <div class="screening-date">{{orderDetails.screeningdate}}</div>
          <div class="time-range">{{orderDetails.starttime}}-{{orderDetails.endtime}}</div>
        </div>
        <div class="content-item seat-info" style="width: 25%">
          <div class="seat-numbers">{{orderDetails.seatnumbers}}</div>
        </div>
        <div class="content-item notice-info" style="width: 25%">
          <div class="notice">{{orderDetails.notice}}</div>
        </div>
      </div>
    </div>
    <!-- 操作按钮 -->
    <div class="order-actions">
      <el-button type="primary" plain @click="$router.push('/PersonalCenter/OrderInfo')">返回订单列表</el-button>
      <el-button
          type="success"
          plain
          v-if="orderDetails.orderstatus === '待取票'"
          @click="goTopay"
      >
        已完成取票
      </el-button>
      <el-button
          type="danger"
          plain
          v-if="orderDetails.orderstatus === '待取票'"
          @click="deleteorder"
      >
        退票
      </el-button>
      <el-button
          type="danger"
          plain
          v-if="orderDetails.orderstatus === '待支付'"
          @click="deleteorder"
      >
        取消订单
      </el-button>
      <el-button
          type="success"
          plain
          v-if="orderDetails.orderstatus === '待支付'"
          @click="goToPay(orderDetails.id)"
      >
        立即支付
      </el-button>
    </div>
  </div>
</template>

<style scoped>

.detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  font-family: 'Helvetica Neue', Arial, sans-serif;
}

/* 步骤条样式 */
.steps-container {
  width: 100%;
  margin: 20px auto 40px;
  padding: 0 20px;
}

/* 状态横幅样式 */
.completion-banner {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  padding: 20px;
  border-radius: 8px;
  margin: 20px auto 40px;
  width: 100%;
  max-width: 1000px;
  border-left: 5px solid;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
.text-area {
  flex: 1;
}

.qr-area {
  margin-left: 20px;
  flex-shrink: 0;
}

.qr-area img {
  width: 160px;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}
.completion-banner.已完成 {
  background: #f0f9eb;
  border-color: #67C23A;
}
/* 添加倒计时样式 */
.countdown {
  font-size: 18px;
  color: #f56c6c;
  margin-left: 10px;
}
/* 待支付状态横幅特殊样式 */
.completion-banner.待取票 {
  background: #fdf6ec;
  border-color: #009688;
  position: relative;
  overflow: hidden;
}
.completion-banner.待取票::after {
  content: '';
  position: absolute;
  left: 0;
  bottom: 0;
  height: 4px;
  background: #009688;
  transition: width 1s linear;
}
/* 待支付状态横幅特殊样式 */
.completion-banner.待支付 {
  background: #fdf6ec;
  border-color: #ebb563;
  position: relative;
  overflow: hidden;
}
.completion-banner.待支付 .text-area h1       { color: #ebb563; font-size: 50px; font-weight: 600;}   /* 橙黄 */
.completion-banner.待取票 .text-area h1       { color: #009688;font-size: 50px; font-weight: 600;}   /*  teal */
.completion-banner.已完成 .text-area h1       { color: #67C23A;font-size: 50px; font-weight: 600; }   /* 绿色 */
.completion-banner.已取消 .text-area h1       { color: #f78989;font-size: 50px; font-weight: 600; }   /* 红色 */

.completion-banner.待支付::after {
  content: '';
  position: absolute;
  left: 0;
  bottom: 0;
  height: 4px;
  background: #ebb563;
  transition: width 1s linear;
}
.completion-banner.已取消 {
  background: #fef0f0;
  border-color: #f78989;
}

.completion-banner h1 {
  display: flex;
  align-items: center;
  font-size: 24px;
  margin: 0;
  color: inherit;
}

.completion-banner i {
  margin-right: 10px;
  font-size: 28px;
}

.order-info {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  font-size: 20px;
  color: #666;
}

/* 表格样式部分 */
.order-table {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
}

.table-header {
  display: flex;
  background: #f5f7fa;
  padding: 15px 20px;
  font-weight: bold;
  color: #333;
  border-bottom: 1px solid #ebeef5;
}

.header-item {
  text-align: center;
  padding: 0 10px;
}

.header-item:first-child {
  text-align: left;
}

.header-item:last-child {
  text-align: right;
}

.table-content {
  display: flex;
  background: #fff;
  padding: 20px;
  text-align: center;
}

.content-item {
  padding: 0 10px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.content-item:first-child {
  text-align: left;
  padding-left: 0;
}

.content-item:last-child {
  text-align: right;
  padding-right: 0;
}

.movie-name {
  font-weight: bold;
  margin-bottom: 5px;
}

.hall-name {
  color: #666;
  font-size: 14px;
}

.time-range {
  color: #666;
  font-size: 14px;
}

.notice {
  color: #f56c6c;
  font-size: 14px;
}

/* 操作按钮样式 */
.order-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 30px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .table-header, .table-content {
    flex-direction: column;
  }

  .header-item, .content-item {
    width: 100% !important;
    text-align: left !important;
    padding: 10px 0 !important;
    border-bottom: 1px solid #eee;
  }

  .header-item:last-child, .content-item:last-child {
    border-bottom: none;
  }
}
</style>
<script>

export default {
  data() {
    return {
      countdown: 900, // 15分钟倒计时，单位秒
      timer: null,
      payImgUrl: require('@/assets/pay.jpg') ,  // 你的本地图片路径
      qrDataUrl: '',  // base64 图片
    }
  },
  methods: {
    startCountdown() {
      this.timer = setInterval(() => {
        if (this.countdown > 0) {
          this.countdown--
        } else {
          clearInterval(this.timer)
          this.$message.warning('订单已超时，请重新选择')
          this.cancelOrder()
        }
      }, 1000)
    },
    formatTime(seconds) {
      const mins = Math.floor(seconds / 60)
      const secs = seconds % 60
      return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
    },
    goToPay(orderId) {
      const payImg = `<div style="text-align:center">
                  <img src="${this.payImgUrl}" style="width:160px;border-radius:4px"/><br/>
                  <small style="color:#999">点击确定后将完成支付</small>
                </div>`
      this.$confirm(payImg,'确定要支付当前订单吗?', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        dangerouslyUseHTMLString: true   // 必须开启 HTML 模式
      }).then(() => {
        this.confirmOrder(orderId)
      }).catch(() => {
        this.$message.info('已取消操作')
      })
    },
    async confirmOrder(orderId) {
      try {
        const response = await this.$axios.post(this.$httpUrl+'/orders/confirm', {
          id:this.selectedOrder.id,
        });
        if (response.data.code === 200) {
          // 2. 更新电影票房
          const updateRes = await this.$axios.post(this.$httpUrl+'/movie/updateBoxOffice', null, {
            params: {
              id: this.selectedMovie.id,
              amount: this.totalPrice
            }
          });
          if (updateRes.data.code === 200) {
            this.$message.success('支付成功！2秒后自动跳转')
            setTimeout(() => {
              this.$router.push({ path: '/DetailInfo', query: { id: orderId } })
            }, 2000)
          } else {
            this.$message.error('票房更新失败: ' + updateRes.data.msg);
          }
        } else {
          this.$message.error(response.data.msg);
        }
      } catch (error) {
        this.$message.error('确认订单失败: ' + (error.response?.data?.msg || error.message));
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
        const response = await this.$axios.post(this.$httpUrl+'/orders/cancel', {
          id:this.selectedOrder.id,
        });
        if (response.data.code === 200) {
          const updatedSeatStatus = JSON.parse(JSON.stringify(this.seatstatus));
          this.selectedOrder.seats.forEach(seat => {
            updatedSeatStatus[seat.row][seat.index] = 0;
          });
          // 3. 调用后端API更新座位状态
          const updateResponse = await this.$axios.post(this.$httpUrl + '/sessions/cancelseatstatus', {
            id: this.selectedSession.id,
            seatstatus: JSON.stringify(updatedSeatStatus),
            seatsold : this.selectedOrder.seats.length
          });
          if (updateResponse.data.code === 200) {
            // 更新本地座位状态
            this.selectedOrder.seats.forEach(seat => {
              this.$set(this.seatstatus[seat.row], seat.index, 0);
            });
            this.$message.success('订单已取消')
            await this.$router.push('/BuyTicket');
          } else {
            this.$message.error(updateResponse.data.msg || '座位状态更新失败');
          }
        } else {
          this.$message.error(response.data.msg);
        }
      } catch (error) {
        this.$message.error('取消订单失败: ' + (error.response?.data?.msg || error.message));
      }
    },
    parseSeatStatus() {
      try {
        // 假设 selectedSession.seatstatus 是一个 JSON 字符串
        const status = this.selectedSession.seatstatus;
        if (status) {
          // 去掉可能存在的 {} 外层，然后解析
          const cleanedStatus = status.replace(/^{|}$/g, '');
          this.seatstatus = JSON.parse(`{${cleanedStatus}}`);
          // 确保seatstatus是响应式的
        }
      } catch (error) {
        console.error('解析座位状态失败:', error);
        this.seatstatus = {};
      }
    },
  },
  computed: {
    selectedMovie() {
      return this.$store.state.selectedMovie
    },
    selectedSession() {
      return this.$store.state.selectedSession
    },
    selectedOrder() {
      return this.$store.state.selectedOrder
    },
    totalPrice() {
      return this.selectedOrder.seats.length * this.selectedSession.price
    }
  },
  created() {
    this.startCountdown()
    this.parseSeatStatus()
  },
  beforeDestroy() {
    clearInterval(this.timer)
  },
  watch: {
    // 监听 selectedSession 变化，以便在数据更新时重新解析
    'selectedSession.seatstatus': {
      handler() {
        this.parseSeatStatus();
      },
      deep: true
    }
  },
}
</script>

<template>
  <div class="order-page">
    <div class="steps-wrapper">
      <el-steps :space="300" :active="2" finish-status="success" align-center>
        <el-step title="已完成(选择电影场次)"></el-step>
        <el-step title="已完成(选择座位)"></el-step>
        <el-step title="进行中(支付)"></el-step>
        <el-step title="待完成(取票观影)"></el-step>
        <el-step title="待完成(已完成取票)"></el-step>
      </el-steps>
    </div>

    <div class="order-container">
      <div class="order-info">
        <h2 class="order-title">订单信息</h2>
        <div class="info-grid">
          <div class="info-item">
            <span class="label">电影名称：</span>
            <span class="value">{{selectedOrder.session.moviename}}</span>
          </div>
          <div class="info-item">
            <span class="label">影院：</span>
            <span class="value">秋水影院</span>
          </div>
          <div class="info-item">
            <span class="label">影厅：</span>
            <span class="value">{{selectedOrder.session.hallname}}</span>
          </div>
          <div class="info-item">
            <span class="label">场次日期：</span>
            <span class="value">{{selectedOrder.session.screeningtime}}</span>
          </div>

          <div class="info-item">
            <span class="label">座位：</span>
            <span class="value">[{{ selectedOrder.seats.map(item => item.seatNum).join('、') }}]</span>
          </div>
          <div class="info-item">
            <span class="label">场次时间：</span>
            <span class="value">{{selectedOrder.session.duration}}-{{selectedOrder.session.endtime}}</span>
          </div>
          <div class="info-item">
            <span class="label">单价：</span>
            <span class="value">¥{{ selectedSession.price }}</span>
          </div>
          <div class="info-item">
            <span class="label">数量：</span>
            <span class="value">{{ selectedOrder.seats.length }}张</span>
          </div>
        </div>

        <div class="total-price">
          <span class="label">总计：</span>
          <span class="value">¥{{ totalPrice }}</span>
        </div>
      </div>

      <div class="payment-section">
        <div class="countdown-card">
          <div class="countdown">
            <i class="el-icon-alarm-clock clock-icon"></i>
            <div class="countdown-text">
              <p>请在 <span class="time">{{ formatTime(countdown) }}</span> 内完成支付</p>
              <p class="hint">超时订单将自动取消</p>
            </div>
          </div>
        </div>
        <div class="action-buttons">
          <el-button
              type="primary"
              size="large"
              class="pay-button"
              @click="goToPay(selectedOrder.id)">
            立即支付 ¥{{ totalPrice }}
          </el-button>
          <el-button
              plain
              size="large"
              class="cancel-button"
              @click="deleteorder">
            取消订单
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.order-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.steps-wrapper {
  width: 100%;
  margin: 20px auto 40px;
  padding: 0 50px;
}

.order-container {
  display: flex;
  gap: 30px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.order-info {
  flex: 1;
  padding: 30px;
  border-right: 1px solid #f0f0f0;
}

.order-title {
  font-size: 22px;
  color: #333;
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18px;
}

.info-item {
  display: flex;
  align-items: center;
}

.label {
  color: #666;
  font-size: 15px;
  min-width: 70px;
  text-align: left;
  margin-right: 10px;
}

.value {
  color: #333;
  font-size: 15px;
  font-weight: 500;
  word-break: break-all;
}

.total-price {
  margin-top: 25px;
  padding-top: 15px;
  border-top: 1px dashed #ddd;
  display: flex;
  align-items: center;
}

.total-price .label {
  font-size: 17px;
  font-weight: bold;
}

.total-price .value {
  font-size: 22px;
  color: #ff6a00;
  font-weight: bold;
}

.payment-section {
  flex: 1;
  padding: 30px;
  display: flex;
  flex-direction: column;
}

.countdown-card {
  background: #fff9f9;
  border-radius: 10px;
  padding: 20px;
  margin-bottom: 30px;
}

.countdown {
  display: flex;
  align-items: center;
}

.clock-icon {
  font-size: 48px;
  color: #ff6b6b;
  margin-right: 20px;
}

.countdown-text {
  font-size: 16px;
}

.countdown-text .time {
  color: #ff6b6b;
  font-weight: bold;
  font-size: 22px;
  margin: 0 3px;
}

.hint {
  color: #999;
  font-size: 14px;
  margin-top: 5px;
}

.payment-option i {
  font-size: 20px;
  margin-right: 10px;
}

.action-buttons {
  margin-top: auto;
  width: 100%;
}

.pay-button {
  margin-left: 10px;
  width: 100%;
  height: 50px;
  font-size: 18px;
  margin-bottom: 15px;
  background: linear-gradient(90deg, #ff6a00, #ff9d00);
  border: none;
}

.cancel-button {
  width: 100%;
  height: 50px;
  font-size: 16px;
  color: #666;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .order-container {
    flex-direction: column;
  }

  .order-info {
    border-right: none;
    border-bottom: 1px solid #f0f0f0;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .steps-wrapper {
    padding: 0;
  }
}
</style>
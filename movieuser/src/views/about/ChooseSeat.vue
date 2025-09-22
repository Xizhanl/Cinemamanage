<template>
  <div class="choose-seat-container">
    <!-- 步骤条容器 - 外层div确保居中 -->
    <div class="steps-wrapper">
      <el-steps :space="300" :active="1" finish-status="success" align-center>
        <el-step title="已完成(选择电影场次)"></el-step>
        <el-step title="进行中(选择座位)"></el-step>
        <el-step title="待完成(支付)"></el-step>
        <el-step title="待完成(取票观影)"></el-step>
        <el-step title="待完成(已完成取票)"></el-step>
      </el-steps>
    </div>
    <!-- 电影信息展示 -->
    <h3 style="text-align: center;margin-bottom: 20px">场次详情</h3>
    <div class="movie-info" v-if="selectedMovie">
      <img :src="selectedMovie.moviecover" alt="电影封面" class="movie-cover">
      <div class="movie-details">
        <h2>{{ selectedMovie.moviename }}</h2>
        <p><span>类型：</span>{{ selectedMovie.movietype.join(' / ') }}</p>
        <p><span>时长：</span>{{ selectedMovie.movieduration }}分钟</p>
      </div>
      <div class="session-details">
        <p><span>影院:</span>秋水影院</p>
        <p><span>放映日期:</span>{{ selectedSession.screeningtime }}</p>
        <p><span>放映时间:</span>{{ selectedSession.duration }}</p>
        <p><span>结束时间:</span>{{ selectedSession.endtime }}</p>
        <p><span>放映厅:</span>{{ selectedSession.hallname }}</p>
        <p><span>价格:</span>¥{{ selectedSession.price }}/张</p>
        <p><span>影厅信息:</span>{{ selectedSession.notice }}</p>
      </div>
    </div>

    <!-- 座位选择区域 -->
    <div class="seat-selection">
      <h3 style="text-align: center;margin-bottom: 20px">请选择座位</h3>
      <div style="display:flex; align-content: center;margin-bottom: 20px;justify-content: center;">
        <span style="margin-right: 50px">可选座位</span>
        <span class="seat" :class="isSelected[0]"></span>
        <span style="margin: 0 50px">已售座位</span>
        <span class="seat" :class="isSelected[1]"></span>
        <span style="margin: 0 50px">已选座位</span>
        <span class="seat" :class="isSelected[3]"></span>
      </div>
      <div class="screen-indicator">
        <div class="screen-text">银幕</div>
      </div>
      <!-- 座位选择区域 -->
      <div class="seat-container">
        <div class="row" v-for="(seats,rowNum) in seatstatus" :key="rowNum">
          <span class="row-label">{{rowNum}}排</span>
          <div class="seats-wrapper">
          <span v-for="(seat,index) in seats"
                class="seat"
                :class="isSelected[seatstatus[rowNum][index]]"
                @click="pressSeat(rowNum, index)"
                :key="index"></span>
          </div>
        </div>
      </div>
      <!-- 已选座位信息展示 -->
      <div class="selected-seats-info">
        <h4>已选座位 ({{ selectedSeats.length }}/5)</h4>
        <div class="seat-tags">
              <span v-for="(seat, index) in selectedSeats" :key="index" class="seat-tag">
                {{ seat.seatNum }}
                <span class="remove-seat" @click="removeSeat(index)">×</span>
              </span>
        </div>
        <p class="total-price">总价：¥{{ selectedSeats.length * selectedSession.price }}</p>
        <p v-if="selectedSeats.length >= 5" class="max-seat-warning">最多只能选择5个座位</p>
      </div>
    </div>
    <!-- 操作按钮 -->
    <div class="action-buttons">
      <button @click="$router.back()">返回</button>
      <button @click="confirmOrder">提交订单</button>
    </div>
  </div>
</template>

<script>

export default {
  data(){
    return{
      isSelected: {    // 座位样式映射
        0: 'seat-default',    // 默认状态的样式
        1: 'seat-inhibit',    // 禁用状态的样式
        2: 'seat-inhibit',
        3: 'seat-choose'      // 选择状态的样式
      },
      selectedSeats: [],    // 用户已选座位
      seatstatus:{},
      maxSeats: 5 ,// 最大可选座位数
      orderstatus: "待支付",
      websocket:null,
      reconnectAttempts:0,
      maxReconnectAttempts:5,
      userId: null,
      lockSeats: []// 存储已锁定的座位
    }
  },
  computed: {
    selectedMovie() {
      return this.$store.state.selectedMovie;
    },
    selectedSession() {
      return this.$store.state.selectedSession;
    },
  },
  methods: {
    // 初始化WebSocket连接
    initWebSocket() {
      const curUser = JSON.parse(sessionStorage.getItem("CurUser"));
      this.userId = curUser?.id || Date.now().toString();

      const sessionId = this.selectedSession.id;
      this.websocket = new WebSocket(`ws://localhost:9999/ws/${sessionId}`);

      this.websocket.onopen = () => {
        console.log("WebSocket连接成功");
        this.reconnectAttempts = 0;

        // 发送初始化消息
        this.sendWebSocketMessage({
          type: "init",
          userId: this.userId
        });
      };
      this.websocket.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data);
          if (data.type === 'heartbeat') return;

          switch(data.type) {
            case 'init':
              this.handleInitData(data);
              break;
            case 'lock':
              this.handleSeatLock(data);
              break;
            case 'unlock':
              this.handleSeatUnlock(data);
              break;
            case 'confirm':
              this.handleSeatConfirm(data);
              break;
            case 'seatUpdate':
              this.handleSeatUpdate(data);
              break;
          }
        } catch (error) {
          console.error("WebSocket消息解析失败", error);
        }
      };

      this.websocket.onerror = (error) => {
        console.error('WebSocket错误:', error);
        this.reconnect();
      };

      this.websocket.onclose = () => {
        console.log('WebSocket连接已关闭');
        this.reconnect();
      };
    },// 处理初始化数据
    handleInitData(data) {
      if (data.seatstatus) {
        try {
          const cleanedStatus = data.seatstatus.replace(/^{|}$/g, '');
          this.seatstatus = JSON.parse(`{${cleanedStatus}}`);
        } catch (error) {
          console.error('解析座位状态失败:', error);
        }
      }
    },

    // 处理座位锁定
    handleSeatLock(data) {
      const { row, col, userId } = data;
      if (userId !== this.userId) {
        this.$set(this.seatstatus[row], col, 1);
        this.removeIfSelected(row, col);
      }
    },
    // 处理座位解锁
    handleSeatUnlock(data) {
      const { row, col } = data;
      this.$set(this.seatstatus[row], col, 0);
    },
    // 处理座位确认
    handleSeatConfirm(data) {
      const { row, col } = data;
      this.$set(this.seatstatus[row], col, 2);
    },
    // 处理座位状态更新
    handleSeatUpdate(data) {
      const { row, col, status } = data;
      // 只更新非自己锁定的座位
      if (!this.lockSeats.some(s => s.row === row && s.col === col)) {
        this.$set(this.seatstatus[row], col, status);
      }
    },
    // 移除已选座位
    removeIfSelected(row, col) {
      this.selectedSeats = this.selectedSeats.filter(seat => {
        if (seat.row === row && seat.index === col) {
          this.$message.warning(`座位 ${seat.seatNum} 状态已更新`);
          return false;
        }
        return true;
      });
    },
    // 发送WebSocket消息
    sendWebSocketMessage(message) {
      if (this.websocket && this.websocket.readyState === WebSocket.OPEN) {
        console.log("✅ 发送消息:", message);
        this.websocket.send(JSON.stringify(message));
      } else {
        console.error('WebSocket is not open. Ready state:', this.websocket?.readyState);
        // 可以在这里实现消息队列，等连接建立后再发送
      }
    },

    pressSeat(rowNum, index) {
      // 如果座位已被占用(状态为1)，则不能选择
      if (this.seatstatus[rowNum][index] === 1) {
        this.$message.warning('该座位被禁用，不可选');
        return;
      }
      if (this.seatstatus[rowNum][index] === 2) {
        this.$message.warning('该座位已售，不可选');
        return;
      }
      // 检查是否已达到最大可选座位数
      if (this.selectedSeats.length >= this.maxSeats && this.seatstatus[rowNum][index] !== 3) {
        this.$message.warning(`最多只能选择${this.maxSeats}个座位`);
        return;
      }
      // 切换座位状态
      if (this.seatstatus[rowNum][index] === 3) {
        // 如果已选中，则取消选择
        this.$set(this.seatstatus[rowNum], index, 0);
        // 从已选座位中移除
        this.selectedSeats = this.selectedSeats.filter(
            seat => !(seat.row === rowNum && seat.index === index)
        );
        this.lockSeats = this.lockSeats.filter(
            seat => !(seat.row === rowNum && seat.col === index)
        );
        this.sendWebSocketMessage({
          type: 'unlock',
          row: rowNum,
          col: index,
          userId: this.userId
        });
      } else {
        // 如果未选中，则选中
        this.$set(this.seatstatus[rowNum], index, 3);
        // 添加到已选座位
        this.selectedSeats.push({
          row: rowNum,
          index: index,
          seatNum: `${rowNum}排${index + 1}座` // 生成座位号，如"3排5座"
        });
        this.lockSeats.push({ row: rowNum, col: index });

        this.sendWebSocketMessage({
          type: 'lock',
          row: rowNum,
          col: index,
          userId: this.userId
        });
      }
    },
    // 移除已选座位
    removeSeat(index) {
      const seat = this.selectedSeats[index];
      // 恢复座位状态
      this.$set(this.seatstatus[seat.row], seat.index, 0);
      // 从已选座位中移除
      this.selectedSeats.splice(index, 1);
    },
    // 重连逻辑
    reconnect() {
      if (this.reconnectAttempts >= this.maxReconnectAttempts) {
        console.log('已达到最大重连次数');
        this.$message.error('与服务器连接断开，请刷新页面重试');
        return;
      }

      this.reconnectAttempts++;
      const delay = Math.min(1000 * this.reconnectAttempts, 5000);

      setTimeout(() => {
        console.log(`尝试重新连接 (${this.reconnectAttempts}/${this.maxReconnectAttempts})`);
        this.initWebSocket();
      }, delay);
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
    async confirmOrder() {
      if (this.selectedSeats.length === 0) {
        this.$message.error('请至少选择一个座位');
        return;
      }
      try {
        // 1. 通知服务器确认座位
        this.selectedSeats.forEach(seat => {
          this.sendWebSocketMessage({
            type: 'confirm',
            row: seat.row,
            col: seat.index,
            userId: this.userId
          });
        });
        // 格式化时间
        // 从sessionStorage获取当前用户信息
        const curUser = JSON.parse(sessionStorage.getItem("CurUser"));
        const username = curUser?.username || '未知用户';

        const orderData = {
          username: username,
          moviename: this.selectedSession.moviename,
          hallname: this.selectedSession.hallname,
          screeningdate: this.selectedSession.screeningtime,
          starttime: this.selectedSession.duration,
          endtime: this.selectedSession.endtime,
          seatnumbers: this.selectedSeats.map(seat => seat.seatNum).join(','),
          totalprice: this.selectedSeats.length * this.selectedSession.price,
          ordertime: new Date(+new Date() + 8 * 3600 * 1000).toISOString().slice(0, 19).replace('T', ' '),
          orderstatus: this.orderstatus,
        };
        // 调用后端API创建订单
        const response = await this.$axios.post(this.$httpUrl + '/orders/create', orderData);
        if (response.data.code === 200) {
          const updatedSeatStatus = JSON.parse(JSON.stringify(this.seatstatus));
          this.selectedSeats.forEach(seat => {
            updatedSeatStatus[seat.row][seat.index] = 2;
          });
          // 3. 调用后端API更新座位状态
          const updateResponse = await this.$axios.post(this.$httpUrl + '/sessions/updateseatstatus', {
            id: this.selectedSession.id,
            seatstatus: JSON.stringify(updatedSeatStatus),
            seatsold : this.selectedSeats.length
          });
          if (updateResponse.data.code === 200) {
            // 更新本地座位状态
            this.selectedSeats.forEach(seat => {
              this.$set(this.seatstatus[seat.row], seat.index, 2);
            });
            // 更新本地订单状态
            const order = {
              id: response.data.data.id,
              movie: this.selectedMovie,
              session: this.selectedSession,
              seats: this.selectedSeats,
              totalprice: orderData.totalprice,
              ordertime: new Date().toISOString(),
              seatstatus: JSON.stringify(this.seatstatus)
            };
            // 保存到Vuex
            this.$store.dispatch('setCurrentOrder', order);
            // 跳转到订单确认页面
            this.$router.push("/ConfirmOrder");
          } else {
            this.$message.error(updateResponse.data.msg || '座位状态更新失败');
          }
        } else {
          this.$message.error(response.data.msg || '订单创建失败');
        }
      } catch (error) {
        console.error('创建订单失败:', error);
        this.$message.error('创建订单失败，请稍后重试');
      }
    },
  },
  created() {
    // 检查是否有选中的电影和场次
    if (!this.selectedMovie || !this.selectedSession) {
      this.$message.error('请重新选择电影和场次');
      this.$router.back()
    }
    // 解析座位状态
    this.parseSeatStatus();
    this.initWebSocket();
  },
  beforeDestroy() {
    // 解锁所有已选座位
    this.selectedSeats.forEach(seat => {
      this.sendWebSocketMessage({
        type: 'unlock',
        row: seat.row,
        col: seat.index,
        userId: this.userId
      });
    });

    // 关闭WebSocket连接
    if (this.websocket) {
      this.websocket.close();
      this.websocket = null;
    }
  },
  watch: {
    // 监听 selectedSession 变化，以便在数据更新时重新解析
    'selectedSession.seatstatus': {
      handler() {
        this.parseSeatStatus();
      },
      deep: true
    }
  }
}
</script>

<style scoped>
.steps-wrapper {
  width: 100%;
  max-width: 1000px; /* 控制步骤条最大宽度 */
  margin: 20px auto;
  padding: 0 100px;
}

.choose-seat-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.movie-info {
  display: flex;
  align-items: flex-start; /* 改为顶部对齐 */
  margin-bottom: 30px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
  position: relative; /* 为session-details定位做准备 */
}

.movie-cover {
  width: 120px;
  height: 160px;
  object-fit: cover;
  border-radius: 4px;
  margin-right: 20px;
}

.movie-details {
  flex: 1;
  max-width: 300px; /* 限制电影详情宽度 */
}

.movie-details h2 {
  margin: 0 0 10px 0;
  color: #333;
}

.movie-details p {
  margin: 5px 0;
  color: #666;
}

.movie-details span {
  color: #999;
  display: inline-block;
  width: 60px;
}

/* 场次详情样式 */
.session-details {
  position: absolute;
  right: 20px;
  top: 20px;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
  width: 400px; /* 调整宽度 */
}

.session-details p {
  margin: 5px 0;
  color: #666;
  text-align:left; /* 文本右对齐 */
}

.session-details span {
  color: #999;
  display: inline-block;
  width: 80px;
  text-align: left; /* 标签左对齐 */
}

.action-buttons {
  margin-top: 30px;
  text-align: center;
}

.action-buttons button {
  padding: 10px 20px;
  margin-left: 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.action-buttons button:first-child {
  background: #dcdfe6;
}

.action-buttons button:last-child {
  background: #409eff;
  color: white;
}
.seat-selection{
  text-align: center;
  background:#f5f7fa;
  height: auto;
  width: 100%;
  max-width: 1000px; /* 控制步骤条最大宽度 */
  margin: 20px auto;
  padding: 0 100px;
}
.seat-container{
  width: 100%;
}
.row{
  white-space: nowrap;
  margin-top: 10px;
  text-align: right;
  display: flex;
  justify-content: center;
}
.row-label {
  width: 50px;
  text-align: right;
  margin-right: 50px;
}
.seat {
  display: flex;
  width: 30px;
  height: 26px;
  background: url("@/assets/seat.png") no-repeat center/contain;
}
.seats-wrapper {
  display: flex;
  gap: 8px;
}
.seat-inhibit {
  background: url("@/assets/inhibit.png") no-repeat center/contain;
}
.seat-choose {
  background: url("@/assets/choose.png") no-repeat center/contain;
}
.screen-indicator {
  width: 80%;
  height: 20px;
  margin: 0 auto 30px;
  background: linear-gradient(to bottom  , #e0e0e0, #f5f5f5);
  border-radius: 0 0 50% 50%;
  position: relative;
}
.screen-text {
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%);
  font-size: 14px;
  color: #666;
}
.selected-seats-info {
  margin: 20px auto;
  padding: 15px;
  background: #f0f9eb;
  border-radius: 4px;
  max-width: 800px;

}

.selected-seats-info h4 {
  margin: 0 0 10px 0;
  color: #67c23a;
}

.seat-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 10px;
}

.seat-tag {
  display: inline-flex;
  align-items: center;
  padding: 5px 10px;
  background: #67c23a;
  color: white;
  border-radius: 4px;
  font-size: 14px;
}

.remove-seat {
  margin-left: 5px;
  cursor: pointer;
  font-size: 16px;
  line-height: 1;
}

.remove-seat:hover {
  color: #f56c6c;
}

.total-price {
  font-weight: bold;
  color: #f56c6c;
  margin: 0;
  text-align: right;
  font-size: 20px;
}
.max-seat-warning{
  color: #f56c6c;
  font-size: 15px;
  font-weight: bold;
}
</style>
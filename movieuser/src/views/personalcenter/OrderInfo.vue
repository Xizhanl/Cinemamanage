<script>
import axios from "axios";

export default {
  data() {
    return {
      orderinfo: [],
      userOrders: [],
      currentUser: JSON.parse(sessionStorage.getItem("CurUser") || "{}"),
      moviePosters: {} // 存储电影海报的映射
    }
  },
  methods: {
    async getOrderInfo() {
      try {
        // 获取订单列表
        const res = await axios.get(this.$httpUrl + '/orders/list');
        this.orderinfo = res.data.map(order => ({
          id: order.id,
          username: order.username,
          moviename: order.moviename,
          hallname: order.hallname,
          screeningdate: order.screeningdate,
          starttime: order.starttime,
          endtime: order.endtime,
          seatnumbers: order.seatnumbers,
          totalprice: order.totalprice.toFixed(2),
          ordertime: order.ordertime,
          orderstatus: order.orderstatus
        }));
        // 获取所有电影信息以获取海报
        await this.fetchMoviePosters();
        this.filterUserOrders();
      } catch (error) {
        console.error('获取订单失败:', error);
        this.error = '获取订单失败，请稍后重试';
      } finally {
        this.loading = false;
      }
    },

    async fetchMoviePosters() {
      try {
        const res = await axios.get(this.$httpUrl + '/movie/list');
        // 创建电影名到海报URL的映射
        res.data.forEach(movie => {
          this.moviePosters[movie.moviename] = movie.moviecover;
        });
      } catch (error) {
        console.error('获取电影海报失败:', error);
      }
    },

    filterUserOrders() {
      if (this.currentUser.username) {
        this.userOrders = this.orderinfo.filter(
            order => order.username === this.currentUser.username
        ).sort((a, b) => new Date(b.ordertime) - new Date(a.ordertime));
      }
    },
    async delorder(orderId) {
      try {
        await this.$confirm('确定要删除当前订单吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        });
        const res = await this.$axios.get(`${this.$httpUrl}/orders/del`, {
          params: { id: orderId } // 必须使用id作为参数名
        });
        if (res.data.code === 200) {
          this.$message.success("订单取消成功");
          this.refreshPage()
        } else {
          this.$message.error(res.data.msg || "操作失败");
        }
      } catch (error) {
        if (error !== 'cancel') { // 用户点击取消不报错
          this.$message.error("操作失败: " + (error.response?.data?.msg || error.message));
        }
      }
    },
    refreshPage() {
      window.location.reload();
    },
    todetail(orderId) {
      this.$router.push({
        path: "/DetailInfo",
        query: {id: orderId}
      });
    }
  },
  async created() {
    await this.getOrderInfo();
  }
}
</script>

<template>
  <div class="order-container">
    <div class="order-header">
      <h1><i class="el-icon-tickets"></i> 我的订单</h1>
    </div>

    <div class="status-alert" v-if="userOrders.length === 0">
      <el-alert title="暂无订单记录" type="info" :closable="false" show-icon>
        <router-link to="/BuyTicket" class="go-booking">立即购票</router-link>
      </el-alert>
    </div>
    <div class="order-panel">
    <div class="order-list">
      <div v-for="order in userOrders" :key="order.id" class="order-card" :class="order.orderstatus">
        <div class="card-header">
          <div class="order-meta">
            <span>订单号: {{ order.id }}</span>
            <span>{{ order.ordertime }}</span>
          </div>
          <el-tag size="small" effect="dark" :color="{
            '待支付': '#e6a23c',
            '已完成': '#67c23a',
            '已取消': '#909399',
            '待取票': '#009688'
          }[order.orderstatus]">
            {{ order.orderstatus }}
          </el-tag>

        </div>

        <div class="card-content">
          <div class="movie-poster">
            <img :src="moviePosters[order.moviename] "
                 :alt="order.moviename"
                 >
          </div>

          <div class="movie-info">
            <h3>{{ order.moviename }}</h3>
            <div class="movie-details">
              <p><i class="el-icon-s-ticket"></i> 秋水影院</p>
              <p><i class="el-icon-video-camera"></i> {{ order.hallname }}</p>
              <p style="color: #ef4238;"><i class="el-icon-date"></i> {{ order.screeningdate }} {{
                  order.starttime
                }}~{{ order.endtime }}</p>
            </div>
          </div>

          <div class="price-info">
            <div class="total-price">¥{{ order.totalprice }}</div>
            <div class="action-buttons">
              <el-button @click="todetail(order.id)" size="small" style="background: #f0f9eb">订单详情</el-button>
              <el-button @click="delorder(order.id)" size="small" style="background: #f0f9eb">删除订单</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
    </div>
  </div>
</template>

<style scoped>
.order-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;

}

.order-header {
  margin-bottom: 20px;
  text-align: center;
}

.order-header h1 {
  font-size: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.order-header i {
  margin-right: 8px;
  color: #409EFF;
}

.status-alert {
  margin-bottom: 20px;
}

.go-booking {
  margin-left: 8px;
  color: #409EFF;
}

.order-list {
  margin-top: 20px;
}

.order-card {
  background: #fff;
  border-radius: 4px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  margin-bottom: 15px;
  border-left: 4px solid;
  overflow: hidden;
}

.order-card.待支付 {
  border-left-color: #e6a23c;
}

.order-card.已完成 {
  border-left-color: #67c23a;
}

.order-card.已取消 {
  border-left-color: #909399;
}
.order-card.待取票 {
  border-left-color: #009688;
}


.card-header {
  display: flex;
  justify-content: space-between;
  padding: 12px 15px;
  border-bottom: 1px solid #eee;
  background-color: #f9f9f9;
}

.order-meta {
  display: flex;
  flex-direction: column;
  font-size: 14px;
}

.order-meta span:first-child {
  color: #555;
}

.order-meta span:last-child {
  color: #888;
  font-size: 12px;
}

.card-content {
  display: flex;
  padding: 15px;
  align-items: center;
}

.movie-poster {
  width: 80px;
  height: 110px;
  margin-right: 15px;
  flex-shrink: 0;
  border-radius: 4px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.movie-poster img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.movie-info {
  flex: 1;
  min-width: 0;
}

.movie-info h3 {
  margin: 0 0 10px 0;
  font-size: 16px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.movie-details p {
  margin: 6px 0;
  color: #666;
  font-size: 13px;
}

.movie-details i {
  margin-right: 5px;
  color: #999;
}

.price-info {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  margin-left: 15px;
  min-width: 120px;
}

.total-price {
  font-size: 18px;
  font-weight: bold;
  color: #ff6b6b;
  margin-bottom: 10px;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.order-panel{
  height: 800px;              /* 视口 60%，也可以写 500px */
  overflow-y: auto;          /* 关键 */
  border-radius: 6px;
  background: #fafbfc;
  padding: 0 8px;
  margin-top: 15px;
}
/* 卡片间距收紧，滚动更紧凑 */
.order-list{ padding: 4px 0; }
.order-card{ margin-bottom: 10px; }

/* 移动端再高一点 */
@media (max-width: 768px) {
  .order-panel{ height: 70vh; }
}
@media (max-width: 768px) {
  .card-content {
    flex-wrap: wrap;
  }

  .movie-poster {
    width: 60px;
    height: 85px;
  }

  .price-info {
    width: 100%;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    margin-top: 10px;
    margin-left: 0;
    padding-top: 10px;
    border-top: 1px dashed #eee;
  }

  .total-price {
    margin-bottom: 0;
  }

  .action-buttons {
    flex-direction: row;
  }
}
</style>
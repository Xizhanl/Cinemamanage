<script>
import axios from "axios";
export default {
  data() {
    return {
      cinema: {
        id: "",
        name: "",
        address: "",
        hallTypes: [],
        phone: "",
        closingTime: "",
        imageUrl: "",
        openingTime: "",
        services: ["免费停车", "可乐爆米花", "儿童乐园", "VIP休息室"]
      },

    }
  },
  methods: {
    async getCinema() {
      try {
        const response = await axios.get(this.$httpUrl + "/cinema/list");
        const cinemaData = response.data[0];
        this.cinema = {
          id: cinemaData.id,
          name: cinemaData.name ,
          address: cinemaData.address ,
          hallTypes: cinemaData.hallTypes ,
          phone: cinemaData.phone,
          closingTime: cinemaData.closingTime ,
          imageUrl: cinemaData.imageUrl ,
          openingTime: cinemaData.openingTime ,
          services: [
            { name: "免费停车", desc: "观影可享3小时免费停车" },
            { name: "可乐爆米花", desc: "购买套餐享8折优惠" },
            { name: "儿童乐园", desc: "带小孩观众可免费使用" },
            { name: "WIFI", desc: "WIFI全覆盖" }
          ]
        };
      } catch (error) {
        console.error("获取影院信息失败:", error);
      }
    },

    tobuy(){
      this.$router.push("/BuyTicket")
    }
  },
  created() {
    this.getCinema();
  }
}
</script>

<template>
  <div>
    <div class="header">
      <div class="info">
        <div class="img-wrapper">
          <img :src="cinema.imageUrl" :alt="cinema.name" class="img-img">
        </div>
      </div>
      <div class="info-cinema">
        <h2 class="name">{{cinema.name}}</h2>
        <p class="information">{{cinema.address}}</p>
        <p class="information">电话:{{cinema.phone}}</p>
        <p class="information">营业时间:{{cinema.openingTime}}-{{cinema.closingTime}}</p>
        <p class="server">影院服务:</p>
        <div class="services-list">
          <div class="service-item" v-for="(service, index) in cinema.services" :key="index">
            <span class="service-badge">{{service.name}}</span>
            <span class="service-desc">{{service.desc}}</span>
          </div>
        </div>
      </div>
    </div>
    <div class="hall-types-section">
      <h3 class="hall-types-title">拥有的影厅类型</h3>
      <div class="hall-types-container">
        <span class="hall-type" v-for="(type, index) in cinema.hallTypes" :key="index">{{type}}</span>
      </div>
    </div>
    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button type="primary" size="large" class="action-button" @click="tobuy">点击购票</el-button>
    </div>
  </div>
</template>

<style scoped>
.header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0;
  background: linear-gradient(to bottom, rgba(245, 108, 108, 0.8), rgba(255, 154, 158, 0.8));
  width: 100%;
  height: 500px;
  padding: 0 5%;
}

.info {
  width: 50%;
  display: flex;
  justify-content: flex-end;
}

.img-wrapper {
  background-color: white;
  padding: 5px;
  border-radius: 12px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  display: inline-block;
}

.img-img {
  width: 530px;
  height: 400px;
  border-radius: 8px;
  display: block;
}

.info-cinema {
  width: 50%;
  padding-left: 30px;
}

.name {
  margin-bottom: 15px;
  font-size: 40px;
  font-weight: bold;
  color: #333;
}

.information {
  font-size: 20px;
  margin: 8px 0;
}

.information:nth-of-type(3) {
  margin-bottom: 15px;
}

.server {
  font-size:20px;
  margin-bottom: 10px;
}

.services-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.service-badge {
  background-color: #f0fff4;
  color: #85ce61;
  font-weight: bold;
  padding: 6px 0; /* 统一垂直padding，水平padding设为0 */
  border-radius: 16px;
  border: 1px solid #85ce61;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 90px; /* 固定宽度 */
  height: 32px; /* 固定高度 */
  font-size: 14px;
  box-sizing: border-box; /* 确保边框包含在尺寸内 */
}

.service-desc {
  color: #666;
  font-size: 14px;
  line-height: 1.5;
}
.action-buttons {
  padding: 0 30px 30px;
  text-align: center;
}

.action-button {
  background: #ef4238;
  margin-top: 20px;
  width: 250px;
  height: 50px;
  font-size: 18px;
  font-weight: bold;
  border-radius: 25px;
}
.action-button:hover{
  background: #f56c6c;
}
.hall-types-section {
  margin: 30px 0;
  text-align: center;
}

.hall-types-title {
  font-size: 24px;
  color: #484848;
  margin-bottom: 15px;
  font-weight: bold;
}

.hall-types-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 10px;
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

.hall-type {
  background-color: #00A699;
  color: white;
  padding: 6px 15px;
  border-radius: 15px;
  font-size: 14px;
  display: inline-block;
}
@media (max-width: 768px) {
  .action-button {
    width: 100%;
  }
}
</style>
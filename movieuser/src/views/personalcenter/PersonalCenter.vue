<script>
export default {
  data() {
    return {
      menuList: [
        { id: "1", name: "个人信息", path: "/PersonalCenter/UserInfo", icon: "el-icon-s-order" },
        { id: "2", name: "订单信息", path: "/PersonalCenter/OrderInfo", icon: "el-icon-s-goods" },
      ],
    }
  },
  computed: {
    activePath() {
      return this.$route.path;
    }
  },
}
</script>

<template>
  <div class="container">
    <div class="navigation" style="background: paleturquoise">
      <div>
        <h1 class="el-icon-s-custom"
            style="margin-left: 20px;font-size: 25px;margin-top: 20px;margin-bottom: 15px;">个人中心</h1>
      </div>
      <el-menu
          :default-active="activePath"
          class="nav-menu"
          :router="true"
          active-text-color="pink"
      >
        <el-menu-item
            :index="item.path"
            v-for="item in menuList"
            :key="item.id"
            class="menu-item"
        >
          <i :class="item.icon" style="font-size: 22px"></i>
          <b style="font-size: 20px">{{ item.name }}</b>
        </el-menu-item>
      </el-menu>
    </div>
    <div class="content">
      <router-view></router-view>
    </div>
  </div>
</template>

<style scoped>
.container {
  display: flex;
  width: 85%;
  margin: 20px auto;
  min-height: 90vh;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  height: 400px;
}

.navigation {
  width: 15%;
  border-right: 1px solid #e6e6e6;
}

.nav-menu {
  border-right: none;
  height: 100%;
}

.content {
  width: 85%;
  padding: 20px;
  background: #fff;
  border-left: 1px solid #e6e6e6;
}

.menu-item {
  height: 60px;
  line-height: 60px;
  display: flex;
  align-items: center;
  transition: all 0.3s;
}

/* 为容器添加更明显的边框效果 */
.navigation, .content {
  border: 1px solid #e6e6e6;
  box-sizing: border-box;
}
</style>
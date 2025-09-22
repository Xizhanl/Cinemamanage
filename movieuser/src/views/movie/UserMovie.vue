<script>
export default {
  data() {
    return {
      menuList: [
        { id: "1", name: "正在热映", path: "/UserMovie/OngoingMovie", icon: "el-icon-sunny" },
        { id: "2", name: "即将上映", path: "/UserMovie/UpcomingMovie", icon: "el-icon-time" },
      ],
    }
  },
  computed: {
    // 添加计算属性获取当前路由路径
    activePath() {
      // 获取当前路由的完整路径
      return this.$route.path;
    }
  },
}
</script>

<template>
  <div>
  <div class="header-full-width">
    <div class="nav-container">
      <el-menu
          :default-active="activePath"
          class="nav-menu"
          mode="horizontal"
          :router="true"
          background-color="#47464A"
          text-color="#999999"
          active-text-color="#EF4238"
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
  </div>
  <div class="main-content">
      <router-view></router-view>
  </div>
  </div>
</template>

<style scoped>
.header-full-width {
  width: 100%;
  background-color: #47464A;
}
/* 新增菜单项样式 */
.menu-item {
  padding: 0 40px !important; /* 增加左右内边距 */
  margin: 0 100px !important;  /* 增加外边距 */
}

/* 调整菜单整体样式 */
.nav-menu {
  border-bottom: none !important;
  display: inline-flex;
}
.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: center; /* 使菜单居中 */
}
.main-content{
  width: 70%;          /* 设置宽度为70% */
  margin: 0 auto;      /* 水平居中 */
}
</style>
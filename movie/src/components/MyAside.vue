<script>
export default {
  data(){
    return {
      menu: [
        {
          menuName: "影院管理",
          menuIcon: "el-icon-office-building",
          children: [
            { menuName: "影院信息管理", menuClick: "Cinema" }
          ]
        },
        {
          menuName: "影片管理",
          menuIcon: "el-icon-video-camera",
          children: [
            { menuName: "电影信息管理", menuClick: "MovieManage" },
            { menuName: "电影类别管理", menuClick: "MovieTypes" }
          ]
        },
        {
          menuName: "影厅管理",
          menuIcon: "el-icon-school",
          children: [
            { menuName: "影厅信息管理", menuClick: "Hall" },
            { menuName: "场次信息管理", menuClick: "Session" }
          ]
        },
        {
          menuName: "订单管理",
          menuIcon: "el-icon-tickets",
          children: [
            { menuName: "订单信息管理", menuClick: "Order" }
          ]
        },
        {
          menuName: "用户管理",
          menuIcon: "el-icon-user",
          children: [
            { menuName: "用户信息管理", menuClick: "UserManage" }
          ]
        },
        {
          menuName: "数据管理",
          menuIcon: "el-icon-s-data",
          children: [
            { menuName: "数据统计", menuClick: "Math" }
          ]
        }
      ]
    }
  },

  props:{
    isCollapse:Boolean,
    defaultActive: {
      type: String,
      default: '/Home' // 设置默认值为'/Home'
    }
  },
  mounted() {
  },
  computed: {
    normalizedDefaultActive() {
      // 确保路径以斜杠开头，并且与路由配置一致
      return this.defaultActive.startsWith('/') ? this.defaultActive : `/${this.defaultActive}`;
    }
  }
}
</script>

<template>
  <el-menu
      background-color="#808080"
      text-color="#fff"
      active-text-color="#ffd04b"
      style="height: 120vh;"
      :default-active="normalizedDefaultActive"
      :collapse="isCollapse"
      :collapse-transition="false"
      router
  >

    <el-menu-item index="/Home">
      <i class="el-icon-s-home"></i>
      <span slot="title">首页</span>
    </el-menu-item>

    <el-submenu v-for="(item,i) in menu" :key="i" :index="'submenu-'+i">
      <template slot="title">
        <i :class="item.menuIcon"></i>
        <span>{{ item.menuName }}</span>
      </template>

      <el-menu-item
          v-for="(subItem, subIndex) in item.children"
          :key="subIndex"
          :index="'/'+subItem.menuClick">
        <i :class="subItem.menuIcon || 'el-icon-menu'"></i>
        <span slot="title">{{ subItem.menuName }}</span>
      </el-menu-item>
    </el-submenu>

<!--    <el-menu-item :index="'/'+item.menuClick" v-for="(item,i) in menu" :key="i">-->
<!--      <i :class="item.menuIcon"></i>-->
<!--      <span slot="title">{{item.menuName}}</span>-->
<!--    </el-menu-item>-->

<!--    <el-menu-item index="/MovieManage">-->
<!--      <i class="el-icon-video-camera-solid"></i>-->
<!--      <span slot="title">电影管理</span>-->
<!--    </el-menu-item>-->

  </el-menu>
</template>

<style scoped>

</style>
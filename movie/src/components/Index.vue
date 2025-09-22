<script>
import MyAside from "@/components/MyAside.vue";
import Header from "@/components/Header.vue";

export default {
  name:"Index",
  components:{Header, MyAside},
  data(){
    return {
      isCollapse:false,
      aside_width:'200px',
      icon:'el-icon-s-fold',
      defaultActive: this.$route.path
    }
  },
  mounted() {
    this.defaultActive = this.$route.path; // 确保刷新时正确设置
  },
  watch: {
    '$route'(to) {
      this.defaultActive = to.path; // 路由变化时更新 defaultActive
    }
  },

  methods: {
    doCollapse() {
      this.isCollapse = !this.isCollapse;
      if (!this.isCollapse) {
        this.aside_width = '200px';
        this.icon = 'el-icon-s-fold';
      } else {
        this.aside_width = '69px';
        this.icon = 'el-icon-s-unfold';
      }
    },
  }
}
</script>

<template>

  <el-container style="height: 100%; border: 1px solid #eee">
    <el-aside :width="aside_width" style="background-color: rgb(238, 241, 246);height: 100%;" >
      <MyAside :isCollapse="isCollapse" :defaultActive="defaultActive"></MyAside>
    </el-aside>

    <el-container style="height: 100%">
      <el-header style="text-align: right; font-size: 12px;height: 100%;border-bottom: #B3C0D1 1px solid">
        <Header @doCollapse="doCollapse" :icon="icon"></Header>
      </el-header>

      <el-main style="height: 100%">
        <router-view/>
      </el-main>
    </el-container>

  </el-container>
</template>

<style scoped>
  .el-header {
    color: #333;
    line-height: 60px;
  }
  .el-main{
    padding: 5px;
  }
  .el-aside {
    color: #333;
  }
</style>
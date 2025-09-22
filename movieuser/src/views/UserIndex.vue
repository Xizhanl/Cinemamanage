<script >
export default {
  name: "UserIndex",
  data() {
    return {
      activeUrl: this.getActiveUrl(),
      moviename:'',
      menuList: [
        {
          id: 1,
          name: "首页",
          path: "/UserHome",
        },
        {
          id: 2,
          name: "影片",
          path: "/UserMovie",
        },
        {
          id: 3,
          name: "榜单",
          path: "/UserRanking",
        },
        {
          id: 4,
          name: "关于影院",
          path: "/UserAbout",
        },
      ],
    };
  },
  methods:{
    getActiveUrl() {
      const path = this.$route.path;
      return path.substring(
          0,
          path.indexOf("/", 1) === -1 ? path.length : path.indexOf("/", 1)
      );
    },
    toUser(){
      this.$router.push("/PersonalCenter");
    },
    toHome(){
      this.$router.push("/UserHome");
      this.activeUrl="/UserHome"
    },
    logout(){
      this.$confirm("您确定要退出登录吗?",'提示',{
        confirmButtonText:"确定",
        type:"warning",
        center:true
      })
          .then(()=>{
            this.$message({
              type:"success",
              message:"退出登录成功"
            })
            this.$router.push("/")
            sessionStorage.clear()
          })
          .catch(()=>{
            this.$message({
              type:'info',
              message:'已取消登录'
            })
          })
    },
    // 使用axios调用示例
    async searchMovies() {
      try {
        const response = await this.$axios.get(this.$httpUrl+'/movie/search', {
          params: {  // 需要将参数放在params对象内
            moviename: this.moviename
          }
        });
        if (response.status==200) {
          // 跳转到搜索结果页，并传递搜索结果
          this.$router.push({
            path: '/SearchMovie',
            query: {
              moviename: this.moviename,
              searchResults: JSON.stringify(response.data.data) // 假设返回数据在data.data中
            }
          });
        } else {
          this.$message.error(response.data.message || '搜索失败');
        }
      } catch (error) {
        this.$message.error('搜索出错: ' + error.message);
      }
    }
  },
  watch: {
    '$route': {
      handler: function() {
        this.activeUrl = this.getActiveUrl();
      },
      immediate: true
    }
  },
}
</script>

<template>
  <el-container>
    <el-header>
      <div class="header-container">
        <!-- 优化后的Logo部分 -->
        <div class="logo-link" @click="toHome">
          <div class="logo-wrapper">
            <img
                class="logo-image"
                src="@/assets/logo.png"
                alt="秋水影院Logo"
            >
            <span class="logo-text">秋水影院</span>
          </div>
        </div>

        <el-menu
            :default-active="activeUrl"
            class="nav-menu"
            mode="horizontal"
            :router="true"
            active-text-color="#ff4d4f"
            text-color="#000000"
        >
          <el-menu-item
              v-for="item in menuList"
              :key="item.id"
              :index="item.path"
              class="nav-item"
          >
            {{ item.name }}
          </el-menu-item>
        </el-menu>
        <!-- 简化后的搜索框 -->
        <div class="search-container">
          <el-input
              v-model="moviename"
              placeholder="搜索电影..."
              class="search-input"
              @keyup.enter.native="searchMovies"
          >
            <el-button
                slot="append"
                type="danger"
                icon="el-icon-search"
                @click="searchMovies"
                class="search-button"
            ></el-button>
          </el-input>
        </div>
        <div class="user-actions">
          <el-dropdown>
              <span class="dropdown-trigger">
                <el-avatar
                    src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"
                    class="user-avatar"
                ></el-avatar>
                <i class="el-icon-arrow-down arrow-icon"></i>
              </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item @click.native="toUser">个人资料</el-dropdown-item>
              <el-dropdown-item @click.native="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
    </el-header>


      <el-main>
        <router-view></router-view>
      </el-main>
      <el-footer   style="background: #47464A;height: 100px">
        <div class="footer" style="padding-bottom: 10px">
          版权归@ljc所有
        </div>
      </el-footer>
    </el-container>

</template>

<style scoped>
.el-header {
  height: 80px !important;
  border-bottom: 1px solid #e6e6e6;
  position: fixed; /* 添加固定定位 */
  top: 0; /* 固定在顶部 */
  left: 0; /* 从左侧开始 */
  width: 100%; /* 宽度占满整个视口 */
  background-color: white; /* 添加背景色，防止内容透出 */
  z-index: 1000; /* 确保header在其他内容之上 */
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1); /*添加阴影增强视觉效果 */
}
.el-main {
  padding-top: 100px !important; /* 根据header高度调整 */
  min-height: calc(100vh - 180px); /* 确保内容足够长时footer在底部 */
}
/* 头部容器 */
.header-container {
  width: 85%;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
}

/* Logo部分 */
.logo-link {
  text-decoration: none;
  display: flex;
  align-items: center;
}

.logo-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
  transition: all 0.3s ease;
}

.logo-wrapper:hover {
  transform: scale(1.03);
}

.logo-image {
  height: 50px;  /* 增大Logo尺寸 */
  width: auto;
  object-fit: contain;
}

.logo-text {
  font-family: 'Microsoft YaHei', 'PingFang SC', sans-serif;
  font-size: 32px;
  font-weight: 700;
  color: #333;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.1);
  letter-spacing: 2px;
  background: linear-gradient(to right, #ff4d4f, #1890ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  position: relative;
}

.logo-text::after {
  content: '';
  position: absolute;
  bottom: -5px;
  left: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(to right, #ff4d4f, #1890ff);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.logo-link:hover .logo-text::after {
  transform: scaleX(1);
}

/* 导航菜单 */
.nav-menu {
  flex: 1;
  display: flex;
  justify-content: center;
  border-bottom: none !important;
  height: 100%;
}

.nav-item {
  font-size: 18px;
  font-weight: 500;
  margin: 0 15px !important;
  padding: 0 20px !important;
  height: 100% !important;
  line-height: 80px !important;
  transition: all 0.3s ease !important;
}

.nav-item:hover {
  color: #ff4d4f !important;
  background-color: rgba(255, 77, 79, 0.05) !important;
  transform: translateY(-2px);
}


.search-container {
  display: flex;
  padding: 20px 0;
  max-width: 500px;
  margin-right: 30px;
}

.search-input >>> .el-input__inner {
  border-radius: 20px 0 0 20px;
  border-right: none;
}

.search-input >>> .el-input-group__append {
  border-radius: 0 20px 20px 0;
  background: #79bbff;
  border: none;
  padding: 0 15px;
  color: #009688;

}

/* 用户操作区 */
.user-actions {
  display: flex;
  align-items: center;
}

.user-avatar {
  cursor: pointer;
  transition: all 0.3s ease;
}

.user-avatar:hover {
  transform: scale(1.1);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.arrow-icon {
  margin-left: 5px;
  color: #666;
  transition: all 0.3s ease;
}

.footer {
  margin-top: 40px;
  display: flex;
  justify-content: center;
}
.dropdown-trigger:hover .arrow-icon {
  color: #ff4d4f;
}

/* 响应式调整 */
@media (max-width: 992px) {
  .header-container {
    width: 90%;
  }

  .logo-text {
    font-size: 28px;
  }

  .logo-image {
    height: 40px;
  }

  .nav-item {
    font-size: 16px;
    margin: 0 10px !important;
    padding: 0 15px !important;
  }
}

@media (max-width: 768px) {
  .logo-text {
    display: none;
  }
  .nav-item {
    font-size: 14px;
    margin: 0 5px !important;
    padding: 0 10px !important;
  }
}
</style>
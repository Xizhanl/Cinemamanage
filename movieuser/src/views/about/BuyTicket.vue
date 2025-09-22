<script>
import axios from "axios";

export default {
  data(){
    return{
      selectedMovie: null,
      sessionlist:[],
      movielist:[],
      cinema:{
        name: "",
        address: "",
        phone: "",
        imageUrl: "",
        services: []
      },
      filteredSessions: [], // 用于存储过滤后的场次
      showToday: true, //控制显示今天/明天的场次
    }
  },
  methods:{
    async GetSession(moviename = null){
      const res = await axios.get(this.$httpUrl+"/sessions/list")
      let sessions = res.data;
      if (moviename) {
        sessions = sessions.filter(session => session.moviename === moviename);
      }
      this.sessionlist=sessions.map(session=>({
        id:session.id,
        moviename:session.moviename,
        hallname:session.hallname,
        screeningtime:session.screeningtime,
        duration:session.duration,
        endtime:session.endtime,
        price:session.price,
        totalseats:session.totalseats,
        seatssold:session.seatssold,
        seatsremaining:session.seatsremaining,
        notice:session.notice,
        seatstatus:session.seatstatus
      }))
    },
    async GetMovieList(){
      const res = await axios.get(this.$httpUrl+"/movie/ongoing")
      this.movielist = res.data.data.map(movie=>({
        id:movie.id,
        moviename:movie.moviename,
        movieduration:movie.movieduration ,
        movietype:movie.movietype,
        moviecover:movie.moviecover,
        moviebox:movie.moviebox,
        rating: this.calculateRating(movie),
      }));
      return res.data.data
    },
    async Getcinema(){
      const res = await axios.get(this.$httpUrl+"/cinema/list")
      const cinemaData=res.data[0];
      this.cinema={
        name: cinemaData.name,
        address: cinemaData.address,
        phone: cinemaData.phone,
        imageUrl: cinemaData.imageUrl,
        services: [
          { name: "免费停车", desc: "观影可享3小时免费停车" },
          { name: "可乐爆米花", desc: "购买套餐享8折优惠" },
          { name: "儿童乐园", desc: "带小孩观众可免费使用" },
          { name: "WIFI", desc: "WIFI全覆盖" }
      ]}
    },
    scrollLeft() {
      this.$refs.movieList.scrollBy({
        left: -900, // 增大滚动距离
        behavior: 'smooth'
      });
    },
    scrollRight() {
      this.$refs.movieList.scrollBy({
        left: 900, // 增大滚动距离
        behavior: 'smooth'
      });
    },
    // 选择电影
    selectMovie(movieId) {
      this.selectedMovie = this.movielist.find(movie => movie.id === movieId);
      this.GetSession(this.selectedMovie.moviename).then(()=>{
        this.filterSessions();
      });
    },
    chooseSeat(session){
      this.$store.commit("setSelectedMovie",this.selectedMovie);
      this.$store.commit("setSelectedSession",session)
      this.$router.push({
        path: "/ChooseSeat",
        query: {
          id: session.id,
          moviename:this.selectedMovie.moviename
        }
      });
    },
    calculateRating(movie) {
      const boxOffice = parseFloat(movie.moviebox);
      const baseRating = 7.5;
      const ratingIncrement = Math.min(boxOffice * 0.01, 2.0); // 最多增加2分
      let rating = baseRating + ratingIncrement;
      rating = Math.min(rating, 9.5);
      return rating.toFixed(1);
    },
    // 从URL参数中获取电影ID
    getMovieIdFromRoute() {
      // 尝试从query参数获取
      if (this.$route.query && this.$route.query.id) {
        return this.$route.query.id;
      }
      // 尝试从params参数获取
      if (this.$route.params && this.$route.params.id) {
        return this.$route.params.id;
      }
      return null;
    },
    filterSessions(){
      const today = new Date().toISOString().split("T")[0];
      const tomorrw = new Date(Date.now() + 86400000).toISOString().split("T")[0];
      this.filteredSessions = this.sessionlist.filter(session=>{
        const sessionDate = session.screeningtime.split("  ")[0];
        return this.showToday? sessionDate === today : sessionDate === tomorrw
      })
    },
    toggleDay(showToday){
      this.showToday = showToday;
      this.filterSessions();
    }
  },
  created() {
    this.GetSession()
    this.Getcinema()
    this.GetMovieList().then(() => {
      if (this.movielist.length > 0) {
        const movieId = this.getMovieIdFromRoute();
        if (movieId) {
          // 如果传入了电影ID，查找对应的电影
          const movie = this.movielist.find(m => m.id == movieId);
          if (movie) {
            this.selectedMovie = movie;
            this.GetSession(movie.moviename).then(()=>{
              this.filterSessions();
            });
            return;
          }
        }
        // 如果没有传入电影ID或找不到对应电影，默认显示第一个
        this.selectedMovie = this.movielist[0];
        this.GetSession(this.movielist[0].moviename).then(()=>{
          this.filterSessions();
        });
      }
  });
  }
}
</script>

<template>
  <div>
    <div class="body">
      <div class="body-navigate">
        <el-breadcrumb separator-class="el-icon-arrow-right">
          <el-breadcrumb-item :to="{ path: '/UserHome' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item :to="{ path: '/UserAbout' }">关于</el-breadcrumb-item>
          <el-breadcrumb-item :to="{ path: '/BuyTicket' }">购票</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
      <div class="movie-section">
        <h2 class="section-title">正在热映</h2>
        <div class="movie-list-container">
          <div class="movie-list-wrapper">
            <div class="movie-list" ref="movieList">
              <div
                  class="movie-item"
                  v-for="(item, index) in movielist"
                  :key="index"
                  @click="selectMovie(item.id)"
              >
                <div class="movie-poster">
                  <img :src="item.moviecover" :alt="item.moviename">
                </div>
                <div class="movie-desc">
                  <h5 class="movie-title">{{item.moviename}}</h5>
                </div>
              </div>
            </div>
          </div>
          <button class="scroll-btn prev-btn" @click="scrollLeft">
            <i class="el-icon-arrow-left"></i>
          </button>
          <button class="scroll-btn next-btn" @click="scrollRight">
            <i class="el-icon-arrow-right"></i>
          </button>
        </div>
        <div class="movie-detail-container" v-if="selectedMovie">
          <div class="movie-detail">
            <!-- 电影基本信息 -->
            <div class="movie-header">
              <h2 class="movie-title2">{{selectedMovie.moviename}}</h2>
              <div class="movie-meta">
                <span class="meta-type" style="margin-right: 70px">
                  <span style="opacity: 0.6">类型:</span>
                  {{selectedMovie.movietype.join(" / ")}}
                </span>
                <span class="meta-duration">
                  <span style="opacity: 0.6">电影时长:</span>
                  {{selectedMovie.movieduration}}分钟
                </span>
                <span class="meta-rating">
                  <span>评分:</span>
                  {{selectedMovie.rating}}分
                </span>
              </div>
            </div>
            <div class="sessions">
              <div class="session-title">
                <h2 style="opacity: 0.8">场次信息</h2>
                <div class="day-toggle">
                  <el-radio-group v-model="showToday" @change="toggleDay">
                    <el-radio-button :label="true">今日场次</el-radio-button>
                    <el-radio-button :label="false">明日场次</el-radio-button>
                  </el-radio-group>
                </div>

              </div>
              <div class="session-content">
                <div v-if="filteredSessions.length > 0" class="session-grid">
                  <div v-for="(session, index) in filteredSessions" :key="index" class="session-card">
                    <div class="session-time">{{ session.screeningtime }}</div>
                    <div class="session-hall" style="color: #ef4238;font-size: 20px">放映时间:{{session.duration}}</div>
                    <div class="session-hall">闭幕:{{session.endtime}}</div>
                    <div class="session-hall" style="color: #9900ff">{{ session.hallname }}</div>
                    <div class="session-price">¥{{ session.price }}</div>
                    <div class="session-seats">剩余座位: {{ session.seatsremaining }}/{{ session.totalseats }}</div>
                    <button class="session-book" @click="chooseSeat(session)">购票</button>
                  </div>
                </div>
                <div v-else class="no-sessions">
                  暂无{{showToday ? '今日' : '明日'}}的场次信息
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.body{
  width: 80%;
  margin: 0 auto; /* 水平居中 */
  align-items: center;
}
.body-navigate{
  margin-bottom: 30px;
}
/* 调整面包屑导航字体大小 */
:deep(.el-breadcrumb) {
  font-size: 18px;
  margin-left: 10px;
  margin-top: 20px;
}

/* 调整面包屑项的字体大小 */
:deep(.el-breadcrumb__item) {
  font-size: 18px;
}

/* 调整面包屑分隔符的字体大小 */
:deep(.el-breadcrumb__separator) {
  font-size: 18px;
}

.movie-section {
  margin: 20px 0 40px;
  padding: 20px;
  border: 2px solid #e4e7ed; /* 为整个电影列表区域添加边框 */
  border-radius: 12px; /* 边框圆角 */
  background-color: #fff; /* 白色背景 */
}

.section-title {
  font-size: 24px; /* 增大标题字体 */
  font-weight: bold;
  margin: 0 0 20px 10px; /* 调整标题边距 */
  color: #333;
  padding-bottom: 8px;
  border-bottom: 1px solid #eee; /* 标题下方添加分割线 */
}

.movie-list-container {
  position: relative;
  padding: 0 60px; /* 增大两侧内边距，为更大的按钮留空间 */
}

.movie-list-wrapper {
  overflow: hidden;
  padding: 5px 0; /* 上下内边距 */
}

.movie-list {
  display: flex;
  gap: 25px; /* 增大电影项之间的间距 */
  padding: 15px 10px; /* 增大内边距 */
  overflow-x: auto;
  scroll-behavior: smooth;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: none; /* Firefox隐藏滚动条 */
}

.movie-list::-webkit-scrollbar {
  display: none; /* Chrome/Safari隐藏滚动条 */
}

.movie-item {
  flex: 0 0 180px; /* 增大电影项宽度 */
  cursor: pointer;
  transition: transform 0.3s ease-out; /* 平滑过渡效果 */
}

.movie-item:hover {
  transform: translateY(-8px) scale(1.02); /* 悬停时有更大上浮效果 */
}

.movie-poster {
  position: relative;
  width: 200px; /* 宽度填满容器 */
  height: 250px; /* 增大海报高度 */
  border-radius: 10px; /* 增大圆角 */
  overflow: hidden;
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15); /* 增大阴影 */
  border: 2px solid #f0f2f5; /* 为海报添加边框 */
  transition: all 0.3s ease; /* 所有属性都有过渡效果 */
}

.movie-poster:hover {
  border-color: #409EFF; /* 悬停时边框变色 */
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2); /* 悬停时阴影更大 */
}

.movie-poster img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease; /* 图片变换过渡 */
}

.movie-poster:hover img {
  transform: scale(1.05); /* 悬停时图片轻微放大 */
}
.movie-desc {
  padding: 12px 5px; /* 增大内边距 */
  text-align: center; /* 文字居中 */
}

.movie-title {
  font-size: 15px; /* 增大字体 */
  margin: 6px 0; /* 调整边距 */
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: #333; /* 深色文字 */
}
.scroll-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 50px; /* 增大按钮宽度 */
  height: 50px; /* 增大按钮高度 */
  border-radius: 50%;
  background: white;
  border: none;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15); /* 增大阴影 */
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2; /* 提高层级确保在列表上方 */
  transition: all 0.3s ease; /* 平滑过渡 */
}

.scroll-btn:hover {
  background: #409EFF; /* 悬停时背景变色 */
  transform: translateY(-50%) scale(1.1); /* 悬停时轻微放大 */
}

.scroll-btn:hover i {
  color: white; /* 悬停时箭头变白 */
}

.prev-btn {
  left: 0px; /* 调整左侧按钮位置 */
}

.next-btn {
  right: 0px; /* 调整右侧按钮位置 */
}

.scroll-btn i {
  font-size: 24px; /* 增大箭头图标 */
  color: #409EFF; /* 使用主题色 */
  transition: color 0.3s ease; /* 颜色过渡效果 */
}

/* 电影详情区域样式 */
.movie-title2{
  font-size: 30px;
  margin-bottom: 15px;
}
.movie-meta{
  gap: 60px;
}
.meta-rating{
  margin-left: 100px;
  color: #E6A23C;
  font-size: 20px;
  font-weight: bold;
}
.session-title{
  margin-top: 15px;
}

.session-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 15px;
  margin-top: 15px;
}

.session-card {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.session-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.session-time {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 8px;
}

.session-hall {
  color: #666;
  margin-bottom: 5px;
  font-size: 20px
}

.session-price {
  font-size: 20px;
  color: #e74c3c;
  font-weight: bold;
  margin-bottom: 5px;
}

.session-seats {
  color: #666;
  font-size: 20px;
  margin-bottom: 10px;
}

.session-book {
  width: 100%;
  padding: 8px 0;
  background: #ef4238;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.3s;
}

.session-book:hover {
  background:#f56c6c;
}

.no-sessions {
  text-align: center;
  color: #999;
  padding: 20px;
}
.day-toggle {
  margin-left: 20px;
  display: inline-block;
}

.session-title {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.el-radio-group {
  margin-left: 15px;
}

.el-radio-button {
  --el-radio-button-checked-bg-color: #ef4238;
  --el-radio-button-checked-text-color: #fff;
  --el-radio-button-checked-border-color: #ef4238;
}

.el-radio-button:first-child .el-radio-button__inner {
  border-radius: 4px 0 0 4px;
}

.el-radio-button:last-child .el-radio-button__inner {
  border-radius: 0 4px 4px 0;
}
</style>
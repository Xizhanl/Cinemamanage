<script>
import axios from "axios";
export default {
  data(){
    return{
      ongoingMovieList:[],
      upcomingMovieList:[],
    }
  },
  // 在script部分添加计算属性
  computed: {
    sortedByBoxOffice() {
      // 按票房从高到低排序
      return [...this.ongoingMovieList]
          .sort((a, b) => {
            const boxA = parseFloat(a.boxOffice);
            const boxB = parseFloat(b.boxOffice);
            return boxB - boxA;
          })
          .slice(0, 10); // 只显示前10名
    },
    sortedBy() {
      return [...this.upcomingMovieList]
          .sort((a, b) => {
            const boxA = parseFloat(a.boxOffice);
            const boxB = parseFloat(b.boxOffice);
            return boxB - boxA;
          })
          .slice(0, 10); // 只显示前10名
    }
  },
  methods:{
    tomovieinfo(movieId){
      this.$router.push({
        path:"/MovieInfo",
        query:{id:movieId}})
    },
    buyticket(movieId) {
      this.$router.push({
        path:"/BuyTicket",
        query:{id:movieId}})
    },
    async getOngoingMovieList() {
      try {
        const response = await axios.get(`${this.$httpUrl}/movie/ongoing`);
        if (response.data && response.data.code === 200 && Array.isArray(response.data.data)) {
          this.ongoingMovieList = response.data.data.map(movie => ({
            id: movie.id,
            title: movie.moviename,
            duration: movie.movieduration + '分钟',
            type:  movie.movietype,
            showTime: movie.movietime,
            description: movie.describes,
            poster: movie.moviecover,
            boxOffice: movie.moviebox,
            rating: this.calculateRating(movie),
            wishCount: this.generateWishCount(movie)
          }));
        } else {
          throw new Error(response.data.msg || '返回数据格式不正确');
        }
      } catch (error) {
        console.error('获取电影列表失败:', error);
        this.error = '获取电影列表失败: ' + (error.message || '未知错误');
        this.ongoingMovieList = [];
      } finally {
        this.loading = false;
      }
    },
    async getUpcomingMovieList() {
      try {
        const response = await axios.get(`${this.$httpUrl}/movie/upcoming`);
        if (response.data && response.data.code === 200 && Array.isArray(response.data.data)) {
          this.upcomingMovieList = response.data.data.map(movie => ({
            id: movie.id,
            title: movie.moviename,
            duration: movie.movieduration + '分钟',
            type:  movie.movietype,
            showTime: movie.movietime,
            description: movie.describes,
            poster: movie.moviecover,
            boxOffice: movie.moviebox,
            rating: this.calculateRating(movie),
            wishCount: this.generateWishCount(movie)
          }));
        } else {
          throw new Error(response.data.msg || '返回数据格式不正确');
        }
      } catch (error) {
        console.error('获取电影列表失败:', error);
        this.error = '获取电影列表失败: ' + (error.message || '未知错误');
        this.upcomingMovieList = [];
      } finally {
        this.loading = false;
      }
    },
    calculateRating(movie) {
      // 从票房字符串中提取数字（如"3.2亿" -> 3.2）
      const boxOffice = parseFloat(movie.moviebox);
      // 票房每增加1亿，评分增加0.05分（票房影响有限）
      // 最高不超过9.5分（避免所有高票房电影都接近满分）
      const baseRating = 7.5;
      const ratingIncrement = Math.min(boxOffice * 0.01, 2.0); // 最多增加2分
      let rating = baseRating + ratingIncrement;
      rating = Math.min(rating, 9.5);
      return rating.toFixed(1);
    },
    generateWishCount(movie) {
      // 从票房字符串中提取数字（如"3.2亿" -> 3.2）
      const boxOffice = parseFloat(movie.moviebox);
      // 基础想看人数算法：
      // - 最低1000人
      // - 票房每增加1亿，想看人数增加10000人
      const baseCount = 100;
      const countIncrement = boxOffice * 100;
      const totalCount = baseCount + countIncrement;
      // 格式化为整数
      return Math.floor(totalCount);
    },
    formatDuration(duration) {
      if (typeof duration === 'number') {
        return `${duration}分钟`;
      }
      return duration;
    },
  },
  filters: {
    formatNumber(value) {
      if (value === null || value === undefined) return '0';
      // 如果已经是格式化过的字符串（包含"万"或","），直接返回
      if (typeof value === 'string' && (value.includes('万') || value.includes(','))) {
        return value;
      }
      // 转换为数字
      const num = typeof value === 'string' ? parseFloat(value) : value;
      if (isNaN(num)) return '0';
      // 大于等于1万时，显示为"x.x万"格式
      if (num >= 10000) {
        return (num / 10000).toFixed(1) + '万';
      }
      // 小于1万时，添加千位分隔符
      return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    }
  },
  created() {
    this.getOngoingMovieList()
    this.getUpcomingMovieList()
  }
}
</script>

<template>
  <div class="main">
    <div class="left">
      <div class="panel">
        <div class="panel-header">
          <div class="panel-title hot">
            <i class="el-icon-sunny"></i>
            <b> 正在热映中({{ ongoingMovieList.length }}部)</b>
          </div>
          <router-link to="/UserMovie/OngoingMovie" style="color: #ef4238">全部</router-link>
        </div>
        <div class="panel-content">
          <div class="movie-grid" >
            <div class="movie-card" v-for="movie in ongoingMovieList.slice(0, 8)" :key="movie.id" @click="tomovieinfo(movie.id)">
              <div class="movie-poster">
                <img :src="movie.poster" :alt="movie.title">
                <div class="movie-tags">
                  <span v-for="(type, index) in movie.type" :key="index">{{ type }}</span>
                </div>
                <div class="movie-rating">
                  <span>{{ movie.rating }}</span>
                </div>
              </div>
              <div class="movie-info">
                <h3 class="movie-title">{{ movie.title }}</h3>
                <div class="movie-meta">
                  <span class="wish-count">
                    <i class="el-icon-star-off"></i>
                    {{ movie.wishCount | formatNumber }}人想看
                  </span>
                  <span class="duration">
                    <i class="el-icon-time"></i>
                    {{ formatDuration(movie.duration) }}
                  </span>
                </div>
                <p class="movie-show-time">
                  <i class="el-icon-date"></i>
                  上映: {{ movie.showTime }}
                </p>
                <div class="ticket-buttons">
                  <el-button type="danger" size="small" @click="buyticket(movie.id)">购票</el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="panel">
        <div class="panel-header">
          <div class="panel-title upcoming">
            <i class="el-icon-time"></i>
            <b> 即将上映({{ upcomingMovieList.length }}部)</b>
          </div>
          <router-link to="/UserMovie/UpcomingMovie" style="color: #2d98f3">全部</router-link>
        </div>
        <div class="panel-content">
          <div class="movie-grid">
            <div class="movie-card" v-for="movie in upcomingMovieList.slice(0, 8)" :key="movie.id" @click="tomovieinfo(movie.id)">
              <div class="movie-poster">
                <img :src="movie.poster" :alt="movie.title">
                <div class="movie-tags">
                  <span v-for="(type, index) in movie.type" :key="index">{{ type }}</span>
                </div>
                <div class="movie-rating">
                  <span>{{ movie.rating }}</span>
                </div>
              </div>
              <div class="movie-info">
                <h3 class="movie-title">{{ movie.title }}</h3>
                <div class="movie-meta">
                  <span class="wish-count">
                    <i class="el-icon-star-off"></i>
                    {{ movie.wishCount | formatNumber }}人想看
                  </span>
                  <span class="duration">
                    <i class="el-icon-time"></i>
                    {{ formatDuration(movie.duration) }}
                  </span>
                </div>
                <p class="movie-show-time">
                  <i class="el-icon-date"></i>
                  上映: {{ movie.showTime }}
                </p>
                <div class="ticket-buttons">
                  <el-button type="danger" size="small" @click="tomovieinfo(movie.id)">查看电影详细</el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="right">
      <div class="panel">
        <div class="panel-header" style="align-items: center;">
          <div style="color: #ffb400;font-size: 28px; display: flex; align-items: center;">
            <i class="iconfont icon-r-paper" style="font-size: 38px; margin-right: 10px;"></i>
            <b>票房榜</b>
          </div>
          <router-link to="/UserRanking" style="color: #ffb400; margin-top: 0;">查看完整榜单</router-link>
        </div>
        <div class="panel-content">
          <div class="box-office-list">
            <div class="box-office-item" v-for="(movie, index) in sortedByBoxOffice" :key="movie.id" @click="tomovieinfo(movie.id)">
              <div class="rank" :class="{top3: index < 3}">{{ index + 1 }}</div>
              <div class="movie-info">
                <h3 class="movie-title">{{ movie.title }}</h3>
                <p class="box-office">票房：{{ movie.boxOffice }}</p>
              </div>
              <img class="poster" :src="movie.poster" :alt="movie.title">
            </div>
          </div>
        </div>
      </div>

      <div class="panel">
        <div class="panel-header" style="align-items: center;">
          <div style="color: #85ce61;font-size: 28px; display: flex; align-items: center;">
            <i class="iconfont icon-r-paper" style="font-size: 38px; margin-right: 10px;"></i>
            <b>最受期待</b>
          </div>
          <router-link to="/UserRanking" style="color: #85ce61; margin-top: 0;">查看完整榜单</router-link>
        </div>
        <div class="panel-content">
          <div class="box-office-list">
            <div class="box-office-item" v-for="(movie, index) in sortedBy" :key="movie.id" @click="tomovieinfo(movie.id)">
              <div class="rank" :class="{top3: index < 3}">{{ index + 1 }}</div>
              <div class="movie-info">
                <h3 class="movie-title">{{ movie.title }}</h3>
                <p class="box-office">{{ movie.wishCount | formatNumber}}人想看</p>
              </div>
              <img class="poster" :src="movie.poster" :alt="movie.title">
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.main {
  width: 1200px;
  margin: 30px auto;
  display: flex;
}

.left {
  width: 70%;
}

.right {
  width: 30%;
  margin-left: 80px;
}
.right .panel {
  margin-bottom: 30px;
}

.right .panel-header {
  padding: 0 10px;
}

.right .panel-content {
  padding: 10px;
}

.panel {
  margin-bottom: 40px;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.panel-title {
  font-size: 28px;
  display: flex;
  align-items: center;
}

.panel-title i {
  font-size: 32px;
  margin-right: 10px;
}

.panel-title.hot {
  color: #ef4238;
}

.panel-title.upcoming {
  color: #2d98f3;
}

.panel-header > a {
  text-decoration: none;
  padding-right: 14px;
  background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAgAAAAOCAYAAAASVl2WAAAABmJLR0QA/wD/AP+gvaeTAAAAv0lEQVQY013RTUpDQRAE4G8eghcR8ScgKCIugpJFjuIjqAvBc7jxj0muEnCjiIQQJOImB3GnbnpkfL1qpqqrunpSzvkDPxjhGdq2VarBF3q4wRHknP8RzvCEQzzguCalaHZwiwHecY6XogCf8TjFHh7Rh9Tx3AylIZa4TgWpSBuY4BSrYlFXKsr4bjrTW5HkJJa9SBW4jbtukmKxG5MDLOKqfzEPcB9LzQN8LSdfwxj7eMMlZvV/NFiPzFddEH4Bt5Y1mf3fnDwAAAAASUVORK5CYII=) no-repeat 100%;
}

.panel-header > a.hot {
  color: #ef4238;
}

.panel-header > a.upcoming {
  color: #2d98f3;
}

.movie-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  grid-gap: 20px;
}

.movie-card {
  background: #fff;
  border-radius: 4px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.movie-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}

.movie-poster {
  position: relative;
  height: 0;
  padding-bottom: 140%;
  overflow: hidden;
}

.movie-poster img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.movie-card:hover .movie-poster img {
  transform: scale(1.05);
}

.movie-tags {
  position: absolute;
  bottom: 10px;
  left: 0;
  width: 100%;
  display: flex;
  flex-wrap: wrap;
  padding: 0 10px;
  box-sizing: border-box;
}

.movie-tags span {
  background: rgba(0, 0, 0, 0.6);
  color: #fff;
  font-size: 12px;
  padding: 2px 5px;
  border-radius: 2px;
  margin-right: 5px;
  margin-bottom: 5px;
}

.movie-rating {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(255, 255, 255, 0.9);
  color: #ffb400;
  font-weight: bold;
  font-size: 14px;
  padding: 3px 8px;
  border-radius: 4px;
}

.movie-info {
  padding: 15px;
}

.movie-title {
  font-size: 16px;
  font-weight: bold;
  margin: 0 0 8px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.movie-description {
  font-size: 13px;
  color: #666;
  margin: 0 0 10px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  height: 36px;
  line-height: 18px;
}

.movie-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #999;
  margin-bottom: 10px;
}

.movie-meta span {
  display: flex;
  align-items: center;
}

.movie-meta i {
  margin-right: 3px;
}

.movie-show-time {
  font-size: 12px;
  color: #666;
  margin: 0 0 10px 0;
  display: flex;
  align-items: center;
}

.movie-show-time i {
  margin-right: 5px;
}

.ticket-buttons {
  text-align: center;
}

.el-button {
  width: 100%;
}
/* 在style部分添加票房榜样式 */
.box-office-list {
  background: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.box-office-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;
  transition: background-color 0.3s;
}

.box-office-item:last-child {
  border-bottom: none;
}

.box-office-item:hover {
  background-color: #f9f9f9;
}

.rank {
  width: 24px;
  height: 24px;
  line-height: 24px;
  text-align: center;
  background: #f0f0f0;
  color: #999;
  border-radius: 4px;
  margin-right: 15px;
  font-weight: bold;
}

.rank.top3 {
  background: #ffb400;
  color: #fff;
}

.box-office-item .movie-info {
  flex: 1;
  min-width: 0;
}

.box-office-item .movie-title {
  font-size: 16px;
  margin: 0 0 5px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.box-office-item .box-office {
  font-size: 12px;
  color: #ffb400;
  margin: 0;
  font-weight: bold;
}

.box-office-item .poster {
  width: 40px;
  height: 55px;
  object-fit: cover;
  border-radius: 2px;
  margin-left: 15px;
}


@media (max-width: 1200px) {
  .main {
    width: 100%;
    padding: 0 15px;
    box-sizing: border-box;
  }

  .left {
    width: 100%;
  }
  .right {
    width: 35%;
    margin-left: 20px;
  }
  .right .panel-header > div {
    font-size: 24px;
  }

  .right .panel-header i {
    font-size: 30px;
  }
  .movie-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .movie-grid {
    grid-template-columns: 1fr;
  }

  .panel-title {
    font-size: 22px;
  }

  .panel-title i {
    font-size: 26px;
  }
}
</style>
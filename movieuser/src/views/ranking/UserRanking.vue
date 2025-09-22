<script >
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
    },
    sortedBy() {
      return [...this.upcomingMovieList]
          .sort((a, b) => {
            const boxA = parseFloat(a.boxOffice);
            const boxB = parseFloat(b.boxOffice);
            return boxB - boxA;
          })
    }
  },
  methods:{
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
            boxOffice: movie.moviebox + '亿',
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
      // 基础评分7.5分（大多数电影的平均水平）
      // 票房每增加1亿，评分增加0.05分（票房影响有限）
      // 最高不超过9.5分（避免所有高票房电影都接近满分）
      const baseRating = 7.5;
      const ratingIncrement = Math.min(boxOffice * 0.05, 2.0); // 最多增加2分
      let rating = baseRating + ratingIncrement;
      // 添加随机波动（±0.3分），避免所有同票房电影评分相同
      const randomFactor = (Math.random() * 0.6 - 0.3);
      rating = Math.min(Math.max(rating + randomFactor, 7.0), 9.5);
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
    tomovieinfo(movieId){
      this.$router.push({
        path:"/MovieInfo",
        query:{id:movieId}})
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
    <div class="left-panel">
      <div class="panel">
        <div class="panel-header" >
          <div class="panel-title">
            <i class="iconfont icon-r-paper"></i>
            <b>票房Top榜</b>
          </div>
        </div>
        <div class="panel-content">
          <div class="box-office-list">
            <div class="box-office-item" v-for="(movie, index) in sortedByBoxOffice" :key="movie.id" @click="tomovieinfo(movie.id)">
              <div class="rank" :class="{top3: (index < 3 && index >0),top1:index===0}">{{ index + 1 }}</div>
              <img class="poster" :src="movie.poster" :alt="movie.title">
              <div class="movie-info">
                <h3 class="movie-title">{{ movie.title }}</h3>
                <p class="box-office">电影时长:{{ movie.duration}}</p>
                <p class="box-office">上映时间:{{ movie.showTime}}</p>
              </div>
              <p class="box-office-end">总票房：{{ movie.boxOffice }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="right-panel">
      <div class="panel">
        <div class="panel-header">
          <div class="panel-title">
            <i class="iconfont icon-r-paper"></i>
            <b>最受期待Top榜</b>
          </div>
        </div>
        <div class="panel-content">
          <div class="box-office-list">
            <div class="box-office-item" v-for="(movie, index) in sortedBy" :key="movie.id" @click="tomovieinfo(movie.id)">
              <div class="rank" :class="{top3: index < 3&& index >0,top1:index === 0 }">{{ index + 1 }}</div>
              <img class="poster" :src="movie.poster" :alt="movie.title">
              <div class="movie-info">
                <h3 class="movie-title">{{ movie.title }}</h3>
                <p class="box-office">电影时长:{{ movie.duration}}</p>
                <p class="box-office">上映时间:{{ movie.showTime}}</p>
              </div>
              <p class="box-office-end">{{ movie.wishCount | formatNumber}}人期待</p>
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
  justify-content: space-between;
}

.left-panel,
.right-panel {
  width: 48%;
}

.panel {
  margin-bottom: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.panel-header {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid #f0f0f0;
  background: #999999;
  border-radius: 8px 8px 0 0; /* 只圆润上边两个角 */
}

.panel-title {
  color: #E6A23C;
  font-size: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.panel-title i {
  font-size: 38px;
  margin-right: 15px;
}

.right-panel .panel-title {
  color: #E6A23C;
}

.panel-content {
  padding: 15px;
}

.box-office-list {
  border-radius: 4px;
}
.box-office-end{
  font-size:20px;
  color: #ef4238;
}
.box-office-item {
  display: flex;
  align-items: center;
  padding: 18px;
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
  width: 28px;
  height: 28px;
  line-height: 28px;
  text-align: center;
  background: #f0f0f0;
  color: #999;
  border-radius: 4px;
  margin-right: 20px;
  font-weight: bold;
  font-size: 16px;
}
.rank.top1{
  background: #ef4238;
  color: #fff;
  transform: scale(1.1); /* 稍微放大 */
  box-shadow: 0 2px 8px rgba(255, 77, 79, 0.3); /* 添加阴影效果 */
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
  margin: 0 0 8px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-weight: bold;
}

.box-office-item .box-office {
  margin: 0;
}

.box-office-item .poster {
  width: 70px;
  height: 100px;
  object-fit: cover;
  border-radius: 4px;
  margin-left: 20px;
  margin-right: 20px;
}

@media (max-width: 1200px) {
  .main {
    width: 100%;
    padding: 0 15px;
    flex-direction: column;
  }

  .left-panel,
  .right-panel {
    width: 100%;
    margin-bottom: 30px;
  }

  .panel-title {
    font-size: 24px;
  }

  .panel-title i {
    font-size: 30px;
  }

  .box-office-item .movie-title {
    font-size: 16px;
  }

  .box-office-item .box-office {
    font-size: 10px;
  }
}
</style>
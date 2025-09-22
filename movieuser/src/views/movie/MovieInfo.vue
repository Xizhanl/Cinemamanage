<script>
import axios from "axios";

export default {
  data() {
    return {
      movielist: {
        movietype:[]
      },
    };
  },
  methods: {
    async getMovieInfo() {
      const res = await axios.get(this.$httpUrl + "/movie/list");
      this.movielist = res.data.map(movie => ({
        id: movie.id,
        moviename: movie.moviename,
        movieduration: movie.movieduration,
        describes: movie.describes,
        movietime: movie.movietime,
        movietype: movie.movietype,
        moviecover: movie.moviecover,
        moviebox: movie.moviebox,
        rating: this.calculateRating(movie),
        wishCount: this.generateWishCount(movie),
        isReleased: this.checkReleaseStatus(movie.movietime) // 检查上映状态
      }));
      return res.data;
    },
    tobuyticket(movieId){
      this.$router.push({
        path:"/BuyTicket",
        query:{id:movieId}})
    },
    calculateRating(movie) {
      const boxOffice = parseFloat(movie.moviebox);
      const baseRating = 7.5;
      const ratingIncrement = Math.min(boxOffice * 0.01, 2.0);
      let rating = baseRating + ratingIncrement;
      rating = Math.min(rating, 9.5);
      return rating.toFixed(1);
    },
    generateWishCount(movie) {
      const boxOffice = parseFloat(movie.moviebox);
      const baseCount = 100;
      const countIncrement = boxOffice * 100;
      return Math.floor(baseCount + countIncrement);
    },
    checkReleaseStatus(releaseDate) {
      const today = new Date();
      today.setHours(0, 0, 0, 0); // 清除时间部分
      return new Date(releaseDate) <= today;
    }
  },
  created() {
    this.getMovieInfo().then(() => {
      if (this.movielist.length > 0) {
        const movieId = this.$route.query.id;
        if (movieId) {
          const movie = this.movielist.find(m => m.id == movieId);
          this.movielist = movie;
        }
      }
    });
  }
};
</script>

<template>
  <div class="movie-detail-container">
    <div class="movie-content">
      <!-- 电影头部信息区 -->
      <div class="movie-header">
        <div class="movie-poster">
          <img :src="movielist.moviecover"
               :alt="movielist.moviename"
               class="poster-img"
               loading="lazy">
        </div>

        <div class="movie-info">
          <h1 class="movie-title">{{ movielist.moviename }}</h1>

          <div class="movie-meta">
            <span class="meta-item" >{{ movielist.movietype.join(" / ") }}</span>
            <span class="meta-item">{{ movielist.movieduration }}分钟</span>
            <span class="meta-item">{{ movielist.movietime }} 9:00上映</span>
          </div>

          <div class="movie-rating-container">
            <div class="movie-score">
              <div class="score-info">
                <span class="score">{{ movielist.rating }}</span>
                <span class="score-text">评分</span>
              </div>
            </div>
            <div class="movie-wish">
              <div class="wish-count">{{ movielist.wishCount }}人想看</div>
            </div>

          </div>
        </div>
      </div>

      <!-- 主要操作区 -->
      <div class="movie-actions">
        <button v-if="movielist.isReleased"
                class="buy-btn primary-btn"
                @click="tobuyticket(movielist.id)">
          立即购票
        </button>
        <button v-else
                class="buy-btn disabled-btn"
                disabled>
          暂未上映
        </button>
      </div>

      <!-- 剧情简介区 -->
      <div class="movie-section">
        <h2 class="section-title">
          <i class="icon-story"></i>
          <span>剧情简介</span>
        </h2>
        <div class="section-content">
          <p class="movie-desc">{{ movielist.describes }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 基础样式 */
.movie-detail-container {
  max-width: 1200px;
  margin: 0 auto;
  background: #f8f8f8;
  min-height: 100vh;
  font-family: "PingFang SC", "Microsoft YaHei", sans-serif;
}

.movie-content {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  margin: 20px;
}

/* 头部信息区 */
.movie-header {
  display: flex;
  padding: 30px;
  background: linear-gradient(135deg, #ff9a9e 0%, #fad0c4 100%);
  position: relative;
  margin-bottom: 20px;
}

.movie-poster {
  width: 220px;
  height: 320px;
  margin-right: 30px;
  flex-shrink: 0;
  border-radius: 6px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transition: transform 0.3s ease;
}

.movie-poster:hover {
  transform: translateY(-5px);
}

.poster-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.poster-img:hover {
  transform: scale(1.03);
}

.movie-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.movie-title {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 10px;
  color: #333;
  text-shadow: 1px 1px 2px rgba(255, 255, 255, 0.5);
}

.movie-meta {
  margin-bottom: 20px;
  display: flex;
  flex-wrap: wrap;
  flex-direction: column; /* 垂直方向排列 */
  gap: 8px; /* 可选：设置子项间距 */
}

.meta-item {
  padding: 6px 12px;
  border-radius: 16px;
  font-size: 18px;
  font-weight: inherit;
}

/* 评分和想看 */
.movie-rating-container {
  display: flex;
  align-items: center;
  gap: 30px;
  margin-top: 20px;
}

.movie-score {
  display: flex;
  align-items: center;
}

.score-info {
  display: flex;
  align-items: baseline;
}

.score {
  font-size: 28px;
  color: #00A699;
  font-weight: bold;
  margin-right: 8px;
}

.score-text {
  font-size: 16px;
  color: #666;
}

.wish-count {
  font-size: 18px;
  color: #ff4757;
  font-weight: 500;
  padding: 8px 16px;
  border-radius: 20px;
}

/* 操作按钮区 */
.movie-actions {
  display: flex;
  gap: 20px;
  padding: 0 30px 30px;
}

.primary-btn {
  flex: 1;
  background: linear-gradient(to right, #ff5f6d, #ff7b54);
  color: white;
  border: none;
  border-radius: 6px;
  padding: 14px 0;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 8px rgba(255, 95, 109, 0.3);
}

.primary-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(255, 95, 109, 0.4);
}
.disabled-btn {
  flex: 1;
  background: #e0e0e0;
  color: #9e9e9e;
  border: none;
  border-radius: 6px;
  padding: 14px 0;
  font-size: 18px;
  font-weight: 600;
  cursor: not-allowed;
}
/* 剧情简介区 */
.movie-section {
  padding: 0 30px 30px;
}

.section-title {
  display: flex;
  align-items: center;
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #eee;
  color: #333;
}

.icon-story {
  display: inline-block;
  width: 24px;
  height: 24px;
  background: #ff5f6d;
  margin-right: 10px;
  border-radius: 4px;
}

.movie-desc {
  font-size: 18px;
  line-height: 1.8;
  color: #555;
  margin: 0;
  text-align: justify;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .movie-header {
    flex-direction: column;
    padding: 20px;
  }

  .movie-poster {
    width: 100%;
    height: auto;
    aspect-ratio: 2/3;
    margin-right: 0;
    margin-bottom: 20px;
  }

  .movie-actions {
    flex-direction: column;
    gap: 15px;
    padding: 0 20px 20px;
  }

  .movie-title {
    font-size: 24px;
  }

  .movie-meta {
    gap: 10px;
  }

  .movie-rating-container {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
}
</style>
<script>
import axios from "axios";

export default {
  data() {
    return {
      movielist: {
        movietype:[]
      },
      isLiked: false, // 收藏状态
      likeid:'',
      reviewlist:[],
      content:''
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
    async addlike() {
        const user =  JSON.parse(sessionStorage.getItem('CurUser'));
        const username = user.username

        if (this.isLiked) {
          // 取消收藏
          await this.$confirm('确定要取消收藏吗？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          });
          const likelistRes = await axios.get(this.$httpUrl + "/userlike/list");
          const likes = likelistRes.data;
          const like = likes.find(like =>
            like.username === username && like.moviename === this.movielist.moviename
          );
          this.likeid=like.id
          const res = await axios.get(this.$httpUrl + "/userlike/del?id="+this.likeid);
          if (res.data.code === 200) {
            this.isLiked = false;
            this.$message.success('已取消收藏');
          } else {
            this.$message.error('取消收藏失败');
          }
        } else {
          // 添加收藏
          const res = await axios.post(this.$httpUrl + "/userlike/save", {
            username: username,
            moviename: this.movielist.moviename,
            moviecover:this.movielist.moviecover
          });
          if (res.data.code === 200) {
            this.isLiked = true;
            this.$message.success('收藏成功');
          } else {
            this.$message.error('收藏失败');
          }
        }
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
    },
    // 检查是否已收藏
    async checkIfLiked() {
      try {
        const user =  JSON.parse(sessionStorage.getItem('CurUser'));
        const username = user.username
        
        const res = await axios.get(this.$httpUrl + "/userlike/list");
        const likes = res.data;
        this.isLiked = likes.some(like => 
          like.username === username && like.moviename === this.movielist.moviename
        );
      } catch (error) {
        console.error('检查收藏状态失败:', error);
      }
    },
    async getreviewlist(){
      const res = await axios.get(this.$httpUrl + "/reviews/list");
      this.reviewlist = res.data.filter(review => review.moviename === this.movielist.moviename);
    },
    async addreview(){
      const user =  JSON.parse(sessionStorage.getItem('CurUser'));
      const username = user.username
      const res = await axios.post(this.$httpUrl + "/reviews/save", {
        username: username,
        moviename: this.movielist.moviename,
        content:this.content
      });
      if (res.data.code === 200) {
        this.$message.success('评论成功');
        // 清空输入框
        this.content = '';
        // 重新获取影评列表
        await this.getreviewlist();
      } else {
        this.$message.error('评论失败');
      }
    },
    async delreview(id,username) {
      const user =  JSON.parse(sessionStorage.getItem('CurUser'));
      const currentuser = user.username

      if (currentuser !== username){
        this.$message.error('只能删除自己的评论');
        return;
      }

      await this.$confirm('确定要删除评论吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      });

      const res = await axios.get(this.$httpUrl + "/reviews/del?id=" + id);
      if (res.data.code === 200) {
        this.$message.success('删除成功');
        await this.getreviewlist();
      }
    }
  },
  created() {
    this.getMovieInfo().then(() => {
      if (this.movielist.length > 0) {
        const movieId = this.$route.query.id;
        if (movieId) {
          const movie = this.movielist.find(m => m.id == movieId);
          this.movielist = movie;
          // 检查收藏状态
          this.checkIfLiked();
          this.getreviewlist()
        }
      }
    });
  },
}
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
            <span class="meta-item">{{ movielist?.movietype?.join('/') }}</span>
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
        <!-- 收藏按钮 -->
        <button
            :class="['like-btn', isLiked ? 'liked' : 'unliked']"
            @click="addlike()">
          <span class="like-icon">♥</span>
          <span class="like-text">{{ isLiked ? '已收藏' : '点击收藏' }}</span>
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
      <!-- 影评 -->
      <div class="movie-section">
        <div class="section-header">
          <h2 class="section-title">
            <i class="icon-story"></i>
            <span>影评</span>
          </h2>
          <div class="review-input-area">
            <input
                v-model="content"
                placeholder="请输入影评内容"
                class="review-input">
            <button class="publish-btn" @click="addreview">点击发表</button>
          </div>
        </div>
        <!-- 影评列表 -->
        <div class="review-list">
          <div v-for="review in reviewlist" :key="review.id" class="review-item">
            <div class="review-user">用户 {{ review.username }}：</div>
            <div class="review-content">
              <p class="review-text">{{ review.content }}</p>
            </div>
            <button class="delete-btn" @click="delreview(review.id,review.username)">
              <i class="el-icon-delete"></i>
            </button>
          </div>
        </div>
        <!-- 空状态 -->
        <div v-if="reviewlist.length === 0" class="empty-reviews">
          暂无影评
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
  gap: 10px;
  padding: 0 20px 20px;
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
/* 收藏按钮样式 */
.like-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border: 2px solid;
  border-radius: 8px;
  padding: 12px 0;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  background: white;
}

.like-btn.unliked {
  border-color: #ff5f6d;
  color: #ff5f6d;
  background: white;
}

.like-btn.unliked:hover {
  background: #fff5f5;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 95, 109, 0.2);
}

.like-btn.liked {
  border-color: #ff5f6d;
  background: linear-gradient(135deg, #ff5f6d, #ff7b54);
  color: white;
  box-shadow: 0 4px 12px rgba(255, 95, 109, 0.3);
}

.like-btn.liked:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(255, 95, 109, 0.4);
}

.like-icon {
  font-size: 18px;
  transition: all 0.3s ease;
}

.like-btn.unliked .like-icon {
  color: #ff5f6d;
}

.like-btn.liked .like-icon {
  color: white;
  animation: pulse 0.5s ease;
}

@keyframes pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.2); }
  100% { transform: scale(1); }
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
  background: #ff9a9e;
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
/* 影评部分样式 */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.publish-btn {
  background: #00A699;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.publish-btn:hover {
  background: #00A699;
}

.review-list {
  border-top: 1px solid #f0f0f0;
}

.review-item {
  display: flex;
  align-items: flex-start;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

.review-user {
  font-weight: bold;
  color: #333;
  min-width: 80px;
  margin-right: 12px;
}

.review-content {
  flex: 1;
  margin-right: 12px;
}

.review-text {
  margin: 0;
  line-height: 1.5;
  color: #666;
}

.delete-btn {
  background: none;
  color: #f56c6c;
  border: none;
  padding: 4px 8px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.3s;
}

.delete-btn:hover {
  background: #fef0f0;
  color: #f78989;
}

.empty-reviews {
  text-align: center;
  color: #999;
  padding: 40px 0;
  border-top: 1px solid #f0f0f0;
}
.review-input-area {
  display: flex;
  align-items: center;
  gap: 12px;
}

.review-input {
  flex: 1;
  min-width: 300px;
  padding: 8px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.3s;
}

.review-input:focus {
  border-color: #409eff;
}
</style>
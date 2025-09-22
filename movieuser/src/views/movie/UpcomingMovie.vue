<script >
export default {
  data(){
    return{
      movieData: [],
      typeList: ["全部"], // 存储所有电影类型
      activeType: "全部", // 当前选中的类型
    }
  },
  methods:{
    tomovieinfo(movieId){
      this.$router.push({
        path:"/MovieInfo",
        query:{id:movieId}})
    },
    async allist() {
      const response = await this.$axios.get(this.$httpUrl + '/movietypes/list');
      this.typeList = ["全部", ...response.data.map(item => item.type)];
    },
    async GetMovieList() {
      const response = await this.$axios.get(this.$httpUrl + '/movie/upcoming');
      this.movieData = response.data.data;
    },
    // 获取筛选后的电影列表
    getFilteredMovies() {
      if (this.activeType === "全部") return this.movieData;
      const filtered = this.movieData.filter((movie) => {
        const formattedTypes = this.formatMovieTypes(movie.movietype);
        return formattedTypes.includes(this.activeType);
      });
      return filtered;
    },
    // 检查是否有匹配的电影
    hasMoviesForCurrentType() {
      if (this.activeType === "全部") return true;
      return this.movieData.some((movie) => {
        const formattedTypes = this.formatMovieTypes(movie.movietype);
        return formattedTypes.includes(this.activeType);
      });
    },
    formatMovieTypes(movietype) {
      // 如果是数组直接连接
      if (Array.isArray(movietype)) {
        return movietype.join("/");
      }
      // 如果是字符串尝试解析JSON
      if (typeof movietype === 'string') {
        try {
          const parsed = JSON.parse(movietype);
          if (Array.isArray(parsed)) {
            return parsed.join("/");
          }
          return movietype;
        } catch {
          return movietype;
        }
      }
      return "未知类型";
    },
    changeType(type) {
      this.activeType = type;
    },
  },
  created() {
    this.GetMovieList()
    this.allist()
  },
}
</script>

<template>
  <div class="main">
    <div class="types">
      <div class="type-tags">
        <div
            v-for="type in typeList"
            :key="type"
            class="type-tag"
            :class="{ active: activeType === type }"
            @click="changeType(type)"
        >
          {{ type }}
        </div>
      </div>
    </div>
    <!-- 电影列表 -->
    <div class="movielist">
      <div v-if="hasMoviesForCurrentType()" class="movie-grid">
        <div
            class="movie-card"
            v-for="movie in getFilteredMovies()"
            :key="movie.id"
            @click="tomovieinfo(movie.id)"
        >
          <div class="movie-poster">
            <img :src="movie.moviecover" :alt="movie.moviename" />
            <div class="movie-rating" v-if="movie.rating">
              <span>{{ movie.rating }}</span>
            </div>
          </div>
          <div class="movie-info">
            <h3 class="movie-title">{{ movie.moviename }}</h3>
            <div class="movie-meta">
              <span class="movie-type">
                {{ formatMovieTypes(movie.movietype) }}
              </span>
              <span class="movie-duration">
                {{ movie.movieduration }}分钟
              </span>
            </div>
          </div>
        </div>
      </div>
      <div v-else class="no-results">
        <p>抱歉，没有找到相关结果，请尝试用其他条件筛选。</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 电影列表样式 */
.movie-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 20px;
}

.movie-card {
  background: #fff;
  border-radius: 4px;
  overflow: hidden;
  transition: transform 0.3s;
}

.movie-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
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
  padding: 10px;
}

.movie-title {
  font-size: 16px;
  margin: 5px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.movie-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #999;
}
.type-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 25px;
  padding: 20px;
  border: 2px solid #f0f0f0; /* 添加外边框 */
  border-radius: 8px; /* 圆角边框 */
  background-color: #fafafa; /* 浅灰色背景 */
}

.type-tag {
  padding: 8px 16px; /* 增大内边距 */
  background: #ffffff;
  border: 1px solid #e0e0e0; /* 添加边框 */
  border-radius: 20px; /* 更大的圆角 */
  font-size: 16px; /* 增大字体 */
  font-weight: 500; /* 中等粗细 */
  color: #555;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 5px rgba(0,0,0,0.05); /* 轻微阴影 */
}
.type-tag:hover {
  background: #f5f5f5;
  border-color: #d0d0d0;
  box-shadow: 0 3px 8px rgba(0,0,0,0.1);
}

.type-tag.active {
  background: #EF4238;
  border-color: #EF4238;
  color: white;
  font-weight: 600;
  box-shadow: 0 3px 10px rgba(239, 66, 56, 0.3);
}
.no-results {
  text-align: center;
  padding: 40px 0;
  color: #999;
  font-size: 16px;
}
/* 响应式设计 */
@media (max-width: 768px) {
  .movie-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 15px;
  }

  .type-tags {
    padding: 15px;
    gap: 10px;
  }

  .type-tag {
    padding: 6px 12px;
    font-size: 14px;
  }
}
</style>
<script >

export default {
  data(){
    return{
      moviename:"",
      searchResults: [], // 存储搜索结果
    }
  },
  methods:{
    tomovieinfo(movieId){
      this.$router.push({
        path:"/MovieInfo",
        query:{id:movieId}})
    },
  },
  created() {
    this.moviename=this.$route.query.moviename;
    this.searchResults = typeof this.$route.query.searchResults === 'string'
        ? JSON.parse(this.$route.query.searchResults)
        : this.$route.query.searchResults || [];
  },
  watch: {
    '$route'(to, from) {
      if (to.query.moviename !== from.query.moviename) {
        this.$router.go(0); // 强制刷新页面
      }
    }
  }
}
</script>

<template>
  <div class="main">
    <div class="header">
      <h1 style="color: #f56c6c">搜索 "{{ this.moviename }}" 的结果</h1>
      <hr class="title-line">
      <div v-if="searchResults.length > 0" class="movie-list">
        <div v-for="(movie, index) in searchResults" :key="index" class="movie-item" @click="tomovieinfo(movie.id)">
          <img class="img" :src="movie.moviecover" >
          <h3>{{ movie.moviename}}</h3>
        </div>
      </div>

      <div v-else class="no-results">
        <h3>未找到关于 "{{ this.moviename }}" 的结果</h3>
        <p>请尝试不同的搜索词</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.main {
  width: 70%;
  margin: 0 auto;
  padding: 20px;
  border-bottom: #f5f7fa;
}

.header {
  text-align: center;
  margin-bottom: 30px;
}

.movie-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr)); /* 减小最小宽度 */
  gap: 15px; /* 减小间隙 */
  margin-top: 20px;
}

.movie-item {
  border: 1px solid #ddd;
  border-radius: 6px; /* 减小圆角 */
  padding: 8px; /* 减小内边距 */
  transition: transform 0.3s ease;
  font-size: 14px; /* 减小字体大小 */
}

.title-line {
  border: none;
  height: 1px;
  background-color: #ddd; /* 浅灰色横线 */
  margin: 10px 0; /* 上下边距 */
}
.movie-item:hover {
  transform: translateY(-3px); /* 减小悬停效果 */
  box-shadow: 0 3px 10px rgba(0,0,0,0.1); /* 减小阴影 */
}

.img {
  width: 100%;
  height: 250px; /* 减小图片高度 */
  object-fit: cover; /* 确保图片比例正确 */
  border-radius: 4px; /* 减小图片圆角 */
}

.no-results {
  text-align: center;
  padding: 40px;
  color: #666;
}

.no-results h3 {
  color: #333;
  margin-bottom: 10px;
}
</style>
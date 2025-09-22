<template>
  <div class="time-container">
    <span class="time-display">
      今天是：<span class="date">{{ formattedDate }}</span>
      <span class="time-segment hour">{{ time.hours }}</span>
      <span class="time-separator">:</span>
      <span class="time-segment minutes">{{ time.minutes }}</span>
      <span class="time-separator">:</span>
      <span class="time-segment seconds">{{ time.seconds }}</span>
    </span>
  </div>
</template>

<script>
export default {
  name: "DateTimeDisplay",
  data() {
    return {
      time: {
        hours: "00",
        minutes: "00",
        seconds: "00"
      },
      formattedDate: "",
      weekDays: ["星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"],
      timer: null
    };
  },
  mounted() {
    this.updateTime();
    // 使用 requestAnimationFrame 替代 setTimeout 更流畅
    this.timer = requestAnimationFrame(this.loopTimeUpdate);
  },
  beforeDestroy() {
    // 组件销毁时清除定时器
    if (this.timer) {
      cancelAnimationFrame(this.timer);
    }
  },
  methods: {
    updateTime() {
      const now = new Date();

      // 更新时间部分
      this.time = {
        hours: this.padZero(now.getHours()),
        minutes: this.padZero(now.getMinutes()),
        seconds: this.padZero(now.getSeconds())
      };

      // 更新日期部分
      this.formattedDate = `${now.getFullYear()}年${
          this.padZero(now.getMonth() + 1)
      }月${
          this.padZero(now.getDate())
      }日 ${this.weekDays[now.getDay()]}`;
    },
    padZero(num) {
      // 更简洁的补零方法
      return num.toString().padStart(2, '0');
    },
    loopTimeUpdate() {
      this.updateTime();
      // 每秒更新一次，但使用更平滑的动画帧
      this.timer = requestAnimationFrame(this.loopTimeUpdate);
    }
  }
};
</script>

<style scoped>
.time-container {
  padding: 20px;
  font-size: 20px;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen, Ubuntu, Cantarell, "Fira Sans", "Droid Sans", sans-serif;
}

.time-display {
  display: inline-flex;
  align-items: center;
  color: #333;
}

.date {
  margin-right: 5px;
  font-weight: 500;
}

.time-segment {
  min-width: 20px;
  display: inline-block;
  text-align: center;
}

.time-separator {
  animation: blink 1s step-end infinite;
  margin: 0 2px;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.3; }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .time-container {
    font-size: 16px;
    padding: 10px;
  }
}

@media (max-width: 480px) {
  .time-container {
    font-size: 14px;
  }
}
</style>
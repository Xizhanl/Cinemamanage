<template>
  <div>
    <div class="total-count" >总电影数：{{ totalMovies }} 部</div>
    <div id="movieTypePie" style="width: 100%; height: 400px;"></div>
    <div class="total-order">总订单数:{{totalOrders}}个</div>
    <div id="orderStatus" style="width: 100%;height: 400px"></div>
  </div>

</template>

<script>
import * as echarts from 'echarts';
export default {
  name: 'AutoMovieTypePie',
  data() {
    return {
      chart: null,
      orderchart:null,
      movieData: [],
      orderData:[],
      totalMovies: 0,
      totalOrders:0
    };
  },
  mounted() {
    this.initChart();
    this.fetchMovieData();
    this.fetchOrderData()
  },
  beforeDestroy() {
    if (this.chart) {
      this.chart.dispose();
    }
    if(this.orderchart){
      this.orderchart.dispose();
    }
    window.removeEventListener("resize",this.handleResize)
  },
  methods: {
    initChart() {
      this.chart = echarts.init(document.getElementById('movieTypePie'));
      this.orderchart = echarts.init(document.getElementById("orderStatus"))
    },
    async fetchMovieData() {
        const response = await this.$axios.get(this.$httpUrl + '/movie/list');
        this.movieData = response.data;
        this.updateChart();
    },
    async fetchOrderData(){
      const response = await this.$axios.get(this.$httpUrl+"/orders/list")
      this.orderData = response.data;
      this.updateOrderChart();
    },
    // 处理电影类型的方法
    processMovieTypes(movie) {
      const type = movie.movietype;
      // 如果类型是数组
      if (Array.isArray(type)) {
        // 处理数组中的每个元素，如果元素本身也是数组则展平
        return type.flatMap(t => {
          // 如果数组元素是字符串，直接返回
          if (typeof t === 'string') return t;
          // 如果数组元素是数组，则递归处理
          if (Array.isArray(t)) return this.processMovieTypes({ movietype: t });
          // 其他情况返回"未知类型"
          return '未知类型';
        });
      }
      // 如果类型是字符串
      else if (typeof type === 'string') {
        // 检查是否是JSON字符串
        try {
          const parsed = JSON.parse(type);
          if (Array.isArray(parsed)) {
            return this.processMovieTypes({ movietype: parsed });
          }
          return [type];
        } catch (e) {
          return [type];
        }
      }
      // 其他情况返回"未知类型"
      return ['未知类型'];
    },
    updateChart() {
      if (!this.movieData.length) return;

      this.totalMovies = this.movieData.length;

      // 使用新的处理方法来统计类型
      const typeCount = this.movieData.reduce((acc, movie) => {
        const types = this.processMovieTypes(movie);
        types.forEach(type => {
          acc[type] = (acc[type] || 0) + 1;
        });
        return acc;
      }, {});

      const chartData = Object.entries(typeCount)
          .map(([name, value]) => ({ name, value }))
          .sort((a, b) => b.value - a.value);

      const option = {
        title: {
          text: '电影类型分布(按部数)',
          left: 'center',
          textStyle: {
            fontSize: 16,
            fontWeight: 'bold'
          }
        },
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c}部 ({d}%)'
        },
        legend: {
          orient: 'vertical',
          right: 10,
          top: 'center',
          data: chartData.map(item => item.name)
        },
        series: [{
          name: '电影类型',
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 5,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: true,
            formatter: '{b}: {c}部'
          },
          labelLine: {
            show: true
          },
          emphasis: {
            label: {
              show: true,
              fontSize: '18',
              fontWeight: 'bold'
            }
          },
          data: chartData,
          animationType: 'scale',
          animationDuration: 800,
          animationEasing: 'quarticOut'
        }],
        // 添加颜色配置
        color: [
          '#5470C6', '#91CC75', '#FAC858', '#EE6666',
          '#73C0DE', '#3BA272', '#FC8452', '#9A60B4',
          '#EA7CCC', '#60C0DD', '#F7C23A', '#B5C334'
        ]
      };

      this.chart.setOption(option, true);
      // 窗口大小变化时重新调整图表大小
      window.addEventListener('resize', this.handleResize);
    },
    updateOrderChart(){
      if(!this.orderData.length) return;
      this.totalOrders = this.orderData.length;
      const statusCount={
        "已完成":0,
        "已取消":0,
        "待支付":0,
      }
      this.orderData.forEach(order=>{
        const status = order.orderstatus;
        if (status==='已完成') statusCount['已完成']++;
        else if (status==='已取消') statusCount['已取消']++;
        else if (status==='待支付') statusCount['待支付']++;
      })

      const option = {
        title :{
          text : "订单状态统计",
          left : "center",
          textStyle: {
            fontSize: 16,
            fontWeight: 'bold'
          }
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          },
          formatter: '{b}: {c}个'
        },
        xAxis: {
          type: 'category',
          data: ['已完成', '已取消', '待支付'],
          axisLabel: {
            interval: 0
          }
        },
        yAxis: {
          type: 'value',
          name: '订单数量'
        },
        series: [{
          name: '订单数',
          type: 'bar',
          barWidth: '40%',
          data: [
            { value: statusCount['已完成'], itemStyle: { color: '#67C23A' } },
            { value: statusCount['已取消'], itemStyle: { color: '#F56C6C' } },
            { value: statusCount['待支付'], itemStyle: { color: '#E6A23C' } }
          ],
          label: {
            show: true,
            position: 'top'
          }
        }]
      }
      this.orderchart.setOption(option,true);
    },
    handleResize() {
      if (this.chart) {
        this.chart.resize();
      }
      if(this.orderchart){
        this.orderchart.resize();
      }
    }
  },
  watch: {
    movieData() {
      this.updateChart();
    },
    orderData(){
      this.updateOrderChart();
    }
  },
};
</script>

<style scoped>
#movieTypePie {
  margin-top: 20px;
  min-height: 400px;
}

.total-count {
  padding: 10px 0;
  font-size: 16px;
  text-align: center;
  font-weight: bold;
  color: #333;
}
#orderStatus {
  margin-top: 20px;
  min-height: 400px;
}

.total-order {
  padding: 10px 0;
  font-size: 16px;
  text-align: center;
  font-weight: bold;
  color: #333;
}
</style>
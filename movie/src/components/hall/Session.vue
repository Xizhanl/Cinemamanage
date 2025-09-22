<script>
export default {
  data() {
    return {
      tableData:[],
      pageSize:20,
      pageNum:1,
      total:0,
      hallname:'',
      moviename:'',
      dialogVisible:false,
      movienames:[],
      hallnames:[],
      form:{
        id:'',
        hallname:'',
        moviename:'',
        screeningtime:'',
        duration:'',
        endtime:'',
        price:'',
        totalseats:'',
        seatssold:'',
        notice:'',
        seatstatus:''
      },
      rules:{
        hallname: [
          { required: true, message: '请选择影厅名称', trigger: 'blur' },
        ],
        moviename: [
          { required: true, message: '请选择电影名称', trigger: 'change' },
        ],
        screeningtime: [
          { required: true, message: '请选择场次日期', trigger: 'blur' },
        ],
        duration: [
          { required: true, message: '请输入播放时长', trigger: 'blur' },
        ],
        price: [
          { required: true, message: '请输入价格', trigger: 'blur' },
          { type: 'number', message: '必须输入数字', trigger: 'blur' },
        ],
        totalseats: [
          { type: 'number', message: '必须输入数字', trigger: 'blur' },
        ],
        seatssold:[
          { type: 'number', message: '必须输入数字', trigger: 'blur' },
          {
            validator: (rule, value, callback) => {
              if (value > this.form.totalseats) {
                callback(new Error('已售座位数不能超过总座位数'));
              } else {
                callback();
              }
            },
            trigger: 'blur'
          }
        ],
      }
    }
  },
  methods:{
    resetForm(){
      this.form = {
        hallname:'',
        moviename:'',
        screeningtime:'',
        duration:'',
        endtime: '',
        price:'',
        totalseats:'',
        seatssold:'',
        seatsremaining:'',
        notice:''
      };
      // 重置表单验证状态
      if (this.$refs.form) {
        this.$nextTick(() => {
          this.$refs.form.clearValidate();
        });
      }
    },
    loadGet(){
      this.$axios.get(this.$httpUrl+'/sessions/list')
          .then(res=>res.data)
          .then(res=>{console.log(res)
          })
    },
    loadPost() {
      this.$axios.post(this.$httpUrl + '/sessions/listPage', {
        pageSize:this.pageSize,
        pageNum:this.pageNum,
        param:{
          hallname:this.hallname,
          moviename:this.moviename,
        }
      }).then(res => res.data).then(res => {
        console.log(res)//处理数据，只要data
        if(res.code==200){//封装数据
          this.tableData = res.data.map(session => {
            const movie = this.movienames.find(m => m.value === session.moviename)
            const hall = this.hallnames.find(h => h.value === session.hallname);
            let totalseats = 0;
            let seatssold = 0;
            //禁用的座位
            let seatssolds=0;
            let seatStatus = {};
            if (hall) {
              // 计算已售座位数
              try {
                seatStatus = JSON.parse(session.seatstatus);
                for (const row in seatStatus) {
                  seatssold += seatStatus[row].filter(seat => seat === 2).length;
                  seatssolds += seatStatus[row].filter(seat => seat === 1).length;
                }
                totalseats = Number(hall.seats)-seatssolds;
              } catch (e) {
                console.error("解析座位状态失败:", e);
              }
            }
            return {
              ...session,
              movieduration: movie? movie.duration: '未知',
              endtime: movie ? this.calculateEndTime(session.duration, movie.duration) : '',
              seatstatus: JSON.stringify(seatStatus), // 保存完整的座位状态
              totalseats: totalseats,
              seatssold: seatssold,
              seatsremaining: (totalseats - seatssold )
            }
          })
          this.total=res.total
        }else{
          alert("获取数据失败")
        }
      })
    },
    resetParam(){
      this.hallname=''
    },
    add(){//新增
      this.dialogVisible=true
      this.$nextTick(()=>{
        this.resetForm();
      })
    },
    doSave() {
      // 获取所选影厅的座位状态作为初始状态
      const selectedHall = this.hallnames.find(h => h.value === this.form.hallname);
      // 初始化场次的座位状态
      if (selectedHall) {
        this.form.seatstatus = selectedHall.seatstatus || '{}';
      } else {
        this.form.seatstatus = '{}'; // 默认空座位图
      }
      this.$axios.post(this.$httpUrl + '/sessions/save', this.form)
          .then(res => res.data)
          .then(res => {
            if(res.code === 200) {
              this.$message.success("场次创建成功");
              this.dialogVisible = false;
              this.loadPost();
            } else {
              this.$message.error(res.msg || "操作失败");
            }
          });
    },
    doMod(){
      this.$axios.post(this.$httpUrl + '/sessions/mod',this.form).then(res => res.data).then(res => {
        console.log(res)//处理数据，只要data
        if(res.code===200){//封装数据
          this.$message.success("操作成功");
          this.dialogVisible = false;
          this.loadPost();
          this.resetForm();
        }else{
          this.$message.error("操作失败");
        }
      })
    },
    save(){//新增
      this.$refs.form.validate((valid) => {
        if (valid) {
          if(this.form.id){
            this.doMod();
          }else{
            this.doSave();
          }
        } else {
          console.log('error submit!!');
          return false;
        }
      });
    },
    mod(row){//编辑赋值
      this.dialogVisible=true
      this.$nextTick(()=>{
        this.form.id=row.id
        this.form.hallname=row.hallname,
        this.form.moviename=row.moviename,
        this.form.screeningtime=row.screeningtime,
        this.form.duration=row.duration,
        this.form.endtime=row.endtime,
        this.form.price=Number(row.price),
        this.form.totalseats=Number(row.totalseats),
        this.form.seatssold=row.seatssold,
        this.form.seatsremaining=Number(row.seatsremaining),
        this.form.seatstatus=row.seatstatus,
        this.form.notice=row.notice
        // 触发一次影厅选择变化事件，确保数据一致
        this.handleHallChange(row.hallname)
        const selectedMovie = this.movienames.find(m => m.value === row.moviename)
        if (selectedMovie) {
          this.form.movieduration = selectedMovie.duration
        }
      })
    },
    del(id){//删除
      console.log(id)
      this.$axios.get(this.$httpUrl + '/sessions/del?id='+id).then(res => res.data).then(res => {
        console.log(res)//处理数据，只要data
        if(res.code==200){//封装数据
          this.$message.success("操作成功");
          this.loadPost();
        }else{
          this.$message.error("操作失败");
        }
      })
    },
    handleSizechange(val){//el方法
      this.pageNum=1//永远是第一页
      this.pageSize=val//获取页数
      this.loadPost()
    },
    handleCurrentchange(val){
      this.pageNum=val
      this.loadPost()
    },
    async fetchmovienames() {
      const response = await this.$axios.get(this.$httpUrl + '/movie/ongoing');
      this.movienames = response.data.data.map(movie => ({
        value: movie.moviename,  // 作为选项的值
        label: movie.moviename,// 显示的名称
        duration: movie.movieduration
      }));
    },
    async fetchhallnames() {
      const response = await this.$axios.get(this.$httpUrl + '/hall/list');
      this.hallnames = response.data.filter(hall=>(hall.statu===1)).map(hall => ({
        value: hall.hallname,  // 作为选项的值
        label: hall.hallname,   // 显示的名称
        seats:hall.totalseats,
        seatstatus: hall.seatstatus
      }));
    },
    handleHallChange(selectedHallname) {
      const selectedHall = this.hallnames.find(hall => hall.value === selectedHallname);
      if (selectedHall) {
        // 计算统计数据
        let disabledSeats = 0;
        let soldseat = 0 ;
        try {
          const hallSeats = JSON.parse(this.form.seatstatus);
          for (const row in hallSeats) {
            disabledSeats += hallSeats[row].filter(s => s === 1).length;
            soldseat += hallSeats[row].filter(s => s === 2).length;
          }
        } catch(e) {
          console.error("座位状态解析失败:", e);
        }
        this.form.totalseats = Number(selectedHall.seats) - disabledSeats;
        this.form.seatsremaining = this.form.totalseats;
        this.form.seatssold = soldseat;
      }
    },
    handleMovieChange(selectedMoviename) {
      const selectedMovie = this.movienames.find(m => m.value === selectedMoviename)
      if (selectedMovie) {
        this.form.movieduration = selectedMovie.duration
        if (this.form.duration) {
          this.form.endtime = this.calculateEndTime(this.form.duration, selectedMovie.duration);
        }
      }
    },
    calculateEndTime(duration, movieduration) {
      if (!duration || !movieduration) return '';
      // 将 movieduration 转换为分钟数（假设 movieduration 是数字）
      const movieMinutes = parseInt(movieduration) || 0;
      // 根据电影时长确定间隔小时数
      let intervalHours = 2; // 默认2小时
      if (movieMinutes > 180) intervalHours = 4;
      else if (movieMinutes > 120) intervalHours = 3;
      else if (movieMinutes > 60) intervalHours = 2;
      // 计算结束时间
      const [hours, minutes] = duration.split(':').map(Number);
      let totalMinutes = hours * 60 + minutes + intervalHours * 60;
      // 处理跨日情况
      const endHours = Math.floor(totalMinutes / 60) % 24;
      const endMinutes = totalMinutes % 60;
      return `${String(endHours).padStart(2, '0')}:${String(endMinutes).padStart(2, '0')}`;
    },
    updateEndTime() {
      if (this.form.moviename && this.form.duration) {
        const movie = this.movienames.find(m => m.value === this.form.moviename);
        if (movie) {
          this.form.endtime = this.calculateEndTime(this.form.duration, movie.duration);
        }
      }
    },
    autoRenewSessions() {
      this.$confirm('确定要自动更新场次吗？这将删除昨天场次并创建后天场次', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$axios.get(this.$httpUrl + '/sessions/autoRenewSessions')
            .then(res => {
              if(res.data.code === 200) {
                this.$message.success(res.data.msg);
                this.loadPost(); // 刷新列表
              } else {
                this.$message.error(res.data.msg);
              }
            })
            .catch(err => {
              this.$message.error('操作失败: ' + err.message);
            });
      }).catch(() => {});
    }
  },
  beforeMount() {
    //创建请求方法
    this.loadGet();
    this.loadPost();
    this.fetchmovienames();
    this.fetchhallnames();
  },
}
</script>

<template>
  <div>
    <div style="margin-bottom: 5px" >
      <el-input v-model="hallname" placeholder="请输入影厅名称" suffix-icon="el-icon-search" style="width: 200px;margin-right: 5px"
                size="small" @keyup.enter.native="loadPost"></el-input>
      <el-input v-model="moviename" placeholder="请输入电影名称" suffix-icon="el-icon-search" style="width: 200px;margin-right: 5px"
                size="small" @keyup.enter.native="loadPost"></el-input>
      <el-button type="success" size="small" style="margin-left: 5px" @click="loadPost">查询</el-button>
      <el-button type="primary" size="small" @click="resetParam">重置</el-button>
      <el-button type="warning" size="small" @click="add">新增</el-button>
      <el-button type="danger" size="small" @click="loadPost">刷新</el-button>
      <el-button type="info" size="small" @click="autoRenewSessions">自动更新场次</el-button>
    </div>
    <el-table :data="tableData"
              :header-cell-style="{background:'#f2f5fc',color:'#555'}"
              border
    >
      <el-table-column prop="id" label="ID" width="80px">
      </el-table-column>
      <el-table-column prop="hallname" label="影厅名称" width="100px">
      </el-table-column>
      <el-table-column prop="moviename" label="电影名称" >
      </el-table-column>
      <el-table-column prop="movieduration" label="电影时长/分钟" width="90px">
      </el-table-column>
      <el-table-column prop="screeningtime" label="场次时间" width="100px">
      </el-table-column>
      <el-table-column prop="duration" label="播放时长" >
        <template slot-scope="scope">
          <span>
          {{ scope.row.duration }} -{{scope.row.endtime}}
        </span>
        </template>
      </el-table-column>
      <el-table-column prop="price" label="价格" width="80px">
      </el-table-column>
      <el-table-column prop="totalseats" label="总座位数" width="80px">
      </el-table-column>
      <el-table-column prop="seatssold" label="已售座位数" width="100px">
      </el-table-column>
      <el-table-column prop="seatsremaining" label="剩余座位数" width="100px">
      </el-table-column>
      <el-table-column prop="notice" label="提示信息" >
      </el-table-column>
      <el-table-column prop="operate" label="操作">
        <template v-slot:default='scope'>
          <el-button
              size="small"
              type="success"
              @click="mod(scope.row)">编辑</el-button>
          <el-popconfirm
              title="确定删除吗？"
              style="margin-left: 5px"
              @confirm="del(scope.row.id)"
              :close-delay="200"
          >
            <el-button slot="reference"
                       size="small"
                       type="danger"
                       style="margin-left: 5px"
            >删除</el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
      <el-table-column label="座位状态" width="100" >
        <template v-slot:default='scope'>
          <el-popover
              placement="right"
              width="auto"
          trigger="click"
          popper-class="seat-popper">
          <div class="seat-container">
            <h4 style="margin-top:0">座位状态图（{{scope.row.hallname}}）</h4>
            <div v-for="(row, rowIndex) in JSON.parse(scope.row.seatstatus)"
                 :key="rowIndex" class="seat-row">
              <span class="row-label">第{{rowIndex}}排:</span>
              <span v-for="(seat, colIndex) in row"
                    :key="colIndex"
                    :class="['seat', seat === 1 ? 'sold' : seat === 2 ? 'sold' : 'available']">
            {{colIndex+1}}
          </span>
            </div>
          </div>
          <el-button slot="reference" size="mini" type="warning">查看座位</el-button>
          </el-popover>
        </template>
      </el-table-column>
    </el-table>
    <!--页码-->
    <el-pagination
        @size-change="handleSizechange"
        @current-change="handleCurrentchange"
        :current-page="pageNum"
        :page-size="pageSize"
        :page-sizes="[20, 40]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
    </el-pagination>
    <!--新增表单-->
    <el-dialog
        title="场次信息"
        :visible.sync="dialogVisible"
        width="30%"
        center>
      <el-form ref="form" :rules="rules" :model="form" label-width="120px">

        <el-form-item label="影厅名称" prop="hallname">
          <el-select v-model="form.hallname" placeholder="请选择影厅名称" clearable @change="handleHallChange">
            <el-option
                v-for="(item, index) in hallnames"
                :key="index"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="电影名称" prop="moviename">
          <el-select v-model="form.moviename" placeholder="请选择电影名称" clearable @change="handleMovieChange" >
            <el-option
                v-for="(item, index) in movienames"
                :key="index"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="场次时间" prop="screeningtime">
          <el-date-picker
              v-model="form.screeningtime"
              type="date"
              value-format="yyyy-MM-dd"
              placeholder="选择日期"
              style="width:100%">
          </el-date-picker>
        </el-form-item>

        <el-form-item label="播放时长" prop="duration">
          <el-col :span="25">
            <el-time-select
                placeholder="起始时间"
                v-model="form.duration"
                :picker-options="{
                    start: '08:30',
                    step: '00:15',
                    end: '21:00'
                  }"
                @change="updateEndTime">
            </el-time-select>
          </el-col>
        </el-form-item>

        <el-form-item label="价格" prop="price">
          <el-col :span="25">
            <el-input v-model.number="form.price"></el-input>
          </el-col>
        </el-form-item>

        <el-form-item label="总座位数" prop="totalseats" >
          <el-col :span="25">
            <el-input v-model.number="form.totalseats" :disabled="true"></el-input>
          </el-col>
        </el-form-item>

        <el-form-item label="已售座位数" prop="seatssold">
          <el-col :span="25">
            <el-input v-model.number="form.seatssold" :disabled="true"></el-input>
          </el-col>
        </el-form-item>

        <el-form-item label="提示信息" prop="notice">
          <el-col :span="25">
            <el-input v-model="form.notice"></el-input>
          </el-col>
        </el-form-item>

      </el-form>
      <span slot="footer" class="dialog-footer">
    <el-button @click="dialogVisible = false">取 消</el-button>
    <el-button type="primary" @click="save">确 定</el-button>
    </span>
    </el-dialog>
  </div>
</template>

<style scoped>
.seat-container {
  max-height: 500px; /* 控制最大高度 */
  overflow-y: auto;  /* 添加垂直滚动条 */
  padding: 10px;
}

.seat-row {
  display: flex;
  flex-wrap: wrap; /* 关键：允许换行 */
  margin-bottom: 5px;
  align-items: center;
}

.row-label {
  width: 60px;
  flex-shrink: 0; /* 防止标签被压缩 */
}

.seat {
  width: 25px;
  height: 25px;
  line-height: 25px;
  text-align: center;
  margin: 2px;
  border-radius: 3px;
  cursor: pointer;
  flex-shrink: 0; /* 防止座位被压缩 */
}
.seat-row { margin-bottom: 5px; }
.row-label { display: inline-block; width: 60px; }
.seat {
  display: inline-block;
  width: 25px;
  height: 25px;
  line-height: 25px;
  text-align: center;
  margin: 2px;
  border-radius: 3px;
  cursor: pointer;
}
.seat.available { background: #67C23A; color: white; }
.seat.sold { background: #F56C6C; color: white; }
</style>
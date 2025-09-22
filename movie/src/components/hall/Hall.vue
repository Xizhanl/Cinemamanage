<script>
export default {
  data() {
    return {
      tableData:[],
      pageSize:10,
      pageNum:1,
      total:0,
      hallname:'',
      halltype:'',
      statu: '',
      dialogVisible:false,
      seatdialogVisible:false,
      statuss:[
          { value: '1', label: '可用' },
          { value: '0', label: '停用' },
          { value: '2', label: '维修中' },
      ],
      halltypes:[],
      form:{
        hallname:"",
        halltype:"",
        startrow:"",
        rowcount:"",
        seatsrow:"",
        totalseats:"",
        statu:"",
        seatstatus:""
      },
      rules:{
        hallname: [
          { required: true, message: '请输入影厅名称', trigger: 'blur' },
        ],
        halltype: [
          { required: true, message: '请选择影厅类型', trigger: 'change' },
        ],
        startrow: [
          { required: true, message: '请输入初始行号', trigger: 'blur' },
          { type: 'number', message: '必须输入数字', trigger: 'blur' },
          { type: 'number', min: 1, max: 5, message: '必须介于 1 到 5之间', trigger: 'blur' }
        ],
        rowcount: [
          { required: true, message: '请输入行数', trigger: 'blur' },
          { type: 'number', message: '必须输入数字', trigger: 'blur' },
          { type: 'number', min: 5, max: 15, message: '必须介于 5 到 15 之间', trigger: 'blur' }
        ],
        seatsrow: [
          { required: true, message: '请输入每排座位数', trigger: 'blur' },
          { type: 'number', message: '必须输入数字', trigger: 'blur' },
          { type: 'number', min: 5, max: 15, message: '必须介于 5 到 15 之间', trigger: 'blur' }
        ],
        statu: [
          { required: true, message: '请选择状态(1-可用,2-维修中，0-停用)', trigger: 'change' },
        ],
      },
      seats: {},       // 存储座位数据，格式为 {行号: [0,1,0,...]}，0表示启用，1表示禁用
      isSelected: {    // 座位样式映射
        0: 'seat-default',  // 启用状态的样式
        1: 'seat-choose'    // 禁用状态的样式
      },
      editSeat: {},
      arrangeDialogWidth: '',
    }
  },
  methods:{
    resetForm(){
      this.form = {
        hallname:"",
        halltype:"",
        startrow:"",
        rowcount:"",
        seatsrow:"",
        totalseats:"",
        statu:"",
      };
      // 重置表单验证状态
      if (this.$refs.form) {
        this.$nextTick(() => {
          this.$refs.form.clearValidate();
        });
      }
    },
    loadGet(){
      this.$axios.get(this.$httpUrl+'/hall/list')
          .then(res=>res.data)
          .then(res=>{console.log(res)
          })
    },
    loadPost() {
      this.$axios.post(this.$httpUrl + '/hall/listPage', {
        pageSize:this.pageSize,
        pageNum:this.pageNum,
        param:{
          hallname:this.hallname,
          halltype:this.halltype,
          statu:this.statu
        }
      }).then(res => res.data).then(res => {
        console.log(res)//处理数据，只要data
        if(res.code===200){//封装数据
          this.tableData =res.data
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
    doSave(){
      // 初始化座位数据（数字行号）
      if (!this.form.id) {
        const seats = {};
        const startRow = parseInt(this.form.startrow);

        for (let i = 0; i < this.form.rowcount; i++) {
          const rowNum = (startRow + i).toString();
          seats[rowNum] = Array(parseInt(this.form.seatsrow)).fill(0);
        }
        this.form.seatstatus = JSON.stringify(seats);
        this.form.totalseats = this.form.rowcount * this.form.seatsrow;
      }
      this.$axios.post(this.$httpUrl + '/hall/save',this.form).then(res => res.data).then(res => {
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
    doMod(){
      this.$axios.post(this.$httpUrl + '/hall/mod',this.form).then(res => res.data).then(res => {
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
      console.log(row)
      this.dialogVisible=true
      this.$nextTick(()=>{
        this.form.id=row.id
        this.form.hallname=row.hallname,
        this.form.halltype=row.halltype,
        this.form.startrow=Number(row.startrow),
        this.form.rowcount=Number(row.rowcount),
        this.form.seatsrow=Number(row.seatsrow),
        this.form.totalseats=Number(row.totalseats),
        this.form.statu=Number(row.statu),
        this.seatstatus=row.seatstatus
      })
    },
    del(id){//删除
      console.log(id)
      this.$axios.get(this.$httpUrl + '/hall/del?id='+id).then(res => res.data).then(res => {
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
      console.log(`每页${val}条`)
      this.pageNum=1//永远是第一页
      this.pageSize=val//获取页数
      this.loadPost()
    },
    handleCurrentchange(val){
      console.log(`当前页:${val}`)
      this.pageNum=val
      this.loadPost()
    },
    async fetchHallTypes() {
      try {
        const response = await this.$axios.get('http://localhost:9999/cinema/list');
        // 1. 检查数据是否存在
        if (response.data && response.data.length > 0) {
          // 2. 提取第一个影院的 hallTypes
          const hallTypeLabels = response.data[0].hallTypes || []
          this.halltypes = hallTypeLabels.map(label => ({
            value: label,  // 直接使用名称作为value
            label: label   // 同样使用名称作为label
          }));
        } else {
          this.halltypes = [];  // 数据为空时的处理
        }
      } catch (error) {
        console.error('获取影厅类型失败:', error);
        this.halltypes = [];
      }
    },
    manageseat(row){
      this.editSeat = JSON.parse(JSON.stringify(row)); // 深拷贝当前行数据
      // 初始化座位数据
      if (!this.editSeat.seatstatus) {
        this.initSeats();
      } else {
        try {
          this.seats = JSON.parse(this.editSeat.seatstatus);
        } catch (e) {
          console.error('座位数据解析失败，使用默认值');
          this.initSeats();
        }
      }
      this.arrangeDialogWidth = 200 + this.editSeat.seatsrow * 40 + 'px';
      this.seatdialogVisible = true;
    },
    initSeats() {
      this.seats = {};
      const startRow = parseInt(this.editSeat.startrow); // 获取起始行号（如1）
      for (let i = 0; i < this.editSeat.rowcount; i++) {
        const rowNum = (startRow + i).toString(); // 生成行号 "1", "2", "3"...
        this.seats[rowNum] = Array(parseInt(this.editSeat.seatsrow)).fill(0);
      }
      // 初始化总座位数
      this.editSeat.totalseats = this.editSeat.rowcount * this.editSeat.seatsrow;
      this.editSeat.seatstatus = JSON.stringify(this.seats);
    },
    seatsave(){
      this.editSeat.seatstatus = JSON.stringify(this.seats);
      this.calculateAvailableSeats();
      this.$axios.post(this.$httpUrl + '/hall/saveOrMod', this.editSeat)
          .then(res => {
            if (res.data.code === 200) {
              this.$message.success('座位配置保存成功');
              this.loadPost();
            } else {
              this.$message.error('保存失败');
            }
            this.seatdialogVisible = false;
          });
    },
    pressSeat(key, idx){
      // 创建深拷贝确保响应式更新
      const newSeats = JSON.parse(JSON.stringify(this.seats));
      newSeats[key][idx] = newSeats[key][idx] === 0 ? 1 : 0;
      this.seats = newSeats;  // 直接替换整个对象
      this.calculateAvailableSeats();
    },
    calculateAvailableSeats() {
      let available = 0;
      for (const row in this.seats) {
        available += this.seats[row].filter(seat => seat === 0).length;
      }
      this.editSeat.totalseats = available;
    }
  },
  beforeMount() {
    //创建请求方法
    this.loadGet();
    this.loadPost();
    this.fetchHallTypes()
  },
}
</script>

<template>
  <div>
    <div style="margin-bottom: 5px" >
      <el-input v-model="hallname" placeholder="请输入影厅名称" suffix-icon="el-icon-search" style="width: 200px;margin-right: 5px"
                size="small" @keyup.enter.native="loadPost"></el-input>
      <el-select
          v-model="halltype"
          filterable
          clearable
          placeholder="请选择影厅类型"
          style="width: 200px; margin-right: 5px"
          size="small">
        <el-option
            v-for="item in halltypes"
            :key="item.value"
            :label="item.label"
            :value="item.value">
        </el-option>
      </el-select>
      <el-select v-model="statu"
                 placeholder="请选择状态"
                 filterable
                 clearable
                 style="width: 200px; margin-right: 5px"
                 size="small">
        <el-option
            v-for="item in statuss"
            :key="item.value"
            :label="item.label"
            :value="item.value">
        </el-option>
      </el-select>
      <el-button type="success" size="small" style="margin-left: 5px" @click="loadPost">查询</el-button>
      <el-button type="primary" size="small" @click="resetParam">重置</el-button>
      <el-button type="warning" size="small" @click="add">新增</el-button>
    </div>
    <el-table :data="tableData"
              :header-cell-style="{background:'#f2f5fc',color:'#555'}"
              border
    >
      <el-table-column prop="id" label="影厅ID" width="80px">
      </el-table-column>
      <el-table-column prop="hallname" label="影厅名称" >
      </el-table-column>
      <el-table-column prop="halltype" label="影厅类型" >
      </el-table-column>
      <el-table-column prop="startrow" label="起始行号" >
      </el-table-column>
      <el-table-column prop="rowcount" label="总行数" >
      </el-table-column>
      <el-table-column prop="seatsrow" label="每行座位数" >
      </el-table-column>
      <el-table-column prop="totalseats" label="总座位数" >
      </el-table-column>
      <el-table-column prop="statu" label="状态(1-可用,0-停用，2-维修中)" >
        <template slot-scope="scope">
          <el-tag
              :type="scope.row.statu === 1 ? 'success' : (scope.row.statu === 0 ? 'danger' : 'warning')"
              size="small">
            {{ scope.row.statu }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="operate" label="操作" width="240">
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
          <el-button
              size="small"
              type="warning"
              style="margin-left: 5px"
              @click="manageseat(scope.row)">安排座位</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!--页码-->
    <el-pagination
        @size-change="handleSizechange"
        @current-change="handleCurrentchange"
        :current-page="pageNum"
        :page-size="pageSize"
        :page-sizes="[10, 15]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
    </el-pagination>
    <!--新增表单-->
    <el-dialog
        title="影厅信息"
        :visible.sync="dialogVisible"
        width="30%"
        center>
      <el-form ref="form" :rules="rules" :model="form" label-width="120px">

        <el-form-item label="影厅名称" prop="hallname">
          <el-col :span="25">
            <el-input v-model="form.hallname"></el-input>
          </el-col>
        </el-form-item>

        <el-form-item label="影厅类型" prop="halltype">
          <el-select v-model="form.halltype" placeholder="请选择影厅类型" clearable>
            <el-option
                v-for="(item, index) in halltypes"
                :key="index"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="起始行号" prop="startrow">
          <el-col :span="25">
            <el-input v-model.number="form.startrow"></el-input>
          </el-col>
        </el-form-item>

        <el-form-item label="总行数" prop="rowcount">
          <el-col :span="25">
            <el-input v-model.number="form.rowcount"></el-input>
          </el-col>
        </el-form-item>

        <el-form-item label="每行座位数" prop="seatsrow">
          <el-col :span="25">
            <el-input v-model.number="form.seatsrow"></el-input>
          </el-col>
        </el-form-item>


        <el-form-item label="状态" prop="statu">
          <template>
            <el-select v-model.number="form.statu" placeholder="请选择状态" clearable>
              <el-option
                  v-for="item in statuss"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
              </el-option>
            </el-select>
          </template>
        </el-form-item>

      </el-form>
      <span slot="footer" class="dialog-footer">
    <el-button @click="dialogVisible = false">取 消</el-button>
    <el-button type="primary" @click="save">确 定</el-button>
    </span>
    </el-dialog>

    <el-dialog title="安排座位"
               :visible.sync="seatdialogVisible"
               :width="arrangeDialogWidth"
               center>
      <div style="display:flex; align-content: center">
        <span style="margin-right: 50px">启用</span>
        <span class="seat" :class="isSelected[0]"></span>
        <span style="margin: 0 50px">禁用</span>
        <span class="seat" :class="isSelected[1]"></span>
      </div>

      <div class="row" v-for="(value, rowNum) in seats" :key="rowNum">
        <span style="margin-right: 100px">{{rowNum}}排</span>
        <div style="display: flex; justify-content: flex-end">
        <span class="seat"
              :class="isSelected[seats[rowNum][index]]"
              @click="pressSeat(rowNum, index)"
              v-for="(seat, index) in value">
        </span>
        </div>
      </div>

    <span slot="footer" class="dialog-footer">
    <el-button @click="seatdialogVisible = false">取 消</el-button>
    <el-button type="primary" @click="seatsave">确 定</el-button>
    </span>
    </el-dialog>
  </div>
</template>

<style>
.row{
  white-space: nowrap;
  margin-top: 10px;
  padding: 0 10px;
  text-align: center;
  display: flex;
  justify-content: space-between;
}
.seat{
  display: inline-block;
  width: 30px;
  height: 26px;
  margin: 0 5px;
  background-position: 0 1px;
}
.seat-default {
  background: url("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAB4AAAAeCAYAAAA7MK6iAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAAEnQAABJ0Ad5mH3gAAAHKSURBVEhLxZfbysIwDMezKeJUVAR9A/H9X0FQZMOLeem14sWc4AnP9muybM4557eT/mCYdLT/tknaqQgJ/ACVf7/Oz4TfbvVisYDlcgm1Wg2SRON0OoGqqtDr9bglAAoHGY1GQgqzl5zz+SyGwyF7z7wITyYTtrJD13W2HrzEuFqtspUdnU4Hbrcbew5fSa5SqfQbYbmzoCgKew5fK6disciWgyd8vV5hPB5DoVAA27bpSct6vQbLsmjFhmFwK0MpJjFNky2H3W4nZEf24rPZbMTlcmFPiPv9LgaDAXu+rNY0jS0HzG5Zh+zFRwo9bS/GuNvtgpwM+SQsVwetVosa/GDnpMhFsfUAJ3I4HMgmYRQIZl1awsbzTyYyqzHRkhK2Yj+RwklwBYMHRpDMhd0tTrXiT52jwCsxCrqPZc3RARLM7O1266V/XMrlMlQqFfYcVqsVZXa9Xo8Wzhq/cOYx/i+e8KeYZIFfgyyMxWw2o4Y8mc/nXty9jz08NqfTKTSbTapBLAt8kh6buDocGh+3xNrtNjQaDbLxRSj7/f7pNolLv98Xx+ORvVfeBla+S3VJ4K7hGO/I7S8MDht2UbjklspRokj+NRQKwB/pWISi3oSUQQAAAABJRU5ErkJggg==") no-repeat;
}
.seat-choose{
  background: url("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAB4AAAAeCAYAAAA7MK6iAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAAEnQAABJ0Ad5mH3gAAAKVSURBVEhLxZXPThpRFMY/hhFBG4okbdiYxgXpxmJi0phi4sKlL+HWlSsfwMSFr+DKjSufwMSFiyakaaagQaOxW1txUUjDHwVh5vacmdtxgGEsA1N/yYHLvdzzzT33nDMhQeAFUOT3f+fFhAeGutRs4tfpKV5dXECoqpz9R8hlKxqFsrqK96mUnOzGVfhrsYh3+/tIXV8Dk5Nydkh0HW3a+21zE59WVuTkE33C5/k8PmxvW4ITE/SPkFzxQacDUOS0nR18XFiQkxZ9dzx9dARwaCOR0UQZ9kP29vAQupz6S39y8UmVMeZcOIwInfp54QAQdJDe2LkLt9tAq2Vmp294L/vhe6Zxb13YwrSMvKZRZMIoLy2hvLYG3N/7E+c9FN7f2SzK8/MQiQS0szO5aGFndfHkBJndXSAeNzc2ZmehUw3GCwUru4eBTlpbXETs6grqwwNgGBBkX7a2kF1eNv9inziWy1minFxU/NM3N2jHYr5PbJAPtdGwHpp8hiiS6YMDUPBNTOE6WfL21sxAGx7Xav5KivaIx0fy7kghGqv0QHR+6yd/GGQUcx52M0Idu+0UDn+ORxovLp24i8CEO9SrvQhM+LlL8hQWnJHcBHxYiKvDA7OOqzTobGwgWalYjZ2hjlObm0N7Zmb4kqIkipZKmLq7e6oU8ldJJqHu7YGK1kOYke3OF1xKzvLsEfa+Y34IDrcfc4q6YAsrfDcBozgy3RSeIvuRyVidinsrNfixW7WKn+m0qcXYLwlum9+Pj5G4vITO709KEDaDmrsfFPLBrtnMrkhvqDfr63gt13nBlYZhiFyhIH8Nz2dNE005dmNgcgl6Fxv8dvGJXq9DcIgHYId63LBbvqpBeJfTCHiJMoEJewP8Afy6sw903o8jAAAAAElFTkSuQmCC") no-repeat;
}
</style>
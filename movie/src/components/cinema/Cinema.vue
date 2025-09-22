<template>
  <div class="cinema-form-container">
    <!-- Cinema Image with overlay -->
    <div class="cinema-image-container">
      <el-image
          :src="cinema.imageUrl"
          alt="Cinema Image"
          class="cinema-image"
          :preview-src-list="[cinema.imageUrl]"
          fit="cover"
          @click.stop>
      </el-image>
      <div class="image-overlay">
        <h1 class="cinema-name">{{ cinema.name }}</h1>
        <div class="business-hours">
          <span class="hours-icon">🕒</span>
          {{ cinema.openingTime || '08:00' }} - {{ cinema.closingTime || '22:00' }}
        </div>
      </div>
    </div>

    <!-- Main Info Section - 修改为全屏宽度 -->
    <div class="full-width-info-section">
      <div class="full-width-info-card">
        <!-- 影院地址 -->
        <div class="form-item">
          <div class="form-label">
            <span class="icon">📍</span>
            <span>影院地址:</span>
          </div>
          <div class="form-content">
            {{ cinema.address || '暂无地址信息' }}
          </div>
        </div>

        <!-- 联系方式 -->
        <div class="form-item">
          <div class="form-label">
            <span class="icon">📞</span>
            <span>联系方式:</span>
          </div>
          <div class="form-content">
            <div class="form-content" >
              {{ cinema.phone || '暂无联系电话' }}
            </div>
          </div>
        </div>

        <!-- 影厅类型 -->
        <div class="form-item">
          <div class="form-label">
            <span class="icon">🎬</span>
            <span>影厅类型:</span>
          </div>
          <div class="form-content">
            <div class="hall-types-container">
              <span v-for="type in cinema.hallTypes"
                    :key="type"
                    class="hall-type"
                    :class="getHallTypeClass(type)">
                {{ type }}
              </span>
              <span v-if="!cinema.hallTypes || cinema.hallTypes.length === 0"
                    class="no-data">
                暂无影厅类型信息
              </span>
            </div>
          </div>
        </div>

        <div class="action-buttons">
          <el-button type="primary" class="edit-button" @click="mod()">
            <i class="iconfont icon-r-edit"></i>
            修改影院信息
          </el-button>
        </div>

        <el-dialog
            title="影院信息"
            :visible.sync="dialogVisible"
            width="40%"
            center>
          <el-form ref="cinema" :model="cinema" label-width="80px" :rules="rules">
            <el-form-item label="影院名称" prop="name">
              <el-input v-model="cinema.name" placeholder="请输入影院名称"></el-input>
            </el-form-item>
            <el-form-item label="影院地址" prop="address">
              <el-input v-model="cinema.address" placeholder="请输入影院地址"></el-input>
            </el-form-item>
            <el-form-item label="影院电话" prop="phone">
              <el-input v-model="cinema.phone" placeholder="请输入影院电话"></el-input>
            </el-form-item>
            <el-form-item label="营业时间" >
              <el-time-select
                  placeholder="起始时间"
                  v-model="cinema.openingTime"
                  :picker-options="{
                    start: '07:30',
                    step: '00:30',
                    end: '23:30'
                  }">
              </el-time-select>
              <span style="margin: 0 10px;">至</span>
              <el-time-select
                  placeholder="结束时间"
                  v-model="cinema.closingTime"
                  :picker-options="{
                    start: '07:30',
                    step: '00:30',
                    end: '23:30',
                    minTime: this.cinema.openingTime
                  }"
                  prop="closingTime"
              >
              </el-time-select>
            </el-form-item>
            <el-form-item label="影厅类型" prop="hallTypes" >
              <el-select
                  v-model="cinema.hallTypes"
                  multiple
                  filterable
                  allow-create
                  default-first-option
                  placeholder="请选择或输入影厅类型"
                  style="width: 524.7px"
                  @change="onHallTypesChange">
                <el-option
                    v-for="item in defaultHallTypes"
                    :key="item"
                    :label="item"
                    :value="item">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="影院图片">
              <el-upload
                  class="avatar-uploader"
                  action="http://localhost:9999/upload"
                  :show-file-list="false"
                  :on-success="handleAvatarSuccess"
                  :before-upload="beforeAvatarUpload">
                <img v-if="cinema.imageUrl" :src="cinema.imageUrl" class="avatar">
                <i v-else class="el-icon-plus avatar-uploader-icon"></i>
              </el-upload>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="doMod">修改信息</el-button>
              <el-button @click="dialogVisible=false">取消</el-button>
            </el-form-item>
          </el-form>
        </el-dialog>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CinemaInfoForm',
  data() {
    return {
      cinema: {
        id:'',
        name: '',
        address: "",
        phone: "",
        openingTime: '',
        closingTime: '',
        hallTypes: [],
        imageUrl: "",
        value:'',
      },
      defaultHallTypes: [
        '普通厅',
        '3D厅',
        'IMAX厅',
        '杜比全景声厅',
        '4DX厅',
      ],
      rules:{
        name:[
          {required: true, message: '请输入影院名称', trigger: 'blur' }],
        address:[
          {required: true, message: '请输入影院地址', trigger: 'blur' }],
        phone:[
          {required: true, message: '请输入影院电话', trigger: 'blur' },
          { pattern: /^[\d\-\+ ]+$/, message: '电话只能包含数字、+、- 或空格', trigger: 'blur' }
        ],
        hallTypes: [
          { type: 'array', required: true, message: '请至少选择一个影院类型', trigger: 'change' }
        ],
      },
      dialogVisible:false,
    }
  },
  created() {
    this.loadCinemaData();
  },
  methods: {
    onHallTypesChange(newTypes) {
      newTypes.forEach(type => {
        if (!this.defaultHallTypes.includes(type)) {
          this.defaultHallTypes.push(type); // 加入下拉列表
        }
      });
    },
    async mod(){//编辑赋值
      this.dialogVisible=true
    },
    doMod(){
      this.$refs.cinema.validate((valid) => {
        if (valid) {
          if(this.cinema.id){
            this.$axios.post(this.$httpUrl + '/cinema/mod',this.cinema).then(res => res.data).then(res => {
              console.log(res)//处理数据，只要data
              if(res.code===200){//封装数据
                this.$message.success("操作成功");
                this.dialogVisible = false;
                this.$emit('refresh')
              }else{
                this.$message.error("操作失败");
              }
            })
          }
        } else {
          console.log('error submit!!');
          return false;
        }
      });
    },
    async loadCinemaData() {
      const response = await this.$axios.get(this.$httpUrl + '/cinema/list');
      if (response.data && response.data.length > 0) {
        this.cinema = response.data[0];
      }
    },
    getHallTypeClass(type) {
      const map = {
        '普通厅': 'default',
        '3D厅': 'three-d',
        'IMAX厅': 'imax',
        '杜比全景声厅': 'dolby',
        '4DX厅': 'four-dx',
      };
      return map[type] || 'default';
    },
    handleAvatarSuccess(res, file) {
      this.cinema.imageUrl=res.data.url
      this.imageUrl = URL.createObjectURL(file.raw);
    },
    beforeAvatarUpload(file) {
      const typeArr = ["image/jpeg","image/png","image/jpg"]
      if(!typeArr.includes(file.type)){
        this.$message.error("上传图片只能是JPG、PNG、JPEG格式!")
        return false
      }
      const isLt2M = file.size / 1024 / 1024 < 2;
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 2MB!');
      }
      return isLt2M
    },
  }
}
</script>

<style scoped>
/* 基础容器样式 */
.cinema-form-container {
  width: 100%;
  max-width: 100%;
  margin: 0;
  padding: 0;
  background: #f5f7fa;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  font-size: 18px;
}

/* 图片区域样式 */
.cinema-image-container {
  position: relative;
  height: 400px;
  overflow: hidden;
  width: 100%;
}

.cinema-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.cinema-image-container:hover .cinema-image {
  transform: scale(1.03);
}

.image-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  padding: 30px;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.7) 0%, rgba(0, 0, 0, 0.3) 70%, transparent 100%);
  color: white;
}

.cinema-name {
  margin: 0;
  font-size: 42px;
  font-weight: 600;
  text-shadow: 0 3px 6px rgba(0, 0, 0, 0.5);
}

.business-hours {
  margin-top: 12px;
  font-size: 22px;
  display: flex;
  align-items: center;
}

.hours-icon {
  margin-right: 12px;
  font-size: 24px;
}

/* 全宽信息区域样式 */
.full-width-info-section {
  width: 100%;
  padding: 40px 0;
  background: #f5f7fa;
}

.full-width-info-card {
  width: 90%;
  max-width: 1200px;
  margin: 0 auto;
  background: white;
  border-radius: 14px;
  padding: 40px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}

/* 表单项样式 */
.form-item {
  display: flex;
  margin-bottom: 30px;
  align-items: center;
}

.form-label {
  display: flex;
  align-items: center;
  min-width: 160px;
  color: #555;
  font-size: 22px;
  font-weight: 500;
}

.form-label .icon {
  margin-right: 15px;
  font-size: 26px;
}

.form-content {
  flex: 1;
  color: #333;
  font-size: 22px;
  line-height: 1.6;
}

/* 影厅类型样式 */
.hall-types-container {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.hall-type {
  display: inline-block;
  padding: 12px 20px;
  border-radius: 8px;
  font-size: 20px;
  font-weight: 500;
  transition: all 0.3s;
}

.hall-type.default {
  background: #f0f2f5;
  color: #606266;
  border: 1px solid #e4e7ed;
}

.hall-type.imax {
  background: rgba(0, 102, 204, 0.1);
  color: #0066cc;
  border: 1px solid rgba(0, 102, 204, 0.3);
}

.hall-type.dolby {
  background: rgba(153, 0, 255, 0.1);
  color: #9900ff;
  border: 1px solid rgba(153, 0, 255, 0.3);
}

.hall-type.three-d {
  background: rgba(0, 150, 136, 0.1);
  color: #009688;
  border: 1px solid rgba(0, 150, 136, 0.3);
}

.hall-type.four-dx {
  background: rgba(233, 30, 99, 0.1);
  color: #e91e63;
  border: 1px solid rgba(233, 30, 99, 0.3);
}

/* 操作按钮样式 */
.action-buttons {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

.edit-button {
  font-size: 22px;
  padding: 15px 30px;
}

.edit-button i {
  font-size: 20px;
  margin-right: 8px;
}

/* 上传图片 */
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}
.avatar {
  width: 178px;
  height: 178px;
  display: block;
}

/* 响应式调整 */
@media (max-width: 992px) {
  .full-width-info-card {
    width: 95%;
    padding: 30px;
  }

  .cinema-name {
    font-size: 36px;
  }

  .form-label {
    font-size: 20px;
    min-width: 140px;
  }

  .form-content {
    font-size: 20px;
  }

  .hall-type {
    font-size: 18px;
    padding: 10px 16px;
  }
}

@media (max-width: 768px) {
  .cinema-image-container {
    height: 300px;
  }

  .cinema-name {
    font-size: 32px;
  }

  .business-hours {
    font-size: 18px;
  }

  .full-width-info-card {
    padding: 25px;
  }

  .form-item {
    flex-direction: column;
    align-items: flex-start;
    margin-bottom: 25px;
  }

  .form-label {
    width: 100%;
    margin-bottom: 10px;
  }

  .edit-button {
    font-size: 20px;
    padding: 12px 24px;
  }
}

@media (max-width: 480px) {
  .cinema-name {
    font-size: 28px;
  }

  .full-width-info-card {
    padding: 20px;
    border-radius: 10px;
  }

  .form-label {
    font-size: 18px;
  }

  .form-content {
    font-size: 18px;
  }

  .hall-type {
    font-size: 16px;
    padding: 8px 14px;
  }

  .edit-button {
    font-size: 18px;
    padding: 10px 20px;
  }
}
</style>
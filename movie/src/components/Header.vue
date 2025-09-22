<script>
export default {
  name:"Header",
  data(){
    return {
      user:JSON.parse(sessionStorage.getItem('CurUser'))
    }
  },
  props:{
    icon:String,
  },
  methods:{
    toUser(){
      this.$router.push("/Self")
    },
    logout(){
      this.$confirm("您确定要退出登录吗?",'提示',{
        confirmButtonText:"确定",
        type:"warning",
        center:true
      })
          .then(()=>{
            this.$message({
              type:"success",
              message:"退出登录成功"
            })
            this.$router.push("/")
            sessionStorage.clear()
          })
          .catch(()=>{
            this.$message({
              type:'info',
              message:'已取消登录'
            })
          })
    },
    collapse(){
      this.$emit('doCollapse')
    }
  },
  created() {
    this.$router.push("/Home")
  }
}
</script>

<template>
  <div style="display: flex;line-height: 60px;font-size: 20px">
  <div style="margin-top: 8px;cursor:pointer">
    <i :class="icon" style="font-size: 20px" @click="collapse"></i>
  </div>
  <div style="flex: 1;text-align: center;font-size: 30px">
    <span>秋水影院后台管理系统</span>
  </div>
    <el-dropdown>
      <span>{{user.username}}<i class="el-icon-arrow-down" style="margin-left: 5px"></i>
         <el-avatar src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"></el-avatar>
      </span>
      <el-dropdown-menu slot="dropdown">
        <el-dropdown-item @click.native="toUser">个人资料</el-dropdown-item>
        <el-dropdown-item @click.native="logout">退出登录</el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>
  </div>
</template>

<style scoped>

</style>
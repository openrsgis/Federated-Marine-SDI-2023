<template>
  <div v-if="showProgress" style="width: 97%">
    <span class="title">
      {{'处理进度'}} 
      <span> {{progressStatus==''? '' : progressStatus}} </span> 
    </span>
    <a-progress
      :stroke-color="{
        from: '#108ee9',
        to: '#87d068',
      }"
      :percent="progressPercent"
      status="active"
      id="progress"
      :stroke-width="15"
    />
  </div>
</template>

<script>
import axios from 'axios'

export default {
  props: {
    apiUrl: String,
  },
  data() {
    return {
      progressPercent: 0,
      progressStatus:'',
      refreshHandle: null,
      showProgress:false,
    }
  },
  methods: {
    getProgressPercent(url) {
      return axios.get(url, {withCredentials: true}).then(
        response => {
          var reg = /\d+%$/;
          console.log('reg=',reg)
          var progressPercent = parseInt(reg.exec(response.data));
          console.log('progressPercent',progressPercent)
          console.log("分析进度：",response.data);
          if(response.data){
            //解析状态：
            let arr=response.data.split(',');
            if(arr[0] == "STARTED"){
              this.progressStatus=":  "+"正在开始";
            }
            else if(arr[0] == "FAILED"){
              this.progressStatus=":  "+"分析失败";
              window.clearInterval(this.refreshHandle);
              this.$emit("onCalDone","failed"); //调用父组件，通知完成
              this.$message.error('在线分析遇到错误，服务器终止计算');  
            }
            else if(arr[0] == "RUNNING"){
              this.progressStatus=":  "+"执行中";
            }
            else if(arr[0] == "FINISHED"){
              this.progressStatus=":  "+"分析完成";
            }

            return new Promise((resolve) => {
              resolve(progressPercent)
            })
          }else{
            window.clearInterval(this.refreshHandle);
            this.$emit("onCalDone","failed"); //调用父组件，通知完成
            this.$message.error('获取分析进度失败,分析进度为空');  
          }
          response => {
            this.$message.error('获取分析进度失败');  
        }
        },
      )
    },
    startRefresh(url) {
      this.stopRefresh()
      this.progressPercent = 0
      this.showProgress = true
      this.refreshHandle = window.setInterval(() => {
        this.getProgressPercent(url).then((progressPercent) => {
          console.log(url)
          console.log('refreshing');
          this.progressPercent = progressPercent
          if(this.progressPercent == 100){
            this.stopRefresh();
            this.$emit("onCalDone",url); //调用父组件，通知完成
          }
        })
      }, 500)
    },
    stopRefresh() {
      window.clearInterval(this.refreshHandle)
      //this.showProgress = false
    },
  },
  mounted() {
    // this.startRefresh()
  },
  beforeDestroy(){
      this.stopRefresh()
  },
  watch: {
    progressPercent(value) {
      if (value == 100) {
        this.stopRefresh()
      }
    },
  },
}
</script>

<style scoped>
#progress {
  width: 100%;
}
.title {
  font-size: 14px; 
  font-weight: bold;
}
</style>
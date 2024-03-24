<template>
  <div >
    <a-table
      :dataSource="tableData"
      style="width: 100%"
      :bordered="true"
      :columns="columns"
      :pagination="pagination" 
      :rowSelection="{
        columnTitle:'选择预览',
        onSelect: handleSelectionChange,
        columnWidth: '70px'
      }"
      size="small"
      :scroll="{x:350}"
    >
    </a-table>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'ResultsTable',
  data() {
    return {
      currentPage: 1, //默认显示页面为1
      pagesize: 5, // 每页的数据条数
      tableData: [], //需要data定义一些，tableData定义一个空数组，请求的数据都是存放这里面
      columns:[
    {
      title:'名称',
      dataIndex: 'name',
      key: 'name',
      align: "center"
    },
    // deprecated
    {
      title: '时间',
      dataIndex: 'time',
      key: 'time',
      align: "center"
    },
    {
      title: '范围',
      dataIndex: 'extent',
      key: 'extent',
      align:'center'
    }
],
      pagination:{ //分页器
        pageSize: 5,
        showSizeChanger: true,
        showQuickJumper: true,
        pageSizeOptions: ["5", "10", "20", "50"],//每页中显示的数据
        //showTotal: total,  //分页中显示总的数据
        position: "top",
        size: "small",
      }
    }
  },
  mounted() {
    //先移除'updateTabledata'事件监听，因为该组件会被实例化很多次，
    //若不移除，在第二次实例化之后，将会接收不到消息（因为每次实例化都会监听一次updateTabledata）
    // this.$bus.$off(); //还是不行

    let self = this;
    //监听'updateTableData'事件
    this.$bus.$on('updateTabledata', function (val) {
      console.log("table组件接收");
      //结果对象转为子组件table所需数组
      var arrs = [];
      for (let i in val) {
        let extentp = "";
        for(let xxx in val[i]){//获取范围
          if(xxx == 'extent' || xxx == 'rasterExtent'){
            extentp = val[i][xxx];
          }
        }
        let tiemtp = "";
        for(let xxx in val[i]){//获取范围
          if(xxx == 'time' ){
            tiemtp = val[i][xxx];
          }
        }        
        for(let j in val[i]){ //获取图片path
          if(j=="path"  || j=='rasterPath'){
            let obj = {
              path: val[i][j],
              extent: extentp,
              time: tiemtp
            }
            arrs.push(obj);
          }
        }
        for(let jjj in val[i]){ //获取json矢量path
          if(jjj=="vectorPath"){
            let obj = {
              path: val[i][jjj],
            }
            arrs.push(obj);
          }
        }        
      }

      //将path转为name
      for (let x of arrs) {
        let filename = x['path'].substring(x['path'].lastIndexOf('/') + 1)
        x.name = filename
        x.key = filename
      }
      self.tableData = arrs; //这一步是必须，但是why？
      this.tableData = self.tableData;    
      console.log('arr3',this.tableData)  
    });
  },
  methods: {
    //checkbox状态改变
    handleSelectionChange(record, selected, selectedRows, nativeEvent){
      let renderItem ={
          addRemove: selected ? 1: 0,
          item: record
      }
    //将选中的图片的url和extent通过父组件传给viewer渲染
      this.$emit('renderAnalysisResult', renderItem, true)
    }
  },
  beforeDestroy() {       
    this.$bus.$off('updateTabledata');//销毁事件监听器
  }
}
</script>

<style >

</style>
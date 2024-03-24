<template>
    <div class="OLAP">
        <div style="padding-top:20px">
            <a-spin :spinning="spinning" />
            <span class="OGCtitle">{{title}}</span>
        </div>
        <a-form
            :form="form"
            layout="horizontal"
        >
          <a-form-item :label="'产品类型'" class="formItem">
              <a-select 
                v-decorator="[ 'rasterProductName', { rules: [{ required: true, 
                             message: 'Please select a raster product type!' }] },]"
                placeholder="Please select a raster product type"
                @dropdownVisibleChange="onProductSelectDropdown">
                <a-select-option v-for="product in productList" :key="product">
                    {{ product }}
                </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item :label="'省'" class="formItem">
              <a-select 
                v-decorator="[ 'provinceName', { rules: [{ required: false, 
                             message: 'Please select a province!' }] },]"
                placeholder="Please select a province"
                @dropdownVisibleChange="onProvinceSelectDropdown">
                <a-select-option v-for="province in provinceList" :key="province">
                    {{ province }}
                </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item :label="'市'" class="formItem">
              <a-select 
                v-decorator="[ 'cityName', { rules: [{ required: true, 
                             message: 'Please select a city!' }] },]"
                placeholder="Please select a city"
                @dropdownVisibleChange="onCitySelectDropdown">
                <a-select-option v-for="city in cityList" :key="city">
                    {{ city }}
                </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item :label="'年'" class="formItem">
              <a-select 
                v-decorator="[ 'year', { rules: [{ required: true, 
                             message: 'Please select a year!' }] },]"
                placeholder="Please select a year"
                @dropdownVisibleChange="onYearSelectDropdown">
                <a-select-option v-for="year in yearList" :key="year">
                    {{ year }}
                </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item :label="'月'" style="margin-bottom: 10px; font-weight: bold" class="formItem">
              <a-select 
                v-decorator="[ 'month', { rules: [{ required: false, 
                             message: 'Please select a month!' }] },]"
                placeholder="Please select a month"
                @dropdownVisibleChange="onMonthSelectDropdown"
                style="font-weight: normal">
                <a-select-option v-for="month in monthList" :key="month">
                    {{ month }}
                </a-select-option>
            </a-select>
          </a-form-item>
        <div style="text-align: right">
            <a-button type="primary" icon="search" :disabled="btnDisable" :loading="searchIconLoading" @click="onSubmit">{{'提交'}}</a-button>
        </div>

          
        </a-form>
        <analysis-progress ref="progressBar" @onCalDone = "onProcessDone" />

    </div>
</template>
<script>
import AnalysisProgress from '../Progress/AnalysisProgress'
import RequestURL from '../../../modules/requestURL'
import axios from 'axios'
import requestURL from '../../../modules/requestURL'
export default {
    components: {
        AnalysisProgress
    },
    props: ['title', 'olapType'],
    data() {
        return {
            spinning: false,
            form: this.$form.createForm(this, {name: 'OLAP_form'}),
            btnDisable: false,
            searchIconLoading: false,
            productList: [],
            provinceList: [],
            cityList: [],
            yearList: [],
            monthList: []
        }
    },
    methods: {
        onProductSelectDropdown(open){
            if(open && this.productList.length === 0){
                this.spinning = true
                let url = new RequestURL("olapProducts", {}).getRequestURL()
                axios.get(url, {timeout: 4000}).then(response=>{
                    if(response.data.result){
                        this.productList = response.data.result
                    }
                    this.spinning = false
                },
                response=>{
                    this.$message.error('请求产品名称失败');  
                    this.spinning = false;
                })
            }
        },
        onCitySelectDropdown(open){
            if(open && this.cityList.length === 0){
                this.spinning = true
                let url = new RequestURL("olapCities", {}).getRequestURL()
                axios.get(url, {timeout: 4000}).then(response=>{
                    if(response.data.result){
                        this.cityList = response.data.result
                    }
                    this.spinning = false
                },
                response=>{
                    this.$message.error('请求城市名称失败');  
                    this.spinning = false;
                })
            }
        },
        onYearSelectDropdown(open){
            if(open && this.yearList.length === 0){
                this.spinning = true
                let url = new RequestURL("olapYears", {}).getRequestURL()
                axios.get(url, {timeout: 4000}).then(response=>{
                    if(response.data.result){
                        this.yearList = response.data.result
                    }
                    this.spinning = false
                },
                response=>{
                    this.$message.error('请求年份数据失败');  
                    this.spinning = false;
                })
            }
        },
        onProvinceSelectDropdown(open){
            
        },
        onMonthSelectDropdown(open){

        },
        onSubmit(){
            this.form.validateFields((err, values) => {
                if(! err){
                    let postData = {
                        InputParams: {
                            ...values
                        }
                    }
                    postData.InputParams.month = 'null'
                    postData.InputParams.provinceName = 'null'

                    let url = new requestURL('olapAnalysisSubmit', {type: this.olapType}).getRequestURL()
                    axios.post(url, postData,{withCredentials: true}).then(response=>{
                        console.log("分析任务ID：", response.data);
                        this.btnDisable = true;
                        this.searchIconLoading = true;
                        let job_id = response.data; //返回uuid
                        let urlID = url  + job_id ;
                        this.$refs.progressBar.startRefresh(urlID);
                    },
                    response=>{
                        this.$message.error("请求在线分析结果失败");
                    })
                }
            })
        },
        onProcessDone(processURL){
            this.btnDisable = false;
            this.searchIconLoading = false;  

            if(processURL !== 'failed'){
                //请求分析的结果  
                let url =processURL + '/results'
                axios.get(url, {withCredentials: true}).then(
                (response) => {
                    let resultData = response.data
                    console.log('请求在线分析结果成功')
                    console.log(resultData)
                    this.$emit('calDone'); //通知父组件，计算完成
                    this.$bus.$emit('updateTabledata', resultData); //将计算结果传给table，
                },
                (response) => {
                    this.$message.error("请求在线分析结果失败");
                }
                )
            }
        },
    },
}
</script>
<style>
.OLAP{
    margin:10px;
}
.OGCtitle{
    font-weight: bold;
    font-size: 20px;
    margin-left: 2px;
    text-align: left; 
}
.formItem{
    margin-bottom: 0;
    text-align: left; 
}
.formItem label{
    font-weight: bold;
    text-align: left; 
}
</style>
<template>
  <div class="AnalysisForm">
    <a-form :form="form" layout="vertical" class="a-form">
      <a-form-item>
        <a-spin :spinning="spinning" />
        <span class="OGCtitle">{{ title }}</span>
      </a-form-item>

      <a-form-item class="boldTitle" :label="'产品类型'" has-feedback>
        <a-select
          class="thinTitle"
          v-decorator="[
            'productType',
            { rules: [{ required: true, message: 'Please select a raster product type!' }] },
          ]"
          placeholder="Please select a raster product type"
          @dropdownVisibleChange="onProductSelectDropdown"
        >
          <a-select-option v-for="product in productList" :key="product">
            {{ product }}
          </a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item class="boldTitle" :label="'矢量产品'" v-if="vectorSetVisible" has-feedback>
        <a-select
          class="thinTitle"
          v-decorator="['vectorType', { rules: [{ required: true, message: 'Please select a vector product type!' }] }]"
          placeholder="Please select a vector product type"
          @dropdownVisibleChange="onVectorSelectDropdown"
        >
          <a-select-option v-for="vector in vectorProductList" :key="vector">
            {{ vector }}
          </a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item class="boldTitle" :label="'空间范围'">
        <a-row class="thinTitle" v-decorator="['nothing', { rules: [{ required: true }], initialValue: 'nothing' }]">
          {{ '经度' }}
        </a-row>
        <a-form-item class="inlineItem" has-feedback>
          <a-input-number
            style="width: 100%"
            :min="-180"
            :max="180"
            :precision="10"
            v-decorator="[
              'minLon',
              { initialValue: initData.minLon, rules: [{ required: true, message: 'Please input min lontitude' }] },
            ]"
            placeholder="Min Lontitude"
          />
        </a-form-item>
        <span class="inlineSpan">~</span>
        <a-form-item class="inlineItem" has-feedback>
          <a-input-number
            style="width: 100%"
            :min="-180"
            :max="180"
            :precision="10"
            v-decorator="[
              'maxLon',
              { initialValue: initData.maxLon, rules: [{ required: true, message: 'Please input max lontitude' }] },
            ]"
            placeholder="Max Lontitude"
          />
        </a-form-item>
        <a-row class="thinTitle">{{ '纬度' }}</a-row>
        <a-form-item class="inlineItem" has-feedback>
          <a-input-number
            style="width: 100%"
            :min="-90"
            :max="90"
            :precision="10"
            v-decorator="[
              'minLat',
              { initialValue: initData.minLat, rules: [{ required: true, message: 'Please input min latitude' }] },
            ]"
            placeholder="Min Latitude"
          />
        </a-form-item>
        <span class="inlineSpan">~</span>
        <a-form-item class="inlineItem" has-feedback>
          <a-input-number
            style="width: 100%"
            :min="-90"
            :max="90"
            :precision="10"
            v-decorator="[
              'maxLat',
              { initialValue: initData.maxLat, rules: [{ required: true, message: 'Please input max latitude' }] },
            ]"
            placeholder="Max Latitude"
          />
        </a-form-item>
      </a-form-item>

      <a-form-item :label="'时间范围'" style="textalign: center; font-weight: bold" has-feedback>
        <a-range-picker
          v-decorator="[
            'dateRange',
            {
              initialValue: [moment(initData.startTime, 'YYYY-MM-DD'), moment(initData.endTime, 'YYYY-MM-DD')],
              rules: [{ required: true, message: 'Please select date range' }],
            },
          ]"
        />
      </a-form-item>

      <a-form-item :style="{ textAlign: 'right' }">
        <a-button type="primary" icon="search" :disabled="btnDisable" :loading="serchIconLoading" @click="onSubmit">{{
          '提交'
        }}</a-button>
      </a-form-item>
    </a-form>
    <analysis-progress ref="progressBar" @onCalDone="onProcessDone"></analysis-progress>
  </div>
</template>

<script>
import axios from 'axios'
import qs from 'qs'
import AnalysisProgress from '../Progress/AnalysisProgress'
import requestURL from '../../../modules/requestURL'
import moment from 'moment'

export default {
  components: {
    AnalysisProgress,
  },
  name: 'DatacubeRetrivalForm',
  props: {
    title: String,
    type: String,
    initData: Object,
    productNeeded: {
      type: Array, ///type: [String, Number],
      default: ['_ARD_EO'], //需要的产品类型，默认只通过_ARD_EO，若需要矢量可通过['_EO','_Vector']
    },
  },
  data() {
    return {
      form: this.$form.createForm(this, { name: 'AnalysisForm' }),
      spinning: false, //控制是否正在加载
      serchIconLoading: false,
      productList: [], //raster 产品
      vectorProductList: [],
      btnDisable: false, //提交按钮是否disable
      vectorSetVisible: false, //vector 产品选择列表是否visible
    }
  },
  mounted() {
    //this.initFormData(this.initData); //设置初始值 deprecated
    this.initForm()
  },
  methods: {
    moment,
    //初始化form
    initForm() {
      let bShowVectorSlt = false
      for (const str of this.productNeeded) {
        if (str.indexOf('_Vector') != -1) {
          //若指定产品类型列表中需要vector
          bShowVectorSlt = true
        }
      }
      this.vectorSetVisible = bShowVectorSlt
    },

    //产品类型选择器：初始化产品类型选择条item
    initProductSelectorItem() {
      this.spinning = false
      this.spinning = true //开启等待旋转
      let url = new requestURL('productNames', {}).getRequestURL()
      //向服务器发送获取产品名称请求
      // axios.defaults.withCredentials = false; // 让 ajax bu携带 cookie
      axios.get(url, { timeout: 4000 }).then(
        (response) => {
          console.log('请求产品请求成功！')
          console.log(response.data.result)
          //删除_vector的type
          let typeArr = []
          for (const ix of this.productNeeded) {
            let bVector = ix.indexOf('_Vector') != -1
            if (!bVector) {
              typeArr.push(ix)
            }
          }
          //根据指定产品类型过滤出所需产品
          let tempArr = []
          for (const product of response.data.result) {
            for (const str of typeArr) {
              let proName = product.productName
              if (proName.indexOf(str) != -1 && proName.indexOf('_TMS') == -1) {
                tempArr.push(proName)
              }
            }
          }
          //数组去重
          this.productList = Array.from(new Set(tempArr))
          this.spinning = false //关闭等待旋转
        },
        response => {
          this.$message.error('请求产品名称失败')
          this.spinning = false //关闭等待旋转
        }
      )
    },
    //vector产品类型选择器：初始化vector产品类型选择条item
    initVectorProductSelectorItem() {
      this.spinning = false
      this.spinning = true //开启等待旋转
      let url = new requestURL('productNames', {}).getRequestURL()
      //向服务器发送获取产品名称请求
      // axios.defaults.withCredentials = false; // 让 ajax bu携带 cookie
      axios.get(url, { timeout: 4000 }).then(
        response => {
          console.log('请求vector产品请求成功！')
          //删除含有_EO的type
          let typeArr = []
          for (const ix of this.productNeeded) {
            let bTMSorVector = ix.indexOf('_EO') != -1
            if (!bTMSorVector) {
              typeArr.push(ix)
            }
          }
          //根据指定产品类型过滤出所需产品
          let tempArr = []
          for (const product of response.data.result) {
            for (const str of typeArr) {
              let proName = product.productName
              if (proName.indexOf(str) != -1) {
                tempArr.push(proName)
              }
            }
          }
          //数组去重
          this.vectorProductList = Array.from(new Set(tempArr))
          this.spinning = false //关闭等待旋转
        },
        response => {
          this.$message.error('请求产品名称失败')
          this.spinning = false //关闭等待旋转
        }
      )
    },
    //rater产品类型选择器：下拉展开时回调
    onProductSelectDropdown(open) {
      if (open && this.productList.length === 0) {
        this.initProductSelectorItem()
      }
    },

    //vector产品类型选择器：下拉展开时回调
    onVectorSelectDropdown(open) {
      if (open && this.vectorProductList.length === 0) {
        this.initVectorProductSelectorItem()
      }
    },

    //计算完成被回调
    onProcessDone(processURL) {
      this.btnDisable = false
      this.serchIconLoading = false

      if (processURL !== 'failed') {
        //请求分析的结果
        let url = processURL + '/results'
        axios.get(url, { withCredentials: true }).then(
          (response) => {
            let resultData = response.data
            console.log('请求在线分析结果成功')
            console.log(resultData)
            this.$emit('calDone') //通知父组件，计算完成
            this.$bus.$emit('updateTabledata', resultData) //将计算结果传给table，
          },
          (response) => {
            this.$message.error('请求在线分析结果失败')
          }
        )
      }
    },

    //提交按钮被点击
    onSubmit(e) {
      //一级判断，判断是否为空
      e.preventDefault()
      this.form.validateFields((err, values) => {
        if (!err) {
          //判断是否需要_Vector
          let postData = {}
          for (const ii of this.productNeeded) {
            if (ii.indexOf('_Vector') != -1) {
              postData = {
                //需要vector
                InputParams: {
                  rasterProductName: values.productType,
                  vectorProductName: values.vectorType,
                  extent: values.minLon + ',' + values.minLat + ',' + values.maxLon + ',' + values.maxLat,
                  startTime: values.dateRange[0].format('YYYY-MM-DD'),
                  endTime: values.dateRange[1].format('YYYY-MM-DD'),
                },
              }
            } else {
              //不需要vector
              postData = {
                InputParams: {
                  rasterProductName: values.productType,
                  extent: values.minLon + ',' + values.minLat + ',' + values.maxLon + ',' + values.maxLat,
                  startTime: values.dateRange[0].format('YYYY-MM-DD'),
                  endTime: values.dateRange[1].format('YYYY-MM-DD'),
                },
              }
            }
          }

          // console.log(qs.stringify(postData));
          let url = new requestURL('onlineAnalysisSubmit', { type: this.type }).getRequestURL()
          // axios.defaults.withCredentials = true; // 让 ajax 携带 cookie
          axios.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded'
          axios
            .post(url, postData, {
              // 单独配置
              withCredentials: true,
            })
            .then(
              (response) => {
                console.log('分析任务ID：', response.data)
                this.btnDisable = true
                this.serchIconLoading = true
                let job_id = response.data //返回uuid
                let urlID = url + job_id
                console.log('urlID',urlID);
                this.$refs.progressBar.startRefresh(urlID)
              },
              (response) => {
                //失败
                this.$message.error('提交分析请求失败')
              }
            )
        }
      })
    },
  },
}
</script>

<style scoped>
#DatacubeRetrivalForm {
  width: 100%;
  height: 100%;
  position: relative;
}
.AnalysisForm {
  margin: 10px;
  height: 100%;
}
.a-form{
  height:500px;
}
.OGCtitle {
  font-weight: bold;
  font-size: 20px;
  margin-left: 2px;
  left: 0px;
}
.ant-form-item {
  margin-bottom: 2px;
}
.boldTitle {
  font-weight: bold;
  text-align: left;
}
.thinTitle {
  font-weight: normal;
  text-align: left;
}
.inlineItem {
  display: inline-block;
  width: calc(50% - 12px);
}
.inlineSpan {
  display: inline-block;
  width: 24px;
  text-align: center;
}
</style>
<template>
  <div>
    <a-drawer :title="$t('singaporeCube')" :width="600" :visible="visible" :bodyStyle="{ paddingBottom: '80px' }"
      :maskClosable="false" :mask="false" :placement="'right'" :wrapStyle="{ paddingTop: '55px' }" @close="onClose">
      <a-form-model ref="ruleForm" :model="form" :rules="rules" :label-col="labelCol" :wrapper-col="wrapperCol">
        <a-form-model-item :label="$t('productDimension')" prop="productDimension" has-feedback>
          <a-select v-model="form.product" :placeholder="$t('pleaseSelectProductDimension')" :allowClear="true"
            @select="onSelectProduct">
            <a-select-option v-for="product in productList" :key="product.productKey">
              {{ product.productName }}
            </a-select-option>
          </a-select>
        </a-form-model-item>

        <a-form-model-item ref="variableDimension" :label="$t('variableDimension')" prop="variableDimension" has-feedback>
          <a-select mode="multiple" v-model="form.variable" :placeholder="$t('pleaseSelectVariableDimension')"
            :allowClear="true">
            <a-select-option v-for="variable in variableList" :key="variable.variableKey">
              {{ variable.variableName }}
            </a-select-option>
          </a-select>
        </a-form-model-item>

        <a-form-model-item :label="$t('extentDimension')" prop="extentDimension" has-feedback>
          <a-input-group compact>
            <a-input-number v-model="form.startX" style="width: 150px; text-align: center"
              :placeholder="$t('longitude')" />
            <a-input style="width: 30px; border-left: 0; pointer-events: none; backgroundcolor: #fff" placeholder=","
              disabled />
            <a-input-number v-model="form.startY" style="width: 150px; text-align: center; border-left: 0"
              :placeholder="$t('latitude')" />
            <a-button type="primary" shape="circle" icon="environment" @click="pointEventSelect" />
            <a-input-number v-model="form.endX" style="width: 150px; text-align: center" :placeholder="$t('longitude')" />
            <a-input style="width: 30px; border-left: 0; pointer-events: none; backgroundcolor: #fff" placeholder=","
              disabled />
            <a-input-number v-model="form.endY" style="width: 150px; text-align: center; border-left: 0"
              :placeholder="$t('latitude')" />
            <a-button type="primary" shape="circle" icon="environment" @click="pointEventSelect" />
          </a-input-group>
        </a-form-model-item>

        <a-form-model-item ref="extentLevel" :label="$t('extentLevel')" prop="extentLevel" has-feedback>
          <a-select v-model="form.extentLevel" :placeholder="$t('pleaseSelectExtentLevel')" :allowClear="true"
            @select="onSelectExtentLevel">
            <a-select-option v-for="variable in Array(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13)" :key="variable">
              {{ variable }}
            </a-select-option>
          </a-select>
        </a-form-model-item>

        <a-form-model-item :label="$t('timeDimension')" prop="timeDimension" has-feedback>
          <a-date-picker v-model="form.startTime" type="date" :placeholder="$t('pleaseSelectTimeDimension')"
            style="width: 100%" :allowClear="true" show-time />
          <a-date-picker v-model="form.endTime" type="date" :placeholder="$t('pleaseSelectTimeDimension')"
            style="width: 100%" :allowClear="true" show-time />
        </a-form-model-item>

        <a-form-model-item :label="$t('progress')" :status="status" :wrapper-col="{ span: 14, offset: 10 }">
          <a-progress type="circle" :percent="percent" />
        </a-form-model-item>

        <a-form-model-item :wrapper-col="{ span: 14, offset: 10 }">
          <a-button style="margin-left: 10px" @click="resetForm"> {{ $t('reset') }} </a-button>
        </a-form-model-item>

      </a-form-model>
      <div :style="{
        position: 'absolute',
        right: 0,
        bottom: '55px',
        width: '100%',
        borderTop: '1px solid #e9e9e9',
        padding: '10px 16px',
        background: '#fff',
        textAlign: 'right',
        zIndex: 1,
      }">
        <a-button :style="{ marginRight: '8px' }" @click="onClose"> {{ $t('cancel') }} </a-button>
        <a-button type="primary" @click="onSubmit"> {{ $t('submit') }} </a-button>
      </div>
    </a-drawer>
  </div>
</template>
<script>
let Cesium = require('cesium/Cesium')
require('cesium/Widgets/widgets.css')
import axios from 'axios'
import moment from 'moment'
import baseURL from '../../../modules/apiConfig'
export default {
  data() {
    return {
      viewer: null,
      labelCol: { span: 6 },
      wrapperCol: { span: 18 },
      form: {
        product: '',
        startX: -180,
        startY: -90,
        endX: 180,
        endY: 90,
        startTime: moment("2021-04-01 00:00:00"),
        endTime: moment("2021-04-30 00:00:00"),
        variable: [],
      },
      visible: false,
      productList: [],
      variableList: [],
      extentLevel: 0,
      UUID: '',
      slider: {
        product: {
          value: 0,
          min: 0,
          max: 0,
          mark: {
            0: '0',
          },
        },
        variable: {
          value: 0,
          min: 0,
          max: 0,
          mark: {
            0: '0',
          },
        },
        extent: {
          value: 0,
          min: 0,
          max: 0,
          mark: {
            0: '0',
          },
        },
        time: {
          value: 0,
          min: 0,
          max: 0,
          mark: {
            0: '0',
          },
        },
        productList: {},
        variableList: {},
        extentList: {},
        timeList: {},
        extentXMin: {},
        extentYMin: {},
        extentXMax: {},
        extentYMax: {},
        UUID: '',
        visibility: '',
      },
      percent: 0,
      status: '',
      checked: true,
    }
  },
  components: {
  },
  computed: {
    rules() {
      return {
        product: [{ required: true, message: this.$t('pleaseSelectProductDimension'), trigger: 'blur' }],
        variable: [{ required: true, message: this.$t('pleaseSelectVariableDimension'), trigger: 'blur' }],
      }
    },
    submitSuccess() {
      return this.$t('submitSuccess')
    },
    submitError() {
      return this.$t('submitError')
    },
  },
  mounted() {
    let self = this
    this.$bus.$on('viewer', function (val) {
      self.viewer = val
    })
  },
  methods: {
    showDrawer() {
      this.visible = true
      axios.get(baseURL.baseURL + 'getAllProduct').then(
        (response) => {
          console.log('提交请求成功！')
          this.productList = response.data
          return response.data
        },
        (response) => {
          console.log('提交请求失败！')
          return 'error'
        }
      )
      axios.get(baseURL.baseURL + 'getAllVariable').then(
        (response) => {
          console.log('提交请求成功！')
          this.variableList = response.data
          return response.data
        },
        (response) => {
          console.log('提交请求失败！')
          return 'error'
        }
      )

    },
    onClose() {
      this.visible = false
      this.$emit('update', 0)
    },
    onSubmit() {
      this.$refs.ruleForm.validate((valid) => {
        if (valid) {
          this.UUID = Date.now().toString()
          this.percent = 30
          axios.get(baseURL.baseURL + 'getAllMeteorologyTile?productKey=' + this.form.product +
            '&variableKey=' + this.form.variable + '&startX=' + this.form.startX + '&startY=' +
            this.form.startY + '&endX=' + this.form.endX + '&endY=' + this.form.endY + '&startTime='
            + moment(this.form.startTime).format('YYYY-MM-DD HH:mm:ss') + '&endTime=' +
            moment(this.form.endTime).format('YYYY-MM-DD HH:mm:ss') + '&UUID=' + this.UUID +
            '&extentLevelKey=' + this.extentLevel + '&cores=16&partitions=64').then(
              (response) => {

                this.percent = 70

                console.log('提交请求成功！')
                console.log(response.data)

                var productLength = response.data.productName.length
                this.slider.product.max = productLength - 1
                for (var i = 0; i < productLength; i++) {
                  this.slider.product.mark[i] = response.data.productName[i]
                }
                var variableLength = response.data.variableName.length
                this.slider.variable.max = variableLength - 1
                for (var i = 0; i < variableLength; i++) {
                  this.slider.variable.mark[i] = response.data.variableName[i]
                }
                var extentLength = response.data.extentKey.length
                this.slider.extent.max = extentLength - 1
                for (var i = 0; i < extentLength; i++) {
                  this.slider.extent.mark[i] = response.data.extentKey[i]
                }
                var timeLength = response.data.timeKey.length
                this.slider.time.max = timeLength - 1
                for (var i = 0; i < timeLength; i++) {
                  this.slider.time.mark[i] = response.data.timeKey[i]
                }
                this.slider.productList = response.data.productName
                this.slider.variableList = response.data.variableName
                this.slider.extentList = response.data.extentKey
                this.slider.extent.value = [this.slider.extent.min, this.slider.extent.max]
                this.slider.timeList = response.data.timeKey
                this.slider.extentXMin = response.data.extentXMin
                this.slider.extentYMin = response.data.extentYMin
                this.slider.extentXMax = response.data.extentXMax
                this.slider.extentYMax = response.data.extentYMax
                this.slider.UUID = this.UUID
                this.slider.visibility = 'visible'
                this.status = "success"
                this.percent = 100

                // this.visible = false
                // this.$emit('update', 0)
                this.$emit('slider', this.slider)
                alert(this.submitSuccess)

                return response.data
              },
              (response) => {
                console.log('提交请求失败！')
                this.status = "exception"
                this.percent = 50
                alert(this.submitError)
                return 'error'
              }
            )


        } else {
          this.status = "exception"
          this.progress = 10
          alert(this.submitError)
          return false
        }
      })
    },

    resetForm() {
      this.$refs.ruleForm.resetFields()
      console.log(this.checked)
    },

    onSelectProduct(key) {
      axios.get(baseURL.baseURL + 'getAllVariableByProductKey?productKey=' + key).then(
        (response) => {
          console.log('提交请求成功！')
          this.variableList = response.data
          return response.data
        },
        (response) => {
          console.log('提交请求失败！')
          return 'error'
        }
      )
    },

    onSelectExtentLevel(key) {
      this.extentLevel = key
      console.log("extentLevel = ", key)
    },

    pointEventSelect() {
      var point = viewer.entities.getById('point')
      viewer.entities.remove(point)
      //坐标存储
      let _this = this
      //坐标存储
      let n = 0
      var handler = new Cesium.ScreenSpaceEventHandler(_this.viewer.scene.canvas)
      //单击鼠标左键画点
      handler.setInputAction(function (movement) {
        var cartesian = _this.viewer.scene.camera.pickEllipsoid(movement.position, _this.viewer.scene.globe.ellipsoid)
        var cartographic = _this.viewer.scene.globe.ellipsoid.cartesianToCartographic(cartesian)
        console.log(Cesium.Math.toDegrees(cartographic.longitude))
        console.log(Cesium.Math.toDegrees(cartographic.latitude))
        _this.form.pointEventCoordinateX = Cesium.Math.toDegrees(cartographic.longitude)
        _this.form.pointEventCoordinateY = Cesium.Math.toDegrees(cartographic.latitude)
        var entity = new Cesium.Entity({
          id: 'point',
          position: Cesium.Cartesian3.fromDegrees(_this.form.pointEventCoordinateX, _this.form.pointEventCoordinateY),
          billboard: {
            // 图像地址，URI或Canvas的属性
            image: require('../../../static/image/locate.png'),
            // 设置颜色和透明度
            color: Cesium.Color.WHITE.withAlpha(0.8),
            // 高度（以像素为单位）
            height: 50,
            // 宽度（以像素为单位）
            width: 50,
            // 逆时针旋转
            // rotation: 20,
            // 大小是否以米为单位
            sizeInMeters: false,
            // 相对于坐标的垂直位置
            verticalOrigin: Cesium.VerticalOrigin.CENTER,
            // 相对于坐标的水平位置
            horizontalOrigin: Cesium.HorizontalOrigin.CENTER, //LEFT
            // 该属性指定标签在屏幕空间中距此标签原点的像素偏移量
            pixelOffset: new Cesium.Cartesian2(0, 0),
            // 应用于图像的统一比例。比例大于会1.0放大标签，而比例小于会1.0缩小标签。
            scale: 1.0,
            // 是否显示
            show: true,
          },
        })
        viewer.entities.add(entity)
        viewer.flyTo(entity, {
          offset: {
            heading: Cesium.Math.toRadians(0.0),
            pitch: Cesium.Math.toRadians(-90),
            range: 5000,
          },
        })
        n = n + 1
        if (n > 0) {
          handler.destroy()
        }
      }, Cesium.ScreenSpaceEventType.LEFT_CLICK)
    },

  },
}
</script>

<template>
  <div class="div_content" :style="'width:calc(100vw - ' + right + 'px);'">
    <div class="vis-options">
      <div class="option elevation-ramp-option">
        <a-switch :checked="showElevationRamp" @change="onShowElevationRampChange" />
        <span style="margin-left:10px">Render Elevation Ramp</span>
        <a-button style="margin-left:10px" v-if="showElevationRamp" @click="gradientPicker.show = !gradientPicker.show">
          Gradient Picker
        </a-button>
        <div class="gradient-picker" v-if="gradientPicker.show && showElevationRamp">
          <ColorPicker
            :isGradient="true"
            :gradient="gradientPicker.gradient"
            :onChange="onGradientChange"
          ></ColorPicker>
        </div>
        <div class="height-inputs" v-if="gradientPicker.show && showElevationRamp">
          <a-input
            :defaultValue="elevationRampParams.minHeight"
            @change="handleElevationRampParamChange('minHeight', $event)"
          ></a-input>
          <a-input
            :defaultValue="elevationRampParams.maxHeight"
            @change="handleElevationRampParamChange('maxHeight', $event)"
          ></a-input>
        </div>
      </div>
    </div>
    <div
      class="div_cesium"
      :id="random"
      :style="'height:' + screenHeight + 'px; position: relative'"
      ref="cesiumContainer"
    ></div>
    <div class="latlng_show">
      <div style="width: 100px; height: 30px; float: left; margin-top: -6px">
        <font size="3" color="white">{{ $t('level') }}:&nbsp;<span id="level_show"></span></font>
      </div>
      <div style="width: 200px; height: 30px; float: left; margin-top: -6px">
        <font size="3" color="white">{{ $t('resolution') }}:&nbsp;<span id="resolution_show"></span>m</font>
      </div>
      <div style="width: 180px; height: 30px; float: left; margin-top: -6px">
        <font size="3" color="white">{{ $t('longitude') }}:&nbsp;<span id="longitude_show"></span>°</font>
      </div>
      <div style="width: 165px; height: 30px; float: left; margin-top: -6px">
        <font size="3" color="white">{{ $t('latitude') }}:&nbsp;<span id="latitude_show"></span>°</font>
      </div>
      <div style="width: 220px; height: 30px; float: left; margin-top: -6px">
        <font size="3" color="white">{{ $t('altitude') }}:&nbsp;<span id="altitude_show"></span>km</font>
      </div>
    </div>
    <div class="div_buttonfather">
      <a-menu v-model="current" mode="horizontal">
        <a-sub-menu>
          <span slot="title" class="submenu-title-wrapper"> <a-icon type="snippets" />{{ $t('historyMode') }} </span>
          <!-- <a-menu-item key="setting:01" @click="meteorologyCube"> {{ $t('meteorologyCube') }} </a-menu-item> -->
          <a-menu-item key="setting:02" @click="singaporeCube"> {{ $t('singaporeCube') }} </a-menu-item>
          <!-- <a-menu-item key="setting:03" @click="waterwave"> {{ $t('waterwave') }} </a-menu-item> -->
          <a-menu-item key="setting:04" @click="windfield"> {{ $t('windfield') }} </a-menu-item>
          <a-menu-item key="setting:05" @click="flood"> {{ $t('flood') }} </a-menu-item>
          <!-- <a-menu-item key="setting:05" @click="seabed"> {{ $t('seabed') }} </a-menu-item> -->
          <!-- <a-menu-item key="setting:99" @click="loadTerrainAndBuildings">
            {{ $t('loadTerrainAndBuildings') }}
          </a-menu-item> -->
        </a-sub-menu>
      </a-menu>
    </div>
    <div class="block" :style="'visibility: ' + this.slider.visibility">
      <el-slider
        v-model="slider.product.value"
        show-input
        :min="slider.product.min"
        :max="slider.product.max"
        :show-stops="true"
        :marks="slider.product.mark"
        @change="productChange"
      >
      </el-slider>
      <el-slider
        v-model="slider.variable.value"
        show-input
        :min="slider.variable.min"
        :max="slider.variable.max"
        :show-stops="true"
        :marks="slider.variable.mark"
        @change="variableChange"
      >
      </el-slider>
      <el-slider
        v-model="slider.extent.value"
        range
        :min="slider.extent.min"
        :max="slider.extent.max"
        :show-stops="true"
        :marks="slider.extent.mark"
        @change="extentChange"
      >
      </el-slider>
      <el-slider
        v-model="slider.time.value"
        show-input
        :min="slider.time.min"
        :max="slider.time.max"
        :show-stops="true"
        :marks="slider.time.mark"
        @change="timeChange"
      >
      </el-slider>
    </div>

    <meteorology-cube
      ref="meteorologyChild"
      @update="rightMeteorologyChanged"
      @slider="sliderChanged"
    ></meteorology-cube>
    <singapore-cube ref="singaporeChild" @update="rightSingaporeChanged" @slider="sliderChanged"></singapore-cube>

    <wave-visualization-params
      ref="wavedatavChild"
      :datasetMetadata="waveDatasetMeta"
      @update="wavedatavUpdate"
      @exaggerationChange="handleWaveDrawerExaggerationChange"
    ></wave-visualization-params>

    <flood-params
      ref="floodChild"
      :waterLevel="waterLevel"
      @update="floodUpdate"
      @waterLevelChange="handleWaterLevelChange"
      @extentChange="data => (floodPrimExtent = data) && updateFloodPrimitive()"
      @rotationChange="data => (floodPrimRotation = data) && updateFloodPrimitive()"
      @animate=";(showFloodWaterReflection = !showFloodWaterReflection) && updateFloodPrimitive()"
    ></flood-params>
  </div>
</template>

<script>
//cesium 不支持import, 改用require
let Cesium = require('cesium/Cesium')
import { ColorPicker } from 'vue-color-gradient-picker'

require('cesium/Widgets/widgets.css')
import imageryViewModels from '../../modules/ProviderViewModels'
import { tianDTAnno, tianDTAnno_EN } from '../../modules/AnnotationLayerProviders'
import { AdjustHeightMixin } from '../../modules/AdjustHeightMixin'
import CesiumNavigation from './cesium-navigation/viewerCesiumNavigationMixin'
import ShrinkView from './ShrinkView.vue'
import {} from '../../modules/ImageryProvider'
import MeteorologyCube from './CubeQueryBoard/MeteorologyCube.vue'
import SingaporeCube from './CubeQueryBoard/SingaporeCube.vue'
import { CanvasWindy } from '../../utils/wind'
import { computeTerrainGeometry } from '../../utils/computeTerrainGeometry'
import { TemporalWaveHeightDataset } from '../../utils/TemporalWaveHeightDataset'
import WaveVisualizationParams from '../FloodParams.vue'
import { SeabedHeightDataset } from '../../utils/SeabedHeightDataset'
const lodash = require('lodash')
const { throttle } = lodash
import { parseGradientMatrix } from '../../utils/gradientParser'
import { getColorRamp } from '../../utils/getColorRamp'
import FloodParams from '../FloodParams.vue'
import baseURL from '../../modules/apiConfig'

export default {
  name: 'CesiumViewer',
  data() {
    return {
      // elevation ramp renderer
      showElevationRamp: false,

      // elevation ramp gradient picker
      gradientPicker: {
        show: false,
        gradient: {
          type: 'linear',
          degree: 0,
          points: parseGradientMatrix([
            [0, 8, 48, 107, 1],
            [11, 14, 89, 162, 1],
            [23, 43, 123, 186, 1],
            [35, 79, 155, 203, 1],
            [46, 123, 183, 217, 1],
            [58, 170, 207, 229, 1],
            [70, 205, 224, 241, 1],
            [81, 228, 239, 249, 1],
            [90, 254, 219, 150, 1],
            [91, 253, 177, 101, 1],
            [93, 238, 117, 70, 1],
            [95, 227, 71, 49, 1],
            [100, 215, 25, 28, 1]
          ])
        }
      },
      elevationRampParams: {
        minHeight: -19.5349,
        maxHeight: 3.0
      },

      // wavedata
      waveExaggLevel: 1.0,
      waveDatasetMeta: '',
      wavePrimitive: null,

      // flood
      waterLevel: 7.8,
      floodPrim: null,
      showFloodDrawer: false,
      floodPrimExtent: [103.825, 1.3, 104.02, 1.305],
      floodPrimRotation: 17.0,
      showFloodWaterReflection: true,
      waterReflectionMaterial: null,
      waterMaterial: null,

      //seabed
      seabedExaggLevel: 10.0,
      seabedPrimitive: null,

      tianDT: 'zh',
      current: ['home'],
      viewer: null,
      random: Math.random()
        .toString(36)
        .substring(2),

      right: 0,
      drawerNum: 0,
      slider: {
        product: {
          value: 0,
          min: 0,
          max: 0,
          mark: {}
        },
        variable: {
          value: 0,
          min: 0,
          max: 0,
          mark: {}
        },
        extent: {
          value: 0,
          min: 0,
          max: 0,
          mark: {}
        },
        time: {
          value: 0,
          min: 0,
          max: 0,
          mark: {}
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
        visibility: 'hidden'
      },
      layers: [],

      windFieldController: null,
      windFieldCanvas: null,
      windFieldScreenSpaceHandler: null
    }
  },
  components: {
    ShrinkView,
    MeteorologyCube,
    SingaporeCube,
    WaveVisualizationParams,
    ColorPicker,
    FloodParams
  },
  computed: {
    Anno: function() {
      return this.$t('Anno_layer')
    }
  },
  watch: {
    Anno: function(n) {
      var m = 0
      if (n == 'eia_w') {
        for (var i = this.viewer.imageryLayers.length - 1; i >= 0; i--) {
          if (
            this.viewer.imageryLayers._layers[i].imageryProvider.url ==
            'http://{s}.tianditu.gov.cn/cia_w/wmts?service=wmts&request=GetTile&version=1.0.0&LAYER=cia&tileMatrixSet=w&TileMatrix={TileMatrix}&TileRow={TileRow}&TileCol={TileCol}&style=default.jpg&tk=2d585d36d89ab86049e29f6f10364dc3'
          ) {
            this.viewer.imageryLayers.remove(this.viewer.imageryLayers._layers[i])
            m = m + 1
          } else if (
            this.viewer.imageryLayers._layers[i].imageryProvider.url ==
            'http://{s}.tianditu.gov.cn/eia_w/wmts?service=wmts&request=GetTile&version=1.0.0&LAYER=eia&tileMatrixSet=w&TileMatrix={TileMatrix}&TileRow={TileRow}&TileCol={TileCol}&style=default.jpg&tk=2d585d36d89ab86049e29f6f10364dc3'
          ) {
            m = m + 1
            this.viewer.imageryLayers.remove(this.viewer.imageryLayers._layers[i])
          }
        }
        if (m == 1) {
          this.viewer.imageryLayers.addImageryProvider(tianDTAnno_EN, 2)
        }
      } else {
        for (var i = this.viewer.imageryLayers.length - 1; i >= 0; i--) {
          if (
            this.viewer.imageryLayers._layers[i].imageryProvider.url ==
            'http://{s}.tianditu.gov.cn/cia_w/wmts?service=wmts&request=GetTile&version=1.0.0&LAYER=cia&tileMatrixSet=w&TileMatrix={TileMatrix}&TileRow={TileRow}&TileCol={TileCol}&style=default.jpg&tk=2d585d36d89ab86049e29f6f10364dc3'
          ) {
            this.viewer.imageryLayers.remove(this.viewer.imageryLayers._layers[i])
            m = m + 1
          } else if (
            this.viewer.imageryLayers._layers[i].imageryProvider.url ==
            'http://{s}.tianditu.gov.cn/eia_w/wmts?service=wmts&request=GetTile&version=1.0.0&LAYER=eia&tileMatrixSet=w&TileMatrix={TileMatrix}&TileRow={TileRow}&TileCol={TileCol}&style=default.jpg&tk=2d585d36d89ab86049e29f6f10364dc3'
          ) {
            this.viewer.imageryLayers.remove(this.viewer.imageryLayers._layers[i])
            m = m + 1
          }
        }
        if (m == 1) {
          this.viewer.imageryLayers.addImageryProvider(tianDTAnno, 2)
        }
      }
    }
  },
  mounted() {
    this.$bus.$on('tianDTchange', this.changetianDT)
    this.initCesiumViewer()
  },
  methods: {
    loadTerrainAndBuildings() {
      var tileset = this.viewer.scene.primitives.add(
        new Cesium.Cesium3DTileset({
          url: '/geostreamcube/singapore_building/tileset.json'
        })
      )

      tileset.readyPromise
        .then(function() {
          var boundingSphere = tileset.boundingSphere
          viewer.camera.viewBoundingSphere(
            boundingSphere,
            new Cesium.HeadingPitchRange(0.0, -0.5, boundingSphere.radius)
          )
          viewer.camera.lookAtTransform(Cesium.Matrix4.IDENTITY)
        })
        .otherwise(function(error) {
          throw error
        })

      var terrainProvider = new Cesium.CesiumTerrainProvider({
        url: '/geostreamcube/singapore_dtm2', // 替换为地形数据源的URL
        // 请求照明
        requestVertexNormals: true,
        // 请求水波纹效果
        requestWaterMask: true
      })
      this.viewer.terrainProvider = terrainProvider
    },

    onGradientChange(gradient) {
      this.gradientPicker.gradient = gradient

      this.updateElevationRampMaterial()
    },

    updateElevationRampMaterial() {
      const elevationMaterial = Cesium.Material.fromType('ElevationRamp')
      const shadingUniforms = elevationMaterial.uniforms
      shadingUniforms.minimumHeight = this.elevationRampParams.minHeight
      shadingUniforms.maximumHeight = this.elevationRampParams.maxHeight
      shadingUniforms.image = getColorRamp(this.gradientPicker.gradient.points)
      this.viewer.scene.globe.material = elevationMaterial
    },

    onShowElevationRampChange() {
      this.showElevationRamp = !this.showElevationRamp

      if (this.showElevationRamp) {
        this.updateElevationRampMaterial()
      } else {
        this.viewer.scene.globe.material = undefined
      }
    },

    handleElevationRampParamChange(which, value) {
      this.elevationRampParams[which] = value
    },

    productChange(value) {
      this.removeAll()
      for (var i = this.slider.extent.value[0]; i <= this.slider.extent.value[1]; i++) {
        var imageURL =
          baseURL.tomcatURL +
          this.slider.UUID +
          '/' +
          this.slider.productList[this.slider.product.value] +
          '_' +
          this.slider.variableList[this.slider.variable.value] +
          '_' +
          this.slider.extentList[i] +
          '_' +
          this.slider.timeList[this.slider.time.value] +
          '.png'
        this.loadImage(
          imageURL,
          this.slider.extentXMin[i],
          this.slider.extentYMin[i],
          this.slider.extentXMax[i],
          this.slider.extentYMax[i]
        )
      }
    },
    variableChange(value) {
      console.log(value)
      this.removeAllImage()
      for (var i = this.slider.extent.value[0]; i <= this.slider.extent.value[1]; i++) {
        var imageURL =
          baseURL.tomcatURL +
          this.slider.UUID +
          '/' +
          this.slider.productList[this.slider.product.value] +
          '_' +
          this.slider.variableList[this.slider.variable.value] +
          '_' +
          this.slider.extentList[i] +
          '_' +
          this.slider.timeList[this.slider.time.value] +
          '.png'
        this.loadImage(
          imageURL,
          this.slider.extentXMin[i],
          this.slider.extentYMin[i],
          this.slider.extentXMax[i],
          this.slider.extentYMax[i]
        )
      }
    },
    extentChange(value) {
      console.log(value)
      this.removeAllImage()
      for (var i = this.slider.extent.value[0]; i <= this.slider.extent.value[1]; i++) {
        var imageURL =
          baseURL.tomcatURL +
          this.slider.UUID +
          '/' +
          this.slider.productList[this.slider.product.value] +
          '_' +
          this.slider.variableList[this.slider.variable.value] +
          '_' +
          this.slider.extentList[i] +
          '_' +
          this.slider.timeList[this.slider.time.value] +
          '.png'
        this.loadImage(
          imageURL,
          this.slider.extentXMin[i],
          this.slider.extentYMin[i],
          this.slider.extentXMax[i],
          this.slider.extentYMax[i]
        )
      }
    },
    timeChange(value) {
      console.log(value)
      this.removeAllImage()
      for (var i = this.slider.extent.value[0]; i <= this.slider.extent.value[1]; i++) {
        var imageURL =
          baseURL.tomcatURL +
          this.slider.UUID +
          '/' +
          this.slider.productList[this.slider.product.value] +
          '_' +
          this.slider.variableList[this.slider.variable.value] +
          '_' +
          this.slider.extentList[i] +
          '_' +
          this.slider.timeList[this.slider.time.value] +
          '.png'
        this.loadImage(
          imageURL,
          this.slider.extentXMin[i],
          this.slider.extentYMin[i],
          this.slider.extentXMax[i],
          this.slider.extentYMax[i]
        )
      }
    },

    singaporeCube() {
      this.removeAll()
      if (this.drawerNum == 0) {
        this.$refs.singaporeChild.showDrawer()
        this.right = 600
      }
      this.drawerNum = 1
    },
    rightSingaporeChanged(right) {
      this.right = right
      this.drawerNum = 0
    },
    meteorologyCube() {
      if (this.drawerNum == 0) {
        this.$refs.meteorologyChild.showDrawer()
        this.right = 600
      }
      this.drawerNum = 1
    },
    rightMeteorologyChanged(right) {
      this.right = right
      this.drawerNum = 0
    },
    sliderChanged(slider) {
      this.slider = slider
      console.log('slider = ', slider)
      if (this.slider.visibility === 'visible') {
        for (var i = this.slider.extent.value[0]; i <= this.slider.extent.value[1]; i++) {
          var imageURL =
            baseURL.tomcatURL +
            this.slider.UUID +
            '/' +
            this.slider.productList[this.slider.product.value] +
            '_' +
            this.slider.variableList[this.slider.variable.value] +
            '_' +
            this.slider.extentList[i] +
            '_' +
            this.slider.timeList[this.slider.time.value] +
            '.png'
          this.loadImage(
            imageURL,
            this.slider.extentXMin[i],
            this.slider.extentYMin[i],
            this.slider.extentXMax[i],
            this.slider.extentYMax[i]
          )
        }
      } else {
        this.viewer.animation.container.style.visibility = 'visible'
        this.viewer.timeline.container.style.visibility = 'visible'
      }
    },
    async seabed() {
      const self = this
      this.removeAll()

      var extentRect = Cesium.Rectangle.fromDegrees(
        103.9143407281605249,
        1.2890762277329646,
        103.9391766318171761,
        1.3055914275511051
      )
      const viewer = this.viewer
      viewer.scene.screenSpaceCameraController.enableCollisionDetection = false

      const seabedDataset = await SeabedHeightDataset.fromUrl('/seabed/singapore-sea-modify.tif', {
        west: 103.9143407281605249,
        south: 1.2890762277329646,
        east: 103.9391766318171761,
        north: 1.3055914275511051
      })

      function updateSeabedPrimitive() {
        const seabedGeom = computeTerrainGeometry(extentRect, seabedDataset.getSampler({ method: 'bilinear' }), {
          latSamples: 120,
          lngSamples: 240,
          NO_DATA_VALUE: seabedDataset.getNoData(),
          replaceNoData: false,
          exaggerationMultiplier: 1.0,
          colorSteps: [
            { height: -1e5, color: [0, 0, 0, 0.0] }, // for no data
            { height: -19.5349, color: [8, 48, 107, 1.0] },
            { height: -16.8931, color: [14, 89, 162, 1.0] },
            { height: -14.2513, color: [43, 123, 186, 1.0] },
            { height: -11.6095, color: [79, 155, 203, 1.0] },
            { height: -8.9677, color: [123, 183, 217, 1.0] },
            { height: -6.3259, color: [170, 207, 229, 1.0] },
            { height: -3.6841, color: [205, 224, 241, 1.0] },
            { height: -1.2455, color: [228, 239, 249, 1.0] },
            { height: 0.7865, color: [247, 251, 255, 1.0] }
          ]
        })

        const seabedGeomIns = new Cesium.GeometryInstance({
          geometry: seabedGeom
        })

        const seabedPrim = new Cesium.Primitive({
          geometryInstances: seabedGeomIns,
          asynchronous: false,
          cull: true,
          appearance: seabedDataset.getElevationBandMaterialAppearance()
          // appearance: new Cesium.EllipsoidSurfaceAppearance({
          //     material: Cesium.Material.fromType("Color", {
          //         color: Cesium.Color.fromRandom(),
          //     }),
          // }),
        })

        const prims = viewer.scene.primitives
        prims.add(seabedPrim)
        self.seabedPrimitive == seabedPrim
      }

      updateSeabedPrimitive()
      viewer.camera.flyTo({
        destination: extentRect
      })

      // viewer.scene.globe.depthTestAgainstTerrain = true
    },
    loadImage(imageURL, lblong, lblat, rtlong, rtlat) {
      var layerAdded = this.viewer.imageryLayers.addImageryProvider(
        new Cesium.SingleTileImageryProvider({
          url: imageURL,
          rectangle: Cesium.Rectangle.fromDegrees(lblong, lblat, rtlong, rtlat)
        })
      )
      let imgObj = {
        url: imageURL,
        type: 'addImageryProvider',
        value: layerAdded
      }
      this.layers.push(imgObj)
    },

    loadImageEntity(imageURL, lblong, lblat, rtlong, rtlat, timeString) {
      var property = new Cesium.SampledPositionProperty()

      var time = Cesium.JulianDate.fromDate(
        new Date(
          timeString.substring(0, 4),
          timeString.substring(4, 6),
          timeString.substring(6, 8),
          timeString.substring(8, 10),
          timeString.substring(10, 12),
          timeString.substring(12, 14)
        )
      )
      var positions = Cesium.Cartesian3.fromDegrees(lblong, lblat, 0)
      // 添加位置，和时间对应
      property.addSample(time, positions)

      this.viewer.entities.add({
        id: imageURL + '_polygon',
        position: property,
        polygon: {
          hierarchy: Cesium.Cartesian3.fromDegreesArray([
            lblong,
            lblat,
            rtlong,
            lblat,
            rtlong,
            rtlat,
            lblong,
            rtlat,
            lblong,
            lblat
          ]),
          material: imageURL,
          height: 0
        }
      })
    },
    handleWaveDrawerExaggerationChange(data) {
      this.waveExaggLevel = data
    },
    wavedatavUpdate(right) {
      this.right = right
      this.drawerNum = 0
    },
    async waterwave() {
      const self = this
      this.removeAll()

      if (this.drawerNum == 0) {
        this.$refs.wavedatavChild.showDrawer()
        this.right = 600
      }
      this.drawerNum = 1

      const extentRect = Cesium.Rectangle.fromDegrees(103.69, 1.13, 104.012, 1.45)

      const viewer = this.viewer

      const waveDataset = await TemporalWaveHeightDataset.fromUrl('/wavedata')

      this.waveDatasetMeta = waveDataset.getDescription()

      const { start: timeStart, end: timeEnd } = waveDataset.getTimeSpan()

      let lastPrim = { current: null }

      function updateWavePrimitive(timestamp) {
        if (timestamp < timeStart || timestamp > timeEnd) return

        console.log('updating wave prim:', timestamp, new Date(timestamp))

        const waveGeom = computeTerrainGeometry(extentRect, waveDataset.getSampler(timestamp, { method: 'bilinear' }), {
          latSamples: 180,
          lngSamples: 360,
          NO_DATA_VALUE: waveDataset.getNoData(),
          replaceNoData: 0.0,
          exaggerationMultiplier: self.waveExaggLevel
        })

        const waveGeomIns = new Cesium.GeometryInstance({
          geometry: waveGeom
        })

        const wavePrim = new Cesium.Primitive({
          geometryInstances: waveGeomIns,
          asynchronous: false,
          // appearance: new Cesium.DebugAppearance({
          //     attributeName: "normal",
          // }),
          appearance: new Cesium.EllipsoidSurfaceAppearance({
            material: new Cesium.Material({
              fabric: {
                type: 'Water',
                uniforms: {
                  baseWaterColor: new Cesium.Color(64 / 255.0, 157 / 255.0, 253 / 255.0, 0.8),
                  normalMap: Cesium.buildModuleUrl('Assets/Textures/waterNormals.jpg'),
                  frequency: 500,
                  animationSpeed: 0.02,
                  amplitude: 5,
                  specularIntensity: 2
                }
              }
            })
          })

          // appearance: new Cesium.PerInstanceColorAppearance({
          //     flat: true,
          // }),
        })

        const prims = viewer.scene.primitives
        if (lastPrim.current !== null) {
          prims.remove(lastPrim.current)
        }
        prims.add(wavePrim)
        self.wavePrimitive = lastPrim.current = wavePrim
      }

      // const frame = () =>
      //     requestAnimationFrame(() => {
      //         updateWaveAttributes();
      //         frame();
      //     });
      // frame();

      const throttledUpdate = throttle(updateWavePrimitive, 200)

      let lastUpdateParams = {
        timestamp: 0,
        waveExaggLevel: this.waveExaggLevel
      }

      this.waveUpdateListener = viewer.clock.onTick.addEventListener(clock => {
        const currentTime = Cesium.JulianDate.toDate(clock.currentTime).getTime()
        const currentParams = {
          timestamp: currentTime,
          waveExaggLevel: self.waveExaggLevel
        }

        if (!lodash.isEqual(currentParams, lastUpdateParams)) {
          throttledUpdate(currentTime)
          lastUpdateParams = currentParams
        }
      })

      viewer.camera.flyTo({
        destination: extentRect
      })
    },
    floodUpdate(right) {
      this.right = right
      this.drawerNum = 0
    },
    updateFloodPrimitive() {
      const prims = this.viewer.scene.primitives

      const oldPrim = this.floodPrim

      // console.log(
      //   'current params:',
      //   'waterLevel',
      //   this.waterLevel,
      //   'extent',
      //   this.floodPrimExtent,
      //   'rotation',
      //   this.floodPrimRotation
      // )

      const geom = new Cesium.RectangleGeometry({
        rectangle: Cesium.Rectangle.fromDegrees(...this.floodPrimExtent),
        height: this.waterLevel,
        rotation: (this.floodPrimRotation / 180.0) * 3.1415926535
        // vertexFormat: Cesium.EllipsoidSurfaceAppearance.VERTEX_FORMAT
      })

      this.floodPrim = new Cesium.Primitive({
        geometryInstances: new Cesium.GeometryInstance({
          geometry: geom, //Cesium.GeometryPipeline.toWireframe(Cesium.RectangleGeometry.createGeometry(geom)),
          attributes: {
            color: Cesium.ColorGeometryInstanceAttribute.fromColor(Cesium.Color.WHITE)
          }
        }),
        appearance: new Cesium.EllipsoidSurfaceAppearance({
          material: this.showFloodWaterReflection ? this.waterReflectionMaterial : this.waterMaterial
        })
      })

      prims.add(this.floodPrim)

      if (oldPrim !== null) {
        setTimeout(() => {
          prims.remove(oldPrim)
        }, 100)
      }
    },
    flood() {
      this.removeAll()

      if (this.drawerNum == 0) {
        this.right = 600
        this.$refs.floodChild.showDrawer()
      }

      this.viewer.camera.flyTo({
        destination: Cesium.Cartesian3.fromDegrees(103.92, 1.295, 1000),
        orientation: {
          pitch: (-45 / 180.0) * 3.1415926535
        }
      })
      // this.viewer.camera.lookAt(Cesium.Cartesian3.fromDegrees(103.92, 1.3083, 0))

      this.waterMaterial = new Cesium.Material.fromType('Color', {
        color: new Cesium.Color(84 / 255.0, 142 / 255.0, 220 / 255.0, 1.0)
      })

      this.waterReflectionMaterial = new Cesium.Material({
        fabric: {
          type: 'Water',
          uniforms: {
            baseWaterColor: new Cesium.Color(64 / 255.0, 157 / 255.0, 253 / 255.0, 0.8),
            normalMap: Cesium.buildModuleUrl('Assets/Textures/waterNormals.jpg'),
            frequency: 500,
            animationSpeed: 0.02,
            amplitude: 5,
            specularIntensity: 1
          }
        }
      })

      this.drawerNum = 1
      this.updateFloodPrimitive()
      this.viewer.scene.globe.depthTestAgainstTerrain = true
    },

    handleWaterLevelChange(newLevel) {
      this.waterLevel = newLevel
      this.updateFloodPrimitive()
    },

    windfield() {
      this.removeAll()

      var extentRect = Cesium.Rectangle.fromDegrees(103.6184, 1.25, 103.98265, 1.444)

      const viewer = this.viewer
      const cesiumContainer = this.$refs.cesiumContainer

      viewer.scene.debugShowFramesPerSecond = true
      viewer.scene.globe.depthTestAgainstTerrain = true
      //修改鼠标操作方式（默认鼠标中建旋转，右键zoom，以下方式为：中建zoom，右键旋转）
      viewer.scene.screenSpaceCameraController.tiltEventTypes = [
        Cesium.CameraEventType.RIGHT_DRAG,
        Cesium.CameraEventType.PINCH,
        { eventType: Cesium.CameraEventType.LEFT_DRAG, modifier: Cesium.KeyboardEventModifier.CTRL },
        { eventType: Cesium.CameraEventType.RIGHT_DRAG, modifier: Cesium.KeyboardEventModifier.CTRL }
      ]
      viewer.scene.screenSpaceCameraController.zoomEventTypes = [
        Cesium.CameraEventType.MIDDLE_DRAG,
        Cesium.CameraEventType.WHEEL,
        Cesium.CameraEventType.PINCH
      ]

      /**
       *如果处于全球状态就设置为[]（只要有一个角获取不到坐标就表示全球状态，实时计算）
       **/
      var globalExtent = []
      var showWindy = function() {
        $('#windycanvas').show()
      }
      var hideWindy = function() {
        $('#windycanvas').hide()
      }
      //获取当前三维窗口左上、右上、左下、右下坐标
      var getCesiumExtent = function() {
        var left_top_pt = new Cesium.Cartesian2(cesiumContainer.offsetLeft, cesiumContainer.offsetTop)
        var left_bottom_pt = new Cesium.Cartesian2(
          cesiumContainer.offsetLeft,
          cesiumContainer.offsetLeft + cesiumContainer.offsetHeight
        )
        var right_top_pt = new Cesium.Cartesian2(
          cesiumContainer.offsetLeft + cesiumContainer.offsetWidth,
          cesiumContainer.offsetTop
        )
        var right_bottom_pt = new Cesium.Cartesian2(
          cesiumContainer.offsetLeft + cesiumContainer.offsetWidth,
          cesiumContainer.offsetLeft + cesiumContainer.offsetHeight
        )

        var pick1 = viewer.scene.globe.pick(viewer.camera.getPickRay(left_top_pt), viewer.scene)
        var pick2 = viewer.scene.globe.pick(viewer.camera.getPickRay(left_bottom_pt), viewer.scene)
        var pick3 = viewer.scene.globe.pick(viewer.camera.getPickRay(right_top_pt), viewer.scene)
        var pick4 = viewer.scene.globe.pick(viewer.camera.getPickRay(right_bottom_pt), viewer.scene)
        if (pick1 && pick2 && pick3 && pick4) {
          //将三维坐标转成地理坐标---只需计算左下右上的坐标即可
          var geoPt1 = viewer.scene.globe.ellipsoid.cartesianToCartographic(pick2)
          var geoPt2 = viewer.scene.globe.ellipsoid.cartesianToCartographic(pick3)
          //地理坐标转换为经纬度坐标
          var point1 = [(geoPt1.longitude / Math.PI) * 180, (geoPt1.latitude / Math.PI) * 180]
          var point2 = [(geoPt2.longitude / Math.PI) * 180, (geoPt2.latitude / Math.PI) * 180]
          // console.log(point1,point2);
          //此时说明extent需要分为东西半球
          if (point1[0] > point2[0]) {
            globalExtent = [point1[0], 180, point1[1], point2[1], -180, point2[0], point1[1], point2[1]]
          } else {
            globalExtent = [point1[0], point2[0], point1[1], point2[1]]
          }
        } else {
          globalExtent = []
        }
      }
      // 开启监听器--无论对当前地球做的任何操作都会监听到
      let postRender = viewer.scene.postRender.addEventListener(() => {
        getCesiumExtent()
      })
      var refreshTimer = -1
      var mouse_down = false
      var mouse_move = false

      var handler = (this.windFieldScreenSpaceHandler = new Cesium.ScreenSpaceEventHandler(viewer.scene.canvas))
      //鼠标滚动、旋转后是否需要重新生成风场---如果需要，打开以下注释--旋转或者移动到北半球的时候计算会有问题
      handler.setInputAction(function(e) {
        clearTimeout(refreshTimer)
        hideWindy()
        setTimeout(function() {
          windFieldController.extent = globalExtent
          windFieldController.redraw()
          showWindy()
        }, 200)
      }, Cesium.ScreenSpaceEventType.WHEEL)
      //鼠标左键、右键按下
      handler.setInputAction(function(e) {
        mouse_down = true
      }, Cesium.ScreenSpaceEventType.LEFT_DOWN)
      handler.setInputAction(function(e) {
        mouse_down = true
      }, Cesium.ScreenSpaceEventType.RIGHT_DOWN)
      //鼠标移动
      handler.setInputAction(function(e) {
        if (mouse_down) {
          hideWindy()
          mouse_move = true
        }
      }, Cesium.ScreenSpaceEventType.MOUSE_MOVE)
      //鼠标左键、右键抬起
      handler.setInputAction(function(e) {
        if (mouse_down && mouse_move) {
          windFieldController.extent = globalExtent
          windFieldController.redraw()
        }
        showWindy()
        mouse_down = false
        mouse_move = false
      }, Cesium.ScreenSpaceEventType.LEFT_UP)
      handler.setInputAction(function(e) {
        if (mouse_down && mouse_move) {
          windFieldController.extent = globalExtent
          windFieldController.redraw()
        }
        showWindy()
        mouse_down = false
        mouse_move = false
      }, Cesium.ScreenSpaceEventType.RIGHT_UP)

      if (this.windFieldController) this.windFieldController.removeLines()
      var windFieldController = (this.windFieldController = null)
      var windFieldCanvas = (this.windFieldCanvas = null)

      var resizeCanvas = function() {
        if (windFieldCanvas == null) {
          return
        }
        windFieldCanvas.width = cesiumContainer.offsetWidth
        windFieldCanvas.height = cesiumContainer.offsetHeight
        console.log(windFieldCanvas.width, windFieldCanvas.height)
        if (windFieldController) {
          windFieldController._resize(windFieldCanvas.width, windFieldCanvas.height)
        }
      }
      this.windFieldCanvas = windFieldCanvas = document.createElement('canvas')
      windFieldCanvas.setAttribute('id', 'windycanvas')
      windFieldCanvas.style['pointer-events'] = 'none'
      windFieldCanvas.style['z-index'] = 10

      windFieldCanvas.style['position'] = 'absolute'
      windFieldCanvas.style['top'] = 0
      windFieldCanvas.style['left'] = 0
      cesiumContainer.appendChild(windFieldCanvas)

      const self = this
      $.ajax({
        type: 'get',
        url: './data_dhp.json',
        dataType: 'json',
        success: function(response) {
          resizeCanvas()
          window.onresize = resizeCanvas
          //风场的参数配置，除了canvas/viewer是必传项，其他可以不传，参数含义见windy.js
          var params = {
            viewer: viewer,
            canvas: windFieldCanvas,
            canvasWidth: cesiumContainer.offsetWidth,
            canvasHeight: cesiumContainer.offsetHeight,
            speedRate: 5000,
            particlesNumber: 2500,
            extent: [103.6184, 103.98265, 1.25, 1.444],
            // initExtent: [103.6184, 103.98265, 1.25, 1.444],
            maxAge: 120,
            color: '#69e2ff',
            lineWidth: 1
          }
          self.windFieldController = windFieldController = new CanvasWindy(response, params)
        },
        error: function(errorMsg) {
          console.log('请求风场数据失败!')
        }
      })

      viewer.camera.flyTo({
        destination: extentRect
      })
    },

    removeScreenSpaceEventHandlers() {
      if (this.windFieldScreenSpaceHandler) {
        this.windFieldScreenSpaceHandler.removeInputAction(Cesium.ScreenSpaceEventType.WHEEL)
        this.windFieldScreenSpaceHandler.removeInputAction(Cesium.ScreenSpaceEventType.LEFT_DOWN)
        this.windFieldScreenSpaceHandler.removeInputAction(Cesium.ScreenSpaceEventType.RIGHT_DOWN)
        this.windFieldScreenSpaceHandler.removeInputAction(Cesium.ScreenSpaceEventType.MOUSE_MOVE)
        this.windFieldScreenSpaceHandler.removeInputAction(Cesium.ScreenSpaceEventType.LEFT_UP)
        this.windFieldScreenSpaceHandler.removeInputAction(Cesium.ScreenSpaceEventType.RIGHT_UP)
        this.windFieldScreenSpaceHandler = null
      }
    },

    removeAll() {
      this.drawerNum = 0
      this.right = 0
      this.slider.visibility = 'hidden'

      this.removeScreenSpaceEventHandlers()
      this.removeAllImage()

      if (this.windFieldController) {
        this.windFieldController.destroy()
        this.windFieldController = null
      }
      if (this.windFieldCanvas) {
        this.$refs.cesiumContainer.removeChild(this.windFieldCanvas)
        this.windFieldCanvas = null
      }
      if (this.waveUpdateListener) {
        this.viewer.clock.onTick.removeEventListener(this.waveUpdateListener)
      }
      if (this.wavePrimitive) {
        this.viewer.scene.primitives.remove(this.wavePrimitive)
      }
      viewer.scene.screenSpaceCameraController.enableCollisionDetection = true
      if (this.seabedPrimitive) {
        this.viewer.scene.primitives.remove(this.seabedPrimitive)
      }
      if (this.floodPrim) {
        this.viewer.scene.primitives.remove(this.floodPrim)
      }

      this.viewer.scene.globe.depthTestAgainstTerrain = false
    },

    removeAllImage() {
      for (let obj of this.layers) {
        if (obj.type == 'addImageryProvider') {
          let layer = obj.value
          this.viewer.imageryLayers.remove(layer)
        } else if (obj.type == 'dataSources') {
          let layer = obj.value
          this.viewer.dataSources.remove(layer)
        }
      }
      this.layers = []
    },

    changetianDT(value) {
      this.tianDT = value
    },
    //返回当前viewer
    getViewer() {
      return this.viewer
    },
    initCesiumViewer() {
      const self = this
      this.$nextTick(() => {
        //设置静态资源目录
        // Cesium.buildModuleUrl.setBaseUrl('/static/Cesium-1.71/')
        //Initialize the viewer widget with several custom options and mixins.
        Cesium.Ion.defaultAccessToken =
          'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiJmMWM1ZjExZi00Yzg5LTRlOTMtODViYS0yOTZmZmI0OGU2NjQiLCJpZCI6MTg5MDIsInNjb3BlcyI6WyJhc3IiLCJnYyJdLCJpYXQiOjE1NzQ2NjU2ODJ9.bPY0jro1abLzWRoT8Mj4CtH7e0B_dogToc2f5JDm-w0'

        this.viewer = new Cesium.Viewer(this.random, {
          baseLayerPicker: true,
          animation: true,
          infoBox: true,
          timeline: true,
          imageryProvider: false,
          scene3DOnly: true,
          // terrainProvider: Cesium.createWorldTerrain(),
          // Show Columbus View map with Web Mercator projection
          mapProjection: new Cesium.WebMercatorProjection()
        })
        this.viewer.animation.container.style.visibility = 'hidden'
        // this.viewer.timeline.container.style.visibility = 'hidden'
        this.viewer.extend(CesiumNavigation)

        // const buildingsPrims = Cesium.createOsmBuildings()
        // this.viewer.scene.primitives.add(buildingsPrims)
        this.viewer.clock.multiplier = 100

        //Add basic drag and drop functionality
        this.viewer.extend(Cesium.viewerDragDropMixin)
        // this.viewer.extend(Cesium.viewerCesiumNavigationMixin, {})
        //取消左下侧cesium ion logo
        this.viewer.cesiumWidget.creditContainer.style.display = 'none'

        //设置image和地形图层
        this.viewer.baseLayerPicker.viewModel.imageryProviderViewModels = imageryViewModels
        this.viewer.baseLayerPicker.viewModel.selectedImagery = this.viewer.baseLayerPicker.viewModel.imageryProviderViewModels[2]
        // 是否叠加注记功能
        if (this.$t('Anno_layer') == 'cia_w') {
          this.viewer.imageryLayers.addImageryProvider(tianDTAnno)
        } else {
          this.viewer.imageryLayers.addImageryProvider(tianDTAnno_EN)
        }

        //设置初始视角
        this.viewer.camera.setView({
          // fromDegrees()方法，将经纬度和高程转换为世界坐标
          //destination: Cesium.Rectangle.fromDegrees(113.0149404672,30.0734572226,113.9181165740,30.9597805439),//west, south, east, north
          destination: Cesium.Cartesian3.fromDegrees(110, 32, 15000000),
          orientation: {
            // 指向
            heading: Cesium.Math.toRadians(0, 0),
            // 视角
            pitch: Cesium.Math.toRadians(-90),
            roll: 0.0
          }
        })

        window.viewer = this.viewer

        self.loadTerrainAndBuildings()

        var longitude_show = document.getElementById('longitude_show')
        var latitude_show = document.getElementById('latitude_show')
        var altitude_show = document.getElementById('altitude_show')
        var level_show = document.getElementById('level_show')
        var resolution_show = document.getElementById('resolution_show')
        var canvas = viewer.scene.canvas
        //具体事件的实现
        var ellipsoid = viewer.scene.globe.ellipsoid
        var handler = new Cesium.ScreenSpaceEventHandler(canvas)
        handler.setInputAction(function(movement) {
          //捕获椭球体，将笛卡尔二维平面坐标转为椭球体的笛卡尔三维坐标，返回球体表面的点
          var cartesian = viewer.camera.pickEllipsoid(movement.endPosition, ellipsoid)
          if (cartesian) {
            //世界坐标转地理坐标（弧度）
            let cartographic = viewer.scene.globe.ellipsoid.cartesianToCartographic(cartesian)
            if (cartographic) {
              //海拔
              let height = viewer.scene.globe.getHeight(cartographic)
              //视角海拔高度
              let he = Math.sqrt(
                viewer.scene.camera.positionWC.x * viewer.scene.camera.positionWC.x +
                  viewer.scene.camera.positionWC.y * viewer.scene.camera.positionWC.y +
                  viewer.scene.camera.positionWC.z * viewer.scene.camera.positionWC.z
              )
              let he2 = Math.sqrt(cartesian.x * cartesian.x + cartesian.y * cartesian.y + cartesian.z * cartesian.z)
              //地理坐标（弧度）转经纬度坐标
              let point = [(cartographic.longitude / Math.PI) * 180, (cartographic.latitude / Math.PI) * 180]
              if (!height) {
                height = 0
              }
              if (!he) {
                he = 0
              }
              if (!he2) {
                he2 = 0
              }
              if (!point) {
                point = [0, 0]
              }

              var layerResolution = 156543.03392804097 // 示例分辨率，根据实际图层进行调整

              longitude_show.innerHTML = point[0].toFixed(6) //经度
              latitude_show.innerHTML = point[1].toFixed(6) //纬度
              altitude_show.innerHTML = (he - he2).toFixed(4)
              level_show.innerHTML = Math.round(Math.log2(layerResolution / ((he - he2) / canvas.clientHeight)))
              resolution_show.innerHTML = ((he - he2) / canvas.clientHeight).toFixed(4)
            }
          }
        }, Cesium.ScreenSpaceEventType.MOUSE_MOVE)
      })
    }
  },
  mixins: [AdjustHeightMixin]
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style>
.div_content {
  position: relative;
}

.div_cesium {
  position: relative;
}

.latlng_show {
  width: 900px;
  height: 30px;
  position: absolute;
  bottom: 25px;
  right: 30px;
  z-index: 1;
  font-size: 15px;
  border-radius: 100px;
  background-color: rgba(112, 128, 144, 0.3);
  padding: 10px;
}

.div_buttonfather {
  position: absolute;
  top: 40px;
  width: 765px;
  height: 52px;
  left: 20px;
  border-radius: 50px;
  background-color: rgba(112, 128, 144, 0.5);
  /* padding: 20px; */
}

.block {
  position: absolute;
  bottom: 200px;
  width: 1000px;
  height: 52px;
  left: 20px;
}

.ant-menu {
  background: transparent;
  font-size: 14px;
}

.submenu-title-wrapper {
  color: #fff;
}

.vis-options {
  position: absolute;
  z-index: 999;
  top: 100px;
  left: 10px;
  background: rgba(0, 0, 0, 0.207);
  border-radius: 6px;
  padding: 15px;
}

.option {
  color: white;
  padding: 10px 0;
}

.gradient-picker {
  /* position: relative; */
  padding: 20px;
}

.height-inputs input {
  width: 100px;
  display: block;
  margin: 10px;
}
</style>

<style src="vue-color-gradient-picker/dist/index.css" lang="css" />

<template>
  <div class="div_content" :style="'width:calc(100vw - ' + right + 'px);'">
    <div class="div_cesium" :id="random" :style="'height:' + screenHeight + 'px'"></div>
    <div class="latlng_show">
      <div style="width: 150px; height: 30px; float: left; margin-top: -6px">
        <font size="3" color="white">{{ $t('longtitude') }}<span id="longitude_show"></span>°</font>
      </div>
      <div style="width: 150px; height: 30px; float: left; margin-top: -6px">
        <font size="3" color="white">{{ $t('latitude') }}<span id="latitude_show"></span>°</font>
      </div>
      <div style="width: 170px; height: 30px; float: left; margin-top: -6px">
        <font size="3" color="white">{{ $t('altitude') }}<span id="altitude_show"></span>km</font>
      </div>
    </div>
    <div class="div_smartbox2">
      <img class="img_smartbox2" src="../../static/image/smart-cg-box2.png" />
      <p class="smartbox2_p">{{ $t('datalist') }}</p>
      <el-tree style="background: none" :data="treedata" node-key="id" ref="tree" default-expand-all
        :expand-on-click-node="false" :default-checked-keys="[11, 12]">
        <span class="custom-tree-node" slot-scope="{ node, data }">
          <span>{{ $t(node.label) }}</span>
          <span>
            <el-button type="text" @click="() => remove(node, data)"> {{ $t('delete') }} </el-button>
          </span>
        </span>
      </el-tree>
    </div>
    <div class="div_buttonfather">
      <a-menu v-model="current" mode="horizontal">
        <a-sub-menu>
          <span slot="title" class="submenu-title-wrapper">
            <a-icon type="snippets" />{{ $t('basicdata') }}
          </span>
          <a-menu-item-group :title="$t('basemap')">
            <a-menu-item key="setting:1" @click="tiandimap"> {{ $t('tiandimap') }} </a-menu-item>
            <a-menu-item key="setting:2" @click="tiandimapannotation">{{ $t('tiandimapannotation') }} </a-menu-item>
          </a-menu-item-group>
          <a-menu-item key="setting:10" @click="HenanGPM"> {{ $t('precipitation') }} </a-menu-item>
          <a-menu-item key="setting:11" @click="Roadnetwork"> {{ $t('henanroadnetwork') }} </a-menu-item>
          <a-menu-item-group :title="$t('datapoi')">
            <a-menu-item key="setting:111" @click="EduPOI"> {{ $t('educationpoi') }} </a-menu-item>
            <a-menu-item key="setting:112" @click="TransPOI">{{ $t('transportationpoi') }} </a-menu-item>
            <a-menu-item key="setting:113" @click="HotelPOI">{{ $t('hotelpoi') }} </a-menu-item>
            <a-menu-item key="setting:114" @click="TravelPOI">{{ $t('travelpoi') }} </a-menu-item>
            <a-menu-item key="setting:115" @click="MedicalPOI">{{ $t('medicalpoi') }} </a-menu-item>
            <a-menu-item key="setting:116" @click="GovPOI">{{ $t('governmentpoi') }} </a-menu-item>
          </a-menu-item-group>
        </a-sub-menu>
        <a-sub-menu>
          <span slot="title" class="submenu-title-wrapper">
            <a-icon type="history" />{{ $t('disasterevent') }}
          </span>
          <a-menu-item key="setting:20" @click="addDisaster"> {{ $t('adddisasterevent') }} </a-menu-item>
          <a-menu-item key="setting:21" @click="addEvent"> {{ $t('addpointlineeventbycoordinate') }} </a-menu-item>
          <a-menu-item key="setting:22" @click="addEventText"> {{ $t('addpointlineeventbytext') }} </a-menu-item>
          <a-menu-item key="setting:23" @click="viewPointEvent"> {{ $t('viewpointevent') }} </a-menu-item>
          <a-menu-item key="setting:24" @click="viewLineEvent"> {{ $t('viewlineevent') }} </a-menu-item>
        </a-sub-menu>
        <a-sub-menu>
          <span slot="title" class="submenu-title-wrapper">
            <a-icon type="loading" />{{ $t('disasterevaluation') }}
          </span>
          <!-- <a-menu-item key="setting:31" @click="floodAnalysis"> {{ $t('floodanalysis') }} </a-menu-item> -->
          <a-menu-item key="setting:32" @click="Flood0720"> {{ $t('floodrangeof0720') }} </a-menu-item>
          <a-menu-item key="setting:33" @click="Flood0722"> {{ $t('floodrangeof0722') }} </a-menu-item>
          <a-menu-item key="setting:34" @click="AffectedPOI"> {{ $t('poiaffectedbydisasters') }} </a-menu-item>
        </a-sub-menu>
        <a-sub-menu>
          <span slot="title" class="submenu-title-wrapper">
            <a-icon type="line" />{{ $t('emergencyvehicleinduction') }}
          </span>
          <a-menu-item key="setting:17" @click="pgrouting"> {{ $t('querytheshortestpath') }} </a-menu-item>
        </a-sub-menu>
      </a-menu>
    </div>
    <disaster-form ref="disasterChild" @update="rightDisasterChanged"></disaster-form>
    <view-point-event ref="viewPointEventChild" @update="rightViewPointEventChanged"></view-point-event>
    <view-line-event ref="viewLineEventChild" @update="rightViewLineEventChanged"></view-line-event>
    <sample-query ref="mychild" @update="rightSampleChanged"></sample-query>
    <event-form ref="eventChild" @update="rightEventChanged"></event-form>
    <event-form-text ref="eventTextChild" @update="rightEventTextChanged"></event-form-text>
    <route ref="routeChild" @update="rightRouteChanged"></route>
    <affected-p-o-i ref="affectedPOIChild" @update="rightAffectedPOIChanged"></affected-p-o-i>
  </div>
</template>

<script>
//cesium 不支持import, 改用require
let Cesium = require('cesium/Cesium')
require('cesium/Widgets/widgets.css')
import imageryViewModels from '../../modules/ProviderViewModels'
import axios from 'axios'
import { tianDTAnno, tainDTVectorAnno, tianDTAnno_EN } from '../../modules/AnnotationLayerProviders'
import { AdjustHeightMixin } from '../../modules/AdjustHeightMixin'
import CesiumNavigation from './cesium-navigation/viewerCesiumNavigationMixin'
import ShrinkView from './ShrinkView.vue'
import DisasterForm from './DisasterForm.vue'
import ViewPointEvent from './ViewPointEvent.vue'
import ViewLineEvent from './ViewLineEvent.vue'
import EventForm from './EventForm.vue'
import EventFormText from './EventFormText.vue'
import Route from './Route.vue'
import AffectedPOI from './AffectedPOI.vue'
import SampleQuery from './SampleQuery.vue'
import ImageLoader from '../../modules/ImageLoader'
import shapeDrawer from '../../modules/shapeDrawer'
import GifImageProperty from '../../static/js/GifImageProperty'
import {
  tianDTimg_w_imageryProvier,
  HenanRoadNetwork,
  POI_Edu,
  POI_Trans,
  POI_Hotel,
  POI_View,
  POI_Medical,
  POI_Adm,
  flood0720,
  flood0722,
  GDRoad,
  testonthefly,
} from '../../modules/ImageryProvider'

var positions = []
export default {
  name: 'CesiumViewer',
  data() {
    return {
      tianDT: 'zh',
      current: ['home'],
      viewer: null,
      random: Math.random().toString(36).substring(2),
      right: 0,
      layers: [],
      treedata: [
        {
          id: 1,
          label: 'basemap',
          children: [
            {
              id: 11,
              label: 'tiandimap',
            },
            {
              id: 12,
              label: 'tiandimapannotation',
            },
          ],
        },
      ],
      positions,
      drawerNum: 0,
      visibility: 'visible',
      replaceFields: {
        children: 'child',
        title: 'name',
      },
      level: 0,
      windowRange: "",
      msg: "",
      layers: [],
      dagbool: false,
    }
  },
  components: {
    ShrinkView,
    DisasterForm,
    ViewPointEvent,
    ViewLineEvent,
    SampleQuery,
    EventForm,
    EventFormText,
    Route,
    AffectedPOI,
  },
  computed: {
    Anno: function () {
      return this.$t('Anno_layer')
    },
  },
  watch: {
    Anno: function (n) {
      var m = 0
      if (n == 'eia_w') {
        for (var i = this.viewer.imageryLayers.length - 1; i >= 0; i--) {
          if (
            this.viewer.imageryLayers._layers[i].imageryProvider.url ==
            'http://{s}.tianditu.gov.cn/cia_w/wmts?service=wmts&request=GetTile&version=1.0.0&LAYER=cia&tileMatrixSet=w&TileMatrix={TileMatrix}&TileRow={TileRow}&TileCol={TileCol}&style=default.jpg&tk=a8d24817df4464a4000f28522ab777b2'
          ) {
            this.viewer.imageryLayers.remove(this.viewer.imageryLayers._layers[i])
            m = m + 1
          } else if (
            this.viewer.imageryLayers._layers[i].imageryProvider.url ==
            'http://{s}.tianditu.gov.cn/eia_w/wmts?service=wmts&request=GetTile&version=1.0.0&LAYER=eia&tileMatrixSet=w&TileMatrix={TileMatrix}&TileRow={TileRow}&TileCol={TileCol}&style=default.jpg&tk=a8d24817df4464a4000f28522ab777b2'
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
            'http://{s}.tianditu.gov.cn/cia_w/wmts?service=wmts&request=GetTile&version=1.0.0&LAYER=cia&tileMatrixSet=w&TileMatrix={TileMatrix}&TileRow={TileRow}&TileCol={TileCol}&style=default.jpg&tk=a8d24817df4464a4000f28522ab777b2'
          ) {
            this.viewer.imageryLayers.remove(this.viewer.imageryLayers._layers[i])
            m = m + 1
          } else if (
            this.viewer.imageryLayers._layers[i].imageryProvider.url ==
            'http://{s}.tianditu.gov.cn/eia_w/wmts?service=wmts&request=GetTile&version=1.0.0&LAYER=eia&tileMatrixSet=w&TileMatrix={TileMatrix}&TileRow={TileRow}&TileCol={TileCol}&style=default.jpg&tk=a8d24817df4464a4000f28522ab777b2'
          ) {
            this.viewer.imageryLayers.remove(this.viewer.imageryLayers._layers[i])
            m = m + 1
          }
        }
        if (m == 1) {
          this.viewer.imageryLayers.addImageryProvider(tianDTAnno, 2)
        }
      }
    },
  },
  mounted() {
    this.$bus.$on('tianDTchange', this.changetianDT)
    this.initCesiumViewer()
    this.$nextTick(() => {
      let viewer = this.viewer
      this.$refs.mychild.imgLoader = new ImageLoader(viewer) //实例化./modules/Imageloader
      this.$refs.mychild.shapeDrawer = new shapeDrawer(viewer, (positions) => {
        this.$refs.mychild.initData.minLon = positions[0]
        this.$refs.mychild.initData.maxLon = positions[2]
        this.$refs.mychild.initData.minLat = positions[1]
        this.$refs.mychild.initData.maxLat = positions[3]
        //TODO:存在bug，提交后，在此绘制无法传输到form中,顾通过此函数传递
        console.log(positions)
      }) //实例化./modules/shapeDrawer
      this.$refs.mychild.currentTab('tab1', false)
    })
  },
  methods: {
    modis() {
      function getZoomLevel(viewer) {
        let h = viewer.camera.positionCartographic.height
        if (h <= 100) { return 19 }
        else if (h <= 300) { return 18 }
        else if (h <= 660) { return 17 }
        else if (h <= 1300) { return 16 }
        else if (h <= 2600) { return 15 }
        else if (h <= 6400) { return 14 }
        else if (h <= 13200) { return 13 }
        else if (h <= 26000) { return 12 }
        else if (h <= 67985) { return 11 }
        else if (h <= 139780) { return 10 }
        else if (h <= 250600) { return 9 }
        else if (h <= 380000) { return 8 }
        else if (h <= 640000) { return 7 }
        else if (h <= 1280000) { return 6 }
        else if (h <= 2600000) { return 5 }
        else if (h <= 6100000) { return 4 }
        else if (h <= 11900000) { return 3 }
        else { return 2 }
      }
      function getCesiumViewerExtend(viewer) {
        let params = {};
        let extend = viewer.camera.computeViewRectangle();
        if (typeof extend === "undefined") {
          //2D下会可能拾取不到坐标，extend返回undefined,所以做以下转换
          let canvas = viewer.scene.canvas;
          let upperLeft = new Cesium.Cartesian2(0, 0); //canvas左上角坐标转2d坐标
          let lowerRight = new Cesium.Cartesian2(canvas.clientWidth, canvas.clientHeight); //canvas右下角坐标转2d坐标

          let ellipsoid = viewer.scene.globe.ellipsoid;
          let upperLeft3 = viewer.camera.pickEllipsoid(upperLeft, ellipsoid); //2D转3D世界坐标

          let lowerRight3 = viewer.camera.pickEllipsoid(lowerRight, ellipsoid); //2D转3D世界坐标

          let upperLeftCartographic = viewer.scene.globe.ellipsoid.cartesianToCartographic(upperLeft3); //3D世界坐标转弧度
          let lowerRightCartographic = viewer.scene.globe.ellipsoid.cartesianToCartographic(lowerRight3); //3D世界坐标转弧度

          let minx = Cesium.Math.toDegrees(upperLeftCartographic.longitude); //弧度转经纬度
          let maxx = Cesium.Math.toDegrees(lowerRightCartographic.longitude); //弧度转经纬度

          let miny = Cesium.Math.toDegrees(lowerRightCartographic.latitude); //弧度转经纬度
          let maxy = Cesium.Math.toDegrees(upperLeftCartographic.latitude); //弧度转经纬度

          console.log("经度：" + minx + "----" + maxx);
          console.log("纬度：" + miny + "----" + maxy);

          params.minx = minx;
          params.maxx = maxx;
          params.miny = miny;
          params.maxy = maxy;
        } else {
          //3D获取方式
          params.maxx = Cesium.Math.toDegrees(extend.east);
          params.maxy = Cesium.Math.toDegrees(extend.north);

          params.minx = Cesium.Math.toDegrees(extend.west);
          params.miny = Cesium.Math.toDegrees(extend.south);
        }
        return params; //返回屏幕所在经纬度范围
      }

      let url1 = '/geocube8085/oge-dag/saveDagJson?dagString=' + { "0": { "functionInvocationValue": { "functionName": "CoverageCollection.addStyles", "arguments": { "input": { "functionInvocationValue": { "functionName": "CoverageCollection.binarization", "arguments": { "coverageCollection": { "functionInvocationValue": { "functionName": "CoverageCollection.subCollection", "arguments": { "filter": { "functionInvocationValue": { "functionName": "Filter.and", "arguments": { "filters": { "arrayValue": { "values": [{ "functionInvocationValue": { "functionName": "Filter.equals", "arguments": { "rightValue": { "constantValue": "EPSG:4326" }, "leftField": { "constantValue": "crs" } } } }, { "functionInvocationValue": { "functionName": "Filter.equals", "arguments": { "rightValue": { "constantValue": "NDVI" }, "leftField": { "constantValue": "measurementName" } } } }] } } } } }, "input": { "functionInvocationValue": { "functionName": "Service.getCoverageCollection", "arguments": { "baseUrl": { "constantValue": "http://localhost" }, "datetime": { "arrayValue": { "values": [{ "valueReference": "1" }, { "valueReference": "1" }] } }, "productID": { "constantValue": "MOD13Q1_061" }, "bbox": { "constantValue": [73.62, 18.19, 134.7601467382, 53.54] } } } } } } }, "threshold": { "constantValue": 220 } } } }, "min": { "constantValue": 0 }, "method": { "constantValue": "timeseries" }, "max": { "constantValue": 255 }, "palette": { "constantValue": "green" } } } }, "1": { "constantValue": "2022-03-06 00:00:00" } }
      axios.post(url1).then(
        (response) => {
          console.log('提交请求成功！')
          this.dagbool = true
          return response.data
        },
        (response) => {
          console.log('提交请求失败！')
          return 'error'
        }
      )
      if (this.dagbool) {
        this.viewer.camera.moveEnd.addEventListener(() => {
          this.level = getZoomLevel(this.viewer)
          this.windowRange = getCesiumViewerExtend(this.viewer)
          console.log(this.level)
          console.log(this.windowRange)
          var spatialRange = this.windowRange.minx + ',' + this.windowRange.miny + ',' + this.windowRange.maxx + ',' + this.windowRange.maxy
          let url2 = '/geocube8085/oge-dag/testDagJson?level=' + this.level + '&spatialRange=' + spatialRange
          axios.post(url2).then(
            (response) => {
              console.log('提交请求成功！')
              for (let obj of this.layers) {
                if (obj.type == 'addImageryProvider') {
                  let layer = obj.value;
                  this.viewer.imageryLayers.remove(layer);
                } else if (obj.type == 'dataSources') {
                  let layer = obj.value;
                  this.viewer.dataSources.remove(layer);
                }
              }
              this.layers = [];
              this.msg = response.data
              var rasterList = this.msg.raster;
              console.log(rasterList)
              for (var rasterIndex in rasterList) {
                console.log(rasterList[rasterIndex].url)
                let layerAdded = this.viewer.imageryLayers.addImageryProvider(
                  new Cesium.UrlTemplateImageryProvider({
                    url: rasterList[rasterIndex].url,
                  })
                );
                let imgObj = {
                  url: rasterList[rasterIndex].url,
                  type: "addImageryProvider",
                  value: layerAdded,
                };
                this.layers.push(imgObj);
              }
              return response.data
            },
            (response) => {
              console.log('提交请求失败！')
              return 'error'
            }
          )
        })
      }
    },
    changetianDT(value) {
      this.tianDT = value
    },
    remove(node, data) {
      console.log(node.data.id)
      const parent = node.parent
      const children = parent.data.children || parent.data
      const index = children.findIndex((d) => d.id === data.id)
      children.splice(index, 1)

      if (node.data.id == 1) {
        for (var i = this.viewer.imageryLayers.length - 1; i >= 0; i--) {
          if (
            this.viewer.imageryLayers._layers[i].imageryProvider.url ==
            'http://{s}.tianditu.gov.cn/img_w/wmts?service=wmts&request=GetTile&version=1.0.0&LAYER=img&tileMatrixSet=w&TileMatrix={TileMatrix}&TileRow={TileRow}&TileCol={TileCol}&style=default&format=tiles&tk=a8d24817df4464a4000f28522ab777b2'
          ) {
            this.viewer.imageryLayers.remove(this.viewer.imageryLayers._layers[i])
          } else if (
            this.viewer.imageryLayers._layers[i].imageryProvider.url ==
            'http://{s}.tianditu.gov.cn/cia_w/wmts?service=wmts&request=GetTile&version=1.0.0&LAYER=cia&tileMatrixSet=w&TileMatrix={TileMatrix}&TileRow={TileRow}&TileCol={TileCol}&style=default.jpg&tk=a8d24817df4464a4000f28522ab777b2'
          ) {
            this.viewer.imageryLayers.remove(this.viewer.imageryLayers._layers[i])
          }
        }
      }
      if (node.data.id == 11) {
        for (var i = this.viewer.imageryLayers.length - 1; i >= 0; i--) {
          if (
            this.viewer.imageryLayers._layers[i].imageryProvider.url ==
            'http://{s}.tianditu.gov.cn/img_w/wmts?service=wmts&request=GetTile&version=1.0.0&LAYER=img&tileMatrixSet=w&TileMatrix={TileMatrix}&TileRow={TileRow}&TileCol={TileCol}&style=default&format=tiles&tk=a8d24817df4464a4000f28522ab777b2'
          ) {
            this.viewer.imageryLayers.remove(this.viewer.imageryLayers._layers[i])
          }
        }
      }
      if (node.data.id == 12) {
        for (var i = this.viewer.imageryLayers.length - 1; i >= 0; i--) {
          if (
            this.viewer.imageryLayers._layers[i].imageryProvider.url ==
            'http://{s}.tianditu.gov.cn/cia_w/wmts?service=wmts&request=GetTile&version=1.0.0&LAYER=cia&tileMatrixSet=w&TileMatrix={TileMatrix}&TileRow={TileRow}&TileCol={TileCol}&style=default.jpg&tk=a8d24817df4464a4000f28522ab777b2'
          ) {
            this.viewer.imageryLayers.remove(this.viewer.imageryLayers._layers[i])
          } else if (
            this.viewer.imageryLayers._layers[i].imageryProvider.url ==
            'http://{s}.tianditu.gov.cn/eia_w/wmts?service=wmts&request=GetTile&version=1.0.0&LAYER=eia&tileMatrixSet=w&TileMatrix={TileMatrix}&TileRow={TileRow}&TileCol={TileCol}&style=default.jpg&tk=a8d24817df4464a4000f28522ab777b2'
          ) {
            this.viewer.imageryLayers.remove(this.viewer.imageryLayers._layers[i])
          }
        }
      }
      if (node.data.id == 2) {
        for (var i = this.viewer.imageryLayers.length - 1; i >= 0; i--) {
          if (this.viewer.imageryLayers._layers[i].imageryProvider._layers == 'HenanGPM:HenanGPM') {
            this.viewer.animation.container.style.visibility = 'hidden'
            this.viewer.timeline.container.style.visibility = 'hidden'
            this.viewer.imageryLayers.remove(this.viewer.imageryLayers._layers[i])
          } else if (this.viewer.imageryLayers._layers[i].imageryProvider._layers == 'poi_edu') {
            this.viewer.imageryLayers.remove(this.viewer.imageryLayers._layers[i])
          } else if (this.viewer.imageryLayers._layers[i].imageryProvider._layers == 'poi_trans') {
            this.viewer.imageryLayers.remove(this.viewer.imageryLayers._layers[i])
          } else if (this.viewer.imageryLayers._layers[i].imageryProvider._layers == 'poi_hotel') {
            this.viewer.imageryLayers.remove(this.viewer.imageryLayers._layers[i])
          } else if (this.viewer.imageryLayers._layers[i].imageryProvider._layers == 'poi_view') {
            this.viewer.imageryLayers.remove(this.viewer.imageryLayers._layers[i])
          } else if (this.viewer.imageryLayers._layers[i].imageryProvider._layers == 'poi_medical') {
            this.viewer.imageryLayers.remove(this.viewer.imageryLayers._layers[i])
          } else if (this.viewer.imageryLayers._layers[i].imageryProvider._layers == 'poi_adm') {
            this.viewer.imageryLayers.remove(this.viewer.imageryLayers._layers[i])
          } else if (this.viewer.imageryLayers._layers[i].imageryProvider._layers == 'henan_osm') {
            this.viewer.imageryLayers.remove(this.viewer.imageryLayers._layers[i])
          }
        }
      }
      if (node.data.id == 21) {
        for (var i = this.viewer.imageryLayers.length - 1; i >= 0; i--) {
          if (this.viewer.imageryLayers._layers[i].imageryProvider._layers == 'HenanGPM:HenanGPM') {
            this.viewer.animation.container.style.visibility = 'hidden'
            this.viewer.timeline.container.style.visibility = 'hidden'
            this.viewer.imageryLayers.remove(this.viewer.imageryLayers._layers[i])
          }
        }
      }
      if (node.data.id == 22) {
        for (var i = this.viewer.imageryLayers.length - 1; i >= 0; i--) {
          if (this.viewer.imageryLayers._layers[i].imageryProvider._layers == 'poi_edu') {
            this.viewer.imageryLayers.remove(this.viewer.imageryLayers._layers[i])
          }
        }
      }
      if (node.data.id == 23) {
        for (var i = this.viewer.imageryLayers.length - 1; i >= 0; i--) {
          if (this.viewer.imageryLayers._layers[i].imageryProvider._layers == 'poi_trans') {
            this.viewer.imageryLayers.remove(this.viewer.imageryLayers._layers[i])
          }
        }
      }
      if (node.data.id == 24) {
        for (var i = this.viewer.imageryLayers.length - 1; i >= 0; i--) {
          if (this.viewer.imageryLayers._layers[i].imageryProvider._layers == 'poi_hotel') {
            this.viewer.imageryLayers.remove(this.viewer.imageryLayers._layers[i])
          }
        }
      }
      if (node.data.id == 25) {
        for (var i = this.viewer.imageryLayers.length - 1; i >= 0; i--) {
          if (this.viewer.imageryLayers._layers[i].imageryProvider._layers == 'poi_view') {
            this.viewer.imageryLayers.remove(this.viewer.imageryLayers._layers[i])
          }
        }
      }
      if (node.data.id == 26) {
        for (var i = this.viewer.imageryLayers.length - 1; i >= 0; i--) {
          if (this.viewer.imageryLayers._layers[i].imageryProvider._layers == 'poi_medical') {
            this.viewer.imageryLayers.remove(this.viewer.imageryLayers._layers[i])
          }
        }
      }
      if (node.data.id == 27) {
        for (var i = this.viewer.imageryLayers.length - 1; i >= 0; i--) {
          if (this.viewer.imageryLayers._layers[i].imageryProvider._layers == 'poi_adm') {
            this.viewer.imageryLayers.remove(this.viewer.imageryLayers._layers[i])
          }
        }
      }
      if (node.data.id == 28) {
        for (var i = this.viewer.imageryLayers.length - 1; i >= 0; i--) {
          if (this.viewer.imageryLayers._layers[i].imageryProvider._layers == 'henan_osm') {
            this.viewer.imageryLayers.remove(this.viewer.imageryLayers._layers[i])
          }
        }
      }
      if (node.data.id == 3) {
        for (var i = this.viewer.imageryLayers.length - 1; i >= 0; i--) {
          if (this.viewer.imageryLayers._layers[i].imageryProvider._layers == 'HenanFlood:HenanFlood0720-0715') {
            this.viewer.imageryLayers.remove(this.viewer.imageryLayers._layers[i])
          } else if (this.viewer.imageryLayers._layers[i].imageryProvider._layers == 'HenanFlood:HenanFlood0722-0715') {
            this.viewer.imageryLayers.remove(this.viewer.imageryLayers._layers[i])
          }
        }
      }
      if (node.data.id == 31) {
        for (var i = this.viewer.imageryLayers.length - 1; i >= 0; i--) {
          if (this.viewer.imageryLayers._layers[i].imageryProvider._layers == 'HenanFlood:HenanFlood0720-0715') {
            this.viewer.imageryLayers.remove(this.viewer.imageryLayers._layers[i])
          }
        }
      }
      if (node.data.id == 32) {
        for (var i = this.viewer.imageryLayers.length - 1; i >= 0; i--) {
          if (this.viewer.imageryLayers._layers[i].imageryProvider._layers == 'HenanFlood:HenanFlood0722-0715') {
            this.viewer.imageryLayers.remove(this.viewer.imageryLayers._layers[i])
          }
        }
      }
    },
    Close() {
      this.visibility = 'hidden'
    },
    tiandimap() {
      const newChild = { id: 11, label: 'tiandimap', children: [] }
      var m = 0
      var n = 0
      if (this.treedata.length != 0) {
        for (var i = 0; i < this.treedata.length; i++) {
          if (this.treedata[i].id == 1) {
            m = 1
          }
        }
      }
      if (m == 0) {
        const newchildren = { id: 1, label: 'basemap', children: [] }
        this.treedata.push(newchildren)
      }
      for (var i = 0; i < this.treedata.length; i++) {
        if (this.treedata[i].id == 1) {
          if (this.treedata[i].children.length != 0) {
            for (var j = 0; j < this.treedata[i].children.length; j++) {
              if (this.treedata[i].children[j].id == 11) {
                n = 1
              }
            }
          }
        }
      }
      if (n == 0) {
        for (var i = 0; i < this.treedata.length; i++) {
          if (this.treedata[i].id == 1) {
            this.treedata[i].children.push(newChild)
          }
        }
        this.viewer.imageryLayers.addImageryProvider(tianDTimg_w_imageryProvier, 0)
      }
    },
    tiandimapannotation() {
      const newChild = { id: 12, label: 'tiandimapannotation', children: [] }
      var m = 0
      var n = 0
      if (this.treedata.length != 0) {
        for (var i = 0; i < this.treedata.length; i++) {
          if (this.treedata[i].id == 1) {
            m = 1
          }
        }
      }
      if (m == 0) {
        const newchildren = { id: 1, label: 'basemap', children: [] }
        this.treedata.push(newchildren)
      }
      for (var i = 0; i < this.treedata.length; i++) {
        if (this.treedata[i].id == 1) {
          if (this.treedata[i].children.length != 0) {
            for (var j = 0; j < this.treedata[i].children.length; j++) {
              if (this.treedata[i].children[j].id == 12) {
                n = 1
              }
            }
          }
        }
      }
      if (n == 0) {
        for (var i = 0; i < this.treedata.length; i++) {
          if (this.treedata[i].id == 1) {
            this.treedata[i].children.push(newChild)
          }
        }
        if (this.$t('Anno_layer') == 'cia_w') {
          this.viewer.imageryLayers.addImageryProvider(tianDTAnno, 2)
        } else {
          this.viewer.imageryLayers.addImageryProvider(tianDTAnno_EN, 2)
        }
      }
    },
    HenanGPM() {
      const newChild = { id: 21, label: 'precipitation', children: [] }
      var m = 0
      var n = 0
      if (this.treedata.length != 0) {
        for (var i = 0; i < this.treedata.length; i++) {
          if (this.treedata[i].id == 2) {
            m = 1
          }
        }
      }
      if (m == 0) {
        const newchildren = { id: 2, label: 'basicdata', children: [] }
        this.treedata.push(newchildren)
      }
      for (var i = 0; i < this.treedata.length; i++) {
        if (this.treedata[i].id == 2) {
          if (this.treedata[i].children.length != 0) {
            for (var j = 0; j < this.treedata[i].children.length; j++) {
              if (this.treedata[i].children[j].id == 21) {
                n = 1
              }
            }
          }
        }
      }
      if (n == 0) {
        for (var i = 0; i < this.treedata.length; i++) {
          if (this.treedata[i].id == 2) {
            this.treedata[i].children.push(newChild)
          }
        }
        this.viewer.animation.container.style.visibility = 'visible'
        this.viewer.timeline.container.style.visibility = 'visible'
        var start = Cesium.JulianDate.fromIso8601('2021-07-18')
        var stop = Cesium.JulianDate.fromIso8601('2021-07-23')
        this.viewer.timeline.zoomTo(start, stop)
        var clock = this.viewer.clock
        clock.startTime = start
        clock.stopTime = stop
        clock.currentTime = start
        clock.clockRange = Cesium.ClockRange.LOOP_STOP
        clock.multiplier = 8640
        var times = Cesium.TimeIntervalCollection.fromIso8601({
          iso8601: '2021-07-18/2021-07-23/P0Y0M0DT0H30M0S',
          leadingInterval: true,
          trailingInterval: true,
          isStopIncluded: false,
          dataCallback: dataCallback,
        })
        function dataCallback(interval, index) {
          var time
          if (index === 0) {
            time = Cesium.JulianDate.toIso8601(interval.stop)
          } else {
            time = Cesium.JulianDate.toIso8601(interval.start)
          }
          return {
            Time: time,
          }
        }
        var provider = new Cesium.WebMapServiceImageryProvider({
          url: 'http://125.220.153.25:8090/geoserver/wms',
          layers: 'HenanGPM:HenanGPM',
          parameters: {
            service: 'WMS',
            format: 'image/png',
            transparent: true,
          },
          clock: viewer.clock,
          times: times,
        })
        this.viewer.imageryLayers.addImageryProvider(provider, 1)
      }
      console.log(this.treedata)
    },
    Roadnetwork() {
      const newChild = { id: 28, label: 'henanroadnetwork', children: [] }
      var m = 0
      var n = 0
      if (this.treedata.length != 0) {
        for (var i = 0; i < this.treedata.length; i++) {
          if (this.treedata[i].id == 2) {
            m = 1
          }
        }
      }
      if (m == 0) {
        const newchildren = { id: 2, label: 'basicdata', children: [] }
        this.treedata.push(newchildren)
      }
      for (var i = 0; i < this.treedata.length; i++) {
        if (this.treedata[i].id == 2) {
          if (this.treedata[i].children.length != 0) {
            for (var j = 0; j < this.treedata[i].children.length; j++) {
              if (this.treedata[i].children[j].id == 28) {
                n = 1
              }
            }
          }
        }
      }
      if (n == 0) {
        for (var i = 0; i < this.treedata.length; i++) {
          if (this.treedata[i].id == 2) {
            this.treedata[i].children.push(newChild)
          }
        }
        this.viewer.imageryLayers.addImageryProvider(HenanRoadNetwork, 3)
      }
    },
    Flood0720() {
      const newChild = { id: 31, label: 'floodrangeof0720', children: [] }
      var m = 0
      var n = 0
      if (this.treedata.length != 0) {
        for (var i = 0; i < this.treedata.length; i++) {
          if (this.treedata[i].id == 3) {
            m = 1
          }
        }
      }
      if (m == 0) {
        const newchildren = { id: 3, label: 'floodrange', children: [] }
        this.treedata.push(newchildren)
      }
      for (var i = 0; i < this.treedata.length; i++) {
        if (this.treedata[i].id == 3) {
          if (this.treedata[i].children.length != 0) {
            for (var j = 0; j < this.treedata[i].children.length; j++) {
              if (this.treedata[i].children[j].id == 31) {
                n = 1
              }
            }
          }
        }
      }
      if (n == 0) {
        for (var i = 0; i < this.treedata.length; i++) {
          if (this.treedata[i].id == 3) {
            this.treedata[i].children.push(newChild)
          }
        }
        this.viewer.imageryLayers.addImageryProvider(flood0720, 3)
      }
    },
    Flood0722() {
      const newChild = { id: 32, label: 'floodrangeof0722', children: [] }
      var m = 0
      var n = 0
      if (this.treedata.length != 0) {
        for (var i = 0; i < this.treedata.length; i++) {
          if (this.treedata[i].id == 3) {
            m = 1
          }
        }
      }
      if (m == 0) {
        const newchildren = { id: 3, label: 'floodrange', children: [] }
        this.treedata.push(newchildren)
      }
      for (var i = 0; i < this.treedata.length; i++) {
        if (this.treedata[i].id == 3) {
          if (this.treedata[i].children.length != 0) {
            for (var j = 0; j < this.treedata[i].children.length; j++) {
              if (this.treedata[i].children[j].id == 32) {
                n = 1
              }
            }
          }
        }
      }
      if (n == 0) {
        for (var i = 0; i < this.treedata.length; i++) {
          if (this.treedata[i].id == 3) {
            this.treedata[i].children.push(newChild)
          }
        }
        this.viewer.imageryLayers.addImageryProvider(flood0722, 3)
      }
    },
    EduPOI() {
      const newChild = { id: 22, label: 'educationpoi', children: [] }
      var m = 0
      var n = 0
      if (this.treedata.length != 0) {
        for (var i = 0; i < this.treedata.length; i++) {
          if (this.treedata[i].id == 2) {
            m = 1
          }
        }
      }
      if (m == 0) {
        const newchildren = { id: 2, label: 'basicdata', children: [] }
        this.treedata.push(newchildren)
      }
      for (var i = 0; i < this.treedata.length; i++) {
        if (this.treedata[i].id == 2) {
          if (this.treedata[i].children.length != 0) {
            for (var j = 0; j < this.treedata[i].children.length; j++) {
              if (this.treedata[i].children[j].id == 22) {
                n = 1
              }
            }
          }
        }
      }
      if (n == 0) {
        for (var i = 0; i < this.treedata.length; i++) {
          if (this.treedata[i].id == 2) {
            this.treedata[i].children.push(newChild)
          }
        }
        this.viewer.imageryLayers.addImageryProvider(POI_Edu, 3)
        // this.viewer.imageryLayers.addImageryProvider(GDRoad, 10)
        // this.viewer.imageryLayers.addImageryProvider(testonthefly, 10)
      }
    },
    TransPOI() {
      const newChild = { id: 23, label: 'transportationpoi', children: [] }
      var m = 0
      var n = 0
      if (this.treedata.length != 0) {
        for (var i = 0; i < this.treedata.length; i++) {
          if (this.treedata[i].id == 2) {
            m = 1
          }
        }
      }
      if (m == 0) {
        const newchildren = { id: 2, label: 'basicdata', children: [] }
        this.treedata.push(newchildren)
      }
      for (var i = 0; i < this.treedata.length; i++) {
        if (this.treedata[i].id == 2) {
          if (this.treedata[i].children.length != 0) {
            for (var j = 0; j < this.treedata[i].children.length; j++) {
              if (this.treedata[i].children[j].id == 23) {
                n = 1
              }
            }
          }
        }
      }
      if (n == 0) {
        for (var i = 0; i < this.treedata.length; i++) {
          if (this.treedata[i].id == 2) {
            this.treedata[i].children.push(newChild)
          }
        }
        this.viewer.imageryLayers.addImageryProvider(POI_Trans, 3)
      }
    },
    HotelPOI() {
      const newChild = { id: 24, label: 'hotelpoi', children: [] }
      var m = 0
      var n = 0
      if (this.treedata.length != 0) {
        for (var i = 0; i < this.treedata.length; i++) {
          if (this.treedata[i].id == 2) {
            m = 1
          }
        }
      }
      if (m == 0) {
        const newchildren = { id: 2, label: 'basicdata', children: [] }
        this.treedata.push(newchildren)
      }
      for (var i = 0; i < this.treedata.length; i++) {
        if (this.treedata[i].id == 2) {
          if (this.treedata[i].children.length != 0) {
            for (var j = 0; j < this.treedata[i].children.length; j++) {
              if (this.treedata[i].children[j].id == 24) {
                n = 1
              }
            }
          }
        }
      }
      if (n == 0) {
        for (var i = 0; i < this.treedata.length; i++) {
          if (this.treedata[i].id == 2) {
            this.treedata[i].children.push(newChild)
          }
        }
        this.viewer.imageryLayers.addImageryProvider(POI_Hotel, 3)
      }
    },
    TravelPOI() {
      const newChild = { id: 25, label: 'travelpoi', children: [] }
      var m = 0
      var n = 0
      if (this.treedata.length != 0) {
        for (var i = 0; i < this.treedata.length; i++) {
          if (this.treedata[i].id == 2) {
            m = 1
          }
        }
      }
      if (m == 0) {
        const newchildren = { id: 2, label: 'basicdata', children: [] }
        this.treedata.push(newchildren)
      }
      for (var i = 0; i < this.treedata.length; i++) {
        if (this.treedata[i].id == 2) {
          if (this.treedata[i].children.length != 0) {
            for (var j = 0; j < this.treedata[i].children.length; j++) {
              if (this.treedata[i].children[j].id == 25) {
                n = 1
              }
            }
          }
        }
      }
      if (n == 0) {
        for (var i = 0; i < this.treedata.length; i++) {
          if (this.treedata[i].id == 2) {
            this.treedata[i].children.push(newChild)
          }
        }
        this.viewer.imageryLayers.addImageryProvider(POI_View, 3)
      }
    },
    MedicalPOI() {
      const newChild = { id: 26, label: 'medicalpoi', children: [] }
      var m = 0
      var n = 0
      if (this.treedata.length != 0) {
        for (var i = 0; i < this.treedata.length; i++) {
          if (this.treedata[i].id == 2) {
            m = 1
          }
        }
      }
      if (m == 0) {
        const newchildren = { id: 2, label: 'basicdata', children: [] }
        this.treedata.push(newchildren)
      }
      for (var i = 0; i < this.treedata.length; i++) {
        if (this.treedata[i].id == 2) {
          if (this.treedata[i].children.length != 0) {
            for (var j = 0; j < this.treedata[i].children.length; j++) {
              if (this.treedata[i].children[j].id == 26) {
                n = 1
              }
            }
          }
        }
      }
      if (n == 0) {
        for (var i = 0; i < this.treedata.length; i++) {
          if (this.treedata[i].id == 2) {
            this.treedata[i].children.push(newChild)
          }
        }
        this.viewer.imageryLayers.addImageryProvider(POI_Medical, 3)
      }
    },
    GovPOI() {
      const newChild = { id: 27, label: 'governmentpoi', children: [] }
      var m = 0
      var n = 0
      if (this.treedata.length != 0) {
        for (var i = 0; i < this.treedata.length; i++) {
          if (this.treedata[i].id == 2) {
            m = 1
          }
        }
      }
      if (m == 0) {
        const newchildren = { id: 2, label: 'basicdata', children: [] }
        this.treedata.push(newchildren)
      }
      for (var i = 0; i < this.treedata.length; i++) {
        if (this.treedata[i].id == 2) {
          if (this.treedata[i].children.length != 0) {
            for (var j = 0; j < this.treedata[i].children.length; j++) {
              if (this.treedata[i].children[j].id == 27) {
                n = 1
              }
            }
          }
        }
      }
      if (n == 0) {
        for (var i = 0; i < this.treedata.length; i++) {
          if (this.treedata[i].id == 2) {
            this.treedata[i].children.push(newChild)
          }
        }
        this.viewer.imageryLayers.addImageryProvider(POI_Adm, 3)
      }
    },
    AffectedPOI() {
      if (this.drawerNum == 0) {
        this.$refs.affectedPOIChild.showDrawer()
        this.right = 600
        this.$bus.$emit('viewer', this.viewer)
      }
      this.drawerNum = 1
    },
    pgrouting() {
      if (this.drawerNum == 0) {
        this.$refs.routeChild.showDrawer()
        this.right = 600
        this.$bus.$emit('viewer', this.viewer)
      }
      this.drawerNum = 1
    },
    addDisaster() {
      if (this.drawerNum == 0) {
        this.$refs.disasterChild.showDrawer()
        this.right = 600
        this.$bus.$emit('viewer', this.viewer)
      }
      this.drawerNum = 1
    },
    viewPointEvent() {
      if (this.drawerNum == 0) {
        this.$refs.viewPointEventChild.showDrawer()
        this.right = 600
        this.$bus.$emit('viewer', this.viewer)
      }
      this.drawerNum = 1
    },
    viewLineEvent() {
      if (this.drawerNum == 0) {
        this.$refs.viewLineEventChild.showDrawer()
        this.right = 600
        this.$bus.$emit('viewer', this.viewer)
      }
      this.drawerNum = 1
    },
    addEvent() {
      if (this.drawerNum == 0) {
        this.$refs.eventChild.showDrawer()
        this.right = 600
        this.$bus.$emit('viewer', this.viewer)
      }
      this.drawerNum = 1
    },
    addEventText() {
      if (this.drawerNum == 0) {
        this.$refs.eventTextChild.showDrawer()
        this.right = 600
        this.$bus.$emit('viewer', this.viewer)
      }
      this.drawerNum = 1
    },
    rightAffectedPOIChanged(right) {
      this.right = right
      this.drawerNum = 0
    },
    rightDisasterChanged(right) {
      this.right = right
      this.drawerNum = 0
    },
    rightViewPointEventChanged(right) {
      this.right = right
      this.drawerNum = 0
    },
    rightViewLineEventChanged(right) {
      this.right = right
      this.drawerNum = 0
    },
    rightEventChanged(right) {
      this.right = right
      this.drawerNum = 0
    },
    rightEventTextChanged(right) {
      this.right = right
      this.drawerNum = 0
    },
    rightSampleChanged(right) {
      this.right = right
      this.$refs.mychild.shapeDrawer.removeDrawer()
      this.drawerNum = 0
    },
    rightRouteChanged(right) {
      this.right = right
      this.drawerNum = 0
    },
    floodAnalysis() {
      if (this.drawerNum == 0) {
        this.$refs.mychild.showDrawer()
        this.right = 600
        this.$refs.mychild.currentTab('tab1', true)
      }
      this.drawerNum = 1
    },
    //返回当前viewer
    getViewer() {
      return this.viewer
      console.log(this.viewer)
    },
    initCesiumViewer() {
      this.$nextTick(() => {
        //设置静态资源目录
        // Cesium.buildModuleUrl.setBaseUrl('/static/Cesium-1.71/')
        //Initialize the viewer widget with several custom options and mixins.
        Cesium.Ion.defaultAccessToken =
          'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiJmMWM1ZjExZi00Yzg5LTRlOTMtODViYS0yOTZmZmI0OGU2NjQiLCJpZCI6MTg5MDIsInNjb3BlcyI6WyJhc3IiLCJnYyJdLCJpYXQiOjE1NzQ2NjU2ODJ9.bPY0jro1abLzWRoT8Mj4CtH7e0B_dogToc2f5JDm-w0'
        this.viewer = new Cesium.Viewer(this.random, {
          sceneMode: Cesium.SceneMode.SCENE3D,
          baseLayerPicker: true,
          animation: true,
          infoBox: true,
          timeline: true,
          imageryProvider: false,
          // Show Columbus View map with Web Mercator projection
          mapProjection: new Cesium.WebMercatorProjection(),
        })
        this.viewer.animation.container.style.visibility = 'hidden'
        this.viewer.timeline.container.style.visibility = 'hidden'
        this.viewer.extend(CesiumNavigation)

        //Add basic drag and drop functionality
        this.viewer.extend(Cesium.viewerDragDropMixin)
        // this.viewer.extend(Cesium.viewerCesiumNavigationMixin, {})
        //取消左下侧cesium ion logo
        this.viewer.cesiumWidget.creditContainer.style.display = 'none'

        //设置image和地形图层
        this.viewer.baseLayerPicker.viewModel.imageryProviderViewModels = imageryViewModels
        this.viewer.baseLayerPicker.viewModel.selectedImagery =
          this.viewer.baseLayerPicker.viewModel.imageryProviderViewModels[0]
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
          destination: Cesium.Cartesian3.fromDegrees(113.6754, 34.7499, 50000),
          orientation: {
            // 指向
            heading: Cesium.Math.toRadians(0, 0),
            // 视角
            pitch: Cesium.Math.toRadians(-90),
            roll: 0.0,
          },
        })

        window.viewer = this.viewer

        var longitude_show = document.getElementById('longitude_show')
        var latitude_show = document.getElementById('latitude_show')
        var altitude_show = document.getElementById('altitude_show')
        var canvas = viewer.scene.canvas
        //具体事件的实现
        var ellipsoid = viewer.scene.globe.ellipsoid
        var handler = new Cesium.ScreenSpaceEventHandler(canvas)
        handler.setInputAction(function (movement) {
          //捕获椭球体，将笛卡尔二维平面坐标转为椭球体的笛卡尔三维坐标，返回球体表面的点
          var cartesian = viewer.camera.pickEllipsoid(movement.endPosition, ellipsoid)
          if (cartesian) {
            //将笛卡尔三维坐标转为地图坐标（弧度）
            var cartographic = viewer.scene.globe.ellipsoid.cartesianToCartographic(cartesian)
            //将地图坐标（弧度）转为十进制的度数
            var lat_String = Cesium.Math.toDegrees(cartographic.latitude).toFixed(4)
            var log_String = Cesium.Math.toDegrees(cartographic.longitude).toFixed(4)
            var alti_String = (viewer.camera.positionCartographic.height / 1000).toFixed(2)
            longitude_show.innerHTML = log_String
            latitude_show.innerHTML = lat_String
            altitude_show.innerHTML = alti_String
          }
        }, Cesium.ScreenSpaceEventType.MOUSE_MOVE)
      })
    },
  },
  mixins: [AdjustHeightMixin],
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
  width: 500px;
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

.div_smartbox2 {
  position: absolute;
  bottom: 50px;
  width: 300px;
  height: 460px;
  left: 0px;
  /* border-radius: 100px;
  background-color: rgba(112, 128, 144, 0.5); */
  padding: 20px;
}

.img_smartbox2 {
  width: 270px;
  height: 470px;
}

.smartbox2_p {
  position: absolute;
  font-size: 25px;
  color: white;
  top: 32px;
  left: 60px;
}

.smartbox2_tree {
  position: absolute;
  font-size: 16px;
  color: white;
  top: 80px;
  left: 30px;
  width: 0;
}

.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 16px;
  padding-right: 8px;
}

.el-tree {
  bottom: 410px;
  left: 10px;
  background: none;
  color: #fff;
}

.el-tree-node {
  margin-top: 5px;
  margin-bottom: 5px;
  background-color: transparent;
}

.el-tree-node__expand-icon {
  background: transparent;
}

.el-tree-node__content:hover .el-tree-node__expand-icon {
  color: #000;
}

.el-tree-node__content:hover .el-tree-node__expand-icon.is-leaf {
  color: transparent;
}

.el-tree-node__content {
  color: #fff;
}

.el-tree-node__content:hover {
  background-color: #fff;
  color: #000;
}

.el-tree-node:focus>.el-tree-node__content .el-tree-node__expand-icon {
  color: #000;
}

.el-tree-node:focus>.el-tree-node__content {
  background-color: #fff;
  color: #000;
}

.ant-menu {
  background: transparent;
  font-size: 14px;
}

.submenu-title-wrapper {
  color: #fff;
}
</style>
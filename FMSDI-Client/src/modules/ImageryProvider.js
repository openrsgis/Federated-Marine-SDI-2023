let Cesium = require('cesium/Cesium');
export let tianDTimg_w_imageryProvier = new Cesium.WebMapTileServiceImageryProvider({
  url: "http://{s}.tianditu.gov.cn/img_w/wmts?service=wmts&request=GetTile&version=1.0.0" +
    "&LAYER=img&tileMatrixSet=w&TileMatrix={TileMatrix}&TileRow={TileRow}&TileCol={TileCol}" +
    "&style=default&format=tiles&tk=2d585d36d89ab86049e29f6f10364dc3",
  layer: "img_w", //WMTS请求的层名称
  style: "default", //WMTS请求的样式名称
  format: "tiles", //MIME类型，用于从服务器检索图像
  tileMatrixSetID: "GoogleMapsCompatible", //	用于WMTS请求的TileMatrixSet的标识符
  subdomains: ["t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7"], //天地图8个服务器
  minimumLevel: 0, //最小层级
  maximumLevel: 18, //最大层级
});
export let HenanRoadNetwork = new Cesium.WebMapServiceImageryProvider({
  url: 'http://125.220.153.25:8090/geoserver/wms',
  layers: 'henan_osm',
  parameters: {
    service: 'WMS',
    format: 'image/png',
    transparent: true,
  }
});
export let POI_Edu = new Cesium.WebMapServiceImageryProvider({
  url: 'http://125.220.153.25:8090/geoserver/wms',
  layers: 'poi_edu',
  parameters: {
    service: 'WMS',
    format: 'image/png',
    transparent: true,
  }
});
export let POI_Trans = new Cesium.WebMapServiceImageryProvider({
  url: 'http://125.220.153.25:8090/geoserver/wms',

  layers: 'poi_trans',
  parameters: {
    service: 'WMS',
    format: 'image/png',
    transparent: true,
  }
});
export let POI_Hotel = new Cesium.WebMapServiceImageryProvider({
  url: 'http://125.220.153.25:8090/geoserver/wms',

  layers: 'poi_hotel',
  parameters: {
    service: 'WMS',
    format: 'image/png',
    transparent: true,
  }
});
export let POI_View = new Cesium.WebMapServiceImageryProvider({
  url: 'http://125.220.153.25:8090/geoserver/wms',

  layers: 'poi_view',
  parameters: {
    service: 'WMS',
    format: 'image/png',
    transparent: true,
  }
});
export let POI_Medical = new Cesium.WebMapServiceImageryProvider({
  url: 'http://125.220.153.25:8090/geoserver/wms',

  layers: 'poi_medical',
  parameters: {
    service: 'WMS',
    format: 'image/png',
    transparent: true,
  }
});
export let POI_Adm = new Cesium.WebMapServiceImageryProvider({
  url: 'http://125.220.153.25:8090/geoserver/wms',

  layers: 'poi_adm',
  parameters: {
    service: 'WMS',
    format: 'image/png',
    transparent: true,
  }
});
export let PointEvent = new Cesium.WebMapServiceImageryProvider({
  url: 'http://125.220.153.25:8090/geoserver/wms',
  layers: 'henan_osm:point',
  parameters: {
    service: 'WMS',
    format: 'image/png',
    transparent: true,
  }
});
export let PointEventAM = new Cesium.WebMapServiceImageryProvider({
  url: 'http://125.220.153.25:8090/geoserver/wms',
  layers: 'henan_osm:pointam',
  parameters: {
    service: 'WMS',
    format: 'image/png',
    transparent: true,
  }
});

var time_num = new Date().getTime();
export let GDRoad = new Cesium.UrlTemplateImageryProvider({
  // url: '/amap/trafficengine/mapabc/traffictile?v=1.0&x={x}&y={y}&z={z}&t=' + time_num
  url: '/bmap/traffic/TrafficTileService?time=' + time_num + '&v=016&level={z}&x={x}&y={y}'
})

// export let testgeoserver = new Cesium.WebMapTileServiceImageryProvider({//UrlTemplateImageryProvider({//WebMapTileServiceImageryProvider({
//     url: 'http://125.220.153.25:8090/geoserver/gwc/service/wmts/rest/DP:red_river_water_change/{style}/{TileMatrixSet}/{TileMatrixSet}:{TileMatrix}/{TileRow}/{TileCol}?format=image/png',
//     layer: 'DP:red_river_water_change',
//     style: '',
//     format: 'image/png',
//     tileMatrixSetID: 'EPSG:900913'
//     // url:'http://125.220.153.25:8090/geoserver/gwc/service/tms/1.0.0/DP%3AOccurrence@EPSG%3A900913@png'
// })

export let flood0720 = new Cesium.WebMapServiceImageryProvider({
  url: 'http://125.220.153.25:8090/geoserver/wms',
  layers: 'HenanFlood:HenanFlood0720-0715',
  parameters: {
    service: 'WMS',
    format: 'image/png',
    transparent: true,
  }
});
export let flood0722 = new Cesium.WebMapServiceImageryProvider({
  url: 'http://125.220.153.25:8090/geoserver/wms',
  layers: 'HenanFlood:HenanFlood0722-0715',
  parameters: {
    service: 'WMS',
    format: 'image/png',
    transparent: true,
  }
});

export let testonthefly = new Cesium.UrlTemplateImageryProvider({
  url: 'http://localhost:8080/{z}/{x}/{y}',
});
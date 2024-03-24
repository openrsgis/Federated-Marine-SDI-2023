/*
 * @Author: RuixiangLiuWHU lrx_lucky@whu.edu.cn
 * @Date: 2023-04-22 13:26:42
 * @LastEditors: RuixiangLiuWHU lrx_lucky@whu.edu.cn
 * @LastEditTime: 2023-07-06 10:46:12
 * @FilePath: \web_cpoyfromchangan\src\modules\AnnotationLayerProviders.js
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
let Cesium = require('cesium/Cesium');
// require('cesium/Build/Cesium/Widgets/widgets.css');

//天地图影像中文标记服务(墨卡托投影)  
//Usage: 用于卫星影像图层，与tainDTVectorAnno相比多了行政矢量边界
export let tianDTAnno = new Cesium.WebMapTileServiceImageryProvider({
    url: "http://{s}.tianditu.gov.cn/cia_w/wmts?service=wmts&request=GetTile&version=1.0.0" +
        "&LAYER=cia&tileMatrixSet=w&TileMatrix={TileMatrix}&TileRow={TileRow}&TileCol={TileCol}" +
        "&style=default.jpg&tk=2d585d36d89ab86049e29f6f10364dc3",
    layer: "cia_w",	//WMTS请求的层名称
    style: "default",//WMTS请求的样式名称
    format: "tiles",//MIME类型，用于从服务器检索图像
    tileMatrixSetID: "GoogleMapsCompatible",//	用于WMTS请求的TileMatrixSet的标识符
    subdomains: ["t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7"],//天地图8个服务器
    minimumLevel: 0,//最小层级
    maximumLevel: 18
});
//天地图矢量中文标记服务        
//Usage:用于矢量地图图层，与tianDTAnno的区别是，只有地名注记，没有行政矢量边界 
export let tainDTVectorAnno = new Cesium.WebMapTileServiceImageryProvider({
    url: "http://{s}.tianditu.gov.cn/cva_w/wmts?service=wmts&request=GetTile&version=1.0.0" +
        "&LAYER=cva&tileMatrixSet=w&TileMatrix={TileMatrix}&TileRow={TileRow}&TileCol={TileCol}" +
        "&style=default.jpg&tk=2d585d36d89ab86049e29f6f10364dc3",
    layer: "cva_w",	//WMTS请求的层名称
    style: "default",//WMTS请求的样式名称
    format: "tiles",//MIME类型，用于从服务器检索图像
    tileMatrixSetID: "GoogleMapsCompatible",//	用于WMTS请求的TileMatrixSet的标识符
    subdomains: ["t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7"],//天地图8个服务器
    minimumLevel: 0,//最小层级
    maximumLevel: 18
});


export let tianDTAnno_EN = new Cesium.WebMapTileServiceImageryProvider({
    url: "http://{s}.tianditu.gov.cn/eia_w/wmts?service=wmts&request=GetTile&version=1.0.0" +
        "&LAYER=eia&tileMatrixSet=w&TileMatrix={TileMatrix}&TileRow={TileRow}&TileCol={TileCol}" +
        "&style=default.jpg&tk=2d585d36d89ab86049e29f6f10364dc3",
    layer: "eia_w",	//WMTS请求的层名称
    style: "default",//WMTS请求的样式名称
    format: "tiles",//MIME类型，用于从服务器检索图像
    tileMatrixSetID: "GoogleMapsCompatible",//	用于WMTS请求的TileMatrixSet的标识符
    subdomains: ["t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7"],//天地图8个服务器
    minimumLevel: 0,//最小层级
    maximumLevel: 18
});


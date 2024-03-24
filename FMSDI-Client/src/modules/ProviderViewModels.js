/*
 * @Author: RuixiangLiuWHU lrx_lucky@whu.edu.cn
 * @Date: 2023-04-22 13:26:42
 * @LastEditors: RuixiangLiuWHU lrx_lucky@whu.edu.cn
 * @LastEditTime: 2023-07-11 21:58:44
 * @FilePath: \geostreamcube_web\src\modules\ProviderViewModels.js
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
let Cesium = require('cesium/Cesium');
// require('cesium/Build/Cesium/Widgets/widgets.css');
// Cesium.buildModuleUrl.setBaseUrl('/static/Cesium-1.71/')//设置静态资源目录

var imageryViewModels = []; //存放需要显示的imagery图层服务


//天地图img_w影像服务,墨卡托投影
let tianDTimg_w_imageryProvier = new Cesium.WebMapTileServiceImageryProvider({
    url: "http://{s}.tianditu.gov.cn/img_w/wmts?service=wmts&request=GetTile&version=1.0.0" +
        "&LAYER=img&tileMatrixSet=w&TileMatrix={TileMatrix}&TileRow={TileRow}&TileCol={TileCol}" +
        "&style=default&format=tiles&tk=a8d24817df4464a4000f28522ab777b2",
    layer: "img_w",	//WMTS请求的层名称
    style: "default",//WMTS请求的样式名称
    format: "tiles",//MIME类型，用于从服务器检索图像
    tileMatrixSetID: "GoogleMapsCompatible",//	用于WMTS请求的TileMatrixSet的标识符
    subdomains: ["t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7"],//天地图8个服务器
    minimumLevel: 0,//最小层级
    maximumLevel: 18,//最大层级
});
let tianDTimg_w_viewModel = new Cesium.ProviderViewModel({
    name: '天地图\u00ad影像服务',
    iconUrl: Cesium.buildModuleUrl('Widgets/Images/ImageryProviders/bingAerial.png'),
    tooltip: '天地图国家地理信息公共服务平台.\nhttps://www.tianditu.gov.cn/',
    creationFunction: function () {
        return tianDTimg_w_imageryProvier;
    }
});
imageryViewModels.push(tianDTimg_w_viewModel);

//OSM地图服务
let osm_imageryProvider = new Cesium.OpenStreetMapImageryProvider({
    url: 'https://c.tile.openstreetmap.org/'
});
let osg_viewModel = new Cesium.ProviderViewModel({
    name: 'Open\u00adStreet\u00adMap',
    iconUrl: Cesium.buildModuleUrl('Widgets/Images/ImageryProviders/openStreetMap.png'),
    tooltip: 'OpenStreetMap (OSM) is a collaborative project to create a free editable \
map of the world.\nhttp://www.openstreetmap.org',
    creationFunction: function () {
        return osm_imageryProvider;
    }
});
imageryViewModels.push(osg_viewModel);

//XYZ地图服务
let OSM = new Cesium.UrlTemplateImageryProvider({
    url: "http://www.google.com/maps/vt?lyrs=s@189&gl=cn&x={x}&y={y}&z={z}",
});
let xyz_viewModel = new Cesium.ProviderViewModel({
    name: 'Google\u00adMap\u00adImagery',
    iconUrl: Cesium.buildModuleUrl('Widgets/Images/ImageryProviders/esriWorldImagery.png'),
    creationFunction: function () {
        return OSM;
    }
});
imageryViewModels.push(xyz_viewModel);

let tileCoordinates = new Cesium.TileCoordinatesImageryProvider()
let Geo_viewModel = new Cesium.ProviderViewModel({
    name: 'Tile Map',
    iconUrl: Cesium.buildModuleUrl('Widgets/Images/ImageryProviders/openStreetMap.png'),
    // tooltip : '天地图国家地理信息公共服务平台.\nhttps://www.tianditu.gov.cn/',
    creationFunction: function () {
        return tileCoordinates;
    }
});
imageryViewModels.push(Geo_viewModel);

export default imageryViewModels
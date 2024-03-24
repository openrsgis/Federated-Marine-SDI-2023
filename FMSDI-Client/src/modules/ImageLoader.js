/*********
 * Example：
 *  var imageLoader = new ImageLoader(viewer);
 *  //叠加影像
 *  imageLoader.loadImage('http://125.220.153.26:8093/data/temp/C4434D76BD5DE07DFC4F0652966FBC16/d355d81c-e9cf-47d9-ad27-2395e8f412f4/LC08_L1TP_ARD_NDWI_2013-03-25.png',120.0,32.0,123.0,35.0);
 *  //移除影像
 *  imageLoader.removeImage('http://125.220.153.26:8093/data/temp/C4434D76BD5DE07DFC4F0652966FBC16/d355d81c-e9cf-47d9-ad27-2395e8f412f4/LC08_L1TP_ARD_NDWI_2013-03-25.png')
 *  作者：LiuChang 
 *  修改：Dyuzz
 *  备注：这个类实例化一次，layer生命周期
 */
let Cesium = require('cesium/Cesium');
// require('cesium/Build/Cesium/Widgets/widgets.css');

class ImageLoader {
    constructor(viewer) {
        this.viewer = viewer;
        this.layers = [];
    }
  
    /**
     * 根据参数叠加影像
     * @param {影像URL} imageURL 
     * @param {左下角经度} lblong 
     * @param {左下角纬度} lblat 
     * @param {右上角经度} rtlong 
     * @param {右上角纬度} rtlat 
     */
    loadImage(imageURL, lblong, lblat, rtlong, rtlat) {
        var layerAdded = this.viewer.imageryLayers.addImageryProvider(new Cesium.SingleTileImageryProvider({
            url: imageURL,
            rectangle: Cesium.Rectangle.fromDegrees(lblong, lblat, rtlong, rtlat),
        }))

        let imgObj = {
            url: imageURL,
            type: "addImageryProvider",
            value: layerAdded
        }
        this.layers.push(imgObj);
    
        this.viewer.camera.flyTo({
            destination: Cesium.Cartesian3.fromDegrees((lblong + rtlong) / 2, (lblat + rtlat) / 2, 500000),
            orientation: {
                heading: Cesium.Math.toRadians(0),
                pitch: Cesium.Math.toRadians(-90),
                roll: Cesium.Math.toRadians(0)
            },
            duration: 0.5
        });
    }

    //加载geojson
    loadGeojson(url) {
        let dataSource =new Cesium.GeoJsonDataSource();
        dataSource.load(url, {
            stroke: Cesium.Color.HOTPINK,
            fill: Cesium.Color.PINK,
            strokeWidth: 3,
            markerSymbol: '!'
          });
        this.viewer.dataSources.add(dataSource);

        let imgObj = {
            url: url,
            type: "dataSources",
            value: dataSource
        }
        this.layers.push(imgObj);      

        this.viewer.flyTo(dataSource);
    }

    /**
     * 根据url移除影像
     * @param {对应layer的url} url 
     */
    removeImageByURL(url) {
        for(let obj of this.layers){
            if(obj.url == url){
                if(obj.type == 'addImageryProvider'){
                    let layer = obj.value
                    this.viewer.imageryLayers.remove(layer)
                    //从layers中删除
                    this.layers = this.layers.filter(function(item) {
                        return item != obj;
                    });             
                }else if(obj.type == 'dataSources'){
                    let layer = obj.value;
                    console.log('测试是否移除geojson');
                    console.log(layer);   
                    this.viewer.dataSources.remove(layer,true)
                    //从layers中删除
                    this.layers = this.layers.filter(function(item) {
                        return item != obj;
                    });                        
                }
            }
        }
    }

    removeAllImage(){
        for(let obj of this.layers) {
            if(obj.type == 'addImageryProvider'){
                let layer = obj.value;
                this.viewer.imageryLayers.remove(layer);
           
            }else if(obj.type == 'dataSources'){
                let layer = obj.value;
                this.viewer.dataSources.remove(layer);                
            }
        }
        this.layers=[];
    }
    /**
     * 加载瓦片
     * @param {瓦片url} url example: 'http://125.220.153.26:8091/getRasterTile/{z}/{x}/{reverseY}.png'
     * @param {左下角经度} lblong 
     * @param {左下角纬度} lblat 
     * @param {右上角经度} rtlong 
     * @param {右上角纬度} rtlat 
     * @param {最大层级} maxLevel 
     * @param {切片方式} tilingScheme 
     */
    loadTileImages(url, lblong, lblat, rtlong, rtlat, maxLevel, tilingScheme = new Cesium.GeographicTilingScheme()) {
        var urlts = new Cesium.UrlTemplateImageryProvider({
            url: url,
            // new Cesium.Resource({
            //     url : url,
            //     // proxy : new Cesium.DefaultProxy('http://localhost:1987/proxy/')
            // }),
            maximumLevel: maxLevel,
            rectangle: new Cesium.Rectangle(
                Cesium.Math.toRadians(lblong),
                Cesium.Math.toRadians(lblat),
                Cesium.Math.toRadians(rtlong),
                Cesium.Math.toRadians(rtlat)),
            tilingScheme: tilingScheme,
        })
        var layerAdded = this.viewer.imageryLayers.addImageryProvider(urlts)
        let imgObj = {
            url: url,
            type: "addImageryProvider",
            value: layerAdded
        }
        this.layers.push(imgObj);
        this.viewer.camera.flyTo({
            destination: Cesium.Cartesian3.fromDegrees((lblong + rtlong) / 2, (lblat + rtlat) / 2, 1000000),
            orientation: {
                heading: Cesium.Math.toRadians(0),
                pitch: Cesium.Math.toRadians(-90),
                roll: Cesium.Math.toRadians(0)
            },
            duration: 0.5
        });
    }    
}

export default ImageLoader;


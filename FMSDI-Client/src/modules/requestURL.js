import apiConfig from './apiConfig'

//基类
class baseUrl {
  constructor(params) {
    //dev环境代理以跨域
    this.baseUrl = apiConfig.baseURL;
    this.baseUrl2 = apiConfig.baseURL2;
    this.baseUrl3 = apiConfig.baseURL3
    // this.baseUrl='http://125.220.153.26:8091/';
    // this.baseUrl2='http://125.220.153.26:8094/';      
    // this.baseUrl3='http://125.220.153.26:8097/';     
    this.params = params;
  }
  getURL() {
  }
}
//获取geocube中产品名称url
/*params:{}
  http://baseUrl/Geocube/GetProductNames   */
class productNames extends baseUrl {
  getURL() {
    return this.baseUrl + 'Geocube/GetProductNames';
  }
}

//根据产品名称获取该产品所具有的波段名称
/*params:{productName:}
  http://baseurl/Geocube/GetMeasurementsByName?name=LC08_L1TP_ARD  */
class bandNamesByProduct extends baseUrl {
  getURL() {
    let productName = this.params.productName;
    return this.baseUrl + 'Geocube/GetMeasurementsByName?name=' + productName;
  }
}


//根据产品名称获取产品中所有item url
/*params:{name:LC08_L1TP_TMS, type:EO}
*/
class capabilitisByProduct extends baseUrl {
  getURL() {
    let { name, type } = this.params;
    return this.baseUrl + 'Geocube/GetProductsByName?name=' + name + '&type=' + type;
  }
}

//根据产品名称、空间位置、时间范围获取产品中所有item url
/*params:{productType, maxAlt, minAlt, minLon, maxLon,minDate,maxDate}
http://125.220.153.26:8094/Geocube/GetProductsByParams?productName=
LC08_L1TP_ARD&startTime=&endTime=&minx=118&miny=30&maxx=122&maxy=36
*/
class capabilitisByParams extends baseUrl {
  getURL() {
    let { productType, maxLat, minLat, minLon, maxLon, minDate, maxDate, bandtype, type } = this.params;
    if (type == 'EO') {//EO产品
      return this.baseUrl + 'Geocube/GetProductsByParamsAndType?productName=' + productType + '&productType=' + type
        + '&startTime=' + minDate + '&endTime=' + maxDate + '&minx=' + minLon
        + '&miny=' + minLat + '&maxx=' + maxLon + '&maxy=' + maxLat + '&measurement=' + bandtype;
    } else if (type == 'Vector') {
      return this.baseUrl + 'Geocube/GetProductsByParamsAndType?productName=' + productType + '&productType=' + type
        + '&startTime=' + minDate + '&endTime=' + maxDate + '&minx=' + minLon
        + '&miny=' + minLat + '&maxx=' + maxLon + '&maxy=' + maxLat;
    }
  }
}

//获取tile瓦片url
/*params: {rasterProductName: LC08_L1TP_TMS,
           time:2013-04-14 02:32:36.004, measurement:Coastal}
 */
class tileURL1st extends baseUrl {
  getURL() {
    let { rasterProductName, time, measurement } = this.params;
    //  return 'http://125.220.153.26:8091/' + 'queryParams?' + 'rasterProductName=' + rasterProductName 
    //           + '&time=' + time + '&measurement=' + measurement;
    return this.baseUrl + 'getRasterTile/' + rasterProductName + '/' + time + '/' + measurement
      + '/' + '{z}/{x}/{reverseY}.png';
  }
}
//获取tile瓦片url，需要提交两次请求，这是第二次
/*params:  {}
  status：deprecated
*/
class tileURL2nd extends baseUrl {
  getURL() {
    return this.baseUrl + 'getRasterTile/' + '{z}/{x}/{reverseY}.png';
  }
}

//在线分析提交url
/*params:  {type: NDWI/WNDWI/NDVI/WOFS/} 
http://125.220.153.26:8091/geocube/processes/ndwi/jobs/
*/
class onlineAnalysisSubmit extends baseUrl {
  getURL() {
    let { type } = this.params;
    return this.baseUrl + 'geocube/processes/' + type + '/jobs/';
  }
}

class olapProducts extends baseUrl {
  getURL() {
    return this.baseUrl3 + 'geocube/olap/getProducts'
  }
}

class olapYears extends baseUrl {
  getURL() {
    return this.baseUrl3 + 'geocube/olap/getYears'
  }
}

class olapCities extends baseUrl {
  getURL() {
    return this.baseUrl3 + 'geocube/olap/getCities'
  }
}

class olapAnalysisSubmit extends baseUrl {
  getURL() {
    let { type } = this.params
    return this.baseUrl3 + 'geocube/processes/' + type + '/jobs/'
  }
}

//新建Cube接口

class insertCube extends baseUrl {
  getURL() {
    return this.baseUrl + 'Geocube/insertCube'
  }
}

class createIngestConf extends baseUrl {
  getURL() {
    return this.baseUrl + 'geocube/createIngestConf'
  }
}

class initDatabase extends baseUrl {
  getURL() {
    let { cubeId, resolutionKey } = this.params
    return this.baseUrl + 'geocube/initDatabase?cubeId=' + cubeId + '&resolutionKey=' + resolutionKey
  }
}

class loadRasterByConf extends baseUrl {
  getURL() {
    let { ingestPath, splitsCount, threadSize, cubeId } = this.params
    return this.baseUrl + 'geocube/loadRasterByConf?ingestPath=' + ingestPath + '&splitsCount=' + splitsCount + '&threadSize=' + threadSize + '&cubeId=' + cubeId
  }
}

/** 对外接口
 *  参数: options(请求类型)：字符串，可取参数[productNames,capabilitis,capabilitisByProduct,tileURL1st,tileURL2nd]
 *  参数：params(请求参数)：对象
 */
import axios from "axios";
class requestURL {
  constructor(options, params) {
    this.options = options;
    this.params = params;
  }

  //获取指定参数的url
  getRequestURL() {
    let requestURL = "";
    switch (this.options) {
      case 'productNames':
        requestURL = new productNames(this.params).getURL();
        break;
      case 'bandNamesByProduct':
        requestURL = new bandNamesByProduct(this.params).getURL();
        break;
      case 'capabilitisByProduct':
        requestURL = new capabilitisByProduct(this.params).getURL();
        break
      case 'tileURL1st':
        requestURL = new tileURL1st(this.params).getURL();
        break;
      case 'tileURL2nd':
        requestURL = new tileURL2nd(this.params).getURL();
        break;
      case 'capabilitisByParams':
        requestURL = new capabilitisByParams(this.params).getURL();
        break;
      case 'onlineAnalysisSubmit':
        requestURL = new onlineAnalysisSubmit(this.params).getURL();
        break;
      case 'olapProducts':
        requestURL = new olapProducts(this.params).getURL();
        break;
      case 'olapCities':
        requestURL = new olapCities(this.params).getURL();
        break;
      case 'olapYears':
        requestURL = new olapYears(this.params).getURL();
        break;
      case 'olapAnalysisSubmit':
        requestURL = new olapAnalysisSubmit(this.params).getURL();
        break;
      case 'insertCube':
        requestURL = new insertCube(this.params).getURL();
        break;
      case 'createIngestConf':
        requestURL = new createIngestConf(this.params).getURL();
        break;
      case 'initDatabase':
        requestURL = new initDatabase(this.params).getURL();
        break;
      case 'loadRasterByConf':
        requestURL = new loadRasterByConf(this.params).getURL()
        break;
      default:
        throw new Error('参数错误!!!');
    }
    return requestURL;
  }

  //同步访问请求,一般情况下不应该使用此函数，应getRequestURL后，使用axios异步回调
  async getResponseDataAsync() {
    let url = this.getRequestURL();
    //向服务器发送请求
    await axios.get(url).then(
      response => {
        console.log("提交请求成功！");
        console.log(response.data);
        return response.data;
      },
      response => {
        console.log("提交请求失败！");
        return "error";
      }
    );
  }
}

export default requestURL;
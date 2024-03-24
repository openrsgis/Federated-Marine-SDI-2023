/*
 * @Author: lrx lrx_lucky@whu.edu.cn
 * @Date: 2022-12-16 02:35:45
 * @LastEditors: RuixiangLiuWHU lrx_lucky@whu.edu.cn
 * @LastEditTime: 2023-09-04 18:01:33
 * @FilePath: \Changan University_bak\src\modules\apiConfig.js
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
//判断是否是生产环境
var isPro = process.env.NODE_ENV === 'production' //process.env.NODE_ENV用于区分是生产环境还是开发环境

//根据环境不同导出不同的baseURL
export default {
    // baseURL: isPro ? 'http://125.220.153.23:20316/geoStreamCube/' : 'http://localhost:20316/geoStreamCube/',
    // tomcatURL: isPro ? 'http://125.220.153.23:8080/geostreamcube/' : 'http://localhost:8080/geostreamcube/'

    baseURL: isPro ? 'http://125.220.153.23:20316/geoStreamCube/' : 'http://125.220.153.23:20316/geoStreamCube/',
    tomcatURL: isPro ? 'http://125.220.153.23:8080/geostreamcube/' : 'http://125.220.153.23:8080/geostreamcube/'
}
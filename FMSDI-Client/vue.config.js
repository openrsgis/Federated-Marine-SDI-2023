/////////////////////////////////////////////////////////////////////////////////
// 去console插件
// const UglifyJsPlugin = require('uglifyjs-webpack-plugin')
const CompressionPlugin = require('compression-webpack-plugin')
// 拷贝文件插件
const CopyWebpackPlugin = require('copy-webpack-plugin')
const webpack = require('webpack')

let cesiumSource = './node_modules/cesium/Source'
let cesiumWorkers = '../Build/Cesium/Workers'
// 后续 import 引入 cesium 为改路径下的
const cesiumBuild = './node_modules/cesium/Build/Cesium'

const path = require('path')
const resolve = dir => {
  return path.join(__dirname, dir)
}

module.exports = {
  assetsDir: 'static',
  parallel: false,
  publicPath: '/', //部署应用包时的基本 URL
  lintOnSave: undefined,
  productionSourceMap: false, // 打包时不生成.map文件
  devServer: {
    //配置服务器
    host: 'localhost',
    port: 3000,
    proxy: {
      '/amap': {
        target: 'https://tm.amap.com',
        changeOrigin: true,
        ws: true,
        pathRewrite: {
          '^/amap': ''
        },
        onProxyReq(proxyReq) {
          if (proxyReq.getHeader('origin')) {
            proxyReq.setHeader('origin', 'https://tm.amap.com')
          }
        }
      },
      '/bmap': {
        target: 'https://its.map.baidu.com:8002',
        changeOrigin: true,
        ws: true,
        pathRewrite: {
          '^/bmap': ''
        },
        onProxyReq(proxyReq) {
          if (proxyReq.getHeader('origin')) {
            proxyReq.setHeader('origin', 'https://its.map.baidu.com:8002')
          }
        }
      }
    }
  },
  chainWebpack: config => {
    config.resolve.alias
      .set('@$', resolve('src'))
      .set('@api', resolve('src/api'))
      .set('@assets', resolve('src/assets'))
      .set('@comp', resolve('src/components'))
      .set('@views', resolve('src/views'))
      .set('@layout', resolve('src/layout'))
      .set('@static', resolve('src/static'))
      .set('@mobile', resolve('src/modules/mobile'))

    //生产环境，开启js\css压缩
    if (process.env.NODE_ENV === 'production') {
      config.plugin('compressionPlugin').use(
        new CompressionPlugin({
          test: /\.js$|.\css|.\less/, // 匹配文件名
          threshold: 10240, // 对超过10k的数据压缩
          deleteOriginalAssets: false // 不删除源文件
        })
      )
    }
  },
  configureWebpack: {
    //配置Webpack
    // devtool: 'source-map',//在浏览器中显示vue源码用来调试
    output: {
      sourcePrefix: ' '
    },
    amd: {
      toUrlUndefined: true
    },
    resolve: {
      alias: {
        vue$: 'vue/dist/vue.esm.js',
        '@': path.resolve('src'),
        cesium: path.resolve(__dirname, cesiumBuild)
      }
    },
    plugins: [
      new CopyWebpackPlugin([{ from: path.join(cesiumSource, cesiumWorkers), to: 'Workers' }]),
      new CopyWebpackPlugin([{ from: path.join(cesiumSource, 'Assets'), to: 'Assets' }]),
      new CopyWebpackPlugin([{ from: path.join(cesiumSource, 'Widgets'), to: 'Widgets' }]),
      new CopyWebpackPlugin([{ from: path.join(cesiumSource, '../Build/Cesium/ThirdParty'), to: 'ThirdParty' }]),
      new webpack.DefinePlugin({
        CESIUM_BASE_URL: JSON.stringify('./')
      })
    ],
    // externals: {
    //   Cesium: "Cesium",
    // },
    optimization: {
      splitChunks: {
        cacheGroups: {
          commons: {
            name: 'Cesium',
            test: /[\\/]node_modules[\\/]cesium/,
            chunks: 'all'
          }
        }
      }
    },
    module: {
      unknownContextCritical: false
    }
  }
}


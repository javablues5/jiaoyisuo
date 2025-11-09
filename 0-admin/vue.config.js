"use strict";
const path = require("path");
const fs = require("fs");

function resolve(dir) {
  return path.join(__dirname, dir);
}

const CompressionPlugin = require("compression-webpack-plugin");
const UglifyJsPlugin = require("uglifyjs-webpack-plugin");

const port = process.env.port || process.env.npm_config_port || 80; // 端口

// ------------------------------
// 关键：实现vue.config.js改动自动重启服务
// 原理：通过监听vue.config.js文件变化，自动退出进程，
//       这样npm run serve（或yarn serve）会重启。
// 推荐：使用webpack-dev-server的before钩子实现，文件变化后自动退出。
// ------------------------------

let watchingConfig = false;
function watchConfigRestart() {
  if (process.env.NODE_ENV === "development" && !watchingConfig) {
    watchingConfig = true;
    const configPath = path.resolve(__dirname, "vue.config.js");
    fs.watch(configPath, { persistent: false }, (eventType) => {
      if (eventType === "change" || eventType === "rename") {
        console.log(
          "\x1b[36m%s\x1b[0m",
          "[dev-server] Detected vue.config.js change. Restarting dev server..."
        );
        process.exit(0);
      }
    });
  }
}
watchConfigRestart();

module.exports = {
  publicPath: process.env.NODE_ENV === "production" ? "/" : "/",
  outputDir: "dist",
  assetsDir: "static",
  lintOnSave: process.env.NODE_ENV === "development",
  productionSourceMap: false,
  devServer: {
    host: "0.0.0.0",
    port: port,
    open: true,
    disableHostCheck: true,
    before() {
      watchConfigRestart();
    },
    proxy: {
      "^/local-oss": {
        target: "http://47.76.27.203:81/", // 测试
        changeOrigin: true, // 开启跨域
        // rewrite: (path) => path.replace(/^\/api/, ''), // 路径重写，去掉 /api 前缀
      },
    },
  },
  css: {
    loaderOptions: {
      sass: {
        sassOptions: { outputStyle: "expanded" },
      },
    },
  },
  configureWebpack: {
    resolve: {
      alias: {
        "@": resolve("src"),
      },
    },
    devtool: "source-map", // 修复可以debugger
    plugins: [
      // new CompressionPlugin({
      //   cache: false, // 不启用文件缓存
      //   test: /\.(js|css|html)?$/i, // 压缩文件格式
      //   filename: "[path].gz[query]", // 压缩后的文件名
      //   algorithm: "gzip", // 使用gzip压缩
      //   minRatio: 0.8, // 压缩率小于1才会压缩
      // }),
      process.env.NODE_ENV === "production"
        ? new UglifyJsPlugin({
            uglifyOptions: {
              output: {
                comments: false, // 去掉注释
              },
              warnings: false, // 禁用警告
              compress: {
                drop_console: true,
                drop_debugger: true,
                // pure_funcs: ["console.log"], // 移除console
              },
            },
          })
        : null,
    ].filter(Boolean),
  },
  chainWebpack(config) {
    config.plugins.delete("preload"); // TODO: need test
    config.plugins.delete("prefetch"); // TODO: need test

    // set svg-sprite-loader
    config.module.rule("svg").exclude.add(resolve("src/assets/icons")).end();
    config.module
      .rule("icons")
      .test(/\.svg$/)
      .include.add(resolve("src/assets/icons"))
      .end()
      .use("svg-sprite-loader")
      .loader("svg-sprite-loader")
      .options({
        symbolId: "icon-[name]",
      })
      .end();

    //  html解析
    config.plugin("html").tap((args) => {
      args[0].title = process.env.VUE_APP_TITLE;
      args[0].platform = process.env.VUE_APP_ENV;
      return args;
    });

    config.when(process.env.NODE_ENV === "production", (config) => {
      // 生产配置
      config
        .plugin("ScriptExtHtmlWebpackPlugin")
        .after("html")
        .use("script-ext-html-webpack-plugin", [
          {
            // `runtime` must same as runtimeChunk name. default is `runtime`
            inline: /runtime\..*\.js$/,
          },
        ])
        .end();

      config.optimization.splitChunks({
        chunks: "all",
        cacheGroups: {
          libs: {
            name: "chunk-libs",
            test: /[\\/]node_modules[\\/]/,
            priority: 10,
            chunks: "initial", // only package third parties that are initially dependent
          },
          elementUI: {
            name: "chunk-elementUI", // split elementUI into a single package
            test: /[\\/]node_modules[\\/]_?element-ui(.*)/, // in order to adapt to cnpm
            priority: 20, // the weight needs to be larger than libs and app or it will be packaged into libs or app
          },
          commons: {
            name: "chunk-commons",
            test: resolve("src/components"), // can customize your rules
            minChunks: 3, //  minimum common number
            priority: 5,
            reuseExistingChunk: true,
          },
        },
      });

      config.optimization.runtimeChunk("single"),
        {
          from: path.resolve(__dirname, "./public/robots.txt"), //防爬虫文件
          to: "./", //到根目录下
        };
    });
  },
};

module.exports = {
  presets: [
    ['@babel/preset-env', { useBuiltIns: 'usage', corejs: 3 }],
    '@vue/babel-preset-jsx'
  ],
  env: {
    development: {
      plugins: ['dynamic-import-node']
    }
  }
}

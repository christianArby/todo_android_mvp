const StyleDictionary = require('style-dictionary')
const baseConfig = require('./config.json')

StyleDictionary.registerTransform({
  name: 'transformPxToRem',
  type: 'value',
  matcher: token => {
    return token.unit === 'pixel' && token.value !== 0
  },
  transformer: token => {
    return `${token.original.value / 16}rem`
  },
})

StyleDictionary.registerTransform({
  name: 'size/percent',
  type: 'value',
  matcher: token => {
    return token.unit === 'percent' && token.value !== 0
  },
  transformer: token => {
    return `${token.value}%`
  },
})

StyleDictionary.registerTransformGroup({
  name: 'custom/css',
  transforms: StyleDictionary.transformGroup['css'].concat([
    'transformPxToRem',
    'size/percent',
  ]),
})

StyleDictionary.registerTransformGroup({
  name: 'custom/scss',
  transforms: StyleDictionary.transformGroup['scss'].concat([
    'transformPxToRem',
    'size/percent',
  ]),
})

StyleDictionary.registerTransformGroup({
  name: 'custom/js',
  transforms: StyleDictionary.transformGroup['js'].concat([
    'transformPxToRem',
    'size/percent',
  ]),
})

const StyleDictionaryExtended = StyleDictionary.extend(baseConfig)

StyleDictionaryExtended.buildAllPlatforms()
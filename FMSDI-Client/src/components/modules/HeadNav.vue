<!--
 * @Author: your name
 * @Date: 2021-09-26 15:16:45
 * @LastEditTime: 2023-09-04 17:02:01
 * @LastEditors: RuixiangLiuWHU lrx_lucky@whu.edu.cn
 * @Description: 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 * @FilePath: \Changan University\src\components\modules\HeadNav.vue
-->
<template>
  <div class="div_HN">
    <div class="div_title">
      <p class="p_title">{{ $t('title') }}</p>
    </div>
    <div class="div_image">
      <img class="img" src="../../static/image/whu.jpg" />
    </div>
    <div class="div_nav">
      <a-menu v-model="current" mode="horizontal">
        <a-menu-item key="home" @click="home"> <a-icon type="desktop" />{{ $t('home') }}</a-menu-item>
      </a-menu>
    </div>
    <div class="div_text">
      <a-button type="link" :style="{ height: '40px', color: 'gray' }">{{ $t('help') }}</a-button>
      <a-button type="link" :style="{ height: '40px', color: 'gray' }">{{ $t('about') }}</a-button>
      <a-radio-group :defaultValue="DEFAULT_LANG" @change="changeLocale">
        <a-radio-button key="cn" :value="DEFAULT_LANG">中文</a-radio-button>
        <a-radio-button key="en" :value="EN_LANG">English</a-radio-button>
      </a-radio-group>
      <a-button :style="{ height: '40px', color: 'gray', border: '1px solid gray' }">{{ $t('login') }}</a-button>
      <a-button :style="{ height: '40px', color: 'gray', border: '1px solid gray' }">{{ $t('register') }}</a-button>
    </div>
  </div>
</template>

<script>
import { DEFAULT_LANG, EN_LANG, setup } from '@/locales'
import moment from 'moment'
export default {
  data() {
    return {
      current: ['home'],
      DEFAULT_LANG,
      EN_LANG,
    }
  },
  methods: {
    home() {
      this.$router.replace('/home')
      this.current = ['home']
      console.log(this.current)
    },
    changeLocale(e) {
      setup(e.target.value)
      this.$bus.$emit('languagechange', e.target.value)
      this.$bus.$emit('tianDTchange', e.target.value)
    },
  },
}
</script>

<style>
.div_title {
  position: absolute;
  left: 60px;
  top: -4px;
}

.p_title {
  font-size: 25px;
}

.div_image {
  position: absolute;
  left: 5px;
  top: -5px;
}

.div_nav {
  position: absolute;
  left: 480px;
  top: 3px;
}

.div_text {
  position: absolute;
  right: 0px;
  top: -4px;
}

.img {
  width: 50px;
  height: 50px;
}
</style>
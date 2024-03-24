<template>
  <a-config-provider :autoInsertSpaceInButton="false" :locale="locale">
    <div id="app">
      <router-view />
    </div>
  </a-config-provider>
</template>

<script>
import zh_CN from 'ant-design-vue/lib/locale-provider/zh_CN'
import en_US from 'ant-design-vue/lib/locale-provider/en_US'
import moment from 'moment'
import 'moment/locale/zh-cn'
import { DEFAULT_LANG, EN_LANG, setup } from './locales'
import { mapState, mapGetters } from 'vuex'
moment.locale('zh-cn')
export default {
  name: 'App',
  data() {
    return {
      locale: zh_CN,
    }
  },
  mounted() {
    this.$bus.$on('languagechange', this.changelanguage)
  },
  methods: {
    moment,
    changelanguage(value) {
      if (value == 'zh') {
        this.locale = zh_CN
      } else if (value == 'en') {
        this.locale = en_US
      }
    },
  },
}
</script>
<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  height: 100%;

  /* margin: 0 60px; */
}
</style>

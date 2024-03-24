<!--
 * @Author: RuixiangLiuWHU lrx_lucky@whu.edu.cn
 * @Date: 2023-04-22 13:26:41
 * @LastEditors: RuixiangLiuWHU lrx_lucky@whu.edu.cn
 * @LastEditTime: 2023-04-26 11:31:23
 * @FilePath: \web\src\components\Home.vue
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
-->
<template>
  <div class="mainBody">
    <div :class="{ 'login-bg': true, 'login-bg-hide': !showLoginForm }">
      <div class="login">
        <h2 class="login-heading">Login</h2>
        <p style="text-align: left; font-family: Montserrat;">Welcome, FMDSI user</p>
        <p style="text-align: left; margin-top: 20px; font-family: Montserrat; color: rgb(255, 68, 35);">
          {{ errorMsg }}
        </p>
        <p style="text-align: left; margin-top: 20px; font-family: Montserrat; color: rgb(0, 127, 195);">
          {{ successMsg }}
        </p>
        <div style="flex:1"></div>
        <a-form class="login-form" @submit="handleSubmit">
          <a-form-item>
            <div class="input-form-item">
              <span class="item-label">Username</span>
              <a-input
                class="item-input"
                placeholder="Enter username here"
                @change="username = $event.currentTarget.value"
              ></a-input>
            </div>
          </a-form-item>
          <a-form-item>
            <div class="input-form-item">
              <span class="item-label">Password</span>
              <a-input
                class="item-input"
                type="password"
                placeholder="Enter password here"
                @change="password = $event.currentTarget.value"
              ></a-input>
            </div>
          </a-form-item>
          <a-form-item class="button-form-item">
            <div class="button-form-item">
              <a-button class="form-button" @click="handleClickSignup" style="margin-right: 20px;">Sign Up</a-button>
              <a-button class="form-button" type="primary" html-type="submit">Login</a-button>
            </div>
          </a-form-item>
        </a-form>
      </div>
    </div>
    <a-layout>
      <a-layout-header class="headerLayout">
        <head-nav></head-nav>
      </a-layout-header>
      <a-layout-content class="contentLayout">
        <cesium-viewer></cesium-viewer>
      </a-layout-content>
    </a-layout>
  </div>
</template>

<script>
import HeadNav from './modules/HeadNav.vue'
import CesiumViewer from './modules/CesiumViewer.vue'

const REGISTERED_USERS = {
  admin: 'admin'
  // Add users here
}

export default {
  components: {
    HeadNav,
    CesiumViewer
  },
  data() {
    return {
      form: null,
      errorMsg: '',
      successMsg: '',
      username: '',
      password: '',
      showLoginForm: true
    }
  },
  methods: {
    handleSubmit(ev) {
      ev.preventDefault()

      if (REGISTERED_USERS[this.username] === undefined || REGISTERED_USERS[this.username] !== this.password) {
        this.errorMsg = 'Username or password was incorrect'
        return
      }

      this.successMsg = 'You have successfully logged in'

      this.showLoginForm = false
    },
    handleClickSignup() {
      alert('Registration not permitted yet.')
    }
  }
}
</script>

<style scoped>
@font-face {
  font-family: Montserrat;
  src: url('../../public/fonts/Montserrat-Medium.ttf') format(truetype);
}
.mainBody {
  margin: 0 auto;
  z-index: 0;
  width: 100%;
  height: 100%;
  position: relative;
}
.headerLayout {
  background-color: white;
  height: 55px;
  margin-top: 0;
  z-index: 2;
}
/* .siderLayout{
  background: #fff;
  z-index: 0;
｝ */
.contentLayout {
  background: #fff;
  /* height:1000px; */
  height: 100%;
  z-index: 1;
}

@keyframes fadeOut {
  0% {
    opacity: 1;
  }
  99% {
    opacity: 0;
  }
  100% {
    opacity: 0;
    display: none;
  }
}

.login-bg {
  position: absolute;
  background: rgba(0, 0, 0, 0.5);
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 999;
  padding: 25vh 30vw;
}

.login-bg-hide {
  animation: fadeOut 1s 1s ease-in-out forwards;
}

.login {
  background: #e0e0e0;
  border-radius: 15px;
  padding: 20px 30px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.login-heading {
  font-size: 36px;
  text-align: left;
  font-weight: normal;
  font-family: Montserrat;
}

.login-form {
}

.input-form-item {
  display: flex;
}

.item-label {
  width: 120px;
  text-align: left;
  font-weight: bold;

  font-family: Montserrat;
}

.item-input {
  border: 2px solid rgb(210, 210, 210);
  border-radius: 6px;
  font-family: Montserrat;
  background: #e0e0e0;
  box-shadow: 3px 3px 6px #bebebe8b, -3px -3px 6px #ffffff75;
}

.item-input:focus-within {
  box-shadow: 3px 3px 6px #00bbff1b, -3px -3px 6px #4bd5ff19;
}

.button-form-item {
  display: flex;
  justify-content: flex-end;
}

.form-button {
  font-family: Montserrat;
  box-shadow: 3px 3px 6px #bebebe8b, -3px -3px 6px #ffffff75;
}
</style>


<template>
  <div>
    <a-drawer
      :title="$t('flood')"
      :width="600"
      :visible="visible"
      :bodyStyle="{ paddingBottom: '80px' }"
      :maskClosable="false"
      :mask="false"
      placement="right"
      :wrapStyle="{ paddingTop: '55px' }"
      @close="onClose"
    >
      <div>
        <span>Sealevel</span>
        <span style="margin-left: 20px;">Current: {{ (waterLevel - 9.0).toFixed(2) }} m</span>
        <a-slider :value="waterLevel" :min="-20.0" :max="300.0" :step="0.1" @change="handleWaterLevelChange" />
      </div>
      <div style="display: flex; justify-content: flex-end;">
        <a-button @click="handleClickPlayButton">{{ animationInterval == null ? '▷ Play' : '|| Stop' }}</a-button>
        <a-button @click="handleClickResetButton" style="margin-left:10px">Reset</a-button>
      </div>
      <!-- <div>
        <span>West - East</span>
        <a-slider
          :value="[extent[0], extent[2]]"
          :range="true"
          :min="103.0"
          :max="104.5"
          :step="0.001"
          @change="handleExtentChange('we', $event)"
        />
      </div>
      <div>
        <span>South - North</span>
        <a-slider
          :value="[extent[1], extent[3]]"
          :range="true"
          :min="1.1"
          :max="1.6"
          :step="0.001"
          @change="handleExtentChange('sn', $event)"
        />
      </div>
      <div>
        <span>Rotation</span>
        <a-slider :default-value="0" :min="0" :max="360" :step="1" @change="handleRotationChange" />
      </div>
      <div
        :style="{
          position: 'absolute',
          right: 0,
          bottom: '55px',
          width: '100%',
          borderTop: '1px solid #e9e9e9',
          padding: '10px 16px',
          background: '#fff',
          textAlign: 'right',
          zIndex: 1
        }"
      >
        <a-button :style="{ marginRight: '8px' }" @click="onClose"> {{ $t('cancel') }} </a-button>
      </div> -->
    </a-drawer>
  </div>
</template>
<script>
const DEFAULT_EXTENT = [103.825, 1.3, 104.02, 1.305]
const DEFAULT_WATER_LEVEL = 9.0

export default {
  props: {
    maxLevel: {
      type: Number,
      default: 300.0
    },
    minLevel: {
      type: Number,
      default: -20.0
    },
    right: 0
  },
  data() {
    return {
      viewer: null,
      visible: false,
      percent: 0,
      waterLevel: 9.0,
      animationInterval: null,
      extent: [103.825, 1.3, 104.02, 1.305],
      rotation: 17.0
    }
  },
  components: {},
  computed: {},
  mounted() {
    let self = this
    this.$bus.$on('viewer', function(val) {
      self.viewer = val
    })
  },
  watch: {
    animationInterval(data) {
      console.log('animate state changed')
      this.$emit('animate')
    }
  },
  methods: {
    showDrawer() {
      this.visible = true
    },
    onClose() {
      this.visible = false
      this.$emit('update', 0)
    },
    handleWaterLevelChange(data) {
      this.waterLevel = data
      this.$emit('waterLevelChange', data)
    },
    handleClickResetButton() {
      this.extent = DEFAULT_EXTENT.concat()
      this.waterLevel = DEFAULT_WATER_LEVEL

      this.$emit('waterLevelChange', this.waterLevel)
      this.$emit('extentChange', this.extent)
    },
    handleClickPlayButton() {
      if (this.animationInterval != null) {
        clearInterval(this.animationInterval)
        this.animationInterval = null
        return
      }

      const levelBefore = this.waterLevel
      const levelTarget = this.waterLevel + 10.0 > this.maxLevel ? this.maxLevel : this.waterLevel + 10.0
      const northBefore = this.extent[3]
      const northTarget = this.extent[3] + 0.015
      const animationElapse = 15000
      const startTime = Date.now()
      const self = this

      const render = () => {
        const t = Date.now()
        const currentLevel = ((t - startTime) / animationElapse) * (levelTarget - levelBefore) + levelBefore
        const currentNorth = ((t - startTime) / animationElapse) * (northTarget - northBefore) + northBefore
        self.waterLevel = currentLevel
        const newExtent = self.extent.concat()
        newExtent[3] = currentNorth
        self.extent = newExtent
        self.$emit('waterLevelChange', currentLevel)
        self.$emit('extentChange', newExtent)
        if (t - startTime > animationElapse) return
      }

      this.animationInterval = setInterval(render, 60)
    },
    handleExtentChange(direction, data) {
      if (direction === 'we') {
        const newExtent = this.extent.concat()
        newExtent[0] = data[0]
        newExtent[2] = data[1]
        this.extent = newExtent
        this.$emit('extentChange', newExtent)
      } else if (direction === 'sn') {
        const newExtent = this.extent.concat()
        newExtent[1] = data[0]
        newExtent[3] = data[1]
        this.extent = newExtent
        this.$emit('extentChange', newExtent)
      } else {
        throw new Error('invalid direction.')
      }
    },
    handleRotationChange(data) {
      this.$emit('rotationChange', data)
    }
  }
}
</script>


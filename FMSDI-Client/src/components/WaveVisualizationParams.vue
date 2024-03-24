<template>
  <div>
    <a-drawer
      :title="$t('wavedataset')"
      :width="600"
      :visible="visible"
      :bodyStyle="{ paddingBottom: '80px' }"
      :maskClosable="false"
      :mask="false"
      :placement="'right'"
      :wrapStyle="{ paddingTop: '55px' }"
      @close="onClose"
    >
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
      </div>
      <div>
        <p>Current Dataset Metadata:</p>
        <p>{{ datasetMetadata }}</p>
      </div>
      <div>
        <span>Exaggeration Multiplier:</span>
        <a-slider :default-value="1.0" :min="1.0" :max="10000.0" @change="handleExaggerationChange" />
      </div>
    </a-drawer>
  </div>
</template>
<script>
let Cesium = require('cesium/Cesium')
export default {
  props: {
    datasetMetadata: {
      type: String,
      default: 'None'
    }
  },
  data() {
    return {
      viewer: null,
      visible: false,
      percent: 0
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
  methods: {
    showDrawer() {
      this.visible = true
    },
    onClose() {
      this.visible = false
      this.$emit('update', 0)
    },
    handleExaggerationChange(data) {
      this.$emit('exaggerationChange', data)
    }
  }
}
</script>


<script setup>
import { ref, onBeforeUnmount } from 'vue'

// 页面模式：idle = 初始页面, monitor = 诊断中
const mode = ref('idle')

// 是否在轮询
const isRunning = ref(false)

// 最新一帧 OBD 数据
const liveData = ref({
  rpm: 0,
  speed: 0,
  coolantTemp: 0,
  throttle: 0,
  fuelPressure: 0,
  batteryVoltage: 0,
  hasActiveDtc: false,
  dtcList: []         
})
const dtcDescriptionMap = {
  P0117: '冷却水温センサー回路の電圧が低すぎます。',
  P0300: 'ランダム／複数気筒でミスファイアが検出されました。',
  P0420: '触媒システム効率が規定値以下です（触媒コンバータ劣化など）。'
  // 以后想到别的故障码，可以继续往这里加
}

const getDtcDescription = (code) => {
  return dtcDescriptionMap[code] || '説明：未登録のDTCコードです。'
}



const showDtcDetail = ref(false)

// 清除 DTC 按钮状态
const isClearing = ref(false)

// 定时器 ID
let timerId = null

// 点击「診断を開始」
const startMonitoring = () => {
  if (isRunning.value) return

  mode.value = 'monitor'
  isRunning.value = true

  // 立即请求一次
  fetchLiveData()

  // 之后每 1 秒请求一次
  timerId = setInterval(fetchLiveData, 1000)
}

// 请求后端 /api/obd/live
const fetchLiveData = async () => {
  try {
    // 如果以后在 vite.config.js 里配置了代理 '/api' -> 'http://localhost:8080'
    // 这里可以改成 fetch('/api/obd/live')
    const res = await fetch('http://localhost:8080/api/obd/live')
    if (!res.ok) {
      console.error('后端返回错误', res.status)
      return
    }
    const data = await res.json()
    liveData.value = data
  } catch (e) {
    console.error('获取 OBD 数据失败', e)
  }
}

// 点击「故障コードをクリア」
const clearDtc = async () => {
  if (!liveData.value.hasActiveDtc || isClearing.value) return

  isClearing.value = true
  try {
    await fetch('http://localhost:8080/api/obd/dtc/clear', {
      method: 'POST'
    })
    // 清除后，下一轮 /live 会返回 hasActiveDtc = false
  } catch (e) {
    console.error('清除 DTC 失败', e)
  } finally {
    isClearing.value = false
  }
}

// 组件销毁时停止轮询
onBeforeUnmount(() => {
  if (timerId) {
    clearInterval(timerId)
  }
})
</script>

<template>
  <div class="page">
    <div class="app-card">
      <!-- 标题 -->
      <div class="title">Web アプリ OBD 車両診断システム（シミュレーター）</div>

      <!-- 初始页面：开始诊断 -->
      <div v-if="mode === 'idle'" class="idle-area">
        <p class="desc">
          ECU からの OBD-Iデータを模擬し、車両状態をリアルタイムに表示します。
        </p>
        <p class="desc">
          Desingned by Luan ZhengYuan
        </p>
        <button class="primary-btn" @click="startMonitoring">
          診断を開始
        </button>
      </div>

      <!-- 监控页面 -->
      <div v-else class="monitor-area">
        <!-- 状态栏 -->
        <div class="status-bar">
          <div>
            OBD 状態：
            <span v-if="liveData.hasActiveDtc" class="badge danger">
              🚨 Fault detected
            </span>
            <span v-else class="badge ok">
              ✅ Connected / Normal
            </span>
          </div>
          <div class="status-sub">
            更新間隔: 1s（自動更新中）
          </div>
        </div>

        <!-- “仪表盘”（先用卡片 + 数字，之后可以换成真仪表盘） -->
        <div class="cards">
          <div class="card">
            <h3>エンジン回転数</h3>
            <p class="value">{{ liveData.rpm }} rpm</p>
          </div>
          <div class="card">
            <h3>車速</h3>
            <p class="value">{{ liveData.speed }} km/h</p>
          </div>
          <div class="card">
            <h3>冷却水温</h3>
            <p class="value">{{ liveData.coolantTemp }} ℃</p>
          </div>
          <div class="card">
            <h3>スロットル開度</h3>
            <p class="value">{{ liveData.throttle }} %</p>
          </div>
          <div class="card">
            <h3>燃料圧力</h3>
            <p class="value">{{ liveData.fuelPressure }} kPa</p>
          </div>
          <div class="card">
            <h3>バッテリー電圧</h3>
            <p class="value">
              {{ liveData.batteryVoltage.toFixed ? liveData.batteryVoltage.toFixed(2) : liveData.batteryVoltage }} V
            </p>
          </div>
        </div>

       
        <!-- 故障提示 + 清除 + 詳細ボタン -->
<div class="dtc-row">
  <div>
    <div v-if="liveData.hasActiveDtc" class="dtc-warning">
      現在、ECU から故障フラグが報告されています。
      <span class="dtc-count">
        （{{ liveData.dtcList.length }} 件のDTC）
      </span>
    </div>
    <div v-else class="dtc-normal">
      アクティブな故障コードはありません。
    </div>
  </div>

  <div class="dtc-actions">
    <button
      class="secondary-btn"
      :disabled="!liveData.hasActiveDtc || isClearing"
      @click="clearDtc"
    >
      {{ isClearing ? 'クリア中...' : '故障コードをクリア' }}
    </button>

    <button
      class="link-btn"
      :disabled="!liveData.dtcList.length"
      @click="showDtcDetail = true"
    >
      詳細を見る
    </button>
  </div>
</div>
<!-- DTC 詳細モーダル（集中展示用） -->
<div v-if="showDtcDetail" class="dtc-modal-backdrop" @click.self="showDtcDetail = false">
  <div class="dtc-modal">
    <div class="dtc-modal-header">
      <h2>DTC 詳細</h2>
      <button class="close-btn" @click="showDtcDetail = false">×</button>
    </div>

    <div class="dtc-modal-body">
      <ul v-if="liveData.dtcList.length">
        <li v-for="code in liveData.dtcList" :key="code" class="dtc-item">
  <div class="dtc-code">{{ code }}</div>
  <div class="dtc-desc">
    {{ getDtcDescription(code) }}
  </div>
</li>

      </ul>
      <p v-else>
        現在アクティブな故障コードはありません。
      </p>
    </div>
  </div>
</div>   
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 保留你原来的渐变背景风格 */
.page {
  min-height: 100vh;
  background: linear-gradient(to right, purple, blue);
  display: flex;
  align-items: center;      /* ⬅ 垂直居中 */
  justify-content: center;  /* 水平居中 */
}


.app-card {
  width: 96%;
  max-width: 960px;
  margin-top: 40px;
  background-color: aliceblue;
  border-radius: 8px;
  padding: 24px 28px 32px;
  box-sizing: border-box;
}




.title {
  font-size: 24px;
  font-weight: 700;
  text-align: center;
}

.idle-area {
  text-align: center;
  margin-top: 40px;
}

.desc {
  margin-bottom: 16px;
  color: #4b5563;
}

.primary-btn,
.secondary-btn {
  padding: 10px 20px;
  border-radius: 999px;
  border: none;
  cursor: pointer;
  font-size: 14px;
}

.primary-btn {
  background: linear-gradient(to right, purple, blue);
  color: #fff;
}

.secondary-btn {
  background: #e5e7eb;
}

.secondary-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.monitor-area {
  margin-top: 24px;
}

.status-bar {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 16px;
}

.badge {
  padding: 4px 12px;
  border-radius: 999px;
  font-size: 13px;
}

.badge.ok {
  background: #d1fae5;
  color: #065f46;
}

.badge.danger {
  background: #fee2e2;
  color: #b91c1c;
}

.status-sub {
  font-size: 12px;
  color: #6b7280;
}

.cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 12px;
  margin-bottom: 20px;
}
.dtc-list {
  margin-top: 20px;
  background: #ffffff;
  padding: 16px;
  border-radius: 8px;
  box-shadow: rgba(149, 157, 165, 0.15) 0px 2px 6px;
}

.dtc-list ul {
  margin-left: 18px;
}

.dtc-list li {
  margin-bottom: 6px;
  font-size: 15px;
}


.card {
  background: #f9fafb;
  border-radius: 12px;
  padding: 12px;
  box-shadow: rgba(149, 157, 165, 0.2) 0px 4px 10px;
}

.card h3 {
  font-size: 14px;
  margin-bottom: 6px;
}

.value {
  font-size: 18px;
  font-weight: 600;
}

.dtc-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.dtc-warning {
  color: #b91c1c;
}

.dtc-normal {
  color: #065f46;
}
/* ---- DTC 詳細モーダル ---- */
.dtc-modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 50;
}

.dtc-modal {
  width: min(960px, 100% - 32px); /* 和 app-card 同一宽度策略 */
  max-height: 80vh;
  background: #f9fafb;
  border-radius: 12px;
  box-shadow: 0 20px 40px rgba(15, 23, 42, 0.3);
  display: flex;
  flex-direction: column;
}



.dtc-modal-header {
  padding: 12px;
  background: #f3f4f6;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.close-btn {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
}

.dtc-modal-body {
  padding: 12px;
  overflow-y: auto;
}

.dtc-item {
  margin-bottom: 12px;
}

.dtc-code {
  font-weight: bold;
  font-size: 16px;
}

.dtc-desc {
  font-size: 13px;
  color: #4b5563;
}

.link-btn {
  background: none;
  border: none;
  color: #2563eb;
  cursor: pointer;
  font-size: 14px;
}

</style>

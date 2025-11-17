# Web アプリ OBD 車両診断システム（シミュレーター）

本プロジェクトは **Vue 3 + Spring Boot** を用いて構築した  
**Web ベースの車両診断（OBD）シミュレーター** です。

ECU → OBD のデータ取得を模擬し、車両の各種センサ値および  
故障コード（DTC：Diagnostic Trouble Code）をブラウザ上でリアルタイムに可視化します。

ソフトウェアエンジニア職のポートフォリオとして作成しました。

---

## <img width="1514" height="895" alt="front-1" src="https://github.com/user-attachments/assets/a6875d9c-b1c5-4941-a2d0-6367e7dfb2be" />
##<img width="1514" height="895" alt="front-2" src="https://github.com/user-attachments/assets/8516a4b4-6ca7-48f7-a67b-804acf1d8fc2" />

##<img width="1514" height="895" alt="front-3" src="https://github.com/user-attachments/assets/cdd47a5e-9cac-413a-bc31-7266170a6ccf" />

（ここに UI のスクリーンショットを挿入してください  
例：リアルタイムメータ画面、DTC 詳細モーダルなど）

---

## ✨ 主な機能

### 🔧 リアルタイム車両データ監視
- ECU からの OBD データを 1 秒間隔で模擬取得
- 表示内容：
  - エンジン回転数（RPM）
  - 車速
  - 冷却水温
  - スロットル開度
  - 燃料圧力
  - バッテリー電圧

### 🚨 DTC（故障コード）検出シミュレーション
- センサ異常値に応じて DTC を自動生成  
  （例：P0117 / P0300 / P0420 など）
- DTC 詳細の表示（説明文あり）
- 「故障コードクリア」ボタンによりリセット可能

### 💡 フロントエンド
- Vue 3（Composition API）
- Vite
- レスポンシブデザイン
- モーダル UI コンポーネント

### 🛠 バックエンド
- Spring Boot 3
- Java 17
- RESTful API 設計
- OBD データ生成ロジック / DTC 判定ルール

---

## 📁 プロジェクト構成


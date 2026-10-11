# G2 Player — APK Standalone

Projeto Android nativo (WebView em modo quiosque) que abre direto na tela do player, se comunicando com o painel G2 Digital Signage.

## ✅ Pré-requisitos

- **Android Studio** (Hedgehog ou superior) — baixe em https://developer.android.com/studio
- **JDK 17** (já vem embutido no Android Studio)

## 🔧 Como gerar o APK

### Opção A — Pelo Android Studio (mais fácil)

1. Abra o Android Studio
2. Vá em **File → Open** e selecione a pasta `player-apk/`
3. Aguarde o Gradle sincronizar (baixa dependências na 1ª vez)
4. Vá em **Build → Build Bundle(s) / APK(s) → Build APK(s)**
5. O APK será gerado em `app/build/outputs/apk/debug/app-debug.apk`
6. Clique em **"locate"** para abrir a pasta do APK

### Opção B — Pela linha de comando

```bash
cd player-apk
./gradlew assembleDebug
# APK gerado em: app/build/outputs/apk/debug/app-debug.apk
```

Para um APK de release (assinado):

```bash
./gradlew assembleRelease
# APK gerado em: app/build/outputs/apk/release/app-release-unsigned.apk
# (precisa assinar — veja https://developer.android.com/build/building-cmdline#sign_cmdline)
```

## 📲 Como instalar

Copie o APK para o dispositivo e instale:

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

Ou transfira o arquivo `.apk` por USB/Download e toque nele no dispositivo.

> Na 1ª instalação, autorize **"Instalar apps de fontes desconhecidas"** nas configurações do Android.

## ⚙️ Personalização

### URL do player

Edite `app/src/main/res/values/strings.xml`:

```xml
<string name="player_url">https://g2-screen-flow.base44.app/player</string>
```

### Nome do app

Edite `app/src/main/res/values/strings.xml`:

```xml
<string name="app_name">G2 Player</string>
```

### Orientação da tela

Edite `app/src/main/AndroidManifest.xml` na tag `<activity>`:

```xml
android:screenOrientation="landscape"  <!-- ou portrait, fullSensor, etc -->
```

### Ícone do app

Substitua `app/src/main/res/drawable/ic_launcher.xml` pelo seu ícone (vector drawable).

## 🎯 Recursos

- ✅ Abre direto na tela do player (sem login, sem menu)
- ✅ Modo quiosque (tela cheia, sem barra de navegação)
- ✅ Tela sempre acesa (wake lock)
- ✅ Auto-início ao ligar o dispositivo (boot)
- ✅ Reconexão automática quando cai a internet
- ✅ Cache local (localStorage persiste entre reinícios)
- ✅ Vídeo com autoplay (sem necessidade de toque)
- ✅ Sem dependências externas (APK leve, ~2 MB)
- ✅ Funciona em TV, tablet, celular e TV Box

## 📋 Especificações

- **Package:** `com.g2signage.player`
- **Min SDK:** 21 (Android 5.0)
- **Target SDK:** 34 (Android 14)
- **Tamanho do APK:** ~2 MB
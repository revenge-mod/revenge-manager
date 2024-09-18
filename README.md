<div align="center">

  <img src="images/logo.webp" alt="Bunny logo" width="200px" style="border-radius: 50%" />
  
  # Revenge Manager

  Easily install Revenge on Android

  [![GitHub Release](https://img.shields.io/github/v/release/revenge-mod/revenge-manager?display_name=release&style=for-the-badge&label=Latest&color=3AB8BA)](https://github.com/revenge-mod/revenge-manager/releases/latest)

  ---

  <br>

  ![Debug build status](https://img.shields.io/github/actions/workflow/status/revenge-mod/revenge-manager/build-debug.yml?label=Debug%20Build&logo=github&style=for-the-badge&branch=main)
  [![Stars](https://img.shields.io/github/stars/revenge-mod/revenge-manager?logo=github&style=for-the-badge)](https://github.com/revenge-mod/revenge-manager/stargazers)
  [![Discord](https://img.shields.io/discord/1196075698301968455?logo=discord&logoColor=white&style=for-the-badge)](https://discord.gg/XjYgWXHb9Q)
  
  <br>
  
  ![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/revenge-mod/revenge-manager?logo=github&logoColor=%23fff&style=for-the-badge)
  ![Downloads (latest)](https://img.shields.io/github/downloads/revenge-mod/revenge-manager/latest/total?style=for-the-badge&logo=github&label=Downloads%20(Latest)&color=blue)
  ![Total downloads](https://img.shields.io/github/downloads/revenge-mod/revenge-manager/total?style=for-the-badge&logo=github&label=Downloads%20(Total)&color=blue)
  ![GitHub top language](https://img.shields.io/github/languages/top/revenge-mod/revenge-manager?style=for-the-badge)

  <br>

  <img src="images/screenshot_home.jpg" width="200px">
  
</div>

Build
---

#### Prerequisites
  - [Git](https://git-scm.com/downloads)
  - [JDK 17](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html)
  - [Android SDK](https://developer.android.com/studio)

#### Instructions

1. Clone the repo
    - `git clone https://github.com/revenge-mod/revenge-manager.git && cd revenge-mod`
2. Build the project
    - Linux: `chmod +x ./gradlew && gradlew assembleDebug`
    - Windows: `./gradlew assembleDebug`
3. Install on device
    - [Enable USB debugging](https://developer.android.com/studio/debug/dev-options) and plug in your phone
    - Run `adb install app/build/outputs/apk/debug/app-debug.apk`

## Contributing

This is an open-source project, you can do so without any programming.

Here are a few things you can do:

- [Test and report issues](https://github.com/revenge-mod/revenge-manager/issues/new/choose)
<!-- - [Translate the app into your language](https://crowdin.com/project/vendetta-manager) -->
    
License
---
Revenge Manager is licensed under the Open Software License version 3.0

[![License: OSL v3](https://img.shields.io/badge/License-OSL%20v3-blue.svg?style=for-the-badge)](https://github.com/revenge-mod/revenge-manager/blob/main/LICENSE)

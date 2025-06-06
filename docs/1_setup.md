# ⚙️ Setup Guide

Follow these steps to set up Revenge Manager — the installer/updater for the modded Discord Android client known as Revenge.

## 📥 1. Clone the Repository

Make sure Git is installed, then run:

```bash
git clone https://github.com/revenge-mod/revenge-manager.git
cd revenge-manager
```

## 🛠️ 2. Open in Your IDE

We recommend **IntelliJ IDEA**:

* Open the `revenge-manager` folder.
* If prompted, auto-import the Gradle project.
* Install the **Android** and **Android Design Tools** plugins from the plugin marketplace.
* Configure the Android SDK:

  1. Click the **Gear Icon** in the top-right corner of IntelliJ.
  2. Select **Android SDK Manager**.
  3. Click **Edit Android SDK Location** and follow the prompts.
  4. In the **SDK Platforms** tab, enable:

     * **Android 14 (API Level 34)**
     * **Android 13 (API Level 33)**
  5. Click **Apply** and let the SDK components install.

## 🧱 3. Build the Project

To build the project:

* Click the **Elephant icon** on the right toolbar in IntelliJ.
* Navigate to `Tasks > build` and double-click `build`.

This will compile the app and output the APKs to:

```
app/build/outputs/apk/
```

You'll find both **release** and **debug** builds available.

## 📦 4. Installing Revenge

Once you have the APK:

* Install it via file manager or using ADB:

  ```bash
  adb install app/build/outputs/apk/debug/revenge-manager-<REVENGE-VERSION>.apk
  ```
* Open the **Revenge Manager** app.
* Tap **Install** to install a patched Discord client with Revenge.

## 🔁 Updating

* Tap **Update** inside the app.

## ❌ Uninstalling

To remove Revenge:

* Uninstall the patched Discord client from your device.
* Optionally, clear related cache or leftover files manually.

---

Next: [🎓 Usage Guide](2_usage.md)

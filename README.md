# Zombie Night: Rescue — prototype 0.1.0

Android pixel-art action prototype based on the supplied protagonist reference.

## Included
- Title screen and first apartment scene.
- Transition to city survival scene.
- Pixel-art protagonist inspired by the supplied reference (hair, glasses, beard, open shirt, tattoo, blue jeans).
- Touch controls.
- Puños, bate, pistola and botella as prototype weapons.
- Three zombie types and basic damage/health/score.
- Collectible weapons and simple enemy spawning.

## Automatic APK build
This repository includes a GitHub Actions workflow at `.github/workflows/android-apk.yml`.

1. Create a GitHub repository.
2. Upload the contents of this folder to the repository.
3. Open the **Actions** tab and run **Build Zombie Night Rescue APK** (or push to `main`/`master`).
4. When it finishes, open the workflow run and download the artifact named **ZombieNightRescue-debug-apk**.
5. Extract the artifact and install `app-debug.apk` on your Android phone. You may need to allow installation from the browser/file manager when Android asks.

The workflow uses JDK 17 and Gradle 9.5 and builds `:app:assembleDebug`.

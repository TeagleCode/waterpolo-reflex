# Reflex

Reaction training for water polo goalies. A ball appears at a random spot on a blue
field, one at a time, on an interval you set. React to it. The app logs how fast you were.

Runs on **Android**, **Windows**, and in any browser. Fully offline — no accounts,
no network, no permissions.

---

## Download

Grab the latest build from the [**Releases**](../../releases/latest) page.

| Platform | File | How |
|---|---|---|
| Android | `Reflex-x.y.z.apk` | Open it on your phone, allow installing from your browser when Android asks |
| Windows | `Reflex-Setup-x.y.z.exe` | Normal installer, adds a Start menu shortcut |
| Windows | `Reflex-Portable-x.y.z.exe` | Runs straight from the file, installs nothing |
| iPhone / anything else | — | Open the web version, then *Share → Add to Home Screen* |

Android will warn about installing outside the Play Store — that's expected for a
sideloaded APK, tap through it.

---

## The drill

A ball appears, you react, it disappears. Tap or click it to log a reaction time;
miss it and it vanishes on its own and counts against you.

**Settings**

| Setting | What it does |
|---|---|
| Min / max interval | Wait between balls, to the millisecond. Set them equal for a fixed rhythm |
| Ball stays for | Your window to react before it counts as a miss |
| Spawn area | **Anywhere**, or **Zones** — the 6 shooting spots in a 3×2 grid |
| Cue | **Ball**, **Ball + beep**, or **Beep only** (ball hidden, tap anywhere) |
| Session ends | **Open**, after N **reps**, or after N minutes |
| Size / color | Ball diameter and colour, six presets plus a custom picker |

**Stats.** Every session records reps, hits, average and best reaction time, and your
average **split by side** — the left/right gap is the thing most goalies can't feel on
their own. History keeps your last 60 sessions with a chart of average reaction time
over time.

Settings and history live on the device. They do not sync between your phone and PC.

---

## Repo layout

```
www/        the app itself — one HTML file, no build step, no dependencies
android/    a fullscreen WebView wrapper (plain Java, no frameworks)
desktop/    an Electron shell for Windows
.github/    CI that builds the APK + EXE and attaches them to a release
```

`www/` is the single source of truth. Both wrappers copy it in at build time, so a
change to the web app ships to every platform.

## Working on it

Edit `www/index.html` and open it in a browser — that's the whole loop. Both wrappers
copy `www/` in at build time, so one change ships everywhere.

### Building locally

```bash
# Android APK  (needs the Android SDK and a JDK 17 — not 21+, the Android plugin rejects it)
cd android
JAVA_HOME=/path/to/jdk-17 /path/to/gradle assembleRelease
# -> android/app/build/outputs/apk/release/

# Windows installer + portable exe, buildable from Linux
cd desktop && cp -r ../www www && npm install && npx electron-builder --win
# -> desktop/dist/
```

The Windows build sets `signAndEditExecutable: false`, which skips `rcedit` — that tool
needs wine, and skipping it is what lets the exe build on Linux at all. The cost is that
the icon isn't stamped into the binary's resources, so the app sets its window icon at
runtime instead and NSIS puts the right icon on the installer and shortcut.

### Publishing a release

```bash
gh release create v1.0.1 --generate-notes \
  android/app/build/outputs/apk/release/Reflex-1.0.1.apk \
  desktop/dist/Reflex-Setup-1.0.1.exe \
  desktop/dist/Reflex-Portable-1.0.1.exe
```

`ci/` holds workflows that would do all of this automatically on `git tag`. They are
parked rather than active — see [ci/README.md](ci/README.md) for the one command that
turns them on.

## Notes on accuracy

- The ball appears instantly. No fade-in — that would smear the onset and corrupt
  the measurement.
- Reaction time is measured from the animation frame the cue lands in to your tap.
  Accuracy is bounded by your screen's refresh rate (~8 ms at 120 Hz, ~16 ms at
  60 Hz), so compare your times to your own, not to a teammate on different hardware.
  Beep-only adds a constant audio-output latency on top.
- In **Anywhere**, the next ball never spawns within 28% of the screen's short side
  of the last one. In **Zones**, it never repeats a zone twice in a row.
- The desktop build disables Chromium's background timer throttling, so the
  intervals hold even if the window loses focus.

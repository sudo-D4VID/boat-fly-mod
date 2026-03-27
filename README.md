# Boat Fly Mod 🚤✈️

A lightweight Minecraft **Fabric mod** that lets boats fly through the air.

[![Build](https://github.com/sudo-D4VID/boat-fly-mod/actions/workflows/build.yml/badge.svg)](https://github.com/sudo-D4VID/boat-fly-mod/actions/workflows/build.yml)

## Features
- 🪶 **Ultra-lightweight** – two mixins, zero Fabric API dependency
- ✈️ **Fly boats** – hold Space to ascend, Shift to descend, release to hover
- 🔄 **Toggle on/off** – press Ctrl+Alt+Shift+1 to enable or disable flying at any time
- 🛡️ **No fall damage** while flying
- 🌐 **Multiplayer compatible** – client-side only, no server mod needed
- 🎨 **Compatible with** OptiFine, Sodium, and vanilla Minecraft 1.20+

## Controls
| Key | Action |
|-----|--------|
| Space (Jump) | Ascend |
| Shift (Sneak) | Descend |
| Neither | Hover / slow descent |
| **Ctrl + Alt + Shift + 1** | Toggle flying on/off |

## Installation
1. Install [Fabric Loader](https://fabricmc.net/use/installer/) for Minecraft **1.20.1**
2. Download the latest `boatfly-*.jar` from the [Releases](https://github.com/sudo-D4VID/boat-fly-mod/releases) page
3. Drop the JAR into your `.minecraft/mods/` folder
4. Launch Minecraft using the Fabric profile – done!

> **No other mods required.** Fabric API is NOT needed.

## Compatibility
| Mod | Compatible |
|-----|-----------|
| Vanilla Minecraft 1.20+ | ✅ Yes |
| OptiFine | ✅ Yes (client rendering only, no conflict) |
| Sodium | ✅ Yes (client rendering only, no conflict) |
| Multiplayer servers | ✅ Yes (client-side, no server install) |

## Building from source
Requires Java 17 and an internet connection (to download Minecraft mappings).

```bash
./gradlew build
```

The compiled JAR will be in `build/libs/boatfly-1.0.0.jar`.

## How it works
The mod uses two **Fabric Mixins**:

1. **`BoatEntityMixin`** – injected into `BoatEntity#tick()`.  
   On each tick, if the local player is riding a boat and flying is enabled:
   - **Jump key pressed** → add upward velocity (max 0.6 b/t)
   - **Sneak key pressed** → add downward velocity (max −0.6 b/t)
   - **Airborne, no key** → apply gentle hover friction instead of free-fall gravity

2. **`KeyboardMixin`** – intercepts raw GLFW key events.  
   Detects the **Ctrl+Alt+Shift+1** combination and toggles flying on/off at runtime.

Fall distance is reset when airborne so there is no fall damage on landing.

## License
MIT – see [LICENSE](LICENSE).


# Advanced Slime Paper — Minecraft 26.2 (interaction fix fork)

Fork of [InfernalSuite/AdvancedSlimePaper](https://github.com/InfernalSuite/AdvancedSlimePaper) on the
`ver/26.2` line, carrying a fix for [PaperMC/Paper#13707](https://github.com/PaperMC/Paper/issues/13707).

> This repository hosts two branches:
> * `26.3` — AdvancedSlimePaper `26.3.build.191-stable`
> * `26.2` — AdvancedSlimePaper `26.2.build.135-stable` (this branch)

| | |
|---|---|
| Minecraft | **26.2** |
| AdvancedSlimePaper | `26.2.build.135-stable` |
| Paper upstream ref | `e4d6dfd1597968e78a3d405dce73d1d1f14a4b81` |
| ASP API | `4.2.0-SNAPSHOT` |
| Java | **25** |

Everything except the single patch described below is upstream code — no other changes.

## Fixed bug — Paper #13707

A plugin could not deny *block* interaction while still allowing the held item to be used (e.g. placing a block).

```java
event.setUseItemInHand(Event.Result.ALLOW);
event.setUseInteractedBlock(Event.Result.DENY);
```

**Before:** the server returned early from the `useInteractedBlock() == DENY` branch and never reached
`ItemStack#useOn`, so no block could be placed.

**Cause:** `ServerPlayerGameMode#useItemOn` treated both results as a single decision:

```java
return (event.useItemInHand() != Event.Result.ALLOW) ? InteractionResult.SUCCESS : InteractionResult.PASS;
```

**After:** block interaction is denied, but the item path still runs:

- `state.useItemOn(...)` / `state.useWithoutItem(...)` are skipped when the block interaction is denied;
- when `useItemInHand()` is `ALLOW`, execution falls through to the normal item use (`itemStack.useOn`),
  so block placement works again;
- spectators with a denied block interaction still receive `PASS`.

The whole change is a single patch:
[`aspaper-server/minecraft-patches/sources/net/minecraft/server/level/ServerPlayerGameMode.java.patch`](aspaper-server/minecraft-patches/sources/net/minecraft/server/level/ServerPlayerGameMode.java.patch)

## Building

Requirements: **JDK 25** (Minecraft 26.x toolchain) and Git.

```bash
./gradlew applyAllPatches
./gradlew build
./gradlew createMojmapPaperclipJar
```

The runnable server jar is written to:

```
aspaper-server/build/libs/aspaper-paperclip-26.2.build.135-stable.jar
```

> **Windows note:** paperweight's multi-patch `git am --3way` can report spurious merge conflicts on Windows.
> Build on Linux/WSL, or apply the upstream patch series patch-by-patch. Long resource paths also need
> `git config core.longpaths true`.

## Upstream project

Advanced Slime Paper is a fork of Paper implementing the Slime Region Format developed by Hypixel.
Originally a plugin, the project has been converted to a server fork to allow deeper fixes and performance
work. Its goal is to let server administrators load worlds faster and save disk space.

- Website / downloads: <https://infernalsuite.com/download/asp>
- Documentation: <https://infernalsuite.com/docs/asp>
- Javadocs: <https://docs.infernalsuite.com/>
- Discord: <https://discord.gg/YevvsMa>

## License

GPL-3.0 — see [LICENSE](LICENSE). This fork keeps the original copyright notices and history.

## Credits

Thanks to:

* All the contributors that actively maintain ASWM and added features to SWM.
* [Paul19988](https://github.com/Paul19988) — ASWM Creator.
* [ComputerNerd100](https://github.com/ComputerNerd100) — Large Contributor & Maintainer.
* [b0ykoe](https://github.com/b0ykoe) — Provider of build services & repositories.
* [Owen1212055](https://github.com/Owen1212055) — Large Contributor & Maintainer.
* [Gerolmed](https://github.com/Gerolmed) — Contributor & Maintainer.
* [Grinderwolf](https://github.com/Grinderwolf) — The original creator.
* [Glare](https://glaremasters.me) — Providing the original Maven repository.
* [Minikloon](https://twitter.com/Minikloon) and all the [Hypixel](https://twitter.com/HypixelNetwork) team for developing the SRF.

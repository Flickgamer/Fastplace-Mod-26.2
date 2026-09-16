## What it does

Vanilla Minecraft puts a 4-tick (1/5th second) cooldown between right-click
placements. FastPlace shortens or removes that cooldown, so holding right-click
places continuously.

## Settings

Toggle key: **J** (changeable in Options > Controls). Commands: `/fastplace` or `/fp`.

| Command | Effect |
| --- | --- |
| `/fp` | show current settings |
| `/fp toggle` / `on` / `off` | turn the module on or off |
| `/fp delay <0-4>` | block cooldown in ticks. 0 = fastest, 4 = vanilla |
| `/fp blocksonly` | only speed up blocks (default **on**) |
| `/fp projectiles` | toggle separate projectile handling |
| `/fp projectiledelay <0-4>` | cooldown for snowballs/eggs/pearls |
| `/fp chat` | toggle chat messages |

`blocksonly` is what keeps eating and item use feeling normal: with it on,
FastPlace only touches the cooldown while you're holding a placeable block.
Food, potions, bows and buckets are left entirely alone.

Settings are saved to `.minecraft/config/fastplace.json` and persist across
restarts, including the on/off state.

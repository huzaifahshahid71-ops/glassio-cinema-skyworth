# Assets

The `cinema` asset directory contains the project's wallpaper collection, alpha masks, custom ARY tile, condensed clock font and seed preferences.

- Fourteen distinct landscape photographs came from the supplied wallpaper ZIP (one duplicate was removed). Their independent redistribution rights have not been established; retain this project privately unless rights are confirmed.
- The cinematic mountain and ARY tile were generated during the project. The live time/date are rendered by the app, not baked into the wallpaper.
- Per-scene masks are RGBA PNG files under `cinema/masks/`; white RGB plus alpha determines which clock pixels are occluded. `depth-profiles.json` maps each photo to its mask and clock layout.
- Mask and photo must use the same 16:9 composition. Align their edges and inspect several times, including individual digits, before adding a new scene to automatic rotation.
- Roboto Condensed is distributed under its bundled OFL. The original Apple root CA file is a public certificate, not a signing key.

No private signing keystore, account token, ADB connection file or device log is included.

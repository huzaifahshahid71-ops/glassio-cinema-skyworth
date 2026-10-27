# Glassio Cinema v3 — depth and dock polish

Installed build: 1.9.8-cinema.3 (132), com.glasslauncher.cinema.

The previous coarse polygon masks could erase sky beside a ridge and leave clock pixels over the subject. They have been replaced by 15 per-photo alpha masks fitted to actual edges, with a small edge feather. The clock is composited on a separate layer and erased by that mask; the background photograph is never repainted. The mask follows the displayed image during transitions and waits for the matching asset to load. Window reflections in the cube-building scene are treated as building, not sky.

Date baseline has a minimum 10.5% screen-height inset. Actual clock glyph bounds must stay at least another 5.5% screen height below that baseline. Clock position and size vary by composition to preserve readable digits. Time stays HH:mm and the four-hour persistent shuffle remains enabled.

Selected dock tiles keep the same center and grow symmetrically. The upward lift was removed; top and bottom padding are equal. The obsolete lift toggle was removed. TV screenshot measurements: selected tile height 184 pixels, unselected 170 pixels; vertical center 922.5 pixels in both (0-pixel shift).

Validation: release compilation and release lint passed. All 1,440 minute values were evaluated for every profile on desktop (21,600 layouts), including individual digit visibility; worst digit occlusion was about 42%. All 15 scenes captured on the real TV; final-adjusted scenes recaptured after the last update. Desktop metrics approximate native font rendering. No cold boot/reboot regression was performed.

Bloat verification: six packages disabled: com.smartdevice.epopplayer, com.coocaa.demonow.lightining, com.smartdevice.recommendation, com.google.android.feedback, com.sky.market, com.security.SwaiotTV. No process matching those six was present at the audit. Nine checklist-selected apps remain absent. Twelve hidden apps were only hidden from the dock and are not claimed as disabled. The full cleanyourtv preset was NOT applied; its Android 14 firmware assumptions do not match this Android 11 TV. Manufacturer Input/casting/recovery services are preserved.

Stock Google TV Home remains enabled. Disabling it in the prior trial caused firmware RecoveryActivity to become Home; it was restored immediately. Custom HomeKeyService handles Home, but eliminating every boot flash remains unresolved. Recovery script: Restore-Google-TV-Home.ps1.

Artifacts: Glassio-Cinema-1.9.8-custom-v3.apk, Glassio-Cinema-source-v3.zip, TV-Depth-v3-contact.jpg, tv-depth-v3/ individual device captures, Disabled-Packages-Verified.txt, Deleted-Apps-Verified.txt, Centered-Focus-Verification.json.

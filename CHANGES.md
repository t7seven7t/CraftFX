CHANGES
=======

Version 0.0.3
-------------
- Changed some access modifiers
- Changed how effects are constructed. Effects now should override initialize method rather than use a constructor, now have access to their own config
- New effect types: MODIFY_HEALTH, MESSAGE, LIGHTNING, BURN, BUKKIT_EFFECT, SOUND, DISARM, MODIFY_WALK_SPEED
- Added some static path variables so I don't trip up and have differing paths
- Effects can now be area effect casting

Version 0.0.2
-------------
- Moved classes around for better package structure (new stuff is permanent now)
- Added custom effect class loader so you can write your own effects easily :)
- Updated silly old config items

Version 0.0.1
-------------
- Initial release

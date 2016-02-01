# CraftFX
CraftFX is a [Bukkit](https://bukkit.org/) plugin that allows extensive customisation of items from recipes to performing effects. 

Some examples of what CraftFX intends to be:
  - *Run faster while holding an amulet of speed on your hotbar*
  - *Apply on-hit effects to cripple your foes whenever you hit them with your brutal waraxe*
  - *Create an extensive crafting system for your server so that your newly diverse economy can thrive*
  - *Grant area of effect regeneration to your allies while you are wearing your Healing Chestplate*

All of this and more just through config files -- no programming knowledge required! Of course, CraftFX aims to eventually allow custom effects that can be written in either Java or JavaScript too. If we're lucky Mojang may add additional overrides for Resource Packs so that CraftFX can help you change the models of **any** item without mods!

**NOTICE:** CraftFX requires that your server runs **Java 8 or it will not function**. Issues and pull requests regarding Java compatibility will be ignored. You can learn how to install Java 8 for your operating system on [Oracle's website](https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html). Alternatively you can use [OpenJDK](http://openjdk.java.net/install/). You can even install it in another directory so that you have no down time - no excuses :)

## Usage
Like any plugin, add CraftFX's jar to your plugins folder and it will run. After the first startup CraftFX will generate a config and items folder in its plugin directory where items can be configured.

More documentation will follow soon™.

## Documentation
When the wiki updates...

## Downloading
CraftFX has not been uploaded anywhere yet. You can try compiling it yourself or wait until it comes to Spigot Resources or DBO.

## Compiling
Use Gradle to compile CraftFX

Before compiling, create a new directory **/libs** and place inside it the version of CraftBukkit/Spigot that you intend to use CraftFX with. Ensure that the name of the jar ends with the internal version string, ie. a valid example would be **spigot-v1_8_R3.jar**.

If no matching spigot versions are found for a NMSAdapter, then the buildscript will not include that adapter into the compiled CraftFX jar. This is still ok if you do not intend on using that version of CB/Spigot, are using MC versions before 1.7, or don't care about some features (there is a default FallbackNMSAdapter).

If you are on Linux or Mac OS X, run the following in your terminal:

    ./gradlew buildAndCleanAll

If you are on Windows, run the following in your command prompt:

    gradlew buildAndCleanAll

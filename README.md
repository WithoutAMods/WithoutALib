# [WithoutALib](https://www.curseforge.com/minecraft/mc-mods/withoutalib "WithoutALib on CurseForge") - Minecraft Forge Mod

This is a libary for all mods by WithoutAName.

## Maven
    repositories {
        maven { // WithoutALib
            url "https://mineplay.link/maven/"
        }

    dependencies {
        compile fg.deobf("withoutaname.mods.withoutalib:WithoutALib:${withoutalib_version}")
    }

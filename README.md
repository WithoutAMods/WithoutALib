# [WithoutALib](https://www.curseforge.com/minecraft/mc-mods/withoutalib "WithoutALib on CurseForge") - Minecraft Forge Mod
![](https://img.shields.io/maven-metadata/v?label=1.16%20latest%20version&metadataUrl=https%3A%2F%2Frepo.withoutaname.eu%2Freleases%2Fwithoutaname%2Fmods%2Fwithoutalib%2FWithoutALib%2Fmaven-metadata.xml&versionPrefix=1.16)
![](https://img.shields.io/maven-metadata/v?label=1.17%20latest%20version&metadataUrl=https%3A%2F%2Frepo.withoutaname.eu%2Freleases%2Fwithoutaname%2Fmods%2Fwithoutalib%2FWithoutALib%2Fmaven-metadata.xml&versionPrefix=1.17)

This is a library for all mods by WithoutAName.

## Maven
    repositories {
        maven { // WithoutALib
            url "https://repo.withoutaname.eu/releases/"
        }
    }

    dependencies {
        compile fg.deobf("withoutaname.mods.withoutalib:WithoutALib:${withoutalib_version}")
    }

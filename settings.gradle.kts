rootProject.name = "AuthFree"

plugins {
    id("dev.scaffoldit") version "0.2.+"
}

hytale {
    usePatchline("release")
    useVersion("latest")

    manifest {
        Group = "dev.hytalemodding"
        Name = rootProject.name
        Main = "dev.hytalemodding.authfree.AuthFreePlugin"
    }
}
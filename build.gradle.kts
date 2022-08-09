allprojects {
    group = "me.tech"
    version = "0.0.1"

    ext { set("javaToolchainVersion", 17) }

    repositories {
        mavenLocal()
        mavenCentral()
    }
}
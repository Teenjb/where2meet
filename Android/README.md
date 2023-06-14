# Where2Meet Android

This project uses gradle as its build tool.

# Installation

## Command-line
```
gradlew build
```

## Android Studio
This project requires at least Android Studio version Flamingo | 2022.2.1

Simply use "Import Project" and open this folder.

# Android Maps SDK

This project displays maps so it requires Android Maps SDK API key from Google Cloud.

The API key is saved on `local.properties` as `mapsApiKey`. Refer to this example.

```kt
mapsApiKey=<MAP_SDK_API_KEY>
```
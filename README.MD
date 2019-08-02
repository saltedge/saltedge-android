# Salt Edge Android SDK & Sample application

Salt Edge Android SDK provides [a handful of classes](SDK.MD) to help you interact with the Salt Edge API from your Android app.  
Current SDK supports access to [Salt Edge API v5](https://docs.saltedge.com/account_information/v5/) or [Salt Edge Partners API v1](https://docs.saltedge.com/partners/v1/).  

**When you migrating to the new version of Salt Edge Android SDK, please read carefully [CHANGELOG](CHANGELOG.MD) since many changes have been made.**

## Requirements

* JDK 8
* Android SDK (minimal sdk version - Android 4.0.2 (15))
* Play Services installed
* `App ID` and `App Secret`

**Warning!** The Sal Edge SDK doesn't work without `App ID` and `App Secret`!
*Note*: You can find your `App ID` and `App Secret` on your [profile](https://www.saltedge.com/keys_and_secrets) page.

## Source

Clone this repository

`$ git clone git@github.com:saltedge/saltedge-android.git`

## How to build sample

1. Fork this repository
1. Import project as Gradle project in Android Studio
1. Replace the `clientAppId`, `clientAppSecret` constants in `SampleApplication.java` with your `App ID` and `App Secret`.

1. Build and run application on target device or emulator

*Note*: You can find your `App ID` and `App Secret` on your [profile](https://www.saltedge.com/keys_and_secrets) page.

## How to add SDK to your project

### Add Java 8 support to application `build.gradle`
   
```groovy
android {
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}
```
  
### Add maven repository to application `build.gradle`
   
```groovy
repositories {
    maven {
        url 'https://raw.github.com/saltedge/saltedge-android/master/repo/'
    }
}
```

### Add SDK dependency to application `build.gradle`
  
```groovy
implementation ('com.saltedge.sdk:saltedge-library:3.0.0@aar') {
    transitive = true
}
```

### [Invoke SDK methods](SDK.MD) 
  
## [Changelog](CHANGELOG.MD)

## License

See the [LICENSE](LICENSE) file.

## References

1. [SDK Description](SDK.MD)
1. [Salt Edge API](https://docs.saltedge.com/account_information/v5/)
1. [Salt Edge Partners API](https://docs.saltedge.com/partners/v1/)
1. [Salt Edge Connect Guide](https://docs.saltedge.com/account_information/v5/#salt_edge_connect)

---
Copyright © 2014 - 2019 Salt Edge. https://www.saltedge.com

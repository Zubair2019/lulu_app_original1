# Build helper for Demo Cart. Usage:  .\build.ps1            (release APK)
#                                    .\build.ps1 -Install   (build + install on the connected phone)
param([switch]$Install, [string]$Task = "assembleRelease")

$env:JAVA_HOME = "D:\dev\jdk17"
$env:ANDROID_HOME = "D:\dev\android-sdk"
$env:GRADLE_USER_HOME = "D:\dev\gradle-home"
$env:Path = "$env:JAVA_HOME\bin;$env:LOCALAPPDATA\Android\Sdk\platform-tools;$env:Path"

Set-Location $PSScriptRoot
& .\gradlew.bat $Task --console=plain --offline
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

if ($Install) {
    $apk = "app\build\outputs\apk\release\app-release.apk"
    adb install -r $apk
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
    adb shell am start -n com.democart.app/com.freshcart.shop.MainActivity
}

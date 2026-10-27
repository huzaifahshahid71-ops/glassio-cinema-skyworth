[CmdletBinding()]
param(
    [Parameter(Mandatory=$true)][string]$Device,
    [string]$AdbPath='adb'
)
$ErrorActionPreference='Stop'
function Invoke-TvAdb {
    param([Parameter(ValueFromRemainingArguments=$true)][string[]]$AdbArguments)
    & $AdbPath -s $Device @AdbArguments
    if($LASTEXITCODE -ne 0){ throw 'ADB command failed; stop and check the device connection.' }
}
& $AdbPath connect $Device
if($LASTEXITCODE -ne 0){throw 'Unable to connect to TV.'}
$current=(& $AdbPath -s $Device shell settings get secure enabled_accessibility_services).Trim()
if($LASTEXITCODE -ne 0){throw 'Unable to read accessibility services; no change made.'}
$otherServices=@($current.Split(':') | Where-Object {$_ -and $_ -ne 'null' -and $_ -notlike 'com.glasslauncher.tv/*' -and $_ -notlike 'com.glasslauncher.cinema/*'})
if($otherServices.Count){
    Invoke-TvAdb shell settings put secure enabled_accessibility_services ($otherServices -join ':')
}else{
    Invoke-TvAdb shell settings put secure enabled_accessibility_services '""'
    Invoke-TvAdb shell settings put secure accessibility_enabled 0
}
Invoke-TvAdb shell pm enable --user 0 com.google.android.apps.tv.launcherx
Invoke-TvAdb shell cmd package set-home-activity --user 0 com.google.android.apps.tv.launcherx/.home.HomeActivity
Invoke-TvAdb shell am start -a android.intent.action.MAIN -c android.intent.category.HOME -n com.google.android.apps.tv.launcherx/.home.HomeActivity

# JDK 21
$jdk = "C:\Program Files\Java\jdk-21"
if (Test-Path $jdk) {
    $env:JAVA_HOME = $jdk
    $env:Path = "$jdk\bin;$env:Path"
}

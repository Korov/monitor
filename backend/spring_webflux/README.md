# Build

```bash
# build docker image
./mvnw spring-boot:build-image -Pnative

./mvnw spring-boot:build-image -Pnative -DskipTests

# build executable binary
# in windows you need add profile `"C:\Program Files\PowerShell\7\pwsh.exe" -NoExit -Command "&{Import-Module """C:\Program Files\Microsoft Visual Studio\2022\Community\Common7\Tools\Microsoft.VisualStudio.DevShell.dll"""; Enter-VsDevShell 078f62ad -SkipAutomaticLocation -DevCmdArguments """-arch=x64 -host_arch=x64"""}"` to windows terminal to build
./mvnw native:compile -Pnative

./mvnw native:compile -Pnative -DskipTests
```
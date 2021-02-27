./gradlew deployNodes
./build/nodes/runnodes

./gradlew --build-file clients/build.gradle runServerBankA
echo "Bank A available with port 10006"
#./gradlew --build-file clients/build.gradle runServerBankB
#./gradlew --build-file clients/build.gradle runServerBankC

node("linux") {
stage("Build") {
echo "Compile package"
}
stage("Test") {
echo "Test with JUnit"
}
}

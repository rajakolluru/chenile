mvn install
cd target
rm -rf cli cli.zip
mkdir cli
cp stm-*-jar-with-dependencies.jar cli
cp ../stm-cli cli
zip cli.zip cli/*


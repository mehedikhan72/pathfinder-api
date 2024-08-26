rm .env
cp .env.prototype .env

mvn -e spring-boot:run -DskipTests -Dcheckstyle.skip=true -Dspring-boot.run.profiles=dev

rm .env
cp .env.development .env
#docker-compose down
docker-compose up --build
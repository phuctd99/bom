FROM openjdk:8-slim-buster

COPY . .
RUN apt update && \
    apt install -y vim git && \
    apt install -y libxext6 libxrender1 libxtst6 libfreetype6 libfontconfig1 && \
    echo "Done!"

# Dev environment:
# docker run -d --name bom-ai -e DISPLAY="host.docker.internal:0" -v /tmp/.X11-unix:/tmp/.X11-unix -v $HOME:/root openjdk:8-slim-buster sleep 999999999
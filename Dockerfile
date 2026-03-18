FROM ubuntu:latest
LABEL authors="skonuru"

ENTRYPOINT ["top", "-b"]
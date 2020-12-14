FROM openjdk:12
WORKDIR /c0-java/
COPY ./* ./
RUN javac Main.java
FROM eclipse-temurin:21-jdk-alpine

# Instala bash e dependências necessárias para o Maven Wrapper
RUN apk add --no-cache bash

WORKDIR /app

# Expondo portas de aplicação e debug
EXPOSE 8080
EXPOSE 5005

# Garante permissão de execução para o wrapper após o volume do projeto ser montado.
# `target` fica isolado em volume próprio no compose; por isso não usamos `clean`,
# que tentaria remover o ponto de montagem `/app/target`.
CMD ["sh", "-c", "chmod +x ./mvnw && exec ./mvnw spring-boot:run -Dmaven.test.skip=true"]

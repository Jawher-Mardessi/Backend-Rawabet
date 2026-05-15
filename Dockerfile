# =========================
# Stage 1 : Build avec Maven + Java 17
# =========================
FROM maven:3.9.8-eclipse-temurin-17 AS build

WORKDIR /app

# Copier seulement pom.xml d'abord pour optimiser le cache Maven
COPY pom.xml .

# Télécharger les dépendances Maven
RUN mvn dependency:go-offline -B

# Copier le code source
COPY src ./src

# Builder le jar Spring Boot
RUN mvn clean package -DskipTests


# =========================
# Stage 2 : Image runtime légère avec Java 17
# =========================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Dossier pour les uploads avatars
RUN mkdir -p /app/uploads

# Copier le jar généré depuis le stage build
COPY --from=build /app/target/*.jar app.jar

# Ton backend tourne sur 8081
EXPOSE 8081

# Lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]

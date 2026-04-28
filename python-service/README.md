# FastAPI AI microservice

This folder is an independent Python microservice stored inside the backend repository.

Spring Boot does not import or execute the Python model directly. FastAPI exposes the model through REST endpoints on port 8000, and Spring Boot calls those endpoints over HTTP with RestTemplate.

Run Spring Boot on port 8081 and FastAPI on port 8000.

Install Python dependencies from Backend-Rawabet:

```powershell
.\setup-python-service.ps1
```

Run only the Python microservice:

```powershell
.\start-python-service.ps1
```

Run the backend stack from Backend-Rawabet:

```powershell
.\start-backend-stack.ps1
```

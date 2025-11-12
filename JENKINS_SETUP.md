# Configuración de Jenkins para FastLap

Esta guía te ayudará a configurar Jenkins para tu proyecto FastLap con integración a GitHub.

## 📋 Pre-requisitos

1. **Jenkins instalado** (versión 2.400 o superior)
2. **Cuenta de GitHub** con acceso al repositorio
3. **JDK 17** instalado en el servidor Jenkins
4. **Maven 3.9+** instalado
5. **Docker** instalado (para build de imágenes)

## 🔧 Configuración Inicial de Jenkins

### 1. Instalar Plugins Necesarios

Ve a `Manage Jenkins` > `Manage Plugins` > `Available` e instala:

- **Git Plugin** - Para integración con Git
- **GitHub Plugin** - Para integración con GitHub
- **GitHub Branch Source Plugin** - Para multibranch pipelines
- **Pipeline** - Para soporte de Jenkinsfile
- **Pipeline: GitHub Groovy Libraries** - Librerías adicionales
- **Docker Pipeline** - Para soporte de Docker en pipelines
- **JUnit Plugin** - Para reportes de pruebas
- **Maven Integration** - Para proyectos Maven
- **Credentials Binding Plugin** - Para manejo de credenciales

### 2. Configurar Herramientas Globales

Ve a `Manage Jenkins` > `Global Tool Configuration`:

#### Java (JDK)
- Nombre: `JDK-17`
- Ruta de instalación: ruta donde está instalado JDK 17
- O marca "Install automatically" y selecciona JDK 17

#### Maven
- Nombre: `Maven-3.9`
- Ruta de instalación: ruta donde está instalado Maven
- O marca "Install automatically" y selecciona versión 3.9.x

#### Git
- Nombre: `Default`
- Ruta: `git` (si está en el PATH)

### 3. Configurar Credenciales de GitHub

#### Opción A: Personal Access Token (Recomendado)

1. Ve a GitHub > Settings > Developer settings > Personal access tokens > Tokens (classic)
2. Click en "Generate new token (classic)"
3. Configura los permisos:
   - ✅ `repo` (acceso completo a repositorios)
   - ✅ `admin:repo_hook` (para webhooks)
4. Copia el token generado

En Jenkins:
1. Ve a `Manage Jenkins` > `Manage Credentials`
2. Click en `(global)` > `Add Credentials`
3. Selecciona:
   - **Kind**: Username with password
   - **Username**: tu nombre de usuario de GitHub
   - **Password**: pega el Personal Access Token
   - **ID**: `github-credentials`
   - **Description**: GitHub Access Token

#### Opción B: SSH Key

1. Genera una clave SSH si no tienes una:
   ```bash
   ssh-keygen -t rsa -b 4096 -C "tu-email@ejemplo.com"
   ```

2. Agrega la clave pública a GitHub:
   - GitHub > Settings > SSH and GPG keys > New SSH key

3. En Jenkins:
   - `Manage Jenkins` > `Manage Credentials` > `Add Credentials`
   - **Kind**: SSH Username with private key
   - **ID**: `github-ssh`
   - Pega tu clave privada

### 4. Crear el Job de Jenkins

#### Pipeline desde SCM (Recomendado)

1. En Jenkins, click en `New Item`
2. Nombre: `fastlap-ci-cd`
3. Tipo: **Pipeline**
4. Click OK

5. En la configuración del job:

   **General:**
   - ✅ Marca "GitHub project"
   - Project url: `https://github.com/TU_USUARIO/fastlap`

   **Build Triggers:**
   - ✅ Marca "GitHub hook trigger for GITScm polling"
   - ✅ O marca "Poll SCM" con schedule: `H/5 * * * *` (cada 5 minutos)

   **Pipeline:**
   - **Definition**: Pipeline script from SCM
   - **SCM**: Git
   - **Repository URL**: 
     - Con HTTPS: `https://github.com/TU_USUARIO/fastlap.git`
     - Con SSH: `git@github.com:TU_USUARIO/fastlap.git`
   - **Credentials**: Selecciona `github-credentials` o `github-ssh`
   - **Branch Specifier**: `*/main` (o `*/master` según tu branch principal)
   - **Script Path**: `Jenkinsfile`

6. Click en **Save**

#### Multibranch Pipeline (Alternativa)

Si quieres CI/CD para múltiples branches:

1. `New Item` > **Multibranch Pipeline**
2. Nombre: `fastlap-multibranch`
3. **Branch Sources** > Add source > GitHub
4. Configura:
   - **Credentials**: Selecciona tus credenciales de GitHub
   - **Repository HTTPS URL**: `https://github.com/TU_USUARIO/fastlap`
5. **Build Configuration**: By Jenkinsfile
6. **Scan Multibranch Pipeline Triggers**: Marca opciones según necesites
7. Save

### 5. Configurar Webhook en GitHub (Opcional pero Recomendado)

Para que Jenkins se ejecute automáticamente en cada push:

1. Ve a tu repositorio en GitHub
2. Settings > Webhooks > Add webhook
3. Configura:
   - **Payload URL**: `http://TU_SERVIDOR_JENKINS/github-webhook/`
   - **Content type**: `application/json`
   - **Which events**: "Just the push event"
   - ✅ Active
4. Add webhook

**Nota**: Si Jenkins está en localhost, necesitarás un túnel como ngrok o que Jenkins sea accesible públicamente.

## 🚀 Uso del Pipeline

### Ejecutar Manualmente

1. Ve al job `fastlap-ci-cd`
2. Click en "Build Now"
3. Observa el progreso en "Build History"
4. Click en el número de build para ver detalles

### Ejecución Automática

Si configuraste el webhook, cada vez que hagas push a GitHub:
```bash
git add .
git commit -m "tu mensaje"
git push origin main
```

Jenkins detectará el cambio y ejecutará el pipeline automáticamente.

## 📊 Stages del Pipeline

El Jenkinsfile incluye las siguientes etapas:

1. **Checkout** - Clona el repositorio
2. **Build** - Compila el código con Maven
3. **Test** - Ejecuta pruebas unitarias
4. **Package** - Genera el archivo JAR
5. **Code Quality Analysis** - Verifica la calidad del código
6. **Build Docker Image** - Construye imagen Docker
7. **Archive Artifacts** - Guarda el JAR generado

## 🔍 Visualizar Resultados

- **Resultados de Tests**: Jenkins > Job > Test Result
- **Artefactos**: Jenkins > Job > Build Number > Build Artifacts
- **Console Output**: Jenkins > Job > Build Number > Console Output

## 🛠️ Personalización del Pipeline

### Habilitar SonarQube (Análisis de Código)

1. Instala SonarQube Scanner plugin en Jenkins
2. Configura SonarQube server en Jenkins
3. Descomenta las líneas en el stage 'Code Quality Analysis' del Jenkinsfile

### Agregar Deploy Automático

Puedes agregar un stage adicional al final del Jenkinsfile:

```groovy
stage('Deploy') {
    steps {
        echo 'Desplegando aplicación...'
        // Aquí tus comandos de deploy
        bat 'docker-compose up -d'
    }
}
```

### Notificaciones

Agrega notificaciones en la sección `post`:

```groovy
post {
    success {
        emailext (
            subject: "Build Exitoso: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
            body: "El build se completó exitosamente.",
            to: "tu-email@ejemplo.com"
        )
    }
}
```

## 🐛 Troubleshooting

### Error: "mvn: command not found"
- Verifica que Maven esté configurado en Global Tool Configuration
- Asegúrate de que el nombre coincida: `Maven-3.9`

### Error de permisos en Docker
- Agrega el usuario de Jenkins al grupo docker:
  ```bash
  sudo usermod -aG docker jenkins
  sudo systemctl restart jenkins
  ```

### Webhook no funciona
- Verifica que la URL sea accesible desde internet
- Revisa los logs en GitHub Settings > Webhooks > Recent Deliveries

### Error: "Unable to find credentials"
- Verifica que el ID de las credenciales coincida: `github-credentials`
- Asegúrate de haber configurado las credenciales correctamente

## 📝 Checklist de Configuración

- [ ] Plugins instalados
- [ ] JDK 17 configurado
- [ ] Maven 3.9 configurado
- [ ] Credenciales de GitHub creadas
- [ ] Job de pipeline creado
- [ ] Repositorio URL configurada
- [ ] Branch correcta especificada
- [ ] Jenkinsfile en el repositorio
- [ ] Webhook de GitHub configurado (opcional)
- [ ] Primera ejecución exitosa

## 📚 Recursos Adicionales

- [Documentación de Jenkins Pipeline](https://www.jenkins.io/doc/book/pipeline/)
- [GitHub Integration Plugin](https://plugins.jenkins.io/github/)
- [Docker Pipeline Plugin](https://plugins.jenkins.io/docker-workflow/)

---

**¡Listo!** Ahora tienes Jenkins configurado para tu proyecto FastLap con integración continua automática desde GitHub.

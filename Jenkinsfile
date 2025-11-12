pipeline {
    agent any
    
    tools {
        maven 'Maven-3.9'
        jdk 'JDK-17'
    }
    
    environment {
        // Variables de entorno
        DOCKER_IMAGE = 'fastlap-app'
        DOCKER_TAG = "${env.BUILD_NUMBER}"
        MAVEN_OPTS = '-Xmx1024m'
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo 'Clonando repositorio desde GitHub...'
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                echo 'Compilando el proyecto con Maven...'
                bat 'mvn clean compile'
            }
        }
        
        stage('Test') {
            steps {
                echo 'Ejecutando pruebas unitarias...'
                bat 'mvn test'
            }
            post {
                always {
                    // Publicar resultados de pruebas
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Package') {
            steps {
                echo 'Empaquetando la aplicación...'
                bat 'mvn package -DskipTests'
            }
        }
        
        stage('Code Quality Analysis') {
            steps {
                echo 'Analizando calidad del código...'
                // Si tienes SonarQube configurado, descomenta las siguientes líneas:
                // withSonarQubeEnv('SonarQube') {
                //     bat 'mvn sonar:sonar'
                // }
                
                // Por ahora, solo verificamos que el build sea exitoso
                bat 'mvn verify -DskipTests'
            }
        }
        
        stage('Build Docker Image') {
            steps {
                echo 'Construyendo imagen Docker...'
                script {
                    bat "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
                    bat "docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest"
                }
            }
        }
        
        stage('Archive Artifacts') {
            steps {
                echo 'Archivando artefactos...'
                archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
            }
        }
    }
    
    post {
        success {
            echo 'Pipeline ejecutado exitosamente!'
            // Aquí puedes agregar notificaciones por email, Slack, etc.
        }
        failure {
            echo 'El pipeline ha fallado.'
            // Notificaciones de fallo
        }
        always {
            echo 'Limpiando workspace...'
            cleanWs()
        }
    }
}

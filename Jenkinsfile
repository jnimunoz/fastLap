pipeline {
    agent any
    
    environment {
        // Variables de entorno
        DOCKER_IMAGE = 'fastlap-app'
        DOCKER_TAG = "${env.BUILD_NUMBER}"
        MAVEN_OPTS = '-Xmx1024m'
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo 'Código obtenido desde GitHub'
                checkout scm
            }
        }
        
        stage('Build & Test') {
            steps {
                echo 'Compilando y ejecutando pruebas con Maven...'
                bat '.\\mvnw.cmd clean test'
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
                bat '.\\mvnw.cmd package -DskipTests'
            }
        }
        
        stage('Build Docker Image') {
            steps {
                echo 'Construyendo imagen Docker...'
                script {
                    bat "docker compose build"
                }
            }
        }

        stage('Docker Deploy') {
            steps {
                echo 'Desplegando contenedores Docker...'
                script {
                    bat "docker compose up -d"
                }
            }
        }
        
        stage('Archive Artifacts') {
            steps {
                echo 'Archivando artefactos...'
                archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true, allowEmptyArchive: true
            }
        }
    }
    
    post {
        success {
            echo '✅ Pipeline ejecutado exitosamente!'
        }
        failure {
            echo '❌ El pipeline ha fallado.'
        }
        always {
            echo 'Pipeline completado.'
        }
    }
}

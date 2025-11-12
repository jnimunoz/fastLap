pipeline {
    agent any
    
    environment {
        DOCKER_IMAGE = 'fastlap-app'
        DOCKER_TAG = "${env.BUILD_NUMBER}"
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo '📥 Obteniendo código desde GitHub...'
                checkout scm
            }
        }
        
        stage('Build Docker Image') {
            steps {
                echo '🔨 Construyendo imagen Docker...'
                script {
                    sh 'docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} .'
                }
            }
        }
        
        stage('Tag Image') {
            steps {
                echo '🏷️ Etiquetando imagen como latest...'
                script {
                    sh 'docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest'
                }
            }
        }
        
        stage('Stop Old Containers') {
            steps {
                echo '🛑 Deteniendo contenedores anteriores...'
                script {
                    sh 'docker stop fastlap-app fastlap-mysql || true'
                }
            }
        }
        
        stage('Remove Old Containers') {
            steps {
                echo '🗑️ Eliminando contenedores anteriores...'
                script {
                    sh 'docker rm -f fastlap-app fastlap-mysql || true'
                }
            }
        }

        stage('Deploy Containers') {
            steps {
                echo '🚀 Desplegando aplicación y base de datos...'
                script {
                    sh 'docker-compose up -d app mysql'
                }
            }
        }
        
        stage('Verify Deployment') {
            steps {
                echo '✔️ Verificando despliegue...'
                script {
                    sh 'docker ps | grep fastlap'
                }
            }
        }
        
        stage('Archive Artifacts') {
            steps {
                echo '📦 Archivando artefactos...'
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

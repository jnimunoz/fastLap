pipeline {
    agent any
    
    environment {
        DOCKER_IMAGE = 'fastlap-app'
        DOCKER_TAG = "${env.BUILD_NUMBER}"
        MAVEN_OPTS = '-Xmx1024m'
    }
    
    stages {
        
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
                archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
            }
        }
    }
    
    post {
        success {
            echo 'Pipeline ejecutado exitosamente!'
        }
        failure {
            echo 'El pipeline ha fallado.'
        }
        always {
            echo 'Limpiando workspace...'
            cleanWs()
        }
    }
}

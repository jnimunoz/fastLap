pipeline {
    agent any
    
    environment {
        DOCKER_IMAGE = 'fastlap-app'
        DOCKER_TAG = "${env.BUILD_NUMBER}"
    }
    
    stages {
        
        stage('Build Docker Image') {
            steps {
                echo 'Construyendo imagen Docker...'
                script {
                    docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}")
                    docker.image("${DOCKER_IMAGE}:${DOCKER_TAG}").tag('latest')
                }
            }
        }

        stage('Docker Deploy') {
            steps {
                echo 'Desplegando contenedores Docker...'
                script {
                    sh """
                        docker-compose down || true
                        docker-compose up -d
                    """
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

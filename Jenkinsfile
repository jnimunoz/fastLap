pipeline {
    agent any
    
    environment {
        DOCKER_IMAGE = 'fastlap-app'
        DOCKER_TAG = "${env.BUILD_NUMBER}"
        CODECOV_TOKEN = '3c83048b-78d6-4b4e-a11a-e1306a36bb51'
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo 'Obteniendo codigo desde GitHub...'
                checkout scm
            }
        }
        
        stage('Setup') {
            steps {
                echo 'Configurando permisos de Maven Wrapper...'
                script {
                    sh 'chmod +x mvnw'
                }
            }
        }
        
        stage('Compile') {
            steps {
                echo 'Compilando el proyecto...'
                script {
                    sh './mvnw clean compile'
                }
            }
        }

        stage('Unit Tests') {
            steps {
                echo 'Ejecutando pruebas unitarias...'
                script {
                    sh './mvnw test -Dspring.profiles.active=test'
                }
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                    echo 'Resultados de tests publicados'
                }
                success {
                    echo 'Todas las pruebas pasaron exitosamente'
                }
                failure {
                    echo 'Algunas pruebas fallaron'
                }
            }
        }

        stage('Code Coverage') {
            steps {
                echo 'Verificando reporte de cobertura...'
                script {
                    sh 'ls -la target/site/jacoco/ || echo "Directorio jacoco no encontrado"'
                }
                echo 'Subiendo reporte a Codecov...'
                script {
                    sh '''
                        curl -Os https://uploader.codecov.io/latest/linux/codecov
                        chmod +x codecov
                        ./codecov -t ${CODECOV_TOKEN} -f target/site/jacoco/jacoco.xml -v
                    '''
                }
            }
            post {
                success {
                    echo 'Reporte de cobertura enviado exitosamente a Codecov!'
                    echo 'Ver en: https://codecov.io/gh/jnimunoz/fastLap'
                }
                failure {
                    echo 'Error al enviar reporte de cobertura'
                }
            }
        }

        stage('Package') {
            steps {
                echo 'Empaquetando aplicacion...'
                script {
                    sh './mvnw package -DskipTests'
                }
            }
        }
        
        stage('Build Docker Image') {
            steps {
                echo 'Construyendo imagen Docker...'
                script {
                    sh 'docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} .'
                }
            }
        }
        
        stage('Tag Image') {
            steps {
                echo 'Etiquetando imagen como latest...'
                script {
                    sh 'docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest'
                }
            }
        }
        
        stage('Stop Old Containers') {
            steps {
                echo 'Deteniendo contenedores anteriores...'
                script {
                    sh 'docker stop fastlap-app fastlap-mysql || true'
                }
            }
        }
        
        stage('Remove Old Containers') {
            steps {
                echo 'Eliminando contenedores anteriores...'
                script {
                    sh 'docker rm -f fastlap-app fastlap-mysql || true'
                }
            }
        }

        stage('Deploy Containers') {
            steps {
                echo 'Desplegando aplicación y base de datos...'
                script {
                    sh 'docker-compose up -d app mysql'
                }
            }
        }
        
        stage('Verify Deployment') {
            steps {
                echo 'Verificando despliegue...'
                script {
                    sh 'docker ps | grep fastlap'
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
            echo 'Pipeline ejecutado exitosamente!'
            echo 'Revisa el reporte de cobertura en Codecov'
        }
        failure {
            echo 'El pipeline ha fallado.'
        }
        always {
            echo 'Pipeline completado.'
        }
    }
}

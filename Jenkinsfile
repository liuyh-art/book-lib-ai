pipeline {
    agent any
    environment {
        PROJECT = "book-lib-ai"
        BACK_IMG = "${PROJECT}-backend:local"
        FRONT_IMG = "${PROJECT}-frontend:local"
    }
    stages {
        stage('1. 拉取最新代码') {
            steps {
                git url: 'https://github.com/liuyh-art/book-lib-ai.git',
                branch: 'main',
                credentialsId: 'github-ssh'
            }
        }

        stage('2. 本地构建后端镜像') {
            steps {
                sh '''
                cd backend
                docker build -t $BACK_IMG .
                '''
            }
        }

        stage('3. 本地构建前端镜像') {
            steps {
                sh '''
                cd frontend
                docker build -t $FRONT_IMG .
                '''
            }
        }

        stage('4. Compose一体化部署') {
            steps {
                sh '''
                # 停止旧服务
                docker-compose down
                # 重新构建并启动
                docker-compose up -d --build
                '''
            }
        }
    }

    post {
        always {
            echo "流水线执行完毕，前端访问：http://localhost"
            echo "后端接口地址：http://localhost:8081/api/ai/chat"
        }
        failure {
            echo "部署失败，请查看流水线日志排查问题"
        }
    }
}
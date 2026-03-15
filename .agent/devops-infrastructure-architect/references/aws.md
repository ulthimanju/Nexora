# AWS Deployment Reference

## ECS (Elastic Container Service) with Fargate

### Task Definition (task-definition.json)
```json
{
  "family": "my-app",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "512",
  "memory": "1024",
  "executionRoleArn": "arn:aws:iam::ACCOUNT_ID:role/ecsTaskExecutionRole",
  "taskRoleArn": "arn:aws:iam::ACCOUNT_ID:role/ecsTaskRole",
  "containerDefinitions": [
    {
      "name": "app",
      "image": "ACCOUNT_ID.dkr.ecr.REGION.amazonaws.com/my-app:latest",
      "portMappings": [{ "containerPort": 3000, "protocol": "tcp" }],
      "environment": [],
      "secrets": [
        {
          "name": "DATABASE_URL",
          "valueFrom": "arn:aws:secretsmanager:REGION:ACCOUNT_ID:secret:my-app/db-url"
        }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/my-app",
          "awslogs-region": "us-east-1",
          "awslogs-stream-prefix": "ecs"
        }
      },
      "healthCheck": {
        "command": ["CMD-SHELL", "curl -f http://localhost:3000/health || exit 1"],
        "interval": 30,
        "timeout": 5,
        "retries": 3
      }
    }
  ]
}
```

### GitHub Actions: Deploy to ECS
```yaml
deploy:
  runs-on: ubuntu-latest
  needs: build-and-push
  environment: production
  steps:
    - uses: actions/checkout@v4
    - uses: aws-actions/configure-aws-credentials@v4
      with:
        aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
        aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
        aws-region: us-east-1
    - name: Download task definition
      run: |
        aws ecs describe-task-definition --task-definition my-app \
          --query taskDefinition > task-definition.json
    - uses: aws-actions/amazon-ecs-render-task-definition@v1
      id: task-def
      with:
        task-definition: task-definition.json
        container-name: app
        image: ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}:${{ github.sha }}
    - uses: aws-actions/amazon-ecs-deploy-task-definition@v1
      with:
        task-definition: ${{ steps.task-def.outputs.task-definition }}
        service: my-app-service
        cluster: my-cluster
        wait-for-service-stability: true
```

## ECR (Elastic Container Registry)

```bash
# Create repo
aws ecr create-repository --repository-name my-app --region us-east-1

# Login
aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com

# Tag and push
docker tag my-app:latest ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/my-app:latest
docker push ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/my-app:latest
```

## Secrets Manager

```bash
# Store a secret
aws secretsmanager create-secret \
  --name my-app/db-url \
  --secret-string "postgres://user:pass@host:5432/db"

# Retrieve in app (Node.js example)
# Use AWS SDK, or reference via ECS task definition secrets[] array
```

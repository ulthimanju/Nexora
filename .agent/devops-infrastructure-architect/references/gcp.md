# GCP Deployment Reference

## Cloud Run (Serverless Containers)

### Deploy via gcloud
```bash
# Build and push to Artifact Registry
gcloud builds submit --tag gcr.io/PROJECT_ID/my-app

# Deploy to Cloud Run
gcloud run deploy my-app \
  --image gcr.io/PROJECT_ID/my-app \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated \
  --set-env-vars NODE_ENV=production \
  --set-secrets DATABASE_URL=my-app-db-url:latest \
  --memory 512Mi \
  --cpu 1 \
  --min-instances 0 \
  --max-instances 10
```

### GitHub Actions: Deploy to Cloud Run
```yaml
deploy:
  runs-on: ubuntu-latest
  needs: build-and-push
  environment: production
  permissions:
    contents: read
    id-token: write  # for Workload Identity Federation
  steps:
    - uses: actions/checkout@v4
    - uses: google-github-actions/auth@v2
      with:
        workload_identity_provider: projects/PROJECT_NUMBER/locations/global/workloadIdentityPools/...
        service_account: github-actions@PROJECT_ID.iam.gserviceaccount.com
    - uses: google-github-actions/deploy-cloudrun@v2
      with:
        service: my-app
        image: gcr.io/PROJECT_ID/my-app:${{ github.sha }}
        region: us-central1
```

## Artifact Registry

```bash
# Create repo
gcloud artifacts repositories create my-app \
  --repository-format=docker \
  --location=us-central1

# Configure Docker auth
gcloud auth configure-docker us-central1-docker.pkg.dev

# Tag and push
docker tag my-app:latest us-central1-docker.pkg.dev/PROJECT_ID/my-app/app:latest
docker push us-central1-docker.pkg.dev/PROJECT_ID/my-app/app:latest
```

## Secret Manager

```bash
# Create a secret
echo -n "postgres://user:pass@host:5432/db" | \
  gcloud secrets create my-app-db-url --data-file=-

# Grant Cloud Run access
gcloud secrets add-iam-policy-binding my-app-db-url \
  --member=serviceAccount:SERVICE_ACCOUNT@PROJECT_ID.iam.gserviceaccount.com \
  --role=roles/secretmanager.secretAccessor
```

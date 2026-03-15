# Kubernetes Reference

## Standard App Manifests

### Namespace
```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: my-app
```

### Secret (from .env values — use Sealed Secrets or External Secrets Operator in prod)
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: my-app-secrets
  namespace: my-app
type: Opaque
stringData:
  DATABASE_URL: "postgres://user:pass@host:5432/db"
  JWT_SECRET: "changeme"
```

### ConfigMap (non-sensitive config)
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: my-app-config
  namespace: my-app
data:
  NODE_ENV: "production"
  LOG_LEVEL: "info"
```

### Deployment
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-app
  namespace: my-app
  labels:
    app: my-app
spec:
  replicas: 2
  selector:
    matchLabels:
      app: my-app
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  template:
    metadata:
      labels:
        app: my-app
    spec:
      containers:
        - name: app
          image: ghcr.io/myorg/my-app:latest
          ports:
            - containerPort: 3000
          envFrom:
            - configMapRef:
                name: my-app-config
            - secretRef:
                name: my-app-secrets
          resources:
            requests:
              cpu: "100m"
              memory: "128Mi"
            limits:
              cpu: "500m"
              memory: "512Mi"
          readinessProbe:
            httpGet:
              path: /health
              port: 3000
            initialDelaySeconds: 10
            periodSeconds: 5
          livenessProbe:
            httpGet:
              path: /health
              port: 3000
            initialDelaySeconds: 30
            periodSeconds: 10
      imagePullSecrets:
        - name: ghcr-secret
```

### Service
```yaml
apiVersion: v1
kind: Service
metadata:
  name: my-app
  namespace: my-app
spec:
  selector:
    app: my-app
  ports:
    - port: 80
      targetPort: 3000
  type: ClusterIP
```

### Ingress (with TLS via cert-manager)
```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: my-app
  namespace: my-app
  annotations:
    cert-manager.io/cluster-issuer: letsencrypt-prod
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
spec:
  ingressClassName: nginx
  tls:
    - hosts:
        - app.example.com
      secretName: my-app-tls
  rules:
    - host: app.example.com
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: my-app
                port:
                  number: 80
```

### HorizontalPodAutoscaler
```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: my-app
  namespace: my-app
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: my-app
  minReplicas: 2
  maxReplicas: 10
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 70
```

## Apply All
```bash
kubectl apply -f k8s/
# Or with kustomize:
kubectl apply -k k8s/overlays/production
```

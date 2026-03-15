# OpenAPI Advanced Patterns

## File Upload
```yaml
/upload:
  post:
    requestBody:
      content:
        multipart/form-data:
          schema:
            type: object
            properties:
              file:
                type: string
                format: binary
              metadata:
                $ref: '#/components/schemas/FileMetadata'
```

## Polymorphism (oneOf / discriminator)
```yaml
components:
  schemas:
    Pet:
      oneOf:
        - $ref: '#/components/schemas/Dog'
        - $ref: '#/components/schemas/Cat'
      discriminator:
        propertyName: type
        mapping:
          dog: '#/components/schemas/Dog'
          cat: '#/components/schemas/Cat'
    Dog:
      allOf:
        - $ref: '#/components/schemas/PetBase'
        - type: object
          properties:
            breed:
              type: string
```

## Webhooks (OpenAPI 3.1)
```yaml
webhooks:
  newOrder:
    post:
      requestBody:
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/Order'
      responses:
        '200':
          description: Webhook received
```

## OAuth2 Security
```yaml
components:
  securitySchemes:
    OAuth2:
      type: oauth2
      flows:
        authorizationCode:
          authorizationUrl: https://auth.example.com/oauth/authorize
          tokenUrl: https://auth.example.com/oauth/token
          scopes:
            read:orders: Read orders
            write:orders: Create and modify orders
```

## Cursor Pagination Pattern
```yaml
components:
  schemas:
    PaginatedResponse:
      type: object
      required: [data, pagination]
      properties:
        data:
          type: array
          items: {}   # replaced by allOf in actual usage
        pagination:
          $ref: '#/components/schemas/PaginationMeta'
    PaginationMeta:
      type: object
      required: [total, page, limit, hasNextPage]
      properties:
        total:
          type: integer
          example: 243
        page:
          type: integer
          example: 1
        limit:
          type: integer
          example: 20
        hasNextPage:
          type: boolean
          example: true
        nextCursor:
          type: string
          example: eyJpZCI6MTAwfQ==
```

## Standard Error Schema
```yaml
components:
  schemas:
    Error:
      type: object
      required: [code, message, requestId]
      properties:
        code:
          type: string
          example: VALIDATION_ERROR
        message:
          type: string
          example: Email address is already in use.
        requestId:
          type: string
          format: uuid
          example: 550e8400-e29b-41d4-a716-446655440000
        details:
          type: array
          items:
            type: object
            properties:
              field:
                type: string
              message:
                type: string
```

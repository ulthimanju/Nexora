# GraphQL Advanced Patterns

## Subscriptions
```graphql
type Subscription {
  orderStatusChanged(orderId: ID!): Order!
  newMessage(conversationId: ID!): Message!
}
```

## Custom Directives
```graphql
directive @auth(requires: Role = USER) on FIELD_DEFINITION
directive @deprecated(reason: String = "No longer supported") on FIELD_DEFINITION | ENUM_VALUE

enum Role {
  ADMIN
  USER
  GUEST
}

type Query {
  adminStats: Stats @auth(requires: ADMIN)
  publicFeed: [Post!]!
}
```

## Federation (Apollo)
```graphql
# In User service
type User @key(fields: "id") {
  id: ID!
  email: String!
}

# In Order service — extend the User type
extend type User @key(fields: "id") {
  id: ID! @external
  orders: [Order!]!
}
```

## Error Union Pattern (recommended over nullable returns)
```graphql
union CreateUserResult =
  | User
  | ValidationError
  | EmailAlreadyExistsError

type ValidationError {
  field: String!
  message: String!
}

type EmailAlreadyExistsError {
  email: String!
  message: String!
}

type Mutation {
  createUser(input: CreateUserInput!): CreateUserResult!
}
```

## DataLoader hint (N+1 note)
When generating schemas with nested types (e.g., `User.orders`), add a comment:
```graphql
type User {
  id: ID!
  # N+1 risk: use DataLoader to batch-load orders by userId
  orders(first: Int = 10, after: String): OrderConnection!
}
```

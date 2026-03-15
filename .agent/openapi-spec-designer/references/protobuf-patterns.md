# Protobuf Advanced Patterns

## Error Handling with google.rpc.Status
```protobuf
import "google/rpc/status.proto";
import "google/rpc/error_details.proto";

// For rich errors, return Status in response or use gRPC trailers
// Client-side, check status.details for typed error info
```

## oneof for Discriminated Unions
```protobuf
message Notification {
  string id = 1;
  oneof payload {
    OrderNotification order = 2;
    MessageNotification message = 3;
    SystemNotification system = 4;
  }
}
```

## Well-Known Types Cheatsheet
```protobuf
import "google/protobuf/timestamp.proto";   // Timestamp
import "google/protobuf/duration.proto";    // Duration
import "google/protobuf/wrappers.proto";    // StringValue, Int32Value (nullable primitives)
import "google/protobuf/empty.proto";       // Empty (for void returns)
import "google/protobuf/struct.proto";      // Struct (arbitrary JSON — use sparingly)
import "google/protobuf/field_mask.proto";  // FieldMask (for PATCH-style partial updates)
```

## Partial Update with FieldMask
```protobuf
message UpdateUserRequest {
  User user = 1;
  google.protobuf.FieldMask update_mask = 2;  // e.g., "name,email"
}
```

## Bi-directional Streaming
```protobuf
service ChatService {
  // Client streams messages, server streams responses
  rpc Chat(stream ChatMessage) returns (stream ChatMessage);
}
```

## Money — Never Use Float
```protobuf
// Use integer cents/paise and a currency code string
message Money {
  int64 amount_cents = 1;   // e.g., 1999 = $19.99
  string currency_code = 2; // ISO 4217: "USD", "INR"
}
```

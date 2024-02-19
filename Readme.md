# A Simple gRPC Implementation

This repository contains a simple implementation of gRPC for a CRUD application, featuring a Python server, an Android client, and a Python client, utilizing SQLite as the database.

## Project Structure

- **/proto:** Contains the protocol buffer files.
- **/server:** Contains the Python server implementation.
- **/client:** Contains both the Python and Android client implementations.
- **/database:** Contains SQLite database files.
- **/Android:** Contains Android Client.
- **/Flutter:** Contains Flutter Client.

## Initializing the Protocol Buffers

To initialize the protocol buffer files, you can use the following command:

```bash
python -m grpc_tools.protoc --proto_path=./CRUD/proto --python_out=CRUD/proto --grpc_python_out=./CRUD/proto --pyi_out=./CRUD/proto CRUD/proto/crud.proto
```

## Setting Up the Server

Navigate to the `CRUD/server` directory and run the server using the following command:

```bash
python server.py
```

## Setting Up the Clients

### Python Client

Navigate to the `CRUD/client` directory and run the Python client using the following command:

```bash
python client.py
```

### Android Client

The Android client can be found in the `CRUD/client/android` directory. Import the Android project into Android Studio and run it on an Android device or emulator.

## Database

SQLite is used as the database for this project. The database files can be found in the `CRUD/database` directory.

## Contributing

Contributions are welcome! Feel free to open issues or submit pull requests.

## License


---
*Note: Make sure to update this README with any additional setup instructions or details specific to your project.*
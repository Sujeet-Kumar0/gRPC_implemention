import grpc
import crud_pb2
import crud_pb2_grpc


def run():
    with grpc.insecure_channel("localhost:50051") as channel:
        stub = crud_pb2_grpc.CRUDServiceStub(channel)

        # Example CRUD operations
        try:
            create_response = stub.Create(crud_pb2.CreateRequest(data="Item 1"))
            print("Created item with id:", create_response.id)

            read_response = stub.Read(crud_pb2.ReadRequest(id=create_response.id))
            print("Read item:", read_response.data)

            # Update operation
            stub.Update(
                crud_pb2.UpdateRequest(id=create_response.id, data="Updated item")
            )
            print("Item updated")

            read_response_after_update = stub.Read(
                crud_pb2.ReadRequest(id=create_response.id)
            )
            print("Read item after update:", read_response_after_update.data)

            # Delete operation
            stub.Delete(crud_pb2.DeleteRequest(id=create_response.id))
            print("Item deleted")

            read_response_after_delete = stub.Read(
                crud_pb2.ReadRequest(id=create_response.id)
            )
            if read_response_after_delete.data:
                print("Read item after delete:", read_response_after_delete.data)
            else:
                print("Item not found after delete")
        except grpc.RpcError as e:
            if e.code() == grpc.StatusCode.NOT_FOUND:
                print("Requested item not found.")
            else:
                print("An error occurred:", e.details())


if __name__ == "__main__":
    run()

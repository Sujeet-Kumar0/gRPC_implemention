import grpc
from concurrent import futures
import crud_pb2_grpc
import crud_pb2
import sqlite3
import logging


class CRUDService(crud_pb2_grpc.CRUDServiceServicer):
    def __init__(self):
        self.conn = sqlite3.connect("database.db")
        self.cursor = self.conn.cursor()
        self.cursor.execute(
            """
            CREATE TABLE IF NOT EXISTS items (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                data TEXT
            )
        """
        )
        self.conn.commit()

    def Create(self, request, context):
        logging.debug(request)
        conn = sqlite3.connect("database.db")
        cursor = conn.cursor()
        cursor.execute("INSERT INTO items (data) VALUES (?)", (request.data,))
        conn.commit()
        conn.close()
        return crud_pb2.CreateResponse(id=cursor.lastrowid)
    

    def Read(self, request, context):
        conn = sqlite3.connect("database.db")
        cursor = conn.cursor()
        cursor.execute("SELECT data FROM items WHERE id=?", (request.id,))
        row = cursor.fetchone()
        conn.close()
        if row:
            return crud_pb2.ReadResponse(data=row[0])
        else:
            context.set_code(grpc.StatusCode.NOT_FOUND)
            return crud_pb2.ReadResponse()

    def Update(self, request, context):
        conn = sqlite3.connect("database.db")
        cursor = conn.cursor()
        cursor.execute("UPDATE items SET data=? WHERE id=?", (request.data, request.id))
        conn.commit()
        conn.close()
        return crud_pb2.UpdateResponse()

    def Delete(self, request, context):
        conn = sqlite3.connect("database.db")
        cursor = conn.cursor()
        cursor.execute("DELETE FROM items WHERE id=?", (request.id,))
        conn.commit()
        conn.close()
        return crud_pb2.DeleteResponse()


def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=1))
    crud_pb2_grpc.add_CRUDServiceServicer_to_server(CRUDService(), server)
    server.add_insecure_port("[::]:50051")
    server.start()
    print("Server started. Listening on port 50051.")
    server.wait_for_termination()


if __name__ == "__main__":
    logging.basicConfig()
    serve()

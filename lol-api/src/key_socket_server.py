import socket
from pynput import keyboard

HOST = '127.0.0.1'
PORT = 4000

def start_server():
    server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)  # <-- ajouté
    server.bind((HOST, PORT))
    server.listen(1)
    print(f"[SERVER] En écoute sur {HOST}:{PORT}")
    conn, addr = server.accept()
    print(f"[SERVER] Client connecté : {addr}")
    return conn

def main():
    conn = start_server()
    def on_press(key):
        try:
            if key.char in ['a','z','e','r']:
                conn.sendall(key.char.encode('utf-8'))
        except AttributeError:
            pass

    with keyboard.Listener(on_press=on_press) as listener:
        listener.join()

if __name__ == "__main__":
    main()

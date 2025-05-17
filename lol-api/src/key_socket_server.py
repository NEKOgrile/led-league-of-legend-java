import socket
from pynput import keyboard

HOST = '127.0.0.1'
PORT = 4000

def start_client():
    client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    client.connect((HOST, PORT))
    print("[CLIENT] Connecté au serveur Java")
    return client

def main():
    client = start_client()

    def on_press(key):
        try:
            if key.char in ['a', 'z', 'e', 'r']:
                client.send((key.char + '\n').encode('utf-8'))  # <-- important : \n pour que Java puisse lire
                print("ENVOYÉ :", key.char)
        except AttributeError:
            pass

    with keyboard.Listener(on_press=on_press) as listener:
        listener.join()

if __name__ == "__main__":
    main()

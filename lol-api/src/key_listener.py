from pynput import keyboard

# Cette fonction sera appelée chaque fois qu'une touche est pressée
def on_press(key):
    try:
        if key.char in ['a', 'z', 'e', 'r']:  # Touche spécifique
            print(f"Touche appuyée : {key.char}")  # Afficher la touche appuyée pour le débogage
            with open("key_pressed.txt", "w") as f:
                f.write(key.char)  # Écrire la touche dans le fichier
    except AttributeError:
        pass

# Initialisation de l'écouteur de clavier
with keyboard.Listener(on_press=on_press) as listener:
    listener.join()  # Attendre l'événement de touche

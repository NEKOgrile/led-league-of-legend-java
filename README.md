# 🧙‍♂️ **SortDetector - Détection de sorts et actions en jeu**

Ce projet utilise **Java** et **Python** pour détecter en temps réel les sorts lancés dans *League of Legends* 🎮. Il associe les touches pressées du clavier, de la souris, ou d'autres périphériques (tapis lumineux, etc.) aux actions effectuées, dans le but de créer des effets lumineux synchronisés (ex: RGB 🟪🟦🟩) en fonction des sorts utilisés.

---

## 📌 Objectif

L'objectif est de **lier les actions en jeu aux périphériques physiques** pour une expérience interactive et immersive. Ce système fonctionne en deux temps :

- 🔹 **Détection en temps réel du mana** du joueur pour savoir si un sort a été lancé.
- 🔸 **Récupération des touches pressées** (clavier, souris...) pour savoir quel sort a été utilisé.

### 📊 Ce que le programme récupère :

- **Mana** : changement du mana à chaque action
- **Touche pressée** : ex. `A`, `Z`, `E`, `R`
- **Sort détecté** : affichage dans la console avec la valeur de mana restante
- **Effet lumineux prévu** (à venir) : système RGB en lien avec le périphérique

---

## 🧱 Structure du projet

```bash
┌── lol-api
│   ├──  src/
│      ├── key_listener.py                # Script Python de récupération des touches
│      ├── key_pressed.txt                # Fichier temporaire contenant les touches pressées
│      ├── main/
│      │   ├── java/
│      │   │   └── com/example/
│      │   │       ├── GetManaPlayer.java     # Récupère le mana du joueur
│      │   │       ├── HttpClientConfig.java  # Configuration HTTP
│      │   │       ├── KeyPressReader.java    # Lit les touches enregistrées
│      │   │       ├── Main.java              # Point d'entrée du programme
│      │   │       └── SortDetector.java      # Détecte les sorts en fonction du mana et des touches
│      └── resources/                    # Ressources futures (config, templates...)
├── README.md
└── .gitignore
```

---

## 🧰 Prérequis

| Outil / Lib                      | Version recommandée     | Utilisation                          |
|----------------------------------|--------------------------|--------------------------------------|
| **Java**                         | 11+                      | Programme principal (détection mana) |
| **Python**                       | 3.13.3                   | Script pour lire les touches         |
| **Visual Studio Code (VSCode)** | Dernière version         | IDE conseillé                        |
| **JDK**                          | 11+                      | Compilation / Exécution Java         |
| **pip**                          | 23.x+                    | Installation de paquets Python       |

> 💡 **Aucune dépendance externe** pour Java n’est requise actuellement.  
> Pour Python, le script fonctionne avec une bibliothèque supplémentaire : **pynput** 

    

---

## ⚙️ Installation rapide

### 1️⃣ Cloner le projet

```bash
git clone https://github.com/NEKOgrile/led-league-of-legend-java.git
cd led-league-of-legend-java
```

### 2️⃣ Ouvrir le projet avec Visual Studio Code

> Visual Studio Code permet de gérer à la fois le code **Java** et **Python** avec les bonnes extensions.

1. Ouvrir VSCode.
2. Sélectionner le dossier `led-league-of-legend-java`.
3. Installer les extensions recommandées si VSCode vous les propose (Java Extension Pack, Python...).

### 3️⃣ Installer les bibliotheque Python

- Ouvrir le terminal.
```bash
pip install pynput
```
### 4️⃣ Lancer la partie de league of legende

- Lancer directement la game de lol , le programme fera le reste

=


### 5️⃣ Lancer le programme

- Lancer directement la **classe `Main.java`** via Visual Studio Code.

> 💡 Le `Main.java` est le point d’entrée. Il lit les touches via le fichier `key_pressed.txt` et détecte les changements de mana en boucle.

---

## 🎯 Fonctionnement global

1. Le script Python tourne en arrière-plan et **capture les pressions de touches**.
2. Le programme Java lit les changements de mana à intervalles réguliers via l’API LoL.
3. Si le mana baisse soudainement, le programme lit la touche enregistrée et **associe un sort**.
4. À terme : envoi de signaux aux périphériques lumineux pour créer une animation RGB personnalisée 💡.

---

## 🧼 Nettoyage automatique

- Le fichier `key_pressed.txt` est **automatiquement vidé** par le programme après lecture.
- Il est temporaire et ne nécessite aucune manipulation manuelle.

---

## 🛣️ Roadmap (prochaines étapes)

- [ ] 💡 Intégration avec Razer Chroma
- [ ] 📈 Interface graphique de visualisation en direct
- [ ] 🎥 Enregistrement des actions pour replay ou training
- [ ] 🔁 Ajout d'une **vérification toutes les minutes** pour détecter une partie en cours (évite les erreurs en boucle)
- [ ] 🧠 Détection des **événements de jeu** :
  - [ ] Premier sang
  - [ ] Tuer un dragon
  - [ ] Tuer le Baron Nashor
  - [ ] Apparition des sbires
  - [ ] Détection des kills / assists
- [ ] 🧪 Mode debug avec logs détaillés pour le développement
- [ ] 💤 Mode veille automatique si aucun jeu n'est détecté

---

## 🧑‍💻 Auteur

Projet développé par **willem cornil** dans un but personnel et technique.  
Si vous voulez contribuer ou discuter, n'hésitez pas à proposer une **Pull Request** ou à me contacter sur GitHub !
Le project et en public alors amusé vous

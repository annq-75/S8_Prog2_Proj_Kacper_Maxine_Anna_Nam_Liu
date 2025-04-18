# Liste des Fonctionnalités

## ✅ Chat App Features (idées potentielles)

| Fonctionnalité                          | niveau |
|----------------------------------------|----------|
| simple login                           | ★        |
| real-time messaging                    | ★        |
| group chat                             | ★        |
| message reaction                       | ★        |
| emojis                                 | ★        |
| file sharing                           | ★        |
| edit / unsend message                  | ★★       |
| voice / video calls                    | ★★★      |
| chatbots                               | ★★★★     |
| channels and private message           | ★★★      |
| dark / light modes                     | ★        |
| status : online / offline              | ★★       |
| block the one you hate (maybe your ex) | ★★       |

---

## 🔧 Les Fonctionnalités à Réaliser

### 🖥️ Côté Serveur

#### Gestion des utilisateurs
- Créer un utilisateur (`UserMsg`) avec un identifiant unique
- Supprimer un utilisateur
- Gérer l’état de connexion d’un utilisateur (connecté / déconnecté)
- Gérer la file de messages à envoyer pour chaque utilisateur (asynchrone)

#### Messagerie individuelle
- Réception et redirection de message privé

#### Gestion des groupes
- Création d’un groupe par un utilisateur
- Ajout de membres dans un groupe
- Suppression d’un utilisateur d’un groupe
- Envoi d’un message à tous les membres du groupe

#### PacketProcessor
- Interprétation du type de message via un `byte type`

---

### 💻 Côté Client

#### Réception de messages
- Boucle de réception des messages du serveur
- Affichage des messages reçus en console ou interface

#### Envoi de messages
- Saisie de message par l’utilisateur
- Choix du destinataire (utilisateur ou groupe)
- Construction du paquet et envoi via socket

#### Interface (simple)
- Menu :
  - Se connecter
  - Lister les utilisateurs connectés (optionnel)
  - Créer un groupe
  - Envoyer un message
  - Quitter

---

### 🎨 À Personnaliser

- Envoi de fichiers
- Authentification (login/password)
- Interface graphique : JavaFX / Swing

---

## ⚙️ Fonctionnalités Optionnelles

- Historique des messages

---

## 📡 Spécification du Protocole

### 1. Envoyer des messages / fichiers

#### Client → Serveur
- Pas besoin d’ID d’expéditeur (connu via socket)
- `destID [int]`
- `fileSize [byte]`
- `Taille [int]`
- `fileBytes [byte[]]`

#### Serveur → Client
- `expID [int]`
- `Taille [int]`
- `Données [byte[]]`

---

### 2. Gestion des utilisateurs dans les groupes

- **Créer un groupe**
  - `groupeId [int]`

- **Ajouter un membre**
  - `groupeId [int]`
  - `userId [int]`

- **Supprimer un membre**
  - `groupeId [int]`
  - `userId [int]`

- **Supprimer un groupe**
  - `groupeId [int]`

---

### 3. Notification

#### Serveur → Client
- `expID : 0 [int]`
- `destID : userId [int]`
- `dataSize [int]`
- `errorMessage [byte[]]`

---

## 📦 Paquets

- **Admin**
  - create group
  - add / delete member
  - delete group

- **Server**
  - error notification
  - system notification

- **User**
  - message
  - send / receive info (nickname / avatar / user’s list)

---

## 💾 Base de Données

### Serveur
- Sérialisation pour :
  - liste des utilisateurs
  - groupes
  - messages non encore envoyés

### Client
- SQLite ou Apache Derby pour :
  - messages reçus
  - liste d’amis
  - infos des groupes
  - fichiers

---

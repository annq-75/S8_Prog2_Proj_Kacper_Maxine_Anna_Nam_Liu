<!-- ABOUT THE PROJECT -->
## About The Project

DcissApp messenger service

<!-- GETTING STARTED -->
## Getting Started

### Prerequisites

Java 11 or higher
Maven

### Installation

1.  Clone the repo
   ```sh
   git clone 
   ```
2. Compile/build
   ```sh
   mvn package
   ```
   
Clean the project
   ```sh
   mvn clean
   ```

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- USAGE EXAMPLES -->
## Usage

Launch server
   ```sh
      mvn package exec:java -Dexec.mainClass="fr.uga.miashs.dciss.chatservice.server.ServerMsg"
   ```
   or (mvn package required to recompile if changes are made)
   ```sh
      java -cp target/chatservice-0.0.1-SNAPSHOT-jar-with-dependencies.jar fr.uga.miashs.dciss.chatservice.server.ServerMsg
   ```

Launch client
   ```sh
      mvn package exec:java -Dexec.mainClass="fr.uga.miashs.dciss.chatservice.client.ClientMsg"
   ```
   or (mvn package required to recompile if changes are made)
   ```sh
      java -cp target/chatservice-0.0.1-SNAPSHOT-jar-with-dependencies.jar fr.uga.miashs.dciss.chatservice.client.ClientMsg
   ```

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- LICENSE -->
## License

Distributed under the MIT License. See `LICENSE.txt` for more information.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- CONTACT -->
## Contact


<!-- ACKNOWLEDGMENTS -->
## Acknowledgments

* []()
* []()
* []()


**********Client***************
Authentification / Connexion :
Connexion avec un identifiant existant ou enregistrement (si l’ID = 0).
***Échange de messages :***
Lundi -- Envoi et réception de messages privés.
Envoi et réception de messages dans des groupes.
Envoi de fichiers.
***Informations sur l’utilisateur :***
Définir et demander un pseudonyme (nickname).
Définir / demander un avatar.
Afficher la liste des membres d’un groupe.
***Historique / stockage local :***
Sauvegarde de l’historique des messages.
Enregistrement des fichiers reçus.
Informations sur les contacts et groupes (via SQLite).

*********************Serv*************************
***Gestion des connexions :***
Attribution d’un identifiant aux nouveaux clients.
Association d’un identifiant à une connexion active.
***Transmission des messages :***
Routage des messages vers les clients concernés.
Diffusion des messages dans les groupes.
Stockage temporaire des messages non distribués.
***Gestion des utilisateurs et des groupes :***
Création d’un groupe.
Ajout / suppression d’utilisateurs dans un groupe.
Stockage des membres du groupe et de son propriétaire.
Suppression d’un groupe (par le créateur uniquement).
***Sauvegarde des données :**
Enregistrement des identifiants, groupes et messages (sérialisation / JSON).
*** Gestion des commandes et erreurs :***
Réponses aux requêtes incorrectes.
Notifications envoyées aux clients.

****************common**************************
Paquets de messages standards.
Paquets de gestion (groupes, utilisateurs).
Paquets de service (erreurs, notifications, etc.).
Paquets de données (fichiers, avatars).




///////////////////////////NAM///////////////////////////////

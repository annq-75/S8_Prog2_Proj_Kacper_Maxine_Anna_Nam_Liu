## Jour 1 : 14/04/2025


**Membres du projet**
Anna, Kacper, Liu, Maxine, Nam

**Lien vers le dépot git**
[Dépot git de notre projet de messagerie](https://github.com/annq-75/S8_Prog2_Proj_Kacper_Maxine_Anna_Nam_Liu#)


---

#### Liste des fonctionalités à réaliser :

- Ajout, modification et suppression des messages
- Création et suppression de groupes
- Ajout, création et modification des membres d'un groupe
- 

#### Liste des fonctionalités optionelles :

- 

---

## Les spécifications des paquetages

### 2 formats de paquetage de base:

**- D’un client vers le serveur** 

id destinataire | longueur des données(en octets) | les donnees (data)
4 octets (int) | 4 octets (int) | taille variable dont le nb d'octets a été donné juste avant

DATA:
Types d'action:
- 
- 
- 
- 


**- Du serveur vers un client**

id expéditeur | id destinataire | longueur des données(en octets) | les donnees (data)
4 octets (int) | 4 octets (int) | 4 octets (int) | taille variable dont le nb d'octets a été donné juste avant


#### Des paquetages en plus pour communiquer avec la base de données :

**- d'un client vers la base de donnée**

id destinataire | longueur des données(en octets) | les donnees (data)
4 octets (int) | 4 octets (int) | taille variable dont le nb d'octets a été donné juste avant

**- du serveur vers la base de donnée**

id expéditeur | id destinataire | longueur des données(en octets) | les donnees (data)
4 octets (int) | 4 octets (int) | 4 octets (int) | taille variable dont le nb d'octets a été donné juste avant


Paquetages en plus pour que la BDD puisse communiquer avec le serveur ou le client??? redondance???

**- de la base de données vers le serveur????**
dans le but d'informer periodicalement le serveur??? ex: 








> Vous devez avoir spécifié le protocole, i.e. les formats de messages que vous avez défini pour implémenter les fonctionnalités. 
Par exemple, paquet de création de groupe commence par la valeur 1 (byte indiquant le type de message serveur), puis nombre de membres (int), puis liste des identifiants des mmembres (ints). 
Expliquer un peu votre politique de gestion de projet (comment et où sont répertoriées les taches et leur affectation), etc.
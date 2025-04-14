Le but du projet est de développer un système de messagerie instantanée qui permet de communiquer entre deux personnes et également en groupes. La messagerie permettra d’envoyer du contenu textuel mais aussi des fichiers.

Pour vous aider, un client et un serveur basiques permettent la communication entre deux utilisateurs, mais aussi au sein de groupes.

Le serveur implémente les fonctionnalités pour la création/suppression des utilisateurs, groupes et la distribution des messages. Le serveur sert de relai entre les différents clients. Toutes les communications entre les clients passent par le serveur. Il ne stocke que les messages qui n’ont pas encore été distribués à tous les clients concernés, une fois distribués, ils sont supprimés du serveur. Il ne stocke aussi que le minimum d’information sur les utilisateurs et groupes. Chaque utilisateur est identifié par un entier strictement positif, les groupes par un entier strictement négatif. Le serveur a l’identifiant 0. Pour les groupes, le serveur connaît la liste des membres ainsi que le propriétaire (i.e. le créateur).

Le protocole de communication implémenté est le suivant :

Le client se connecte au serveur et envoie sont identifiant ou 0 si il n’en a pas.
Au niveau du serveur, si l’identifiant est inconnu le serveur fermera la connexion, si l’identifiant est 0 alors le serveur attribut un identifiant (en incrémentant une séquence) au client et lui envoie, si l’identifiant est connu le serveur associe la connexion à l’utilisateur correspondant.
Une fois identifiée la connexion/session est établie jusqu’à ce que client se déconnecte (ou que le serveur soit éteint).
Durant la vie d’une connexion/session utilisateur, les messages sont gérés de manière asynchrone:le serveur peut à la fois envoyer et recevoir des paquets/messages.

Deux formats de paquets sont utilisés :

du client vers le serveur : identifiant du destinataire (int), taille des données en octets (int), les données (byte[]).

du serveur vers le client : identifiant de l’expéditeur (int), identifiant du destinataire (int), la taille des données en octets (int), les données (byte[]).

Le destinataire d’un message peut être soit un utilisateur ou un groupe, l’expéditeur est toujours un utilisateur. Du client vers le serveur, le serveur n’a pas besoin de l’identifiant de l’expéditeur car il connaît avec qui est établie la connexion. Dans le cas serveur vers client, on envoie l’identifiant du destinataire car cela permet au client de distinguer les messages de groupe ou personnels.

Formats de paquets :
Il va falloir étendre les formats de paquets afin de pouvoir implémenter les fonctionnalités que vous voulez offrir.

On peut par exemple distinguer :

Les paquets de gestion à destination du serveur : création de groupe, ajout/suppression d’un utilisateur à un groupe, suppression d’un groupe, etc.

Les paquets du serveur à destination des clients : notifications d’erreurs, ou autre.

Les paquets entre utilisateurs et/ou groupe : messages, demande ou envoie d’informations (nickname, avatar, liste des membres du groupe, etc).

Sauvegarde/persistance des données :
Il va falloir gérer la persistance des données à la fois au niveau serveur et au niveau client.

Au niveau serveur, il est suggéré d’utiliser la Serialization ou autre format pour la sauvegarde. Au niveau du client, il faut stocker les messages, les infos sur les contacts, groupes, fichiers reçus, etc. Pour cela, il serait souhaitable d’utiliser une base de données relationnelle (type SQLite ou apache Derby).

Bien sûr cela n’est pas exhaustif et vous êtes libres des fonctionnalités que vous voulez ajouter.

///////////////////////////////////////////////////////////////////////

Цель проекта — разработать систему мгновенного обмена сообщениями, которая позволяет общаться как между двумя людьми, так и в группах. Система будет поддерживать отправку текстовых сообщений, а также файлов.

Для вашей помощи уже реализованы базовые клиент и сервер, которые позволяют обмениваться сообщениями между двумя пользователями, а также внутри групп.

Сервер реализует функции создания/удаления пользователей, групп и рассылки сообщений. Он служит ретранслятором между клиентами. Все сообщения между клиентами проходят через сервер. Сервер хранит только те сообщения, которые ещё не были доставлены всем соответствующим получателям, после доставки они удаляются. Также сервер хранит минимум информации о пользователях и группах. Каждый пользователь идентифицируется положительным целым числом, а группы — отрицательным целым числом. Идентификатор сервера — 0. Для групп сервер знает список участников и владельца (то есть создателя).

Протокол общения:
Клиент подключается к серверу и отправляет свой идентификатор, либо 0, если у него его нет.

На стороне сервера:

Если идентификатор неизвестен, сервер закрывает соединение.

Если идентификатор равен 0, сервер выдаёт новый идентификатор (увеличивая счётчик) и отправляет его клиенту.

Если идентификатор известен, сервер связывает соединение с соответствующим пользователем.

После установления связи соединение (сессия) действует до тех пор, пока клиент не отключится или сервер не будет остановлен.

Во время сессии сообщения обрабатываются асинхронно: сервер может одновременно и отправлять, и принимать сообщения.

Используемые форматы пакетов:
От клиента к серверу:
Идентификатор получателя (int),

Размер данных в байтах (int),

Данные (byte[])

От сервера к клиенту:
Идентификатор отправителя (int),

Идентификатор получателя (int),

Размер данных в байтах (int),

Данные (byte[])

Получателем может быть либо пользователь, либо группа, отправителем — всегда пользователь.
От клиента к серверу идентификатор отправителя не нужен, так как сервер уже знает, кто подключён.
От сервера к клиенту указывается идентификатор получателя, чтобы клиент мог различать личные сообщения и сообщения групп.

Расширение форматов пакетов
Необходимо будет расширить форматы пакетов для реализации дополнительных функций.
Например, можно выделить:

Пакеты управления, направленные серверу: создание/удаление группы, добавление/удаление участника и т.д.

Пакеты от сервера к клиентам: уведомления об ошибках и другое.

Пакеты между пользователями и/или группами: сообщения, запрос или передача информации (никнейм, аватар, список участников группы и т.д.).

Сохранение/персистентность данных
Необходимо обеспечить сохранение данных как на сервере, так и на клиенте:

На стороне сервера предлагается использовать сериализацию или другой подход для сохранения состояния.

На клиенте необходимо сохранять сообщения, информацию о контактах и группах, принятые файлы и т.д.
Рекомендуется использовать реляционную базу данных (например, SQLite или Apache Derby).

Это описание не является исчерпывающим — вы можете реализовать любые дополнительные функции по своему желанию.
///////////////////////////////

fr.uga.miashs.dciss.chatservice.client – клиентская часть (отправка сообщений, соединение и т.п.).
fr.uga.miashs.dciss.chatservice.server – серверная логика.
fr.uga.miashs.dciss.chatservice.common – общие классы (например, Packet и ExempleConnexionDB).

Maven: проект использует pom.xml, значит зависимости удобно подключать.



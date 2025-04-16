package fr.uga.miashs.dciss.chatservice.test;

import fr.uga.miashs.dciss.chatservice.server.ServerMsg;
import fr.uga.miashs.dciss.chatservice.client.ClientMsg;
import fr.uga.miashs.dciss.chatservice.common.Packet;

import java.io.*;
import java.util.concurrent.*;

public class ChatSystemTest {//This is just a test class. Anna.

	public static void main(String[] args) throws Exception {

		// 1. On lance le serveur dans un thread séparé -- Запускаем сервер в отдельном
		// потоке
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.submit(() -> {
			try {
				ServerMsg server = new ServerMsg(1666);
				server.start(); // boucle infinie — donc dans un thread -- бесконечный цикл — поэтому в
								// отдельном потоке
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		// On donne un peu de temps au serveur pour démarrer -- Даём серверу немного
		// времени на запуск
		Thread.sleep(1000);

		// 2. On lance 3 clients -- Запускаем 3 клиента
		ClientMsg client1 = new ClientMsg("localhost", 1666);
		ClientMsg client2 = new ClientMsg("localhost", 1666);
		ClientMsg client3 = new ClientMsg("localhost", 1666);

		// On se connecte -- Подключаемся
		client1.startSession();
		client2.startSession();
		client3.startSession();

		System.out.println("Clients connected:");
		System.out.println("Client1 ID = " + client1.getIdentifier());
		System.out.println("Client2 ID = " + client2.getIdentifier());
		System.out.println("Client3 ID = " + client3.getIdentifier());

		// 3. On s’abonne aux messages entrants -- Подписываемся на входящие сообщения
		client1.addMessageListener(p -> System.out.println("Client1 received: " + new String(p.data)));
		client2.addMessageListener(p -> System.out.println("Client2 received: " + new String(p.data)));
		client3.addMessageListener(p -> System.out.println("Client3 received: " + new String(p.data)));

		// 4. On crée un groupe au nom de client1 avec client2 et client3 -- Создаем
		// группу от имени client1 с client2 и client3
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);

		dos.writeByte(1); // type 1 = create group
		dos.writeInt(2); // количество участников
		dos.writeInt(client2.getIdentifier());
		dos.writeInt(client3.getIdentifier());
		dos.flush();

		client1.sendPacket(0, bos.toByteArray());

		// On attend un peu pour que le groupe soit bien créé -- Подождем немного, чтобы
		// группа была создана
		Thread.sleep(500);

		// 5. On suppose que l’ID du groupe sera -1 (puisque c’est le premier groupId
		// créé) -- Предположим, что ID группы будет -1 (так как это первый созданный
		// groupId)
		int groupId = -1;

		// 6. On envoie un message dans le groupe -- Отправляем сообщение в группу
		client1.sendPacket(groupId, "Hello my groupe!".getBytes());

		// On attend un peu pour que tout le monde ait le temps de recevoir le message.
		// -- Подождем, чтобы все успели получить сообщение
		Thread.sleep(1000);

		// 7. On termine la session. -- Завершаем сессию
		client1.closeSession();
		client2.closeSession();
		client3.closeSession();

		// On arrête le serveur.--Завершаем сервер
		executor.shutdownNow();

		System.out.println("Test completed.");
	}
}

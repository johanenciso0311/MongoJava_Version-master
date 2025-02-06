package com.jarroba.MongoJava.MongoJava_ejemplo;

import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import org.bson.Document;



public class Main {

	public static void main(String args[]) {
		 menu();

	}


	public static void menu (){
		Scanner sc = new Scanner(System.in);
		int opcion = 0;
		do{
			System.out.println("\n\n1. Insertar futbolistas en MongoDB");
			System.out.println("2. Insertar conciertos en MongoDB");
			System.out.println("0. Salir");
			opcion = sc.nextInt();
			switch(opcion){
				case 1:
					futbolistasMongo(futbolistas());
					break;
				case 2:
					ConciertosMongo(conciertos());
					break;
				case 0:
					System.out.println("Saliendo...");
					break;
				default:
					System.out.println("Opción no válida");
					break;
			}
		}while(opcion != 0);
	}
	public static ArrayList<Futbolista> futbolistas() {
		ArrayList<Futbolista> futbolistas = new ArrayList<Futbolista>();

		futbolistas.add(new Futbolista("Iker", "Casillas", 33, new ArrayList<String>(Arrays.asList("Portero")), true));
		futbolistas.add(new Futbolista("Carles", "Puyol", 36, new ArrayList<String>(Arrays.asList("Central", "Lateral")), true));
		futbolistas.add(new Futbolista("Sergio", "Ramos", 28, new ArrayList<String>(Arrays.asList("Lateral", "Central")), true));
		futbolistas.add(new Futbolista("Andrés", "Iniesta", 30, new ArrayList<String>(Arrays.asList("Centrocampista", "Delantero")), true));
		futbolistas.add(new Futbolista("Fernando", "Torres", 30, new ArrayList<String>(Arrays.asList("Delantero")), true));
		futbolistas.add(new Futbolista("Leo", " Baptistao", 22, new ArrayList<String>(Arrays.asList("Delantero")), false));

		return futbolistas;
	}

	public static ArrayList<Concierto> conciertos(){
		ArrayList<Concierto> listConciertos;

		try {
			File file = new File("src/main/java/com/jarroba/MongoJava/MongoJava_ejemplo/CONCIERTOS.XML");
			// Crear el contexto de JAXB para la clase Conciertos
			JAXBContext jaxbContext = JAXBContext.newInstance(Conciertos.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			Conciertos conciertos = (Conciertos) jaxbUnmarshaller.unmarshal(file);

            listConciertos = new ArrayList<Concierto>(conciertos.getConciertos());

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return listConciertos;
	}



	public static void futbolistasMongo(ArrayList<Futbolista> futbolistas){

		String connectionString = "mongodb://localhost:27017";
		String databaseName = "test";

		try (MongoClient mongoClient = MongoClients.create(connectionString)) {
			System.out.println("✅ PASO 1: Conexión realizada: " + mongoClient.getClusterDescription() + "\n");
			// PASO 1: Conexión al Server de MongoDB Pasandole el host y el puerto


			// PASO 2: Conexión a la base de datos
			MongoDatabase database = mongoClient.getDatabase(databaseName);
			System.out.println("✅ PASO 2: Conectado a la base de datos: " + database.getName() + "\n");

			// PASO 3: Obtenemos una coleccion para trabajar con ella
			MongoCollection<Document> collection = database.getCollection("Futbolistas");
			System.out.println("✅ PASO 3: Conexión realizada:" + collection.getNamespace() + "\n");

			// PASO 4: CRUD (Create-Read-Update-Delete)

			// PASO 4.1: "CREATE" -> Metemos los objetos futbolistas (o documentos en Mongo) en la coleccion Futbolista

			for (Futbolista fut : futbolistas) {
				collection.insertOne(fut.toDBObjectFutbolista());
			}
			System.out.println("✅ PASO 4.1: Número de documentos en la colección Futbolistas: " + (int) collection.countDocuments() + "\n");


			// PASO 4.2.1: "READ" -> Leemos todos los documentos de la base de datos



			System.out.println("✅ PASO 4.2.1: Futbolistas de la coleccion" + "\n");

			// Busco todos los documentos de la colección y los imprimo
			try (MongoCursor<Document> cursor = collection.find().iterator()) {
				while (cursor.hasNext()) {
					// Se imprime el documento en formato JSON para mayor legibilidad
					System.out.println(cursor.next().toJson());
				}
			}

			// PASO 4.2.2: "READ" -> Hacemos una Query con condiciones (Buscar Futbolistas que sean delanteros) y lo pasamos a un objeto Java

			System.out.println("\n Futbolistas que juegan en la posición de Delantero \n");
			// Se crea la query usando Document y el operador $regex
			BasicDBObject query = new BasicDBObject("demarcacion", "Delantero");

			// Se obtiene un cursor para iterar los documentos que cumplen la query
			try (MongoCursor<Document> cursor = collection.find(query).iterator()) {
				while (cursor.hasNext()) {
					Document doc = cursor.next();
					Futbolista futbolista = new Futbolista(doc);
					System.out.println(futbolista.toString());
				}
			}


			// PASO 4.3: "UPDATE" -> Actualizamos la edad de los jugadores. Sumamos 100 años a los jugadores que tengan mas de 30 años
			BasicDBObject find = new BasicDBObject("edad", new BasicDBObject("$gt", 30));

			BasicDBObject updated = new BasicDBObject().append("$inc", new BasicDBObject().append("edad", 100));
			collection.updateMany(find, updated);
			FindIterable<Document> documents = collection.find();

		   	System.out.println("\n PASO 4.3: Futbolistas después de la modificacion y antes del borrado" + collection.find().toString() + "\n");
			for (Document doc : documents) {
				System.out.println(doc.toJson());
			}

			// PASO 4.4: "DELETE" -> Borramos todos los futbolistas que sean internacionales (internacional = true)
			BasicDBObject findDoc = new BasicDBObject("internacional", true);
			System.out.println("\n PASO 4.4: Futbolistas despues del borrado" + collection.find().toString() + "\n");
			collection.deleteMany(findDoc);
			for (Document doc : collection.find()){
				System.out.printf(doc.toJson());
			}

			// PASO FINAL: Cerrar la conexion

			System.out.println("\nPASO FINAL: Eliminando la base de datos...");
			database.drop(); // Esto borra TODA la base de datos

			// Cerrar la conexión
			mongoClient.close();
			System.out.println("Conexión cerrada y base de datos eliminada.");



		} catch (MongoException ex) {
			System.out.println("Exception al conectar al server de Mongo: " + ex.getMessage());
		}
	}

	public static void ConciertosMongo(ArrayList<Concierto> futbolistas){

		String connectionString = "mongodb://localhost:27017";
		String databaseName = "test";

		try (MongoClient mongoClient = MongoClients.create(connectionString)) {
			System.out.println("✅ PASO 1: Conexión realizada: " + mongoClient.getClusterDescription() + "\n");
			// PASO 1: Conexión al Server de MongoDB Pasandole el host y el puerto


			// PASO 2: Conexión a la base de datos
			MongoDatabase database = mongoClient.getDatabase(databaseName);
			System.out.println("✅ PASO 2: Conectado a la base de datos: " + database.getName() + "\n");

			// PASO 3: Obtenemos una coleccion para trabajar con ella
			MongoCollection<Document> collection = database.getCollection("Conciertos");
			System.out.println("✅ PASO 3: Conexión realizada:" + collection.getNamespace() + "\n");

			// PASO 4: CRUD (Create-Read-Update-Delete)

			// PASO 4.1: "CREATE" -> Metemos los objetos futbolistas (o documentos en Mongo) en la coleccion Futbolista

			for (Concierto fut : futbolistas) {
				collection.insertOne(fut.toDBObjectConcierto());
			}
			System.out.println("✅ PASO 4.1: Número de documentos en la colección Conciertos: " + (int) collection.countDocuments() + "\n");


			// PASO 4.2.1: "READ" -> Leemos todos los documentos de la base de datos



			System.out.println("✅ PASO 4.2.1: Conciertos de la coleccion" + "\n");

			// Busco todos los documentos de la colección y los imprimo
			try (MongoCursor<Document> cursor = collection.find().iterator()) {
				while (cursor.hasNext()) {
					// Se imprime el documento en formato JSON para mayor legibilidad
					System.out.println(cursor.next().toJson());
				}
			}

			// PASO 4.2.2: "READ" -> Hacemos una Query con condiciones (Buscar Futbolistas que sean delanteros) y lo pasamos a un objeto Java

			System.out.println("\n✅ PASO 4.2.2 Concierto que se van a realizar el dia Viernes 11 octubre 2024\n");
			// Se crea la query usando Document y el operador $regex
			BasicDBObject query = new BasicDBObject("fecha", "Viernes 11 octubre 2024");

			// Se obtiene un cursor para iterar los documentos que cumplen la query
			try (MongoCursor<Document> cursor = collection.find(query).iterator()) {
				while (cursor.hasNext()) {
					Document doc = cursor.next();
					Concierto concierto = new Concierto(doc);
					System.out.println(concierto.toString());
				}
			}


			// PASO 4.3: "UPDATE" -> Actualizamos la edad de los jugadores. Sumamos 100 años a los jugadores que tengan mas de 30 años
			BasicDBObject find = new BasicDBObject("hora", "18:30 H.");

			FindIterable<Document> beforeUpdate = collection.find(find);
			Document beforeDoc = beforeUpdate.first();

			if (beforeDoc != null) {
				System.out.println("\n✅ PASO 4.3: Concierto antes de la modificacion: \n" + beforeDoc.toJson());
			}

			BasicDBObject updated = new BasicDBObject().append("$set", new BasicDBObject().append("hora", "23:11 H."));
			collection.updateMany(find, updated);

			FindIterable<Document> afterUpdate = collection.find(new BasicDBObject("hora", "23:11 H."));
			Document afterDoc = afterUpdate.first();

			System.out.println("✅ PASO 4.3: Concierto después de la modificacion y antes del borrado" + collection.find().toString());
			if (afterDoc != null) {
				System.out.println("✅ PASO 4.3:Después de la modificación:\n" + afterDoc.toJson());
			}

			// PASO 4.4: "DELETE" -> Borramos todos los futbolistas que sean internacionales (internacional = true)
			BasicDBObject findDoc = new BasicDBObject("grupo","LA CASA AZUL");


			FindIterable<Document> beforeDelete = collection.find(findDoc);
			System.out.println("\n✅ PASO 4.4: Elemento que vamos a eliminar:");
			for (Document doc : beforeDelete) {
				System.out.println(doc.toJson());
			}

			collection.deleteMany(findDoc);

			System.out.println("\n✅ PASO 4.4: Conciertos después del borrado:");
			FindIterable<Document> afterDelete = collection.find(); // Obtener todos los documentos restantes
			for (Document doc : afterDelete) {
				System.out.println(doc.toJson());
			}

			System.out.println("\nPASO FINAL: Eliminando la base de datos...");
			database.drop(); // Esto borra TODA la base de datos

			// Cerrar la conexión
			mongoClient.close();
			System.out.println("Conexión cerrada y base de datos eliminada.");

		} catch (MongoException ex) {
			System.out.println("Exception al conectar al server de Mongo: " + ex.getMessage());
		}
	}


}

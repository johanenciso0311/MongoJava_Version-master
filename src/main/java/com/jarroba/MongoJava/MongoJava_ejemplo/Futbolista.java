package com.jarroba.MongoJava.MongoJava_ejemplo;

import org.bson.Document;

import java.util.ArrayList;


public class Futbolista {

	private String nombre;
	private String apellidos;
	private Integer edad;
	private ArrayList<String> demarcacion;
	private Boolean internacional;

	public Futbolista() {
	}

	public Futbolista(String nombre, String apellidos, Integer edad, ArrayList<String> demarcacion, Boolean internacional) {
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.edad = edad;
		this.demarcacion = demarcacion;
		this.internacional = internacional;
	}

	// Transformo un objecto que me da MongoDB a un Objecto Java
	public Futbolista(Document doc) {
		this.nombre = doc.getString("nombre");
		this.apellidos = doc.getString("apellidos");
		this.edad = doc.getInteger("edad");

		// Cuidado cuando trabajamos con Arrays o Listas
		ArrayList<String> listDemarcaciones = (ArrayList<String>) doc.get("demarcacion");
		this.demarcacion = new ArrayList<String>();
		for (String demarc : listDemarcaciones) {
			this.demarcacion.add(demarc);
		}

		this.internacional = doc.getBoolean("internacional");
	}

	public Document toDBObjectFutbolista() {

		// Creamos una instancia BasicDBObject
		Document dBObjectFutbolista = new Document();

		dBObjectFutbolista.append("nombre", this.getNombre());
		dBObjectFutbolista.append("apellidos", this.getApellidos());
		dBObjectFutbolista.append("edad", this.getEdad());
		dBObjectFutbolista.append("demarcacion", this.getDemarcacion());
		dBObjectFutbolista.append("internacional", this.getInternacional());

		return dBObjectFutbolista;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public Integer getEdad() {
		return edad;
	}

	public void setEdad(Integer edad) {
		this.edad = edad;
	}

	public ArrayList<String> getDemarcacion() {
		return demarcacion;
	}

	public void setDemarcacion(ArrayList<String> demarcacion) {
		this.demarcacion = demarcacion;
	}

	public Boolean getInternacional() {
		return internacional;
	}

	public void setInternacional(Boolean internacional) {
		this.internacional = internacional;
	}

	@Override
	public String toString() {
		return "Nombre: " + this.getNombre() + " " + this.getApellidos() + " / Edad: " + this.edad + " / Demarcación: " + this.demarcacion.toString() + " / Internacional: " + this.internacional;
	}
}

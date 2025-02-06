package com.jarroba.MongoJava.MongoJava_ejemplo;

import org.bson.Document;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Concierto")
public class Concierto {
    private String grupo;
    private String lugar;
    private String fecha;
    private String hora;

    public Concierto(){

    }

    public Concierto(String grupo, String lugar, String fecha, String hora) {
        this.grupo = grupo;
        this.lugar = lugar;
        this.fecha = fecha;
        this.hora = hora;
    }


    public Concierto(Document doc) {
        this.grupo = doc.getString("grupo");
        this.lugar = doc.getString("lugar");
        this.fecha = doc.getString("fecha");
        this.hora = doc.getString("hora");

    }

    public Document toDBObjectConcierto() {

        // Creamos una instancia BasicDBObject
        Document dBObjectConcierto = new Document();

        dBObjectConcierto.append("grupo", this.getGrupo());
        dBObjectConcierto.append("lugar", this.getLugar());
        dBObjectConcierto.append("fecha", this.getFecha());
        dBObjectConcierto.append("hora", this.getHora());


        return dBObjectConcierto;
    }


    @XmlElement(name = "Grupo")
    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    @XmlElement(name = "Hora")
    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }
    @XmlElement(name = "Lugar")
    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }
    @XmlElement(name = "Fecha")
    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "Concierto: " +
                "grupo='" + grupo + '\'' +
                ", lugar='" + lugar + '\'' +
                ", fecha='" + fecha + '\'' +
                ", hora='" + hora + '\'';
    }
}

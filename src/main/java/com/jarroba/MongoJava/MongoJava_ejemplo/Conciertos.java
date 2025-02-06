package com.jarroba.MongoJava.MongoJava_ejemplo;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "Conciertos")
public class Conciertos {

    private List<Concierto> conciertos;

    @XmlElement(name = "Concierto")
    public List<Concierto> getConciertos() {
        return conciertos;
    }

    public void setConciertos(List<Concierto> conciertos) {
        this.conciertos = conciertos;
    }

}

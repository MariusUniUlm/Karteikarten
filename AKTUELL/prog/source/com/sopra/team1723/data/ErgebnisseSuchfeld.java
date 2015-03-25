package com.sopra.team1723.data;

// Dient zur Kapselung der R�ckgabedaten der Funktion durchsucheDatenbank() im Datenbankmanager.
// Dabei wird der zum Suchbegriff �hnliche Text in der Variablen text gespeichert. Mit der 
// Variablen Klasse und id wei� man zu welchem Objekt die Variable text geh�rt
// 
public class ErgebnisseSuchfeld {
    public String text;
    public int id;
    public String klasse;
    
    public ErgebnisseSuchfeld(String text, int id, String klasse)
    {
        super();
        this.text = text;
        this.id = id;
        this.klasse = klasse;
    }
}